package florida.tony.hw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayFragment extends Fragment {

	public interface DisplayFragmentListener {
		void onEdit(Contact contact);

		void onCancel();
	}

	private DisplayFragmentListener displayFragmentListener;

	public void setDisplayFragmentListener(
			DisplayFragmentListener displayFragmentListener) {
		this.displayFragmentListener = displayFragmentListener;
	}

	private TextView displayName;
	private TextView firstName;
	private TextView lastName;
	private TextView birthday;
	private TextView homePhone;
	private TextView workPhone;
	private TextView mobilePhone;
	private TextView emailAddress;

	private Contact contact;

	public void setContactId(long contactId) {
		Log.d("display frag", "set id");
		if (contactId != -1) {
			contact = ContactContentProvider.findContact(getActivity(),
					contactId);
		} else {
			contact = new Contact("", "", "", "", "", "", "", "");
		}

		displayName.setText(contact.getDisplayName());
		firstName.setText(contact.getFirstName());
		lastName.setText(contact.getLastName());
		birthday.setText(contact.getBirthday());
		homePhone.setText(contact.getHomePhone());
		workPhone.setText(contact.getWorkPhone());
		mobilePhone.setText(contact.getMobilePhone());
		emailAddress.setText(contact.getEmailAddress());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_display, container,
				false);

		displayName = (TextView) view.findViewById(R.id.display_name);
		firstName = (TextView) view.findViewById(R.id.first_name);
		lastName = (TextView) view.findViewById(R.id.last_name);
		birthday = (TextView) view.findViewById(R.id.birthday);
		homePhone = (TextView) view.findViewById(R.id.home_phone);
		workPhone = (TextView) view.findViewById(R.id.work_phone);
		mobilePhone = (TextView) view.findViewById(R.id.mobile_phone);
		emailAddress = (TextView) view.findViewById(R.id.email_address);

		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.display, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:

			if (displayFragmentListener == null) {
				throw new RuntimeException(
						"You must set a DisplayFragmentListener");
			}

			displayFragmentListener.onCancel();
			return true;
		case R.id.action_edit:
			this.contact.setDisplayName(displayName.getText().toString());
			this.contact.setFirstName(firstName.getText().toString());
			this.contact.setLastName(lastName.getText().toString());
			this.contact.setBirthday(birthday.getText().toString());
			this.contact.setHomePhone(homePhone.getText().toString());
			this.contact.setWorkPhone(workPhone.getText().toString());
			this.contact.setMobilePhone(mobilePhone.getText().toString());
			this.contact.setEmailAddress(emailAddress.getText().toString());

			ContactContentProvider.updateContact(getActivity(), this.contact);

			if (displayFragmentListener == null) {
				throw new RuntimeException(
						"You must set an EditFragmentListener");
			}
			displayFragmentListener.onEdit(this.contact);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
