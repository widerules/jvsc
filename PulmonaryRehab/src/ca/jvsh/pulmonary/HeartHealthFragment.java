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
public class HeartHealthFragment extends Fragment
{
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public HeartHealthFragment()
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
		View root = inflater.inflate(R.layout.fragment_heart_health, container, false);
	
		NumberPicker restingHRPicker = (NumberPicker) root.findViewById(R.id.numberPickerRestingHR);
		restingHRPicker.setMaxValue(200);
		restingHRPicker.setMinValue(0);
		restingHRPicker.setValue(100);
		restingHRPicker.setWrapSelectorWheel(false);
		
		NumberPicker peakHRPicker = (NumberPicker) root.findViewById(R.id.numberPickerPeakHR);
		peakHRPicker.setMaxValue(200);
		peakHRPicker.setMinValue(0);
		peakHRPicker.setValue(100);
		peakHRPicker.setWrapSelectorWheel(false);

		NumberPicker hrReservePicker = (NumberPicker) root.findViewById(R.id.numberPickerHRReserve);
		hrReservePicker.setMaxValue(200);
		hrReservePicker.setMinValue(0);
		hrReservePicker.setValue(100);
		hrReservePicker.setWrapSelectorWheel(false);

		NumberPicker peakSystolicPicker = (NumberPicker) root.findViewById(R.id.numberPickerPeakSystolic);
		peakSystolicPicker.setMaxValue(200);
		peakSystolicPicker.setMinValue(0);
		peakSystolicPicker.setValue(100);
		peakSystolicPicker.setWrapSelectorWheel(false);

		NumberPicker restingSystolicPicker = (NumberPicker) root.findViewById(R.id.numberPickerRestingSystolic);
		restingSystolicPicker.setMaxValue(200);
		restingSystolicPicker.setMinValue(0);
		restingSystolicPicker.setValue(100);
		restingSystolicPicker.setWrapSelectorWheel(false);

		NumberPicker restingDiastolicPicker = (NumberPicker) root.findViewById(R.id.numberPickerRestingDiastolic);
		restingDiastolicPicker.setMaxValue(200);
		restingDiastolicPicker.setMinValue(0);
		restingDiastolicPicker.setValue(100);
		restingDiastolicPicker.setWrapSelectorWheel(false);

		NumberPicker peakDiastolicPicker = (NumberPicker) root.findViewById(R.id.numberPickerPeakDiastolic);
		peakDiastolicPicker.setMaxValue(200);
		peakDiastolicPicker.setMinValue(0);
		peakDiastolicPicker.setValue(100);
		peakDiastolicPicker.setWrapSelectorWheel(false);

		return root;
	}
}
