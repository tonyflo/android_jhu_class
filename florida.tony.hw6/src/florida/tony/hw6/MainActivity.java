package florida.tony.hw6;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private static final int GOOGLE_PLAY_SETUP = 42;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override protected void onResume() {
		super.onResume();
		int googlePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		switch (googlePlayServicesAvailable) {
			case ConnectionResult.SUCCESS:
				// all's well
				break;
			case ConnectionResult.SERVICE_MISSING:
			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			case ConnectionResult.SERVICE_DISABLED:
				GooglePlayServicesUtil.getErrorDialog(googlePlayServicesAvailable, this, GOOGLE_PLAY_SETUP).show();
				return;
			default:
				throw new RuntimeException("Unexpected result code from isGooglePlayServicesAvailable: " + googlePlayServicesAvailable + " (" + GooglePlayServicesUtil.getErrorString(googlePlayServicesAvailable) + ")");
		}
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}	


}
