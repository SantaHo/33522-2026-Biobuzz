package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@Autonomous(name = "Comp One Auto")
public class CompOneAuto extends LinearOpMode {
    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private DcMotor intake;
    private DcMotor flywheel;
    private DcMotor index;
    private Servo hood;
    private Limelight3A limelight;

    @Override
    public void runOpMode() {
        //Hardware Map
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        //turret = hardwareMap.get(DcMotorEx.class, "turret");
        intake = hardwareMap.get(DcMotor.class, "intake");
        index = hardwareMap.get(DcMotor.class,"index");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        hood = hardwareMap.get(Servo.class, "hood");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //imu = hardwareMap.get(IMU.class, "imu");

        //Motor Directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   // Gear drive
        frontRight.setDirection(DcMotor.Direction.FORWARD);  // Gear drive
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // Direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   // Direct drive
        telemetry.addData("Status", "Ready to start");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            //Auto Program

            //Shoot Preload
            flywheel.setPower(1);
            sleep(1700);
            intake.setPower(-1);
            index.setPower(1);
            sleep(5000);

            flywheel.setPower(0);
            intake.setPower(0);
            index.setPower(0);

            //Drive forward
            frontLeft.setPower(1);
            frontRight.setPower(1);
            backLeft.setPower(1);
            backRight.setPower(1);
            sleep(300);

            stopDrive();

            /*
            //Strafe Right
            frontLeft.setPower(0.5);
            frontRight.setPower(0.5);
            backLeft.setPower(-0.5);
            backRight.setPower(-0.5);
            sleep(7000);

            stopDrive();

            //Move away from wall
            frontLeft.setPower(-1);
            frontRight.setPower(-1);
            backLeft.setPower(1);
            backRight.setPower(1);
            sleep(500);

            stopDrive();

            //Drive to park
            frontLeft.setPower(-1);
            frontRight.setPower(-1);
            backLeft.setPower(-1);
            backRight.setPower(-1);
            sleep(500);

            stopDrive();*/
        }
    }

    private void stopDrive() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
