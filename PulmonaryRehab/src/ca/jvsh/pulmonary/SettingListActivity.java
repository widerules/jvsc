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
	public static final String	ARG_ITEM_ID	= "item_id";
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean				mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_list);

		//getActionBar().hide();
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

			//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
			GeneralInformationFragment fragmentGeneralInformation = new GeneralInformationFragment();
			//fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.setting_detail_container, fragmentGeneralInformation)
					.commit();
			getActionBar().setTitle(R.string.general_information_tab);
			getActionBar().setIcon(R.drawable.general);
		}
		else
		{
			getActionBar().setTitle(R.string.user_settings);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link SettingListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id)
	{
		if (mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			//Bundle arguments = new Bundle();

			switch (id)

			{
				case R.string.general_information_tab:
					//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
					GeneralInformationFragment fragmentGeneralInformation = new GeneralInformationFragment();
					//fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.setting_detail_container, fragmentGeneralInformation)
							.commit();
					getActionBar().setIcon(R.drawable.general);
					getActionBar().setTitle(R.string.general_information_tab);
					break;
				case R.string.measurements_at_rest_tab:
					//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
					HeartHealthFragment fragmentMeasurementsAtRest = new HeartHealthFragment();
					//fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.setting_detail_container, fragmentMeasurementsAtRest)
							.commit();
					getActionBar().setIcon(R.drawable.heart);
					getActionBar().setTitle(R.string.measurements_at_rest_tab);

					break;
				case R.string.exercise_plan_tab:
					//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
					LungsHealthFragment fragmentExercisePlan = new LungsHealthFragment();
					//fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.setting_detail_container, fragmentExercisePlan)
							.commit();
					getActionBar().setIcon(R.drawable.lungs);
					getActionBar().setTitle(R.string.exercise_plan_tab);
					break;
				case R.string.contact_information_tab:
					//arguments.putString(SettingDetailFragment.ARG_ITEM_ID, id);
					WalkingInformationFragment fragmentContactInformation = new WalkingInformationFragment();
					//fragment.setArguments(arguments);
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.setting_detail_container, fragmentContactInformation)
							.commit();
					getActionBar().setIcon(R.drawable.walking);
					getActionBar().setTitle(R.string.contact_information_tab);

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
