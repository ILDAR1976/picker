package com.frame;

import com.libs.Projector;
import com.libs.Ray;

public class PickFactory {

	private static Ray gPickRay = new Ray();

	public static Ray getPickRay() {
		return gPickRay;
	}

	private static Projector gProjector = new Projector();

	private static float[] gpObjPosArray = new float[4];

		public static void update(float screenX, float screenY) {
		AppConfig.gMatView.fillFloatArray(AppConfig.gpMatrixViewArray);

		float openglY = AppConfig.gpViewport[3] - screenY;
		// z = 0 
		gProjector.gluUnProject(screenX, openglY, 0.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		gPickRay.mvOrigin.set(gpObjPosArray[0], gpObjPosArray[1],
				gpObjPosArray[2]);

		// z = 1, P1
		gProjector.gluUnProject(screenX, openglY, 1.0f,
				AppConfig.gpMatrixViewArray, 0, AppConfig.gpMatrixProjectArray,
				0, AppConfig.gpViewport, 0, gpObjPosArray, 0);
		// P1 - P0
		gPickRay.mvDirection.set(gpObjPosArray[0], gpObjPosArray[1],
				gpObjPosArray[2]);
		gPickRay.mvDirection.sub(gPickRay.mvOrigin);

		gPickRay.mvDirection.normalize();
	}

}
