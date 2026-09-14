package benchmark.joml2_fields.matrix;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import static fields.org.joml2.Joml.*;

import fields.org.joml2.Float3;
import fields.org.joml2.Float4x4;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4fBenchmarks {
	private static final float ROTATION = (float)Math.toRadians(32D);
	private Float4x4 matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		matrix = float4x4()
				.translate(32F, 0.5F, 1F)
				.scale(0.25F, 2F, 1F)
				.rotateAxis(ROTATION, 0, 1F, 0);
	}
	
	@Benchmark
	public Float4x4 testCreation() {
		return float4x4();
	}
	
	@Benchmark
	public Float4x4 testStandardOperation(Blackhole hole) {
		return float4x4()
				.translate(32F, 0.5F, 1F)
				.scale(0.25F, 2F, 1F)
				.rotateAxis(ROTATION, 0, 1F, 0);
	}
	
	@Benchmark
	public Float3 testMatrixTransform(Blackhole hole) {
		return matrix.transformPosition(float3(1, 3, 6));
	}
	
}
