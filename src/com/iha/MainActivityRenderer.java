package com.iha;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.figures.Cube;
import com.frame.AppConfig;
import com.frame.OnSurfacePickedListener;
import com.frame.PickFactory;

import com.libs.IBufferFactory;
import com.libs.Matrix4f;
import com.libs.Ray;
import com.libs.Vector3f;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;

public class MainActivityRenderer implements Renderer {

	private Context mContext;
	private Cube cube;

	int texture = -1;

	public float mfAngleX = 0.0f;
	public float mfAngleY = 0.0f;

	public float gesDistance = 0.0f;

	private Vector3f mvEye = new Vector3f(0, 0, 7f), mvCenter = new Vector3f(0,
			0, 0), mvUp = new Vector3f(0, 1, 0);

	private OnSurfacePickedListener onSurfacePickedListener;

	public MainActivityRenderer(Context context) {
		mContext = context;
		cube = new Cube();
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity(); 

		setUpCamera(gl);
	
		gl.glPushMatrix();
		{
			drawModel(gl);
		}
		gl.glPopMatrix();


		gl.glPushMatrix();
		{
			drawPickedTriangle(gl);
		}
		gl.glPopMatrix();

		updatePick();
	}

	private void setUpCamera(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		Matrix4f.gluLookAt(mvEye, mvCenter, mvUp, AppConfig.gMatView);
		gl.glLoadMatrixf(AppConfig.gMatView.asFloatBuffer());
	}

	private Matrix4f matRot = new Matrix4f();
	private Vector3f point;

	private void drawModel(GL10 gl) {
		matRot.setIdentity();

		point = new Vector3f(mfAngleX, mfAngleY, 0);

		try {
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			matInvertModel.transform(point, point);

			float d = Vector3f.distance(new Vector3f(), point);

			if (Math.abs(d - gesDistance) <= 1E-4) {

				matRot.glRotatef((float) (gesDistance * Math.PI / 180), point.x
						/ d, point.y / d, point.z / d);

				if (0 != gesDistance) {
					AppConfig.gMatModel.mul(matRot);
				}
			}
		} catch (Exception e) {
		}
		gesDistance = 0;

		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());

		gl.glColor4f(0.0f, 1.0f, 1.0f, 0.0f);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
				cube.getCoordinate(Cube.VERTEX_BUFFER));
		
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0,
				cube.getCoordinate(Cube.TEXTURE_BUFFER));
		
		/*
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 24, GL10.GL_UNSIGNED_BYTE,
				cube.getIndices());
		*/
		gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
				cube.getIndices());
		

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	private Vector3f transformedSphereCenter = new Vector3f();
	private Ray transformedRay = new Ray();
	private Matrix4f matInvertModel = new Matrix4f();
	
	private Vector3f[] mpTriangle = { new Vector3f(), new Vector3f(),
			new Vector3f() };
	private Vector3f[] mpTriangle2 = { new Vector3f(), new Vector3f(),
			new Vector3f() };
	
	private FloatBuffer mBufPickedTriangle = IBufferFactory
			.newFloatBuffer(3 * 3);

	private FloatBuffer mBufPickedTriangle2 = IBufferFactory
			.newFloatBuffer(3 * 3);
	
	private void updatePick() {
		if (!AppConfig.gbNeedPick) {
			return;
		}
		AppConfig.gbNeedPick = false;
		PickFactory.update(AppConfig.gScreenX, AppConfig.gScreenY);
		Ray ray = PickFactory.getPickRay();

		AppConfig.gMatModel.transform(cube.getSphereCenter(),
				transformedSphereCenter);

		cube.surface = -1;

		if (ray.intersectSphere(transformedSphereCenter, cube.getSphereRadius())) {
			matInvertModel.set(AppConfig.gMatModel);
			matInvertModel.invert();
			ray.transform(matInvertModel, transformedRay);
			if (cube.intersect2(transformedRay, mpTriangle, mpTriangle2)) {
				AppConfig.gbTrianglePicked = true;
				//Log.i("–", "=" + cube.surface);
				if (null != onSurfacePickedListener) {
					onSurfacePickedListener.onSurfacePicked(cube.surface);
				}
				mBufPickedTriangle.clear();
				for (int i = 0; i < 3; i++) {
					IBufferFactory
							.fillBuffer(mBufPickedTriangle, mpTriangle[i]);
					// Log.i("–" + i, mpTriangle[i].x + "\t" + mpTriangle[i].y
					// + "\t" + mpTriangle[i].z);
				}
				mBufPickedTriangle.position(0);
				
				mBufPickedTriangle2.clear();
				for (int i = 0; i < 3; i++) {
					IBufferFactory
							.fillBuffer(mBufPickedTriangle2, mpTriangle2[i]);
					// Log.i("–" + i, mpTriangle[i].x + "\t" + mpTriangle[i].y
					// + "\t" + mpTriangle[i].z);
				}
				mBufPickedTriangle2.position(0);
			}
		} else {
			AppConfig.gbTrianglePicked = false;
		}
	}

	private void drawPickedTriangle(GL10 gl) {
		if (!AppConfig.gbTrianglePicked) {
			return;
		}
		gl.glMultMatrixf(AppConfig.gMatModel.asFloatBuffer());
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufPickedTriangle2);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glEnable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glClearColor(1.f, 1.f, 1.f, 1);
		gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_BLEND);

		loadTexture(gl);

		AppConfig.gMatModel.setIdentity();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		AppConfig.gpViewport[0] = 0;
		AppConfig.gpViewport[1] = 0;
		AppConfig.gpViewport[2] = width;
		AppConfig.gpViewport[3] = height;

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		Matrix4f.gluPersective(45.0f, ratio, 1, 10, AppConfig.gMatProject);
		gl.glLoadMatrixf(AppConfig.gMatProject.asFloatBuffer());
		AppConfig.gMatProject.fillFloatArray(AppConfig.gpMatrixProjectArray);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	private void loadTexture(GL10 gl) {

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		try {
			IntBuffer intBuffer = IntBuffer.allocate(1);
			gl.glGenTextures(1, intBuffer);
			texture = intBuffer.get();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

			InputStream is = mContext.getResources().openRawResource(R.drawable.snow_leopard);
			Bitmap mBitmap = BitmapFactory.decodeStream(is);
			// Log.i("", mBitmap.getWidth() + "|" + mBitmap.getHeight());

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);

			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);

			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setOnSurfacePickedListener(
			OnSurfacePickedListener onSurfacePickedListener) {
		this.onSurfacePickedListener = onSurfacePickedListener;
	}

}
