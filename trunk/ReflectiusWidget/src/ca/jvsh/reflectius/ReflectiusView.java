package ca.jvsh.reflectius;

import java.util.Random;
import ca.jvsh.reflectius.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public class ReflectiusView
{
	public static String				INTENT_ON_CLICK_FORMAT	= "ca.jvsh.reflectius.id.%d.click";
	private static final int			REFRESH_RATE			= 40;
	private int							cheight;
	private int							cwidth;
	private float						density;

	private long						lastRedrawMillis		= 0;
	private int							mWidgetId;

	private final Paint			mPaint			= new Paint();
	Bitmap bitmap;
	Canvas canvasBitmap;
	Random random = new Random();

	public ReflectiusView(Context context, int widgetId)
	{
		DisplayMetrics metrics =ReflectiusWidgetApp.getMetrics();;
		
		density = metrics.density;
		cwidth = (int) (400 * metrics.density);
		cheight =  (int) (200 * metrics.density);

		mWidgetId = widgetId;
		setState();
		
		bitmap = Bitmap.createBitmap(cwidth, cheight, Bitmap.Config.ARGB_8888);
		canvasBitmap = new Canvas(bitmap);

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

		
		mPaint.setColor(Color.argb(40,random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		
		canvasBitmap.drawRect(0, 0, cwidth, cheight,  mPaint);
		
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
}
