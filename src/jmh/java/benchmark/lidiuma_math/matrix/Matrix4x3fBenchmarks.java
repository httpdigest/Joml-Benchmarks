package benchmark.lidiuma_math.matrix;

import static org.lidiuma.math.matrix.Matrices.fromTRS;
import static org.lidiuma.math.matrix.Matrices.identityAffine3F32;
import static org.lidiuma.math.matrix.Matrices.multiply;
import static org.lidiuma.math.vector.Vectors.vec3;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

import org.lidiuma.math.matrix.Affine3F32;
import org.lidiuma.math.rotation.QuaternionF32;
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

import benchmark.base.matrix.Matrix4x3fData;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4x3fBenchmarks extends Matrix4x3fData {
	private Affine3F32 matrix;

	@Setup(Level.Iteration)
	public void setupMatrix() {
		setupMatrixData();
		matrix = fromTRS(vec3(tx, ty, tz), new QuaternionF32(qx, qy, qz, qw), vec3(sx, sy, sz));
	}

	@Benchmark
	public Affine3F32 testCreation() {
		return identityAffine3F32();
	}

	@Benchmark
	public Affine3F32 testComposeTRS() {
		return fromTRS(vec3(tx, ty, tz), new QuaternionF32(qx, qy, qz, qw), vec3(sx, sy, sz));
	}

	@Benchmark
	public Vec3F32 testMatrixTransform() {
		return multiply(matrix, vec3(px, py, pz));
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
