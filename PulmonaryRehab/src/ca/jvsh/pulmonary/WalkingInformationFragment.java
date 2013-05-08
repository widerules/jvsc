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
		View root = inflater.inflate(R.layout.fragment_walking_information, container, false);
	
		NumberPicker peakWorkloadPicker = (NumberPicker) root.findViewById(R.id.numberPickerPeakWorkload);
		peakWorkloadPicker.setMaxValue(200);
		peakWorkloadPicker.setMinValue(0);
		peakWorkloadPicker.setValue(100);
		peakWorkloadPicker.setWrapSelectorWheel(false);
		
		NumberPicker restingBorgPicker = (NumberPicker) root.findViewById(R.id.numberPickerRestingBorg);
		restingBorgPicker.setMaxValue(10);
		restingBorgPicker.setMinValue(0);
		restingBorgPicker.setValue(0);
		restingBorgPicker.setWrapSelectorWheel(false);

		NumberPicker sixMinWalkPicker = (NumberPicker) root.findViewById(R.id.numberPickerSixMinWalk);
		sixMinWalkPicker.setMaxValue(1000);
		sixMinWalkPicker.setMinValue(0);
		sixMinWalkPicker.setValue(400);
		sixMinWalkPicker.setWrapSelectorWheel(false);

		NumberPicker maxExercisePicker = (NumberPicker) root.findViewById(R.id.numberPickerMaxExercise);
		maxExercisePicker.setMaxValue(200);
		maxExercisePicker.setMinValue(0);
		maxExercisePicker.setValue(100);
		maxExercisePicker.setWrapSelectorWheel(false);
		
		return root;
	}
}
