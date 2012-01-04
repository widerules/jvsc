package ca.jvsh.clockclock;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public class ClockClockView
{
	private static final int	HORIZONTAL_DIALS = 2;
	private static final int	VERTICAL_DIALS = 3;
	private static final int 	DIAL_ARROWS = 2;
	
	private static final int	SEGMENTS = 2;
	private static final int	DIGIT_PER_SEGMENT = 2;
	
	private static final int	CLOCK_DIGITS = SEGMENTS * DIGIT_PER_SEGMENT;
	
	private static final int	COORDINATES = 2;
	
	private int					mHeight;
	private int					mWidth;
	private float				mDensity;

	private int					mWidgetId;

	private final Paint			mPaint					= new Paint();

	Bitmap						mMainBitmap;
	Canvas						mCanvasMain;

	Bitmap						mCoverBitmap;

	Bitmap						mArrowsBitmap;
	Canvas						mCanvasArrows;
	
	float						mHourHandLength			= 4;
	float						mMinuteHandLength			= 5;
	
	


	int[]						mOldDigits				= new int[SEGMENTS];
	int[]						mCurrentDigits			= new int[SEGMENTS];

	float[][][][]				mTargetAngles			= new float[CLOCK_DIGITS][VERTICAL_DIALS][HORIZONTAL_DIALS][DIAL_ARROWS];
	float[][][][]				mCurrentAngles			= new float[CLOCK_DIGITS][VERTICAL_DIALS][HORIZONTAL_DIALS][DIAL_ARROWS];
	float[][][][]				mTurnAngles				= new float[CLOCK_DIGITS][VERTICAL_DIALS][HORIZONTAL_DIALS][DIAL_ARROWS];

	float[][][][]				mDialCenterCoordinates	= new float[CLOCK_DIGITS][VERTICAL_DIALS][HORIZONTAL_DIALS][COORDINATES];
	
	private final float			mOffsetX				= 80;
	private final float			mOffsetY				= 20;
	private final float			mDialSize				= 40;
	private final float			mDialSizeHalf			= mDialSize/2;
	private final float			mDigitOffsetX			= 10;


	Time						mCurrentTime			= new Time();

	int							mHandsColor				= 0xFF000000;
	boolean						mReadSettings = false;

	public ClockClockView(Context context, int widgetId)
	{
		DisplayMetrics metrics = ClockClockWidgetApp.getMetrics();

		mDensity = metrics.density;
		mWidth = (int) (370 * metrics.density);
		mHeight = (int) (100 * metrics.density);

		mWidgetId = widgetId;

		//set Paint variables
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);

		mMainBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvasMain = new Canvas(mMainBitmap);

		mArrowsBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mCanvasArrows = new Canvas(mArrowsBitmap);

	
		mCoverBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		Canvas canvasCoverBitmap = new Canvas(mCoverBitmap);

		mPaint.setColor(0xFF0C141F);


		//set arrow 
		for (int i = 0; i < CLOCK_DIGITS; i++)
		{
			for (int j = 0; j < VERTICAL_DIALS; j++)
			{
				for (int k = 0; k < HORIZONTAL_DIALS; k++)
				{

					mDialCenterCoordinates[i][j][k][0] = mOffsetX + k * mDialSize;
					mDialCenterCoordinates[i][j][k][0] += mDigitOffsetX * (i % DIGIT_PER_SEGMENT );
					mDialCenterCoordinates[i][j][k][1] = mOffsetY + j * mDialSize;
	
					canvasCoverBitmap.drawRect(mDialCenterCoordinates[i][j][k][0]-mDialSizeHalf, mDialCenterCoordinates[i][j][k][1]-mDialSizeHalf,
							mDialCenterCoordinates[i][j][k][0]+mDialSizeHalf, mDialCenterCoordinates[i][j][k][1]+mDialSizeHalf, mPaint);

					
					for (int l = 0; l < COORDINATES; l++)
					{
						mDialCenterCoordinates[i][j][k][l] *= mDensity;
					}
				}
			}
		}
	}

	public Context getContext()
	{
		return (ClockClockWidgetApp.getApplication());
	}



	public void Redraw(AppWidgetManager appWidgetManager)
	{
		if (!mReadSettings)
		{
			SharedPreferences prefs = getContext().getSharedPreferences("prefs", 0);
			mHandsColor = prefs.getInt("color" + mWidgetId, 0xff6FC3DF);
			mReadSettings = true;
			
			mPaint.setStrokeWidth(4 * mDensity);
			mPaint.setColor(mHandsColor);

		}

		RemoteViews rviews = new RemoteViews(getContext().getPackageName(), R.layout.clockclock_widget);

		mCanvasMain.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
		mCanvasArrows.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);

		mCanvasMain.drawBitmap(mCoverBitmap, 0, 0, mPaint);

		drawArrowHands();

		mCanvasMain.drawBitmap(mArrowsBitmap, 0, 0, mPaint);

		rviews.setImageViewBitmap(R.id.block, mMainBitmap);

		appWidgetManager.updateAppWidget(mWidgetId, rviews);
	}

	//drawing functions
	private static void setAngles(int digit, float[][][] handlesAngles)
	{
		switch (digit)
		{
			case 0:
			{
				//[VERTICAL_DIALS][HORIZONTAL_DIALS][DIAL_ARROWS]
				//handlesAngles[0][0][1]
				break;
			}
			case 1:
			{
			

				break;
			}
			case 2:
			{
				
				break;
			}
			case 3:
			{
				
				break;
			}
			case 4:
			{
				
				break;
			}
			case 5:
			{
				
				break;
			}
			case 6:
			{
				
				break;
			}
			case 7:
			{
				
				break;
			}
			case 8:
			{
				
				break;
			}
			case 9:
			{
				
				break;
			}
		}

	}

	private void drawArrowHands()
	{

	/*	mCurrentTime.setToNow();

		mCurrentDigits[0] = mCurrentTime.hour;
		mCurrentDigits[1] = mCurrentTime.minute;
				

		if (mCurrentDigits[0] != mOldDigits[0] || mCurrentDigits[1] != mOldDigits[1] || mCurrentDigits[2] != mOldDigits[2])
		{
			mOldDigits[0] = mCurrentDigits[0];
			mOldDigits[1] = mCurrentDigits[1];
			mOldDigits[2] = mCurrentDigits[2];

			setAngles(mCurrentDigits[0], mTargetAngles[0], mTargetAngles[1], 0);
			setAngles(mCurrentDigits[1], mTargetAngles[2], mTargetAngles[3], mCurrentDigits[0] % 10);
			setAngles(mCurrentDigits[2], mTargetAngles[4], mTargetAngles[5], mCurrentDigits[1] % 10);

			for (int j = 0; j < 6; j++)
			{
				for (int i = 0; i < 17; i++)
				{
					if (Math.abs(mCurrentAngles[j][i] - mTargetAngles[j][i]) > 5)
					{
						mTurnAngles[j][i] = (mTargetAngles[j][i] - mCurrentAngles[j][i]) / 3.0f;
					}
				}
			}
		}

		for (int j = 0; j < 6; j++)
		{
			for (int i = 0; i < 17; i++)
			{
				if (Math.abs(mCurrentAngles[j][i] - mTargetAngles[j][i]) > 5)
				{
					mCurrentAngles[j][i] += mTurnAngles[j][i];
				}
				else
				{
					mCurrentAngles[j][i] = mTargetAngles[j][i];
				}

			}
		}

		{
			mLaserPath.reset();
			//propagate laser beam
			{
				mLaserX = 5 * scale;
				mLaserY = (189 + mOffsetY) * digitscale;

				mLaserPath.moveTo(mLaserX, mLaserY);
				mLaserRotation = 0;

				mDigit = -1;
				mMirror = -1;
				//tens
				while (mLaserX >= (5 * scale) && mLaserX <= ((mWidth - 5) * scale) && mLaserY > (5 * scale) && mLaserY < ((mHeight - 5) * scale))
				{

					mLaserX += (float) (Math.cos(mLaserRotation / 180.0f * Math.PI));
					mLaserY += (float) (Math.sin(mLaserRotation / 180.0f * Math.PI));
					mMirrorFound = false;

					for (int j = 0; j < 6; j++)
					{
						for (int i = 0; i < 17; i++)
						{
							if (Math.abs(mMirrorCoordinates[j][0][i] - mLaserX) < eps && Math.abs(mMirrorCoordinates[j][1][i] - mLaserY) < eps)
							{
								if (mMirror != i || mDigit != j)
								{
									mMirror = i;
									mDigit = j;
									mMirrorFound = true;
									mLaserX = mMirrorCoordinates[j][0][i];
									mLaserY = mMirrorCoordinates[j][1][i];

									if (mTimeFormat == 2)
									{
										if (mCurrentAngles[j][i] == 0 || mCurrentAngles[j][i] == 90 || mCurrentAngles[j][i] == 45 || mCurrentAngles[j][i] == -45 || mCurrentAngles[j][i] == 22.5 || mCurrentAngles[j][i] == -22.5 || mCurrentAngles[j][i] == -90 || mCurrentAngles[j][i] == -67.5 || mCurrentAngles[j][i] == 67.5)
										{
											mLaserPath.lineTo(mLaserX, mLaserY);
											mLaserRotation = 2.0f * mCurrentAngles[j][i] - mLaserRotation;
										}
									}
									else
									{
										mLaserPath.lineTo(mLaserX, mLaserY);
										mLaserRotation = 2.0f * mTargetAngles[j][i] - mLaserRotation;
									}
									break;
								}
							}
						}

						if (mMirrorFound)
							break;
					}
				}
				mLaserPath.lineTo(mLaserX, mLaserY);
			}

			for (int j = 0; j < 6; j++)
			{
				for (int i = 0; i < 17; i++)
				{
					if (Math.abs(mCurrentAngles[j][i] - mTargetAngles[j][i]) > 5)
					{
						mCurrentAngles[j][i] += mTurnAngles[j][i];
					}
					else
					{
						mCurrentAngles[j][i] = mTargetAngles[j][i];
					}

					drawPixelMirror(mMirrorCoordinates[j][0][i], mMirrorCoordinates[j][1][i], mCurrentAngles[j][i]);

				}
			}

			mPaint.setColor(mLaserColor);
			mPaint.setStyle(Paint.Style.STROKE);
			mCanvasLaser.drawPath(mLaserPath, mPaint);
			mPaint.setStyle(Paint.Style.FILL);

			mPaintBlur.setColor(mLaserColor & 0x00FFFFFF + 0x99000000);
			mCanvasLaser.drawPath(mLaserPath, mPaintBlur);
		}*/
	}

	private void drawClockHand(float centerX, float centerY, float angleDeg, float handLength)
	{

		mCanvasArrows.drawLine(centerX, centerY,
				centerX + handLength * (float) Math.cos(angleDeg * Math.PI / 180.0f)* mDensity, 
				centerY + handLength * (float) Math.sin(angleDeg * Math.PI / 180.0f)* mDensity, mPaint);
	}
}
