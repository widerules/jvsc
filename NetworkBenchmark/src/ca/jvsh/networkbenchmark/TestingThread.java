package ca.jvsh.networkbenchmark;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

public class TestingThread extends Thread
{
	public SparseArray<TestingSequence>		mTestingSequences;
	private int					mId;
	public boolean				mNetworkThreadActive	= false;

	private int					mServerOpenPort;
	private InetAddress			mServerIp;

	ClientFragment mClientFragment;
	private static final String	TAG						= "TestingThread";

	public TestingThread(int num_sequences, int id)
	{
		mTestingSequences = new SparseArray<TestingSequence>(num_sequences);
		mId = id;
	}

	public void setupSocket(InetAddress ip, int port, ClientFragment clientFragment)
	{
		mServerIp = ip;
		mServerOpenPort = port;
		mClientFragment = clientFragment;
	}

	public void run()
	{
		mNetworkThreadActive = true;
		Socket socket = null;

		boolean bFirstFlag = true;
		int sequenceId = 0;

		long sleepMilliseconds;
		long timeStart = System.currentTimeMillis(), timeStop;
		long sendStart, sendStop;

		byte[] data = null;

		while (mNetworkThreadActive)
		{
			sendStart = System.currentTimeMillis();

			TestingSequence sequence = mTestingSequences.get(sequenceId);
			if (sequence.repeat != 0)
			{

				if (bFirstFlag)
				{
					timeStart = System.currentTimeMillis();
					data = new byte[sequence.bytes_send];
					bFirstFlag = false;
				}

				if (sequence.bytes_send == 0)
				{
					try
					{
						Thread.sleep(sequence.time_total, 0);
					}
					catch (InterruptedException e)
					{
						Log.d(TAG, "InterruptedException while sleep");
						e.printStackTrace();
						e.printStackTrace();
					}
					timeStop = System.currentTimeMillis();
				}
				else
				{

					try
					{
						socket = new Socket(mServerIp, mServerOpenPort);

						OutputStream os = socket.getOutputStream();
						if (data != null)
							os.write(data);
						socket.close();

						mClientFragment.addSendedBytes(sequence.bytes_send);
					}
					catch (IOException e)
					{
						Log.d(TAG, "Can't open socket on thread with id " + mId + " on ip " + mServerIp.toString() + " and port " + mServerOpenPort);
						e.printStackTrace();
						return;
					}

					timeStop = sendStop = System.currentTimeMillis();
					sleepMilliseconds = (int) (sequence.delay_ms - (sendStop - sendStart));
					if (sleepMilliseconds > 0)
					{
						try
						{
							Thread.sleep(sleepMilliseconds, 0);
						}
						catch (InterruptedException e)
						{
							Log.d(TAG, "InterruptedException while sleep");
							e.printStackTrace();
							e.printStackTrace();
						}
					}

				}

				if ((timeStop - timeStart) > sequence.time_total)
				{
					if (sequence.repeat > 0)
						sequence.repeat--;

					sequenceId++;
					if (sequenceId >= mTestingSequences.size())
						sequenceId = 0;
					bFirstFlag = true;

				}
			}
			else
			{
				sequenceId++;
				if (sequenceId >= mTestingSequences.size())
					sequenceId = 0;
			}

		}
	}

	public class TestingSequence
	{
		public int	time_total;
		public int	bytes_send;
		public int	delay_ms;
		public int	repeat;

		public TestingSequence()
		{
			repeat = -1;
		}
	}
};
