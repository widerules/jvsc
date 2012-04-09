package ca.jvsh.flute.activity;

import org.metalev.multitouch.controller.MultiTouchController;
import org.metalev.multitouch.controller.MultiTouchController.MultiTouchObjectCanvas;
import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FluteView extends SurfaceView implements MultiTouchObjectCanvas<Object>, SurfaceHolder.Callback
{
	private MultiTouchController<Object>	multiTouchController;
	private PointInfo						mCurrTouchPoint;

	private static final int[]				mTouchPointColors		= { 0xFFF2E3B6, 0xFFBCD5B0, 0xFF76A68B, 0xFF898C70, 0xFFBF5F56,
																    0xFF66202C, 0xFF8F4A3C, 0xFFAB7245, 0xFFB59E65, 0xFFD4CE76 };

	private final Paint						mPaint				= new Paint();

	//private int[]							mTouchPointColors	= new int[MultiTouchController.MAX_TOUCH_POINTS];

	// Width, height and pixel format of the surface.
	private int								canvasWidth			= 0;
	private int								canvasHeight		= 0;

	// Application handle.
	private Context							appContext;

	// The surface manager for the view.
	private SurfaceHolder					surfaceHolder		= null;

	// Debugging tag.
	private static final String				TAG					= "FluteView";

	// Enable flags.  In order to run, we need onSurfaceCreated() and
	// onResume(), which can come in either order.  So we track which ones
	// we have by these flags.  When all are set, we're good to go.  Note
	// that this is distinct from the game state machine, and its pause
	// and resume actions -- the whole game is enabled by the combination
	// of these flags set in enableFlags.
	private static final int				ENABLE_SURFACE		= 0x01;
	private static final int				ENABLE_SIZE			= 0x02;
	private static final int				ENABLE_RESUMED		= 0x04;
	private static final int				ENABLE_STARTED		= 0x08;
	private static final int				ENABLE_FOCUSED		= 0x10;
	private static final int				ENABLE_ALL			= ENABLE_SURFACE | ENABLE_SIZE | ENABLE_RESUMED | ENABLE_STARTED | ENABLE_FOCUSED;

	// The time in ms to sleep each time round the main animation loop.
	// If zero, we will not sleep, but will run continuously.
	private long							animationDelay		= 0;

	// Enablement flags; see comment above.
	private int								enableFlags			= 0;

	// The ticker thread which runs the animation.  null if not active.
	private Ticker							animTicker			= null;

	//////////////////////////////////////////////////////////////////////
	//clarinet variables
	//////////////////////////////////////////////////////////////////////

	private AudioTrack						mAudioOutput;
	private int								mOutBufferSize;

	private int								mInBufferSize;
	private AudioRecord						mAudioInput;

	private int								mBufferSize;

	int										fs					= 44100;

	float									frequency			= 440.0f;
	float									amplitude			= 1.0f;
	float									increment;
	float									angle				= 0;
	short[]									buffer;
	short[]									inputBuffer;

	private static boolean					mActive				= false;

	/////////////////////////////////////////////////////////////////////////
	//clarinet constants
	float									c					= 347.0f;
	float									rho					= 1.2f;

	float									l					= 0.52f;
	float									a					= 0.01f;
	float									Z0					= rho * c / ((float) Math.PI * a * a);

	int										ptr					= 0;
	int										N					= (int) Math.floor(l * fs / c);
	float									upper[]				= new float[N];
	float									lower[]				= new float[N];

	float									bRL[]				= new float[] { -0.2162f, -0.2171f, -0.0545f };
	float									aRL[]				= new float[] { 1, -0.6032f, 0.0910f };

	float									bTL[]				= new float[] { -0.2162f + 1, -0.2171f + -0.6032f, -0.0545f + 0.0910f };
	float									aTL[]				= new float[] { 1, -0.6032f, 0.0910f };

	float									stateRL[]			= new float[2];

	float									stateTL[]			= new float[2];

	float									bL[]				= new float[] { 0.806451596106077f, -1.855863155228909f, 1.371191452991298f, -0.312274852426121f, -0.006883256612646f };
	float									aL[]				= new float[] { 1.000000000000000f, -2.392436499871960f, 1.891289981326362f, -0.511406512428537f, 0.015235504020645f };

	float									R0					= 0.9f;

	float									aw					= 0.015f;
	float									S					= 0.034f * aw;
	float									k					= S * 10000000.0f;
	float									H0					= 0.0007f;

	float									max					= 0;

	float									y0[];
	float									yL[];

	float									U[];

	float									stateLU[]			= new float[4];
	float									stateLL[]			= new float[4];

	int										nDecay				= (int) (0.2 * fs);
	int										nAttack				= (int) (0.1 * fs);

	float									multiplier			= 5000.0f;
	private static final float				MAX_16_BIT			= 32768;
	Thread									clarinetThread;
	float									power;

	//////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////
	//power gauge
	///////////////////////////////////////////////////////////////////////////

	// Number of peaks we will track in the VU meter.
	private static final int				METER_PEAKS			= 4;

	// Time in ms over which peaks in the VU meter fade out.
	private static final int				METER_PEAK_TIME		= 800;

	// Number of updates over which we average the VU meter to get
	// a rolling average.  32 is about 2 seconds.
	private static final int				METER_AVERAGE_COUNT	= 32;

	// Colours for the meter power bar and average bar and peak marks.
	// In METER_PEAK_COL, alpha is set dynamically in the code.
	private static final int				METER_POWER_COL		= 0xff796B7D;
	private static final int				METER_AVERAGE_COL	= 0xa045334A;
	private static final int				METER_PEAK_COL		= 0x00FFA3A3;

	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //

	// Configured meter bar thickness.
	//private int								barHeight			= 32;

	// Display position and size within the parent view.
	//private int								dispX				= 0;
	//private int								dispY				= 0;
	//private int					dispWidth			= 0;
	//private int								dispHeight			= 0;

	// Label text size for the gauge.  Zero if not set yet.
	//private float							labelSize			= 0f;

	// Layout parameters for the VU meter.  Position and size for the
	// bar itself; position and size for the bar labels; position
	// and size for the main readout text.
	//private float							meterBarTop			= 0;
	//private float							meterBarGap			= 0;
	//private float							meterLabX			= 0;
	//private float							meterBarMargin		= 0;

	// Current and previous power levels.
	private float							currentPower		= 0f;
	private float							prevPower			= 0f;

	// Buffered old meter levels, used to calculate the rolling average.
	// Index of the most recent value.
	private float[]							powerHistory		= null;
	private int								historyIndex		= 0;

	// Rolling average power value,  calculated from the history buffer.
	private float							averagePower		= 1.0f;

	// Peak markers in the VU meter, and the times for each one.  A zero
	// time indicates a peak not set.
	private float[]							meterPeaks			= null;
	private long[]							meterPeakTimes		= null;
	private float							meterPeakMax		= 0f;

	// The paint we use for drawing.
	private Paint							powerMeterPaint			= null;

	//////////////////////////////////////////////////////////////////////////

	public FluteView(Context context)
	{
		this(context, null);
	}

	public FluteView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public FluteView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		appContext = context;

		multiTouchController = new MultiTouchController<Object>(this);
		mCurrTouchPoint = new PointInfo();

		/*for (int i = 0; i < MultiTouchController.MAX_TOUCH_POINTS; i++)
		{
			mTouchPointColors[i] = i < TOUCH_COLORS.length ? TOUCH_COLORS[i] : (int) (Math.random() * 0xffffff) + 0xff000000;
		}*/

		// Register for events on the surface.
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		setFocusable(true);
		setFocusableInTouchMode(true);

		mOutBufferSize = AudioTrack.getMinBufferSize(fs, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mInBufferSize = AudioRecord.getMinBufferSize(fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioOutput = new AudioTrack(AudioManager.STREAM_MUSIC, fs, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * mOutBufferSize, AudioTrack.MODE_STREAM);
		mAudioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * mInBufferSize);

		mBufferSize = (int) Math.min(mOutBufferSize, mInBufferSize);

		inputBuffer = new short[mBufferSize];
		buffer = new short[mBufferSize];
		y0 = new float[mBufferSize];
		yL = new float[mBufferSize];
		U = new float[mBufferSize];

		meterPeaks = new float[METER_PEAKS];
		meterPeakTimes = new long[METER_PEAKS];

		// Create and initialize the history buffer.
		powerHistory = new float[METER_AVERAGE_COUNT];
		for (int i = 0; i < METER_AVERAGE_COUNT; ++i)
			powerHistory[i] = 1.0f;
		averagePower = 1.0f;

		// Set up our paint.
		powerMeterPaint = new Paint();
		powerMeterPaint.setAntiAlias(true);
	}

	protected void animStart()
	{
		mActive = true;
		clarinetThread = new Thread()
		{
			public void run()
			{
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

				try
				{
					mAudioOutput.play();
				}
				catch (Exception e)
				{
					Log.e(TAG, "Failed to start playback");
					return;
				}

				try
				{
					mAudioInput.startRecording();
				}
				catch (Exception e)
				{
					Log.e(TAG, "Failed to start recording");
					mAudioOutput.stop();
					return;
				}

				clarinetSound();
				//mAudioOutput.stop();
			}
		};
		clarinetThread.start();
	}

	protected void animStop()
	{
		mActive = false;
		clarinetThread = null;
	}

	void clarinetSound()
	{
		float dp;
		float x;
		float pr;

		float outL;
		float out0;

		float y0_prev = 0;

		float pm = multiplier;
		//float pm_prev = multiplier;
		//float nu = .05f;

		try
		{

			while (mActive)
			{
				mAudioInput.read(inputBuffer, 0, mBufferSize);

				// We need longs to avoid running out of bits.
				float sum = 0;
				float sqsum = 0;
				for (int i = 0; i < mBufferSize; i++)
				{
					final long v = inputBuffer[i];
					sum += v;
					sqsum += v * v;
				}

				power = (sqsum - sum * sum / mBufferSize) / mBufferSize;
				power /= MAX_16_BIT * MAX_16_BIT;
				updatePower(power);

				for (int n = 0; n < mBufferSize; n++)
				{

					/*
					 envelope following
					 */

					pm = 10 * power;// ((1 - nu) * Math.abs(inputBuffer[n]) + nu * pm_prev )/(float)(Short.MAX_VALUE);
					if (pm < 0.05f)
						pm = 0.1f;
					//else if(pm < 0.05f)  pm = 0.8f;
					else if (pm > 1.0f)
						pm = 1.0f;
					//else  pm = 1.0f;
					//pm = 1.0f;
					//if(pm > 0)
					//	Log.d(TAG, "pm " + pm);

					//pm_prev = pm;
					if (n == 0)
					{
						dp = (float) Math.abs(multiplier * pm - y0_prev);
					}
					else
					{
						dp = (float) Math.abs(multiplier * pm - y0[n - 1]);
					}
					x = (float) Math.min(H0, dp * S / k);
					U[n] = aw * (H0 - x) * (float) Math.sqrt(dp * 2 / rho);
					pr = U[n] * Z0;

					//filters
					{
						outL = bL[0] * upper[ptr] + stateLU[0];
						stateLU[0] = bL[1] * upper[ptr] + stateLU[1] - aL[1] * outL;
						stateLU[1] = bL[2] * upper[ptr] + stateLU[2] - aL[2] * outL;
						stateLU[2] = bL[3] * upper[ptr] + stateLU[3] - aL[3] * outL;
						stateLU[3] = bL[4] * upper[ptr] - aL[4] * outL;
					}

					{
						out0 = bL[0] * lower[ptr] + stateLL[0];
						stateLL[0] = bL[1] * lower[ptr] + stateLL[1] - aL[1] * out0;
						stateLL[1] = bL[2] * lower[ptr] + stateLL[2] - aL[2] * out0;
						stateLL[2] = bL[3] * lower[ptr] + stateLL[3] - aL[3] * out0;
						stateLL[3] = bL[4] * lower[ptr] - aL[4] * out0;

					}

					//writer to delay lines
					upper[ptr] = pr + R0 * out0;
					{
						lower[ptr] = bRL[0] * outL + stateRL[0];
						stateRL[0] = bRL[1] * outL + stateRL[1] - aRL[1] * lower[ptr];
						stateRL[1] = bRL[2] * outL - aRL[2] * lower[ptr];
					}

					//output buffers
					y0[n] = out0 + upper[ptr];
					{
						yL[n] = bTL[0] * outL + stateTL[0];
						stateTL[0] = bTL[1] * outL + stateTL[1] - aTL[1] * yL[n];
						stateTL[1] = bTL[2] * outL - aTL[2] * yL[n];
					}

					ptr++;
					if (ptr >= N)
						ptr = 0;

					if (Math.abs(yL[n]) > max)
						max = Math.abs(yL[n]);
				}

				y0_prev = y0[mBufferSize - 1];

				for (int i = 0; i < mBufferSize; i++)
					buffer[i] = (short) (yL[i] / max * Short.MAX_VALUE);

				/*int written=*/mAudioOutput.write(buffer, 0, mBufferSize);
				//Log.d(TAG, "Written " + written);
			}

		}
		catch (Exception e)
		{
			Log.d(TAG, "Error while recording, aborting.");
		}

		try
		{
			mAudioOutput.stop();
		}
		catch (Exception e)
		{
			Log.e(TAG, "Can't stop playback");
			try
			{
				mAudioInput.stop();
			}
			catch (Exception ex)
			{
				Log.e(TAG, "Can't stop recording");
				return;
			}
			return;
		}

		try
		{
			mAudioInput.stop();
		}
		catch (Exception e)
		{
			Log.e(TAG, "Can't stop recording");
			return;
		}

	}

	// ******************************************************************** //
	// State Handling.
	// ******************************************************************** //

	/**
	 * Set the delay in ms in each iteration of the main loop.
	 * 
	 * @param   delay       The time in ms to sleep each time round the main
	 *                      animation loop.  If zero, we will not sleep,
	 *                      but will run continuously.
	 *                      
	 *                      <p>If you want to do all your animation under
	 *                      direct app control using {@link #postUpdate()},
	 *                      just set a large delay.  You may want to consider
	 *                      using 1000 -- i.e. one second -- to make sure
	 *                      you get a refresh at a decent interval.
	 */
	public void setDelay(long delay)
	{
		Log.i(TAG, "setDelay " + delay);
		animationDelay = delay;
	}

	/**
	 * This is called immediately after the surface is first created.
	 * Implementations of this should start up whatever rendering code
	 * they desire.
	 * 
	 * Note that only one thread can ever draw into a Surface, so you
	 * should not draw into the Surface here if your normal rendering
	 * will be in another thread.
	 * 
	 * @param	holder		The SurfaceHolder whose surface is being created.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		setEnable(ENABLE_SURFACE, "surfaceCreated");
	}

	/**
	 * This is called immediately after any structural changes (format or
	 * size) have been made to the surface.  This method is always
	 * called at least once, after surfaceCreated(SurfaceHolder).
	 * 
	 * @param	holder		The SurfaceHolder whose surface has changed.
	 * @param	format		The new PixelFormat of the surface.
	 * @param	width		The new width of the surface.
	 * @param	height		The new height of the surface.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

		setSize(format, width, height);
		setEnable(ENABLE_SIZE, "set size " + width + "x" + height);
	}

	/**
	 * This is called immediately before a surface is destroyed.
	 * After returning from this call, you should no longer try to
	 * access this surface.  If you have a rendering thread that directly
	 * accesses the surface, you must ensure that thread is no longer
	 * touching the Surface before returning from this function.
	 * 
	 * @param	holder		The SurfaceHolder whose surface is being destroyed.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		clearEnable(ENABLE_SURFACE, "surfaceDestroyed");

	}

	/**
	 * The application is starting.  Applications must call this from their
	 * Activity.onStart() method.
	 */
	public void onStart()
	{
		Log.i(TAG, "onStart");

	}

	/**
	 * We're resuming the app.  Applications must call this from their
	 * Activity.onResume() method.
	 */
	public void onResume()
	{
		setEnable(ENABLE_RESUMED, "onResume");
	}

	/**
	 * Start the surface running.  Applications must call this to set
	 * the surface going.  They may use this to implement their own level
	 * of start/stop control, for example to implement a "pause" button.
	 */
	public void surfaceStart()
	{
		setEnable(ENABLE_STARTED, "surfaceStart");
	}

	/**
	 * Stop the surface running.  Applications may call this to stop
	 * the surface running.  They may use this to implement their own level
	 * of start/stop control, for example to implement a "pause" button.
	 */
	public void surfaceStop()
	{
		clearEnable(ENABLE_STARTED, "surfaceStop");
	}

	/**
	 * Pause the app.  Applications must call this from their
	 * Activity.onPause() method.
	 */
	public void onPause()
	{
		clearEnable(ENABLE_RESUMED, "onPause");
	}

	/**
	 * The application is closing down.  Applications must call
	 * this from their Activity.onStop() method.
	 */
	public void onStop()
	{
		Log.i(TAG, "onStop()");

		// Make sure we're paused.
		onPause();
	}

	/**
	 * Handle changes in focus.  When we lose focus, pause the game
	 * so a popup (like the menu) doesn't cause havoc.
	 * 
	 * @param	hasWindowFocus		True iff we have focus.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if (!hasWindowFocus)
			clearEnable(ENABLE_FOCUSED, "onWindowFocusChanged");
		else
			setEnable(ENABLE_FOCUSED, "onWindowFocusChanged");
	}

	/**
	 * Set the given enable flag, and see if we're good to go.
	 * 
	 * @param   flag        The flag to set.
	 * @param   why         Short tag explaining why, for debugging.
	 */
	private void setEnable(int flag, String why)
	{
		boolean enabled1 = false;
		boolean enabled2 = false;
		synchronized (surfaceHolder)
		{
			enabled1 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;
			enableFlags |= flag;
			enabled2 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;

			Log.i(TAG, "EN + " + why + " -> " + enableString());
		}

		// Are we all set?
		if (!enabled1 && enabled2)
			startRun();
	}

	/**
	 * Clear the given enable flag, and see if we need to shut down.
	 * 
	 * @param   flag        The flag to clear.
	 * @param   why         Short tag explaining why, for debugging.
	 */
	private void clearEnable(int flag, String why)
	{
		boolean enabled1 = false;
		boolean enabled2 = false;
		synchronized (surfaceHolder)
		{
			enabled1 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;
			enableFlags &= ~flag;
			enabled2 = (enableFlags & ENABLE_ALL) == ENABLE_ALL;

			Log.i(TAG, "EN - " + why + " -> " + enableString());
		}

		// Do we need to stop?
		if (enabled1 && !enabled2)
			stopRun();
	}

	/**
	 * Get the current enable state as a string for debugging.
	 * 
	 * @return              The current enable state as a string.
	 */
	private String enableString()
	{
		char[] buf = new char[5];
		buf[0] = (enableFlags & ENABLE_SURFACE) != 0 ? 'S' : '-';
		buf[1] = (enableFlags & ENABLE_SIZE) != 0 ? 'Z' : '-';
		buf[2] = (enableFlags & ENABLE_RESUMED) != 0 ? 'R' : '-';
		buf[3] = (enableFlags & ENABLE_STARTED) != 0 ? 'A' : '-';
		buf[4] = (enableFlags & ENABLE_FOCUSED) != 0 ? 'F' : '-';

		return String.valueOf(buf);
	}

	/**
	 * Start the animation running.  All the conditions we need to
	 * run are present (surface, size, resumed).
	 */
	private void startRun()
	{
		synchronized (surfaceHolder)
		{

			// Tell the subclass we're running.
			try
			{
				animStart();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (animTicker != null && animTicker.isAlive())
				animTicker.kill();
			Log.i(TAG, "set running: start ticker");
			//animTicker = !optionSet(LOOPED_TICKER) ? new ThreadTicker() : new LoopTicker();
			animTicker = new LoopTicker();
		}
	}

	/**
	 * Stop the animation running.  Our surface may have been destroyed, so
	 * stop all accesses to it.  If the caller is not the ticker thread,
	 * this method will only return when the ticker thread has died.
	 */
	private void stopRun()
	{
		// Kill the thread if it's running, and wait for it to die.
		// This is important when the surface is destroyed, as we can't
		// touch the surface after we return.  But if I am the ticker
		// thread, don't wait for myself to die.
		Ticker ticker = null;
		synchronized (surfaceHolder)
		{
			ticker = animTicker;
		}
		if (ticker != null && ticker.isAlive())
		{
			if (onSurfaceThread())
				ticker.kill();
			else
				ticker.killAndWait();
		}
		synchronized (surfaceHolder)
		{
			animTicker = null;
		}

		// Tell the subclass we've stopped.
		try
		{
			animStop();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ******************************************************************** //
	// Run Control.
	// ******************************************************************** //

	/**
	 * Asynchronously schedule an update; i.e. a frame of animation.
	 * This can only be called if the SurfaceRunner was created with
	 * the option LOOPED_TICKER.
	 */
	public void postUpdate()
	{
		synchronized (surfaceHolder)
		{
			if (!(animTicker instanceof LoopTicker))
				throw new IllegalArgumentException("Can't post updates" + " without LOOPED_TICKER set");
			LoopTicker ticker = (LoopTicker) animTicker;
			ticker.post();
		}
	}

	private void tick()
	{
		try
		{
			// And update the screen.
			refreshScreen();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Draw the game board to the screen in its current state, as a one-off.
	 * This can be used to refresh the screen.
	 */
	private void refreshScreen()
	{
		Canvas canvas = null;
		try
		{
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder)
			{
				canvas.drawColor(Color.WHITE);

				int numPoints = mCurrTouchPoint.getNumTouchPoints();
				int[] pointerIds = mCurrTouchPoint.getPointerIds();

				if (mCurrTouchPoint.isDown())
				{
					float[] xs = mCurrTouchPoint.getXs();
					float[] ys = mCurrTouchPoint.getYs();

					for (int idx = 0; idx < numPoints; idx++)
					{
						// Show touch circles
						mPaint.setColor(mTouchPointColors[idx]);
						canvas.drawCircle(xs[idx], ys[idx], 50, mPaint);

						// Label touch points on top of everything else
						String label = (idx + 1) + (idx == pointerIds[idx] ? "" : "(id:" + (pointerIds[idx] + 1) + ")");

						canvas.drawText(label, xs[idx] + 50, ys[idx] - 50, mPaint);
					}
				}

				long now = System.currentTimeMillis();

				{
					//canvas.drawColor(0xff000000);

				

				
					// Draw the grid.

					/*final float my = dispY + meterBarMargin;
					final float mh = dispHeight - meterBarMargin * 2;
					final float bx = dispX;
					final float bh = mh - 1;
					final float bw = barHeight - 1;
					final float gh = bh / 10f;

					canvas.drawRect(bx, my, bx + bw, my + bh, drawPaint);

					for (int i = 1; i < 10; ++i)
					{
						final float y = (float) i * (float) bh / 10f;
						canvas.drawLine(bx, my + y, bx + bw, my + y, drawPaint);

					}

					// Draw the labels below the grid.
					final float lx = dispX + meterLabX;
					final float ls = labelSize;
					drawPaint.setTextSize(ls);
					int step = drawPaint.measureText("-99") > bh / 10f - 1 ? 2 : 1;
					for (int i = 0; i <= 10; i += step)
					{
						float ly = my + bh - i * gh - 1 + (labelSize / 2);
						canvas.drawText("" + (i / 10.0f), lx, ly, drawPaint);
					}*/
				}

				{
					// Re-calculate the peak markers.
					calculatePeaks(now, currentPower, prevPower);

					powerMeterPaint.setColor(0xffffff00);
					powerMeterPaint.setStyle(Style.STROKE);
					powerMeterPaint.setStrokeWidth(4.0f);
					// Position parameters.

					/*final float my = dispY + meterBarMargin;
					final float mh = dispHeight - meterBarMargin * 2;
					final float bx = dispX + meterBarTop;
					final float bw = barHeight;
					final float gap = meterBarGap;
					final float bh = mh - 2f;*/

						
					
					//final float p = (currentPower) * bh;
					powerMeterPaint.setStyle(Style.FILL);
					powerMeterPaint.setColor(METER_POWER_COL);
					//canvas.drawRect(mx + 1, by + gap, mx + p + 1, by + bh - gap, paint);
					//canvas.drawRect(bx + gap, my + bh, bx + bw - gap, my + bh - p, drawPaint);
					canvas.drawCircle(canvasWidth / 2, canvasHeight, currentPower * canvasWidth / 4, powerMeterPaint);

					// Draw the average bar.
					//final float pa = (averagePower) * bh;
					powerMeterPaint.setStyle(Style.FILL);
					powerMeterPaint.setColor(METER_AVERAGE_COL);
					//canvas.drawRect(mx + 1, by + 1, mx + pa + 1, by + bh - 1, paint);
					//canvas.drawRect(bx + 1, my + bh, bx + bw - 1, my + bh - pa, drawPaint);
					canvas.drawCircle(canvasWidth / 2, canvasHeight, averagePower * canvasWidth / 4, powerMeterPaint);
					// Draw the power bar.

					
					// Now, draw in the peaks.
					powerMeterPaint.setStyle(Style.STROKE);
					for (int i = 0; i < METER_PEAKS; ++i)
					{
						if (meterPeakTimes[i] != 0)
						{
							// Fade the peak according to its age.
							long age = now - meterPeakTimes[i];
							float fac = 1f - ((float) age / (float) METER_PEAK_TIME);
							int alpha = (int) (fac * 255f);
							powerMeterPaint.setColor(METER_PEAK_COL | (alpha << 24));
							// Draw it in.
							canvas.drawCircle(canvasWidth / 2, canvasHeight, meterPeaks[i] * canvasWidth / 4, powerMeterPaint);
							//final float pp = (meterPeaks[i]) * bh;
							//canvas.drawRect(mx + pp - 1, by + gap, mx + pp + 3, by + bh - gap, paint);
							//canvas.drawRect(bx + gap, my + bh - pp + 1, bx + bw - gap, my + bh - pp - 3, drawPaint);
						}
					}
				}
			}
		}
		finally
		{
			// do this in a finally so that if an exception is thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (canvas != null)
				surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * Re-calculate the positions of the peak markers in the VU meter.
	 */
	private final void calculatePeaks(long now, float power, float prev)
	{
		// First, delete any that have been passed or have timed out.
		for (int i = 0; i < METER_PEAKS; ++i)
		{
			if (meterPeakTimes[i] != 0 && (meterPeaks[i] < power || now - meterPeakTimes[i] > METER_PEAK_TIME))
				meterPeakTimes[i] = 0;
		}

		// If the meter has gone up, set a new peak, if there's an empty
		// slot.  If there isn't, don't bother, because we would be kicking
		// out a higher peak, which we don't want.
		if (power > prev)
		{
			boolean done = false;

			// First, check for a slightly-higher existing peak.  If there
			// is one, just bump its time.
			for (int i = 0; i < METER_PEAKS; ++i)
			{
				if (meterPeakTimes[i] != 0 && meterPeaks[i] - power < 2.5)
				{
					meterPeakTimes[i] = now;
					done = true;
					break;
				}
			}

			if (!done)
			{
				// Now scan for an empty slot.
				for (int i = 0; i < METER_PEAKS; ++i)
				{
					if (meterPeakTimes[i] == 0)
					{
						meterPeaks[i] = power;
						meterPeakTimes[i] = now;
						break;
					}
				}
			}
		}

		// Find the highest peak value.
		meterPeakMax = 0f;
		for (int i = 0; i < METER_PEAKS; ++i)
			if (meterPeakTimes[i] != 0 && meterPeaks[i] > meterPeakMax)
				meterPeakMax = meterPeaks[i];
	}

	/**
	 * Determine whether the caller is on the surface's animation thread.
	 * 
	 * @return             The resource value.
	 */
	public boolean onSurfaceThread()
	{
		return Thread.currentThread() == animTicker;
	}

	/**
	 * Set the size of the table.
	 * 
	 * @param   format      The new PixelFormat of the surface.
	 * @param   width       The new width of the surface.
	 * @param   height      The new height of the surface.
	 */
	private void setSize(int format, int width, int height)
	{
		synchronized (surfaceHolder)
		{
			canvasWidth = width;
			canvasHeight = height;
		}
	}

	void updatePower(double power)
	{
		//synchronized (this)
		{
			//Log.d("PowerGauge", "power" + power);
			// Save the current level.  Clip it to a reasonable range.
			if (power > 1.0)
				power = 1.0;
			else if (power < 0.0)
				power = 0.0;
			currentPower = (float) power;

			// Get the previous power value, and add the new value into the
			// history buffer.  Re-calculate the rolling average power value.
			if (++historyIndex >= powerHistory.length)
				historyIndex = 0;
			prevPower = powerHistory[historyIndex];
			powerHistory[historyIndex] = (float) power;
			averagePower -= prevPower / METER_AVERAGE_COUNT;
			averagePower += (float) power / METER_AVERAGE_COUNT;

		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		((Activity) appContext).onTouchEvent(event);
		// Pass the event on to the controller
		return multiTouchController.onTouchEvent(event);
	}

	public Object getDraggableObjectAtPoint(PointInfo pt)
	{
		// IMPORTANT: to start a multitouch drag operation, this routine must
		// return non-null
		return this;
	}

	public void getPositionAndScale(Object obj, PositionAndScale objPosAndScaleOut)
	{
		// We aren't dragging any objects, so this doesn't do anything in this
		// app
	}

	public void selectObject(Object obj, PointInfo touchPoint)
	{
		// We aren't dragging any objects in this particular app, but this is
		// called when the point goes up (obj == null) or down (obj != null),
		// save the touch point info
		touchPointChanged(touchPoint);
	}

	public boolean setPositionAndScale(Object obj, PositionAndScale newObjPosAndScale, PointInfo touchPoint)
	{
		// Called during a drag or stretch operation, update the touch point
		// info
		touchPointChanged(touchPoint);
		return true;
	}

	/**
	 * Called when the touch point info changes, causes a redraw.
	 * 
	 * @param touchPoint
	 */
	private void touchPointChanged(PointInfo touchPoint)
	{
		// Take a snapshot of touch point info, the touch point is volatile
		mCurrTouchPoint.set(touchPoint);
		//invalidate();
	}

	// ******************************************************************** //
	// Private Classes.
	// ******************************************************************** //

	/**
	 * Base interface for the ticker we use to control the animation.
	 */
	private interface Ticker
	{
		// Stop this thread.  There will be no new calls to tick() after this.
		public void kill();

		// Stop this thread and wait for it to die.  When we return, it is
		// guaranteed that tick() will never be called again.
		// 
		// Caution: if this is called from within tick(), deadlock is
		// guaranteed.
		public void killAndWait();

		// Run method for this thread -- simply call tick() a lot until
		// enable is false.
		public void run();

		// Determine whether this ticker is still going.
		public boolean isAlive();
	}

	/**
	 * Looper-based ticker class.  This has the advantage that asynchronous
	 * updates can be scheduled by passing it a message.
	 */
	private class LoopTicker extends Thread implements Ticker
	{
		// Constructor -- start at once.
		private LoopTicker()
		{
			super("Surface Runner");
			Log.v(TAG, "Ticker: start");
			start();
		}

		// Post a tick.  An update will be done near-immediately on the
		// appropriate thread.
		public void post()
		{
			synchronized (this)
			{
				if (msgHandler == null)
					return;

				// Remove any delayed ticks.
				msgHandler.removeMessages(MSG_TICK);

				// Do a tick right now.
				msgHandler.sendEmptyMessage(MSG_TICK);
			}
		}

		// Stop this thread.  There will be no new calls to tick() after this.
		@Override
		public void kill()
		{
			Log.v(TAG, "LoopTicker: kill");

			synchronized (this)
			{
				if (msgHandler == null)
					return;

				// Remove any delayed ticks.
				msgHandler.removeMessages(MSG_TICK);

				// Do an abort right now.
				msgHandler.sendEmptyMessage(MSG_ABORT);
			}
		}

		// Stop this thread and wait for it to die.  When we return, it is
		// guaranteed that tick() will never be called again.
		// 
		// Caution: if this is called from within tick(), deadlock is
		// guaranteed.
		@Override
		public void killAndWait()
		{
			Log.v(TAG, "LoopTicker: killAndWait");

			if (Thread.currentThread() == this)
				throw new IllegalStateException("LoopTicker.killAndWait()" + " called from ticker thread");

			synchronized (this)
			{
				if (msgHandler == null)
					return;

				// Remove any delayed ticks.
				msgHandler.removeMessages(MSG_TICK);

				// Do an abort right now.
				msgHandler.sendEmptyMessage(MSG_ABORT);
			}

			// Wait for the thread to finish.  Ignore interrupts.
			if (isAlive())
			{
				boolean retry = true;
				while (retry)
				{
					try
					{
						join();
						retry = false;
					}
					catch (InterruptedException e)
					{
					}
				}
				Log.v(TAG, "LoopTicker: killed");
			}
			else
			{
				Log.v(TAG, "LoopTicker: was dead");
			}
		}

		@Override
		public void run()
		{
			Looper.prepare();

			msgHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					switch (msg.what)
					{
						case MSG_TICK:
							tick();
							if (!msgHandler.hasMessages(MSG_TICK))
								msgHandler.sendEmptyMessageDelayed(MSG_TICK, animationDelay);
							break;
						case MSG_ABORT:
							Looper.myLooper().quit();
							break;
					}
				}
			};

			// Schedule the first tick.
			msgHandler.sendEmptyMessageDelayed(MSG_TICK, animationDelay);

			// Go into the processing loop.
			Looper.loop();
		}

		// Message codes.
		private static final int	MSG_TICK	= 6;
		private static final int	MSG_ABORT	= 9;

		// Our message handler.
		private Handler				msgHandler	= null;
	}
}
