package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain {
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    public Drivetrain(HardwareMap hardwareMap) {
        //Hardware Map
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        //Run Without Encoder
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Motor Directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   //Gear drive
        frontRight.setDirection(DcMotor.Direction.FORWARD);  //Gear drive
        backLeft.setDirection(DcMotor.Direction.FORWARD);    //Direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   //Direct drive

        //Brake so that it breaks when it breaks
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void drive(double forward, double strafe, double rotate) {
        double fl, bl, fr, br;

        //Mecanum Math
        fl = -forward - strafe - rotate;
        bl = +forward - strafe + rotate;
        fr = -forward + strafe + rotate;
        br = +forward + strafe - rotate;

        //Normalize motor powers
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        ));

        fl /= max;
        fr /= max;
        bl /= max;
        br /= max;

        //Set Power
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        frontRight.setPower(fr);
        backRight.setPower(br);
    }

    //Stop motors
    public void stop() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }
}