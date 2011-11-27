package ca.jvsh.audicy;

import java.util.ArrayList;
import java.util.Collections;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class GuitarView extends View implements MultiTouchObjectCanvas<Object>
{
	private final MultiTouchController<Object>	multiTouchController;
	private final PointInfo						mCurrTouchPoint;

	private static final int[]					TOUCH_COLORS					= { Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLUE, Color.WHITE, Color.GRAY, Color.LTGRAY, Color.DKGRAY };

	private static final int					STRINGS							= 6;
	private static final int					FRETS							= 12;

	private static final float[]				STRING_THICKNESS				= { 4.6f, 3.6f, 2.6f, 1.7f, 1.3f, 1.0f };
	private static final float[]				STRING_NOTES					= { 1318.5f, 987.77f, 783.99f, 587.33f, 440.0f, 329.63f };

	private final PointF[]						mStringsBegin					= new PointF[STRINGS];
	private final PointF[]						mStringsEnd						= new PointF[STRINGS];

	private final Paint							mFretPaint						= new Paint();
	private final Paint							mStringPaint					= new Paint();
	private final Paint							mPaint							= new Paint();

	private final int[]							mTouchPointColors				= new int[MultiTouchController.MAX_TOUCH_POINTS];

	private final PointF[]						mCurPoint						= new PointF[MultiTouchController.MAX_TOUCH_POINTS];
	private final PointF[]						mPrevPoint						= new PointF[MultiTouchController.MAX_TOUCH_POINTS];

	private final boolean						mStringPushedDown[][]			= new boolean[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];

	private final PointF						mStringsPushedDownLeftX[][]		= new PointF[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];
	private final PointF						mStringsPushedDownRightX[][]	= new PointF[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];

	private final boolean						mStringPulledFromDown[][]		= new boolean[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];
	private final boolean						mStringPulledFromUp[][]			= new boolean[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];

	private final boolean						mStringPlay[]					= new boolean[STRINGS];
	private final float							mStringPlayFingerX[]			= new float[STRINGS];

	private final PointF						mStringsPulledPoint[][]			= new PointF[STRINGS][MultiTouchController.MAX_TOUCH_POINTS];

	private final float[]						mFretX							= new float[FRETS];

	private float								mFingerThickness				= 50;

	int											mHeight;
	int											mWidth;

	float										mScreenThird;
	float										mStringDistance;

	float										mScreenWidth;
	float										mScreenHeight;
	
	@SuppressWarnings("unchecked")
	private ArrayList<PointF>[]  coordinateList = new ArrayList[STRINGS];
	
	private static final PointCompare PointComparatorX = new PointCompare();
	
	private PointF prevPoint = new PointF();
	private PointF currentPoint=new PointF();


	public GuitarView(Context context)
	{
		this(context, null);
	}

	public GuitarView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public GuitarView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// get information about window size
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();

		multiTouchController = new MultiTouchController<Object>(this);
		mCurrTouchPoint = new PointInfo();

		mPaint.setTextSize(60);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setAntiAlias(true);

		mStringPaint.setStyle(Style.STROKE);
		mStringPaint.setAntiAlias(true);

		for (int string = 0; string < STRINGS; string++)
		{
			mStringsBegin[string] = new PointF(0, 0);
			mStringsEnd[string] = new PointF(0, 0);
			coordinateList[string] = new ArrayList<PointF>();
		}

		for (int finger = 0; finger < MultiTouchController.MAX_TOUCH_POINTS; finger++)
		{
			mTouchPointColors[finger] = finger < TOUCH_COLORS.length ? TOUCH_COLORS[finger] : (int) (Math.random() * 0xffffff) + 0xff000000;
			mCurPoint[finger] = new PointF(0, 0);
			mPrevPoint[finger] = new PointF(0, 0);
			for (int string = 0; string < STRINGS; string++)
			{
				mStringsPulledPoint[string][finger] = new PointF(0, 0);
				mStringsPushedDownLeftX[string][finger] = new PointF(0, 0);
				mStringsPushedDownRightX[string][finger] = new PointF(0, 0);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// Pass the event on to the controller
		return multiTouchController.onTouchEvent(event);
	}

	@Override
	public Object getDraggableObjectAtPoint(PointInfo pt)
	{
		// IMPORTANT: to start a multitouch drag operation, this routine must
		// return non-null
		return this;
	}

	@Override
	public void getPositionAndScale(Object obj, PositionAndScale objPosAndScaleOut)
	{
		// We aren't dragging any objects, so this doesn't do anything in this
		// app
	}

	@Override
	public void selectObject(Object obj, PointInfo touchPoint)
	{
		// We aren't dragging any objects in this particular app, but this is
		// called when the point goes up (obj == null) or down (obj != null),
		// save the touch point info
		touchPointChanged(touchPoint);
	}

	@Override
	public boolean setPositionAndScale(Object obj, PositionAndScale newObjPosAndScale, PointInfo touchPoint)
	{
		// Called during a drag or stretch operation, update the touch point
		// info
		touchPointChanged(touchPoint);
		return true;
	}

	/**
	 * Called when the touch point info changes, causes a redraw.
	 * 
	 * @param touchPoint
	 */
	private void touchPointChanged(PointInfo touchPoint)
	{
		// Take a snapshot of touch point info, the touch point is volatile
		mCurrTouchPoint.set(touchPoint);
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidth = getWidth();
		mHeight = getHeight();

		mScreenThird = mHeight / 3.0f;
		mStringDistance = mScreenThird / 4.0f;
		mFingerThickness = mStringDistance / 2;
		// calculate frets X
		{
			mFretX[0] = mWidth / 17.817f;

			for (int fret = 1; fret < FRETS; fret++)
			{
				mFretX[fret] = mFretX[fret - 1] + (mWidth - mFretX[fret - 1]) / 17.817f;
			}

			for (int fret = 0; fret < FRETS; fret++)
			{
				mFretX[fret] = mWidth * mFretX[fret] / mFretX[FRETS - 1];
			}
		}

		// calculate normal (without tension) string Y
		for (int string = 0; string < STRINGS; string++)
		{
			mStringsBegin[string].set(0, mScreenThird + string * mStringDistance);
			mStringsEnd[string].set(mWidth, mScreenThird + string * mStringDistance);
		}

	}

	private void drawFrets(Canvas canvas)
	{
		mFretPaint.setStrokeWidth(3);
		mFretPaint.setColor(Color.RED);

		for (int fret = 0; fret < FRETS; fret++)
		{
			canvas.drawLine(mFretX[fret], 0.9f * mScreenThird, mFretX[fret], 2.3f * mScreenThird, mFretPaint);
		}
	}

	private void drawUnstrummedStrings(Canvas canvas)
	{
		for (int string = 0; string < STRINGS; string++)
		{
			mStringPaint.setStrokeWidth(2 * STRING_THICKNESS[string]);
			mStringPaint.setColor(mTouchPointColors[string]);
			mStringPaint.setAlpha(175);
			canvas.drawLine(0, mScreenThird + string * mStringDistance, mWidth, mScreenThird + string * mStringDistance, mStringPaint);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// draw strings
		drawFrets(canvas);

		final int numPoints = mCurrTouchPoint.getNumTouchPoints();

		if (mCurrTouchPoint.isDown())
		{

			final int[] pointerIds = mCurrTouchPoint.getPointerIds();

			final float[] xs = mCurrTouchPoint.getXs();
			final float[] ys = mCurrTouchPoint.getYs();


			int pointerId = 0;
			// checking what points are still pushing the fret board
			// multitouch controller has pointer ids
			// but not always pointer id == finger (because sometimes we can
			// lift some of the fingers)
			for (int finger = 0; finger < MultiTouchController.MAX_TOUCH_POINTS; finger++)
			{
				if (finger == pointerIds[pointerId] && pointerId < numPoints)
				{
					mCurPoint[finger].set(xs[pointerId], ys[pointerId]);

					pointerId++;
				}
				else
				{
					// if the finger is not pushing the string any more - set
					// its coordinates to zero
					mCurPoint[finger].set(0, 0);
				}
			}

			// check what fingers pushing/pulling on what strings
			for (int string = 0; string < STRINGS; string++)
			{
				//clear coordinates
				coordinateList[string].clear();
				//add coordinates of string beginning and end
				coordinateList[string].add(mStringsBegin[string]);
				coordinateList[string].add(mStringsEnd[string]);

				
				
				for (int finger = 0; finger < MultiTouchController.MAX_TOUCH_POINTS; finger++)
				{
					// first checks

					// finger started to push string to the fretboard
					// condition - previous finger was zero.
					// finger thickness cover the string
					if (mCurPoint[finger].y + mFingerThickness > mStringsBegin[string].y && mCurPoint[finger].y - mFingerThickness < mStringsBegin[string].y && mPrevPoint[finger].y == 0 && mStringPushedDown[string][finger] == false)
					{
						mStringPushedDown[string][finger] = true;
					}
					// detect whether finger start pulling the string from up
					// condition - previous finger position was less than string
					// y coordinate,
					// but present position is higher
					else if (mCurPoint[finger].y + mFingerThickness > mStringsBegin[string].y && mPrevPoint[finger].y + mFingerThickness <= mStringsBegin[string].y && mPrevPoint[finger].y != 0 && mStringPulledFromUp[string][finger] == false)
					{
						mStringPulledFromUp[string][finger] = true;
					}
					// detect whether finger start pulling the string from down
					// condition - previous finger position was bigger than
					// string y coordinate,
					// but present position is lower
					else if (mCurPoint[finger].y - mFingerThickness < mStringsBegin[string].y && mPrevPoint[finger].y - mFingerThickness >= mStringsBegin[string].y && mPrevPoint[finger].y != 0 && mStringPulledFromDown[string][finger] == false)
					{
						mStringPulledFromDown[string][finger] = true;
					}

					// now detecting current situation

					if (mStringPushedDown[string][finger] == true)
					{
						// string was pushed down on previous iteration

						// detect whether we still pushing the string
						if (mCurPoint[finger].y + mFingerThickness > mStringsBegin[string].y && mCurPoint[finger].y - mFingerThickness < mStringsBegin[string].y)
						{
							// detect the closest fret
							for (int fret = 0; fret < FRETS; fret++)
							{
								if (mFretX[fret] > mCurPoint[finger].x)
								{
									mStringsPushedDownRightX[string][finger].set(mFretX[fret], mStringsBegin[string].y);

									if (fret > 0)
									{
										mStringsPushedDownLeftX[string][finger].set(mFretX[fret - 1], mStringsBegin[string].y);
									}
									else
									{
										mStringsPushedDownLeftX[string][finger].set(0, mStringsBegin[string].y);
									}

									break;
								}
							}
							coordinateList[string].add(mStringsPushedDownLeftX[string][finger]);
							coordinateList[string].add(mStringsPushedDownRightX[string][finger]);
							

						}
						else
						{
							mStringPushedDown[string][finger] = false;
						}
					}
					// detect if we are still pulling the string from up
					else if (mStringPulledFromUp[string][finger] == true)
					{
						// detect if we still pulling the string

						// pulled to much == strummed the string -> play the
						// sound (potentially)
						// and yes, we are not pulling the string any more.
						if (mCurPoint[finger].y + mFingerThickness > mStringsBegin[string].y + 0.8 * mStringDistance)
						{
							mStringPlay[string] = true;
							mStringPlayFingerX[string] = mCurPoint[finger].x;
							mStringPulledFromUp[string][finger] = false;
						}
						// we started pulling from up and we are moved finger up
						// again
						// so no sound to play. However, we stop pulling the
						// string
						else if (mCurPoint[finger].y + mFingerThickness < mStringsBegin[string].y)
						{
							mStringPulledFromUp[string][finger] = false;
						}
						// ok, we are still pulling this string
						else
						{
							mStringsPulledPoint[string][finger].set(mCurPoint[finger].x, mCurPoint[finger].y + mFingerThickness);
							coordinateList[string].add(mStringsPulledPoint[string][finger]);
						}
					}
					// detect if we are still pulling the string from down
					else if (mStringPulledFromDown[string][finger] == true)
					{
						// detect if we still pulling the string

						// pulled to much == strummed the string -> play the
						// sound (potentially)
						// and yes, we are not pulling the string any more.
						if (mCurPoint[finger].y - mFingerThickness < mStringsBegin[string].y - 0.8 * mStringDistance)
						{
							mStringPlay[string] = true;
							mStringPlayFingerX[string] = mCurPoint[finger].x;
							mStringPulledFromDown[string][finger] = false;
						}
						// we started pulling from down and we are moved finger
						// down again
						// so no sound to play. However, we stop pulling the
						// string
						else if (mCurPoint[finger].y - mFingerThickness > mStringsBegin[string].y)
						{
							mStringPulledFromDown[string][finger] = false;
						}
						// ok, we are still pulling this string
						else
						{
							mStringsPulledPoint[string][finger].set(mCurPoint[finger].x, mCurPoint[finger].y - mFingerThickness);
							coordinateList[string].add(mStringsPulledPoint[string][finger]);
						}
					}


				}
				
				Collections.sort(coordinateList[string], PointComparatorX);
				
				mStringPaint.setStrokeWidth(2 * STRING_THICKNESS[string]);
				mStringPaint.setColor(mTouchPointColors[string]);

				int points = coordinateList[string].size();
				
				prevPoint.set(coordinateList[string].get(0));
				
				if (points >= 2)
				{
					for (int point = 1; point < points; point++)
					{
						currentPoint .set( coordinateList[string].get(point));
						if (prevPoint.y != currentPoint.y)
						{
							mStringPaint.setAlpha(255);
						}
						else
						{
							mStringPaint.setAlpha(175);
						}
						
						canvas.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y, mStringPaint);

						prevPoint.set(currentPoint);
						
					}
				}
				// detect whether we have to play the sound
				if (mStringPlay[string] == true)
				{
					prevPoint.set(coordinateList[string].get(points - 2));
					if (prevPoint.y == mStringsBegin[string].y)
					{
						if (mStringPlayFingerX[string] > prevPoint.x)
						{
						
							//detect the fret
							int playFret = 0;
							// detect the closest fret
							for (int fret = 0; fret < FRETS; fret++)
							{
								if (mFretX[fret] == prevPoint.x)
								{
									playFret = fret;
									break;
								}
		
							}
							
							playFret += 1;
							Log.i("play", "play string " + string + " fret " + playFret);
							mStringPlay[string] = false;
						}
					}
				}
			
			}
			
			// draw the points
			for (int idx = 0; idx < numPoints; idx++)
			{
				// Show touch circles
				mPaint.setColor(mTouchPointColors[pointerIds[idx] + 1]);
				canvas.drawCircle(xs[idx], ys[idx], mFingerThickness, mPaint);

				// Label touch points on top of everything else
				canvas.drawText(" "+ (pointerIds[idx] + 1), xs[idx] + mFingerThickness, ys[idx] - mFingerThickness, mPaint);
			}


			for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			{
				mPrevPoint[i].set(mCurPoint[i]);
			}
		}
		else if (numPoints == 1)
		{
			for (int finger = 0; finger < MultiTouchController.MAX_TOUCH_POINTS; finger++)
			{
				mCurPoint[finger].set(0, 0);
				mPrevPoint[finger].set(0, 0);
			}

			for (int string = 0; string < STRINGS; string++)
			{
				mStringPlay[string] = false;
				for (int finger = 0; finger < MultiTouchController.MAX_TOUCH_POINTS; finger++)
				{
					mStringPushedDown[string][finger] = false;
					mStringPulledFromDown[string][finger] = false;
					mStringPulledFromUp[string][finger] = false;
				}
			}
			// draw unstrummed strings
			drawUnstrummedStrings(canvas);
		}
		else
		{
			// draw unstrummed strings
			drawUnstrummedStrings(canvas);
		}

	}

}
