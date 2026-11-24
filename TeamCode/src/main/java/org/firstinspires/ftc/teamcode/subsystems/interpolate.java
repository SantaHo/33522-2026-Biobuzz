
package org.firstinspires.ftc.teamcode.subsystems;

public class interpolate {
    public double hood(double x) {
        // Known data points (must be sorted in ascending order of x)
        double[] preX = new double[]{0, 1, 2, 3};
        double[] preY = new double[]{0, 1, 2, 3};

        // Edge case: if x is outside the bounds, clamp to minimum or maximum
        if (x <= preX[0]) return preY[0];
        if (x >= preX[preX.length - 1]) return preY[preY.length - 1];

        // Find the segment [i, i+1] that contains x
        for (int i = 0; i < preX.length; i++) {
            if (x >= preX[i] && x <= preX[i + 1]) {
                // Linear interpolation formula: y = y0 + (x - x0) * (y1 - y0) / (x1 - x0)
                double x0 = preX[i];
                double x1 = preX[i + 1];
                double y0 = preY[i];
                double y1 = preY[i + 1];

                return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
            }
        }

        return 0;
    }
    public double rpm (double x) {
        // Known data points (must be sorted in ascending order of x)
        double[] preX = new double[]{0, 1, 2, 3};
        double[] preY = new double[]{0, 1, 2, 3};

        // Edge case: if x is outside the bounds, clamp to minimum or maximum
        if (x <= preX[0]) return preY[0];
        if (x >= preX[preX.length - 1]) return preY[preY.length - 1];

        // Find the segment [i, i+1] that contains x
        for (int i = 0; i < preX.length; i++) {
            if (x >= preX[i] && x <= preX[i + 1]) {
                // Linear interpolation formula: y = y0 + (x - x0) * (y1 - y0) / (x1 - x0)
                double x0 = preX[i];
                double x1 = preX[i + 1];
                double y0 = preY[i];
                double y1 = preY[i + 1];

                return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
            }
        }

        return 0;
    }
}
