package ca.jvsh.audicy;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Karpluser
{
	private final int			fs	= 22050;
	private final int 			N = 22050; 
	
	AudioTrack					track;
	short[]						buffer;

	public Karpluser(float freq)
	{
		int D = (int) Math.round(0.5*fs/freq);
		
		float [] b_loss = new float[2];
		b_loss[0] = b_loss[1] =  -0.99f*0.5f;

	
		float[] y = new float [N];
		for (int i = 0; i < y.length; i++)
			y[i] = 0;
			
		float[][] dlines = new float [2][N];
		
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < N; j++)
				dlines[i][j] = (float) Math.random();
		
		float[] x = new float[N];
		for (int i = 0; i < x.length; i++)
			x[i] = 0;

		int ptr = 0;
		float readloc = 0.1f;// 0 = left-end, 1 = right-end
	
		int upreadptr = Math.round((1-readloc)*(D-1));//+1;
		int dwnreadptr = D-upreadptr;//+1;

		float temp1= 0;
		for(int n = 0; n < N; n++)
		{
			float temp = dlines[1] [ptr];
			y [n] = dlines[0][upreadptr];
			y[n] = y[n] + dlines[1][dwnreadptr];
			
			dlines[1][ptr] = -dlines[0][ptr]; 
			
			dlines[0][ptr] = temp * b_loss[0] + temp1 *  b_loss[1];
			temp1 = temp;
			dlines[0][ptr] = dlines[0][ptr] + x[n];
			
			//****** Increment Pointers & Check Limits ******
			 ptr = ptr + 1;
			 upreadptr = upreadptr + 1;
			 dwnreadptr = dwnreadptr + 1;
			 if (ptr >= D)
				ptr = 0;
			
			  if (upreadptr >= D)
				upreadptr = 0;
			
			  if (dwnreadptr >= D)
				dwnreadptr = 0;
			
		}
	
	
		//int minSize = AudioTrack.getMinBufferSize(fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);

		float max = Math.abs(y[0]);
		for (int i = 1; i < y.length; i++)
			if( Math.abs(y[i]) > max)
				max = Math.abs(y[i]);
		for (int i = 0; i < y.length; i++)
		{
			y[i] = y[i]/ max;
			//Log.i("y", " y " + i + " " + y[i]);
		}
			
			
		
		buffer = new short[y.length];

		for (int i = 0; i < y.length; i++)
			buffer[i] = (short) (y[i] * Short.MAX_VALUE);

		track = new AudioTrack(AudioManager.STREAM_MUSIC, fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * buffer.length, AudioTrack.MODE_STATIC);
		track.write(buffer, 0, y.length);
		track.play();
	}

	


}
