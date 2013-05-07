package ca.jvsh.pulmonary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A fragment representing a single Setting detail screen.
 * This fragment is either contained in a {@link SettingListActivity}
 * in two-pane mode (on tablets) or a {@link SettingDetailActivity}
 * on handsets.
 */
public class WalkingInformationFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WalkingInformationFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_walking_information, container, false);
	}
}
