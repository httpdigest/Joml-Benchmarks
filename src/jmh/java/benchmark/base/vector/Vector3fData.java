package benchmark.base.vector;

public class Vector3fData {
	protected float sx, sy, sz;	//Start	
	protected float ex, ey, ez;	//End
	
	protected void setupVectorData() {
		// Two arbitrary unit vectors about 62 degrees apart.
		sx = 0.309426374F; sy = 0.928279122F; sz = 0.206284249F;
		ex = 0.843274043F; ey = 0.105409255F; ez = 0.527046277F;
	}
}
