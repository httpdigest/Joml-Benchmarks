package benchmark.joml2_fields.vector;

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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class Vector3Float {
	Float3 a;
	Float3 b;	
	
	@Setup(Level.Iteration)
	public void setupMatrix() {
		a = float3(0.0f, 1.0f, 0.0f);
		b = float3(1.0f, 0.0f, 0.0f);
	}
	
	@Benchmark
	public Float3 testCreation(Blackhole hole) {
		return float3(1F, 0F, 0F);
	}
	
	@Benchmark
	public Float3 testExampleCase(Blackhole hole) {
		Float3 v = float3(a);
		Float3 a = float3(b);
		v.add(a);
		a.cross(v);
		return a.normalize();
	}
	
	@Benchmark
	public float testAngle(Blackhole hole) {
		return a.angleBetween(b);
	}
}
