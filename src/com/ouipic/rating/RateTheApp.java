package com.ouipic.rating;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;


public class RateTheApp {

	private Context context;
	
	private static final String PREFS_NAME = "RateTheApp";
	private static final String LAUNCH_TIMES = "rta_launch_times";
	private static final String OPT_OUT = "rta_opt_out";	

	private int launchFirst = 10;
	private int launchLater = 5;
	private int launchTimes;
	
	private String title, message, bt_ok, bt_ko, bt_later;
	private Boolean showTitle = true;
	private Boolean showNobutton = true;
	private Boolean opt_out = false;
	
	private SharedPreferences pref;
	private Editor editor;

	public RateTheApp(Context context) {
		
		this.context = context;
		
		// Init Strings
		title = context.getResources().getString(R.string.rta_dialog_title);
		message = context.getResources().getString(R.string.rta_dialog_message);
		bt_ok = context.getResources().getString(R.string.rta_dialog_bt_ok);
		bt_ko = context.getResources().getString(R.string.rta_dialog_bt_ko);
		bt_later = context.getResources().getString(R.string.rta_dialog_bt_later);
		
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setBtOk(String bt_ok) {
		this.bt_ok = bt_ok;
	}

	public void setBtKo(String bt_ko) {
		this.bt_ko = bt_ko;
	}

	public void setBtLater(String bt_later) {
		this.bt_later = bt_later;
	}

	public void setLaunchFirst(int launchFirst) {
		this.launchFirst = launchFirst;
	}

	public void setLaunchLater(int launchLater) {
		this.launchLater = launchLater;
	}

	public void showTitle(Boolean showTitle) {
		this.showTitle = showTitle;
	}

	public void showNoButton(Boolean showNoButton){
		this.showNobutton = showNoButton;
	}

	public void start() {

		pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = pref.edit();

		launchTimes = pref.getInt(LAUNCH_TIMES, 0);
		opt_out = pref.getBoolean(OPT_OUT, false);

		// Increment launch times and save it in prefs
		launchTimes++;
		editor.putInt(LAUNCH_TIMES, launchTimes);
		editor.commit();

		// Checking if user opt out from rating
		if(opt_out == false){
			// Checking if the dialog has to be launched according to "launchFirst"
			if(launchTimes >= launchFirst){
				showDialog();
			}
		}
		
	}


	private void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		// Set dialog title
		if (showTitle == true){
			builder.setTitle(title);
		}
		
		// Set dialog message
		builder.setMessage(message);
		
		// Positive button
		builder.setPositiveButton(bt_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				
				// Launch Google Play based on the package name from the main app (not this package)
				String appPackage = context.getPackageName();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
				context.startActivity(intent);
				
				// Saving opt out in pref as true
				editor.putBoolean(OPT_OUT, true );
				editor.commit();
				
			}
		});
		
		// Neutral button
		builder.setNeutralButton(bt_later, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				
				// Resetting launch time. 
				launchTimes = launchFirst - launchLater;
				
				// Saving launch time in prefs
				editor.putInt(LAUNCH_TIMES, launchTimes);
				editor.commit();
			}
		});
		
		// Negative button
		if (showNobutton == true){
			builder.setNegativeButton(bt_ko, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					
					// Saving opt out in pref as true
					editor.putBoolean(OPT_OUT, true );
					editor.commit();
					
				}
			});
		}
		
		// Cancel (click outside or back button)
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});
		
		builder.create().show();

	}
}
