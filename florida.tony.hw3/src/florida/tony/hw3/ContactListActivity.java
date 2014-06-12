package florida.tony.hw3;

import florida.tony.hw3.ContactListFragment.ContactListFragmentListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ContactListActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ContactListFragment listFragment = (ContactListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.list_fragment);

		listFragment.setListFragmentListener(new ContactListFragmentListener() {
			@Override
			public void onCreate() {
				Intent intent = new Intent(ContactListActivity.this,
						EditActivity.class);
				intent.putExtra("contactId", (long) -1);
				startActivity(intent);
			}

			@Override
			public void onEdit(long id) {

				Intent intent = new Intent(ContactListActivity.this,
						EditActivity.class);
				intent.putExtra("contactId", id);
				startActivity(intent);

			}
		});
	}
}
