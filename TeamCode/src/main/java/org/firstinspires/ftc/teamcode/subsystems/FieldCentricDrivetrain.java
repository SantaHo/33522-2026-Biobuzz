package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class FieldCentricDrivetrain {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    public FieldCentricDrivetrain(HardwareMap hardwareMap) {
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

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));


    }

    // make this into a button where it gets called when pressed to reset field centric heading
    public void resetYaw() {
        imu.resetYaw();
    }


    // call this instead of drive to use field centric driving
    // it does all of the math to turn joystick inputs into polar coordinates
    //then it calls drive with new strafe rotate and forward values
    public void driveFieldCentric(double forward, double strafe, double rotate) {
        // 1. Convert joystick inputs to polar coordinates
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        // 2. Rotate angle by the robot's current heading from the IMU
        theta = AngleUnit.normalizeRadians(theta - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // 3. Convert back to cartesian coordinates
        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        // 4. Pass adjusted inputs to standard robot-centric drive method
        drive(newForward, newStrafe, rotate);
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
