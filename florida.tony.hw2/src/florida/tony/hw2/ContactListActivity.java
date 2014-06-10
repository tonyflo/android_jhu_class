package florida.tony.hw2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactListActivity extends ActionBarActivity {

	private static final int DISPLAY_CONTACT = 2;
	private static final int EDIT_CONTACT = 3;
	private ContactList contactList;
	ListView listView;
	private ContactListAdapter contactListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.contact_list_view);

		if (savedInstanceState == null) {

			contactList = new ContactList();
		} else {
			contactList = savedInstanceState.getParcelable("contactList");
		}

		contactListAdapter = new ContactListAdapter(contactList,
				getLayoutInflater());
		listView.setAdapter(contactListAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Contact contact = contactList.get(position);

				// implicit intent
				Intent intent = new Intent("florida.tony.hw2.display");
				intent.putExtra("contact", contact);
				startActivityForResult(intent, DISPLAY_CONTACT);

			}
		});
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save the contact list
		outState.putParcelable("contactList", contactList);
		;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create:

			// explicit intent
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("contact", new Contact("", "", "", "", "", "", "",
					""));
			startActivityForResult(intent, DISPLAY_CONTACT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED)
			return;

		switch (requestCode) {
		case DISPLAY_CONTACT:
		case EDIT_CONTACT:
			Contact contact = (Contact) data.getSerializableExtra("contact");
			contactList.merge(contact);
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

}
