package florida.tony.hw2;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

	private static long nextId = 1;
	private long id;
	private String displayName;
	private String firstName;
	private String lastName;
	private String birthday;
	private String homePhone;
	private String workPhone;
	private String mobilePhone;
	private String emailAddress;

	public Contact(String displayName, String firstName, String lastName,
			String birthday, String homePhone, String workPhone,
			String mobilePhone, String emailAddress) {
		super();
		this.id = nextId++;
		this.displayName = displayName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.homePhone = homePhone;
		this.workPhone = workPhone;
		this.mobilePhone = mobilePhone;
		this.emailAddress = emailAddress;
	}
	
	private Contact(long id, String displayName, String firstName, String lastName,
			String birthday, String homePhone, String workPhone,
			String mobilePhone, String emailAddress) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.homePhone = homePhone;
		this.workPhone = workPhone;
		this.mobilePhone = mobilePhone;
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * @param homePhone
	 *            the homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * @return the workPhone
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * @param workPhone
	 *            the workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone
	 *            the mobilePhone to set
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Contact [id=" + id + ", displayName=" + displayName
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", birthday=" + birthday + ", homePhone=" + homePhone
				+ ", mobilePhone=" + mobilePhone + ", emailAddress="
				+ emailAddress + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(displayName);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(birthday);
		dest.writeString(homePhone);
		dest.writeString(workPhone);
		dest.writeString(mobilePhone);
		dest.writeString(emailAddress);
	}
	
	public static Parcelable.Creator<Contact> CREATOR = new Creator<Contact> () {

		@Override
		public Contact createFromParcel(Parcel source) {
			
			long id = source.readLong();
			String displayName = source.readString();
			String firstName = source.readString();
			String lastName = source.readString();
			String birthday = source.readString();
			String homePhone = source.readString();
			String workPhone = source.readString();
			String mobilePhone = source.readString();
			String emailAddress = source.readString();
			
			return new Contact(id, displayName, firstName, lastName, birthday, homePhone, workPhone, mobilePhone, emailAddress);

		}

		@Override
		public Contact[] newArray(int size) {
			return new Contact[size];
		}
		
	};

}
