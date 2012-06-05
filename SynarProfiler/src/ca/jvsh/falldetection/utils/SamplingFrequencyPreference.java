package ca.jvsh.falldetection.utils;

/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import ca.jvsh.falldetection.R;
import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * An {@link EditTextPreference} that only allows float values.
 * @author Levente Bagi
 */
public class SamplingFrequencyPreference extends EditMeasurementPreference
{

	public SamplingFrequencyPreference(Context context)
	{
		super(context);
	}

	public SamplingFrequencyPreference(Context context, AttributeSet attr)
	{
		super(context, attr);
	}

	public SamplingFrequencyPreference(Context context, AttributeSet attr, int defStyle)
	{
		super(context, attr, defStyle);
	}

	protected void initPreferenceDetails() {
		mTitleResource = R.string.sampling_frequency_setting_title;
		
	}
}
