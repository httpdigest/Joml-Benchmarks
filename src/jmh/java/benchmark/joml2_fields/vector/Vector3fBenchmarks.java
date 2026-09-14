package benchmark.joml2_fields.vector;

import static fields.org.joml2.Joml.float3;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import benchmark.base.vector.Vector3fData;
import fields.org.joml2.Float3;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Vector3fBenchmarks extends Vector3fData {
	Float3 a;
	Float3 b;	
	
	@Setup(Level.Iteration)
	public void setupVectors() {
		setupVectorData();
		a = float3(sx, sy, sz);
		b = float3(ex, ey, ez);
	}
	
	@Benchmark
	public Float3 testCreation() {
		return float3(sx, sy, sz);
	}

	@Benchmark
	public Float3 testExampleCase() {
		Float3 v = float3(a);
		Float3 w = float3(b);
		v.add(w);
		w.cross(v);
		return w.normalize();
	}

	@Benchmark
	public float testAngle() {
		return a.angleBetween(b);
	}
}
