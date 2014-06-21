/* ======================================================================
 *  Copyright � 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 *  QTI Proprietary and Confidential.
 *  =====================================================================
 *  
 * @file:   DrawView.java
 *
 */

package com.qualcomm.qti.polyqon;

import java.util.HashMap;

import com.qualcomm.qti.polyqon.R;
import com.qualcomm.snapdragon.sdk.face.FaceData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;

public class DrawView extends SurfaceView
{

	private Paint		leftEyeBrush	= new Paint();
	private Paint		rightEyeBrush	= new Paint();
	private Paint		mouthBrush		= new Paint();
	private Paint		rectBrush		= new Paint();
	public Point		leftEye, rightEye, mouth;
	Rect				mFaceRect;
	public FaceData[]	mFaceArray;
	boolean				_inFrame;						// Boolean to see if there is any faces in the frame
	int					mSurfaceWidth;
	int					mSurfaceHeight;
	int					cameraPreviewWidth;
	int					cameraPreviewHeight;
	boolean				mLandScapeMode;
	float				scaleX			= 1.0f;
	float				scaleY			= 1.0f;

	public DrawView(Context context, FaceData[] faceArray, boolean inFrame, int surfaceWidth, int surfaceHeight, Camera cameraObj, boolean landScapeMode)
	{
		super(context);

		setWillNotDraw(false); // This call is necessary, or else the draw method will not be called. 
		mFaceArray = faceArray;
		_inFrame = inFrame;
		mSurfaceWidth = surfaceWidth;
		mSurfaceHeight = surfaceHeight;
		mLandScapeMode = landScapeMode;
		if (cameraObj != null)
		{
			cameraPreviewWidth = cameraObj.getParameters().getPreviewSize().width;
			cameraPreviewHeight = cameraObj.getParameters().getPreviewSize().height;
		}
		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);

		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		float xscale = (float) display.getWidth() / (float) bmp.getWidth();
		float yscale = (float) display.getHeight() / (float) bmp.getHeight();
		if (xscale > yscale) // make sure both dimensions fit (use the smaller scale)
			xscale = yscale;
		int newx = (int) ((float) bmp.getWidth() * xscale);
		int newy = (int) ((float) bmp.getHeight() * xscale); // use the same scale for both dimensions
		// if you want it centered on the display (black borders)
		//float borderx = ((float)display.getWidth() - newx) / 2.0;
		//float bordery = ((float)display.getHeight() - newy) / 2.0;
		photoBitmap = Bitmap.createScaledBitmap(bmp, newx, newy, true);
		bmp.recycle();
		bmp = null;
	}

	Bitmap	bmp;
	Bitmap	photoBitmap;
	Paint	paint;

	@Override
	protected void onDraw(Canvas canvas)
	{
		Log.w("DrawView", "onDraw");

		if (photoBitmap != null)
		{
			canvas.drawBitmap(photoBitmap, 0, 0, paint);
			photoBitmap.recycle();
			photoBitmap = null;
		}

		if (_inFrame) // If the face detected is in frame. 
		{
			for (int i = 0; i < mFaceArray.length; i++)
			{
				leftEyeBrush.setColor(Color.RED);
				canvas.drawCircle(mFaceArray[i].leftEye.x * scaleX, mFaceArray[i].leftEye.y * scaleY, 5f, leftEyeBrush);

				rightEyeBrush.setColor(Color.GREEN);
				canvas.drawCircle(mFaceArray[i].rightEye.x * scaleX, mFaceArray[i].rightEye.y * scaleY, 5f, rightEyeBrush);

				mouthBrush.setColor(Color.WHITE);
				canvas.drawCircle(mFaceArray[i].mouth.x * scaleX, mFaceArray[i].mouth.y * scaleY, 5f, mouthBrush);

				rectBrush.setColor(Color.YELLOW);
				rectBrush.setStyle(Paint.Style.STROKE);
				canvas.drawRect(mFaceArray[i].rect.left * scaleX, mFaceArray[i].rect.top * scaleY, mFaceArray[i].rect.right * scaleX, mFaceArray[i].rect.bottom
						* scaleY, rectBrush);
			}
		}
		//else
		//{
		//	canvas.drawColor(0, Mode.CLEAR);
		//}
	}
}
