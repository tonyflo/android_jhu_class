package florida.tony.hw3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import florida.tony.hw3.EditFragment.EditFragmentListener;

/*
 * Code adapted from Scott Stanchfield
 */

public class EditActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		EditFragment editFragment = (EditFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editFragment);

		long contactId = getIntent().getLongExtra("contactId", -1);
		editFragment.setContactId(contactId);

		editFragment.setEditFragmentListener(new EditFragmentListener() {

			@Override
			public void onDone(Contact contact) {
				getIntent().putExtra("contactId", contact.getId());
				finish();
			}

			@Override
			public void onCancel() {
				finish();
			}
		});
	}
}
