package com.figures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.libs.Ray;
import com.libs.Vector3f;
import com.libs.Vector4f;

import android.util.Log;

public class Cube {

	public static final int VERTEX_BUFFER = 0;
	public static final int TEXTURE_BUFFER = 1;

	private float one = 1.0f;
/*
	private float[] vertices = new float[] { -one, -one,  one, 
											  one, -one,  one,
											  one,  one,  one,
											 -one,  one,  one, 
											 -one, -one, -one, 
											 -one,  one, -one,
											  one,  one, -one, 
											  one, -one, -one, 
											 -one,  one, -one, 
											 -one,  one,  one,
											  one,  one,  one, 
											  one,  one, -one, 
											 -one, -one, -one, 
											  one, -one, -one,
			                                  one, -one,  one, 
			                                 -one, -one,  one, 
			                                  one, -one, -one, 
			                                  one,  one, -one,
			                                  one,  one,  one, 
			                                  one, -one,  one, 
			                                 -one, -one, -one, 
			                                 -one, -one,  one,
			                                 -one,  one,  one, 
			                                 -one,  one, -one };

	private float[] texCoords = new float[] {   one,   0, 
			                                      0,   0,
			                                      0, one, 
			                                    one, one,
			                                      0,   0, 
			                                      0, one, 
			                                    one, one, 
			                                    one,   0, 
			                                    one, one, 
			                                    one,   0, 
			                                      0,   0, 
			                                      0, one, 
			                                      0, one,
			                                    one, one,
			                                    one,   0,
			                                      0,   0,
			                                      0,   0,
			                                      0, one,
			                                    one, one,
			                                    one,   0, 
			                                    one,   0,
			                                      0,   0,
			                                      0, one,
			                                    one, one };

	
	private byte[] indices = new byte[] { 0, 1, 3, 2, 4, 5, 7, 6, 8, 9, 11, 10,
			12, 13, 15, 14, 16, 17, 19, 18, 20, 21, 23, 22 };
	
	*/
	private float[] vertices = new float[] {
			// Vertices according to faces
			-1.0f, -1.0f, 1.0f, //v0
			1.0f, -1.0f, 1.0f, 	//v1
			-1.0f, 1.0f, 1.0f, 	//v2
			1.0f, 1.0f, 1.0f, 	//v3

			1.0f, -1.0f, 1.0f, 	//...
			1.0f, -1.0f, -1.0f, 
			1.0f, 1.0f, 1.0f, 
			1.0f, 1.0f, -1.0f,

			1.0f, -1.0f, -1.0f, 
			-1.0f, -1.0f, -1.0f, 
			1.0f, 1.0f, -1.0f, 
			-1.0f, 1.0f, -1.0f,

			-1.0f, -1.0f, -1.0f, 
			-1.0f, -1.0f, 1.0f, 
			-1.0f, 1.0f, -1.0f, 
			-1.0f, 1.0f, 1.0f,

			-1.0f, -1.0f, -1.0f, 
			1.0f, -1.0f, -1.0f, 
			-1.0f, -1.0f, 1.0f, 
			1.0f, -1.0f, 1.0f,

			-1.0f, 1.0f, 1.0f, 
			1.0f, 1.0f, 1.0f, 
			-1.0f, 1.0f, -1.0f, 
			1.0f, 1.0f, -1.0f, 
								};
/** The initial texture coordinates (u, v) */	
private  float[] texCoords = new float[] {
			//Mapping coordinates for the vertices
			0.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f,

			0.0f, 0.0f,
			0.0f, 1.0f, 
			1.0f, 0.0f,
			1.0f, 1.0f,

			0.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f,

			0.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f,

			0.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f,

			0.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f, 
						};

/** The initial indices definition */
private byte[] indices = new byte[]  {
			// Faces definition
			0, 1, 3, 0, 3, 2, 		// Face front
			4, 5, 7, 4, 7, 6, 		// Face right
			8, 9, 11, 8, 11, 10, 	// ...
			12, 13, 15, 12, 15, 14, 
			16, 17, 19, 16, 19, 18, 
			20, 21, 23, 20, 23, 22, 
									};


	
	public int surface = -1;

