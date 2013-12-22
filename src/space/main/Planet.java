package space.main;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Planet extends CelestialBody{

	public Planet(String name, double radius, double x, double y, double orbitTime, int r, int g, int b)
	{
		this.name = name;
		this.radius = this.drawingRadius = radius;
		this.x = x;
		this.drawingX = this.x;
		this.y = y;
		this.drawingY = this.y;
		this.orbitTime = orbitTime;
		this.red = r;
		this.green = g;
		this.blue = b;
	}

	@Override
	public void render(Canvas c) {
		Paint paint = new Paint();
		  paint.setARGB(255, red(), green(), blue());
    	paint.setAntiAlias(true);
    	
    	Paint textPaint = new Paint();
    	textPaint.setARGB(255, 255, 255, 255);
    	textPaint.setAntiAlias(true);
    	textPaint.setTextSize(15);
    	c.drawCircle((float)(getDrawingX()),
    			(float)(getDrawingY()), (float)getDrawingRadius(), paint);
    	
    	if(getDrawingX() < 30000 && getDrawingX() > -100)
    		c.drawText(getName(), (float)(getDrawingX() + 10), (float)(getDrawingY() - getDrawingRadius()), textPaint);
	    
		
	}

	@Override
	public void calculateDistanceAndScale(double screenX, double screenY,
			double zoomCenterX, double zoomCenterY, double scale) {
		
		setDrawingRadius((getRadius() / earthRadius) * scale);
		if(getDrawingRadius() < 1)
			setDrawingRadius(1);
		setDrawingX(screenX + zoomCenterX + getX() * scale);
		setDrawingY(-screenY + zoomCenterY + getY() * scale);
		
	}
	
	
	
	
}
