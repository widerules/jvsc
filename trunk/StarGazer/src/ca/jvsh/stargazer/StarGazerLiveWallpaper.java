package ca.jvsh.stargazer;

import ca.jvsh.stargazer.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class StarGazerLiveWallpaper extends WallpaperService 
{
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
		return new StarGazerEngine();
	}
	

	class StarGazerEngine extends Engine
	{

		private static final int			STARS = 6;
		private static final int			STARS_COUNT = 100;
		private static final int			CLOUDS = 6;

		private final Handler		mHandler		=   new Handler();

		private final Runnable		mDrawPattern	=   new Runnable()
														{
															public void run()
															{
																drawFrame();
															}
														};
		private boolean				mVisible;


		//! patterns that we drawing
		private Bitmap[]			mStars;
		private int[]				nStarIndex;
		private int[]				mStarsX;
		private int[]				mStarsY;
		private int[]				mStarsAlpha;

		private Bitmap[]			mClouds;
		private int[]				mCloudsX;
		private int[]				mCloudsY;

		private int					mScreenSizeX;
		private int					mScreenSizeY;

		private final Paint 		paint = new Paint();

		private Resources 			mRes;
		private java.util.Random 	mRandom;

		StarGazerEngine()
		{
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			mRes = getResources();

			mRandom = new java.util.Random();

			//Setting patterns
			{
				//load stars
				mStars = new Bitmap[STARS];
				mStars[0] = BitmapFactory.decodeResource(mRes, R.drawable.star0);
				mStars[1] = BitmapFactory.decodeResource(mRes, R.drawable.star1);
				mStars[2] = BitmapFactory.decodeResource(mRes, R.drawable.star2);
				mStars[3] = BitmapFactory.decodeResource(mRes, R.drawable.star3);
				mStars[4] = BitmapFactory.decodeResource(mRes, R.drawable.star4);
				mStars[5] = BitmapFactory.decodeResource(mRes, R.drawable.star5);

				nStarIndex = new int [STARS_COUNT];
				mStarsX = new int [STARS_COUNT];
				mStarsY = new int [STARS_COUNT];
				mStarsAlpha = new int [STARS_COUNT];

				//load clouds
				mClouds = new Bitmap[CLOUDS];
				mClouds[0] = BitmapFactory.decodeResource(mRes, R.drawable.cloud0);
				mClouds[1] = BitmapFactory.decodeResource(mRes, R.drawable.cloud1);
				mClouds[2] = BitmapFactory.decodeResource(mRes, R.drawable.cloud2);
				mClouds[3] = BitmapFactory.decodeResource(mRes, R.drawable.cloud3);
				mClouds[4] = BitmapFactory.decodeResource(mRes, R.drawable.cloud4);
				mClouds[5] = BitmapFactory.decodeResource(mRes, R.drawable.cloud5);

				mCloudsX = new int [CLOUDS];
				mCloudsY = new int [CLOUDS];
			}
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			surfaceHolder.setFormat(android.graphics.PixelFormat.RGBA_8888);
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
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here.
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
			c.drawColor(0xff3989C2);

			drawStars(c);

			paint.setAlpha(255);

			drawClouds(c);

			c.restore();
		}

		void drawStars(Canvas c)
		{
			for(int i = 0; i < STARS_COUNT; i++)
			{
				paint.setAlpha(mStarsAlpha[i]);
				c.drawBitmap(mStars[nStarIndex[i]],
							mStarsX[i],
							mStarsY[i],
							paint);
				--mStarsAlpha[i];
				if(mStarsAlpha[i] < 5)
				{
					nStarIndex[i] = mRandom.nextInt(STARS);
					mStarsX[i] = mRandom.nextInt(mScreenSizeX);
					mStarsY[i] = mRandom.nextInt(mScreenSizeY);
					mStarsAlpha[i] = mRandom.nextInt(250) + 5;
				}
			}
		}

		void drawClouds(Canvas c)
		{
			for(int i = 0; i < CLOUDS; i++)
			{
				c.drawBitmap(mClouds[i],
							mCloudsX[i],
							mCloudsY[i],
							null);
				mCloudsX[i]++;
				if(mCloudsX[i] > mScreenSizeX)
				{
					mCloudsX[i] = -mClouds[i].getWidth() - mRandom.nextInt(50);
					mCloudsY[i] = mRandom.nextInt(mScreenSizeY);
				}
			}
		}

		void initFrameParams()
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);

			mScreenSizeX = metrics.widthPixels;
			mScreenSizeY = metrics.heightPixels;

			//set stars

			for(int i = 0; i < STARS_COUNT; i++)
			{
				nStarIndex[i] = mRandom.nextInt(STARS);
				mStarsX[i] = mRandom.nextInt(mScreenSizeX);
				mStarsY[i] = mRandom.nextInt(mScreenSizeY);
				mStarsAlpha[i] = mRandom.nextInt(250) + 5;
			}

			//set clouds
			for(int i = 0; i < CLOUDS; i++)
			{
				mCloudsX[i] = mRandom.nextInt(mScreenSizeX);
				mCloudsY[i] = mRandom.nextInt(mScreenSizeY);
			}
		}
	}
}