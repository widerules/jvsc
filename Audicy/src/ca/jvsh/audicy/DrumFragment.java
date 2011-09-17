package ca.jvsh.audicy;


import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class DrumFragment extends Fragment
{

	static final int ADD_ID = Menu.FIRST;
	static final int HELP_ID = Menu.FIRST+1;

	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		
		if(savedInstanceState == null)
		{
			System.out.println("\n DrumFragment onCreateView savedInstanceState == null\n");

			View v = inflater.inflate(R.layout.drum_fragment, container, false);
			View tv = v.findViewById(R.id.drum_fragment_layout);
			LinearLayout linearLayout = (LinearLayout) tv;
			
			LinearLayout.LayoutParams layoutParamsSound = new LinearLayout.LayoutParams(
					200, 100);
			LinearLayout.LayoutParams layoutParamsTV = new LinearLayout.LayoutParams(
					100, 100);
			LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layoutParamsLLD = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);

			
			
			LinearLayout samplesLayout = new LinearLayout(v.getContext());
			samplesLayout.setOrientation(LinearLayout.VERTICAL);
			//make buttons
			for (int i = 0; i < 7; i++)
			{
				Button button = new Button(v.getContext());
				try
				{
					button.setText("Sound"+((Integer)i).toString());
					samplesLayout.addView(button, layoutParamsSound);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			HorizontalScrollView scrollView = new HorizontalScrollView(v.getContext());
			LinearLayout drumPatternLayout = new LinearLayout(v.getContext());
			for (int k = 0; k < 26; k++)
			{
				LinearLayout llv = new LinearLayout(v.getContext());
				for (int i = 0; i < 7; i++)
				{
					llv.setOrientation(LinearLayout.VERTICAL);
					ToggleButton button = new ToggleButton(v.getContext());
					try
					{
						button.setText("Drum "+((Integer)k).toString() + "," + ((Integer)i).toString());
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					llv.addView(button, layoutParamsTV);
				}

				drumPatternLayout.addView(llv, layoutParamsLL);
			}
			scrollView.addView(drumPatternLayout, layoutParamsLLD);
			linearLayout.addView(samplesLayout);
			linearLayout.addView(scrollView);

			return v;
		}
		else
		{
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.add(Menu.NONE, ADD_ID, 0, "Add").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(Menu.NONE, HELP_ID, 0, "Help").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//final ContentResolver cr = getActivity().getContentResolver();

		switch (item.getItemId())
		{
		case ADD_ID:
			
			return true;

		case HELP_ID:
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
