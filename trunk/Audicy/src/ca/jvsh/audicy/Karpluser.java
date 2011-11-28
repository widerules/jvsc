package ca.jvsh.audicy;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Karpluser
{
	// sampling frequency (Hz)
	private final int	fs			= 22050;

	private final float	T			= 1.0f / (float) fs;
	private float		signalt		= 2;

	// number of samples to compute
	private int			M			= (int) (signalt * fs);

	// initial feedback gain
	float				fbg_init[]	= { 0.002f, 0.00f };
	// slope of the feedback gain ramp
	float				fbg_slope[]	= { 0.006f, 0.00f };

	AudioTrack			track;
	private final float fo;

	public Karpluser(final float fo)
	{
		this.fo = fo;
		
		new Thread(new Runnable()
		{
			public void run()
			{
				float ffb[] = { 3.0f * fo, fo };

				// Pre-amp Gain and end decay envelope
				float pregain[] = new float[M];
				for (int i = 0; i < M; i++)
				{
					pregain[i] = (float) (6 * Math.exp(-(i * T) / 2.0));
				}

				// cutoff frequency for DC blocking filter
				float fco = fo / 100.0f;

				// ---------------Karplus-Strong Algorithm
				// Parameters------------------------

				// General KS Parameters
				// Coefficient of the low-pass filter
				float lpcoeff = 0.5f;
				// blend factor - setting to 1 gives string simulation,
				// otherwise it gives a drum simulator
				float b = 1;
				int bufsz = 1000;

				// delay length
				int N = (int) Math.floor(fs / fo);
				// fraction of the way between sample n and n+1 that we want
				float eta = fs / fo - N;

				float a = (1.0f - eta) / (1.0f + eta);
				// delay line buffer - max delay=bufsz - corresponds to a
				// frequency of 44.1Hz
				float buffer[] = new float[bufsz];

				// feedback loop parameters
				// delay length
				int Nfb[] = { (int) Math.floor(fs / ffb[0]), (int) Math.floor(fs / ffb[1]) };
				// fraction of the way between sample n and n+1 that we want
				float etafb[] = { (float) (Math.floor(fs / ffb[0]) - Nfb[0]), (float) (Math.floor(fs / ffb[1]) - Nfb[1]) };

				float afb = (1.0f - eta) / (1.0f + eta);

				// feedback delay line buffer - same conditions as buffer
				float fbbuffer1[] = new float[bufsz];
				float fbbuffer2[] = new float[bufsz];

				float fbg_max[] = { fbg_slope[0] + fbg_init[0], fbg_slope[1] + fbg_init[1] };

				// initialize gain vector
				float fbgain1[] = new float[M];
				float fbgain2[] = new float[M];

				for (int i = 0; i < M; i++)
				{
					fbgain1[i] = fbg_init[0] + fbg_slope[0] / 9 * i * T;
					fbgain2[i] = fbg_init[1] + fbg_slope[1] / 9 * i * T;
				}

				// Distortion Parameters

				// initialize clipping lookup table

				// the function will be defined from -2 to +2
				float maxsig = 2;

				// lookup table step size
				float ltstep = 0.0001f;
				int elements = 20000;
				float invals[] = new float[elements];
				for (int i = 0; i < elements; i++)
				{
					invals[i] = -1 + i * ltstep;
				}

				// define the lowest input value possible for the lookup gain
				float inlow = (float) (-Math.ceil(maxsig / ltstep) * ltstep);

				// main part of soft clipping function: y=x-(x^3)/3
				float softclip[] = new float[elements];
				for (int i = 0; i < elements; i++)
				{
					softclip[i] = invals[i] - (float) (Math.pow(invals[i], 3) / 3);
				}

				// now define a lookup table with our distortion function
				// defined above
				int lookup_size = (int) Math.ceil((maxsig - 1) / ltstep);
				float[] lookup = new float[2 * lookup_size + elements];
				int lookup_counter = 0;

				for (; lookup_counter < lookup_size; lookup_size++)
				{
					lookup[lookup_counter] = -2.0f / 3.0f;
				}
				for (; lookup_counter < lookup_size + elements; lookup_size++)
				{
					lookup[lookup_counter] = softclip[lookup_counter - lookup_size];
				}
				for (; lookup_counter < 2 * lookup_size + elements; lookup_size++)
				{
					lookup[lookup_counter] = 2.0f / 3.0f;
				}

				// DC Blocking Filter Parameters

				float wco = (float) (2 * Math.PI * fco / fs);
				// DC blocking filter coeff's - note that
				float bDC[] = { 1 / (1 + 0.5f * wco), -1 / (1 + 0.5f * wco) };

				// a's, b's as well as sign convention are
				// reversed in the Sullivan paper!
				float aDC[] = { 1, -bDC[0] * (1 - 0.5f * wco) };

				// -------------Pointer and delay memory
				// initializations---------------------
				// Each array index in the following arrays represents a string.

				// output pointer for KS delay line 'buffer'
				int outptr = 2;
				// input pointer for KS buffer
				int inptr = N + 1;
				// int inptr = N + 2;

				// buffer(outptr-1)
				float bufoptrm1 = buffer[0];

				float ykm1 = 0;

				// single delay for low-pass filter in KS algorithm
				float blendm1 = 0;

				// for DC blocking filter - single delayed output
				float yDC = 0;
				// for DC blocking filter - single delayed input
				float xDC = 0;
				// filter delay states for DC-blocking filter
				float zf = 0;

				// ouput pointer for feedback delay line 'fbbuffer'
				int[] fboutptr = { 2, 2 };
				// input pointer for feedback buffer
				int[] fbinptr = { Nfb[0] + 2, Nfb[1] + 2 };

				// fbbuffer(fboutptr-1)
				float[] fbbufm1 = { 0, 0 };
				// fb delay line output 1 sample ago
				float[] fbkm1 = { 0, 0 };

				float meanrand = 0;
				float[] initrand = new float[N];
				for (int i = 0; i < N; i++)
				{
					initrand[i] = (float) Math.random();
					// binary random data
					if (initrand[i] >= 0.5)
						initrand[i] = 0.99f;
					else
						initrand[i] = -0.99f;
					meanrand += initrand[i];
				}
				meanrand = meanrand / (float) N;
				for (int i = 0; i < N; i++)
				{
					initrand[i] = initrand[i] - meanrand;

					// inject random data into the buffer
					buffer[i] = initrand[i];
				}

				// initialize 1 string
				float str = 0;
				// output vector
				float[] y = new float[M];

				float temp;
				float randnum;
				float fbtemp[] = new float[2];
				float tempDC;
				for (int k = 0; k < M; k++)
				{
					// read interpolated delay line output
					str = a * (buffer[outptr] - ykm1) + bufoptrm1;

					// These will be str(k-1) once we loop
					ykm1 = str;

					// These will be x(k-1) once we loop
					bufoptrm1 = buffer[outptr];

					// ----------------------Low-Pass
					// Filter---------------------------------

					// Pre-gain must be sufficiently high.
					temp = str;
					randnum = (float) Math.random();

					if (randnum <= b)
					{
						// modifier w/ probability b
						str = lpcoeff * (str + blendm1);
					}
					else
					{
						// modifier w/ probability 1-b
						str = -lpcoeff * (str + blendm1);
					}
					// these will be str(k-1) once we loop
					blendm1 = temp;

					// all-pass interpolated delay line
					fbtemp[0] = afb * (fbbuffer1[fboutptr[0]] - fbkm1[0]) + fbbufm1[0];
					fbtemp[1] = afb * (fbbuffer2[fboutptr[1]] - fbkm1[1]) + fbbufm1[1];

					// inject the feedback signal back into the KS loops
					str = str + 0.5f * (fbtemp[0] + fbtemp[1]);
					// This will be the output of the delay line 1 sample ago
					// once we loop
					fbkm1[0] = fbtemp[0];
					fbkm1[1] = fbtemp[1];

					// This will be fbbuffer(fboutptr-1) once we loop
					fbbufm1[0] = fbbuffer1[fboutptr[0]];
					fbbufm1[1] = fbbuffer2[fboutptr[1]];

					// /-------------DC blocking
					// filter---------------------------------
					tempDC = bDC[0] * str + bDC[1] * xDC - aDC[1] * yDC;
					// /will be str(k-1) once we loop
					yDC = tempDC;
					// will be x(k-1) once we loop
					xDC = str;

					str = tempDC;

					// split the signal into the output and beginning of the
					// delay line
					buffer[inptr] = str;

					y[k] = str;

					// preamp gain
					y[k] = pregain[k] * y[k];

					// -----------------Distortion
					// Module------------------------------
					// apply the lookup table to the output to give it
					// distortion
					if (Math.abs(y[k]) > 1.5)
						y[k] = Math.signum(y[k]) * 2.0f / 3.0f;
					else
						y[k] = lookup[(int) Math.round(((y[k] - inlow) / ltstep)) + 1];

					// put the output into the feedback delay line
					fbbuffer1[fbinptr[0]] = fbgain1[k] * y[k];
					fbbuffer2[fbinptr[1]] = fbgain2[k] * y[k];

					// ------------------------pointer
					// increments----------------------------

					inptr++;
					outptr++;
					fbinptr[0]++;
					fbinptr[1]++;
					fboutptr[0]++;
					fboutptr[0]++;

					for (int n = 0; n < 2; n++)
					{
						// put the pointer back to the start of the delay line
						if (fbinptr[n] >= bufsz)
							fbinptr[n] = 0;

						// put the pointer back to the start of the delay line
						if (fboutptr[n] >= bufsz)
							fboutptr[n] = 1;

					}

					// put the pointer back to the start of the delay line
					if (outptr >= bufsz)
						outptr = 1;

					// put the pointer back to the start of the delay line
					if (inptr >= bufsz)
						inptr = 1;

				}

				float max = Math.abs(y[0]);
				for (int i = 1; i < y.length; i++)
					if (Math.abs(y[i]) > max)
						max = Math.abs(y[i]);
				for (int i = 0; i < y.length; i++)
				{
					y[i] = y[i] / max;
					// Log.i("y", " y " + i + " " + y[i]);
				}

				int lenght = y.length;
				short[] bufferOut = new short[lenght];

				for (int i = 0; i < y.length; i++)
					bufferOut[i] = (short) (y[i] * Short.MAX_VALUE);

				track = new AudioTrack(AudioManager.STREAM_MUSIC, fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * buffer.length, AudioTrack.MODE_STATIC);
				track.write(bufferOut, 0, y.length);
				track.play();
			}
		}).start();
	}

	/*
	 * private final int fs = 16000; private final int N = fs; private final
	 * float freq; AudioTrack track; short[] buffer;
	 * 
	 * public Karpluser(float freq1) { this.freq = freq1; new Thread(new
	 * Runnable() { public void run() { int D = (int) Math.round(0.5 * fs /
	 * freq);
	 * 
	 * float[] b_loss = new float[2]; b_loss[0] = b_loss[1] = -0.99f * 0.5f;
	 * 
	 * float[] y = new float[N]; for (int i = 0; i < y.length; i++) y[i] = 0;
	 * 
	 * float[][] dlines = new float[2][N];
	 * 
	 * for (int i = 0; i < 2; i++) for (int j = 0; j < N; j++) dlines[i][j] =
	 * (float) Math.random();
	 * 
	 * float[] x = new float[N]; for (int i = 0; i < x.length; i++) x[i] = 0;
	 * 
	 * int ptr = 0; float readloc = 0.1f;// 0 = left-end, 1 = right-end
	 * 
	 * int upreadptr = Math.round((1 - readloc) * (D - 1));// +1; int dwnreadptr
	 * = D - upreadptr;// +1;
	 * 
	 * float temp1 = 0; for (int n = 0; n < N; n++) { float temp =
	 * dlines[1][ptr]; y[n] = dlines[0][upreadptr]; y[n] = y[n] +
	 * dlines[1][dwnreadptr];
	 * 
	 * dlines[1][ptr] = -dlines[0][ptr];
	 * 
	 * dlines[0][ptr] = temp * b_loss[0] + temp1 * b_loss[1]; temp1 = temp;
	 * dlines[0][ptr] = dlines[0][ptr] + x[n];
	 * 
	 * // ****** Increment Pointers & Check Limits ****** ptr = ptr + 1;
	 * upreadptr = upreadptr + 1; dwnreadptr = dwnreadptr + 1; if (ptr >= D) ptr
	 * = 0;
	 * 
	 * if (upreadptr >= D) upreadptr = 0;
	 * 
	 * if (dwnreadptr >= D) dwnreadptr = 0;
	 * 
	 * }
	 * 
	 * // int minSize = AudioTrack.getMinBufferSize(fs, //
	 * AudioFormat.CHANNEL_CONFIGURATION_MONO, //
	 * AudioFormat.ENCODING_PCM_16BIT);
	 * 
	 * float max = Math.abs(y[0]); for (int i = 1; i < y.length; i++) if
	 * (Math.abs(y[i]) > max) max = Math.abs(y[i]); for (int i = 0; i <
	 * y.length; i++) { y[i] = y[i] / max; // Log.i("y", " y " + i + " " +
	 * y[i]); }
	 * 
	 * buffer = new short[y.length];
	 * 
	 * for (int i = 0; i < y.length; i++) buffer[i] = (short) (y[i] *
	 * Short.MAX_VALUE);
	 * 
	 * track = new AudioTrack(AudioManager.STREAM_MUSIC, fs,
	 * AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 2
	 * * buffer.length, AudioTrack.MODE_STATIC); track.write(buffer, 0,
	 * y.length); track.play(); } }).start();
	 * 
	 * }
	 */

}
