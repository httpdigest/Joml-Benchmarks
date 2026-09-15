package benchmark.joml2_records.matrix;

import java.util.random.RandomGenerator;

import records.org.joml2.Float3;
import records.org.joml2.Float3x4;
import records.org.joml2.FloatQuat;

public class BoneAnimation {
	int size;
	Float3[] translationStart;
	FloatQuat[] rotationStart;
	Float3[] scaleStart;
	
	Float3[] translationEnd;
	FloatQuat[] rotationEnd;
	Float3[] scaleEnd;
	Float3x4[] inverseMatrices;
	/** Interpolation factors, drawn up front so the RNG stays out of the measured region. */
	float[] factors;
	
	public BoneAnimation(int count, RandomGenerator generator) {
		this.size = count;
		translationStart = new Float3[count];
		rotationStart = new FloatQuat[count];
		scaleStart = new Float3[count];
		
		translationEnd = new Float3[count];
		rotationEnd = new FloatQuat[count];
		scaleEnd = new Float3[count];
		
		inverseMatrices = new Float3x4[count];
		factors = new float[count];
		
		for(int i = 0;i<count;i++) {
			translationStart[i] = new Float3((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationStart[i] = new FloatQuat((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()).normalize();
			scaleStart[i] = new Float3((float)generator.nextGaussian());
			
			translationEnd[i] = new Float3((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationEnd[i] = new FloatQuat((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()).normalize();
			scaleEnd[i] = new Float3((float)generator.nextGaussian());

			inverseMatrices[i] = Float3x4.composeTRS(translationStart[i], rotationStart[i], scaleStart[i]).invert();
		}
		for(int i = 0;i<count;i++) {
			factors[i] = generator.nextFloat();
		}
	}
	
	public Float3x4[] process() {
		Float3x4[] results = new Float3x4[size];
		for(int i = 0;i<size;i++) {
			float t = factors[i];
			Float3 translation = translationStart[i].lerp(translationEnd[i], t);
			// JOML 1's nlerp is the shortest-arc variant: it negates the second quaternion
			// when the dot product is negative. JOML 2 splits the two apart, so nlerpShortest
			// is the equivalent; plain nlerp would take the long way round on roughly half
			// the bones and this row would no longer compare the same rotation.
			FloatQuat rotation = rotationStart[i].nlerpShortest(rotationEnd[i], t);
			Float3 scale = scaleStart[i].lerp(scaleEnd[i], t);
			results[i] = Float3x4.composeTRSMul(translation, rotation, scale, inverseMatrices[i]);
		}
		return results;
	}
}
