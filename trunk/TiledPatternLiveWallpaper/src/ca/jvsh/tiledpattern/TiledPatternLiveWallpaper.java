package ca.jvsh.tiledpattern;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class TiledPatternLiveWallpaper extends WallpaperService 
{
	public static final String	SHARED_PREFS_NAME	= "tiledpatternsettings";

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
		return new TiledPatternEngine();
	}

	class TiledPatternEngine extends Engine
							 implements SharedPreferences.OnSharedPreferenceChangeListener
	{

		private final Handler		mHandler		= new Handler();
		private float				mTouchX			= -1;
		private float				mTouchY			= -1;
		private final Paint			mPaint			= new Paint();
		private final Runnable		mDrawPattern	=   new Runnable()
														{
															public void run()
															{
																drawFrame();
															}
														};
		private boolean				mVisible;
		private SharedPreferences	mPreferences;

		private Rect				mRectFrame;

		private int					mFrameCounter	= 0;
		/** The drawable to use as the far background of the animation canvas */
		private Bitmap mBackgroundImageFar;
		private int moveUp;
		private int moveDown;

		Resources mRes;
		
		TiledPatternEngine()
		{
			mRes = getResources();
			mBackgroundImageFar = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_simple_paisley);

			moveUp = 0;
			moveDown = 0;

			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);

			mPreferences = TiledPatternLiveWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key)
		{

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
		public void onSurfaceChanged(SurfaceHolder holder,
									 int format,
									 int width,
									 int height)
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
			paint.setAlpha(255);
			moveUp ++;
			moveDown ++;
			
			c.drawBitmap(mBackgroundImageFar, 0 + moveUp, 0 + moveDown, paint);
			c.drawBitmap(mBackgroundImageFar, mBackgroundImageFar.getWidth() + moveUp, 0 + moveDown, paint);
			c.drawBitmap(mBackgroundImageFar, 0 + moveUp, mBackgroundImageFar.getHeight() + moveDown, paint);
			c.drawBitmap(mBackgroundImageFar, mBackgroundImageFar.getWidth() + moveUp, mBackgroundImageFar.getHeight() + moveDown, paint);

			if(moveUp > 100)
				moveUp = 0;
			if(moveDown > 100)
				moveDown = 0;
			
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

			mRectFrame = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);

			int rotation = display.getRotation();

			/*if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				mHorizontal = false;
			else
				mHorizontal = true;

			System.out.println("mHorizontal "+mHorizontal);
			System.out.println("mShape "+mShape);
			if(mShape.compareToIgnoreCase("smpte") == 0)
			{
				System.out.println("mShape == smpte");
			
				CreateSmpte();
			}
			else if(mShape.compareToIgnoreCase("bars") == 0)
			{
				System.out.println("mShape == bars");
				CreateBars();
			}
			else
			{
				System.out.println("mShape == ebu");
				CreateEbu();
			}*/
		}
	}
}