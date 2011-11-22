package ca.jvsh.audicy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class WobbleFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.wobble_fragment, container, false);
		View tv = v.findViewById(R.id.frameLayout1);
		FrameLayout frameLayout = (FrameLayout) tv;

		//HeatView heatView = new HeatView(v.getContext());
		//linearLayout.addView(heatView);

		SoundView soundView = new SoundView(v.getContext());
		frameLayout.addView(soundView);
		return v;
	}

}