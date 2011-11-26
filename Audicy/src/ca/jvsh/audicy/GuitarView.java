package ca.jvsh.audicy;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

public class GuitarView extends View implements MultiTouchObjectCanvas<Object>
{
	private MultiTouchController<Object>	multiTouchController;
	private PointInfo						mCurrTouchPoint;

	private static final int[]				TOUCH_COLORS			= { Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLUE, Color.WHITE, Color.GRAY, Color.LTGRAY, Color.DKGRAY };
	private static final float[]			STRING_THICKNESS = {1.0f, 1.3f, 1.7f, 2.6f, 3.6f, 4.6f};
	private static final float[]			STRING_NOTES = {329.63f, 440.0f, 587.33f, 783.99f, 987.77f, 1318.5f};
	private final Paint	mStringPaint = new Paint();
	private final Paint						mPaint	= new Paint();

	private int[]							mTouchPointColors		= new int[MultiTouchController.MAX_TOUCH_POINTS];

	private float[] mCurX = new float [MultiTouchController.MAX_TOUCH_POINTS];
	private float[] mCurY = new float [MultiTouchController.MAX_TOUCH_POINTS];
	private float[] mPrevX = new float [MultiTouchController.MAX_TOUCH_POINTS];
	private float[] mPrevY = new float [MultiTouchController.MAX_TOUCH_POINTS];
	private float[] mStringPlayedX = new float [MultiTouchController.MAX_TOUCH_POINTS];
	private float[] mStringPlayedY = new float [MultiTouchController.MAX_TOUCH_POINTS];
	
	
	float mScreenWidth;
	float mScreenHeight;
	
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
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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

