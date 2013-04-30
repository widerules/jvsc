package ca.jvsh.pulmonary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
public class FingerInsertActivity extends Activity
{
	/**
	 * Launch Home activity helper
	 * 
	 * @param c context where launch home from (used by SplashscreenActivity)
	 */
	public static void launch(Context c)
	{
		Intent intent = new Intent(c, FingerInsertActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.finger_insert_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		WebView view = (WebView) findViewById(R.id.webView1);
		view.loadUrl("file:///android_asset/finger.gif");
		view.setPadding(0, 0, 0, 0);
		view.setInitialScale(getScale());

		// find the button and add click method to it
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), ExerciseStartActivity.class);
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
		Double val = (double) width / 894.0;
		val = val * 100d;
		return val.intValue();
	}
}
