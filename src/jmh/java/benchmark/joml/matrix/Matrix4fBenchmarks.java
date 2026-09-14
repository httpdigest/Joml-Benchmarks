package benchmark.joml.matrix;

import java.util.concurrent.TimeUnit;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4fBenchmarks {
	private float tx, ty, tz;
	private float angle, ax, ay, az;
	private float qx, qy, qz, qw;
	private float sx, sy, sz;
	private float px, py, pz;
	private Matrix4f matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		tx = 32F; ty = 0.5F; tz = 1F;
		// 32 degrees about +Y, as an axis/angle and as the equivalent unit quaternion,
		// so testStandardOperation and testComposeTRS build the very same matrix.
		angle = 0.558505361F; ax = 0F; ay = 1F; az = 0F;
		qx = 0F; qy = 0.275637356F; qz = 0F; qw = 0.961261696F;
		sx = 0.25F; sy = 2F; sz = 1F;
		px = 1F; py = 3F; pz = 6F;
		matrix = new Matrix4f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Matrix4f testCreation() {
		return new Matrix4f();
	}

	@Benchmark
	public Matrix4f testStandardOperation() {
		return new Matrix4f().translate(tx, ty, tz).rotate(angle, ax, ay, az).scale(sx, sy, sz);
	}

	@Benchmark
	public Matrix4f testComposeTRS() {
		return new Matrix4f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}

	@Benchmark
	public Vector3f testMatrixTransform() {
		return matrix.transformPosition(new Vector3f(px, py, pz));
	}
}
