package florida.tony.hw6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

/*
 * Code adapted from Scott Stanchfield
 */
public class RemoteServiceImpl extends Service {
	private int requestNumber = 0;

	private class ServiceThread extends Thread {

		private String uri = "http://javadude.com/aliens/";
		private boolean shouldContinue = true;
		List<UFOPosition> ufoList = null;

		private boolean makeJsonRequest(int requestNumber)
				throws ClientProtocolException, IOException {
			// http return status
			boolean returnStatus = true;

			String fullUri = uri + requestNumber + ".json";
			HttpUriRequest request = new HttpGet(fullUri);

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(request);
			StatusLine statusLine = response.getStatusLine();

			HttpEntity entity = response.getEntity();
			String resultString;
			if (entity != null) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				entity.writeTo(out);
				out.close();
				resultString = out.toString();
			} else {
				resultString = statusLine.getReasonPhrase();
			}

			if (statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				returnStatus = false;
			} else {
				// create a list of ufo positons
				ufoList = parseJson(resultString);
			}

			// Log.d("http", resultString);
			return returnStatus;
		}

		// convert the json result into a list of ships
		private List<UFOPosition> parseJson(String jsonResult) {
			List<UFOPosition> ufoList = new ArrayList<UFOPosition>();

			JSONArray jsonArray = null;
			try {
				jsonArray = new JSONArray(jsonResult);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				int shipNumber = Integer.parseInt(jsonArray.optJSONObject(i)
						.optString("ship"));
				double lat = Double.parseDouble(jsonArray.optJSONObject(i)
						.optString("lat"));
				double lon = Double.parseDouble(jsonArray.optJSONObject(i)
						.optString("lon"));
				UFOPosition ufoPosition = new UFOPosition(shipNumber, lat, lon);
				ufoList.add(ufoPosition);
			}

			return ufoList;
		}

		@Override
		public void run() {
			while (!isInterrupted() && shouldContinue) {
				requestNumber++;
				List<RemoteServiceReporter> targets;
				synchronized (reporters) {
					targets = new ArrayList<RemoteServiceReporter>(reporters);
				}
				for (RemoteServiceReporter reporter : targets) {
					try {
						reporter.report(ufoList);
					} catch (RemoteException e) {
					}
				}

				// make the json request
				try {
					if (!makeJsonRequest(requestNumber)) {
						// if json data wasn't returned, stop
						shouldContinue = false;
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					sleep(1000);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
	}

	private ServiceThread serviceThread;

	public void resetCounter() {
		requestNumber = 0;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (serviceThread != null) {
			serviceThread.interrupt();
			serviceThread = null;
		}
		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		serviceThread = new ServiceThread();
		serviceThread.start();
		return binder;
	}

	private RemoteService.Stub binder = new RemoteService.Stub() {
		@Override
		public void reset() throws RemoteException {
			resetCounter();
		}

		@Override
		public void add(RemoteServiceReporter reporter) throws RemoteException {
			synchronized (reporters) {
				reporters.add(reporter);
			}
		}

		@Override
		public void remove(RemoteServiceReporter reporter)
				throws RemoteException {
			synchronized (reporters) {
				reporters.remove(reporter);
			}
		}
	};

	private List<RemoteServiceReporter> reporters = new ArrayList<RemoteServiceReporter>();
}
