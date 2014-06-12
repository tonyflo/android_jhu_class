package florida.tony.hw3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditFragment extends Fragment {

	public interface EditFragmentListener {
		void onDone(Contact contact);

		void onCancel();
	}

	private EditFragmentListener editFragmentListener;

	private EditText displayName;
	private EditText firstName;
	private EditText lastName;
	private EditText birthday;
	private EditText homePhone;
	private EditText workPhone;
	private EditText mobilePhone;
	private EditText emailAddress;

	private Contact contact;

	public void setContactId(long contactId) {
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
		View view = inflater.inflate(R.layout.fragment_edit, container, false);

		displayName = (EditText) view.findViewById(R.id.display_name);
		firstName = (EditText) view.findViewById(R.id.first_name);
		lastName = (EditText) view.findViewById(R.id.last_name);
		birthday = (EditText) view.findViewById(R.id.birthday);
		homePhone = (EditText) view.findViewById(R.id.home_phone);
		workPhone = (EditText) view.findViewById(R.id.work_phone);
		mobilePhone = (EditText) view.findViewById(R.id.mobile_phone);
		emailAddress = (EditText) view.findViewById(R.id.email_address);

		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:
			if (editFragmentListener == null) {
				throw new RuntimeException(
						"You must set an EditFragmentListener");
			}

			editFragmentListener.onCancel();

			return true;
		case R.id.action_done:
			this.contact.setDisplayName(displayName.getText().toString());
			this.contact.setFirstName(firstName.getText().toString());
			this.contact.setLastName(lastName.getText().toString());
			this.contact.setBirthday(birthday.getText().toString());
			this.contact.setHomePhone(homePhone.getText().toString());
			this.contact.setWorkPhone(workPhone.getText().toString());
			this.contact.setMobilePhone(mobilePhone.getText().toString());
			this.contact.setEmailAddress(emailAddress.getText().toString());

			ContactContentProvider.updateContact(getActivity(), this.contact);

			if (editFragmentListener == null) {
				throw new RuntimeException(
						"You must set an EditFragmentListener");
			}
			editFragmentListener.onDone(this.contact);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.edit, menu);
	}

	public void setEditFragmentListener(
			EditFragmentListener editFragmentListener) {
		this.editFragmentListener = editFragmentListener;
	}
}
