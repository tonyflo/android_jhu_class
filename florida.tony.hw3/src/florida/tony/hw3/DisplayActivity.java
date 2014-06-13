package florida.tony.hw3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import florida.tony.hw3.DisplayFragment.DisplayFragmentListener;

/*
 * Code adapted from Scott Stanchfield
 */

public class DisplayActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		Log.d("display", "on create");

		DisplayFragment displayFragment = (DisplayFragment) getSupportFragmentManager()
				.findFragmentById(R.id.displayFragment);

		long contactId = getIntent().getLongExtra("contactId", -1);
		displayFragment.setContactId(contactId);

		displayFragment
				.setDisplayFragmentListener(new DisplayFragmentListener() {

					@Override
					public void onEdit(Contact contact) {
						Log.d("display", "edit");
						Intent intent = new Intent(DisplayActivity.this,
								EditActivity.class);
						intent.putExtra("contactId", contact.getId());
						startActivity(intent);
					}

					@Override
					public void onCancel() {
						Log.d("display", "cancel");
						finish();
					}

				});
	}
}
