package ca.jvsh.networkbenchmark;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public class BenchmarkActivity extends Activity
{

	ActionBar					mActionBar;
	private static final String	TAG	= "BenchmarkActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_benchmark);

		//create action bar
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		//create and add tabs
		ActionBar.Tab serverTab = mActionBar.newTab().setText("Server");
		ActionBar.Tab clientTab = mActionBar.newTab().setText("Client");

		serverTab.setTabListener(new TabListener<ServerFragment>(this, "server", ServerFragment.class));
		clientTab.setTabListener(new TabListener<ClientFragment>(this, "client", ClientFragment.class));

		mActionBar.addTab(serverTab);
		mActionBar.addTab(clientTab);

		//switch to the previously saved tab
		switch (PreferenceManager.getDefaultSharedPreferences(this).getInt("tab_selected", 0))
		{
			case 1:
				mActionBar.selectTab(clientTab);
				break;
			case 0:
			default:
				mActionBar.selectTab(serverTab);
				break;
		}
	}

	//on activity stop we are saving the tab that was selected last
	public void onStop()
	{
		if (mActionBar != null)
		{
			// save result in the memory
			{
				Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
				editor.putInt("tab_selected", mActionBar.getSelectedTab().getPosition());
				editor.commit();
			}
		}
		super.onStop();
	}

	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener
	{
		private final Activity	mActivity;
		private final String	mTag;
		private final Class<T>	mClass;
		private final Bundle	mArgs;
		private Fragment		mFragment;

		public TabListener(Activity activity, String tag, Class<T> clz)
		{
			this(activity, tag, clz, null);
		}

		public TabListener(Activity activity, String tag, Class<T> clz,
				Bundle args)
		{
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab.
			//If so, hide it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isHidden())
			{
				FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
				ft.hide(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			if (mFragment == null)
			{
				Log.d(TAG, "onTabSelected (mFragment == null)");

				mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			}
			else
			{
				Log.d(TAG, "onTabSelected (mFragment != null)");
				ft.show(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
			if (mFragment != null)
			{
				Log.d(TAG, "onTabUnselected method, hiding mFragment");
				ft.hide(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
			Log.d(TAG, "onTabReselected, Reselected tab");
		}
	}

}
