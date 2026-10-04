/*
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Main Drive", group="Linear OpMode")
public class DriveTrain extends LinearOpMode {

    private DcMotor frontLeft, backLeft, frontRight, backRight;

    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // --- Motor Directions ---
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   // chain driven
        frontRight.setDirection(DcMotor.Direction.REVERSE);  // chain driven
        backLeft.setDirection(DcMotor.Direction.REVERSE);    // direct drive
        backRight.setDirection(DcMotor.Direction.FORWARD);   // direct drive

        DcMotor[] motors = {frontLeft, backLeft, frontRight, backRight};
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        waitForStart();

        while (opModeIsActive()) {

            // Apply smooth deadzone to joystick
            double y = applyDeadzone(-gamepad1.left_stick_y, 0.07);
            double x = applyDeadzone(gamepad1.left_stick_x, 0.07);
            double rx = applyDeadzone(gamepad1.right_stick_x, 0.07);

            // Strafe compensation
            x = x * 1.1;

            // Mecanum math
            double frontLeftPower = y + x + rx;
            double backLeftPower = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower = y + x - rx;

            // Normalize powers
            double max = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            ));

            frontLeft.setPower(frontLeftPower / max);
            backLeft.setPower(backLeftPower / max);
            frontRight.setPower(frontRightPower / max);
            backRight.setPower(backRightPower / max);
        }
    }

    private double applyDeadzone(double input, double deadzone) {
        if (Math.abs(input) < deadzone) {
            return 0;
        } else {
            return (input - Math.signum(input) * deadzone) / (1.0 - deadzone);
        }
    }
}
*/