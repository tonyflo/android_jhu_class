package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayFragment extends Fragment {

	private static final int EDIT_CONTACT = 3;

	private TextView displayName;
	private TextView firstName;
	private TextView lastName;
	private TextView birthday;
	private TextView homePhone;
	private TextView workPhone;
	private TextView mobilePhone;
	private TextView emailAddress;

	private Contact contact;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_display, container,
				false);

		contact = (Contact) getActivity().getIntent().getParcelableExtra(
				"contact");

		displayName = (TextView) view.findViewById(R.id.display_name);
		firstName = (TextView) view.findViewById(R.id.first_name);
		lastName = (TextView) view.findViewById(R.id.last_name);
		birthday = (TextView) view.findViewById(R.id.birthday);
		homePhone = (TextView) view.findViewById(R.id.home_phone);
		workPhone = (TextView) view.findViewById(R.id.work_phone);
		mobilePhone = (TextView) view.findViewById(R.id.mobile_phone);
		emailAddress = (TextView) view.findViewById(R.id.email_address);

		setContactFields();

		setHasOptionsMenu(true);
		return view;
	}

	// populate fields with contact information
	public void setContactFields() {
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:
			getActivity().finish();
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

			Intent intent = new Intent(getActivity(), EditActivity.class);
			intent.putExtra("contact", this.contact);
			startActivityForResult(intent, EDIT_CONTACT);

			getActivity().finish();
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

}
