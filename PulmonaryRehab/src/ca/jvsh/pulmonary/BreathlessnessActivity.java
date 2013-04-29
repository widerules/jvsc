package ca.jvsh.pulmonary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BreathlessnessActivity extends Activity
{
	ListView	lv;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.breathlessness_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		String[] s = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" };
		lv = (ListView) findViewById(R.id.selection_listview);

		lv.setAdapter(new BreathlessnessListAdapter<String>(this, R.layout.breathlessness_item, s));
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				ListView lv = (ListView) arg0;
				
				//TextView tv = (TextView) lv.getChildAt(arg2);
				//String s = tv.getText().toString();
				Toast.makeText(BreathlessnessActivity.this, "Clicked item is " + lv.getCheckedItemPosition(), Toast.LENGTH_SHORT).show();
			}
		});
		// find the button and add click method to it
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				int p = lv.getCheckedItemPosition();
				String s = ((TextView) lv.getChildAt(p)).getText().toString();
				Toast.makeText(BreathlessnessActivity.this, "Selected item is " + s, Toast.LENGTH_LONG).show();

				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), OximeterConnectActivity.class);
				startActivity(hIntent);
			}
		});

	}

	private List<Car> getData()
	{
		List<Car> cars = new ArrayList<Car>();
		cars.add(new Car("Dodge", "Viper"));
		cars.add(new Car("Chevrolet", "Corvette"));
		cars.add(new Car("Aston Martin", "Vanquish"));
		cars.add(new Car("Lamborghini", "Diablo"));
		cars.add(new Car("Ford", "Pinto"));
		return cars;
	}

	public class Car extends HashMap
	{
		public String	make;
		public String	model;

		public Car(String make, String model)
		{
			this.make = make;
			this.model = model;
		}

		public static final String	KEY_MODEL	= "model";
		public static final String	KEY_MAKE	= "make";

		@Override
		public String get(Object k)
		{
			String key = (String) k;
			if (KEY_MAKE.equals(key))
				return make;
			else if (KEY_MODEL.equals(key))
				return model;
			return null;
		}
	}
}
