package florida.tony.hw6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends Activity {
	private static final int GOOGLE_PLAY_SETUP = 42;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		int googlePlayServicesAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		switch (googlePlayServicesAvailable) {
		case ConnectionResult.SUCCESS:
			// all's well
			break;
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
			GooglePlayServicesUtil.getErrorDialog(googlePlayServicesAvailable,
					this, GOOGLE_PLAY_SETUP).show();
			return;
		default:
			throw new RuntimeException(
					"Unexpected result code from isGooglePlayServicesAvailable: "
							+ googlePlayServicesAvailable
							+ " ("
							+ GooglePlayServicesUtil
									.getErrorString(googlePlayServicesAvailable)
							+ ")");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
    @Override
    protected void onStart() {
    	Log.d("Main", "onStart");
    	Intent intent = new Intent("florida.tony.hw6.RemoteService");
    	if (!bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE))
    		Toast.makeText(getBaseContext(), "Could not bind to service", Toast.LENGTH_LONG).show();
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	Log.d("Main", "onStop");
		try {
			remoteService.remove(reporter);
		} catch (RemoteException e) {
			Log.e("MainActivity", "addReporter", e);
		}
    	unbindService(serviceConnection);
    	super.onStop();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    private RemoteService remoteService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override public void onServiceDisconnected(ComponentName name) {
			remoteService = null;
		}
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			remoteService = RemoteService.Stub.asInterface(service);
			try {
				remoteService.add(reporter);
			} catch (RemoteException e) {
				Log.e("MainActivity", "addReporter", e);
			}
		}
	};
	
	private RemoteServiceReporter reporter = new RemoteServiceReporter.Stub() {
		@Override public void report(final int n) throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override public void run() {
					//reporter to be notified of ship positions
					setProgress(n * 1000);
				}});
		}};
		
	


}
