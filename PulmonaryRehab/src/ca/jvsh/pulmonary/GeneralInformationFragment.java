package ca.jvsh.pulmonary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * A fragment representing a single Setting detail screen.
 * This fragment is either contained in a {@link SettingListActivity}
 * in two-pane mode (on tablets) or a {@link SettingDetailActivity}
 * on handsets.
 */
public class GeneralInformationFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GeneralInformationFragment()
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
		View root =  inflater.inflate(R.layout.fragment_general_information, container, false);
		
		NumberPicker heigthPicker = (NumberPicker) root.findViewById(R.id.numberPickerHeight);
		heigthPicker.setMaxValue(230);
		heigthPicker.setMinValue(40);
		heigthPicker.setValue(150);
		heigthPicker.setWrapSelectorWheel(false);
		

		NumberPicker weightPicker = (NumberPicker) root.findViewById(R.id.numberPickerWeight);
		weightPicker.setMaxValue(230);
		weightPicker.setMinValue(10);
		weightPicker.setValue(80);
		weightPicker.setWrapSelectorWheel(false);
		
		return root;
	}
}
