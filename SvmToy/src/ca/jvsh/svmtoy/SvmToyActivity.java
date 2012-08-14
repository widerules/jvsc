package ca.jvsh.svmtoy;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SvmToyActivity extends Activity
{
	private DotSurfaceView	mDotSurfaceView;

	private EditText		mEdit;
	//buttons
	private Button			mButtonChange;
	private Button			mButtonRun;
	private Button			mButtonClear;

	private final int		COLORS			= 3;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm_toy);

		mButtonChange = (Button) findViewById(R.id.button_change);
		mButtonChange.setOnClickListener(changeListener);

		mButtonRun = (Button) findViewById(R.id.button_run);
		mButtonRun.setOnClickListener(runListener);

		mButtonClear = (Button) findViewById(R.id.button_clear);
		mButtonClear.setOnClickListener(clearListener);

		mEdit = (EditText) findViewById(R.id.edit_params);
		mDotSurfaceView = (DotSurfaceView) findViewById(R.id.surfaceView_draw);
	}

	private OnClickListener	changeListener	= new OnClickListener()
											{
												public void onClick(View v)
												{
													mDotSurfaceView.mColorSwitch++;
													mDotSurfaceView.mColorSwitch %= COLORS;

													switch (mDotSurfaceView.mColorSwitch)
													{
														case 0:
															mDotSurfaceView.dotColor = Color.RED;
															mButtonChange.setBackgroundColor(Color.RED);
															break;
														case 1:
															mDotSurfaceView.dotColor = Color.BLUE;
															mButtonChange.setBackgroundColor(Color.BLUE);
															break;
														case 2:
															mDotSurfaceView.dotColor = Color.GREEN;
															mButtonChange.setBackgroundColor(Color.GREEN);
															break;
													}
													
												}
											};

	private OnClickListener	runListener		= new OnClickListener()
											{
												public void onClick(View v)
												{
													Toast.makeText(SvmToyActivity.this, "The run button was clicked.", Toast.LENGTH_LONG).show();
											         
												}
											};

	private OnClickListener	clearListener	= new OnClickListener()
											{
												public void onClick(View v)
												{
													mDotSurfaceView.cleanSurface();
												}
											};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_svm_toy, menu);
		return true;
	}
}
