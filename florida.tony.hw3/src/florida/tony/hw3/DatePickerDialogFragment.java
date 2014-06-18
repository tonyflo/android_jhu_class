package florida.tony.hw3;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

/*
 * Code adapted from Scott Stanchfield
 */

public class DatePickerDialogFragment extends DialogFragment {
	public interface OnDatePickerDialogFragmentDateSetListener {
		void onDateSet(int dateId, int year, int month, int day);
	}

	private OnDatePickerDialogFragmentDateSetListener listener;
	private int dateId;
	private int fragmentId;

	public static DatePickerDialogFragment create(Fragment fragment, int dateId) {
		return create(fragment, dateId, Calendar.getInstance());
	}

	public static DatePickerDialogFragment create(Fragment fragment,
			int dateId, Calendar calendar) {
		if (!(fragment instanceof OnDatePickerDialogFragmentDateSetListener))
			throw new IllegalArgumentException(
					"Fragment must implement OnDatePickerDialogFragmentDateSetListener");
		DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
		Bundle args = new Bundle();
		args.putInt("fragmentId", fragment.getId());
		args.putInt("dateId", dateId);
		args.putInt("year", calendar.get(Calendar.YEAR));
		args.putInt("month", calendar.get(Calendar.MONTH));
		args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialogFragment.setArguments(args);
		return datePickerDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		fragmentId = getArguments().getInt("fragmentId");
		dateId = getArguments().getInt("dateId");
		Fragment fragment = getActivity().getSupportFragmentManager()
				.findFragmentById(fragmentId);
		if (fragment == null)
			throw new IllegalStateException("No fragment with id " + fragmentId
					+ " is available");
		if (!fragment.isInLayout())
			throw new IllegalStateException("No fragment with id " + fragmentId
					+ " is in current layout");
		if (!(fragment instanceof OnDatePickerDialogFragmentDateSetListener))
			throw new IllegalArgumentException(
					"Fragment must implement OnDatePickerDialogFragmentDateSetListener");
		listener = (OnDatePickerDialogFragmentDateSetListener) fragment;

		int year = getArguments().getInt("year");
		int day = getArguments().getInt("day");
		int month = getArguments().getInt("month");

		DatePickerDialog dialog = new DatePickerDialog(getActivity(), callBack,
				year, month, day);
		return dialog;
	}

	private OnDateSetListener callBack = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			listener.onDateSet(dateId, year, monthOfYear, dayOfMonth);
		}
	};
}
