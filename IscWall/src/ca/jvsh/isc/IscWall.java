package ca.jvsh.isc;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class IscWall extends WallpaperService
{
	private final Handler		mHandler			= new Handler();

	public static final String	SHARED_PREFS_NAME	= "iscsettings";

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
		return new IscWallEngine();
	}

	class IscWallEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener
	{

		private float				mTouchX			= -1;
		private float				mTouchY			= -1;
		private final Paint			mPaint			= new Paint();
		private final Runnable		mDrawPattern	= new Runnable()
													{
														public void run()
														{
															drawFrame();
														}
													};
		private boolean				mVisible;
		private SharedPreferences	mPreferences;

		// screen parameters
		private final float			mWidth;
		private final float			mHeight;
		private final float			mDiagonal;

		private boolean				mVertical;

		private final Bitmap		mVerticalBitmap;
		private final Bitmap		mHorizontalBitmap;


		float						maxD			= 2.0f;
		float						power			= 0.1f;
		float						friction		= 0.7f;//0.08f;
		float						ratio			= 0.9f;// 0.1f;
		float						maxD2			= maxD * maxD;
		float						a				= power / maxD2;

		private Particle[]			particles;

		IscWallEngine()
		{
			mPaint.setDither(true);
			mPaint.setAntiAlias(true);

			mPreferences = IscWall.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPreferences.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPreferences, null);

			// create bitmaps
			mHeight = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getWidth();
			mWidth = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getHeight();

			mDiagonal = (int) Math.sqrt(mHeight * mHeight + mWidth * mWidth);

			if (mHeight > mWidth)
			{
				mVerticalBitmap = Bitmap.createBitmap((int) mWidth, (int) mHeight, Config.ARGB_8888);
				mPaint.setShader(new RadialGradient((int) mWidth, (int) mHeight, mDiagonal, 0xFF323850, 0xFF26B8C2, android.graphics.Shader.TileMode.CLAMP));
				(new Canvas(mVerticalBitmap)).drawCircle(mWidth, mHeight, mDiagonal, mPaint);

				mHorizontalBitmap = Bitmap.createBitmap((int) mHeight, (int) mWidth, Config.ARGB_8888);
				mPaint.setShader(new RadialGradient((int) mHeight, (int) mWidth, mDiagonal, 0xFF323850, 0xFF26B8C2, android.graphics.Shader.TileMode.CLAMP));
				(new Canvas(mHorizontalBitmap)).drawCircle(mHeight, mWidth, mDiagonal, mPaint);
			}
			else
			{
				mVerticalBitmap = Bitmap.createBitmap((int) mHeight, (int) mWidth, Config.ARGB_8888);
				mPaint.setShader(new RadialGradient((int) mHeight, (int) mWidth, mDiagonal, 0xFF323850, 0xFF26B8C2, android.graphics.Shader.TileMode.CLAMP));
				(new Canvas(mVerticalBitmap)).drawCircle(mHeight, mWidth, mDiagonal, mPaint);

				mHorizontalBitmap = Bitmap.createBitmap((int) mWidth, (int) mHeight, Config.ARGB_8888);
				mPaint.setShader(new RadialGradient((int) mWidth, (int) mHeight, mDiagonal, 0xFF323850, 0xFF26B8C2, android.graphics.Shader.TileMode.CLAMP));
				(new Canvas(mHorizontalBitmap)).drawCircle(mWidth, mHeight, mDiagonal, mPaint);
			}


			particles = new Particle[60];
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
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);

			mVertical = height > width;
			for (int i = 0; i < 60; i++)
				particles[i] = new Particle(getResources(), width, height);

			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
			holder.setFormat(PixelFormat.RGBA_8888);
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
				//mTouchX = -1;
				//mTouchY = -1;
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
					drawBackground(c);
					// drawParticles(c);
				}
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}
			catch (Exception ex)
			{

			}
			finally
			{
			}

			mHandler.removeCallbacks(mDrawPattern);
			if (mVisible)
			{
				mHandler.postDelayed(mDrawPattern, 1000 / 60);
			}
		}

		void drawBackground(Canvas c)
		{
			c.save();
			c.drawColor(0xff000000);
			if (mVertical)
				c.drawBitmap(mVerticalBitmap, 0, 0, mPaint);
			else
				c.drawBitmap(mHorizontalBitmap, 0, 0, mPaint);

			drawParticles(c);
			c.restore();
		}

		void drawParticles(Canvas c)
		{
			float cursorX = mTouchX - 88;//88;
			float cursorY = mTouchY - 77;//77;
			int l = particles.length;
			for (int i = 0; i < l; i++)
			{
				Particle p = particles[i];
				
				p.y -= p.mvy + p.mn;
				p.x += p.mvx;
				
				if(mVertical)
				{
					if (p.y < -20)
					{
						p.y = (float) Math.floor(Math.random() * (mHeight  - 200 + 1) + 200);
						p.x = (float) Math.random() * mWidth;
						p.opacity = 0;
					}
	
					if (p.x > mWidth)
						p.x = -50;
				}
				else
				{
					if (p.y < -20)
					{
						p.y = (float) Math.floor(Math.random() * (mWidth - 200 + 1) + 200);
						p.x = (float) Math.random() * mHeight;
						p.opacity = 0;
					}
	
					if (p.x > mHeight)
						p.x = -50;
				}

				if (p.y > cursorY - 50 && p.y < cursorY + 50 && p.x > cursorX - 50 && p.x < cursorX + 50 && p.isBeam == false)
				{
					float forceX = 0;
					float forceY = 0;
					float disX = cursorX - p.x;
					float disY = cursorY - p.y;
					int signX;
					int signY;
					if (disX > 0)
						signX = -1;
					else
						signX = 1;
					if (disY > 0)
						signY = -1;
					else
						signY = 1;
					float dis = disX * disX + disY * disY;

					if (dis < this.maxD2)
					{
						float force = -1 * a * dis / 10.0f * (float) Math.random();
						forceX = disX * disX / dis * signX * force / 5.0f;//10.0f;
						forceY = disY * disY / dis * signY * force / 5.0f;//10.0f;
					}
					p.spx = (p.spx * friction - disX * ratio + forceX) * 0.5f;
					p.spy = (p.spy * friction - disY * ratio + forceY) * 0.5f;
				}
				
				p.spx *= 0.95;
				p.spy *= 0.9;
				p.x += p.spx;
				p.y += p.spy * 0.8;
				if (p.y < 50)
					p.opacity = (float) Math.max(0.0f, p.opacity - 0.01f);
				else
					p.opacity = (float) Math.min(0.3f, p.opacity + 0.005f);

				//Log.i("Opacity", "Particle " + i + " opacity" + p.opacity);

				mPaint.setAlpha((int) (255.0f * p.opacity));
				c.drawBitmap(p.mParticle, p.x, p.y, mPaint);

				mPaint.setAlpha(255);
			}
		}
	}
}