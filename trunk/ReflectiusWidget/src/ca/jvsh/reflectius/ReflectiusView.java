package ca.jvsh.reflectius;

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

	float						scale					= 0.4f;

	Path						mCoverPath;
	Path						mCoverReflectionPath;

	public ReflectiusView(Context context, int widgetId)
	{
		DisplayMetrics metrics = ReflectiusWidgetApp.getMetrics();
		;

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
		drawLaser();

		canvasBitmap.drawBitmap(bitmapLaser, 0, 0, mPaint);

		mirrorsBitmap = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasMirrors = new Canvas(mirrorsBitmap);
		drawMirrors();
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

	private void drawLaser()
	{

		mPaint.setColor(0xFFFF0000);
		canvasBitmapLaser.drawLine(122 * scale, 114 * scale, 581 * scale, 265 * scale, mPaint);
		canvasBitmapLaser.drawLine(122 * scale, 114 * scale, 581 * scale, 265 * scale, _paintBlur);

	}

	private void drawLaserCover()
	{
		mPaint.setColor(0xFF0D091D);
		
		canvasBitmapLaser1.drawRect(110 * scale* 0.89f, 120 * scale* 0.89f, 130 * scale * 0.89f, 320 * scale * 0.89f, mPaint);
		
		Path path = new Path();
		path.addPath(mCoverPath);
		path.addRoundRect(new RectF(162 * scale * 0.89f, 135 * scale * 0.89f, (162 + 90) * scale * 0.89f, (135 + 165) * scale * 0.89f), 20 * scale * 0.89f, 20 * scale * 0.89f, Direction.CCW);
		path.addRoundRect(new RectF(278 * scale * 0.89f, 135 * scale * 0.89f, (278 + 90) * scale * 0.89f, (135 + 165) * scale * 0.89f), 20 * scale * 0.89f, 20 * scale * 0.89f, Direction.CCW);
		path.addRoundRect(new RectF(415 * scale * 0.89f, 135 * scale * 0.89f, (415 + 90) * scale * 0.89f, (135 + 165) * scale * 0.89f), 20 * scale * 0.89f, 20 * scale * 0.89f, Direction.CCW);
		path.addRoundRect(new RectF(531 * scale * 0.89f, 135 * scale * 0.89f, (531 + 90) * scale * 0.89f, (135 + 165) * scale * 0.89f), 20 * scale * 0.89f, 20 * scale * 0.89f, Direction.CCW);

		path.setFillType(Path.FillType.WINDING);
		mPaint.setColor(0xCC000000);
		canvasBitmapLaser1.drawPath(path, mPaint);

	}

	private void drawMirrors()
	{
		//tens
		float offsetX = 110;
		float offsetY = 120;
		drawPixelMirror((offsetX + 60) * scale* 0.89f, (offsetY + 6) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 132) * scale* 0.89f, (offsetY + 6) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 60) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 132) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 60) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 132) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 60) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 132) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 60) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 132) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 69) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 113) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 39) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);

		//digit
		offsetX = 250;
		offsetY = 120;
		drawPixelMirror((offsetX + 38) * scale* 0.89f, (offsetY + 6) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 110) * scale* 0.89f, (offsetY + 6) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 38) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 110) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 38) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 110) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 38) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 110) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 38) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 110) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 24) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 69) * scale* 0.89f, 0);

		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 96) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 168) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 11) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
		drawPixelMirror((offsetX + 17) * scale* 0.89f, (offsetY + 189) * scale* 0.89f, 0);
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
