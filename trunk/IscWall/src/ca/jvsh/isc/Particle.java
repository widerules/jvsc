
package ca.jvsh.isc;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

class Particle
{
	public boolean catchMouse = false;
	public boolean isBeam = false;
	public float spx = 0;
	public float spy = 0;
	
	public float x;
	public float y;
	
	public float mn;
	public float mvx;
	public float mvy;
	
	public float scale;
	public float opacity = 0;
	
	public int color;
	
	// particles
	public Bitmap		mParticle;
	
	public Particle(Resources resources, int width, int height)
	{
		//mn = 0.1f + (float)Math.random()/8.0f;
		//mvx = 0.05f + (float)Math.random() / 4.0f;
		//mvy = 0.05f + (float)Math.random() / 8.0f;
		mn = 0.6f + (float)Math.random()/1.0f;
		mvx = 0.4f + 2 * (float)Math.random() / 1.0f;
		mvy = 0.4f + (float)Math.random() / 1.0f;
		
		
		scale = (float) Math.floor(Math.random() * (10 - 3 + 1) + 3) / 4.0f;//10.0f;
		color = (int)Math.floor(Math.random() * 5);
		
		if (color == 1)
		{
			mParticle = BitmapFactory.decodeResource(resources, R.drawable.particleblue);
		}
		else if (color == 2)
		{
			isBeam = true;
			//mvx = 0.5f;
			//mvx = 0.1f;
			mvx = 1.5f;
			mvx = 0.4f;
			
			scale = (float)Math.floor(Math.random() * (2 - 1 + 1) + 1) / 2.0f;//10.0f;
			mParticle = BitmapFactory.decodeResource(resources, R.drawable.beam);
		}
		else
		{
			mParticle = BitmapFactory.decodeResource(resources, R.drawable.particle);
		}

		int particleWidth = mParticle.getWidth();
		int particleHeight = mParticle.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		mParticle = Bitmap.createBitmap(mParticle, 0, 0, particleWidth, particleHeight, matrix, true);

		
		x =  (float)Math.random() * width;
		y = (float)Math.random() * height;
	}
	
}