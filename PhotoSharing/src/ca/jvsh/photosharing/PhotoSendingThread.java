package ca.jvsh.photosharing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.Message;
import android.util.Log;

public class PhotoSendingThread implements Runnable
{
	private String		filepath;
	private InetAddress	ip;
	private Integer		port;
	private boolean		tcpOnly;

	public PhotoSendingThread(String filepath, InetAddress ip, Integer port, boolean tcpOnly)
	{
		this.filepath = filepath;
		this.ip = ip;
		this.port = port;
		this.tcpOnly = tcpOnly;
	}

	public void run()
	{
		if (tcpOnly)
		{
			Socket TcpSocket = null;
			try
			{
				TcpSocket = new Socket();
				TcpSocket.connect(new InetSocketAddress(ip, port), 2000);

				//send an image
				FileInputStream fileInputStream = new FileInputStream(filepath);
				OutputStream os = TcpSocket.getOutputStream();
				int nRead;
				byte[] data = new byte[16384];

				try
				{
					while ((nRead = fileInputStream.read(data, 0, data.length)) != -1)
					{
						os.write(data, 0, nRead);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				fileInputStream.close();

				TcpSocket.close();
			}
			catch (SocketTimeoutException ex)
			{
				Log.d(PhotoSendingThread.class.getName(), "SocketTimeoutException: Client can't connect to server with ip " + ip + " on port " + port);

				/*Message m = new Message();
				m.what = MSG_CANT_CONNECT;
				m.obj = mContext;
				mToastHandler.sendMessage(m);*/

				TcpSocket = null;
				ex.printStackTrace();
				return;
			}
			catch (UnknownHostException ex)
			{
				Log.d(PhotoSendingThread.class.getName(), "UnknownHostException: Client can't connect to server with ip " + ip + " on port " + port);

				/*Message m = new Message();
				m.what = MSG_CANT_CONNECT;
				m.obj = mContext;
				mToastHandler.sendMessage(m);*/

				TcpSocket = null;
				ex.printStackTrace();
				return;
			}
			catch (IOException ex)
			{
				Log.d(PhotoSendingThread.class.getName(), "IOException: Client can't connect to server with ip " + ip + " on port " + port);

				/*Message m = new Message();
				m.what = MSG_CANT_CONNECT;
				m.obj = mContext;
				mToastHandler.sendMessage(m);*/

				TcpSocket = null;
				ex.printStackTrace();

				return;
			}
			finally
			{
				TcpSocket = null;
			}

			/*for (int i = 0; i < mTestingThreads.size(); i++)
			{
				mTestingThreads.get(i).setupSocket(mServerIp, mServerOpenPort, ClientFragment.this, false);
				mTestingThreads.get(i).start();
			}

					case R.id.radioClientUdp:
						
						DatagramSocket UdpSocket = null;
						try
						{
							UdpSocket = new DatagramSocket();
							UdpSocket.connect(new InetSocketAddress(mServerIp, mServerOpenPort));

							UdpSocket.close();

						}
						catch (SocketException ex)
						{
							Log.d(PhotoSendingThread.class.getName(), "SocketException: Client can't connect to server with ip " + mServerIp + " on port " + mServerOpenPort);

							Message m = new Message();
							m.what = MSG_CANT_CONNECT;
							m.obj = mContext;
							mToastHandler.sendMessage(m);

							UdpSocket = null;
							ex.printStackTrace();
							return;
						}

						finally
						{
							UdpSocket = null;
						}
						

						for (int i = 0; i < mTestingThreads.size(); i++)
						{
							mTestingThreads.get(i).setupSocket(mServerIp, mServerOpenPort, ClientFragment.this, true);
							mTestingThreads.get(i).start();
						}

						break;*/
		}
		else
		{

		}
	}
}
