package benchmark.lidiuma_math.vector;

import static org.lidiuma.math.vector.Vectors.add;
import static org.lidiuma.math.vector.Vectors.cross;
import static org.lidiuma.math.vector.Vectors.dot;
import static org.lidiuma.math.vector.Vectors.lengthSquared;
import static org.lidiuma.math.vector.Vectors.normalize;

import java.util.concurrent.TimeUnit;

import org.lidiuma.math.vector.Vec3F32;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import benchmark.base.vector.Vector3fData;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Vector3fBenchmarks extends Vector3fData {
	Vec3F32 a;
	Vec3F32 b;	
	
	@Setup(Level.Iteration)
	public void setupVectors() {
		setupVectorData();
		a = new Vec3F32(sx, sy, sz);
		b = new Vec3F32(ex, ey, ez);
	}
	
	@Benchmark
	public Vec3F32 testCreation() {
		return new Vec3F32(sx, sy, sz);
	}

	@Benchmark
	public Vec3F32 testExampleCase() {
		Vec3F32 c = add(a, b);
		return normalize(cross(b, c));
	}

	@Benchmark
	public float testAngle() {
		final double dot = dot(a, b);
		final double length1 = lengthSquared(a);
		final double length2 = lengthSquared(b);
		final double theta = dot / Math.sqrt(length1 * length2);
		return (float) Math.acos(theta);
	}
}
