package florida.tony.hw5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import florida.tony.hw5.Shape.Type;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends Activity {

	// drawables
	private Drawable square;
	private Drawable squareHole;
	private Drawable circle;
	private Drawable circleHole;

	// level variables
	private int level = 1;
	private double DIFFICULITY = 0.5; // objects increase at this rate per
										// second
	private boolean winner = false;

	// timer variables
	private int SECONDS_PER_LEVEL = 15;
	private int count = SECONDS_PER_LEVEL;
	Timer timer;

	private double dropRateSeconds = 5;
	private int fallingSpeedPixels = 5;

	private int shapeSize;

	// sensor variables
	private Sensor accelerometer;
	private Sensor magnetometer;
	private SensorManager sensorManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startStopwatch();

		square = getResources().getDrawable(R.drawable.square);
		squareHole = getResources().getDrawable(R.drawable.square_hole);
		circle = getResources().getDrawable(R.drawable.circle);
		circleHole = getResources().getDrawable(R.drawable.circle_hole);

		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		DrawingView drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);

		shapeSize = (int) getResources().getDimension(R.dimen.shape_size);

		Toast.makeText(getApplicationContext(),
				"Level " + level + "\nAvoid the falling blocks!",
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

						if (count == 0) {
							// go to next level
							count = SECONDS_PER_LEVEL;
							level++;

							// make it harder
							dropRateSeconds -= DIFFICULITY;
							fallingSpeedPixels++;

							if (detectWin()) {
								stopGame();
								
								// alert the user
								Toast.makeText(getApplicationContext(),
										"You win!", Toast.LENGTH_SHORT).show();

								// set winner var to true
								winner = true;
							} else {

								// tell the user what level they are on
								Toast.makeText(getApplicationContext(),
										"Level " + level, Toast.LENGTH_SHORT)
										.show();
							}
						}

						count--;
					}
				});
			}
		}, 1000, 1000);
	}
	
	private void stopGame()
	{
		// winner, stop the game
		fallingSpeedPixels = 0;

		// stop the timer
		timer.cancel();
	}

	private boolean detectWin() {
		return dropRateSeconds <= 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end onCreateOptionsMenu

	public class DrawingView extends View {

		private Shape person;
		private Display display;

		SensorEventListener accelListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(final SensorEvent event) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						float x = 0;
						switch (display.getRotation()) {
						case Surface.ROTATION_0:
							x = event.values[0];
							break;
						case Surface.ROTATION_90:
							x = -event.values[1];
							break;
						case Surface.ROTATION_180:
							x = -event.values[0];
							break;
						case Surface.ROTATION_270:
							x = event.values[1];
							break;
						}

						movePerson(x);
						findContainerAt(person.getBounds());
					}
				});
			}

			public void movePerson(float x) {
				boolean hitLeft = checkPersonHitLeftBounds(person.getBounds().left);
				boolean hitRight = checkPersonHitRightBounds(person.getBounds().right);
				boolean hitBottom = checkPersonHitBottomBounds(person
						.getBounds().bottom);

				if (!hitLeft && !hitRight) {
					// move freely
					person.move(x);
				} else if (hitLeft && x < 0) {
					// get off left wall if tilting right
					person.move(x);
				} else if (hitRight && x > 0) {
					// get off right wall if tilting left
					person.move(x);
				} else if (hitBottom) {
					//user lost
					stopGame();
					Toast.makeText(getApplicationContext(),
							"You loose!", Toast.LENGTH_SHORT).show();
				}
			}

			public boolean checkPersonHitLeftBounds(float left) {
				boolean hitBounds = false;
				// check left bounds
				if (left < 0) {
					hitBounds = true;
				}
				return hitBounds;
			}

			public boolean checkPersonHitRightBounds(float right) {
				boolean hitBounds = false;
				// check right bounds
				if (right > canvasWidth) {
					hitBounds = true;
				}
				return hitBounds;
			}

			public boolean checkPersonHitBottomBounds(float bottom) {
				boolean hitBounds = false;
				// check bottom bounds
				if (bottom > canvasHeight) {
					hitBounds = true;
				}
				return hitBounds;
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				//stub
			}
		};
		SensorEventListener magListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(final SensorEvent event) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						float x = 0;
						float y = 0;
						switch (display.getRotation()) {
						case Surface.ROTATION_0:
							x = event.values[0];
							y = event.values[1];
							break;
						case Surface.ROTATION_90:
							x = -event.values[1];
							y = event.values[0];
							break;
						case Surface.ROTATION_180:
							x = -event.values[0];
							y = -event.values[1];
							break;
						case Surface.ROTATION_270:
							x = event.values[1];
							y = -event.values[0];
							break;
						}
					}
				});
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				//stub
			}
		};

		// animation variables
		LinkedList<Long> times = new LinkedList<Long>();
		private final int MAX_SIZE = 100;
		private final double NANOS = 1000000000.0;
		private int frameCount = 0;

		// person variables
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

		// drawing variables
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

			// sensor management

			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

			WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();

			accelerometer = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magnetometer = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

			sensorManager.registerListener(accelListener, accelerometer,
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(magListener, magnetometer,
					SensorManager.SENSOR_DELAY_GAME);
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
		 * Draw the person on the screen based on the canvas width and height
		 */
		private void drawPerson() {

			personStartPosX = canvasWidth / 2 - shapeSize / 2;
			personStartPosY = (float) (canvasHeight * 0.1);

			// draw shape
			person = new Shape(Type.Square);
			person.getBounds().set(personStartPosX, personStartPosY,
					personStartPosX + shapeSize, personStartPosY + shapeSize);
			shapes.add(person);
		}

		private boolean findContainerAt(RectF bounds) {
			for (int i = containers.size() - 1; i >= 0; i--) {
				Shape container = containers.get(i);

				// check for a null container
				if (container == null) {
					return false;
				}

				// check if shape is above container
				if (container.getBounds().intersect(bounds.left, bounds.top,
						bounds.right, bounds.bottom)) {
					// remove container if shape is above it
					containers.remove(container);

					// move person down
					person.moveDown(shapeSize);

					if (!winner) {
						invalidate();
					}
					return true;
				}
			}
			return false;
		}

		/*
		 * http://stackoverflow.com/questions/10210439/how-to-count-the-framerate
		 * -with-which-a-surfaceview-refreshes
		 */
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

			// calculate frames per second
			double fps = fps();
			// calculate seconds
			double seconds = frameCount * (1.0 / fps);

			// drop objects at a increasing rate per level
			if (seconds >= dropRateSeconds) {
				drawContainer();
				frameCount = 0;
			}
			frameCount++;

			if (!winner) {
				// force view to redraw
				invalidate();
			}
		} // end onDraw
	} // end DrawingView
} // end MainActivity
