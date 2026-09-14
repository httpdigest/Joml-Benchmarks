package benchmark.lidiuma_math.vector;

import lidiuma.org.lidiuma.math.vector.Vec3F32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import static lidiuma.org.lidiuma.math.vector.Vectors.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Vector3Float {
	Vec3F32 a;
	Vec3F32 b;	
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		a = new Vec3F32(0.0f, 1.0f, 0.0f);
		b = new Vec3F32(1.0f, 0.0f, 0.0f);
	}
	
	@Benchmark
	public Vec3F32 testCreation(Blackhole hole) {
		return new Vec3F32(1F, 0F, 0F);
	}
	
	@Benchmark
	public Vec3F32 testExampleCase(Blackhole hole) {
		Vec3F32 c = add(a, b);
		return normalize(cross(c, a));
	}
	
	@Benchmark
	public float testAngle(Blackhole hole) {
		final double dot = dot(a, b);
		final double length1 = lengthSquared(a);
		final double length2 = lengthSquared(b);
		final double theta = dot / Math.sqrt(length1 * length2);
		return (float) Math.acos(theta);
	}
}
