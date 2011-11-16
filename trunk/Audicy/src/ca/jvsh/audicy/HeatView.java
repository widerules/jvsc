package ca.jvsh.audicy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
 
public class HeatView extends View {
	private Canvas myCanvas;
	private Bitmap backbuffer;
	private float radius;
 
	public HeatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
 
	public HeatView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	public HeatView(Context context) {
		super(context);
	}
 
	private void init() {
		this.radius = 20f;
		backbuffer = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		myCanvas = new Canvas(backbuffer);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.TRANSPARENT);
		myCanvas.drawRect(0, 0, getWidth(), getHeight(), p);
 
	}
 
	@Override
	protected void onDraw(Canvas canvas) {
 
		if (backbuffer == null) {
			init();
		}
		canvas.drawBitmap(backbuffer, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
 
	}
 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int fingers = event.getPointerCount();
		float points[][] = new float[fingers][2];
		for (int i = 0; i < fingers; i++) {
			points[i][0] = event.getX(event.getPointerId(i));
			points[i][1] = event.getY(event.getPointerId(i));
		}
		addPoint(points);
		return true;
	}
 
	public void addPoint(float[][] points) {
		for (int i = 0; i < points.length; i++) {
			float x = points[i][0];
			float y = points[i][1];
			RadialGradient g = new RadialGradient(x, y, radius, Color.argb(10,
					0, 0, 0), Color.TRANSPARENT, TileMode.CLAMP);
			Paint gp = new Paint();
			gp.setShader(g);
			myCanvas.drawCircle(x, y, radius, gp);
			colorize(x - radius, y - radius, radius * 2);
		}
		invalidate();
	}
 
	private void colorize(float x, float y, float d) {
		if (x + d > myCanvas.getWidth()) {
			x = myCanvas.getWidth() - d;
		}
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (y + d > myCanvas.getHeight()) {
			y = myCanvas.getHeight() - d;
		}
 
		int[] pixels = new int[(int) (d * d)];
		backbuffer.getPixels(pixels, 0, (int) d, (int) x, (int) y, (int) d,
				(int) d);
		for (int i = 0; i < pixels.length; i++) {
			int r = 0, g = 0, b = 0, tmp = 0;
			int alpha = pixels[i] >>> 24;
			if (alpha <= 255 && alpha >= 240) {
				tmp = 255 - alpha;
				r = 255 - tmp;
				g = tmp * 12;
			} else if (alpha <= 239 && alpha >= 200) {
				tmp = 234 - alpha;
				r = 255 - (tmp * 8);
				g = 255;
			} else if (alpha <= 199 && alpha >= 150) {
				tmp = 199 - alpha;
				g = 255;
				b = tmp * 5;
			} else if (alpha <= 149 && alpha >= 100) {
				tmp = 149 - alpha;
				g = 255 - (tmp * 5);
				b = 255;
			} else
				b = 255;
			pixels[i] = Color.argb(alpha, r, g, b);
		}
		backbuffer.setPixels(pixels, 0, (int) d, (int) x, (int) y, (int) d,
				(int) d);
	}
 
}

