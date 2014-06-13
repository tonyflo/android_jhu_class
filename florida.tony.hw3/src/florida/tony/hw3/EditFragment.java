package florida.tony.hw3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import florida.tony.hw3.DatePickerDialogFragment.OnDatePickerDialogFragmentDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/*
 * Code adapted from Scott Stanchfield
 */

public class EditFragment extends Fragment implements
		OnDatePickerDialogFragmentDateSetListener {

	public interface EditFragmentListener {
		void onDone(Contact contact);

		void onCancel();
	}

	private static final int BIRTH_DAY = 1;

	private EditFragmentListener editFragmentListener;

	public void setEditFragmentListener(
			EditFragmentListener editFragmentListener) {
		this.editFragmentListener = editFragmentListener;
	}

	private EditText displayName;
	private EditText firstName;
	private EditText lastName;
	private Button birthdayButton;
	private EditText homePhone;
	private EditText workPhone;
	private EditText mobilePhone;
	private EditText emailAddress;

	private Calendar birthday = Calendar.getInstance();

	private Contact contact;

	public void setContactId(long contactId) {
		Log.d("edit frag", "set id");
		if (contactId != -1) {
			contact = ContactContentProvider.findContact(getActivity(),
					contactId);
		} else {
			contact = new Contact("", "", "", "", "", "", "", "");
		}

		displayName.setText(contact.getDisplayName());
		firstName.setText(contact.getFirstName());
		lastName.setText(contact.getLastName());
		homePhone.setText(contact.getHomePhone());
		workPhone.setText(contact.getWorkPhone());
		mobilePhone.setText(contact.getMobilePhone());
		emailAddress.setText(contact.getEmailAddress());
		
		//get text for birthday button
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date birthdayAsDate;
		try {
			if(!contact.getBirthday().isEmpty())
			{
				birthdayAsDate = format.parse(contact.getBirthday());
				birthday.setTime(birthdayAsDate);	
			}
			updateDateButtonText("Birthday", birthdayButton, birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("edit frag", "on create");
		View view = inflater.inflate(R.layout.fragment_edit, container, false);

		displayName = (EditText) view.findViewById(R.id.display_name);
		firstName = (EditText) view.findViewById(R.id.first_name);
		lastName = (EditText) view.findViewById(R.id.last_name);
		birthdayButton = (Button) view.findViewById(R.id.birthdayButton);
		homePhone = (EditText) view.findViewById(R.id.home_phone);
		workPhone = (EditText) view.findViewById(R.id.work_phone);
		mobilePhone = (EditText) view.findViewById(R.id.mobile_phone);
		emailAddress = (EditText) view.findViewById(R.id.email_address);

		if (savedInstanceState != null) {
			birthday.setTimeInMillis(savedInstanceState.getLong("birthday"));
		} else {
			birthday.set(2012, 12, 12);
		}

		updateDateButtonText("Birthday", birthdayButton, birthday);
		
		birthdayButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				DatePickerDialogFragment fragment = DatePickerDialogFragment.create(EditFragment.this, BIRTH_DAY, birthday);
				fragment.show(getActivity().getSupportFragmentManager(), "setting birthday");
			}});

		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("birthday", birthday.getTimeInMillis());
	}

	private void updateDateButtonText(String header, Button button,
			Calendar calendar) {
		button.setText(header + ": " + calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.edit, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:
			Log.d("edit frag", "cancel");
			if (editFragmentListener == null) {
				throw new RuntimeException(
						"You must set an EditFragmentListener");
			}

			editFragmentListener.onCancel();

			return true;
		case R.id.action_done:
			Log.d("edit frag", "done");
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			String formattedBirthday = format.format(birthday.getTime());
			
			this.contact.setDisplayName(displayName.getText().toString());
			this.contact.setFirstName(firstName.getText().toString());
			this.contact.setLastName(lastName.getText().toString());
			this.contact.setBirthday(formattedBirthday);
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
	public void onDateSet(int dateId, int year, int month, int day) {
		switch (dateId) {
		case BIRTH_DAY:
			birthday.set(year, month, day);
			updateDateButtonText("Birthday", birthdayButton, birthday);
			break;
		default:
			throw new IllegalStateException("unexpected date id");
		}
	}
}
