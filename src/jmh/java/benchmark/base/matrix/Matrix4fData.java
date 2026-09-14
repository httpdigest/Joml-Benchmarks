package benchmark.base.matrix;

public class Matrix4fData {
	protected float tx, ty, tz; 		//Translation Data
	protected float angle, ax, ay, az;	//Rotation Data (Axis Angle)
	protected float qx, qy, qz, qw;		//Rotation Data (Quaternion)
	protected float sx, sy, sz;			//Scale Data
	protected float px, py, pz;			//Position Data
	
	protected void setupMatrixData() {
		tx = 32F; ty = 0.5F; tz = 1F;
		angle = 0.558505361F; ax = 0F; ay = 1F; az = 0F;
		qx = 0F; qy = 0.275637356F; qz = 0F; qw = 0.961261696F;
		sx = 0.25F; sy = 2F; sz = 1F;
		px = 1F; py = 3F; pz = 6F;
	}
}
