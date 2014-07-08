package florida.tony.hw5;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomView2 extends View {
	public CustomView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		processAttributes(context, attrs);
	}
	public CustomView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		processAttributes(context, attrs);
	}
	public CustomView2(Context context) {
		super(context);
	}
	
	private void processAttributes(Context context, AttributeSet attrs) {
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomView2);
		color = attributes.getColor(R.styleable.CustomView2_color, 0xff666666);
		attributes.recycle();
	}
	
	private Paint paint;
	private int cx;
	private int cy;
	private int radius;
	private int color;
	
	private void init() {
		paint = new Paint();
		paint.setColor(color);
	}
	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		cx = w / 2;
		cy = h / 2;
		radius = Math.min(w, h) / 3;
	}
	@Override protected void onDraw(Canvas canvas) {
		if (paint == null)
		{
			init();
		}
		
		canvas.drawColor(Color.WHITE);
		canvas.drawCircle(cx, cy, radius, paint);
	}
}
