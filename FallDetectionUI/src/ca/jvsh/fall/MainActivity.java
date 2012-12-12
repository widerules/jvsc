package ca.jvsh.fall;

import gnu.trove.function.TIntFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import java.lang.ref.WeakReference;
import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
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
	//consts and magic numbers
	private static final int	AXES					= 3;
	static final String			AXES_NAMES[]			= { "X: ", "Y: ", "Z: " };
	private static final int	COUNTS					= 777;
	private static final int	MAX						= 0;
	private static final int	MIN						= 1;

	protected static final int	MSG_SENSOR				= 1;

	protected static final int	UPDATE_COUNTER			= 10;

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
	private ToneGenerator		mToneGenerator;

	//Logger
	private TextView			mCurrentState;
	private EditText			mLogEditText;

	//Sensors
	private SensorManager		mSensorManager;
	private Sensor				mAccelerometer;
	private Sensor				mGyroscope;
	private SensorDataHandler	mSensorHandler;

	//flags
	private boolean				mTalkBack;
	private boolean				mBeep;

	//lists
	private TFloatList			mElementsList[]			= new TFloatList[AXES];
	private TIntList			mMinMaxIndicesList[][]	= new TIntList[AXES][2];
	private float				mRanges[]				= new float[AXES];
	private float				mTotal[]				= new float[AXES];
	private TLongList			mTimeStampsList;

	//this variable is to reduce frequency of the screen updates - we don't need it to update text field values so often
	private int					mUpdateCounter;

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
		mSensorHandler = new SensorDataHandler(this);

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

		for (int i = 0; i < AXES; i++)
		{
			mElementsList[i] = new TFloatArrayList(COUNTS);
			for (int j = 0; j < 2; j++)
				mMinMaxIndicesList[i][j] = new TIntArrayList();
		}

		mTimeStampsList = new TLongArrayList(COUNTS);

		//test
		/*{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 2 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}

		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 3 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 4 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 2 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 6 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 2 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 5 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}
		
		{
			Message mSensorMessage = new Message();
			Bundle mMessageBundle = new Bundle();
		
			mMessageBundle.putInt("SensorType", 1);
			mMessageBundle.putFloatArray("SensorValues", new float[] { 1 });
			mMessageBundle.putLong("Timestamp", System.nanoTime());
		
			mSensorMessage.setData(mMessageBundle);
		
			mSensorHandler.sendMessage(mSensorMessage);
		}*/
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

		for (int i = 0; i < AXES; i++)
		{
			mElementsList[i].fill(0, COUNTS, 0);
			for (int j = 0; j < 2; j++)
			{
				mMinMaxIndicesList[i][j].clear();
				mMinMaxIndicesList[i][j].add(COUNTS - 1);
			}
			mTotal[i] = 0;
		}
		mTimeStampsList.fill(0, COUNTS, 0);

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
		Message mSensorMessage = new Message();
		Bundle mMessageBundle = new Bundle();

		mMessageBundle.putInt("SensorType", event.sensor.getType());
		mMessageBundle.putFloatArray("SensorValues", event.values);
		mMessageBundle.putLong("Timestamp", event.timestamp);

		mSensorMessage.setData(mMessageBundle);

		mSensorHandler.sendMessage(mSensorMessage);
	}

	static class SensorDataHandler extends Handler
	{
		WeakReference<MainActivity>	mSensorActivity;

		Decreaser					decreaser	= new Decreaser();

		SensorDataHandler(MainActivity sensorActivity)
		{
			mSensorActivity = new WeakReference<MainActivity>(sensorActivity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			MainActivity sensorActivity = mSensorActivity.get();

			Bundle bundle = msg.getData();

			int type = bundle.getInt("SensorType");

			if (type == Sensor.TYPE_ACCELEROMETER)
			{

			}
			else if (type == Sensor.TYPE_GYROSCOPE)
			{

			}

			float values[] = bundle.getFloatArray("SensorValues");

			String maxVal = "";
			String minVal = "";
			String averageVal = "";
			String rangeVal = "";

			for (int i = 0; i < AXES; i++)
			{
				//average calculation
				sensorActivity.mTotal[i] -= sensorActivity.mElementsList[i].removeAt(0);
				sensorActivity.mElementsList[i].add(values[i]);
				sensorActivity.mTotal[i] += values[i];
				averageVal += String.format(AXES_NAMES[i] + " %6.3f ", sensorActivity.mTotal[i] / COUNTS);

				//max calculation
				{
					sensorActivity.mMinMaxIndicesList[i][MAX].transformValues(decreaser);

					while (!sensorActivity.mMinMaxIndicesList[i][MAX].isEmpty()
							&& values[i] >= sensorActivity.mElementsList[i].get(sensorActivity.mMinMaxIndicesList[i][MAX]
									.get(sensorActivity.mMinMaxIndicesList[i][MAX]
											.size() - 1)))
						sensorActivity.mMinMaxIndicesList[i][MAX].remove(sensorActivity.mMinMaxIndicesList[i][MAX].size() - 1, 1);

					sensorActivity.mMinMaxIndicesList[i][MAX].add(COUNTS - 1);

					sensorActivity.mRanges[i] = sensorActivity.mElementsList[i].get(sensorActivity.mMinMaxIndicesList[i][MAX].get(0));
					maxVal += String.format(AXES_NAMES[i] + " %6.3f ", sensorActivity.mRanges[i]);

					//maxVal +=
					//		String.format(AXES_NAMES[i] + " %6.3f sz %3d ", sensorActivity.mElementsList[i].get(sensorActivity.mIndicesList[i].get(0)),
					//				sensorActivity.mIndicesList[i].size());

					if (!sensorActivity.mMinMaxIndicesList[i][MAX].isEmpty() && sensorActivity.mMinMaxIndicesList[i][MAX].get(0) == 0)
						sensorActivity.mMinMaxIndicesList[i][MAX].remove(0, 1);
				}

				//max calculation
				{
					sensorActivity.mMinMaxIndicesList[i][MIN].transformValues(decreaser);

					while (!sensorActivity.mMinMaxIndicesList[i][MIN].isEmpty()
							&& values[i] <= sensorActivity.mElementsList[i].get(sensorActivity.mMinMaxIndicesList[i][MIN]
									.get(sensorActivity.mMinMaxIndicesList[i][MIN]
											.size() - 1)))
						sensorActivity.mMinMaxIndicesList[i][MIN].remove(sensorActivity.mMinMaxIndicesList[i][MIN].size() - 1, 1);

					sensorActivity.mMinMaxIndicesList[i][MIN].add(COUNTS - 1);

					minVal += String.format(AXES_NAMES[i] + " %6.3f ", sensorActivity.mElementsList[i].get(sensorActivity.mMinMaxIndicesList[i][MIN].get(0)));

					sensorActivity.mRanges[i] -= sensorActivity.mElementsList[i].get(sensorActivity.mMinMaxIndicesList[i][MIN].get(0));
					if (!sensorActivity.mMinMaxIndicesList[i][MIN].isEmpty() && sensorActivity.mMinMaxIndicesList[i][MIN].get(0) == 0)
						sensorActivity.mMinMaxIndicesList[i][MIN].remove(0, 1);
				}

				rangeVal += String.format(AXES_NAMES[i] + " %6.3f ", sensorActivity.mRanges[i]);

			}

			sensorActivity.mTimeStampsList.removeAt(0);
			sensorActivity.mTimeStampsList.add(bundle.getLong("Timestamp"));

			if (sensorActivity.mUpdateCounter++ % UPDATE_COUNTER == 0)
			{
				sensorActivity.mMaxTextView.setText(maxVal);
				sensorActivity.mMinTextView.setText(minVal);
				sensorActivity.mRangeTextView.setText(rangeVal);

				sensorActivity.mAverageTextView.setText(averageVal);
				sensorActivity.mFrequencyTextView.setText(String.format("%7.3f Hz", COUNTS
						/ ((double) (sensorActivity.mTimeStampsList.get(COUNTS - 1) - sensorActivity.mTimeStampsList.get(0)) / 1000000000.0)));
			}

		}

		private static class Decreaser implements TIntFunction
		{
			@Override
			public int execute(int v)
			{
				return v - 1;
			}
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
