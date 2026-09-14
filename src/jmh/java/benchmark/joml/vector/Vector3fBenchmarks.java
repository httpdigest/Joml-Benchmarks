package benchmark.joml.vector;

import java.util.concurrent.TimeUnit;

import org.joml.Vector3f;
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
	Vector3f s;
	Vector3f e;
	
	@Setup(Level.Iteration)
	public void setupVectors() {
		setupVectorData();
		s = new Vector3f(sx, sy, sz);
		e = new Vector3f(ex, ey, ez);
	}
	
	@Benchmark
	public Vector3f testCreation() {
		return new Vector3f(sx, sy, sz);
	}

	@Benchmark
	public Vector3f testExampleCase() {
		Vector3f v = new Vector3f(s);
		Vector3f w = new Vector3f(e);
		v.add(w);
		w.cross(v);
		return w.normalize();
	}
	
	@Benchmark
	public float testAngle() {
		return s.angle(e);
	}
}
