package benchmark.lidiuma_math.matrix;

import static org.lidiuma.math.matrix.Matrices.*;
import static org.lidiuma.math.rotation.Rotations.*;
import static org.lidiuma.math.vector.Vectors.*;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

import org.lidiuma.math.matrix.Affine3F32;
import org.lidiuma.math.rotation.AngleF32;
import org.lidiuma.math.vector.Vec3F32;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4x3fBenchmarks {
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
	public Vec3F32 testMatrixTransform() {
		// TODO In a future release use Point3F32.
		return multiply(matrix, vec3(1f, 3f, 6f));
	}
	
	@Benchmark
	public Affine3F32[] testBoneAnimation(AnimationContainer container) {
		return container.animation.process();
	}
	
	@State(Scope.Benchmark)
	public static class AnimationContainer {
		private BoneAnimation animation;
		
		@Param("100")
		public int operationMultiplier;
		
		@Setup(Level.Iteration)
		public void setupContainer() {
			animation = new BoneAnimation(operationMultiplier, RandomGeneratorFactory.getDefault().create(32231212134522L));
		}
	}
}
