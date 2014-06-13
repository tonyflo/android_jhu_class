package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import florida.tony.hw3.ContactListFragment.ContactListFragmentListener;
import florida.tony.hw3.DisplayFragment.DisplayFragmentListener;
import florida.tony.hw3.EditFragment.EditFragmentListener;

public class ContactListActivity extends ActionBarActivity {
	private EditFragment editFragment;
	private ContactListFragment listFragment;
	private DisplayFragment displayFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("contact list activity", "on create");
		
		listFragment = (ContactListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.listFragment);
		editFragment = (EditFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editFragment);
		Log.d("a", "e");
		displayFragment = (DisplayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.displayFragment);
		Log.d("a", "d");

		final boolean dualMode = editFragment != null
				&& editFragment.isInLayout();
		Log.d("d fragment", displayFragment + "");
		Log.d("e fragment", editFragment + "");

		if (dualMode) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			Log.d("list activity", displayFragment + "");
			transaction.hide(displayFragment);
			transaction.commit();
		}

		listFragment.setListFragmentListener(new ContactListFragmentListener() {
			@Override
			public void onDisplay(long id) {
				if (dualMode) {
					editFragment.setContactId(id);
				} else {
					Intent intent = new Intent(ContactListActivity.this,
							DisplayActivity.class);
					intent.putExtra("contactId", id);
					startActivity(intent);
				}

			}

			@Override
			public void onCreate() {
				if (dualMode) {
					editFragment.setContactId(-1);
				} else {
					Intent intent = new Intent(ContactListActivity.this,
							EditActivity.class);
					intent.putExtra("contactId", (long) -1);
					startActivity(intent);
				}
			}
		});

		if (false) {
		//if (dualMode) {
			editFragment.setEditFragmentListener(new EditFragmentListener() {

				@Override
				public void onDone(Contact contact) {
					// FragmentTransaction transaction =
					// getSupportFragmentManager()
					// .beginTransaction();
					// transaction.hide(editFragment);
					// transaction.show(displayFragment);
					// transaction.commit();
					// displayFragment.setContactId(contact.getId());
				}

				@Override
				public void onCancel() {
					editFragment.setContactId(listFragment.getSelectedId());
					// FragmentTransaction transaction =
					// getSupportFragmentManager()
					// .beginTransaction();
					// transaction.show(editFragment);
					// transaction.hide(displayFragment);
					// transaction.commit();
				}

			});

			displayFragment
					.setDisplayFragmentListener(new DisplayFragmentListener() {

						@Override
						public void onEdit(Contact contact) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub

						}

					});
		}
	}
}
