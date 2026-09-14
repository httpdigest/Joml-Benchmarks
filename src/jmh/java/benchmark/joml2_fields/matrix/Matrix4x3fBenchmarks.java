package benchmark.joml2_fields.matrix;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static fields.org.joml2.Joml.*;

import fields.org.joml2.Float3;
import fields.org.joml2.Float3x4;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4x3fBenchmarks {
	private float tx, ty, tz;
	private float angle, ax, ay, az;
	private float qx, qy, qz, qw;
	private float sx, sy, sz;
	private float px, py, pz;
	private Float3x4 matrix;

	@Setup(Level.Iteration)
	public void setupMatrix() {
		tx = 32F; ty = 0.5F; tz = 1F;
		angle = 0.558505361F; ax = 0F; ay = 1F; az = 0F;
		qx = 0F; qy = 0.275637356F; qz = 0F; qw = 0.961261696F;
		sx = 0.25F; sy = 2F; sz = 1F;
		px = 1F; py = 3F; pz = 6F;
		matrix = float3x4().composeTRS(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Float3x4 testCreation() {
		return float3x4();
	}
	
	@Benchmark
	public Float3x4 testStandardOperation() {
		return float3x4()
				.translate(tx, ty, tz)
				.rotateAxis(angle, ax, ay, az)
				.scale(sx, sy, sz);
	}
	
	@Benchmark
	public Float3x4 testComposeTRS() {
		return float3x4().composeTRS(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Float3 testMatrixTransform() {
		return matrix.transformPosition(float3(px, py, pz));
	}
	
	@Benchmark
	public Float3x4[] testBoneAnimation(AnimationContainer container) {
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
