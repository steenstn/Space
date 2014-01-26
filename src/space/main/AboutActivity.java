package space.main;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends FragmentActivity{

	String version = "";
	String versionName = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        try {
			version = "Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			version = "Unknown version";
		}
        
        
        TextView versionTextView = (TextView)findViewById(R.id.textViewVersion);
        versionTextView.setText(version);
	}
	
	public void sendFeedback(View v) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"alest849@gmail.com"});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SimpleSpace feedback");
		

		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
		boolean isIntentSafe = activities.size() > 0;

		if(isIntentSafe)
		{
			startActivity(emailIntent);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Could not find any email application to use.", Toast.LENGTH_LONG).show();
		}
	}
	
	public void rateApp(View v) {
		final String appPackageName = getPackageName(); 
		try {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}
	
}
