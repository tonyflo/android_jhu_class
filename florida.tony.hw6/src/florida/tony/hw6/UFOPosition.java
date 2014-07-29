package florida.tony.hw6;

import android.os.Parcel;
import android.os.Parcelable;

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
	
	/*
	public static Parcelable.Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
		@Override public TodoItem[] newArray(int size) {
			return new TodoItem[size];
		}
		@Override public TodoItem createFromParcel(Parcel source) {
			int id = source.readInt();
			String name = source.readString();
			String displayName = source.readString();
			int priority = source.readInt();
			return new TodoItem(id, name, displayName, priority);
		}
	};
	*/

}
