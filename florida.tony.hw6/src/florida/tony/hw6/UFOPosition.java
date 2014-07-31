package florida.tony.hw6;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class UFOPosition implements Parcelable{
	
	private int shipNumber;
	private double lat;
	private double lon;

	public int getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(int shipNumber) {
		this.shipNumber = shipNumber;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	public void printUFOPosition()
	{
		Log.d("UFOPositon", "Ship: " + this.getShipNumber() + "\n"
				+ "Lat: " + this.getLat() + "\n"
				+ "Lon: " + this.getLon());
	}

}
