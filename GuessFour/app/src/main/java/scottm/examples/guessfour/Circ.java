package scottm.examples.guessfour;

import android.util.FloatMath;

public class Circ {
	float x;
	float y;
	float r;
	
	public void setData(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	public boolean contains(float x, float y) {
		float xDist = this.x - x;
		float yDist = this.y - y;
		float distance = FloatMath.sqrt(xDist * xDist + yDist * yDist);
		return distance <= r;
	}
}
