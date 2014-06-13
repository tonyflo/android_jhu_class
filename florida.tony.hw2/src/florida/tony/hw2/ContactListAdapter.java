package florida.tony.hw2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import florida.tony.hw2.ContactList.ChangeListener;

/*
 * Code adapted from Scott Stanchfield
 */

public class ContactListAdapter extends BaseAdapter {

	private ContactList contactList;
	private LayoutInflater layoutInflater;

	public ContactListAdapter(ContactList contactList,
			LayoutInflater layoutInflater) {
		this.contactList = contactList;
		this.layoutInflater = layoutInflater;

		contactList.addChangeListener(new ChangeListener() {
			@Override
			public void changed() {
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getCount() {
		return contactList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.contact_row, null);
		}

		TextView contactName = (TextView) convertView
				.findViewById(R.id.display_name);
		TextView contactPhone = (TextView) convertView
				.findViewById(R.id.home_phone);

		Contact contact = (Contact) getItem(position);
		contactName.setText(contact.getDisplayName());
		contactPhone.setText(contact.getHomePhone());

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return contactList.size() == 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
