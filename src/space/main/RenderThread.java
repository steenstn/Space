package space.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

class RenderThread extends Thread {
	  private boolean run = false;

	  private SurfaceHolder sh;
	  private ArrayList<CelestialBody> bodies;
	  private MainView view;
	  
	  public RenderThread(SurfaceHolder surfaceHolder, Context context,
	         Handler handler, MainView view, ArrayList<CelestialBody> bodies) {
	    sh = surfaceHolder;
	    this.bodies = bodies;
	    this.view = view;
	  }
	  public void doStart() {
	    synchronized (sh) {
	    }
	  }
	  public void run() {
	    while (run) {
	      Canvas c = null;
	      try {
	        c = sh.lockCanvas(null);
	        synchronized (sh) {
	        	calculateDistanceAndScale();
	          doDraw(c);
	        }
	      } finally {
	        if (c != null) {
	          sh.unlockCanvasAndPost(c);
	        }
	      }
	    }
	  }
	    
	  public void setRunning(boolean b) { 
	    run = b;
	  }
	  public void setSurfaceSize(int width, int height) {
	    synchronized (sh) {
	      doStart();
	    }
	  }
	  
	  public void calculateDistanceAndScale()
		{
			if(MainView.currentState == MainView.sizes)
			{
				double distanceSum = 0;
				double distance;
		
				bodies.get(0).setDrawingRadius((bodies.get(0).getRadius() / view.earthRadius) * view.scale);
				bodies.get(0).setDrawingX(view.screenX + view.zoomCenterX + distanceSum);
				bodies.get(0).setDrawingY(-view.screenY + view.zoomCenterY + bodies.get(0).getY() * view.scale);
				
				for(int i = 1; i < bodies.size(); i++)
				{
					bodies.get(i).setDrawingRadius((bodies.get(i).getRadius() / view.earthRadius) * view.scale);
					
					if(bodies.get(i).getClass() == Planet.class)
					{
						distance = (bodies.get(i-1).getRadius()/CelestialBody.earthRadius)*view.scale 
								+ 50*view.scale + bodies.get(i).getRadius()/CelestialBody.earthRadius*view.scale;
						
						distanceSum += distance;
						bodies.get(i).setDrawingX(view.screenX + view.zoomCenterX + distanceSum);
						bodies.get(i).setDrawingY(-view.screenY + view.zoomCenterY + bodies.get(i).getY() * view.scale);
					}
					else if(bodies.get(i).getClass() == Satellite.class)
					{
						((Satellite)bodies.get(i)).setDrawingDistance(2*((Satellite)bodies.get(i)).getPlanet().getRadius()/CelestialBody.earthRadius*view.scale 
								+ 4*bodies.get(i).getRadius()/CelestialBody.earthRadius*view.scale*i);
						
					}
				}
		
			}
			else if(view.currentState == view.distances)
			{
		    	for(int i = 0; i < bodies.size(); i++)
		    	{
		    		bodies.get(i).calculateDistanceAndScale(view.screenX, view.screenY, view.zoomCenterX, view.zoomCenterY, view.scale);
		    	}
			}
		}
	  private void doDraw(Canvas canvas) {
	    //canvas.restore();
	    canvas.drawColor(Color.BLACK);
	    Paint textPaint = new Paint();
	    textPaint.setARGB(255,200,200,200);
	    textPaint.setTextSize(15);
	    textPaint.setAntiAlias(true);
	   // CelestialBody startPlanet = bodies.get(0);
	  //  CelestialBody endPlanet = bodies.get(bodies.size()-1);

	    
	    for(int i = 0; i < bodies.size(); i++)
	    {
	    	bodies.get(i).render(canvas);
	    }
	  }
	  
	}