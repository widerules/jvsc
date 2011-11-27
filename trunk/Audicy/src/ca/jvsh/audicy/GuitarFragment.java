package ca.jvsh.audicy;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class GuitarFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.guitar_fragment, container, false);
		View tv = v.findViewById(R.id.guitarframeLayout);
		FrameLayout frameLayout = (FrameLayout) tv;

		GuitarView guitarView = new GuitarView(v.getContext());
		frameLayout.addView(guitarView);
		return v;
	}

}
