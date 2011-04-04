package ca.jvsh.enemy;

import ca.jvsh.enemy.R;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class EnemyFleetLiveWallpaper extends WallpaperService 
{
	public static final String	SHARED_PREFS_NAME	= "enemyfleetsettings";


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
		return new EnemyFleetEngine();
	}

	class EnemyFleetEngine extends Engine
							 implements SharedPreferences.OnSharedPreferenceChangeListener
	{

		private final Handler		mHandler		=   new Handler();

		private final Runnable		mDrawPattern	=   new Runnable()
														{
															public void run()
															{
																drawFrame();
															}
														};
		private boolean				mVisible;
		private SharedPreferences	mPreferences;

		//private final int			PATTERNS = 6;

		//private final int			DIRECTION_CHANGE_COUNTER = 1500;
		//private final int			LOGO_SHOW_COUNTER = 1500;
		//private final int			PATTERN_CHANGE_COUNTER_OFTEN = 2000;
		//private final int			PATTERN_CHANGE_COUNTER_RARE = 4000;

		//! patterns that we drawing
		//private Bitmap[] 			mPattern;

		//private int[]				tile_size_x;
		//private int[]				tile_size_y;

		private int					screen_size_x;
		private int					screen_size_y;

		//private float				tile_shift_x;
		//private float				tile_shift_y;
		//private float				tile_shift_x_next;
		//private float				tile_shift_y_next;
		//private float				movement_speed_x;
		//private float				movement_speed_y;

		//private int[]				fit_x;
		//private int[]				fit_y;

		//private int[]				remain_x;
		//private int[]				remain_y;

		//private int					mCurrentPattern;
		//private int					mNextPattern;

		private final Paint 		paint = new Paint();
		//private int 				mPreviousOffset;
		private Resources 			mRes;
		private java.util.Random 	mRandom;

		private int					mPoints;
		//! flags that show available patterns
		//private int					mSpeed;

		//! flag that show if we switching pattern on home screen switch
		private boolean				mHomeScreenSwitch;

		//private boolean				mChangeRandomly;

		//private boolean				mChangingScreen;

		//counters that helps to gently switch between patterns and logos

		//!counter that show that we need to change direction if we move randomly
		//private int					mChangeRandomDirectionCounter = 0;

		//!counter that show that we have to change pattern
		//private int					mPatternChangeCounter = 0;

		//!counter that show that we have to show logo
		//private int					mShowLogoCounter = 0;

		//private int					mTransparency;

		EnemyFleetEngine()
		{
			mRes = getResources();

			mRandom = new java.util.Random();

			//Setting patterns
			/*{
				mPattern = new Bitmap[PATTERNS];
				mAvailablePatterns = new boolean[PATTERNS];

				//load patterns
				mPattern[0] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_aloha_turkey);
				mPattern[1] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_haunted_2_regal);
				mPattern[2] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_hollyhock);
				mPattern[3] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_kiwi);
				mPattern[4] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_simple_paisley);
				mPattern[5] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_twirll_2);

				//set size
				tile_size_x = new int[PATTERNS];
				tile_size_y = new int[PATTERNS];
	
				for(int i = 0; i < PATTERNS; i++)
				{
					tile_size_x[i] = mPattern[i].getWidth();
					tile_size_y[i] = mPattern[i].getHeight();

					mAvailablePatterns[i] = true;
				}
	
				fit_x =  new int[PATTERNS];
				fit_y =  new int[PATTERNS];
				remain_x =  new int[PATTERNS];
				remain_y =  new int[PATTERNS];
			}

			//get logo and its size
			{
				mLogo = new Bitmap[2];
				logo_size_x = new int[2];
				logo_size_y = new int[2];

				mLogo[0] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_logo_black);
				logo_size_x[0] = mLogo[0].getWidth();
				logo_size_y[0] = mLogo[0].getHeight();

				mLogo[1] = BitmapFactory.decodeResource(mRes, R.drawable.dinpattern_logo_white);
				logo_size_x[1] = mLogo[1].getWidth();
				logo_size_y[1] = mLogo[1].getHeight();
			}

			tile_shift_x = 0;
			tile_shift_y = 0;
			tile_shift_x_next = 0;
			tile_shift_y_next = 0;

			mPreviousOffset = 0;*/

			mPreferences = EnemyFleetLiveWallpaper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
												String key)
		{
			// if we are changing pattern on home screen change
			mHomeScreenSwitch = prefs.getBoolean("homescreen_change", true);
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
			if(mHomeScreenSwitch)
			{
				int offset = (int)(xOffset * 100);
				/*if(offset%25 == 0 && offset != mPreviousOffset)
				{
					mPreviousOffset = offset;
	
					mChangingScreen = true;
					mTransparency = 255;
					//try several times until new pattern is different from current pattern
					for(int j = 0; j < PATTERNS; j++)
					{
						mNextPattern = GetPatternId(mCurrentPattern);
						if(mNextPattern != mCurrentPattern)
							break;
					}
					if(mNextPattern == mCurrentPattern)
					{
						mChangingScreen = false;
						mTransparency = 0;
					}
				}*/
			}

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
			c.drawColor(0xff000000);

			int x=0;
			paint.setStrokeWidth(4);

			for(int angle =0;angle<=mPoints;angle++)
			{
				Float y =(FloatMath.sin((float) (angle*0.01745))*80);
				paint.setColor(0xff8b00ff);
				c.drawPoint(x,128-y,paint);
				paint.setColor(0xffff00ff);
				c.drawPoint(x,133-y,paint);
				paint.setColor(0xff0f2efd);
				c.drawPoint(x,138-y,paint);
				x++;
			}

			mPoints+=3;
			if(mPoints > 480)
				mPoints = 0;
			c.restore();
		}

		void initFrameParams()
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);

			screen_size_x = metrics.widthPixels;
			screen_size_y = metrics.heightPixels;
		}
	}
}