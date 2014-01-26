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
	  
	  public RenderThread(SurfaceHolder surfaceHolder, Context context,
	         Handler handler, ArrayList<CelestialBody> bodies) {
	    sh = surfaceHolder;
	    this.bodies = bodies;
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