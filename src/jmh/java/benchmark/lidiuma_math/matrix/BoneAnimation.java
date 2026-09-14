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
			
			inverseMatrices[i] = inverse(fromTRS(translationStart[i], rotationStart[i], scaleStart[i]));
		}
		for(int i = 0;i<count;i++) {
			factors[i] = generator.nextFloat();
		}
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
