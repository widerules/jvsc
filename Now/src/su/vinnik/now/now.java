package su.vinnik.now;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class now extends Activity implements View.OnClickListener
{
	Integer i = 0;
	Button btn;
	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.main);
		btn = (Button)findViewById(R.id.Button01);
		btn.setOnClickListener(this);
		updateCounter( );
	}

	public void onClick(View view)
	{
		updateCounter( );
	}

	private void updateCounter()
	{
		i++;
		btn.setText(i.toString( ));
	}
}