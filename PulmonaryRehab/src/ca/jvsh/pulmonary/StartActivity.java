package ca.jvsh.pulmonary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class StartActivity extends Activity
{
	/**
	 * Launch Home activity helper
	 * 
	 * @param c context where launch home from (used by SplashscreenActivity)
	 */
	public static void launch(Context c)
	{
		Intent intent = new Intent(c, StartActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.start_activity);

		getActionBar().hide();
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// find the button and add click method to it
		final Button getStartedButton = (Button) findViewById(R.id.get_started_button);
		getStartedButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), BreathlessnessActivity.class);
				//hIntent.setClass(v.getContext(), ExerciseActivity.class);
				//hIntent.setClass(v.getContext(), QuizActivity.class);
				//hIntent.setClass(v.getContext(), SettingListActivity.class);
				startActivity(hIntent);
			}
		});
		
		final ImageButton editUserButton = (ImageButton) findViewById(R.id.edit_user_button);
		editUserButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), SettingListActivity.class);
				startActivity(hIntent);
			}
		});
	}

}
