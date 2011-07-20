package com.ggervais.gameengine.math;

public class MathUtils {

	public static float convertDirectionToAngle(Vector3D direction) {
		Vector3D standardAxis = new Vector3D(1, 0, 0);
		float dotProduct = standardAxis.dotProduct(direction);
		float normProduct = standardAxis.length() * direction.length();
		return (float) Math.acos(dotProduct / normProduct);
	}
	
	public static Vector3D convertDirectionToAxis(Vector3D direction) {
		Vector3D standardAxis = new Vector3D(1, 0, 0);
		return standardAxis.crossProduct(direction).normalized();
	}

    public static int clamp(int value, int minValue, int maxValue) {
        int returnValue = value;
        if (returnValue < minValue) {
            returnValue = minValue;
        }
        if (returnValue > maxValue) {
            returnValue = maxValue;
        }
        return returnValue;
    }

    public static long clamp(long value, long minValue, long maxValue) {
        long returnValue = value;
        if (returnValue < minValue) {
            returnValue = minValue;
        }
        if (returnValue > maxValue) {
            returnValue = maxValue;
        }
        return returnValue;
    }

    public static float clamp(float value, float minValue, float maxValue) {
        float returnValue = value;
        if (returnValue < minValue) {
            returnValue = minValue;
        }
        if (returnValue > maxValue) {
            returnValue = maxValue;
        }
        return returnValue;
    }

    public static double clamp(double value, double minValue, double maxValue) {
        double returnValue = value;
        if (returnValue < minValue) {
            returnValue = minValue;
        }
        if (returnValue > maxValue) {
            returnValue = maxValue;
        }
        return returnValue;
    }
}
