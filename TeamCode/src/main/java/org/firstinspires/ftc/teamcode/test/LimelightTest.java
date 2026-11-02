//This code turns a motor on when a tag is detected
//Wowwwww crazy
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "LimelightTest", group = "Linear Opmode")
public class LimelightTest extends OpMode {

    private Limelight3A limelight;
    private DcMotor motor;

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
            motor.setPower(0.3);
            telemetry.addData("AprilTag", "Detected");
            telemetry.addData("Motor", "Spinning");
        } else {
            motor.setPower(0);
            telemetry.addData("AprilTag", "Not detected");
            telemetry.addData("Motor", "Stopped");
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        motor.setPower(0);
        limelight.stop();
    }
}