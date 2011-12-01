package ca.jvsh.audicy;

public class Tone
{
	////////////////////////////////////////////////////////////////////
	//tone variable
	////////////////////////////////////////////////////////////////////
	private int			delta;
	private float		numDecay;

	private float		phase;
	private float		phaseIncr;

	private int			bufferSize;
	private float		amplitude;
	private float		env;
	private float		tmp;
	
	public boolean end;

	public Tone(int fs, int bufferSize, int delta, int note, int octave)
	{
		this.delta = delta;
		this.bufferSize = bufferSize;
		end = false;

		numDecay = 20000;

		phase = 0.0f;
		phaseIncr = 110.0f * (float) Math.pow(2.0, octave + note / 2.0f) / (float) (fs);
	}

	public void processAudio(float buffer[])
	{

		for (int i = delta; i < bufferSize; i++)
		{
			env = (float) numDecay / 20000.0f;

			if (phase < 0.5f)
			{
				tmp = (phase * 4.0f - 1.0f);
				amplitude = (1.0f - tmp * tmp) * env * env * 0.5f;
			}
			else
			{
				tmp = (phase * 4.0f - 3.0f);
				amplitude = (tmp * tmp - 1.0f) * env * env * 0.5f;
			}

			phase += phaseIncr;

			if (phase >= 1)
				--phase;

			buffer[i] += amplitude;

			if (--numDecay <= 0)
			{
				end = true;
				return;
			}
		}

		delta = 0;

		end = false;
		return;
	}
}
