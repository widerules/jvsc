package ca.jvsh.reflectius;

import java.util.Calendar;
import java.util.Random;

import ca.jvsh.reflectius.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public class ReflectiusView
{
	public static String		INTENT_ON_CLICK_FORMAT	= "ca.jvsh.reflectius.id.%d.click";
	private static final int	REFRESH_RATE			= 60;
	private int					cheight;
	private int					cwidth;
	private float				density;

	private long				lastRedrawMillis		= 0;
	private int					mWidgetId;

	private final Paint			mPaint					= new Paint();
	private final Paint			_paintBlur				= new Paint();

	Bitmap						bitmap;
	Bitmap						coverBitmap;
	Bitmap						mirrorsBitmap;
	Bitmap						bitmap1;
	Bitmap						bitmap2;

	Canvas						canvasBitmap;
	Canvas						canvasMirrors;
	Canvas						canvasCoverBitmap;
	Canvas						canvasBitmap1;
	Canvas						canvasBitmap2;

	Bitmap						bitmapLaser;
	Canvas						canvasBitmapLaser;
	Bitmap						bitmapLaser1;
	Canvas						canvasBitmapLaser1;

	Random						random					= new Random();

	private static float		scale;
	private static float		eps;
	Path						mCoverPath;
	Path						mCoverReflectionPath;

	public ReflectiusView(Context context, int widgetId)
	{
		DisplayMetrics metrics = ReflectiusWidgetApp.getMetrics();

		density = metrics.density;
		cwidth = (int) (400 * metrics.density);
		cheight = (int) (200 * metrics.density);
		scale = (400 * metrics.density) / 663.0f;

		mWidgetId = widgetId;
		setState();

		mCoverPath = new Path();
		mCoverPath.moveTo(0.2f * scale, 139 * scale);
		mCoverPath.lineTo(367 * scale, 8 * scale);
		mCoverPath.lineTo(607 * scale, 60 * scale);
		mCoverPath.lineTo(664 * scale, 132 * scale);
		mCoverPath.lineTo(571 * scale, 322 * scale);
		mCoverPath.lineTo(385 * scale, 291 * scale);
		mCoverPath.lineTo(107 * scale, 321 * scale);
		mCoverPath.close();

		mCoverReflectionPath = new Path();
		mCoverReflectionPath.moveTo(57 * scale, 239 * scale);

		mCoverReflectionPath.lineTo(614 * scale, 239 * scale);

		mCoverReflectionPath.lineTo(571 * scale, 322 * scale);
		mCoverReflectionPath.lineTo(385 * scale, 291 * scale);
		mCoverReflectionPath.lineTo(107 * scale, 321 * scale);
		mCoverReflectionPath.close();

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);

		_paintBlur.set(mPaint);
		_paintBlur.setStyle(Paint.Style.STROKE);
		_paintBlur.setColor(0x99FF0000);
		_paintBlur.setStrokeWidth(45f * scale);
		_paintBlur.setMaskFilter(new BlurMaskFilter(45 * scale, BlurMaskFilter.Blur.NORMAL));

		coverBitmap = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasCoverBitmap = new Canvas(coverBitmap);
		drawCover();

		bitmapLaser1 = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmapLaser1 = new Canvas(bitmapLaser1);
		drawLaserCover();

		bitmap1 = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmap1 = new Canvas(bitmap1);
		drawCoverGradient();

		bitmap2 = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmap2 = new Canvas(bitmap1);
		drawCoverReflection();

		eps = 1.335f * scale;
		setLaser();
	}

	public Context getContext()
	{
		return (ReflectiusWidgetApp.getApplication());
	}

	public float getDensity()
	{
		return density;
	}

	public int getmWidgetId()
	{
		return mWidgetId;
	}

	public void OnClick()
	{

	}

	public void Redraw(Context context)
	{
		RemoteViews rviews = new RemoteViews(context.getPackageName(), R.layout.reflectius_widget);

		bitmap = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmap = new Canvas(bitmap);
		canvasBitmap.drawBitmap(coverBitmap, 0, 0, mPaint);

		bitmapLaser = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmapLaser = new Canvas(bitmapLaser);

		mirrorsBitmap = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasMirrors = new Canvas(mirrorsBitmap);
		drawMirrorsLaser();

		canvasBitmap.drawBitmap(bitmapLaser, 0, 0, mPaint);
		canvasBitmap.drawBitmap(mirrorsBitmap, 0, 0, mPaint);

		canvasBitmap.drawBitmap(bitmapLaser1, 0, 0, mPaint);
		canvasBitmap.drawBitmap(bitmap1, 0, 0, mPaint);
		canvasBitmap.drawBitmap(bitmap2, 0, 0, mPaint);

		rviews.setImageViewBitmap(R.id.block, bitmap);

		updateClickIntent(rviews);
		AppWidgetManager.getInstance(context).updateAppWidget(mWidgetId, rviews);
		lastRedrawMillis = SystemClock.uptimeMillis();

		scheduleRedraw();
	}

	private void scheduleRedraw()
	{
		long nextRedraw = lastRedrawMillis + REFRESH_RATE;
		nextRedraw = nextRedraw > SystemClock.uptimeMillis() ? nextRedraw : SystemClock.uptimeMillis() + REFRESH_RATE;
		scheduleRedrawAt(nextRedraw);
	}

	private void scheduleRedrawAt(long timeMillis)
	{
		(new Handler()).postAtTime(new Runnable()
		{
			public void run()
			{
				Redraw(ReflectiusWidgetApp.getApplication());
			}
		}, timeMillis);
	}

	public void setState()
	{
		scheduleRedraw();
	}

	private void updateClickIntent(RemoteViews rviews)
	{
		Intent intent = new Intent(String.format(INTENT_ON_CLICK_FORMAT, mWidgetId));
		intent.setClass(getContext(), ReflectiusWidgetProvider.class);
		intent.putExtra("widgetId", mWidgetId);
		PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		rviews.setOnClickPendingIntent(R.id.widget, pi);
	}

	//drawing functions
	private void drawCover()
	{
		mPaint.setColor(0xFF0D091D);

		canvasCoverBitmap.drawPath(mCoverPath, mPaint);
	}

	private void drawCoverGradient()
	{
		int[] colors = { 0x29A0B5EA, 0x00A0B5EA };
		float[] positions = { 0.0627f, 0.8f };
		LinearGradient gradient = new LinearGradient(0, 0, 0, 600 * scale, colors, positions, android.graphics.Shader.TileMode.CLAMP);

		mPaint.setColor(0xFFFFFFFF);
		mPaint.setShader(gradient);

		canvasBitmap1.drawPath(mCoverPath, mPaint);
		mPaint.setShader(null);
	}

	private void drawCoverReflection()
	{
		int[] colors = { 0x33A6B3EB, 0x10A6B3EB, 0x33A6B3EB, 0x00A6B3EB };
		float[] positions = { 0.0627f, 0.274f, 0.667f, 0.8f };
		LinearGradient gradient = new LinearGradient(0, 0, 720 * scale, 200 * scale, colors, positions, android.graphics.Shader.TileMode.CLAMP);
		mPaint.setColor(0xFFFFFFFF);
		mPaint.setShader(gradient);
		mPaint.setMaskFilter(new BlurMaskFilter(1.0f * scale, Blur.INNER));

		canvasBitmap2.drawPath(mCoverReflectionPath, mPaint);
		mPaint.setShader(null);
		mPaint.setMaskFilter(null);
	}

	private void drawLaserCover()
	{
		mPaint.setColor(0xFF0D091D);

		Path hidePath = new Path();
		hidePath.moveTo(0.2f * scale, 139 * scale);
		hidePath.lineTo(120 * scale, 100 * scale);

		hidePath.lineTo(120 * scale, 319 * scale);
		hidePath.lineTo(107 * scale, 321 * scale);
		hidePath.close();

		canvasBitmapLaser1.drawPath(hidePath, mPaint);

		Path path = new Path();
		path.addPath(mCoverPath);
		path.addRoundRect(new RectF(144.18f * scale, 120.15f * scale, 224.28f * scale, 267 * scale), 17.8f * scale, 17.8f * scale, Direction.CCW);
		path.addRoundRect(new RectF(247.42f * scale, 120.15f * scale, 327.52f * scale, 267 * scale), 17.8f * scale, 17.8f * scale, Direction.CCW);
		path.addRoundRect(new RectF(369.35f * scale, 120.15f * scale, 449.45f * scale, 267 * scale), 17.8f * scale, 17.8f * scale, Direction.CCW);
		path.addRoundRect(new RectF(472.59f * scale, 120.15f * scale, 552.69f * scale, 267 * scale), 17.8f * scale, 17.8f * scale, Direction.CCW);

		path.setFillType(Path.FillType.WINDING);
		mPaint.setColor(0xCC000000);
		canvasBitmapLaser1.drawPath(path, mPaint);

	}

	void setAngles(int timeparam, float[] angles1, float[] angles2, int param4)
	{
		int tens = timeparam < 10 ? 0 : timeparam / 10;
		int digits = timeparam < 10 ? timeparam : timeparam % 10;

		for (int i = 0; i < 17; i++)
			angles1[i] = 0;

		for (int i = 0; i < 16; i++)
			angles2[i] = 0;

		switch (tens)
		{
			case 0:
			{
				angles1[2] = angles1[9] = -45;
				angles1[3] = angles1[6] = angles1[7] = 45;
				angles1[4] = angles1[5] = 90;
				break;
			}
			case 1:
			{
				angles1[3] = 22.5f;
				angles1[4] = -22.5f;
				angles1[5] = -90;
				angles1[7] = 45;

				break;
			}
			case 2:
			{
				angles1[3] = 45;
				angles1[5] = -67.5f;
				angles1[6] = 67.5f;

				break;
			}
			case 3:
			{
				angles1[3] = angles1[4] = angles1[5] = angles1[16] = 67.5f;
				angles1[6] = -45;
				break;
			}
			case 4:
			{
				angles1[0] = -45;
				angles1[1] = angles1[4] = angles1[5] = angles1[9] = 45;
				angles1[2] = angles1[3] = angles1[7] = -90;
				break;
			}
			case 5:
			{
				angles1[2] = angles1[7] = -45;
				angles1[4] = angles1[5] = 45;
				break;
			}
			case 6:
			{
				angles1[3] = -22.5f;
				angles1[4] = 67.5f;
				angles1[5] = angles1[6] = 45f;
				angles1[6] = -45;
				break;
			}
			case 7:
			{
				angles1[3] = 67.5f;
				angles1[4] = -67.5f;
				angles1[6] = -90;
				angles1[8] = 45;
				break;
			}
			case 8:
			{
				angles1[2] = angles1[7] = -45;
				angles1[3] = angles1[4] = angles1[5] = angles1[6] = 45;
				break;
			}
			case 9:
			{
				angles1[2] = -45;
				angles1[3] = angles1[4] = 45;
				angles1[5] = 67.5f;
				angles1[6] = -22.5f;
				break;
			}
		}

		switch (digits)
		{
			case 0:
			{
				angles2[2] = angles2[9] = -45;
				angles2[3] = angles2[6] = angles2[7] = 45;
				angles2[4] = angles2[5] = 90;
				break;
			}
			case 1:

			{
				angles2[3] = 22.5f;
				angles2[4] = -22.5f;
				angles2[5] = -90;
				angles2[7] = 45;
				break;
			}
			case 2:
			{
				angles2[3] = 45;
				angles2[5] = -67.5f;
				angles2[6] = 67.5f;

				break;
			}
			case 3:
			{
				angles2[3] = angles2[4] = angles2[5] = 67.5f;
				angles2[6] = -45;
				angles2[15] = angles2[16] = 67.5f;

				break;
			}
			case 4:
			{
				angles2[0] = angles2[9] = -45;
				angles2[1] = angles2[4] = angles2[5] = 45;
				angles2[2] = angles2[3] = angles2[7] = -90;
				break;
			}
			case 5:
			{
				angles2[2] = angles2[7] = -45;
				angles2[4] = angles2[5] = 45;
				break;
			}
			case 6:
			{
				angles2[3] = -22.5f;
				angles2[4] = 67.5f;
				angles2[5] = angles2[6] = 45;
				angles2[7] = -45;
				break;
			}
			case 7:
			{
				angles2[3] = 67.5f;
				angles2[4] = -67.5f;
				angles2[6] = -90;
				angles2[8] = 45;
				break;
			}
			case 8:
			{
				angles2[2] = angles2[7] = -45;
				angles2[3] = angles2[4] = angles2[5] = angles2[6] = 45;
				break;
			}
			case 9:
			{
				angles2[2] = -45;
				angles2[3] = angles2[4] = 45;
				angles2[5] = 67.5f;
				angles2[6] = -22.5f;
				break;
			}
		}
		if (tens == 0 && digits == 0)
		{
			angles2[6] = -45;
			angles2[7] = -45;
			angles2[8] = 45;
			angles2[9] = 0;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 1)
		{
			angles2[12] = -45;
			angles2[13] = -45;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 2)
		{
			angles2[10] = -45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = -45;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 3)
		{
			angles2[10] = -45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = -45;
		}
		if (tens == 0 && digits == 4)
		{
			angles2[13] = 45;
			angles2[14] = 45;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 6)
		{
			angles2[11] = -22.5f;
			angles2[12] = -90;
			angles2[13] = -45;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 7)
		{
			angles2[8] = 45;
			angles2[10] = -45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = -45;
		}
		if ((tens == 0 || tens == 1 || tens == 2) && digits == 8)
		{
			angles2[12] = -45;
			angles2[13] = -45;
		}
		if ((tens == 1 || tens == 2) && digits == 0)
		{
			angles2[6] = -45;
			angles2[7] = -45;
			angles2[8] = 45;
			angles2[9] = 0;
		}
		if (tens == 1 && digits == 4)
		{
			angles1[7] = -90;
			angles1[9] = 45;
		}
		if (tens == 2 && digits == 4)
		{
			angles2[13] = 45;
			angles2[14] = 45;
		}
		if (tens == 3 && digits == 0)
		{
			angles2[13] = -45;
			angles2[14] = -45;
			angles2[6] = -45;
			angles2[7] = -45;
			angles2[8] = 45;
			angles2[9] = 0;
		}
		if (tens == 3 && (digits == 1 || digits == 8))
		{
			angles2[12] = -45;
			angles2[13] = -90;
			angles2[14] = -45;
		}
		if (tens == 3 && (digits == 2 || digits == 3 || digits == 7))
		{
			angles2[10] = -45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = -90;
			angles2[14] = -45;
		}
		if (tens == 3 && digits == 5)
		{
			angles2[13] = -45;
			angles2[14] = -45;
		}
		if (tens == 3 && digits == 9)
		{
			angles2[15] = angles2[16] = -22.5f;
			angles2[6] = -45;
		}
		if (tens == 3 && digits == 6)
		{
			angles2[11] = -22.5f;
			angles2[12] = -90;
			angles2[13] = -90;
			angles2[14] = -45;
		}
		if (tens == 4 && digits == 0)
		{
			angles2[12] = 45;
			angles2[13] = 45;
			angles2[6] = -45;
			angles2[7] = -45;
			angles2[8] = 45;
			angles2[9] = 0;
		}
		if (tens == 4 && (digits == 2 || digits == 3 || digits == 7))
		{
			angles2[10] = -45;
			angles2[11] = -90;
			angles2[12] = -45;
		}
		if (tens == 4 && digits == 4)
		{
			angles2[12] = 45;
			angles2[13] = -90;
			angles2[14] = 45;
		}
		if (tens == 4 && (digits == 5 || digits == 9))
		{
			angles2[12] = 45;
			angles2[13] = 45;
		}
		if (tens == 4 && digits == 6)
		{
			angles2[11] = -22.5f;
			angles2[12] = -45;
		}
		if (tens == 5 && digits == 0)
		{
			angles2[10] = 45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = 45;
			angles2[6] = -45;
			angles2[7] = -45;
			angles2[8] = 45;
			angles2[9] = 0;
		}
		if (tens == 5 && (digits == 1 || digits == 8))
		{
			angles2[10] = 45;
			angles2[11] = -90;
			angles2[12] = 45;
		}
		if (tens == 5 && digits == 4)
		{
			angles2[10] = 45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = -90;
			angles2[14] = 45;
		}
		if (tens == 5 && (digits == 5 || digits == 9))
		{
			angles2[10] = 45;
			angles2[11] = -90;
			angles2[12] = -90;
			angles2[13] = 45;
		}
		if (tens == 5 && digits == 6)
		{
			angles2[10] = 45;
			angles2[11] = 67.5f;
		}
		if (tens == 1 && param4 == 0)
		{
			angles1[12] = -45;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = -45;
		}
		if ((tens == 2 || tens == 3) && param4 == 0)
		{
			angles1[10] = -45;
			angles1[11] = -90;
			angles1[12] = -90;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = -45;
		}
		if (tens == 5 && param4 == 0)
		{
			angles1[14] = -45;
			angles1[15] = -45;
		}
		if ((param4 == 1 || param4 == 2) && (tens == 0 || tens == 4))
		{
			angles1[14] = 45;
			angles1[15] = 45;
		}
		if ((param4 == 1 || param4 == 2) && tens == 1)
		{
			angles1[12] = -45;
			angles1[13] = -90;
			angles1[14] = -45;
		}
		if ((param4 == 1 || param4 == 2) && (tens == 2 || tens == 3))
		{
			angles1[10] = -45;
			angles1[11] = -90;
			angles1[12] = -90;
			angles1[13] = -90;
			angles1[14] = -45;
		}
		if ((param4 == 3 || param4 == 7) && tens == 1)
		{
			angles1[12] = -45;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = -45;
		}
		if ((param4 == 3 || param4 == 7) && (tens == 2 || tens == 3))
		{
			angles1[10] = -45;
			angles1[11] = -90;
			angles1[12] = -90;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = -45;
		}
		if ((param4 == 3 || param4 == 7) && tens == 5)
		{
			angles1[14] = -45;
			angles1[15] = -45;
		}
		if ((param4 == 4 || param4 == 8) && (tens == 0 || tens == 4))
		{
			angles1[12] = 45;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = 45;
		}
		if ((param4 == 4 || param4 == 8) && (tens == 2 || tens == 3))
		{
			angles1[12] = -45;
			angles1[11] = -90;
			angles1[10] = -45;
		}
		if ((param4 == 4 || param4 == 8) && tens == 5)
		{
			angles1[12] = 45;
			angles1[13] = -90;
			angles1[14] = 45;
		}
		if ((param4 == 5 || param4 == 6) && (tens == 0 || tens == 4))
		{
			angles1[10] = 45;
			angles1[11] = -90;
			angles1[12] = -90;
			angles1[13] = -90;
			angles1[14] = -90;
			angles1[15] = 45;
		}
		if ((param4 == 5 || param4 == 6) && tens == 1)
		{
			angles1[10] = 45;
			angles1[11] = -90;
			angles1[12] = 45;
		}
		if ((param4 == 5 || param4 == 6) && tens == 5)
		{
			angles1[10] = 45;
			angles1[11] = -90;
			angles1[12] = -90;
			angles1[13] = -90;
			angles1[14] = 45;
		}
		if (param4 == 9 && (tens == 0 || tens == 4))
		{
			angles1[13] = 67.5f;
			angles1[14] = -90;
			angles1[15] = 45;
		}
		if (param4 == 9 && tens == 1)
		{
			angles1[13] = -22.5f;
			angles1[12] = -45;
		}
		if (param4 == 9 && (tens == 2 || tens == 3))
		{
			angles1[13] = -22.5f;
			angles1[12] = -90;
			angles1[11] = -90;
			angles1[10] = -45;
		}
		if (param4 == 9 && tens == 5)
		{
			angles1[13] = 67.5f;
			angles1[14] = 45;
		}
	}

	private void setCoordinates(float offsetX, float offsetY, float[][] coordinates, boolean tens)
	{
		if (tens)
		{
			coordinates[0][0] = (offsetX + 53.4f) * scale;
			coordinates[0][1] = (offsetY + 5.34f) * scale;
			coordinates[1][0] = (offsetX + 117.48f) * scale;
			coordinates[1][1] = (offsetY + 5.34f) * scale;
			coordinates[2][0] = (offsetX + 53.4f) * scale;
			coordinates[2][1] = (offsetY + 21.36f) * scale;
			coordinates[3][0] = (offsetX + 117.48f) * scale;
			coordinates[3][1] = (offsetY + 21.36f) * scale;

			coordinates[4][0] = (offsetX + 53.4f) * scale;
			coordinates[4][1] = (offsetY + 85.44f) * scale;
			coordinates[5][0] = (offsetX + 117.48f) * scale;
			coordinates[5][1] = (offsetY + 85.44f) * scale;
			coordinates[6][0] = (offsetX + 53.4f) * scale;
			coordinates[6][1] = (offsetY + 149.52f) * scale;
			coordinates[7][0] = (offsetX + 117.48f) * scale;
			coordinates[7][1] = (offsetY + 149.52f) * scale;

			coordinates[8][0] = (offsetX + 53.4f) * scale;
			coordinates[8][1] = (offsetY + 168.21f) * scale;
			coordinates[9][0] = (offsetX + 117.48f) * scale;
			coordinates[9][1] = (offsetY + 168.21f) * scale;
			coordinates[10][0] = (offsetX + 9.79f) * scale;
			coordinates[10][1] = (offsetY + 21.36f) * scale;
			coordinates[11][0] = (offsetX + 9.79f) * scale;
			coordinates[11][1] = (offsetY + 61.41f) * scale;

			coordinates[12][0] = (offsetX + 9.79f) * scale;
			coordinates[12][1] = (offsetY + 85.44f) * scale;
			coordinates[13][0] = (offsetX + 9.79f) * scale;
			coordinates[13][1] = (offsetY + 100.57f) * scale;
			coordinates[14][0] = (offsetX + 9.79f) * scale;
			coordinates[14][1] = (offsetY + 168) * scale;
			coordinates[15][0] = (offsetX + 9.79f) * scale;
			coordinates[15][1] = (offsetY + 168.21f) * scale;
			coordinates[16][0] = (offsetX + 34.71f) * scale;
			coordinates[16][1] = (offsetY + 168.21f) * scale;
		}
		else
		{
			coordinates[0][0] = (offsetX + 33.82f) * scale;
			coordinates[0][1] = (offsetY + 5.34f) * scale;
			coordinates[1][0] = (offsetX + 97.9f) * scale;
			coordinates[1][1] = (offsetY + 5.34f) * scale;
			coordinates[2][0] = (offsetX + 33.82f) * scale;
			coordinates[2][1] = (offsetY + 21.36f) * scale;
			coordinates[3][0] = (offsetX + 97.9f) * scale;
			coordinates[3][1] = (offsetY + 21.36f) * scale;

			coordinates[4][0] = (offsetX + 33.82f) * scale;
			coordinates[4][1] = (offsetY + 85.44f) * scale;
			coordinates[5][0] = (offsetX + 97.9f) * scale;
			coordinates[5][1] = (offsetY + 85.44f) * scale;
			coordinates[6][0] = (offsetX + 33.82f) * scale;
			coordinates[6][1] = (offsetY + 149.52f) * scale;
			coordinates[7][0] = (offsetX + 97.9f) * scale;
			coordinates[7][1] = (offsetY + 149.52f) * scale;

			coordinates[8][0] = (offsetX + 33.82f) * scale;
			coordinates[8][1] = (offsetY + 168.21f) * scale;
			coordinates[9][0] = (offsetX + 97.9f) * scale;
			coordinates[9][1] = (offsetY + 168.21f) * scale;
			coordinates[10][0] = (offsetX + 9.79f) * scale;
			coordinates[10][1] = (offsetY + 21.36f) * scale;
			coordinates[11][0] = (offsetX + 9.79f) * scale;
			coordinates[11][1] = (offsetY + 61.41f) * scale;

			coordinates[12][0] = (offsetX + 9.79f) * scale;
			coordinates[12][1] = (offsetY + 85.44f) * scale;
			coordinates[13][0] = (offsetX + 9.79f) * scale;
			coordinates[13][1] = (offsetY + 149.52f) * scale;
			coordinates[14][0] = (offsetX + 9.79f) * scale;
			coordinates[14][1] = (offsetY + 168.21f) * scale;
			coordinates[15][0] = (offsetX + 15.13f) * scale;
			coordinates[15][1] = (offsetY + 168.21f) * scale;

			//this mirror is identical to previous. I've made it on purpose,
			//so the tens and digits mirror arrays have the same number of members
			coordinates[16][0] = (offsetX + 15.13f) * scale;
			coordinates[16][1] = (offsetY + 168.21f) * scale;
		}
	}

	int[]		mOldDigits			= new int[2];
	int[]		mCurrentDigits		= new int[2];

	float[][]	mTargetAngles		= new float[4][17];
	float[][]	mCurrentAngles		= new float[4][17];
	float[][]	mTurnAngles		= new float[4][17];

	float[][][]	mMirrorCoordinates	= new float[4][17][2];
	Calendar	mCalendar;
	float		laserX;
	float		laserY;
	float		laserRotation;
	float		angle;
	boolean		found;

	int			index1;
	int			index2;
	Path laserPath;
	
	void setLaser()
	{
		mCalendar = Calendar.getInstance();

		setCoordinates(98, 107, mMirrorCoordinates[0], true);
		setCoordinates(222, 107, mMirrorCoordinates[1], false);
		setCoordinates(324, 107, mMirrorCoordinates[2], true);
		setCoordinates(449, 107, mMirrorCoordinates[3], false);

	}

	private void drawMirrorsLaser()
	{

		mCurrentDigits[0] = mCalendar.get(Calendar.MINUTE);
		mCurrentDigits[1] = mCalendar.get(Calendar.SECOND);

		if (mCurrentDigits[0] != mOldDigits[0] || mCurrentDigits[1] != mOldDigits[1])
		{
			mOldDigits[0] = mCurrentDigits[0];
			mOldDigits[1] = mCurrentDigits[1];

			setAngles(mCurrentDigits[0], mTargetAngles[0], mTargetAngles[1], 0);
			setAngles(mCurrentDigits[1], mTargetAngles[2], mTargetAngles[3], mCurrentDigits[0] < 10 ? (mCurrentDigits[0]) : (mCurrentDigits[0] % 10));
			
			
			for (int j = 0; j < 4; j++)
			{
				for (int i = 0; i < 17; i++)
				{
					if(Math.abs(mCurrentAngles[j][i]  - mTargetAngles[j][i]) < eps )
					{
						mTurnAngles[j][i] = (mTargetAngles[j][i]  - mCurrentAngles[j][i] ) / 5.0f;
					}
				}
			}
		}
		
		for (int j = 0; j < 4; j++)
		{
			for (int i = 0; i < 17; i++)
			{
				if(Math.abs(mCurrentAngles[j][i]  - mTargetAngles[j][i]) < eps )
				{
					mCurrentAngles[j][i] += mTurnAngles[j][i];
				}
			}
		}
		{
			laserPath = new Path();

			//propagate laser beam
			{
				laserX = 102 * scale;
				laserY = 275 * scale;

				laserPath.moveTo(laserX, laserY);
				laserRotation = 0;
				angle = -1;
				found = false;

				index1 = -1;
				index2 = -1;
				//tens
				while (laserX > (44 * scale) && laserX < (580 * scale) && laserY > (107 * scale) && laserY < (294 * scale))
				{

					laserX = laserX + (float) (Math.cos(laserRotation / 180.0f * Math.PI));
					laserY = laserY + (float) (Math.sin(laserRotation / 180.0f * Math.PI));
					found = false;

					for (int j = 0; j < 4; j++)
					{
						for (int i = 0; i < 17; i++)
						{
							if (Math.abs(mMirrorCoordinates[j][i][0] - laserX) < eps && Math.abs(mMirrorCoordinates[j][i][1] - laserY) < eps)
							{
								if (index1 != i || index2 != j)
								{
									index1 = i;
									index2 = j;
									found = true;
									laserX = mMirrorCoordinates[j][i][0];
									laserY = mMirrorCoordinates[j][i][1];

									angle = mTargetAngles[j][i];
									break;
								}
							}
						}

						if (found)
							break;
					}

					if (found)
					{
						if (angle == 0 || angle == 90 || angle == 45 || angle == -45 || angle == 22.5 || angle == -22.5 || angle == -90 || angle == -67.5 || angle == 67.5)
						{
							laserPath.lineTo(laserX, laserY);
							laserRotation = 2.0f * angle - laserRotation;
						}
					}
				}
				laserPath.lineTo(laserX, laserY);
			}
			for (int j = 0; j < 4; j++)
			{
				for (int i = 0; i < 17; i++)
				{
					drawPixelMirror(mMirrorCoordinates[j][i][0], mMirrorCoordinates[j][i][1], mCurrentAngles[j][i]);
				}
			}

			mPaint.setColor(0xFFFF0000);
			mPaint.setStyle(Paint.Style.STROKE);
			canvasBitmapLaser.drawPath(laserPath, mPaint);
			mPaint.setStyle(Paint.Style.FILL);
			canvasBitmapLaser.drawPath(laserPath, _paintBlur);
		}
	}

	private void drawPixelMirror(float centerX, float centerY, float angleDeg)
	{
		mPaint.setStrokeWidth(2);
		mPaint.setColor(0xFFDEDEDE);
		float length = 5 * scale;
		float startX = centerX - length * (float) Math.cos(angleDeg * Math.PI / 180.0f);
		float startY = centerY - length * (float) Math.sin(angleDeg * Math.PI / 180.0f);
		float stopX = centerX + length * (float) Math.cos(angleDeg * Math.PI / 180.0f);
		float stopY = centerY + length * (float) Math.sin(angleDeg * Math.PI / 180.0f);

		canvasMirrors.drawLine(startX, startY, stopX, stopY, mPaint);
	}
}
