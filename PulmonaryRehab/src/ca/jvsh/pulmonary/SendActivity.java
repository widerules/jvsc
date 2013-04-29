package ca.jvsh.pulmonary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SendActivity extends Activity
{
	/**
	 * Launch Home activity helper
	 * 
	 * @param c context where launch home from (used by SplashscreenActivity)
	 */
	public static void launch(Context c)
	{
		Intent intent = new Intent(c, SendActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_activity);

		
		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		// find the button and add click method to it
		final Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				
			}
		});	

	}


}
