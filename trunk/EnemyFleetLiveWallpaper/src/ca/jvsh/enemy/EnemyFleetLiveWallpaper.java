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
	//public static final String	SHARED_PREFS_NAME	= "enemyfleetsettings";

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
	{

		//private SharedPreferences	mPreferences;
		private static final int			SPEED = 15;
		private static final int			STROKE_WIDTH = 4;
		private static final int			ENEMIES = 24;
		private static final int			COLORS = 5;

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
		private Bitmap[] 			mEnemy;
		private Bitmap				mRotatedEnemy;
		private Matrix				mMatrix;

		private int[]				mEnemySizeX;
		private int[]				mEnemySizeY;

		private int					mScreenSizeX;
		private int					mScreenSizeY;

		private int					mCurrentEnemy;
		private int					mCurrentDirection;
		private int					mChange;
		private int					mCurrentShape;
		private int					mCurrentFit;
		private int					mCurrentWidth;
		private int					mCurrentOffset;
		private int					mShift;

		//coordinates
		private float				mPrevX;
		private float				mPrevY;

		private float				mX;
		private float				mY;

		private float 				mAngle;
		private float 				mRotateAngle;

		private int					mCurrentAmplitude;
		private int[]				mCurrentColor;
		
		private final Paint 		paint = new Paint();

		private Resources 			mRes;
		private java.util.Random 	mRandom;

		private int					mPoints;


		EnemyFleetEngine()
		{
			paint.setStrokeWidth(STROKE_WIDTH);
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);

			mRes = getResources();

			mRandom = new java.util.Random();
			mMatrix = new Matrix();
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
				mEnemySizeX = new int[ENEMIES];
				mEnemySizeY = new int[ENEMIES];
	
				for(int i = 0; i < ENEMIES; i++)
				{
					mEnemySizeX[i] = mEnemy[i].getWidth();
					mEnemySizeY[i] = mEnemy[i].getHeight();
				}
			}

			mCurrentEnemy = 0;
			mCurrentColor = new int[COLORS];
		}

		void setNextEnemy()
		{
			mCurrentEnemy ++;
			if(mCurrentEnemy >= ENEMIES)
				mCurrentEnemy = 0;

			mCurrentDirection = mRandom.nextInt(4);
			mCurrentDirection = 2;
			switch(mCurrentDirection)
			{
			case 0:
				mPoints = 0;
				mChange = SPEED;
				break;
			case 1:
				mPoints = mScreenSizeX;
				mChange -= SPEED;
				break;
			case 2:
				mPoints = 0;
				mChange = SPEED;
				break;
			case 3:
				mPoints = mScreenSizeY;
				mChange -= SPEED;
				break;
			}

			mCurrentShape = mRandom.nextInt(3);
			setCurrentColors();
			mCurrentAmplitude = mRandom.nextInt(80);
			setCurrentFit();
		}

		private void setCurrentFit()
		{
			switch(mCurrentDirection)
			{
			case 0://left
			case 1://right
				mCurrentWidth = mEnemySizeY[mCurrentEnemy] + 2 * mCurrentAmplitude;
				mCurrentFit = mScreenSizeY / mCurrentWidth;
				mCurrentOffset = (mScreenSizeY - mCurrentFit * mCurrentWidth - 4 * STROKE_WIDTH ) / 2 + mCurrentAmplitude;
				break;
			case 2://up
			case 3://down
				mCurrentWidth = mEnemySizeX[mCurrentEnemy] + 2 * mCurrentAmplitude;
				mCurrentFit = mScreenSizeX / mCurrentWidth;
				mCurrentOffset = (mScreenSizeX - mCurrentFit * mCurrentWidth + 4 * STROKE_WIDTH ) / 2 + mCurrentAmplitude;
				break;
			default:
				mCurrentFit = 1;
				break;
			}
			System.out.println("mCurrentWidth " + mCurrentWidth);
			System.out.println("mCurrentFit " + mCurrentFit);
			System.out.println("mCurrentOffset " + mCurrentOffset);
		}
		private void setCurrentColors()
		{
			for(int i = 0; i < COLORS; i++)
			{
				mCurrentColor[i] = Color.argb(0xff, mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
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

			drawStars();

			switch(mCurrentDirection)
			{
			case 0:
				drawHorizontal(c, true);
				break;
			case 1:
				drawHorizontal(c, false);
				break;
			case 2:
				drawVertical(c, true);
				break;
			case 3:
				drawVertical(c, true);
				break;
			}

			/*int enemy = 2;

			float amplitude = 80.0f;
			float x_prev =  0.0f;
			float y_prev =  0.0f;

			float x =  0.0f;
			float y =  0.0f;
			float angle = 0.0f;

			float offset = 128.0f;

			float rotateAngle = 0;

			while(angle < mPoints)
			{
				x_prev = x;
				y_prev = y;

				y += STROKE_WIDTH;
				angle += STROKE_WIDTH;
				x = FloatMath.sin(0.01745f * angle) * amplitude;

				paint.setColor(0xff8b00ff);
				c.drawLine(offset - x_prev, y_prev, offset - x, y, paint);
				paint.setColor(0xffff00ff);
				c.drawLine( offset - x_prev + STROKE_WIDTH,y_prev, offset - x + STROKE_WIDTH, y, paint);
				paint.setColor(0xff0f2efd);
				c.drawLine(offset - x_prev - STROKE_WIDTH, y_prev, offset - x - STROKE_WIDTH, y, paint);

				if(angle >= mPoints - 2 *STROKE_WIDTH)
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
					Bitmap rotatedBMP = Bitmap.createBitmap(mEnemy[enemy], 0, 0, mEnemySizeX[enemy], mEnemySizeY[enemy], mtx, true);

					paint.setAlpha(255 - (int)(mPoints - angle) * 30);
					c.drawBitmap(rotatedBMP, offset - x - rotatedBMP.getWidth() / 2, y - rotatedBMP.getHeight() / 2, paint);

				}
			}

			mPoints += STROKE_WIDTH;
			if(mPoints > mScreenSizeY)
				mPoints = 0;

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
				Bitmap rotatedBMP = Bitmap.createBitmap(mEnemy[enemy], 0, 0, mEnemySizeX[enemy], mEnemySizeY[enemy], mtx, true);

				//c.drawBitmap(rotatedBMP, 100-(rotatedBMP.getWidth() - mEnemySizeX[enemy])/2, 100 - (rotatedBMP.getHeight() - mEnemySizeY[enemy])/2, paint);
				c.drawBitmap(rotatedBMP, x - rotatedBMP.getWidth() / 2, offset - y - rotatedBMP.getHeight() / 2, paint);

			}

			mPoints+=strokeWidth;
			if(mPoints > mScreenSizeX)
				mPoints = 0;*/
			c.restore();
		}

		void drawStars()
		{
			
		}
		
		void drawHorizontal(Canvas c, boolean left)
		{
			

			if(left)
			{
				mPoints += STROKE_WIDTH;
				if(mPoints > mScreenSizeX)
				{
					setNextEnemy();
				}
			}
			else
			{
				mPoints -= STROKE_WIDTH;
				if(mPoints < 0)
				{
					setNextEnemy();
				}
			}
		}

		void drawVertical(Canvas c, boolean down)
		{
			if(down)
			{
				mX = mY =  0.0f;
			}
			else
			{
				mX =  0.0f;
				mY =  mScreenSizeY;
			}

			mRotateAngle = mAngle = 0.0f;

			while(true)
			{
				if(down)
				{
					if(mAngle > mPoints)
						break;
				}
				else
				{
					if(mAngle < mPoints)
						break;
				}

				mPrevX = mX;
				mPrevY = mY;

				mY += mChange;

				mAngle += SPEED;

				mX = FloatMath.sin(0.01745f * mAngle) * mCurrentAmplitude;

				for(int j = 0; j < COLORS; j++ )
				{
					paint.setColor(mCurrentColor[j]);
					for(int k = 0; k < mCurrentFit; k++)
					{
						mShift =  mCurrentOffset + k * mCurrentWidth + j * STROKE_WIDTH * 2;
						c.drawLine( mShift + mPrevX,
									mPrevY,
									mShift + mX,
									mY,
									paint);
					}
				}
			}

			mRotateAngle = (float) Math.asin( (mY-mPrevY) / FloatMath.sqrt( (mX-mPrevX)* (mX-mPrevX) + (mY-mPrevY)* (mY-mPrevY) )) * 57.29f;

			if(mX-mPrevX < 0)
				mRotateAngle = - mRotateAngle + 180;

			mMatrix.setRotate(mRotateAngle);

			mRotatedEnemy = Bitmap.createBitmap(mEnemy[mCurrentEnemy],
												0, 0,
												mEnemySizeX[mCurrentEnemy],
												mEnemySizeY[mCurrentEnemy],
												mMatrix, true);

			for(int k = 0; k < mCurrentFit; k++)
			{
				c.drawBitmap(mRotatedEnemy,
							mCurrentOffset + k * mCurrentWidth + 4 * STROKE_WIDTH + mX - mRotatedEnemy.getWidth() / 2,
							mY - mRotatedEnemy.getHeight() / 2,
							null);
			}
			
			if(down)
			{
				mPoints += SPEED;
				if(mPoints > mScreenSizeY)
				{
					setNextEnemy();
				}
			}
			else
			{
				mPoints -= SPEED;
				if(mPoints < 0)
				{
					setNextEnemy();
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

			setNextEnemy();
		}
	}
}