		for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			mTouchPointColors[i] = i < TOUCH_COLORS.length ? TOUCH_COLORS[i] : (int) (Math.random() * 0xffffff) + 0xff000000;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// Pass the event on to the controller
		return multiTouchController.onTouchEvent(event);
	}

	public Object getDraggableObjectAtPoint(PointInfo pt)
	{
		// IMPORTANT: to start a multitouch drag operation, this routine must
		// return non-null
		return this;
	}

	public void getPositionAndScale(Object obj, PositionAndScale objPosAndScaleOut)
	{
		// We aren't dragging any objects, so this doesn't do anything in this
		// app
	}

	public void selectObject(Object obj, PointInfo touchPoint)
	{
		// We aren't dragging any objects in this particular app, but this is
		// called when the point goes up (obj == null) or down (obj != null),
		// save the touch point info
		touchPointChanged(touchPoint);
	}

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
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//draw strings
		
		int numPoints = mCurrTouchPoint.getNumTouchPoints();
		int[] pointerIds = mCurrTouchPoint.getPointerIds();

		float wd = getWidth(), ht = getHeight();

		float third = ht / 3.0f;
		float third_six = third / 5.0f;
		
		int radius = 30;

		//draw frets
		{
			float scale=wd, location, scaling_factor;
			int num_frets = 12, fret;
			
			scaling_factor = 0;
			float[] distance = new float[num_frets+1];
			distance[0] = 0;
			
			for (fret = 1; fret <= num_frets; fret++) 
			{
			   location = scale - distance[fret-1];
			   scaling_factor = location / 17.817f;
			   distance[fret] = distance[fret-1] + scaling_factor;
		   
			}
			
			for (fret = 1; fret <= num_frets; fret++) 
				distance[fret] = wd * distance[fret] / distance[num_frets] ;
			
			mStringPaint.setStrokeWidth(3);
			mStringPaint.setColor(Color.RED);
			for (fret = 0; fret <= num_frets; fret++) 
				canvas.drawLine(distance[fret], 0.9f * third, distance[fret], 2.1f*third, mStringPaint);
		}
		
		//draw strings
		float[] normalStringsY = new float [6];
		boolean[] stringDrawn = new boolean [6];

		{
			for(int i= 0; i < 6; i++)
			{
				normalStringsY[i] = third + i * third_six;
				//mStringPaint.setStrokeWidth(2 * STRING_THICKNESS[i]);
				//mStringPaint.setColor(mTouchPointColors[i]);
				//canvas.drawLine(0, third + i * third_six, wd, third + i * third_six, mStringPaint);
			}
		}
		
		float[] xs = mCurrTouchPoint.getXs();
		float[] ys = mCurrTouchPoint.getYs();
		if (mCurrTouchPoint.isDown())
		{
			//float x = mCurrTouchPoint.getX(), y = mCurrTouchPoint.getY();

			//Karpluser karpluser = new Karpluser(440.0f);
			// Log touch point indices
			if (MultiTouchController.DEBUG)
			{
				StringBuilder buf = new StringBuilder();
				for (int i = 0; i < numPoints; i++)
					buf.append(" " + i + "->" + pointerIds[i]);
				Log.i("MultiTouchVisualizer", buf.toString());
			}
			
			int pointerId = 0;
			for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			{
				if(i == pointerIds[pointerId] && pointerId < numPoints)
				{
					mCurX[i] = xs[pointerId];
					mCurY[i] = ys[pointerId];
					
					pointerId++;
				}
				else
				{
					mCurX[i] = 0;
					mCurY[i] = 0;
				}
			}
			
			for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			{
				for(int string= 0; string < 6; string++)
				{
					if(Math.abs(mCurY[i] - normalStringsY[string]) < third_six/2.0f)
					{
						if(stringDrawn[string] == false)
						{
							Log.i("test", "i " + i + " string "+ string + " mCurX " + mCurX[i] + " mCurY " + mCurY[i]);
							mStringPaint.setStrokeWidth(2 * STRING_THICKNESS[string]);
							mStringPaint.setColor(mTouchPointColors[string]);
							
							canvas.drawLine(0, normalStringsY[string], mCurX[i], mCurY[i], mStringPaint);
							canvas.drawLine(mCurX[i], mCurY[i], wd, normalStringsY[string], mStringPaint);
							stringDrawn[string] = true;
						}
					}
				}
			}
			
			for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			{
				for (int j = 0; j < MultiTouchController.MAX_TOUCH_POINTS; j++)
				{
					for(int string= 0; string < 6; string++)
					{
						if(Math.abs(mPrevY[i] - normalStringsY[string]) < third_six/2.0f && Math.abs(mCurY[i] - normalStringsY[string]) > third_six/2.0f)
						{
							if( mCurY[i] != mStringPlayedY[i])
							{
					        	 Karpluser karpluser = new Karpluser(STRING_NOTES[string]);     	
					        									
								
								mStringPlayedY[i] = mCurY[i];
							}
						}
					}
				}
			}
			
			for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			{
				mPrevX[i] = mCurX[i];
				mPrevY[i] = mCurY[i];
			}
			
		}
		for(int i= 0; i < 6; i++)
		{
			if(stringDrawn[i] == false )
			{
				mStringPaint.setStrokeWidth(2 * STRING_THICKNESS[i]);
				mStringPaint.setColor(mTouchPointColors[i]);
				canvas.drawLine(0, normalStringsY[i], wd, normalStringsY[i], mStringPaint);
			}
		}
		
		if (mCurrTouchPoint.isDown())
		{	
			

			for (int idx = 0; idx < numPoints; idx++)
			{
				// Show touch circles
				mPaint.setColor(mTouchPointColors[idx]);
				canvas.drawCircle(xs[idx], ys[idx], radius, mPaint);

				// Label touch points on top of everything else
				String label = (idx + 1) + (idx == pointerIds[idx] ? "" : "(id:" + (pointerIds[idx] + 1) + ")");

				canvas.drawText(label, xs[idx] + radius, ys[idx] - radius, mPaint);
			}
		}



	}
	
	
}
