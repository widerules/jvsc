package ca.jvsh.svmtoy;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DotSurfaceView extends View implements MultiTouchObjectCanvas<Object>
{
	private MultiTouchController<Object>	multiTouchController;
	private PointInfo						mCurrTouchPoint;

	private final Paint						mPaint			= new Paint();
	// Application handle.
	private Context							appContext;

	// Debugging tag.
	private static final String				TAG				= "FluteView";

	private Canvas							myCanvas;
	private Bitmap							backbuffer;
	private float							radius;

	private final Paint						mDotPaint		= new Paint();

	public int								dotColor;
	public int								mColorSwitch	= 0;
	
	public TIntList				mListLabels;
	public TFloatList			mListX;
	public TFloatList			mListY;


	public DotSurfaceView(Context context)
	{
		this(context, null);
	}

	public DotSurfaceView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public DotSurfaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		appContext = context;

		multiTouchController = new MultiTouchController<Object>(this);
		mCurrTouchPoint = new PointInfo();

		// Set up our paint.
		mPaint.setAntiAlias(true);
		mDotPaint.setAntiAlias(true);
		dotColor = Color.RED;
		
		mListLabels = new TIntArrayList();
		mListX = new TFloatArrayList();
		mListY = new TFloatArrayList();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		((Activity) appContext).onTouchEvent(event);
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
		if (backbuffer == null)
		{
			init();
		}
		canvas.drawBitmap(backbuffer, 0, 0, mPaint);

		int numPoints = mCurrTouchPoint.getNumTouchPoints();

		if (mCurrTouchPoint.isDown())
		{
			float[] xs = mCurrTouchPoint.getXs();
			float[] ys = mCurrTouchPoint.getYs();

			for (int i = 0; i < numPoints; i++)
			{
				mDotPaint.setColor(dotColor);
				myCanvas.drawCircle(xs[i], ys[i], radius, mDotPaint);
				
				mListLabels.add(mColorSwitch);
				mListX.add(xs[i]/getWidth());
				mListY.add(ys[i]/getHeight());
			}
			invalidate();
		}
	}

	private void init()
	{
		radius = 5f;
		backbuffer = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		myCanvas = new Canvas(backbuffer);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.TRANSPARENT);
		myCanvas.drawRect(0, 0, getWidth(), getHeight(), p);

	}
	
	public void cleanSurface()
	{
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.TRANSPARENT);
		myCanvas.drawRect(0, 0, getWidth(), getHeight(), p);
		
		mListLabels.clear();
		mListX.clear();
		mListY.clear();
	
		invalidate();
	}
}
