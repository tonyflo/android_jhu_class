package florida.tony.hw2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactListActivity extends ActionBarActivity {

	private static final int DISPLAY_CONTACT = 2;
	private static final int EDIT_CONTACT = 3;
	private ContactList contactList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.contact_list_view);

		contactList = new ContactList();

		listView.setAdapter(new ContactListAdapter(contactList,
				getLayoutInflater()));

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Contact contact = contactList.get(position);

				Intent intent = new Intent(ContactListActivity.this,
						DisplayActivity.class);
				intent.putExtra("contact", contact);
				startActivityForResult(intent, DISPLAY_CONTACT);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			// show display activity
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