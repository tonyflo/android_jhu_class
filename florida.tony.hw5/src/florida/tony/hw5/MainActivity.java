package florida.tony.hw5;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import florida.tony.hw4.R;
import florida.tony.hw5.Shape.Type;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends Activity {

	private Drawable square;
	private Drawable selectedSquare;
	private Drawable squareHole;
	private Drawable selectedSquareHole;
	private Drawable circle;
	private Drawable selectedCircle;
	private Drawable circleHole;
	private Drawable selectedCircleHole;

	// level variables
	private int numContainers = 1;
	private double bestAverage = 0.0;

	// timer variables
	private int count = 0;
	Timer timer;

	private int shapeSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startStopwatch();

		square = getResources().getDrawable(R.drawable.square);
		selectedSquare = getResources().getDrawable(R.drawable.selected_square);
		squareHole = getResources().getDrawable(R.drawable.square_hole);
		selectedSquareHole = getResources().getDrawable(
				R.drawable.selected_square_hole);
		circle = getResources().getDrawable(R.drawable.circle);
		selectedCircle = getResources().getDrawable(R.drawable.selected_circle);
		circleHole = getResources().getDrawable(R.drawable.circle_hole);
		selectedCircleHole = getResources().getDrawable(
				R.drawable.selected_circle_hole);

		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		DrawingView drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);

		shapeSize = (int) getResources().getDimension(R.dimen.shape_size);

		Toast.makeText(getApplicationContext(),
				"Level " + numContainers + "\nAvoid the falling blocks!",
				Toast.LENGTH_SHORT).show();

	} // end onCreate

	/**
	 * Display a stopwatch and update every second
	 * http://stackoverflow.com/questions/11730902/android-simple-time-counter
	 */
	private void startStopwatch() {
		timer = new Timer();
		final TextView stopwatchText = (TextView) findViewById(R.id.stopwatch);

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// in your OnCreate() method
						stopwatchText.setText(count + "");
						count++;
					}
				});
			}
		}, 1000, 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end onCreateOptionsMenu

	public class DrawingView extends View {

		//animation variables
		LinkedList<Long> times = new LinkedList<Long>();
		private final int MAX_SIZE = 100;
		private final double NANOS = 1000000000.0;
		private int frameCount = 0;
		private double dropRateSeconds = 5;
		private int fallingSpeedPixels = 5;
		
		//person variables
		private float personStartPosX;
		private float personStartPosY;

		public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		} // end DrawingView

		public DrawingView(Context context, AttributeSet attrs) {
			super(context, attrs);
		} // end DrawingView

		public DrawingView(Context context) {
			super(context);
		} // end DrawingView

		//drawing variables
		private List<Shape> shapes = new ArrayList<Shape>();
		private List<Shape> containers = new ArrayList<Shape>();
		private Paint paint;
		private Rect rect = new Rect();

		private int canvasWidth = 0;
		private int canvasHeight = 0;

		private void init() {
			times.addLast(System.nanoTime());
			
			paint = new Paint();
			paint.setColor(Color.DKGRAY);

			drawContainer();
			drawPerson();
		} // end init

		private int getRandomX() {
			// get x value of where to put container
			return (int) (Math.random() * ((canvasWidth - (shapeSize)) + 1));
		}

		/*
		 * Draw the containers on the screen based on the canvas width and
		 * height
		 */
		private void drawContainer() {

			// draw random containers
			// pick a random shape type
			Type type = (Math.random() < 0.5) ? Type.CircleHole
					: Type.SquareHole;

			int xCoord = getRandomX();

			// draw shape
			Shape shape = new Shape(type);
			shape.getBounds().set(xCoord, 0, xCoord + shapeSize, 0 + shapeSize);
			containers.add(shape);
		}
		
		/*
		 * Draw the person on the screen based on the canvas width and
		 * height
		 */
		private void drawPerson() {

			int xCoord = getRandomX();

			personStartPosX = canvasWidth/2 - shapeSize/2;
			personStartPosY = (float) (canvasHeight * 0.8);
			
			// draw shape
			Shape shape = new Shape(Type.Square);
			shape.getBounds().set(personStartPosX, personStartPosY, personStartPosX + shapeSize, personStartPosY + shapeSize);
			shapes.add(shape);
		}

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

				// check for a null container
				if (container == null) {
					return false;
				}

				// check if shape is above container
				if (container.getBounds().contains(x, y)) {
					// remove container if shape is above it
					containers.remove(container);
					invalidate();
					return true;
				}
			}
			return false;
		}
		
		/* http://stackoverflow.com/questions/10210439/how-to-count-the-framerate-with-which-a-surfaceview-refreshes */
		/** Calculates and returns frames per second */
		private double fps() {
		    long lastTime = System.nanoTime();
		    double difference = (lastTime - times.getFirst()) / NANOS;
		    times.addLast(lastTime);
		    int size = times.size();
		    if (size > MAX_SIZE) {
		        times.removeFirst();
		    }
		    return difference > 0 ? times.size() / difference : 0.0;
		}

		private Type getShapeTypeOfContainerAt(float x, float y) {
			for (int i = containers.size() - 1; i >= 0; i--) {
				Shape container = containers.get(i);

				// check for a null container
				if (container == null) {
					return null;
				}

				// check if shape is above container
				if (container.getBounds().contains(x, y)) {

					// get shape type of container
					Type currentType = container.getType();
					Type newType = null;
					if (currentType == Type.CircleHole) {
						newType = Type.Circle;
					} else if (currentType == Type.SquareHole) {
						newType = Type.Square;
					}

					// indicate that the container is selected by changing its
					// type
					container.setContainerSelected(true);

					return newType;
				}
			}
			return null;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (paint == null) {
				// get the canvas width and height
				canvasWidth = canvas.getWidth();
				canvasHeight = canvas.getHeight();
				init();
			}

			// draw containers
			for (Shape container : containers) {

				// animate downward
				container.moveDown(fallingSpeedPixels);

				switch (container.getType()) {
				case CircleHole:
					container.getBounds().round(rect);
					circleHole.setBounds(rect);
					circleHole.draw(canvas);
					break;
				case SquareHole:
					container.getBounds().round(rect);
					squareHole.setBounds(rect);
					squareHole.draw(canvas);
					break;
				default:
					break;
				} // end switch
			} // end for

			// draw shapes
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
			
			//calculate frames per second
			double fps = fps();
			//calculate seconds
			double seconds = frameCount * (1.0/fps);
			
			//drop objects at a increasing rate per level
			if(seconds >= dropRateSeconds)
			{
				drawContainer();
				frameCount = 0;
			}
			frameCount++;

			// force view to redraw
			invalidate();
		} // end onDraw
	} // end DrawingView
} // end MainActivity
