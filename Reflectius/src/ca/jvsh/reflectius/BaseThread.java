/*
 * Project: 24ClockWidget
 * Copyright (C) 2009 ralfoide gmail com,
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.jvsh.reflectius;

import android.util.Log;

//-----------------------------------------------

public abstract class BaseThread extends Thread
{

	private static final String	TAG			= "BaseThread";

	protected boolean			mContinue	= true;

	public BaseThread(String name)
	{
		super(name);
		this.setPriority(Thread.currentThread().getPriority() + 1);
	}

	/**
	 * Called by the main activity when it's time to stop.
	 * <p/>
	 * Requirement: this MUST be called from another thread, not from GLBaseThread.
	 * The activity thread is supposed to called this.
	 * <p/>
	 * This lets the thread complete it's render loop and wait for it
	 * to fully stop using a join.
	 */
	public void waitForStop()
	{
		// Not synchronized. Setting one boolean is assumed to be atomic.
		mContinue = false;

		try
		{
			assert Thread.currentThread() != this;
			wakeUp();
			join();
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, "Thread.join failed", e);
		}
	}

	/**
	 * Starts the thread if it wasn't already started.
	 * Does nothing if started.
	 */
	@Override
	public synchronized void start()
	{
		if (getState() == State.NEW)
		{
			super.start();
		}
	}

	// -----------------

	/**
	 * Base implementation of the thread loop.
	 * Derived classes can re-implement this, as long as they follow this
	 * contract:
	 * - the loop must continue whilst mContinue is true
	 * - each iteration must invoke runIteration() when not paused.
	 * - the loop must pause when mIsPaused is true by first waiting for
	 *   the pause barrier and do a waitForALongTime() until mIsPaused is
	 *   released.
	 */
	@Override
	public void run()
	{

		try
		{
			startRun();

			while (mContinue)
			{
				runIteration();
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "Run-Loop Exception, stopping thread", e);
		}
		finally
		{
			endRun();
		}
	}

	protected void startRun()
	{
	}

	/**
	 * Performs one iteration of the thread run loop.
	 * Implementations must implement this and not throw exceptions from it.
	 */
	protected abstract void runIteration();

	protected void endRun()
	{
	}

	protected void setCompleted()
	{
		mContinue = false;
	}

	protected void wakeUp()
	{
		this.interrupt();
	}

	protected void waitFor(long time_ms)
	{
		try
		{
			synchronized (this)
			{
				if (time_ms > 0)
					wait(time_ms);
			}
		}
		catch (InterruptedException e)
		{
			// pass
		}

	}
}
