package benchmark.joml2_records.vector;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import records.org.joml2.Float3;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Vector3Float {
	float ax, ay, az;
	float bx, by, bz;
	Float3 a;
	Float3 b;	
	
	@Setup(Level.Iteration)
	public void setupVectors() {
		// Same two unit vectors (~62 degrees apart) as every other library's row.
		ax = 0.309426374F; ay = 0.928279122F; az = 0.206284249F;
		bx = 0.843274043F; by = 0.105409255F; bz = 0.527046277F;
		a = new Float3(ax, ay, az);
		b = new Float3(bx, by, bz);
	}
	
	@Benchmark
	public Float3 testCreation() {
		return new Float3(ax, ay, az);
	}

	@Benchmark
	public Float3 testExampleCase() {
		Float3 c = a.add(b);
		return b.cross(c).normalize();
	}

	@Benchmark
	public float testAngle() {
		return a.angleBetween(b);
	}
}
