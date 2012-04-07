package ca.jvsh.flute.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import ca.jvsh.flute.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class GameLevelsActivity extends SherlockFragmentActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levels);
		// find the button and add click method to it
		final Button gameButton = (Button) findViewById(R.id.button_game);
		gameButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), GameActivity.class);
				startActivity(hIntent);
			}
		});

	}
}
