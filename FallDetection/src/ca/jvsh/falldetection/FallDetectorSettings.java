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

package ca.jvsh.falldetection;

import ca.jvsh.falldetection.utils.Utils;
import android.content.SharedPreferences;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 * @author Levente Bagi
 */
public class FallDetectorSettings
{

	SharedPreferences	mSettings;

	public static int	M_NONE	= 1;
	public static int	M_PACE	= 2;
	public static int	M_SPEED	= 3;

	public FallDetectorSettings(SharedPreferences settings)
	{
		mSettings = settings;
	}

	public float getAccelerometerFrequency()
	{
		try
		{
			return Float.valueOf(mSettings.getString("accelerometer_frequency", "128").trim());
		}
		catch (NumberFormatException e)
		{
			// TODO: reset value, & notify user somehow
			return 0f;
		}
	}

	public boolean wakeAggressively()
	{
		return mSettings.getString("operation_level", "run_in_background").equals("wake_up");
	}

	public boolean keepScreenOn()
	{
		return mSettings.getString("operation_level", "run_in_background").equals("keep_screen_on");
	}

	//
	// Internal

	public void saveServiceRunningWithTimestamp(boolean running)
	{
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", Utils.currentTimeInMillis());
		editor.commit();
	}

	public void saveServiceRunningWithNullTimestamp(boolean running)
	{
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", running);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	public void clearServiceRunning()
	{
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", false);
		editor.putLong("last_seen", 0);
		editor.commit();
	}

	public boolean isServiceRunning()
	{
		return mSettings.getBoolean("service_running", false);
	}

	public boolean isNewStart()
	{
		// activity last paused more than 10 minutes ago
		return mSettings.getLong("last_seen", 0) < Utils.currentTimeInMillis() - 1000 * 60 * 10;
	}

}
