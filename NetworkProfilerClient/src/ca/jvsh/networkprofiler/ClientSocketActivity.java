package ca.jvsh.networkprofiler;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ClientSocketActivity extends Activity
{
	private Button				mButtonChange;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_socket);
		
		mButtonChange = (Button) findViewById(R.id.button_connect);
		mButtonChange.setOnClickListener(buttonConnectListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_client_socket, menu);
		return true;
	}
	
	private OnClickListener	buttonConnectListener	= new OnClickListener()
	{
		public void onClick(View v)
		{
			Toast.makeText(ClientSocketActivity.this, "Connecting to server", Toast.LENGTH_SHORT).show();

		}
	};
}
