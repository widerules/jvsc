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
import android.graphics.Matrix;

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

		private final int			ENEMIES = 24;

		//private final int			DIRECTION_CHANGE_COUNTER = 1500;
		//private final int			LOGO_SHOW_COUNTER = 1500;
		//private final int			PATTERN_CHANGE_COUNTER_OFTEN = 2000;
		//private final int			PATTERN_CHANGE_COUNTER_RARE = 4000;

		//! patterns that we drawing
		private Bitmap[] 			mEnemy;

		private int[]				enemy_size_x;
		private int[]				enemy_size_y;

		private int					screen_size_x;
		private int					screen_size_y;

		//private float				movement_speed_x;
		//private float				movement_speed_y;

		//private int					mCurrentPattern;
		//private int					mNextPattern;

		private final Paint 		paint = new Paint();

		private int strokeWidth = 4;
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

			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			mRes = getResources();

			mRandom = new java.util.Random();

			//Setting patterns
			{
				mEnemy = new Bitmap[ENEMIES];

				//load patterns
				mEnemy[0] = BitmapFactory.decodeResource(mRes, R.drawable.enemy0);
				mEnemy[1] = BitmapFactory.decodeResource(mRes, R.drawable.enemy1);
				mEnemy[2] = BitmapFactory.decodeResource(mRes, R.drawable.enemy2);
				mEnemy[3] = BitmapFactory.decodeResource(mRes, R.drawable.enemy3);
				mEnemy[4] = BitmapFactory.decodeResource(mRes, R.drawable.enemy4);
				mEnemy[5] = BitmapFactory.decodeResource(mRes, R.drawable.enemy5);
				mEnemy[6] = BitmapFactory.decodeResource(mRes, R.drawable.enemy6);
				mEnemy[7] = BitmapFactory.decodeResource(mRes, R.drawable.enemy7);
				mEnemy[8] = BitmapFactory.decodeResource(mRes, R.drawable.enemy8);
				mEnemy[9] = BitmapFactory.decodeResource(mRes, R.drawable.enemy9);
				mEnemy[10] = BitmapFactory.decodeResource(mRes, R.drawable.enemy10);
				mEnemy[11] = BitmapFactory.decodeResource(mRes, R.drawable.enemy11);
				mEnemy[12] = BitmapFactory.decodeResource(mRes, R.drawable.enemy12);
				mEnemy[13] = BitmapFactory.decodeResource(mRes, R.drawable.enemy13);
				mEnemy[14] = BitmapFactory.decodeResource(mRes, R.drawable.enemy14);
				mEnemy[15] = BitmapFactory.decodeResource(mRes, R.drawable.enemy15);
				mEnemy[16] = BitmapFactory.decodeResource(mRes, R.drawable.enemy16);
				mEnemy[17] = BitmapFactory.decodeResource(mRes, R.drawable.enemy17);
				mEnemy[18] = BitmapFactory.decodeResource(mRes, R.drawable.enemy18);
				mEnemy[19] = BitmapFactory.decodeResource(mRes, R.drawable.enemy19);
				mEnemy[20] = BitmapFactory.decodeResource(mRes, R.drawable.enemy20);
				mEnemy[21] = BitmapFactory.decodeResource(mRes, R.drawable.enemy21);
				mEnemy[22] = BitmapFactory.decodeResource(mRes, R.drawable.enemy22);
				mEnemy[23] = BitmapFactory.decodeResource(mRes, R.drawable.enemy23);

				//set size
				enemy_size_x = new int[ENEMIES];
				enemy_size_y = new int[ENEMIES];
	
				for(int i = 0; i < ENEMIES; i++)
				{
					enemy_size_x[i] = mEnemy[i].getWidth();
					enemy_size_y[i] = mEnemy[i].getHeight();
				}
			}

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
			c.drawColor(0xff3989C2);

			int enemy = 2;

			float amplitude = 80.0f;
			float x_prev =  0.0f;
			float y_prev =  0.0f;

			float x =  0.0f;
			float y =  0.0f;
			float angle = 0.0f;

			float offset = 128.0f;

			paint.setStrokeWidth(strokeWidth);
			float rotateAngle = 0;

			while(angle <= mPoints)
			{
				x_prev = x;
				y_prev = y;

				y += strokeWidth;
				angle += strokeWidth;
				x = FloatMath.sin(0.01745f * angle) * amplitude;

				paint.setColor(0xff8b00ff);
				c.drawLine(offset - x_prev, y_prev, offset - x, y, paint);
				paint.setColor(0xffff00ff);
				c.drawLine( offset - x_prev + strokeWidth * 2,y_prev, offset - x + strokeWidth * 2, y, paint);
				paint.setColor(0xff0f2efd);
				c.drawLine(offset - x_prev - strokeWidth * 2, y_prev, offset - x - strokeWidth * 2, y, paint);

				if(angle >= mPoints - 2 *strokeWidth)
				{
					double length = FloatMath.sqrt( (x-x_prev)* (x-x_prev) + (y-y_prev)* (y-y_prev) );
					System.out.println("length" + length);

					rotateAngle = (float) Math.asin( (y-y_prev) / length ) * 57.29f;

					if(x-x_prev > 0)
						rotateAngle = - rotateAngle - 270;
					else
						rotateAngle = rotateAngle + 270;

					System.out.println("RotateAngle" + rotateAngle);
					// Setting post rotate to 90
					Matrix mtx = new Matrix();
					mtx.postRotate(rotateAngle);

					// Rotating Bitmap
					Bitmap rotatedBMP = Bitmap.createBitmap(mEnemy[enemy], 0, 0, enemy_size_x[enemy], enemy_size_y[enemy], mtx, true);

					int alpha =  255 - (int)(mPoints - angle) * 40;
					System.out.println("Alpha" + alpha);
					paint.setAlpha(alpha);
					c.drawBitmap(rotatedBMP, offset - x - rotatedBMP.getWidth() / 2, y - rotatedBMP.getHeight() / 2, paint);

				}
			}

			{
	
			}

			mPoints += strokeWidth;
			if(mPoints > screen_size_y)
				mPoints = 0;

			/*int enemy = 2;

			float amplitude = 80.0f;
			float x_prev =  0.0f;
			float y_prev =  0.0f;

			float x =  0.0f;
			float y =  0.0f;
			float angle = 0.0f;

			float offset = 128.0f;

			paint.setStrokeWidth(strokeWidth);
			float rotateAngle = 0;

			while(angle <= mPoints)
			{
				x_prev = x;
				y_prev = y;

				x += strokeWidth;
				angle += strokeWidth;
				y = FloatMath.sin(0.01745f * angle) * amplitude;

				paint.setColor(0xff8b00ff);
				c.drawLine(x_prev, offset - y_prev, x, offset - y, paint);
				paint.setColor(0xffff00ff);
				c.drawLine(x_prev, offset - y_prev + strokeWidth * 2, x, offset - y + strokeWidth * 2, paint);
				paint.setColor(0xff0f2efd);
				c.drawLine(x_prev, offset - y_prev - strokeWidth * 2, x, offset - y - strokeWidth * 2, paint);

			}

			{
				double length = FloatMath.sqrt( (x-x_prev)* (x-x_prev) + (y-y_prev)* (y-y_prev) );

				rotateAngle = (float) Math.asin( (x-x_prev) / length ) * 57.29f;
				if(y-y_prev > 0)
					rotateAngle -=180;
				else
					rotateAngle = -rotateAngle;

				System.out.println("Rotate angle" + rotateAngle);
				// Setting post rotate to 90
				Matrix mtx = new Matrix();
				mtx.postRotate(rotateAngle);

				// Rotating Bitmap
				Bitmap rotatedBMP = Bitmap.createBitmap(mEnemy[enemy], 0, 0, enemy_size_x[enemy], enemy_size_y[enemy], mtx, true);

				//c.drawBitmap(rotatedBMP, 100-(rotatedBMP.getWidth() - enemy_size_x[enemy])/2, 100 - (rotatedBMP.getHeight() - enemy_size_y[enemy])/2, paint);
				c.drawBitmap(rotatedBMP, x - rotatedBMP.getWidth() / 2, offset - y - rotatedBMP.getHeight() / 2, paint);

			}

			mPoints+=strokeWidth;
			if(mPoints > screen_size_x)
				mPoints = 0;*/
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