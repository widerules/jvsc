package ca.jvsh.andorion;

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

public class AndorionView extends SurfaceView implements MultiTouchObjectCanvas<Object>, SurfaceHolder.Callback
{
	private MultiTouchController<Object>	multiTouchController;
	private PointInfo						mCurrTouchPoint;

	private static final int[]				mTouchPointColors	= { 0xFFF2E3B6, 0xFFBCD5B0, 0xFF76A68B, 0xFF898C70, 0xFFBF5F56, 0xFF66202C, 0xFF8F4A3C, 0xFFAB7245, 0xFFB59E65, 0xFFD4CE76 };

	private final Paint						mPaint				= new Paint();

	// Width, height and pixel format of the surface.
	private int								canvasWidth			= 0;
	private int								canvasHeight		= 0;

	// Application handle.
	private Context							appContext;

	// The surface manager for the view.
	private SurfaceHolder					surfaceHolder		= null;

	// Debugging tag.
	private static final String				TAG					= "AndorionView";
	
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

	private int								mBufferSize;
	int										fs					= 44100;

	short[]									buffer;

	private static boolean					mActive				= false;

	Thread									soundThread;
	

	/////////////////////////////////////////////////////////////////////////

	public AndorionView(Context context)
	{
		this(context, null);
	}

	public AndorionView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public AndorionView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		appContext = context;

		multiTouchController = new MultiTouchController<Object>(this);
		mCurrTouchPoint = new PointInfo();

		// Register for events on the surface.
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		setFocusable(true);
		setFocusableInTouchMode(true);

		mOutBufferSize = AudioTrack.getMinBufferSize(fs, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		mAudioOutput = new AudioTrack(AudioManager.STREAM_MUSIC, fs, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * mOutBufferSize, AudioTrack.MODE_STREAM);

		mBufferSize = (int) mOutBufferSize;

		buffer = new short[mBufferSize];
		

		// Set up our paint.
		mPaint.setAntiAlias(true);
	}

	protected void animStart()
	{
		mActive = true;
		soundThread = new Thread()
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

				
				instrumentSound();
			}
		};
		soundThread.start();
	}

	protected void animStop()
	{
		mActive = false;
		soundThread = null;
	}

	void instrumentSound()
	{
		
		try
		{

			while (mActive)
			{
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
				canvas.drawColor(Color.BLACK);
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
