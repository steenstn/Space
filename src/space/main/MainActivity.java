package space.main;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {
	
	MainView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        //                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new MainView(this);
        setContentView(view);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		view.screenX = savedInstanceState.getFloat("screenX");
        view.screenY = savedInstanceState.getFloat("screenY");
        view.scale = savedInstanceState.getDouble("scale");
        view.offsetX = savedInstanceState.getFloat("offsetX");
        view.offsetY = savedInstanceState.getFloat("offsetY");
        view.currentState = savedInstanceState.getInt("state");
        
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);

		  savedInstanceState.putFloat("screenX", view.screenX);
		  savedInstanceState.putFloat("screenY", view.screenY);
		  savedInstanceState.putDouble("scale", view.scale);
		  savedInstanceState.putFloat("offsetX", view.offsetX);
		  savedInstanceState.putFloat("offsetY", view.offsetY);
		  savedInstanceState.putInt("state", view.currentState);
		  
	}
	@Override
	public void onPause()
	{
		super.onPause();
		view.thread.setRunning(false);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(view.thread!=null)
		{
			view.thread.setRunning(true);
		}

		view.calculateDistanceAndScale();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
	        case R.id.menuShowDistances:
		        view.currentState = view.distances;
		        view.calculateDistanceAndScale();
	        	return true;
	        case R.id.menuShowSizes:
	        	view.currentState = view.sizes;
	        	view.calculateDistanceAndScale();
	        	return true;
	        case R.id.menuAbout:
	        	Intent intent = new Intent(this,AboutActivity.class);
            	startActivity(intent);
        	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}

}
