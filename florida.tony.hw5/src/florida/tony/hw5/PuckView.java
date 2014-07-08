package florida.tony.hw5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class PuckView extends View {
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float ax;
	private float ay;
	private static final int size = 50;
	private Paint paint;
	
	private Handler handler = new Handler();
	private Runnable invalidator = new Runnable() {
		@Override public void run() {
			invalidate();
		}
	};
	private AnimationThread animationThread;

	public PuckView(Context context) {
		super(context);
	}
	public PuckView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PuckView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void start() {
		stop();
		animationThread = new AnimationThread();
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread.interrupt();
			animationThread = null;
		}
	}
	public void change(float ax, float ay) {
		this.ax = ax;
		this.ay = ay;
	}
	private class AnimationThread extends Thread {
		@Override public void run() {
			while (!isInterrupted()) {
				// accelerate
				vx += ax; 
				vy += ay;

				// move
				x += vx;
				y += vy;
				
				// check bounds
				if (x < 0) {
					x = 0;
					ax = 0;
					vx = -vx/2;
				}
				if (x > getWidth()-size) {
					x = getWidth()-size;
					ax = 0;
					vx = -vx/2;
				}
				if (y < 0) {
					y = 0;
					ay = 0;
					vy = -vy/2;
				}
				if (y > getHeight()-size) {
					y = getHeight()-size;
					ay = 0;
					vy = -vy/2;
				}
				
				handler.post(invalidator);
				try {
					sleep(50);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
	}
	
	@Override protected void onDraw(Canvas canvas) {
		if (paint == null) {
			paint = new Paint();
			paint.setColor(Color.RED);
		}
		canvas.drawRect(x,y,x+size,y+size, paint);
	}
}