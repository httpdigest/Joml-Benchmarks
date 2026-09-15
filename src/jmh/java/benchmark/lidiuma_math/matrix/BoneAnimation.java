package benchmark.lidiuma_math.matrix;

import static org.lidiuma.math.matrix.Matrices.*;
import static org.lidiuma.math.rotation.Rotations.*;
import static org.lidiuma.math.vector.Vectors.*;

import java.util.random.RandomGenerator;

import org.lidiuma.math.matrix.Affine3F32;
import org.lidiuma.math.rotation.QuaternionF32;
import org.lidiuma.math.vector.Vec3F32;

public class BoneAnimation {
	int size;
	Vec3F32[] translationStart;
	QuaternionF32[] rotationStart;
	Vec3F32[] scaleStart;
	
	Vec3F32[] translationEnd;
	QuaternionF32[] rotationEnd;
	Vec3F32[] scaleEnd;
	Affine3F32[] inverseMatrices;
	/** Interpolation factors, drawn up front so the RNG stays out of the measured region. */
	float[] factors;
	
	public BoneAnimation(int count, RandomGenerator generator) {
		this.size = count;
		translationStart = new Vec3F32[count];
		rotationStart = new QuaternionF32[count];
		scaleStart = new Vec3F32[count];
		
		translationEnd = new Vec3F32[count];
		rotationEnd = new QuaternionF32[count];
		scaleEnd = new Vec3F32[count];
		
		inverseMatrices = new Affine3F32[count];
		factors = new float[count];
		
		for(int i = 0;i<count;i++) {
			translationStart[i] = new Vec3F32((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationStart[i] = normalize(new QuaternionF32((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()));
			scaleStart[i] = multiply(oneVec3F32(), (float)generator.nextGaussian());
			
			translationEnd[i] = new Vec3F32((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationEnd[i] = normalize(new QuaternionF32((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()));
			scaleEnd[i] = multiply(oneVec3F32(), (float)generator.nextGaussian());
			
			inverseMatrices[i] = inverseTRS(translationStart[i], rotationStart[i], scaleStart[i]);
		}
		for(int i = 0;i<count;i++) {
			factors[i] = generator.nextFloat();
		}
	}
	
	/**
	 * The inverse of T*R*S, built as S<sup>-1</sup>*R<sup>-1</sup>*T<sup>-1</sup>.
	 * <p>
	 * Matrices.inverse(Affine3F32) does not return an inverse once the matrix carries a
	 * rotation. In lidiuma-math 0.3.0-j17 the 3x3 determinant combines its cofactors as
	 * m00 + m01 - m02 instead of m00 - m01 + m02 (math-traits 0.1.2, Affine3Ops.java:119
	 * and Matrix3Ops.java:96), and inverse() divides an otherwise correct adjugate by it,
	 * so M*inverse(M) comes out as (detCorrect/detBuggy)*I rather than the identity. The
	 * two disagreeing terms are both zero for a diagonal matrix, which is why pure
	 * translation and scale still round-trip and the bug is invisible until a rotation
	 * shows up.
	 * <p>
	 * Using it here would leave this row computing something other than the near-identity
	 * delta transform the other three libraries compute, so the inverse is composed from
	 * primitives that do work. This runs in setup, not in the measured region, so it does
	 * not affect the timings.
	 */
	private static Affine3F32 inverseTRS(Vec3F32 translation, QuaternionF32 rotation, Vec3F32 scale) {
		return multiply(multiply(fromScale(divide(oneVec3F32(), scale)), fromRotation(invert(rotation))), fromTranslation(negated(translation)));
	}

	public Affine3F32[] process() {
		Affine3F32[] results = new Affine3F32[size];
		for(int i = 0;i<size;i++) {
			float t = factors[i];
			Vec3F32 translation = lerp(translationStart[i], translationEnd[i], t);
			QuaternionF32 rotation = nlerp(rotationStart[i], rotationEnd[i], t);
			Vec3F32 scale = lerp(scaleStart[i], scaleEnd[i], t);
			results[i] = multiply(fromTRS(translation, rotation, scale), inverseMatrices[i]);
		}
		return results;
	}
}
