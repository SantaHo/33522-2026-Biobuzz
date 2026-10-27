//This code *should* aim the turret at the Apriltag
//Maybe this works, maybe it doesn't
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TurretAim", group = "Linear Opmode")
public class TurretAim extends OpMode {

    private Limelight3A limelight;
    private DcMotor motor;

    //TUNE THESE IF NEEDED
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3; //CHANGE ONCE BUILT
    private static final double MIN_POWER = 0.08; //However much is neededd

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        motor = hardwareMap.get(DcMotor.class, "motor");

        limelight.pipelineSwitch(0);
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();
            double error = tx;

            double power = 0;

            if (Math.abs(error) > DEADZONE) {
                power = KP * error;

                //Limit motor power
                power = Math.max(
                        -MAX_POWER,
                        Math.min(MAX_POWER, power)
                );

                //Minimum power for friction
                if (Math.abs(power) < MIN_POWER) {
                    power = Math.signum(power) * MIN_POWER;
                }

                //MAKE -power IF REVERSED
                motor.setPower(power);
            } else {
                motor.setPower(0);
            }

            telemetry.addData("Target", "Detected");
            telemetry.addData("tx", "%.2f deg", tx);
            telemetry.addData("Error", "%.2f", error);
            telemetry.addData("P Output", "%.3f", power);
            telemetry.addData("KP", KP);

        } else {
            motor.setPower(0);
            telemetry.addData("Target", "Not detected");
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        motor.setPower(0);
        limelight.stop();
    }
}
