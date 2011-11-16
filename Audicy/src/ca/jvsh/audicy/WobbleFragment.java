package ca.jvsh.audicy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WobbleFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.wobble_fragment, container, false);
		View tv = v.findViewById(R.id.wobble_fragment_layout);
		LinearLayout linearLayout = (LinearLayout) tv;
		HeatView heatView = new HeatView(v.getContext());
		linearLayout.addView(heatView);
		return v;
	}

}