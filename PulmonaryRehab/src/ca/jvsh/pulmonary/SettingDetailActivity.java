package ca.jvsh.pulmonary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * An activity representing a single Setting detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SettingListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link SettingDetailFragment}.
 */
public class SettingDetailActivity extends FragmentActivity
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String		ARG_ITEM_ID	= "item_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_detail);

		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().hide();
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null)
		{
			String id = getIntent().getExtras().getString(ARG_ITEM_ID);
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			//Bundle arguments = new Bundle();
			//arguments.putString(ARG_ITEM_ID,
			//		getIntent().getStringExtra(ARG_ITEM_ID));
			//SettingDetailFragment fragment = new SettingDetailFragment();
			//fragment.setArguments(arguments);
			//getSupportFragmentManager().beginTransaction()
			//		.add(R.id.setting_detail_container, fragment)
			//		.commit();
			
			
			if(id.equalsIgnoreCase("1"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				GeneralInformationFragment fragment = new GeneralInformationFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.add(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("2"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				HeartHealthFragment fragment = new HeartHealthFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.add(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("3"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				LungsHealthFragment fragment = new LungsHealthFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.add(R.id.setting_detail_container, fragment)
						.commit();
				
			}
			else if (id.equalsIgnoreCase("4"))
			{
				//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
				WalkingInformationFragment fragment = new WalkingInformationFragment();
				//fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.add(R.id.setting_detail_container, fragment)
						.commit();
				
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpTo(this, new Intent(this, SettingListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}