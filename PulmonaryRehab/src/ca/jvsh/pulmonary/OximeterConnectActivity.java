package ca.jvsh.pulmonary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class OximeterConnectActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.oximeter_connect_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		WebView view = (WebView) findViewById(R.id.webView1);
		view.loadUrl("file:///android_asset/jack.gif");
		view.setPadding(0, 0, 0, 0);
		view.setInitialScale(getScale());
		// find the button and add click method to it
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), FingerInsertActivity.class);
				startActivity(hIntent);
			}
		});

	}

	private int getScale()
	{
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point outSize = new Point();
		display.getSize(outSize);
		int width = outSize.x;
		Double val = (double) width / 1353.0;
		val = val * 100d;
		return val.intValue();
	}

}
