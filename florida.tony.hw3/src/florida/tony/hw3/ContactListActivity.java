package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import florida.tony.hw3.ContactListFragment.ContactListFragmentListener;
import florida.tony.hw3.DisplayFragment.DisplayFragmentListener;
import florida.tony.hw3.EditFragment.EditFragmentListener;

/*
 * Code adapted from Scott Stanchfield
 */

public class ContactListActivity extends ActionBarActivity {
	private EditFragment editFragment;
	private ContactListFragment listFragment;
	private DisplayFragment displayFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listFragment = (ContactListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.listFragment);
		editFragment = (EditFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editFragment);
		displayFragment = (DisplayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.displayFragment);

		final boolean dualMode = editFragment != null
				&& editFragment.isInLayout();

		if (dualMode) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.hide(displayFragment);
			transaction.commit();
			editFragment.setContactId(-1);
		}

		listFragment.setListFragmentListener(new ContactListFragmentListener() {
			@Override
			public void onDisplay(long id) {
				if (dualMode) {
					displayFragment.setContactId(id);

					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.hide(editFragment);
					transaction.show(displayFragment);
					transaction.commit();

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

					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.hide(displayFragment);
					transaction.show(editFragment);
					transaction.commit();

				} else {
					Intent intent = new Intent(ContactListActivity.this,
							EditActivity.class);
					intent.putExtra("contactId", (long) -1);
					startActivity(intent);
				}
			}
		});

		if (dualMode) {
			editFragment.setEditFragmentListener(new EditFragmentListener() {
				@Override
				public void onDone(Contact contact) {
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.hide(editFragment);
					transaction.show(displayFragment);
					transaction.commit();
					displayFragment.setContactId(contact.getId());
				}

				@Override
				public void onCancel() {
					editFragment.setContactId(listFragment.getSelectedId());

					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.hide(editFragment);
					transaction.show(displayFragment);
					transaction.commit();
				}
			});

			displayFragment
					.setDisplayFragmentListener(new DisplayFragmentListener() {
						@Override
						public void onEdit(Contact contact) {
							FragmentTransaction transaction = getSupportFragmentManager()
									.beginTransaction();
							transaction.hide(displayFragment);
							transaction.show(editFragment);
							transaction.commit();
							editFragment.setContactId(contact.getId());
						}

						@Override
						public void onCancel() {
							editFragment.setContactId(-1);

							FragmentTransaction transaction = getSupportFragmentManager()
									.beginTransaction();
							transaction.hide(displayFragment);
							transaction.show(editFragment);
							transaction.commit();
						}
					});
		}
	}
}
