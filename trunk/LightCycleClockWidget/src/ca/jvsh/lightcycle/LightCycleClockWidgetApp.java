package ca.jvsh.lightcycle;

import java.util.Hashtable;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class LightCycleClockWidgetApp extends Application
{
	private static LightCycleClockWidgetApp					self;
	private static Hashtable<Integer, LightCycleClockView>	views	= new Hashtable<Integer, LightCycleClockView>();
	private static DisplayMetrics						metrics;

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		self = this;
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		UpdateAllWidgets();
	}

	public void UpdateAllWidgets()
	{
		AppWidgetManager man = AppWidgetManager.getInstance(getApplication());
		views.clear();
		int[] ids = man.getAppWidgetIds(new ComponentName(getApplication(), LightCycleClockWidgetProvider.class));
		for (int x : ids)
		{
			UpdateWidget(x);
		}
	}

	public void UpdateWidget(int widgetId)
	{
		if (!views.containsKey(widgetId))
		{
			LightCycleClockView view = new LightCycleClockView(this, widgetId);
			views.put(widgetId, view);
		}
	}

	public void DeleteWidget(int widgetId)
	{
		if (views.containsKey(widgetId))
		{
			views.remove(widgetId);
		}
	}

	public LightCycleClockView GetView(int widgetId)
	{
		if (!views.containsKey(widgetId))
		{
			LightCycleClockView view = new LightCycleClockView(this, widgetId);
			views.put(widgetId, view);
		}
		return views.get(widgetId);
	}

	public static Context getApplication()
	{
		return self;
	}

	public static DisplayMetrics getMetrics()
	{
		return metrics;
	}
}
