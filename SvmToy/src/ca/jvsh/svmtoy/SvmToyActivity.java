package ca.jvsh.svmtoy;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SvmToyActivity extends Activity
{
	/** Called when the activity is first created. */
	private static final String	TAG		= "SvmToyActivity";

	private DotSurfaceView		mDotSurfaceView;

	private EditText			mEdit;
	//buttons
	private Button				mButtonChange;
	private Button				mButtonRun;
	private Button				mButtonClear;

	private final int			COLORS	= 3;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_svm_toy);

		mButtonChange = (Button) findViewById(R.id.button_change);
		mButtonChange.setOnClickListener(changeListener);

		mButtonRun = (Button) findViewById(R.id.button_run);
		mButtonRun.setOnClickListener(runListener);

		mButtonClear = (Button) findViewById(R.id.button_clear);
		mButtonClear.setOnClickListener(clearListener);

		mEdit = (EditText) findViewById(R.id.edit_params);
		mDotSurfaceView = (DotSurfaceView) findViewById(R.id.surfaceView_draw);
	}

	private OnClickListener	changeListener	= new OnClickListener()
											{
												public void onClick(View v)
												{
													mDotSurfaceView.mColorSwitch++;
													mDotSurfaceView.mColorSwitch %= COLORS;

													switch (mDotSurfaceView.mColorSwitch)
													{
														case 0:
															mDotSurfaceView.dotColor = Color.rgb(0, 120, 120);
															mButtonChange.setBackgroundColor(Color.rgb(0, 120, 120));
															break;
														case 1:
															mDotSurfaceView.dotColor = Color.rgb(120, 120, 0);
															mButtonChange.setBackgroundColor(Color.rgb(120, 120, 0));
															break;
														case 2:
															mDotSurfaceView.dotColor = Color.rgb(120, 0, 120);
															mButtonChange.setBackgroundColor(Color.rgb(120, 0, 120));
															break;
													}

												}
											};

	private OnClickListener	runListener		= new OnClickListener()
											{
												public void onClick(View v)
												{
													if (mDotSurfaceView.mListLabels.isEmpty())
														Toast.makeText(SvmToyActivity.this, "There are no dots on the surface", Toast.LENGTH_LONG).show();

													svm_parameter param = new svm_parameter();

													// default values
													param.svm_type = svm_parameter.C_SVC;
													param.kernel_type = svm_parameter.RBF;
													param.degree = 3;
													param.gamma = 0;
													param.coef0 = 0;
													param.nu = 0.5;
													param.cache_size = 100;
													param.C = 100;
													param.eps = 1e-3;
													param.p = 0.1;
													param.shrinking = 1;
													param.probability = 0;
													param.nr_weight = 0;
													param.weight_label = new int[0];
													param.weight = new double[0];

													// build problem
													svm_problem prob = new svm_problem();
													prob.l = mDotSurfaceView.mListLabels.size();
													prob.y = new double[prob.l];

													if (param.kernel_type == svm_parameter.PRECOMPUTED)
													{
													}
													else if (param.svm_type == svm_parameter.EPSILON_SVR ||
															param.svm_type == svm_parameter.NU_SVR)
													{
														/*if(param.gamma == 0) param.gamma = 1;
														prob.x = new svm_node[prob.l][1];
														for(int i=0;i<prob.l;i++)
														{
															
															prob.x[i][0] = new svm_node();
															prob.x[i][0].index = 1;
															prob.x[i][0].value = mDotSurfaceView.mListX.get(i);
															prob.y[i] = mDotSurfaceView.mListY.get(i);
														}

														// build model & classify
														svm_model model = svm.svm_train(prob, param);
														svm_node[] x = new svm_node[1];
														x[0] = new svm_node();
														x[0].index = 1;
														int[] j = new int[mDotSurfaceView.mWidth];

														//Graphics window_gc = getGraphics();
														for (int i = 0; i < mDotSurfaceView.mWidth; i++)
														{
															x[0].value = (double) i / mDotSurfaceView.mWidth;
															j[i] = (int)(mDotSurfaceView.mHeight*svm.svm_predict(model, x));
														}
														
														//mDotSurfaceView.mPaint
														//mDotSurfaceView.myCanvas
														
														//buffer_gc.setColor(colors[0]);
														//buffer_gc.drawLine(0,0,0,YLEN-1);
														//window_gc.setColor(colors[0]);
														//window_gc.drawLine(0,0,0,YLEN-1);
														
														int p = (int)(param.p * mDotSurfaceView.mHeight);
														for(int i=1;i<mDotSurfaceView.mWidth;i++)
														{
															buffer_gc.setColor(colors[0]);
															buffer_gc.drawLine(i,0,i,YLEN-1);
															window_gc.setColor(colors[0]);
															window_gc.drawLine(i,0,i,YLEN-1);

															buffer_gc.setColor(colors[5]);
															window_gc.setColor(colors[5]);
															buffer_gc.drawLine(i-1,j[i-1],i,j[i]);
															window_gc.drawLine(i-1,j[i-1],i,j[i]);

															if(param.svm_type == svm_parameter.EPSILON_SVR)
															{
																buffer_gc.setColor(colors[2]);
																window_gc.setColor(colors[2]);
																buffer_gc.drawLine(i-1,j[i-1]+p,i,j[i]+p);
																window_gc.drawLine(i-1,j[i-1]+p,i,j[i]+p);

																buffer_gc.setColor(colors[2]);
																window_gc.setColor(colors[2]);
																buffer_gc.drawLine(i-1,j[i-1]-p,i,j[i]-p);
																window_gc.drawLine(i-1,j[i-1]-p,i,j[i]-p);
															}
														}*/
													}
													else
													{
														if (param.gamma == 0)
															param.gamma = 0.5;

														//svm_node x_space = new svm_node[prob.l][3];
														prob.x = new svm_node[prob.l][3];

														for (int i = 0; i < prob.l; i++)
														{
															prob.x[i][0] = new svm_node();
															prob.x[i][0].index = 1;
															prob.x[i][0].value = mDotSurfaceView.mListX.get(i);
															prob.x[i][1] = new svm_node();
															prob.x[i][1].index = 2;
															prob.x[i][1].value = mDotSurfaceView.mListY.get(i);
															prob.x[i][2] = new svm_node();
															prob.x[i][2].index = -1;
															prob.y[i] = mDotSurfaceView.mListLabels.get(i);
														}

														// build model & classify
														svm_model model = svm.svm_train(prob, param);
														svm_node[] x = new svm_node[3];
														x[0] = new svm_node();
														x[1] = new svm_node();
														x[2] = new svm_node();
														x[0].index = 1;
														x[1].index = 2;
														x[2].index = -1;

														mDotSurfaceView.myCanvas.drawRect(0, 0, mDotSurfaceView.mWidth, mDotSurfaceView.mHeight,
																mDotSurfaceView.mPaint);

														for (int i = 0; i < mDotSurfaceView.mWidth; i++)
															for (int j = 0; j < mDotSurfaceView.mHeight; j++)
															{
																x[0].value = (double) i / (double) mDotSurfaceView.mWidth;
																x[1].value = (double) j / (double) mDotSurfaceView.mHeight;
																double d = svm.svm_predict(model, x);
																if (param.svm_type == svm_parameter.ONE_CLASS && d < 0)
																	d = 2;

																switch ((int) d)
																{
																	case 0:
																		mDotSurfaceView.mDotPaint.setColor(Color.rgb(0, 200, 200));
																		break;
																	case 1:
																		mDotSurfaceView.mDotPaint.setColor(Color.rgb(200, 200, 0));
																		break;
																	case 2:
																		mDotSurfaceView.mDotPaint.setColor(Color.rgb(200, 0, 200));
																		break;
																}

																mDotSurfaceView.myCanvas.drawPoint(i, j, mDotSurfaceView.mDotPaint);
																//mDotSurfaceView.myCanvas.drawLine(i, j, i, j, );
																//buffer_gc.drawLine(i,j,i,j);
																//window_gc.drawLine(i,j,i,j);
															}
													}

													mDotSurfaceView.drawAllPoints();
													mDotSurfaceView.invalidate();

												}
											};

	private OnClickListener	clearListener	= new OnClickListener()
											{
												public void onClick(View v)
												{
													mDotSurfaceView.cleanSurface();
												}
											};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_svm_toy, menu);
		return true;
	}
}
