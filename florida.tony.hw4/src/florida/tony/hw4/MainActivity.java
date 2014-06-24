package florida.tony.hw4;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import florida.tony.hw4.Shape.Type;

public class MainActivity extends Activity {

	private static enum Mode {
		DrawCircle, DrawSquare, Select
	} // end Mode

	private Mode mode = Mode.Select;

	private Drawable square;
	private Drawable circle;
	final private int NUM_CONTAINERS = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		square = getResources().getDrawable(R.drawable.square);
		circle = getResources().getDrawable(R.drawable.circle);

		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		DrawingView drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);
	} // end onCreate

	public void squarePressed(View view) {
		mode = Mode.DrawSquare;
	} // end squarePressed

	public void circlePressed(View view) {
		mode = Mode.DrawCircle;
	} // end circlePressed

	public void selectPressed(View view) {
		mode = Mode.Select;
	} // end circlePressed

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end onCreateOptionsMenu

	public class DrawingView extends View {

		private Shape selected;
		private Shape hovered;

		private float selectionOffsetX = 0;
		private float selectionOffsetY = 0;

		public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		public DrawingView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		public DrawingView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		private List<Shape> shapes = new ArrayList<Shape>();
		private List<Shape> containers = new ArrayList<Shape>();
		private Paint paint;
		private Rect rect = new Rect();

		private void init(int width, int height) {
			paint = new Paint();
			paint.setColor(Color.DKGRAY);

			drawContainers(width, height);
		} // end init
		
		/*
		 * Draw the containers on the screen based on the canvas width and height
		 */
		private void drawContainers(int canvasWidth, int canvasHeight)
		{
			/**
			 * Get shape size from dimens.xml Source:
			 * http://stackoverflow.com/questions
			 * /2238883/what-is-the-correct-way
			 * -to-specify-dimensions-in-dip-from-java-code
			 */
			int shapeSize = (int) getResources().getDimension(
					R.dimen.shape_size);

			// draw random containers
			for (int i = 0; i < NUM_CONTAINERS; i++) {
				// pick a random shape type
				Type type = (Math.random() < 0.5) ? Type.Circle : Type.Square;

				// get coordinates of where to put container
				int xCoord = 0 + (int) (Math.random() * ((canvasWidth - (shapeSize)) + 1));
				int yCoord = 0 + (int) (Math.random() * ((canvasHeight - (shapeSize)) + 1));
				Log.d("hw4", "hw4 i=" + i + " (" + xCoord + ", " + yCoord + ")");


				// draw shape
				Shape shape = new Shape(type);
				shape.getBounds().set(xCoord, yCoord, xCoord + shapeSize,
						yCoord + shapeSize);
				containers.add(shape);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			Shape shape = null;
			Drawable drawable = null;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				switch (mode) {
				case DrawCircle:
					shape = new Shape(Type.Circle);
					drawable = circle;
					break;
				case DrawSquare:
					shape = new Shape(Type.Square);
					drawable = square;
					break;
				case Select:
					selected = findShapeAt(event.getX(), event.getY());
					if (selected != null) {
						RectF bounds = selected.getBounds();
						selectionOffsetX = event.getX() - bounds.left;
						selectionOffsetY = event.getY() - bounds.top;
					}
					break;
				} // end switch
			case MotionEvent.ACTION_MOVE:
				if (selected != null) {
					RectF bounds = selected.getBounds();
					float width = bounds.right - bounds.left;
					float height = bounds.bottom - bounds.top;
					bounds.set(event.getX() - selectionOffsetX, event.getY()
							- selectionOffsetY, event.getX() - selectionOffsetX
							+ width, event.getY() - selectionOffsetY + height);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if(findContainerAt(event.getX(), event.getY())) {
					Log.d("hw4", "hw4 over a container");
				}

				break;
			} // end switch

			if (drawable != null) {
				shape.getBounds().set(
						event.getX() - drawable.getIntrinsicWidth() / 2,
						event.getY() - drawable.getIntrinsicHeight() / 2,
						event.getX() + drawable.getIntrinsicWidth() / 2,
						event.getY() + drawable.getIntrinsicHeight() / 2);
				shapes.add(shape);
				invalidate();
				return true;
			} // end if

			return true;
		} // end onTouchEvent

		private Shape findShapeAt(float x, float y) {
			for (int i = shapes.size() - 1; i >= 0; i--) {
				Shape shape = shapes.get(i);
				if (shape.getBounds().contains(x, y)) {
					return shape;
				}
			}
			return null;
		}
		
		private boolean findContainerAt(float x, float y) {
			for (int i = containers.size() - 1; i >= 0; i--) {
				Shape container = containers.get(i);
				
				//check if shape is above container and if it is the same type
				if (container.getBounds().contains(x, y) && container.getType() == selected.getType()) {
					//remove container if shape is above it
					containers.remove(container);
					invalidate();
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (paint == null) {
				//pass the canvas width and height to init
				init(canvas.getWidth(), canvas.getHeight());
			}

			// draw containers
			for (Shape container : containers) {
				switch (container.getType()) {
				case Circle:
					container.getBounds().round(rect);
					circle.setBounds(rect);
					circle.draw(canvas);
					break;
				case Square:
					container.getBounds().round(rect);
					square.setBounds(rect);
					square.draw(canvas);
					break;
				default:
					break;
				} // end switch
			} // end for

			//draw shapes
			for (Shape shape : shapes) {
				switch (shape.getType()) {
				case Circle:
					shape.getBounds().round(rect);
					circle.setBounds(rect);
					circle.draw(canvas);
					break;
				case Square:
					shape.getBounds().round(rect);
					square.setBounds(rect);
					square.draw(canvas);
					break;
				default:
					break;
				} // end switch
			} // end for
		} // end onDraw
	} // end DrawingView
} // end MainActivity
