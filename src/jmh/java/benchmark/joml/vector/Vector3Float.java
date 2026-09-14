package benchmark.joml.vector;

import java.util.concurrent.TimeUnit;

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
public class Vector3Float {
	float ax, ay, az;
	float bx, by, bz;
	Vector3f a;
	Vector3f b;
	
	@Setup(Level.Iteration)
	public void setupVectors() {
		// Two arbitrary unit vectors about 62 degrees apart.
		ax = 0.309426374F; ay = 0.928279122F; az = 0.206284249F;
		bx = 0.843274043F; by = 0.105409255F; bz = 0.527046277F;
		a = new Vector3f(ax, ay, az);
		b = new Vector3f(bx, by, bz);
	}
	
	@Benchmark
	public Vector3f testCreation() {
		return new Vector3f(ax, ay, az);
	}

	@Benchmark
	public Vector3f testExampleCase() {
		Vector3f v = new Vector3f(a);
		Vector3f w = new Vector3f(b);
		v.add(w);
		w.cross(v);
		return w.normalize();
	}
	
	@Benchmark
	public float testAngle() {
		return a.angle(b);
	}
}
