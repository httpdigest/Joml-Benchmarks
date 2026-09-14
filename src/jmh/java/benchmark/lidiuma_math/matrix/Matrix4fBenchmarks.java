package benchmark.lidiuma_math.matrix;

import lidiuma.org.lidiuma.math.matrix.Affine3F32;
import lidiuma.org.lidiuma.math.rotation.AngleF32;
import lidiuma.org.lidiuma.math.vector.Vec3F32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import static lidiuma.org.lidiuma.math.matrix.Matrices.*;
import static lidiuma.org.lidiuma.math.rotation.Rotations.fromAxisAngle;
import static lidiuma.org.lidiuma.math.rotation.Rotations.radians;
import static lidiuma.org.lidiuma.math.vector.Vectors.vec3;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4fBenchmarks { // Cannot do rotations with full Matrix4 classes. (it's equivalent to Matrix4x3)
	private static final AngleF32 ROTATION = radians(32f);
	private Affine3F32 matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		final var translation = vec3(32F, 0.5F, 1F);
		final var rotation = fromAxisAngle(vec3(0.25F, 2F, 1F), ROTATION);
		final var scale = vec3(0F, 1F, 0F);
		matrix = fromTRS(translation, rotation, scale);
	}
	
	@Benchmark
	public Affine3F32 testCreation() {
		return identityAffine3F32();
	}

	@Benchmark
	public Affine3F32 testStandardOperation() {
		final var translation = vec3(32F, 0.5F, 1F);
		final var rotation = fromAxisAngle(vec3(0.25F, 2F, 1F), ROTATION);
		final var scale = vec3(0F, 1F, 0F);
		return fromTRS(translation, rotation, scale);
	}
	
	@Benchmark
	public Vec3F32 testMatrixTransform(Blackhole hole) {
		// TODO In a future release use Point3F32.
		return multiply(matrix, vec3(1f, 3f, 6f));
	}
}
