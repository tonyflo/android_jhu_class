package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import florida.tony.hw3.ContactListFragment.ContactListFragmentListener;
import florida.tony.hw3.EditFragment.EditFragmentListener;

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
		//displayFragment = (DisplayFragment) getSupportFragmentManager()
		//		.findFragmentById(R.id.displayFragment);

//		FragmentTransaction transaction = getSupportFragmentManager()
//				.beginTransaction();
//		transaction.hide(editFragment);
//		transaction.commit();

		final boolean dualMode = editFragment != null
				&& editFragment.isInLayout();


		listFragment.setListFragmentListener(new ContactListFragmentListener() {
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

			@Override
			public void onEdit(long id) {
				if (dualMode) {
					editFragment.setContactId(id);
				} else {
					Intent intent = new Intent(ContactListActivity.this,
							EditActivity.class);
					intent.putExtra("contactId", id);
					startActivity(intent);
				}

			}
		});

		if (dualMode) {
			editFragment.setEditFragmentListener(new EditFragmentListener() {

				@Override
				public void onDone(Contact contact) {
//					FragmentTransaction transaction = getSupportFragmentManager()
//							.beginTransaction();
//					transaction.hide(editFragment);
//					transaction.show(displayFragment);
//					transaction.commit();
//					displayFragment.setContactId(contact.getId());
				}

				@Override
				public void onCancel() {
					editFragment.setContactId(listFragment.getSelectedId());
//					FragmentTransaction transaction = getSupportFragmentManager()
//							.beginTransaction();
//					transaction.show(editFragment);
//					transaction.hide(displayFragment);
//					transaction.commit();
				}

			});
//			 displayFragment.setDisplayFragmentListener(new
//			 DisplayFragmentListener() {
//			
//			
//			 });
		}
	}
}
