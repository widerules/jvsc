package ca.jvsh.lightcycle;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class LightCycleClockWidgetProvider extends AppWidgetProvider {
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int x : appWidgetIds) {
			((LightCycleClockWidgetApp) context.getApplicationContext()).DeleteWidget(x);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int i=0; i<appWidgetIds.length; i++)
		{
			((LightCycleClockWidgetApp) context.getApplicationContext()).UpdateWidget(appWidgetIds[i]);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().startsWith("ca.jvsh.reflectius")) {
			int id = intent.getIntExtra("widgetId", 0);
			((LightCycleClockWidgetApp) context.getApplicationContext()).GetView(id).OnClick();
		}
	}
}
