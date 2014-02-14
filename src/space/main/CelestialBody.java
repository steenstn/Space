package space.main;

import android.graphics.Canvas;

public abstract class CelestialBody {
	
	public static double scale = 255;
	public static final double earthRadius = 6378;
	protected String name;
	protected double radius;
	protected double x;
	protected double y;
	protected double drawingX;
	protected double drawingY;
	protected int red;
	protected int green;
	protected int blue;
	protected double drawingRadius;
	protected double orbitTime;
	private long currentTime = System.currentTimeMillis();
	
	public void setDrawingX(double x) {
		drawingX = x;
	}
	
	public void setDrawingY(double y) {
		drawingY = y;
	}
	
	public void setDrawingRadius(double radius) {
		drawingRadius = radius;
	}
	
	public float calculateOrbitalX() {
		long oldTime = currentTime;
		long deltaTime = System.currentTimeMillis() - oldTime;
		return (float) (getX() * Math.cos((double)0.001f * deltaTime / orbitTime));
	}
	
	public float calculateOrbitalY() {
		long oldTime = currentTime;
		long deltaTime = System.currentTimeMillis() - oldTime;
		return (float) (getX() * Math.sin((double)0.001f * deltaTime / orbitTime));
	}

	public abstract void calculateDistanceAndScale(double screenX, double screenY, double zoomCenterX, double zoomCenterY, double scale);
	public abstract void render(Canvas c);
	
	public String getName() { return name; }
	public double getRadius() { return radius; }
	public double getDrawingRadius() { return drawingRadius; }
	public double getX() { return x; }
	public double getDrawingX() { return drawingX; }
	public double getY() { return y; }
	public double getDrawingY() { return drawingY; }
	public int red() { return red; }
	public int green() { return green; }
	public int blue() { return blue; }
}
