package ca.jvsh.pulmonary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

/**
 * An activity representing a list of Settings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SettingDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SettingListFragment} and the item details
 * (if present) is a {@link SettingDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SettingListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SettingListActivity extends FragmentActivity
		implements SettingListFragment.Callbacks
{
	
	
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String		ARG_ITEM_ID	= "item_id";
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean	mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_list);

		getActionBar().hide();
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (findViewById(R.id.setting_detail_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SettingListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.setting_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link SettingListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id)
	{
		if (mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			//Bundle arguments = new Bundle();
			
			if(id.equalsIgnoreCase("1"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				GeneralInformationFragment fragment = new GeneralInformationFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("2"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				HeartHealthFragment fragment = new HeartHealthFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("3"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				LungsHealthFragment fragment = new LungsHealthFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("4"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				WalkingInformationFragment fragment = new WalkingInformationFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			
			

		}
		else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, SettingDetailActivity.class);
			detailIntent.putExtra(ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
