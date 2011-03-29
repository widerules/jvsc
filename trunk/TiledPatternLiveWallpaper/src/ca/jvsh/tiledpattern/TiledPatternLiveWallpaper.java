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
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.os.SystemClock;

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
		//private float				mTouchX			= -1;
		//private float				mTouchY			= -1;
		//private final Paint			mPaint			= 
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

		private final int			PATTERNS = 6;
		
		//! patterns that we drawing
		private Bitmap[] 			mPattern;
		
		private int[]				tile_size_x;
		private int[]				tile_size_y;

		private int					screen_size_x;
		private int					screen_size_y;

		private float				tile_shift_x;
		private float				tile_shift_y;
		private float				movement_speed_x;
		private float				movement_speed_y;

		private int[]				fit_x;
		private int[]				fit_y;

		private int[]				remain_x;
		private int[]				remain_y;

		private int					mCurrentPattern;
		private int					mNextPattern;
		
		private final Paint 		paint = new Paint();
		private int 				mPreviousOffset;
		Resources mRes;

		//private long lastVisibilityOnChange;

		TiledPatternEngine()
		{
			mRes = getResources();
			mPattern = new Bitmap[PATTERNS];

			//load patterns
			mPattern[0] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_simple_paisley);
			mPattern[1] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_haunted_two_regal);
			mPattern[2] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_kiwi);
			mPattern[3] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_mahalo);
			mPattern[4] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_salvage);
			mPattern[5] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_finding_a_cure);

			//set size
			tile_size_x = new int[PATTERNS];
			tile_size_y = new int[PATTERNS];
			for(int i = 0; i < PATTERNS; i++)
			{
				tile_size_x[i] = mPattern[i].getWidth();
				tile_size_y[i] = mPattern[i].getHeight();

				//System.out.println("Pattern " + i+ ", tile_size_x = " + tile_size_x[i] + ", tile_size_y = "+tile_size_y[i]);
			}

			fit_x =  new int[PATTERNS];
			fit_y =  new int[PATTERNS];
			remain_x =  new int[PATTERNS];
			remain_y =  new int[PATTERNS];

			movement_speed_x = -1;
			movement_speed_y = -1;

			tile_shift_x = 0;
			tile_shift_y = 0;

			mCurrentPattern = 0;
			mNextPattern = mCurrentPattern + 1;

			mPreviousOffset = 0;
			//paint 

			/*final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);*/

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
			//lastVisibilityOnChange = SystemClock.elapsedRealtime();
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
			//if (SystemClock.elapsedRealtime() - lastVisibilityOnChange > 30)
				//return;
			int offset = (int)(xOffset * 100);
			if(offset%25 == 0 && offset != mPreviousOffset)
			{
				mPreviousOffset = offset;

				System.out.println("offset " + offset);
				System.out.println("onOffsetsChanged xOffset" + xOffset + " yOffset" + yOffset + " xStep" + xStep + " yStep" + yStep + " xPixels" + xPixels + " yPixels" + yPixels);
				mCurrentPattern = mNextPattern;
				mNextPattern = mCurrentPattern + 1;
				if(mNextPattern > 5)
					mNextPattern = 0;
				System.out.println("mCurrentPattern " + mCurrentPattern);
				System.out.println("mNextPattern " + mNextPattern);
			}


			drawFrame();
			super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
		}

		/*
		 * Store the position of the touch event so we can use it for drawing
		 * later
		 */
		/*@Override
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
		}*/

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
					//drawTouchPoint(c);
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

			//Paint paint = new Paint();

			paint.setAlpha(125);
			tile_shift_x += movement_speed_x;
			tile_shift_y += movement_speed_y;

			for(int x = -1; x < fit_x[mCurrentPattern] + 1; x++)
				for(int y = -1; y < fit_y[mCurrentPattern] + 1; y++)
					c.drawBitmap(mPattern[mCurrentPattern], tile_size_x[mCurrentPattern] * x + tile_shift_x, tile_size_y[mCurrentPattern] * y + tile_shift_y, paint);

			if(tile_shift_x > tile_size_x[mCurrentPattern] )
				tile_shift_x = 0;
			if(tile_shift_y > tile_size_y[mCurrentPattern] )
				tile_shift_y = 0;

			if(tile_shift_x < -(tile_size_x[mCurrentPattern] + remain_x[mCurrentPattern]) )
				tile_shift_x = -remain_x[mCurrentPattern];
			if(tile_shift_y < -(tile_size_y[mCurrentPattern] + remain_y[mCurrentPattern]) )
				tile_shift_y = -remain_y[mCurrentPattern];

			c.restore();
		}

		/*void drawTouchPoint(Canvas c)
		{
			if (mTouchX >= 0 && mTouchY >= 0)
			{
				c.drawCircle(mTouchX, mTouchY, 80, mPaint);
			}
		}*/

		void initFrameParams()
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);

			mRectFrame = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);

			//int rotation = display.getRotation();

			screen_size_x = metrics.widthPixels;
			screen_size_y = metrics.heightPixels;

			//System.out.println("screen_size_x = " + screen_size_x + ", screen_size_y = "+screen_size_y);

			for(int i = 0; i < PATTERNS; i++)
			{
				fit_x[i] = (int)FloatMath.ceil( (float)(screen_size_x) / (float)(tile_size_x[i]) );
				fit_y[i] = (int)FloatMath.ceil( (float)(screen_size_y) / (float)(tile_size_y[i]) );

				//System.out.println("Pattern " + i+ ", fit_x = " + fit_x[i] + ", fit_y = "+fit_y[i]);

				remain_x[i] = fit_x[i] * tile_size_x[i] - screen_size_x;
				remain_y[i] = fit_y[i] * tile_size_y[i] - screen_size_y;

				//System.out.println("Pattern " + i+ ", remain_x = " + remain_x[i] + ", remain_y = "+remain_y[i]);
			}

			/*tile_size_x = mBackgroundImageFar.getWidth();
			tile_size_y = mBackgroundImageFar.getHeight();

			int fit_x = 
			

			*/
			/*if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				mHorizontal = false;
			else
				mHorizontal = true;

			*/
		}
	}
}