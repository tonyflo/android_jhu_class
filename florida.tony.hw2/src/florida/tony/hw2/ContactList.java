package florida.tony.hw2;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactList implements Parcelable {

	public interface ChangeListener {
		void changed();
	}

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
	private List<Contact> contacts = new ArrayList<Contact>();

	public void addChangeListener(ChangeListener changeListener) {
		listeners.add(changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		listeners.remove(changeListener);
	}

	public void merge(Contact contact) {
		boolean found = false;
		for (Contact contactInList : contacts) {
			if (contactInList.getId() == contact.getId()) {
				contactInList.setDisplayName(contact.getDisplayName());
				contactInList.setFirstName(contact.getFirstName());
				contactInList.setLastName(contact.getLastName());
				contactInList.setBirthday(contact.getBirthday());
				contactInList.setHomePhone(contact.getHomePhone());
				contactInList.setWorkPhone(contact.getWorkPhone());
				contactInList.setMobilePhone(contact.getMobilePhone());
				contactInList.setHomePhone(contact.getHomePhone());
				found = true;
				break;
			}
		}

		if (!found) {
			contacts.add(contact);
		}

		for (ChangeListener changeListener : listeners) {
			changeListener.changed();
		}
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Contact contact) {
		boolean added = contacts.add(contact);
		for (ChangeListener changeListener : listeners) {
			changeListener.changed();
		}
		return added;
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.List#get(int)
	 */
	public Contact get(int location) {
		return contacts.get(location);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.List#remove(int)
	 */
	public Contact remove(int location) {
		Contact removed = contacts.remove(location);
		for (ChangeListener changeListener : listeners) {
			changeListener.changed();
		}
		return removed;
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return contacts.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContactList [Items=" + contacts + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(contacts.size());
		for (Contact contact : contacts) {
			dest.writeParcelable(contact, 0);
		}
	}

	public static Parcelable.Creator<ContactList> CREATOR = new Creator<ContactList>() {

		@Override
		public ContactList createFromParcel(Parcel source) {
			ContactList list = new ContactList();
			int size = source.readInt();

			for (int i = 0; i < size; i++) {
				Contact contact = source.readParcelable(getClass()
						.getClassLoader());
				list.add(contact);
			}

			return list;
		}

		@Override
		public ContactList[] newArray(int size) {
			return new ContactList[size];
		}

	};

}
