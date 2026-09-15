package benchmark.lidiuma_math.vector;

import static org.lidiuma.math.vector.Vectors.add;
import static org.lidiuma.math.vector.Vectors.cross;
import static org.lidiuma.math.vector.Vectors.dot;
import static org.lidiuma.math.vector.Vectors.lengthSquared;
import static org.lidiuma.math.vector.Vectors.normalize;
import static org.lidiuma.math.vector.Vectors.vec3;

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
		a = vec3(sx, sy, sz);
		b = vec3(ex, ey, ez);
	}
	
	@Benchmark
	public Vec3F32 testCreation() {
		return vec3(sx, sy, sz);
	}

	@Benchmark
	public Vec3F32 testExampleCase() {
		Vec3F32 c = add(a, b);
		return normalize(cross(b, c));
	}

	/**
	 * lidiuma-math has no vector-to-vector angle function, so this one is written out
	 * here. It mirrors JOML 1's Vector3f.angle exactly - the same float cosine, the same
	 * clamp against precision loss pushing it outside [-1, 1], and the same exact
	 * Math.acos - so the row measures the same algorithm rather than a second one. See
	 * the note the report prints for this function: JOML 2's angleBetween is atan2-based
	 * and is a third algorithm again.
	 */
	@Benchmark
	public float testAngle() {
		final float dot = dot(a, b);
		final float length1 = lengthSquared(a);
		final float length2 = lengthSquared(b);
		float cos = (float) (dot / Math.sqrt(length1 * length2));
		cos = cos < 1F ? cos : 1F;
		cos = cos > -1F ? cos : -1F;
		return (float) Math.acos(cos);
	}
}
