package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Turret {
    private DcMotor turret;
    private Limelight3A limelight;

    //TUNE
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3;
    private static final double MIN_POWER = 0.08;

    //Hardware
    public Turret(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotor.class, "motor"); // Make sure this matches your Robot Configuration name
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.pipelineSwitch(0);
        limelight.start();

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //Limelight Aim
    public void updateAim() {
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();
            double error = tx;
            double power = 0;

            if (Math.abs(error) > DEADZONE) {
                power = KP * error;

                //Limit motor power
                power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

                //Minimum power to overcome friction
                if (Math.abs(power) < MIN_POWER) {
                    power = Math.signum(power) * MIN_POWER;
                }

                turret.setPower(power);
            } else {
                turret.setPower(0);
            }
        } else {
            turret.setPower(0);
        }
    }

    //Manual power override if you ever need it
    public void setPower(double power) {
        turret.setPower(power);
    }

    // Stop method for safety
    public void stop() {
        turret.setPower(0);
        limelight.stop();
    }
}