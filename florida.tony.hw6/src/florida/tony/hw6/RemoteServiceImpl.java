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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/*
 * Code adapted from Scott Stanchfield
 */
public class RemoteServiceImpl extends Service {
	private int n = 0;
	private class ServiceThread extends Thread {
		
		private String uri = "http://javadude.com/aliens/";
		private boolean shouldContinue = true;
		
		private boolean makeJsonRequest(int requestNumber) throws ClientProtocolException, IOException
		{	
			//http return status
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
	        
	        Log.d("status code", ""+statusLine.getStatusCode());
	        if(statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND)
	        {
	        	returnStatus = false;
	        	Log.d("DONE", "4040404");
	        }
	        
	        Log.d("http", resultString);
	        return returnStatus;
		}

		
		@Override public void run() {
			while(!isInterrupted() && shouldContinue) {
				n++;
				List<RemoteServiceReporter> targets;
				synchronized (reporters) {
					targets = new ArrayList<RemoteServiceReporter>(reporters);
				}
				for(RemoteServiceReporter reporter : targets) {
					try {
						reporter.report(n);
					} catch (RemoteException e) {
						Log.e("RemoteService2Impl","report",e);
					}
				}
				Log.d("RemoteService2Impl", "running... " + n);
				
				//make the json request
				try {
					if(!makeJsonRequest(n))
					{
						//if json data wasn't returned, stop
						shouldContinue = false;
						Log.d("here", "here");
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
			Log.d("RemoteService2Impl", "interrupted");
		}
	}
	
	private ServiceThread serviceThread;
	
	public void resetCounter() {
		n = 0; 
		// race condition: chance that it could be set in the middle of (n++) above! 
		// could make it volatile or sync access to it, but does it matter?
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		Log.d("RemoteService2Impl", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("RemoteService2Impl", "onStartCommand(" + intent + ", " + flags + ", " + startId + ")");
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
		Log.d("RemoteService2Impl", "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		serviceThread = new ServiceThread();
		serviceThread.start();
		return binder;
	}

	private RemoteService.Stub binder = new RemoteService.Stub() {
		@Override public void reset() throws RemoteException {
			resetCounter();
		}

		@Override public void add(RemoteServiceReporter reporter) throws RemoteException {
			synchronized (reporters) {
				reporters.add(reporter);
			}
		}

		@Override public void remove(RemoteServiceReporter reporter) throws RemoteException {
			synchronized (reporters) {
				reporters.remove(reporter);
			}
		}
	};
	
	private List<RemoteServiceReporter> reporters = new ArrayList<RemoteServiceReporter>();
}
