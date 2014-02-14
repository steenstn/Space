package space.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class MainView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener, OnScaleGestureListener {

	public static int distances = 0;
	public static int sizes = 1;
	public static int orbits = 2;
	
	public static int currentState = distances;
	private static final int INVALID_POINTER_ID = -1;
	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	int pointerIndex = INVALID_POINTER_ID;
	int pointerIndex2 = INVALID_POINTER_ID;
	
	int screenWidth;
	int screenHeight;
	public float screenX = -100, screenY = 0, offsetX = 0, offsetY = 0;
	public float oldScreenX = screenX;
	float oldScreenY = screenY;
	float oldX;
	float oldY;
	float oldX2, oldY2;
	float zoomCenterX = 0, zoomCenterY = 0;
	ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), this);
	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	public RenderThread thread;
	Context ctx;

	double earthRadius = 6378;
	double scale = 2;
	private ArrayList<CelestialBody> bodies;
	
	public MainView(Context context) {
		super(context);
	    sh = getHolder();
	    sh.addCallback(this);
	    paint.setColor(Color.BLUE);
	    paint.setStyle(Style.FILL);
		ctx = context;
	    setFocusable(true); // make sure we get key events
	    setOnTouchListener(this);
	    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay(); 
	    screenWidth = display.getWidth();  
	    screenHeight = display.getHeight();  
	    offsetX = screenWidth/2;
	    offsetY = -screenHeight/2;
	    zoomCenterX = screenWidth/2;
	    zoomCenterY = screenHeight/2;
	    createUniverse();
	    CelestialBody.scale = scale;
	    
	}
		
	  public RenderThread getThread() {
	    return thread;
	  }
	  
	  @Override
	  public void surfaceCreated(SurfaceHolder holder) {
	    thread = new RenderThread(sh, ctx, new Handler(), this, bodies);
	    thread.setRunning(true);
	    thread.start();
	  }
	  @Override
	  public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) 	  {      
	    thread.setSurfaceSize(width, height);
	  }
	  @Override
	  public void surfaceDestroyed(SurfaceHolder holder) {
	    boolean retry = true;
	    thread.setRunning(false);
	    while (retry) {
	      try {
	        thread.join();
	        retry = false;
	      } catch (InterruptedException e) {
	      }
	    }
	  }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		scaleDetector.onTouchEvent(event);
		
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				oldX = (event.getX());
				oldY = (event.getY());
				mActivePointerId = event.getPointerId(0);
			break;
			
			case MotionEvent.ACTION_POINTER_DOWN:
				pointerIndex2 = event.getActionIndex();
				oldX2 = (event.getX(pointerIndex2));
				oldY2 = (event.getY(pointerIndex2));
			break;
			
			case MotionEvent.ACTION_MOVE:
				int numTouch = event.getPointerCount();
				if(numTouch == 1)
				{
					pointerIndex = event.findPointerIndex(mActivePointerId);
					
					float x = event.getX(pointerIndex);
					float y = event.getY(pointerIndex);
					
					
					float dx = x - oldX;
					float dy = y - oldY;
					
					screenX+= dx;
					screenY-= dy;
					oldX = x;
					oldY = y;
					
				}
			break;
			
			case MotionEvent.ACTION_UP:
				mActivePointerId = INVALID_POINTER_ID;
			break;
			
			case MotionEvent.ACTION_CANCEL: {
		        mActivePointerId = INVALID_POINTER_ID;
		        break;
		    }
			case MotionEvent.ACTION_POINTER_UP: {
		        // Extract the index of the pointer that left the touch sensor
		        pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
		                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		        final int pointerId = event.getPointerId(pointerIndex);
		        
		        //if (pointerId == mActivePointerId) {
		            // This was our active pointer going up. Choose a new
		            // active pointer and adjust accordingly.
		            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
		            oldX = event.getX(newPointerIndex);
		            oldY = event.getY(newPointerIndex);
		            mActivePointerId = event.getPointerId(newPointerIndex);
		        //}
		        pointerIndex2 = INVALID_POINTER_ID;
		        break;
		    }
		
		}
		//calculateDistanceAndScale();
		return true;
	}
  	
	public void calculateDistanceAndScale()
	{
		if(currentState == sizes)
		{
			double distanceSum = 0;
			double distance;
	
			bodies.get(0).setDrawingRadius((bodies.get(0).getRadius() / earthRadius) * scale);
			bodies.get(0).setDrawingX(screenX + zoomCenterX + distanceSum);
			bodies.get(0).setDrawingY(-screenY + zoomCenterY + bodies.get(0).getY() * scale);
			
			for(int i = 1; i < bodies.size(); i++)
			{
				bodies.get(i).setDrawingRadius((bodies.get(i).getRadius() / earthRadius) * scale);
				
				if(bodies.get(i).getClass() == Planet.class)
				{
					distance = (bodies.get(i-1).getRadius()/CelestialBody.earthRadius)*scale 
							+ 50*scale + bodies.get(i).getRadius()/CelestialBody.earthRadius*scale;
					
					distanceSum += distance;
					bodies.get(i).setDrawingX(screenX + zoomCenterX + distanceSum);
					bodies.get(i).setDrawingY(-screenY + zoomCenterY + bodies.get(i).getY() * scale);
				}
				else if(bodies.get(i).getClass() == Satellite.class)
				{
					((Satellite)bodies.get(i)).setDrawingDistance(2*((Satellite)bodies.get(i)).getPlanet().getRadius()/CelestialBody.earthRadius*scale 
							+ 4*bodies.get(i).getRadius()/CelestialBody.earthRadius*scale*i);
					
				}
			}
	
		}
		else if(currentState == distances)
		{
	    	for(int i = 0; i < bodies.size(); i++)
	    	{
	    		bodies.get(i).calculateDistanceAndScale(screenX, screenY, zoomCenterX, zoomCenterY, scale);
	    	}
		}
	}
	
	private double lyToKm(double lightYears)
	{
		return lightYears * 9.46e12;
	}
	
	
	private void createUniverse()
    {
    	double mercuryRadius = 2440;
    	double sunRadius = 695500;
    	double venusRadius = 6051;
    	double marsRadius = 3397;
    	double jupiterRadius = 71492;
    	double saturnRadius = 60268;
    	double uranusRadius = 25559;
    	double neptuneRadius = 24764;
    	
    	// Distance from sun to planets in kilometers
    	double sunMercuryDist = 57910000/earthRadius;
    	double sunVenusDist = 108200000/earthRadius;
    	double sunEarthDist = 149600000/earthRadius;
    	double sunMarsDist = 227900000/earthRadius;
    	double sunJupiterDist = 778500000/earthRadius;
    	double sunSaturnDist = 1433000000/earthRadius;
    	double sunUranusDist = 2.877e9/earthRadius;
    	double sunNeptuneDist = 4.503e9/earthRadius;
    	double sunVYCanisMajorisDist = 4.628e16/earthRadius;
    	
    	double sunVoyagerDist = 18642695100.0/earthRadius;
    	bodies = new ArrayList<CelestialBody>();
    	
    	bodies.add(new Planet("The sun", sunRadius,0, 0, 0, 255, 255, 0));
    	bodies.add(new Planet("Mercury", mercuryRadius, sunMercuryDist, 0, 0.24, 250,0,40));
    	bodies.add(new Planet("Venus", venusRadius, sunVenusDist, 0, 0.615, 180,180,0));
    	bodies.add(new Planet("Earth", earthRadius, sunEarthDist, 0, 1, 0,255,0));
    	bodies.add(new Planet("Mars", marsRadius, sunMarsDist, 0, 2.135, 221,1,1));
    	bodies.add(new Planet("Jupiter", jupiterRadius, sunJupiterDist, 0, 11.8618, 252,170,170));
    	bodies.add(new Planet("Saturn", saturnRadius, sunSaturnDist, 0, 29.45, 0,250,250));
    	bodies.add(new Planet("Uranus", uranusRadius, sunUranusDist, 0, 84.32, 17,170,170));
    	bodies.add(new Planet("Neptune", neptuneRadius, sunNeptuneDist, 0, 164.79, 34,34,255));

    	bodies.add(new Planet("Voyager 1 (as of December 2013)", 1, sunVoyagerDist, 0, 0, 200,200,200));

    	bodies.add(new Planet("Sirius", 1.711 * sunRadius, lyToKm(8.6) / earthRadius, 0, 0, 175,175,255));
    	bodies.add(new Planet("Capella", 12.2 * sunRadius, lyToKm(42.2) / earthRadius, 0, 0, 255,170,0));
    	bodies.add(new Planet("Arcturus", 25.7 * sunRadius, lyToKm(36.6) / earthRadius, 0, 0, 255,221,0));
    	bodies.add(new Planet("Aldebaran", 44.2 * sunRadius, lyToKm(65.23) / earthRadius, 0, 0, 255,170,0));
    	bodies.add(new Planet("VY Canis Majoris", sunRadius * 1420, sunVYCanisMajorisDist ,0, 0, 250,170,0));
    	bodies.add(new Planet("NML Cygni", 1650 * sunRadius, lyToKm(5300) / earthRadius, 0, 0, 255,204,51));

    	
    	double earthMoonDist = 384400/earthRadius;

    	double jupiterEuropaDist = 671000/earthRadius;
    	double jupiterIoDist = 422000/earthRadius;
    	double jupiterGanymedeDist = 1070000/earthRadius;
    	double jupiterCallistoDist = 1883000/earthRadius;

    	double saturnTitanDist = 1.2e6/earthRadius;

    	bodies.add(new Satellite("The moon", 1737,earthMoonDist,(Planet)bodies.get(3), 0.074, 200,200,200));
    	// Jupiter's moons
    	bodies.add(new Satellite("Europa", 1560, jupiterEuropaDist,(Planet)bodies.get(5), 0.0097, 170,135,176));
    	bodies.add(new Satellite("Io", 1815, jupiterIoDist,(Planet)bodies.get(5), 0.0048, 175,170,238));
    	bodies.add(new Satellite("Ganymede", 2634, jupiterGanymedeDist,(Planet)bodies.get(5), 0.0196, 175,136,136));
    	bodies.add(new Satellite("Callisto", 2403, jupiterCallistoDist,(Planet)bodies.get(5), 0.0458, 153,175,175));

    	bodies.add(new Satellite("Titan",2575, saturnTitanDist,(Planet)bodies.get(6), 0.044, 170,85,17));
    	bodies.add(new Satellite("International Space Station", 0.01, (400+earthRadius)/earthRadius, (Planet)bodies.get(3), 0.0001767, 250,250,250));
    	
    	calculateDistanceAndScale();
    }

	@Override
	public boolean onScale(ScaleGestureDetector detector) {

		if(detector.getCurrentSpan() - detector.getPreviousSpan() > 0)
			scale *= 1+(detector.getCurrentSpan() - detector.getPreviousSpan()) / 400.0f;
		else if(detector.getCurrentSpan() - detector.getPreviousSpan() < 0)
			scale /= 1+Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan()) / 400.0f;
		
		CelestialBody.scale = scale;
		screenX = (float) (oldScreenX * scale);
		screenY = (float) (oldScreenY * scale);
		calculateDistanceAndScale();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		oldScreenY = (float) (screenY / scale);
		oldScreenX = (float) (screenX / scale);
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		
	}
  	
	
}
