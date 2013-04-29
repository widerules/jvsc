package ca.jvsh.pulmonary;



import ca.jvsh.pulmonary.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

public class SplashScreenActivity extends Activity
{

	private Animation			endAnimation;

	private Handler				endAnimationHandler;
	private Runnable			endAnimationRunnable;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreen_activity);
		
		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		findViewById(R.id.splashlayout);
		
		

		endAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		endAnimation.setFillAfter(true);

		endAnimationHandler = new Handler();
		endAnimationRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				findViewById(R.id.splashlayout).startAnimation(endAnimation);
			}
		};

		endAnimation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				StartActivity.launch(SplashScreenActivity.this);
				SplashScreenActivity.this.finish();
			}
		});

		endAnimationHandler.removeCallbacks(endAnimationRunnable);
		endAnimationHandler.postDelayed(endAnimationRunnable, 300);
	}

}
