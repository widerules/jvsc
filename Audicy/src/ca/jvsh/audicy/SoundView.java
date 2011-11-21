package ca.jvsh.audicy;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SoundView extends View implements MultiTouchObjectCanvas<Object>
{
	private MultiTouchController<Object> multiTouchController;
	private PointInfo mCurrTouchPoint;

	private static final int[] TOUCH_COLORS = { Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLUE, Color.WHITE,
		Color.GRAY, Color.LTGRAY, Color.DKGRAY };

	private Paint mLinePaintMultiTouch = new Paint();
	private Paint mPointLabelPaint = new Paint();
	private Paint mTouchTheScreenLabelPaint = new Paint();
	private Paint mPointLabelBg = new Paint();

	private int[] mTouchPointColors = new int[MultiTouchController.MAX_TOUCH_POINTS];
	
	public SoundView(Context context)
	{
		this(context, null);
	}

	public SoundView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public SoundView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		multiTouchController = new MultiTouchController<Object>(this);
		mCurrTouchPoint = new PointInfo();

		mLinePaintMultiTouch.setStrokeWidth(5);
		mLinePaintMultiTouch.setStyle(Style.STROKE);
		mLinePaintMultiTouch.setAntiAlias(true);
		mPointLabelPaint.setTextSize(82);
		mPointLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPointLabelPaint.setAntiAlias(true);
		mTouchTheScreenLabelPaint.setColor(Color.GRAY);
		mTouchTheScreenLabelPaint.setTextSize(24);
		mTouchTheScreenLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTouchTheScreenLabelPaint.setAntiAlias(true);
		
		mPointLabelBg.set(mPointLabelPaint);
		mPointLabelBg.setColor(Color.BLACK);
		mPointLabelBg.setAlpha(180);
		mPointLabelBg.setStyle(Style.STROKE);
		mPointLabelBg.setStrokeWidth(15);
		/*mAngLabelPaint.setTextSize(32);
		mAngLabelPaint.setTypeface(Typeface.SANS_SERIF);
		mAngLabelPaint.setColor(mLinePaintCrossHairs.getColor());
		mAngLabelPaint.setTextAlign(Align.CENTER);
		mAngLabelPaint.setAntiAlias(true);
		mAngLabelBg.set(mAngLabelPaint);
		mAngLabelBg.setColor(Color.BLACK);
		mAngLabelBg.setAlpha(180);
		mAngLabelBg.setStyle(Style.STROKE);
		mAngLabelBg.setStrokeWidth(15);*/
		
		for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
			mTouchPointColors[i] = i < TOUCH_COLORS.length ? TOUCH_COLORS[i] : (int) (Math.random() * 0xffffff) + 0xff000000;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// Pass the event on to the controller
		return multiTouchController.onTouchEvent(event);
	}
	
	
	public Object getDraggableObjectAtPoint(PointInfo pt) {
		// IMPORTANT: to start a multitouch drag operation, this routine must return non-null
		return this;
	}

	public void getPositionAndScale(Object obj, PositionAndScale objPosAndScaleOut) {
		// We aren't dragging any objects, so this doesn't do anything in this app
	}

	public void selectObject(Object obj, PointInfo touchPoint) {
		// We aren't dragging any objects in this particular app, but this is called when the point goes up (obj == null) or down (obj != null),
		// save the touch point info
		touchPointChanged(touchPoint);
	}

	public boolean setPositionAndScale(Object obj, PositionAndScale newObjPosAndScale, PointInfo touchPoint) {
		// Called during a drag or stretch operation, update the touch point info
		touchPointChanged(touchPoint);
		return true;
	}

	/**
	 * Called when the touch point info changes, causes a redraw.
	 * 
	 * @param touchPoint
	 */
	private void touchPointChanged(PointInfo touchPoint) {
		// Take a snapshot of touch point info, the touch point is volatile
		mCurrTouchPoint.set(touchPoint);
		invalidate();
	}
	
	/*public void addPoint()
	{
		int numPoints = mCurrTouchPoint.getNumTouchPoints();
		float[] xs = mCurrTouchPoint.getXs();
		float[] ys = mCurrTouchPoint.getYs();
		float[] pressures = mCurrTouchPoint.getPressures();
		int[] pointerIds = mCurrTouchPoint.getPointerIds();
		
	}*/
	
	private void paintText(Canvas canvas, String msg, float vPos) {
		Rect bounds = new Rect();
		int msgLen = msg.length();
		mTouchTheScreenLabelPaint.getTextBounds(msg, 0, msgLen, bounds);
		canvas.drawText(msg, (canvas.getWidth() - bounds.width()) * .5f, vPos, mTouchTheScreenLabelPaint);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mCurrTouchPoint.isDown()) {
			int numPoints = mCurrTouchPoint.getNumTouchPoints();
			float[] xs = mCurrTouchPoint.getXs();
			float[] ys = mCurrTouchPoint.getYs();
			float[] pressures = mCurrTouchPoint.getPressures();
			int[] pointerIds = mCurrTouchPoint.getPointerIds();
			float x = mCurrTouchPoint.getX(), y = mCurrTouchPoint.getY();
			float wd = getWidth(), ht = getHeight();

			
			// Show touch circles
			for (int i = 0; i < numPoints; i++)
			{
				mLinePaintMultiTouch.setColor(mTouchPointColors[i]);
				float r = 70 + pressures[i] * 120;
				canvas.drawCircle(xs[i], ys[i], r, mLinePaintMultiTouch);
			}

			// Log touch point indices
			if (MultiTouchController.DEBUG) {
				StringBuilder buf = new StringBuilder();
				for (int i = 0; i < numPoints; i++)
					buf.append(" " + i + "->" + pointerIds[i]);
				Log.i("MultiTouchVisualizer", buf.toString());
			}

			// Label touch points on top of everything else
			for (int idx = 0; idx < numPoints; idx++) {
				int id = pointerIds[idx];
				mPointLabelPaint.setColor(mTouchPointColors[idx]);
				float r = 70 + pressures[idx] * 120, d = r * .71f;
				String label = (idx + 1) + (idx == id ? "" : "(id:" + (id + 1) + ")");
				canvas.drawText(label, xs[idx] + d, ys[idx] - d, mPointLabelBg);
				canvas.drawText(label, xs[idx] + d, ys[idx] - d, mPointLabelPaint);
			}
		} 
		else 
		{
			//float spacing = mTouchTheScreenLabelPaint.getFontSpacing();
			//float totHeight = spacing * infoLines.length;
			//for (int i = 0; i < infoLines.length; i++)
				//paintText(canvas, infoLines[i], (canvas.getHeight() - totHeight) * .5f + i * spacing);
		}
	}
}
