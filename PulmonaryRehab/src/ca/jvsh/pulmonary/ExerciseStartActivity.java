package ca.jvsh.pulmonary;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ExerciseStartActivity extends Activity
{

	Random								random						= new Random();

	private GraphicalView				mOxygenChartView;
	private GraphicalView				mHeartRateChartView;

	private TimeSeries					mOxygenData					= new TimeSeries("Oxygen %");
	private XYMultipleSeriesDataset		mOxygenDataset				= new XYMultipleSeriesDataset();

	private TimeSeries					mHeartRateData				= new TimeSeries("HeartRate");
	private XYMultipleSeriesDataset		mHeartRateDataset			= new XYMultipleSeriesDataset();

	private XYSeriesRenderer			mOxygenSeriesRenderer		= new XYSeriesRenderer();
	private XYMultipleSeriesRenderer	mOxygenRenderer				= new XYMultipleSeriesRenderer();

	private XYSeriesRenderer			mHeartRateSeriesRenderer	= new XYSeriesRenderer();
	private XYMultipleSeriesRenderer	mHeartRateRenderer			= new XYMultipleSeriesRenderer();

	private Thread						mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.exercise_start_activity);

		this.getActionBar().hide();
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mOxygenDataset.addSeries(mOxygenData);// Add single dataset to multiple dataset
		mHeartRateDataset.addSeries(mHeartRateData);// Add single dataset to multiple dataset

		setSeriesStyle(mOxygenSeriesRenderer);
		setupRenderer(mOxygenRenderer);

		mOxygenRenderer.setXTitle("Time");
		mOxygenRenderer.setYTitle("Oxygen %");
		mOxygenRenderer.addSeriesRenderer(mOxygenSeriesRenderer);		// Add single renderer to multiple renderer
		
		setSeriesStyle(mHeartRateSeriesRenderer);
		setupRenderer(mHeartRateRenderer);

		mHeartRateRenderer.setXTitle("Time");
		mHeartRateRenderer.setYTitle("Heart Rate");
		mHeartRateRenderer.addSeriesRenderer(mHeartRateSeriesRenderer);// Add single renderer to multiple renderer

		// find the button and add click method to it
		final Button startButton = (Button) findViewById(R.id.start_exercise_button);
		startButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Intent hIntent = new Intent();
				hIntent.setClass(v.getContext(), ExerciseActivity.class);
				startActivity(hIntent);
			}
		});

		mThread = new Thread()
		{
			public void run()
			{
				for (int i = 0; i < 1000; i++)
				{
					try
					{
						Thread.sleep(25);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					{
						Point p = new Point(i, random.nextInt(40));

						mOxygenData.add(p.getX(), p.getY());
						if (mOxygenData.getItemCount() > 100)
							mOxygenData.remove(0);

						mOxygenChartView.repaint();
					}

					{
						Point p = new Point(i, random.nextInt(40));

						mHeartRateData.add(p.getX(), p.getY());
						if (mHeartRateData.getItemCount() > 100)
							mHeartRateData.remove(0);

						mHeartRateChartView.repaint();
					}
				}
			}
		};
		mThread.start();

	}

	private void setSeriesStyle(XYSeriesRenderer renderer)
	{
		if (renderer != null)
		{
			renderer.setColor(Color.WHITE);
			renderer.setPointStyle(PointStyle.POINT);
			renderer.setFillPoints(false);
		}
	}

	private void setupRenderer(XYMultipleSeriesRenderer renderer)
	{
		if (renderer != null)
		{
			renderer.setZoomButtonsVisible(false);
			renderer.setZoomEnabled(false, false);
			renderer.setPanEnabled(false);
			renderer.setShowLegend(false);
			renderer.setLabelsTextSize(20);
			renderer.setAxisTitleTextSize(20);
		}
	}

	public class Point
	{

		private int	x;
		private int	y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int getX()
		{
			return x;
		}

		public int getY()
		{
			return y;
		}

	}

	protected void onResume()
	{
		super.onResume();
		if (mOxygenChartView == null)
		{
			LinearLayout layout = (LinearLayout) findViewById(R.id.chartOxygen);
			mOxygenChartView = ChartFactory.getCubeLineChartView(this, mOxygenDataset,
					mOxygenRenderer, 0.15f);

			layout.addView(mOxygenChartView, new LayoutParams
					(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		}
		else
		{
			mOxygenChartView.repaint();
		}

		if (mHeartRateChartView == null)
		{
			LinearLayout layout = (LinearLayout) findViewById(R.id.chartHeartRate);
			mHeartRateChartView = ChartFactory.getCubeLineChartView(this, mHeartRateDataset,
					mHeartRateRenderer, 0.15f);
			layout.addView(mHeartRateChartView, new LayoutParams
					(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		}
		else
		{
			mHeartRateChartView.repaint();
		}
	}

}
