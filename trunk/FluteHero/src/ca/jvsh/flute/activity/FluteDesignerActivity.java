package ca.jvsh.flute.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import ca.jvsh.flute.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FluteDesignerActivity extends SherlockFragmentActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flute_designer);

	}
}
