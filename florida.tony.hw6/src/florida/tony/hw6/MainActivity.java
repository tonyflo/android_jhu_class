package florida.tony.hw6;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity {
	private static final int GOOGLE_PLAY_SETUP = 42;
	private static final String LON_PREF = "lon";
	private static final String LAT_PREF = "lat";
	private LatLng savedLocation;
	private GoogleMap googleMap;
	private Marker marker;
	private BitmapDescriptor blueCar;
	private Marker fakeLocationMarker;
	private BitmapDescriptor locationIcon;
	private Polyline route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		blueCar = BitmapDescriptorFactory.fromResource(R.drawable.car_blue);
		locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_fake_location);
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
		MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		googleMap = map.getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override public void onMyLocationChange(Location location) {
				CameraUpdate cameraUpdate =
						CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
				googleMap.animateCamera(cameraUpdate);
				// only move the first time
				googleMap.setOnMyLocationChangeListener(null);
			}});
		
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override public boolean onMarkerClick(Marker marker) {
				if (marker.equals(fakeLocationMarker)) {
					fakeLocationMarker.remove();
					fakeLocationMarker = null;
				}
				return false;
			}});
		googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override public void onMapLongClick(LatLng point) {
				if (fakeLocationMarker != null)
					fakeLocationMarker.remove();
				fakeLocationMarker = googleMap.addMarker(new MarkerOptions()
															.position(point)
															.draggable(true)
															.icon(locationIcon));
			}});
		
		// if you want to track draggable markers...
		googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override public void onMarkerDragStart(Marker marker) {
			}
			@Override public void onMarkerDragEnd(Marker marker) {
			}
			@Override public void onMarkerDrag(Marker marker) {
			}
		});
		
		
    	googleMap.setMyLocationEnabled(true);
    	googleMap.getUiSettings().setCompassEnabled(true);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        
        // default location is APL parking lot
        String latString = preferences.getString(LAT_PREF, "39.171571");
        String lonString = preferences.getString(LON_PREF, "-76.896910");
       	double lat = Double.parseDouble(latString);
       	double lon = Double.parseDouble(lonString);
       	updateSavedLocation(lat, lon);
	}	
	
	@Override protected void onPause() {
		googleMap.setMyLocationEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(false);
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(LAT_PREF, savedLocation.latitude + "");
		editor.putString(LON_PREF, savedLocation.longitude + "");
		editor.commit();
		super.onPause();
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true; 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.remember:
				if (marker != null)
					marker.remove();
				double lat;
				double lon;
				
				if (fakeLocationMarker != null) {
					LatLng position = fakeLocationMarker.getPosition();
					lat = position.latitude;
					lon = position.longitude;
				} else {
					Location location = googleMap.getMyLocation();
					lat = location.getLatitude();
					lon = location.getLongitude();
				}
				updateSavedLocation(lat, lon);
				return true;
			case R.id.navigate:
				Intent intent = 
					new Intent(android.content.Intent.ACTION_VIEW,
								Uri.parse("google.navigation:q=" + savedLocation.latitude+","+savedLocation.longitude));
				startActivity(intent);
				return true;
			case R.id.showRoute:
				showRoute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateSavedLocation(double lat, double lon) {
		if (marker != null)
			marker.remove();
		savedLocation = new LatLng(lat, lon);
		marker = googleMap.addMarker(new MarkerOptions()
							.position(savedLocation)
							.icon(blueCar)
							.title("Saved Location"));
	}
	
    private void showRoute() {
    	AsyncTask<Location, Void, List<LatLng>> task = new AsyncTask<Location, Void, List<LatLng>>() {
    		@Override protected void onPreExecute() {
    			if (route != null)
    				route.remove();
    			route = null;
    		}

    		@Override protected void onPostExecute(List<LatLng> points) {
				route = googleMap.addPolyline(new PolylineOptions()
													.addAll(points)
													.color(Color.RED)
													.width(8));
    		}
    		
			@Override protected List<LatLng> doInBackground(Location... myLocations) {
				try {
					Location myLocation = myLocations[0];
					
					// ask google maps for the route as JSON
//					String urlString = "http://maps.googleapis.com/maps/api/directions/json?origin=" + myLocation.getLatitude() + ',' + myLocation.getLongitude() + "&destination=" + savedLocation.latitude + ',' + savedLocation.longitude + "&sensor=true";
					String urlString = "http://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=" + myLocation.getLatitude() + ',' + myLocation.getLongitude() + "&destination=" + savedLocation.latitude + ',' + savedLocation.longitude + "&sensor=true";
					
					Log.d("", "Route URL=" + urlString);
					URL url = new URL(urlString);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.connect();
					
					InputStream inputStream = connection.getInputStream();
					InputStreamReader isr = new InputStreamReader(inputStream);
					BufferedReader br = new BufferedReader(isr);
					StringBuilder sb = new StringBuilder();
					String line;
					while((line = br.readLine()) != null) {
						sb.append(line);
						sb.append('\n');
					}
					String json = sb.toString();
					JSONObject jsonObject = new JSONObject(json);
					
					List<LatLng> points = new ArrayList<LatLng>();
					
					JSONArray steps = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
					for (int i = 0; i < steps.length(); i++) {
						JSONObject step = steps.getJSONObject(i);
						String pointsString = step.getJSONObject("polyline").getString("points");
						points.addAll(decodePoly(pointsString));
						Log.d("STEP POINTS", pointsString);
						Log.d("DECODED", points.toString());
					}
					
					return points;
    			} catch (ThreadDeath t) {
    				throw t;
    			} catch (Throwable t) {
    				Log.e("", "Error showing route", t);
    				throw new RuntimeException(t);
    			}
			}
			
    	};
    	
    	task.execute(googleMap.getMyLocation());
    }
    
    // from http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
    //   with tweaks for Maps API v2
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;
    }
}
