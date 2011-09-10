package ca.jvsh.audicy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DrumActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drum_activity);
		
		if(savedInstanceState == null)
		{
			//Do first time initialization
			Fragment drumFragment = DrumFragment.newInstance();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.drum_fragment_placement, drumFragment).commit();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	//TODO check if we need to remove static
	public static class DrumFragment extends Fragment
	{
		static DrumFragment newInstance()
		{
			DrumFragment drumFragment = new DrumFragment();
			
			return drumFragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.drum_fragment, container, false);
			return v;
		}
	}
}
