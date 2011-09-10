package ca.jvsh.audicy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WobbleActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wobble_activity);
		
		if(savedInstanceState == null)
		{
			//Do first time initialization
			Fragment wobbleFragment = WobbleFragment.newInstance();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.wobble_fragment_placement, wobbleFragment).commit();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	//TODO check if we need to remove static
	public static class WobbleFragment extends Fragment
	{
		static WobbleFragment newInstance()
		{
			WobbleFragment wobbleFragment = new WobbleFragment();
			
			return wobbleFragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.wobble_fragment, container, false);
			return v;
		}
	}
}