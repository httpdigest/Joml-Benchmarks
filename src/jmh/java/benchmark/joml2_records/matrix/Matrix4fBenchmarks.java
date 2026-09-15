package benchmark.joml2_records.matrix;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import benchmark.base.matrix.Matrix4fData;
import records.org.joml2.Float3;
import records.org.joml2.Float4x4;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4fBenchmarks extends Matrix4fData {
	private Float4x4 matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		setupMatrixData();
		matrix = Float4x4.composeTRS(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Float4x4 testCreation() {
		return new Float4x4();
	}

	@Benchmark
	public Float4x4 testStandardOperation() {
		return new Float4x4()
				.translate(tx, ty, tz)
				.rotateAxis(angle, ax, ay, az)
				.scale(sx, sy, sz);
	}

	@Benchmark
	public Float4x4 testComposeTRS() {
		return Float4x4.composeTRS(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Float3 testMatrixTransform() {
		return matrix.transformPosition(new Float3(px, py, pz));
	}
}
