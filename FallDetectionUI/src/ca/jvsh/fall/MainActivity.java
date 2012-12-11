package ca.jvsh.fall;

import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener, TextToSpeech.OnInitListener
{
	//Text views
	private TextView			mStatusTextView;
	private TextView			mMaxTextView;
	private TextView			mMinTextView;
	private TextView			mRangeTextView;
	private TextView			mAverageTextView;
	private TextView			mFrequencyTextView;

	//Radio buttons
	private RadioGroup			mSensorTypeRadioGroup;

	//TalkBack
	private CheckBox			mTalkBackCheckBox;
	private TextToSpeech		mTts;
	
	//Beep
	private CheckBox			mBeepCheckBox;
	private ToneGenerator mToneGenerator;
	
	//Logger
	private TextView			mCurrentState;
	private EditText			mLogEditText;

	//Sensors
	private SensorManager	mSensorManager;
	private Sensor		mAccelerometer;
	private Sensor		mGyroscope;

	//flags
	private boolean				mTalkBack	= false;
	private boolean				mBeep = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mStatusTextView = ((TextView) findViewById(R.id.textView_status));
		mMaxTextView = ((TextView) findViewById(R.id.textView_max));
		mMinTextView = ((TextView) findViewById(R.id.textView_min));
		mRangeTextView = ((TextView) findViewById(R.id.textView_range));
		mAverageTextView = ((TextView) findViewById(R.id.textView_average));
		mFrequencyTextView = ((TextView) findViewById(R.id.textView_frequency));

		mSensorTypeRadioGroup = ((RadioGroup) findViewById(R.id.radioGroupSensor));
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		
		mTalkBackCheckBox = ((CheckBox) findViewById(R.id.checkBoxTalkback));
		mTalkBackCheckBox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//is chkIos checked?
				mTalkBack = ((CheckBox) v).isChecked();

				if (mTalkBack)
				{
					mTts.speak("Talkback is on", TextToSpeech.QUEUE_FLUSH, null);
				}
				else
				{
					Toast.makeText(v.getContext(), "Talkback is off", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mTts = new TextToSpeech(this, this);
		
		mBeepCheckBox = ((CheckBox) findViewById(R.id.checkBoxBeep));
		mBeepCheckBox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//is chkIos checked?
				mBeep = ((CheckBox) v).isChecked();

				if (mBeep)
				{
					mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
					//TONE_CDMA_ONE_MIN_BEEP
				}
				else
				{
					Toast.makeText(v.getContext(), "No beeping on fall", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mToneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

		mCurrentState = ((TextView) findViewById(R.id.textView_state));

		mLogEditText = ((EditText) findViewById(R.id.editTextLog));

	}

	@Override
	public void onDestroy()
	{
		mSensorManager.unregisterListener(this);
		// Don't forget to shutdown!
		if (mTts != null)
		{
			mTts.stop();
			mTts.shutdown();
		}

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.menu_start:

				onMenuItemStart();

				return true;

			case R.id.menu_stop:

				onMenuItemStop();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void onMenuItemStart()
	{
		Toast.makeText(this, "Starting receiving data", Toast.LENGTH_SHORT).show();
		mLogEditText.setText("");

		switch (mSensorTypeRadioGroup.getCheckedRadioButtonId())
		{
			case R.id.radioAccelerometer:
				mSensorManager.registerListener(this,
						mAccelerometer,
						SensorManager.SENSOR_DELAY_FASTEST);
				mStatusTextView.setText("receiving data from accelerometer");
				break;

			case R.id.radioGyroscope:
				mSensorManager.registerListener(this,
						mGyroscope,
						SensorManager.SENSOR_DELAY_FASTEST);
				mStatusTextView.setText("receiving data from gyroscope");
				break;
		}

		for (int i = 0; i < mSensorTypeRadioGroup.getChildCount(); i++)
		{
			mSensorTypeRadioGroup.getChildAt(i).setEnabled(false);
		}
	}

	private void onMenuItemStop()
	{
		mSensorManager.unregisterListener(this);
		
		for (int i = 0; i < mSensorTypeRadioGroup.getChildCount(); i++)
		{
			mSensorTypeRadioGroup.getChildAt(i).setEnabled(true);
		}
		
		Toast.makeText(this, "Stopping receiving data", Toast.LENGTH_SHORT).show();
		mStatusTextView.setText("no activity");
		
		mMaxTextView.setText("");
		mMinTextView.setText("");
		mRangeTextView.setText("");
		mAverageTextView.setText("");
		mFrequencyTextView.setText("");
		mCurrentState.setText("");
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		switch (accuracy)
		{
			case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
				Toast.makeText(this, "maximum accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
				Toast.makeText(this, "average level of accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
				Toast.makeText(this, "low accuracy", Toast.LENGTH_LONG).show();
				break;
			case SensorManager.SENSOR_STATUS_UNRELIABLE:
				Toast.makeText(this, "sensor cannot be trusted", Toast.LENGTH_LONG).show();
				break;

		}
	}

	public void onSensorChanged(SensorEvent event)
	{

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			//Acceleration X (m/s^2)
			//event.values[0];
			//Acceleration Y (m/s^2)
			//event.values[1];
			//Acceleration Z (m/s^2)
			//event.values[2];
		}
		else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
		{
			//Angular Velocity X (rad/s)
			//event.values[0];
			//Angular Velocity Y (rad/s)
			//event.values[1];
			//Angular Velocity Z (rad/s)
			//event.values[2];
		}

	}

	// Implements TextToSpeech.OnInitListener.
	public void onInit(int status)
	{
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS)
		{
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will indicate this.
			int result = mTts.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA ||
					result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				// Lanuage data is missing or the language is not supported.
				Toast.makeText(this, "Language is not available.", Toast.LENGTH_SHORT).show();
			}
			else
			{
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.

				// The TTS engine has been successfully initialized.
				// Allow the user to press the button for the app to speak again.

			}
		}
		else
		{
			// Initialization failed.
			Toast.makeText(this, "Could not initialize TextToSpeech.", Toast.LENGTH_SHORT).show();
		}
	}
}
