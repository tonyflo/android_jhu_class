package florida.tony.hw6;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//describes a ufo object
public class UFOPosition implements Parcelable {

	private int shipNumber;
	private double lat;
	private double lon;

	public UFOPosition(int shipNumber, double lat, double lon) {
		this.shipNumber = shipNumber;
		this.lat = lat;
		this.lon = lon;
	}

	public static Parcelable.Creator<UFOPosition> CREATOR = new Creator<UFOPosition>() {
		@Override
		public UFOPosition[] newArray(int size) {
			return new UFOPosition[size];
		}

		@Override
		public UFOPosition createFromParcel(Parcel source) {
			int shipNumber = source.readInt();
			double lat = source.readDouble();
			double lon = source.readDouble();
			return new UFOPosition(shipNumber, lat, lon);
		}
	};

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

	public void printUFOPosition() {
		Log.d("UFOPositon", "Ship: " + this.getShipNumber() + "\n" + "Lat: "
				+ this.getLat() + "\n" + "Lon: " + this.getLon());
	}

}
