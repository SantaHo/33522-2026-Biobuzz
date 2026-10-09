package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="new main ", group="Linear Opmode")
public class CrapCode extends LinearOpMode {

    private DcMotor frontLeft, backLeft, frontRight, backRight;
    //private DcMotor Intake, flyWheelLeft, flyWheelRight;
    //private Servo flyWheelServo;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // --- Hardware Map ---
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");


        // --- Motor Directions ---
        frontLeft.setDirection(DcMotor.Direction.FORWARD);   // Direct drive
        frontRight.setDirection(DcMotor.Direction.REVERSE);  // Direct drive
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // Direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   // Direct drive
        double forward, strafe, rotate, turretPower, rpm, lastError, lastTime,tagDistanceInchs;
        double fl, fr, br, bl;


        // --- Run without encoders ---
        DcMotor[] motors = {frontLeft, backLeft, frontRight, backRight};
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        waitForStart();

        while (opModeIsActive()) {
            //region DRIVE CONTROL
            strafe = gamepad1.left_stick_y;
            forward = -gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            fl = -forward + strafe - rotate;
            bl = +forward + strafe - rotate;
            fr = -forward + strafe + rotate;
            br = +forward + strafe + rotate;

            frontRight.setPower(fr);
            frontLeft.setPower(fl);
            backLeft.setPower(bl);
            backRight.setPower(br);

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}

