package ca.jvsh.audicy;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Karpluser extends Thread
{
	//////////////////////////////////////////////////////////////////
	//General Audio parameters
	//////////////////////////////////////////////////////////////////
	
	// sampling frequency (Hz)
	private final int	fs			= 22050;
	// sampling period
	private final float	T			= 1.0f / ( (float)fs);
	
	// length and  number of samples to compute before starting output 0;
	//calculating 9 seconds, then just output zeros
	private float		signalt		= 25;
	public int			M			= (int) (signalt * fs);

	
	///////////////////////////////////////////////////////////////////
	//thread control variable
	///////////////////////////////////////////////////////////////////
	// Must be volatile:
	private volatile boolean	stop			= false;

	//////////////////////////////////////////////////////////////////
	//audio track parameters
	//////////////////////////////////////////////////////////////////
	private AudioTrack			track;
	private short				mBuffer[];
	private int        BufferSize;
	
	
	//////////////////////////////////////////////////////////////
	//extended Karplus-Strong algorithm parameters
	//////////////////////////////////////////////////////////////
	// initial feedback gain
	float				fbg_init[]	= { 0.002f, 0.00f };
	// slope of the feedback gain ramp
	float				fbg_slope[]	= { 0.006f, 0.00f };
	
	float fbg_max[] = { 0.008f, 0.00f };
	//frequency to generate and play
	private float fo[] = new float[3];
	
	///////////////////////////////////////////////////////////////////
	//General Karplus-Strong Algorithm Parameters
	//////////////////////////////////////////////////////////////////

	// Coefficient of the low-pass filter
	float lpcoeff = 0.5f;
	
	// blend factor - setting to 1 gives string simulation,
	// otherwise it gives a drum simulator
	float b = 1.0f;
	int bufsz = 1000;
	
	// delay line buffer - max delay=bufsz - corresponds to a
	// frequency of 44.1Hz
	float buffer[][] = new float[3][bufsz];
	
	// feedback delay line buffer - same conditions as buffer
	float fbbuffer1[] = new float[bufsz];
	float fbbuffer2[] = new float[bufsz];
	
	///////////////////////////////////////////////////////////////////
	//Frequency dependent parameters
	//////////////////////////////////////////////////////////////////////
	
	
	////frequencies favoured for feedback (Hz)
	float ffb[] = new float [2];
	
	// cutoff frequency for DC blocking filter
	float fco;
	
	// delay length
	int N[] = new int[3] ;
	// fraction of the way between sample n and n+1 that we want
	float eta[] = new float[3];

	float a[] = new float[3];	
	
	//////////////////////////////////////////////////////////////
	// feedback loop parameters
	//////////////////////////////////////////////////////////////
	
	// delay length
	int Nfb[] = new int[2];
	float etafb[] = new float [2];
	float afb[] = new float [2];
	
	////////////////////////////////////////////////////////////////
	//initialize clipping lookup table
	////////////////////////////////////////////////////////////////

	// the function will be defined from -2 to +2
	float maxsig = 2;
	
	// lookup table step size
	float ltstep = 0.0001f;
	
	// define the lowest input value possible for the lookup gain
	float inlow = -2.0f;
	
	// now define a lookup table with our distortion function
	// defined above
	float[] lookup = new float[40000];
	
	/////////////////////////////////////////////////////////////////////
	// DC Blocking Filter Parameters
	////////////////////////////////////////////////////////////////////
	float wco;
	
	// DC blocking filter coeff's - note that
	float bDC[] = new float[2];
	
	// a's, b's as well as sign convention are
	// reversed in the Sullivan paper!
	float aDC[] = new float[2];
	
	
	///////////////////////////////////////////////////////////////////////
	//Pointer and delay memory initializations
	///////////////////////////////////////////////////////////////////////
	
	// output pointer for KS delay line 'buffer'
	int outptr [] = new int[3];
	
	// input pointer for KS buffer
	int inptr[] = new int[3];
	
	// buffer(outptr-1)
	float bufoptrm1[] = new float[3];
	
	float ykm1 [] = new float[3];

	// single delay for low-pass filter in KS algorithm
	float blendm1[] = new float[3];

	// for DC blocking filter - single delayed output
	float yDC[] = new float[3];
	// for DC blocking filter - single delayed input
	float xDC[] = new float[3];

	// ouput pointer for feedback delay line 'fbbuffer'
	int[] fboutptr = new int[2];
	
	// input pointer for feedback buffer
	int[] fbinptr = new int[2];
	
	// fbbuffer(fboutptr-1)
	float[] fbbufm1 = new float [2];
	// fb delay line output 1 sample ago
	float[] fbkm1= new float [2];
	
	
	/////////////////////////////////////////////////////////////////////
	//random data
	////////////////////////////////////////////////////////////////////
	float meanrand[] = new float[3];
	float initrand[][] = new float[3][];
	
	///////////////////////////////////////////////////////////////////
	//for main cycle
	///////////////////////////////////////////////////////////////////
	
	// initialize 1 string
	float str[] = new float[3];
	
	//main filling counter
	int k = 0;
	
	float temp[] = new float[3];

	float fbtemp[] = new float[2];
	float tempDC[] = new float[3];
	float y;
	private static final Random random = new Random();
	
	private final ReentrantLock l = new ReentrantLock();
	
	public Karpluser()
	{
	
		//choose buffer to be twice as bigger as a minimum size
		BufferSize = AudioTrack.getMinBufferSize(fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		track = new AudioTrack(AudioManager.STREAM_MUSIC, fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, BufferSize, AudioTrack.MODE_STREAM);

		//allocate buffer for audio track
		mBuffer = new short [BufferSize];
		
		//initialization of frequency independent parameters is done above
		
		//init lookup table
		
		for(int lookup_counter = 0; lookup_counter < 10000; lookup_counter++ )
			lookup[lookup_counter] = - 0.66666667f;
		
		for(int lookup_counter = 0; lookup_counter < 20000; lookup_counter++ )
		{
			lookup[lookup_counter + 10000] = (-1.0f + lookup_counter * ltstep);
			// main part of soft clipping function: y=x-(x^3)/3
			lookup[lookup_counter + 10000] = lookup[lookup_counter + 10000] -
					(lookup[lookup_counter + 10000] * lookup[lookup_counter + 10000] * lookup[lookup_counter + 10000]) / 3.0f;
		}
		for(int lookup_counter = 0; lookup_counter < 10000; lookup_counter++ )
			lookup[lookup_counter + 30000] = 0.66666667f;

	}
	
	
	
	/**
	 * Function that initializes frequency dependent parameters
	 */
	public void setFrequency (float freq)
	{
		l.lock();

		fo[0] = freq;
		fo[1] = freq;
		fo[2] = freq;
		
		//frequencies favoured for feedback (Hz)
		ffb[0] = 3.0f*fo[0];
		ffb[1] = fo[0];
		
		// cutoff frequency for DC blocking filter
		fco = fo[0] / 100.0f;
		
		// delay length
		for(int i = 0; i < 3; i ++)
		{
			N[i] = (int) Math.floor(fs / fo[i]);
			// fraction of the way between sample n and n+1 that we want
			eta[i] = fs / fo[i] - N[i];
	
			a[i] = (1.0f - eta[i]) / (1.0f + eta[i]);
			for(int j = 0; j < bufsz; j++)
			{
				buffer[i][j] = 0;
			}
		}
		for(int j = 0; j < bufsz; j++)
		{
		fbbuffer1[j] = 0;
		fbbuffer2[j] = 0;
		}
		
		// feedback loop parameters
		// delay length
		Nfb[0] = (int) Math.floor(fs / ffb[0]);
		Nfb[1] = (int) Math.floor(fs / ffb[1]);
		etafb[0] = (fs / ffb[0]) - (float) (Nfb[0]);
		etafb[1] = (fs / ffb[1]) - (float) (Nfb[1]);
		
		afb[0] = (1.0f - etafb[0]) / (1.0f + etafb[0]);
		afb[1] = (1.0f - etafb[1]) / (1.0f + etafb[1]);
		
		// DC Blocking Filter Parameters
		wco = (float) (2 * Math.PI * fco / fs);
		bDC[0] = 1.0f / (1.0f + 0.5f * wco);
		bDC[1] = -bDC[0];
		
		aDC[0] = 1;
		aDC[1] = -bDC[0] * (1 - 0.5f * wco);
		
		//Pointer and delay memory initializations
		for(int i = 0; i < 3; i ++)
		{
			outptr[i] = 1;
			inptr[i] = N[i] + 1;
			bufoptrm1[i] = buffer[i][0];
			ykm1[i] = 0;
		}
		
		// input pointer for feedback buffer
		fbinptr[0] =  Nfb[0];
		fbinptr[1] =  Nfb[1];
		fbbufm1[0] = 0;
		fbbufm1[1] = 0;
		fbkm1[0] = 0;
		fbkm1[1] = 0;
		fboutptr[0] = 1;
		fboutptr[1] = 1;
		
		////////////////////////////////////////////////////////////////////
		//calculate random data
		////////////////////////////////////////////////////////////////////
		for(int j = 0; j < 3; j++)
		{
			meanrand[j] = 0;
			initrand[j] = new float[N[j]];
			for (int i = 0; i < N[j]; i++)
			{
				initrand[j][i] = random.nextFloat();
				
				// binary random data
				if (initrand[j][i] >= 0.5f)
					initrand[j][i] = 0.99f;
				else
					initrand[j][i] = -0.99f;
				
				meanrand[j] += initrand[j][i];
			}
			meanrand[j] = meanrand[j] /( (float) N[j]);
		
			// inject random data into the buffer
			for (int i = 0; i < N[j]; i++)
			{
				buffer[j][i] = initrand[j][i] - meanrand[j];
			}
		}
		//set main counter back to zero
		k = 0;
		
		
		//set other counters to zero
		for(int j = 0; j < 3; j++)
		{
			blendm1[j] = 0;
			yDC[j] = 0;
			xDC[j] = 0;
			temp[j] = 0;
			tempDC[j] = 0;
		}

		fbtemp[0] = 0;
		fbtemp[1] = 0;
		y = 0;

		l.unlock();
	}


	
	public void run()
	{
		track.play();

		while (!stop)
		{
			writeSamples();
		}
	}
	
	public void writeSamples()
	{
		l.lock();
		if(k < M)
		{
			for (int i = 0; i < BufferSize; i++)
			{
				
				// read interpolated delay line output
				for(int j = 0; j < 3; j++)
				{
					str[j] = a[j] * (buffer[j][outptr[j]] - ykm1[j]) + bufoptrm1[j];
				
				
					// These will be str(k-1) once we loop
					ykm1[j] = str[j];
					
					// These will be x(k-1) once we loop
					bufoptrm1[j] = buffer[j][outptr[j]];
				
					// ------------- Low-Pass Filter --------------------
					
					// Pre-gain must be sufficiently high.
					temp[j] = str[j];
					
					if (random.nextFloat() <= b)
					{
						// modifier w/ probability b
						str[j] = lpcoeff * (str[j] + blendm1[j]);
					}
					else
					{
						// modifier w/ probability 1-b
						str[j] = -lpcoeff * (str[j] + blendm1[j]);
					}
					
					// these will be str(k-1) once we loop
					blendm1[j] = temp[j];
				}
				// all-pass interpolated delay line
				fbtemp[0] = afb[0] * (fbbuffer1[fboutptr[0]] - fbkm1[0]) + fbbufm1[0];
				fbtemp[1] = afb[0] * (fbbuffer2[fboutptr[1]] - fbkm1[1]) + fbbufm1[1];
				
				// inject the feedback signal back into the KS loops
				for(int j = 0; j < 3; j++)
				{
					str[j] = str[j] + 0.5f * (fbtemp[0] + fbtemp[1]);
				}
				// This will be the output of the delay line 1 sample ago
				// once we loop
				fbkm1[0] = fbtemp[0];
				fbkm1[1] = fbtemp[1];

				// This will be fbbuffer(fboutptr-1) once we loop
				fbbufm1[0] = fbbuffer1[fboutptr[0]];
				fbbufm1[1] = fbbuffer2[fboutptr[1]];
				
				
				//------------------------DC blocking filter -----------------------------
				for(int j = 0; j < 3; j++)
				{
					tempDC[j] = bDC[0] * str[j] + bDC[1] * xDC[j] - aDC[1] * yDC[j];
					// /will be str(k-1) once we loop
					yDC[j] = tempDC[j];
					// will be x(k-1) once we loop
					xDC[j] = str[j];
	
					str[j] = tempDC[j];
				

					// split the signal into the output and beginning of the
					// delay line
					buffer[j][inptr[j]] = str[j];
				}
				
				y = 1.0f/3.0f *(str[0]+str[1]+str[2]);
				//apply preamp gain 
				if(k > (signalt-10)*fs)
					y *= (float) (6.0f * Math.exp(-((k-(signalt-10)*fs) * T) / 2.0f) );
				else
					y *= 6.0f;
				
				//--------------------------- Distortion Module --------------------
				// apply the lookup table to the output to give it distortion
				if (Math.abs(y) > 1.5f)
					y = Math.signum(y) * 0.66666667f;
				else
					y = lookup[(int) Math.round(((y - inlow) / ltstep)) + 1];

				// put the output into the feedback delay line
				if(k < 9* fs)
				{
					fbbuffer1[fbinptr[0]] = fbg_init[0] + fbg_slope[0] / 9 * k * T * y;
					fbbuffer2[fbinptr[1]] = fbg_init[1] + fbg_slope[1] / 9 * k * T * y;
				}
				else
				{
					fbbuffer1[fbinptr[0]] = fbg_max[0];
					fbbuffer2[fbinptr[1]] = fbg_max[1];
				}
				
				
				// ------------------------pointer  increments----------------------------
				for(int j = 0; j < 3; j++)
				{
					inptr[j]++;
					outptr[j]++;
				}
				fbinptr[0]++;
				fbinptr[1]++;
				fboutptr[0]++;
				fboutptr[0]++;
				
				//increment main pointer
				k++;
				
				
				for (int n = 0; n < 2; n++)
				{
					// put the pointer back to the start of the delay line
					if (fbinptr[n] >= bufsz)
						fbinptr[n] = 0;

					// put the pointer back to the start of the delay line
					if (fboutptr[n] >= bufsz)
						fboutptr[n] = 0;

				}

				for(int j = 0; j < 3; j++)
				{
					// put the pointer back to the start of the delay line
					if (outptr[j] >= bufsz)
						outptr[j] = 0;
	
					// put the pointer back to the start of the delay line
					if (inptr[j] >= bufsz)
						inptr[j] = 0;
				}
				//set up the output buffer
				mBuffer[i] = (short) (y * Short.MAX_VALUE);
				//Log.i("y ", "y " + y);
			}
		}
		else
		{
			for (int i = 0; i < BufferSize; i++)
				mBuffer[i]  = 0;
		}
		l.unlock();
		track.write(mBuffer, 0, BufferSize);
	}
	
	
	public synchronized void requestStop()
	{
		track.stop();
		stop = true;
	}
	
	public void setK(int k)
	{
		l.lock();
		this.k = k;
		l.unlock();
	}

}
