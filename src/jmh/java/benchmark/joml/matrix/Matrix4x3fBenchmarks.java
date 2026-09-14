package benchmark.joml.matrix;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

import org.joml.Matrix4x3f;
import org.joml.Vector3f;
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
	private Matrix4x3f matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		setupMatrixData();
		matrix = new Matrix4x3f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Matrix4x3f testCreation() {
		return new Matrix4x3f();
	}
	
	@Benchmark
	public Matrix4x3f testStandardOperation() {
		return new Matrix4x3f().translate(tx, ty, tz).rotate(angle, ax, ay, az).scale(sx, sy, sz);
	}
	
	@Benchmark
	public Matrix4x3f testComposeTRS() {
		return new Matrix4x3f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Vector3f testMatrixTransform() {
		return matrix.transformPosition(new Vector3f(px, py, pz));
	}
	
	@Benchmark
	public Matrix4x3f[] testBoneAnimation(AnimationContainer container) {
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
