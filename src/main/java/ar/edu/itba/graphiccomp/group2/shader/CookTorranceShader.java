package ar.edu.itba.graphiccomp.group2.shader;

import javax.vecmath.Vector3d;

import ar.edu.itba.graphiccomp.group2.material.Material;
import ar.edu.itba.graphiccomp.group2.scene.CollisionContext;
import ar.edu.itba.graphiccomp.group2.util.Vector3ds;

public class CookTorranceShader implements Shader{

	private double roughness;
	
	public CookTorranceShader(double roughness){
		this.roughness = roughness;
	}
	
	public double roughness() {
		return roughness;
	}

	@Override
	public Vector3d apply(Vector3d diffuse, Vector3d specular, CollisionContext collision, Vector3d I,
			Vector3d eye) {
		Material material = collision.geometry().material();
		final Vector3d result = new Vector3d();
		final double IdotN = I.dot(collision.normal());
		{
			Vector3d kd = material.kd();
			result.x = diffuse.x * kd.x * IdotN;
			result.y = diffuse.y * kd.y * IdotN;
			result.z = diffuse.z * kd.z * IdotN;
//			System.out.println("diff "+diffuse+" KD "+ kd+ "IDOTN"+ IdotN);
		}
		{
			Vector3d ks = material.ks();
			double ct = calcCT(roughness, collision.normal(), eye, I, collision);
//			System.out.println("spec "+specular+" KS "+ ks+ "CT "+ ct);
			result.x *= specular.x * ks.x * ct * IdotN;
			result.y *= specular.y * ks.y * ct * IdotN;
			result.z *= specular.z * ks.z * ct * IdotN;
		}
		return Vector3ds.mult(result, collision.geometry().uvmapping(collision.localPosition(), collision.index()));
	}

	private double calcCT(double roughness, Vector3d n, Vector3d eye, Vector3d I, CollisionContext collision){
		Vector3d h = new Vector3d();
		h.set(eye);
		h.add(I);
		h.normalize();
		return (microfacetDistribution(roughness, n, h) * geometricAttenuation(n, h, eye, I) * 
				schlickFresnel(eye, h, collision)) / ( n.dot(I) * n.dot(eye));
	}
	
	private double microfacetDistribution(double roughness, Vector3d n, Vector3d h){
		double nh = n.dot(h);
		double nhSq = nh * nh;
		double roughnessSq = roughness * roughness;
		double base = 1 / (Math.PI * roughnessSq * Math.pow(nh, 4));
		double exp = (nhSq - 1) / (roughnessSq * nhSq);
		return base * Math.pow(Math.E, exp);
	}
	
	private double geometricAttenuation(Vector3d n, Vector3d h, Vector3d eye, Vector3d I){
		return Math.min(1, Math.min(gParam(n, h, eye), gParam(n, h, I)));
	}
	
	private double gParam(Vector3d n, Vector3d h, Vector3d param){
		return (2 * n.dot(h) * n.dot(param)) / (param.dot(h));
	}
	
	private double schlickFresnel(Vector3d eye, Vector3d h, CollisionContext collision){
		// TODO ver tema indices de refraccion
		double r0 = r0(Material.AIR_REFRACTION, collision.geometry().material().refractionIndex());
		double hv = h.dot(eye);
		return r0 + ((1 - r0) * Math.pow(1 - hv, 1));		
	}
	
	private double r0(double n1, double n2){
		return Math.pow((n1-n2)/(n1+n2),2);
	}
}