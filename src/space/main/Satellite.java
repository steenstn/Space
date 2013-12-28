package space.main;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Satellite extends CelestialBody{

	private double distance;
	private double drawingDistance;
	private Planet planet;
	
	public Satellite(String name, double radius, double distance, Planet planet, double orbitTime, int r, int g, int b)
	{
		this.name = name;
		this.radius = this.drawingRadius = radius;
		this.distance = this.drawingDistance = distance;
		this.planet = planet;
		this.orbitTime = orbitTime;
		this.red = r;
		this.green = g;
		this.blue = b;
		this.x = planet.getX();
		this.y = planet.getY()+distance;
	}
	
	public double getDistance()
	{
		return distance;
	}
	public double getDrawingDistance()
	{
		return drawingDistance;
	}
	
	public Planet getPlanet()
	{
		return planet;
	}
	
	public void setDrawingDistance(double distance)
	{
		drawingDistance = distance;
	}

	@Override
	public void render(Canvas c) {
		Paint paint = new Paint();
	    paint.setARGB(255, red(), green(), blue());
    	paint.setAntiAlias(true);
    	
    	Paint textPaint = new Paint();
    	

    	double alpha = (scale/10*255);
    	if(alpha < 0.00001)
    		alpha = 0;
    	else if(alpha>255)
    		alpha = 255;
    	
    	textPaint.setARGB((int)alpha, 255, 255, 255);
    	textPaint.setAntiAlias(true);
    	textPaint.setTextSize(18);
    	c.drawCircle((float)(getDrawingX()),
    			(float)(getDrawingY()), (float)getDrawingRadius(), paint);
    	

    	if(getDrawingX() < 30000 && getDrawingX() > -100)
    	c.drawText(getName(), (float)(getDrawingX() + 10), (float)(getDrawingY() - getDrawingRadius()), textPaint);
	    	
		
	}

	@Override
	public void calculateDistanceAndScale(double screenX, double screenY,
			double zoomCenterX, double zoomCenterY, double scale) {
		CelestialBody.scale = scale;
		setDrawingRadius((getRadius() / earthRadius) * scale);
		if(getDrawingRadius() < 1)
			setDrawingRadius(1);
		
		setDrawingDistance(getDistance() * scale);
		setDrawingX(screenX + zoomCenterX + getPlanet().getDrawingX());
		setDrawingY(-screenY + zoomCenterY + getPlanet().getDrawingY());
		
	}
	
	@Override
	public double getDrawingX()
	{
		return planet.getDrawingX();
	}
	
	@Override
	public double getDrawingY()
	{
		return planet.getDrawingY()+getDrawingDistance();
	}
	
}
