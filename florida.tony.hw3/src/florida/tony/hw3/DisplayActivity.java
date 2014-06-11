package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayActivity extends ActionBarActivity {

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);

		contact = (Contact) getIntent().getParcelableExtra("contact");

		displayName = (TextView) findViewById(R.id.display_name);
		firstName = (TextView) findViewById(R.id.first_name);
		lastName = (TextView) findViewById(R.id.last_name);
		birthday = (TextView) findViewById(R.id.birthday);
		homePhone = (TextView) findViewById(R.id.home_phone);
		workPhone = (TextView) findViewById(R.id.work_phone);
		mobilePhone = (TextView) findViewById(R.id.mobile_phone);
		emailAddress = (TextView) findViewById(R.id.email_address);

		setContactFields();

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
			setResult(RESULT_CANCELED);
			finish();
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

			Intent intent = new Intent(DisplayActivity.this, EditActivity.class);
			intent.putExtra("contact", this.contact);
			startActivityForResult(intent, EDIT_CONTACT);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode) {
		case EDIT_CONTACT:
			contact = (Contact) data.getParcelableExtra("contact");

			// update contact fields
			setContactFields();

			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		getIntent().putExtra("contact", this.contact);
		setResult(RESULT_OK, getIntent());
		finish();
	}

}
