package benchmark.joml2_fields.matrix;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static fields.org.joml2.Joml.*;

import fields.org.joml2.Float3;
import fields.org.joml2.Float3x4;
import fields.org.joml2.FloatQuat;

public class BoneAnimation {
	int size;
	Float3[] translationStart;
	FloatQuat[] rotationStart;
	Float3[] scaleStart;
	
	Float3[] translationEnd;
	FloatQuat[] rotationEnd;
	Float3[] scaleEnd;
	Float3x4[] inverseMatrices;
	RandomGenerator generator;
	
	public BoneAnimation(int count, RandomGenerator generator) {
		this.size = count;
		translationStart = new Float3[count];
		rotationStart = new FloatQuat[count];
		scaleStart = new Float3[count];
		
		translationEnd = new Float3[count];
		rotationEnd = new FloatQuat[count];
		scaleEnd = new Float3[count];
		
		inverseMatrices = new Float3x4[count];
		
		for(int i = 0;i<count;i++) {
			translationStart[i] = float3((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationStart[i] = floatQuat((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()).normalize();
			scaleStart[i] = float3((float)generator.nextGaussian());
			
			translationEnd[i] = float3((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian());
			rotationEnd[i] = floatQuat((float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian(), (float)generator.nextGaussian()).normalize();
			scaleEnd[i] = float3((float)generator.nextGaussian());
			
			inverseMatrices[i] = float3x4().composeTRS(translationStart[i], rotationStart[i], scaleStart[i]).invert();
		}
		this.generator = RandomGeneratorFactory.getDefault().create(generator.nextLong());
	}
	
	public Float3x4[] process() {
		Float3x4[] results = new Float3x4[size];
		Float3 translation = float3();
		FloatQuat rotation = floatQuat();
		Float3 scale = float3();
		for(int i = 0;i<size;i++) {
			float t = generator.nextFloat();
			Float3x4 result = float3x4();
			
			translationStart[i].lerp(translationEnd[i], t, translation);
			rotationStart[i].nlerp(rotationEnd[i], t, rotation);
			scaleStart[i].lerp(scaleEnd[i], t, scale);
			results[i] = result.composeTRSMul(translation, rotation, scale, inverseMatrices[i]);
		}
		return results;
	}
}
