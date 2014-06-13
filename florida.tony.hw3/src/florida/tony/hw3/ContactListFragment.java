package florida.tony.hw3;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactListFragment extends Fragment {

	public interface ContactListFragmentListener {
		void onCreate();

		void onEdit(long id);
	}
	
	private ListView listView;
	private ContactListFragmentListener listFragmentListener;

	public void setListFragmentListener(
			ContactListFragmentListener listFragmentListener) {
		this.listFragmentListener = listFragmentListener;
	}

	private static final int CONTACT_LOADER = 1;
	private SimpleCursorAdapter cursorAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		listView = (ListView) view.findViewById(R.id.contact_list_view);

		String[] from = { ContactContentProvider.ID,
				ContactContentProvider.DISPLAY_NAME,
				ContactContentProvider.HOME_PHONE, };
		int[] to = { -1, // id not displayed in the layout
				R.id.display_name, R.id.home_phone };

		cursorAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.contact_row, null, from, to, 0);
		listView.setAdapter(cursorAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listFragmentListener == null) {
					throw new RuntimeException(
							"You must register a ContactListFragmentListener first.");
				}

				listFragmentListener.onEdit(id);
			}
		});

		getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER,
				null, loaderCallbacks);

		setHasOptionsMenu(true);
		return view;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create:

			if (listFragmentListener == null) {
				throw new RuntimeException(
						"You must register a ContactListFragmentListener first.");
			}

			listFragmentListener.onCreate();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
			String[] projection = { ContactContentProvider.ID,
					ContactContentProvider.DISPLAY_NAME,
					ContactContentProvider.FIRST_NAME,
					ContactContentProvider.LAST_NAME,
					ContactContentProvider.BIRTHDAY,
					ContactContentProvider.HOME_PHONE,
					ContactContentProvider.WORK_PHONE,
					ContactContentProvider.MOBILE_PHONE,
					ContactContentProvider.EMAIL_ADDRESS };

			return new CursorLoader(getActivity(),
					ContactContentProvider.CONTENT_URI, projection, null, null, // groupby,
																				// having
					ContactContentProvider.DISPLAY_NAME + " asc");
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			cursorAdapter.swapCursor(cursor); // set the data
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursor) {
			cursorAdapter.swapCursor(null); // clear the data
		}
	};

	public long getSelectedId() {
		return listView.getAdapter().getItemId(
				listView.getCheckedItemPosition());
	}
}
