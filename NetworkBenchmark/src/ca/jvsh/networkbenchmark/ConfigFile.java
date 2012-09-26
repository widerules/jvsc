package ca.jvsh.networkbenchmark;

public class ConfigFile
{
	public TestingThread[]	threads;

	public ConfigFile(int num_threads)
	{
		threads = new TestingThread[num_threads];
	}

	public class TestingThread
	{
		public TestingSequence	sequences[];

		public TestingThread(int num_sequences)
		{
			sequences = new TestingSequence[num_sequences];
		}

		public class TestingSequence
		{
			public int	time_total;
			public int	bytes_send;
			public int	delay_ms;

			public TestingSequence()
			{

			}
		}
	};
};