	public FloatBuffer getCoordinate(int coord_id) {
		switch (coord_id) {
		case VERTEX_BUFFER:
			return getDirectBuffer(vertices);
		case TEXTURE_BUFFER:
			return getDirectBuffer(texCoords);
		default:
			throw new IllegalArgumentException();
		}
	}

	public ByteBuffer getIndices() {
		return ByteBuffer.wrap(indices);
	}

	public FloatBuffer getDirectBuffer(float[] buffer) {
		ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer directBuffer = bb.asFloatBuffer();
		directBuffer.put(buffer);
		directBuffer.position(0);
		return directBuffer;
	}

	public Vector3f getSphereCenter() {
		return new Vector3f(0, 0, 0);
	}

	public float getSphereRadius() {
		return 1.732051f;
	}

	private static Vector4f location = new Vector4f();

	public boolean intersect(Ray ray, Vector3f[] trianglePosOut) {
		boolean bFound = false;
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;

		for (int i = 0; i < 6; i++) {

			for (int j = 0; j < 2; j++) {
				if (0 == j) {
					v0 = getVector3f(indices[i * 4 + j]);
					v1 = getVector3f(indices[i * 4 + j + 1]);
					v2 = getVector3f(indices[i * 4 + j + 2]);
				} else {
					v0 = getVector3f(indices[i * 4 + j]);
					v1 = getVector3f(indices[i * 4 + j + 2]);
					v2 = getVector3f(indices[i * 4 + j + 1]);
				}

				if (ray.intersectTriangle(v0, v1, v2, location)) {
					if (!bFound) {
						bFound = true;
						closeDis = location.w;
						trianglePosOut[0].set(v0);
						trianglePosOut[1].set(v1);
						trianglePosOut[2].set(v2);
						surface = i;
					} else {
						if (closeDis > location.w) {
							closeDis = location.w;
							trianglePosOut[0].set(v0);
							trianglePosOut[1].set(v1);
							trianglePosOut[2].set(v2);
							surface = i;
						}
					}
				}
			}
		}
		return bFound;
	}

	public boolean intersect2(Ray ray, Vector3f[] trianglePosOut, Vector3f[] trianglePosOut2) {
		boolean draw = false;
		boolean bFound = false;
		float closeDis = 0.0f;
		
		Vector3f v0, v1, v2;
		Vector3f a0 = null, a1 = null, a2 = null;
		Vector3f b0 = null, b1 = null, b2 = null;

		for (int i = 0; i < 6; i++) {
			Log.i("i", "value = "+i);
			for (int j = 0; j < 2; j++) {
				if (0 == j) {
					v0 = getVector3f(indices[i * 6 + 0]);
					v1 = getVector3f(indices[i * 6 + 1]);
					v2 = getVector3f(indices[i * 6 + 2]);

					a0 = v0;
					a1 = v1;
					a2 = v2;
				} else {
					v0 = getVector3f(indices[i * 6 + 3]);
					v1 = getVector3f(indices[i * 6 + 4]);
					v2 = getVector3f(indices[i * 6 + 5]);
					
					b0 = v0;
					b1 = v1;
					b2 = v2;
				}

				if (ray.intersectTriangle(v0, v1, v2, location)) {
					if (!bFound) {
						bFound = true;
						draw = true;
						closeDis = location.w;
						surface = i;
					} else {
						if (closeDis > location.w) {
							closeDis = location.w;
							surface = i;
							
							draw = true;

						}
					}
				}
			}
			
			if (draw){
				trianglePosOut[0] = a0;
				trianglePosOut[1] = a1;
				trianglePosOut[2] = a2;
				trianglePosOut2[0] = b0;
				trianglePosOut2[1] = b1;
				trianglePosOut2[2] = b2;
				draw = false;
			};
		}
		return bFound;
	}
	
	private Vector3f getVector3f(int start) {
		return new Vector3f(vertices[3 * start], vertices[3 * start + 1],
				vertices[3 * start + 2]);
	}
}
