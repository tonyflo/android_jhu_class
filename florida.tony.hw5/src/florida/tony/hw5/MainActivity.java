package florida.tony.hw5;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView xView;
	private TextView yView;
	private TextView zView;
	private TextView magxView;
	private TextView magyView;
	private TextView magzView;
	private Display display;
	private PuckView puckView;
	private SensorManager sensorManager;

	SensorEventListener accelListener = new SensorEventListener() {
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
					xView.setText("" + x);
					yView.setText("" + y);
					zView.setText("" + event.values[2]);
					puckView.change(-x * 5, y * 5);
				}
			});
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("ACCURACY CHANGE", sensor.getName() + ": " + accuracy);
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
					magxView.setText("" + x);
					magyView.setText("" + y);
					magzView.setText("" + event.values[2]);
				}
			});
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("ACCURACY CHANGE", sensor.getName() + ": " + accuracy);
		}
	};
	private Sensor accelerometer;
	private Sensor magnetometer;
	private Vibrator vibrator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor sensor : sensorList) {
			System.out.println(sensor.getName());
			System.out.println("  VENDOR: " + sensor.getVendor());
			System.out.println("  TYPE: " + typeName(sensor.getType()));
			System.out.println("  POWER: " + sensor.getPower() + "mA");
			System.out.println("  RESOLUTION: " + sensor.getResolution());
			System.out.println("  RANGE: " + sensor.getMaximumRange());
		}

		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();

		xView = (TextView) findViewById(R.id.x);
		yView = (TextView) findViewById(R.id.y);
		zView = (TextView) findViewById(R.id.z);
		magxView = (TextView) findViewById(R.id.magx);
		magyView = (TextView) findViewById(R.id.magy);
		magzView = (TextView) findViewById(R.id.magz);
		puckView = (PuckView) findViewById(R.id.puck);

		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		puckView.setVibrator(vibrator);
	}

	@Override
	protected void onResume() {
		puckView.start();
		super.onResume();
		sensorManager.registerListener(accelListener, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(magListener, magnetometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		
	}

	@Override
	protected void onPause() {
		puckView.stop();
		sensorManager.unregisterListener(accelListener);
		sensorManager.unregisterListener(magListener);
		super.onPause();
	}

	private String typeName(int type) {
		switch (type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "TYPE_ACCELEROMETER";
		case Sensor.TYPE_GYROSCOPE:
			return "TYPE_GYROSCOPE";
		case Sensor.TYPE_LIGHT:
			return "TYPE_LIGHT";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "TYPE_MAGNETIC_FIELD";
		case Sensor.TYPE_PRESSURE:
			return "TYPE_PRESSURE";
		case Sensor.TYPE_PROXIMITY:
			return "TYPE_PROXIMITY";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "TYPE_AMBIENT_TEMPERATURE";
		case Sensor.TYPE_GRAVITY:
			return "TYPE_GRAVITY";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "TYPE_LINEAR_ACCELERATION";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "TYPE_RELATIVE_HUMIDITY";
		case Sensor.TYPE_ROTATION_VECTOR:
			return "TYPE_ROTATION_VECTOR";
		case Sensor.TYPE_ORIENTATION:
			return "TYPE_ORIENTATION";
		case Sensor.TYPE_TEMPERATURE:
			return "TYPE_TEMPERATURE";
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return "TYPE_GAME_ROTATION_VECTOR";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "TYPE_GYROSCOPE_UNCALIBRATED";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "TYPE_MAGNETIC_FIELD_UNCALIBRATED";
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return "TYPE_SIGNIFICANT_MOTION";

		default:
			return "(unknown type " + type + ")";
		}
	}

}
