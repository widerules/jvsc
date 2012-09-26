package ca.jvsh.networkbenchmark;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.Point;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class ClientLineGraph {

	private GraphicalView view;
	
	private XYSeries dataset = new XYSeries("network traffic"); 
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	private XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
	
	public ClientLineGraph()
	{
		// Add single dataset to multiple dataset
		mDataset.addSeries(dataset);
		
		// Customization time for line 1!
		renderer.setColor(Color.BLACK);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		
		// Enable Zoom
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXTitle("time, s");
		mRenderer.setYTitle("bytes transmitted");
		mRenderer.setZoomEnabled(true, false);
		mRenderer.setRange(new double[] {0,60,0,100});
		
		// Add single renderer to multiple renderer
		mRenderer.addSeriesRenderer(renderer);	
	}
	
	public GraphicalView getView(Context context) 
	{
		view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return view;
	}
	
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}
	
	
	public void clearPoints()
	{
		dataset.clear();
	}
	
}
