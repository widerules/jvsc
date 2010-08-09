package ca.jvsh.smpte;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class SMPTE extends WallpaperService
{

	public static final String	SHARED_PREFS_NAME	= "smptesettings";

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine()
	{
		return new TestPatternEngine();
	}

	class TestPatternEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener
	{

		private final Handler		mHandler			= new Handler();
		private float				mTouchX				= -1;
		private float				mTouchY				= -1;
		private final Paint			mPaint				= new Paint();
		private final Runnable		mDrawPattern		= new Runnable()
														{
															public void run()
															{
																drawFrame();
															}
														};
		private boolean				mVisible;
		private SharedPreferences	mPreferences;

		private Rect				mRectFrame;

		private Rect[]				mColorRectangles	= new Rect[27];
		private int[]				rectColor			= { 0xFF696969, 0xFFC1C1C1, 0xFFC1C100, 0xFF00C1C1, 0xFF00C100, 0xFFC100C1, 0xFFC10000, 0xFF0000C1, 0xFF696969, 0xFF00FFFF, 0xFFFFFF00, 0xFF052550, 0xFF36056D, 0xFF0000FF, 0xFFFF0000, 0xFFC1C1C1, 0xFF2B2B2B, 0xFF050505, 0xFFFFFFFF, 0xFF050505, 0xFF000000, 0xFF050505, 0xFF0A0A0A, 0xFF050505, 0xFF0D0D0D, 0xFF050505, 0xFF2b2b2b };
		private Rect				mGradientRect;
		GradientDrawable			mGradient;
		private int					mRotation			= Surface.ROTATION_0;
		private int					mFrameCounter		= 0;
		private boolean				mMotion				= true;
		private String				mShape				= "smpte";

		TestPatternEngine()
		{
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);

			mPreferences = SMPTE.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key)
		{
			mShape = prefs.getString("smpte_testpattern", "smpte");
			mMotion = prefs.getBoolean("smpte_movement", true);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				drawFrame();
			}
			else
			{
				mHandler.removeCallbacks(mDrawPattern);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);

			initFrameParams();

			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels)
		{

			drawFrame();
		}

		/*
		 * Store the position of the touch event so we can use it for drawing
		 * later
		 */
		@Override
		public void onTouchEvent(MotionEvent event)
		{
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				mTouchX = event.getX();
				mTouchY = event.getY();
			}
			else
			{
				mTouchX = -1;
				mTouchY = -1;
			}
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame()
		{
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// draw something
					drawPattern(c);
					drawTouchPoint(c);
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawPattern);
			if (mVisible)
			{
				mHandler.postDelayed(mDrawPattern, 1000 / 25);
			}
		}

		void drawPattern(Canvas c)
		{
			c.save();
			c.drawColor(0xff000000);

			Paint paint = new Paint();

			if (mMotion)
			{
				mFrameCounter++;
				if (mFrameCounter > mRectFrame.bottom)
					mFrameCounter = 0;
				for (int i = 0; i < 27; i++)
				{
					paint.setColor(rectColor[i]);
					if (mRotation == Surface.ROTATION_0 || mRotation == Surface.ROTATION_180)
					{
						c.drawRect(mColorRectangles[i].left, mColorRectangles[i].top + mFrameCounter, mColorRectangles[i].right, mColorRectangles[i].bottom + mFrameCounter, paint);
					}
					else
					{
						c.drawRect(mColorRectangles[i].left + mFrameCounter, mColorRectangles[i].top, mColorRectangles[i].right + mFrameCounter, mColorRectangles[i].bottom, paint);
					}
				}

				if (mRotation == Surface.ROTATION_0 || mRotation == Surface.ROTATION_180)
				{
					mGradient.setBounds(mGradientRect.left, mGradientRect.top + mFrameCounter, mGradientRect.right, mGradientRect.bottom + mFrameCounter);
				}
				else
				{
					mGradient.setBounds(mGradientRect.left + mFrameCounter, mGradientRect.top, mGradientRect.right + mFrameCounter, mGradientRect.bottom);
				}

				mGradient.draw(c);
			}
			else
			{
				for (int i = 0; i < 27; i++)
				{
					paint.setColor(rectColor[i]);
					c.drawRect(mColorRectangles[i], paint);
				}
				mGradient.setBounds(mGradientRect);
				mGradient.draw(c);
			}
			c.restore();
		}

		void drawTouchPoint(Canvas c)
		{
			if (mTouchX >= 0 && mTouchY >= 0)
			{
				c.drawCircle(mTouchX, mTouchY, 80, mPaint);
			}
		}

		void initFrameParams()
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);

			mRectFrame = new Rect(0, 0, metrics.heightPixels, metrics.widthPixels);

			mRotation = display.getRotation();
			if (mRotation == Surface.ROTATION_0 || mRotation == Surface.ROTATION_180)
			{
				int topHeight = mRectFrame.bottom * 5 / 12;
				int bottomHeight = mRectFrame.bottom / 4;
				int wideColumnWidth = mRectFrame.right / 8;
				int narrowColumnWidth = mRectFrame.right * 3 / 28;

				mColorRectangles[0] = new Rect(topHeight, 0, mRectFrame.bottom, wideColumnWidth);

				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(topHeight, mColorRectangles[i - 1].bottom, mRectFrame.bottom, narrowColumnWidth + mColorRectangles[i - 1].bottom);
				}

				mColorRectangles[8] = new Rect(topHeight, mColorRectangles[7].bottom, mRectFrame.bottom, mRectFrame.right);

				for (int i = 0; i < 2; i++)
				{
					int middleLeft = mRectFrame.bottom * (4 - i) / 12;
					int middleRight = mRectFrame.bottom * (5 - i) / 12;
					mColorRectangles[i + 9] = new Rect(middleLeft, 0, middleRight, wideColumnWidth);
					mColorRectangles[i + 11] = new Rect(middleLeft, wideColumnWidth, middleRight, narrowColumnWidth + wideColumnWidth);
					mColorRectangles[i + 13] = new Rect(middleLeft, narrowColumnWidth * 7 + wideColumnWidth, middleRight, mRectFrame.right);
				}
				mColorRectangles[15] = new Rect(mRectFrame.bottom * 4 / 12, narrowColumnWidth + wideColumnWidth, mRectFrame.bottom * 5 / 12, narrowColumnWidth * 7 + wideColumnWidth);

				mGradientRect = new Rect(mRectFrame.bottom * 3 / 12, mColorRectangles[15].top, mColorRectangles[15].left, mColorRectangles[15].bottom);
				mGradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[] { 0xff050505, 0xfffdfdfd });
				mGradient.setBounds(mGradientRect);

				mColorRectangles[16] = new Rect(0, 0, bottomHeight, wideColumnWidth);
				mColorRectangles[17] = new Rect(0, mColorRectangles[16].bottom, bottomHeight, mRectFrame.right * 9 / 56 + mColorRectangles[16].bottom);
				mColorRectangles[18] = new Rect(0, mColorRectangles[17].bottom, bottomHeight, mRectFrame.right * 3 / 14 + mColorRectangles[17].bottom);
				mColorRectangles[19] = new Rect(0, mColorRectangles[18].bottom, bottomHeight, mRectFrame.right * 45 / 448 + mColorRectangles[18].bottom);
				for (int i = 20; i < 25; i++)
				{
					mColorRectangles[i] = new Rect(0, mColorRectangles[i - 1].bottom, bottomHeight, mRectFrame.right * 15 / 448 + mColorRectangles[i - 1].bottom);
				}
				mColorRectangles[25] = new Rect(0, mColorRectangles[24].bottom, bottomHeight, narrowColumnWidth + mColorRectangles[24].bottom);
				mColorRectangles[26] = new Rect(0, mColorRectangles[25].bottom, bottomHeight, mRectFrame.right);
			}
			else
			{
				int topHeight = mRectFrame.right * 7 / 12;
				int bottomHeight = mRectFrame.right * 3 / 4;
				int wideColumnWidth = mRectFrame.bottom / 8;
				int narrowColumnWidth = mRectFrame.bottom * 3 / 28;

				mColorRectangles[0] = new Rect(0, 0, wideColumnWidth, topHeight);
				for (int i = 1; i < 8; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, 0, mColorRectangles[i - 1].right + narrowColumnWidth, topHeight);
				}

				mColorRectangles[8] = new Rect(mColorRectangles[7].right, 0, mRectFrame.bottom, topHeight);

				for (int i = 0; i < 2; i++)
				{
					int middleTop = mRectFrame.right * (7 + i) / 12;
					int middleBottom = mRectFrame.right * (8 + i) / 12;
					mColorRectangles[i + 9] = new Rect(0, middleTop, wideColumnWidth, middleBottom);
					mColorRectangles[i + 11] = new Rect(wideColumnWidth, middleTop, narrowColumnWidth + wideColumnWidth, middleBottom);
					mColorRectangles[i + 13] = new Rect(narrowColumnWidth * 7 + wideColumnWidth, middleTop, mRectFrame.bottom, middleBottom);
				}

				mColorRectangles[15] = new Rect(narrowColumnWidth + wideColumnWidth, topHeight, narrowColumnWidth * 7 + wideColumnWidth, mRectFrame.right * 8 / 12);

				mGradientRect = new Rect(mColorRectangles[15].left, mColorRectangles[15].bottom, mColorRectangles[15].right, mRectFrame.right * 9 / 12);
				mGradient = new GradientDrawable(Orientation.LEFT_RIGHT, new int[] { 0xff050505, 0xfffdfdfd });
				mGradient.setBounds(mGradientRect);

				mColorRectangles[16] = new Rect(0, bottomHeight, wideColumnWidth, mRectFrame.right);
				mColorRectangles[17] = new Rect(mColorRectangles[16].right, bottomHeight, mRectFrame.bottom * 9 / 56 + mColorRectangles[16].right, mRectFrame.right);
				mColorRectangles[18] = new Rect(mColorRectangles[17].right, bottomHeight, mRectFrame.bottom * 3 / 14 + mColorRectangles[17].right, mRectFrame.right);
				mColorRectangles[19] = new Rect(mColorRectangles[18].right, bottomHeight, mRectFrame.bottom * 45 / 448 + mColorRectangles[18].right, mRectFrame.right);
				for (int i = 20; i < 25; i++)
				{
					mColorRectangles[i] = new Rect(mColorRectangles[i - 1].right, bottomHeight, mRectFrame.bottom * 15 / 448 + mColorRectangles[i - 1].right, mRectFrame.right);
				}
				mColorRectangles[25] = new Rect(mColorRectangles[24].right, bottomHeight, narrowColumnWidth + mColorRectangles[24].right, mRectFrame.right);
				mColorRectangles[26] = new Rect(mColorRectangles[25].right, bottomHeight, mRectFrame.bottom, mRectFrame.right);
			}
		}
	}
}