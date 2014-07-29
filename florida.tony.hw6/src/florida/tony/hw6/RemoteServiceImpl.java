package florida.tony.hw6;

import java.util.ArrayList;
import java.util.List;

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
		@Override public void run() {
			while(!isInterrupted()) {
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
