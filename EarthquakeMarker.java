package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;

public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker> {

	protected boolean isOnLand;

	protected float radius;

	// constants for distance
	protected static final float kmPerMile = 1.6f;

	public static final float THRESHOLD_MODERATE = 5;

	public static final float THRESHOLD_LIGHT = 4;

	public static final float THRESHOLD_INTERMEDIATE = 70;

	public static final float THRESHOLD_DEEP = 300;

	public abstract void drawEarthquake(PGraphics pg, float x, float y);

	// constructor
	public EarthquakeMarker(PointFeature feature) {
		super(feature.getLocation());

		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2 * magnitude);
		setProperties(properties);
		this.radius = 1.75f * getMagnitude();
	}

	// calls abstract method drawEarthquake and then checks age and draws X if
	// needed
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();

		// determine color of marker from depth
		colorDetermine(pg);

		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);

		// IMPLEMENT: add X over marker if within past day
		String age = getStringProperty("age");
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {

			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x - (radius + buffer), y - (radius + buffer), x + radius + buffer, y + radius + buffer);
			pg.line(x - (radius + buffer), y + (radius + buffer), x + radius + buffer, y - (radius + buffer));

		}


		pg.popStyle();

	}

	public void showTitle(PGraphics pg, float x, float y) {
		String title = getTitle();
		pg.pushStyle();

		pg.rectMode(PConstants.CORNER);

		pg.stroke(110);
		pg.fill(255, 255, 255);
		pg.rect(x, y + 15, pg.textWidth(title) + 6, 18, 5);

		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3, y + 18);

		pg.popStyle();

	}

	public double threatCircle() {
		double miles = 20.0f * Math.pow(1.8, 2 * getMagnitude() - 5);
		double km = (miles * kmPerMile);
		return km;
	}

	private void colorDetermine(PGraphics pg) {
		float depth = getDepth();

		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		} else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		} else {
			pg.fill(255, 0, 0);
		}
	}


	public String toString() {
		return getTitle();
	}

	@Override
	public int compareTo(EarthquakeMarker marker) {
		float mag = getMagnitude();
		float otherMag = marker.getMagnitude();
		return mag > otherMag ? -1 : (mag == otherMag ? 0 : 1);
	}

	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}

	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());
	}

	public String getTitle() {
		return (String) getProperty("title");

	}

	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}

	public boolean isOnLand() {
		return isOnLand;
	}

}