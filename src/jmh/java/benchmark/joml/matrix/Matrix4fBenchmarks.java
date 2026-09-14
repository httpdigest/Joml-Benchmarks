package benchmark.joml.matrix;

import java.util.concurrent.TimeUnit;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import benchmark.base.matrix.Matrix4fData;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Matrix4fBenchmarks extends Matrix4fData {
	private Matrix4f matrix;
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		setupMatrixData();
		matrix = new Matrix4f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}
	
	@Benchmark
	public Matrix4f testCreation() {
		return new Matrix4f();
	}

	@Benchmark
	public Matrix4f testStandardOperation() {
		return new Matrix4f().translate(tx, ty, tz).rotate(angle, ax, ay, az).scale(sx, sy, sz);
	}

	@Benchmark
	public Matrix4f testComposeTRS() {
		return new Matrix4f().translationRotateScale(tx, ty, tz, qx, qy, qz, qw, sx, sy, sz);
	}

	@Benchmark
	public Vector3f testMatrixTransform() {
		return matrix.transformPosition(new Vector3f(px, py, pz));
	}
}
