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
public class LungsHealthFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LungsHealthFragment()
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
		View root = inflater.inflate(R.layout.fragment_lungs_health, container, false);
		
		NumberPicker fevPicker = (NumberPicker) root.findViewById(R.id.numberPickerFev);
		fevPicker.setMaxValue(200);
		fevPicker.setMinValue(0);
		fevPicker.setValue(100);
		fevPicker.setWrapSelectorWheel(false);
		
		NumberPicker fev1Picker = (NumberPicker) root.findViewById(R.id.numberPickerFev1);
		fev1Picker.setMaxValue(200);
		fev1Picker.setMinValue(0);
		fev1Picker.setValue(100);
		fev1Picker.setWrapSelectorWheel(false);

		NumberPicker packYearsPicker = (NumberPicker) root.findViewById(R.id.numberPickerPackYearsSmoked);
		packYearsPicker.setMaxValue(200);
		packYearsPicker.setMinValue(0);
		packYearsPicker.setValue(100);
		packYearsPicker.setWrapSelectorWheel(false);

		NumberPicker fvcPicker = (NumberPicker) root.findViewById(R.id.numberPickerFVC);
		fvcPicker.setMaxValue(200);
		fvcPicker.setMinValue(0);
		fvcPicker.setValue(100);
		fvcPicker.setWrapSelectorWheel(false);

		NumberPicker fvc1Picker = (NumberPicker) root.findViewById(R.id.numberPickerFVC1);
		fvc1Picker.setMaxValue(200);
		fvc1Picker.setMinValue(0);
		fvc1Picker.setValue(100);
		fvc1Picker.setWrapSelectorWheel(false);

		NumberPicker vo2Picker = (NumberPicker) root.findViewById(R.id.numberPickerV02);
		vo2Picker.setMaxValue(200);
		vo2Picker.setMinValue(0);
		vo2Picker.setValue(100);
		vo2Picker.setWrapSelectorWheel(false);
		
		return root;
	}
}
