package com.caldroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartapps.helpers.BijoyFontUtil;
import com.smartapps.muslimprayerhelper.R;

/**
 * The CaldroidGridAdapter provides customized view for the dates gridview
 * 
 * @author thomasdao
 * 
 */
public class CaldroidGridAdapter extends BaseAdapter {
	protected ArrayList<DateTime> datetimeList;
	protected int month;
	protected int year;
	protected Context context;
	protected ArrayList<DateTime> disableDates;
	protected ArrayList<DateTime> selectedDates;
	protected DateTime minDateTime;
	protected DateTime maxDateTime;
	protected DateTime today;
	protected int startDayOfWeek;
	private Typeface banglaTypeface;
	private BijoyFontUtil tfUtil;
	/**
	 * caldroidData belongs to Caldroid
	 */
	protected HashMap<String, Object> caldroidData = new HashMap<String, Object>();
	/**
	 * extraData belongs to client
	 */
	protected HashMap<String, Object> extraData = new HashMap<String, Object>();

	public void setAdapterDateTime(DateTime dateTime) {
		this.month = dateTime.getMonthOfYear();
		this.year = dateTime.getYear();
		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek);
	}

	// GETTERS AND SETTERS
	public ArrayList<DateTime> getDatetimeList() {
		return datetimeList;
	}

	public DateTime getMinDateTime() {
		return minDateTime;
	}

	public void setMinDateTime(DateTime minDateTime) {
		this.minDateTime = minDateTime;
	}

	public DateTime getMaxDateTime() {
		return maxDateTime;
	}

	public void setMaxDateTime(DateTime maxDateTime) {
		this.maxDateTime = maxDateTime;
	}

	public ArrayList<DateTime> getDisableDates() {
		return disableDates;
	}

	public void setDisableDates(ArrayList<DateTime> disableDates) {
		this.disableDates = disableDates;
	}

	public ArrayList<DateTime> getSelectedDates() {
		return selectedDates;
	}

	public void setSelectedDates(ArrayList<DateTime> selectedDates) {
		this.selectedDates = selectedDates;
	}

	public HashMap<String, Object> getCaldroidData() {
		return caldroidData;
	}

	public void setCaldroidData(HashMap<String, Object> caldroidData) {
		this.caldroidData = caldroidData;

		// Reset parameters
		populateFromCaldroidData();
	}

	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param month
	 * @param year
	 * @param caldroidData
	 * @param extraData
	 */
	public CaldroidGridAdapter(Context context, int month, int year,
			HashMap<String, Object> caldroidData,
			HashMap<String, Object> extraData) {
		super();
		this.month = month;
		this.year = year;
		this.context = context;
		this.caldroidData = caldroidData;
		this.extraData = extraData;

		// Get data from caldroidData
		populateFromCaldroidData();
	}

	/**
	 * Retrieve internal parameters from caldroid data
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCaldroidData() {
		disableDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.DISABLE_DATES);
		selectedDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.SELECTED_DATES);
		minDateTime = (DateTime) caldroidData
				.get(CaldroidFragment.MIN_DATE_TIME);
		maxDateTime = (DateTime) caldroidData
				.get(CaldroidFragment.MAX_DATE_TIME);
		startDayOfWeek = (Integer) caldroidData
				.get(CaldroidFragment.START_DAY_OF_WEEK);
		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek);
	}

	protected DateTime getToday() {
		if (today == null) {
			today = CalendarHelper.convertDateToDateTime(new Date());
		}
		return today;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.datetimeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;

		// For reuse
		if (convertView == null) {
			cellView = inflater.inflate(R.layout.date_cell_1, null);
		}

		TextView txtEnglish = (TextView) cellView
				.findViewById(R.id.calendar_tv);
		TextView txtArabic = (TextView) cellView
				.findViewById(R.id.calendar_tv_arabic);
		txtEnglish.setTextColor(Color.BLACK);
		txtArabic.setTextColor(Color.BLACK);

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);
		Resources resources = context.getResources();

		// Set color of the dates in previous / next month
		if (dateTime.getMonthOfYear() != month) {
			txtEnglish.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
			txtArabic.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));

		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.isBefore(minDateTime))
				|| (maxDateTime != null && dateTime.isAfter(maxDateTime))
				|| (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

			txtEnglish.setTextColor(CaldroidFragment.disabledTextColor);
			txtArabic.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
			if (CaldroidFragment.selectedBackgroundDrawable != -1) {
				cellView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
			} else {
				cellView.setBackgroundColor(resources
						.getColor(R.color.caldroid_sky_blue));
			}

			txtEnglish.setTextColor(CaldroidFragment.selectedTextColor);
			txtArabic.setTextColor(CaldroidFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for today
			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.red_border);
			} else {
				cellView.setBackgroundResource(R.drawable.cell_bg);
			}
		}

		tfUtil = new BijoyFontUtil();
		banglaTypeface = Typeface.createFromAsset(context.getAssets(),
				"font/suttony.ttf");

//		txtEnglish.setText("" + dateTime.getDayOfMonth());
//		txtArabic.setText("" + dateTime.getDayOfMonth());

		txtEnglish.setTypeface(banglaTypeface);
		txtEnglish.setText(tfUtil.convertUnicode2BijoyString(""
				+ dateTime.getDayOfMonth()));
		txtArabic.setTypeface(banglaTypeface);

		Chronology iso = ISOChronology.getInstanceUTC();
		Chronology hijri = IslamicChronology.getInstanceUTC();

		LocalDate todayIso = new LocalDate(dateTime.getYear(),
				dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), iso);
		LocalDate todayHijri = new LocalDate(todayIso.toDate(), hijri);
		txtArabic.setText(tfUtil.convertUnicode2BijoyString(""
				+ todayHijri.getDayOfMonth()));// conversion needed
		return cellView;
	}

}
