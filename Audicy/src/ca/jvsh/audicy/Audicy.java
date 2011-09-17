package ca.jvsh.audicy;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.Activity;

import android.os.Bundle;

public class Audicy extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		ActionBar.Tab drumTab = bar.newTab().setText("Drum Machine");
		ActionBar.Tab wobbleTab = bar.newTab().setText("Wobble Bass");

		DrumFragment drumFragment = new DrumFragment();
		WobbleFragment wobbleFragment = new WobbleFragment();

		drumTab.setTabListener(new AudicyTabListener(drumFragment));
		wobbleTab.setTabListener(new AudicyTabListener(wobbleFragment));


		bar.addTab(drumTab);
		bar.addTab(wobbleTab);
	}


	protected static class AudicyTabListener implements ActionBar.TabListener
	{
		private Fragment		mFragment;

		public AudicyTabListener(Fragment fragment)
		{
			mFragment = fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			ft.add(R.id.fragment_place, mFragment, null);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
			ft.remove(mFragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
			//...
		}
	}
}