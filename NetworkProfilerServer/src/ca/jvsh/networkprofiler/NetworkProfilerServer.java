package ca.jvsh.networkprofiler;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class NetworkProfilerServer
{

	protected Shell	shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			NetworkProfilerServer window = new NetworkProfilerServer();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open()
	{
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);

				messageBox.setText("Warning");
				messageBox.setMessage("Save the changes before exiting?");
				int buttonID = messageBox.open();
			}
		});
		btnNewButton.setBounds(0, 0, 75, 25);
		btnNewButton.setText("New Button");

	}

}
