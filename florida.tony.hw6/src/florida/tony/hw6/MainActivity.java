package florida.tony.hw6;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends FragmentActivity {
	private static final int GOOGLE_PLAY_SETUP = 42;
	List<UFOPosition> knownUfos = new ArrayList<UFOPosition>();
	private BitmapDescriptor ufoBitmap;
	private GoogleMap googleMap;
	List<Marker> ufoMarkers = new ArrayList<Marker>();
	private LatLngBounds bounds;
	private int padding;
	private double startLat = 38.9073;
	private double startLon = -77.0365;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ufoBitmap = BitmapDescriptorFactory.fromResource(R.drawable.red_ufo);
		padding = (int) getResources().getDimension(R.dimen.padding);
		bounds = new LatLngBounds(new LatLng(startLat, startLon), new LatLng(
				startLat, startLon));
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

		SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		googleMap = map.getMap();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		Intent intent = new Intent("florida.tony.hw6.RemoteService");
		if (!bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE))
			Toast.makeText(getBaseContext(), "Could not bind to service",
					Toast.LENGTH_LONG).show();
		super.onStart();
	}

	@Override
	protected void onStop() {
		try {
			remoteService.remove(reporter);
		} catch (RemoteException e) {
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
		@Override
		public void onServiceDisconnected(ComponentName name) {
			remoteService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			remoteService = RemoteService.Stub.asInterface(service);
			try {
				remoteService.add(reporter);
			} catch (RemoteException e) {
			}
		}
	};

	// reporter to be notified of ship positions
	private RemoteServiceReporter reporter = new RemoteServiceReporter.Stub() {
		@Override
		public void report(final List<UFOPosition> ufoPosition)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					List<Integer> currentList = new ArrayList<Integer>();

					for (int i = 0; i < ufoPosition.size(); i++) {
						UFOPosition ufo = ufoPosition.get(i);

						// add the number of this ship to the current list
						int shipNumber = ufo.getShipNumber();
						currentList.add(shipNumber);

						// check to see if we know about this ship
						int indexOfKnown = -1;
						for (int j = 0; j < knownUfos.size(); j++) {

							if (knownUfos.get(j).getShipNumber() == shipNumber) {
								indexOfKnown = j;
								break;
							}
						}

						if (indexOfKnown < 0) {
							// this is a new ship

							// add the ship to the map
							Marker marker = googleMap
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(ufo.getLat(),
															ufo.getLon()))
											.icon(ufoBitmap).title("UFO!"));

							// add the ship to the lists
							knownUfos.add(ufo);
							ufoMarkers.add(marker);
						} else {
							// adjust bounds
							bounds = bounds.including(new LatLng(ufo.getLat(),
									ufo.getLon()));

							// adjust camera
							googleMap.animateCamera(CameraUpdateFactory
									.newLatLngBounds(bounds, padding));

							// this is a known ship
							UFOPosition knownUfo = knownUfos.get(indexOfKnown);
							Marker ufoMarker = ufoMarkers.get(indexOfKnown);

							// draw a line
							googleMap.addPolyline((new PolylineOptions())
									.add(new LatLng(knownUfo.getLat(), knownUfo
											.getLon()),
											new LatLng(ufo.getLat(), ufo
													.getLon())).width(3)
									.color(Color.BLUE).geodesic(true));

							// remove old marker
							ufoMarker.remove();

							// remove old ufo from lists
							knownUfos.remove(knownUfo);
							ufoMarkers.remove(ufoMarker);

							// update ufo
							Marker marker = googleMap
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(ufo.getLat(),
															ufo.getLon()))
											.icon(ufoBitmap).title("UFO!"));

							// add new ufo to lists
							knownUfos.add(ufo);
							ufoMarkers.add(marker);
						} // end if
					} // end for

					// loop over known ufos to see if any of them are gone
					for (int k = 0; k < knownUfos.size(); k++) {
						boolean stillExists = false;
						for (int m = 0; m < currentList.size(); m++) {
							if (knownUfos.get(k).getShipNumber() == currentList
									.get(m)) {
								stillExists = true;
							}
						}

						if (!stillExists) {
							ufoMarkers.get(k).remove();
							knownUfos.remove(k);
							ufoMarkers.remove(k);
						}
					}
				}
			});
		}
	};

}
