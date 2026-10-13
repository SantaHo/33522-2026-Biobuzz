package org.firstinspires.ftc.teamcode.ideas;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FieldCentric {

    OTOSTest OTOSConfig = new OTOSTest();
    private DcMotor frontRight, frontLeft, backRight, backLeft;
    // Class members
    public double flPower, blPower, frPower, brPower;

    public void init(HardwareMap hwMap) {

        frontLeft = hwMap.get(DcMotor.class, "frontLeft");
        frontRight = hwMap.get(DcMotor.class, "frontRight");
        backLeft = hwMap.get(DcMotor.class, "backLeft");
        backRight = hwMap.get(DcMotor.class, "backRight");

        OTOSConfig.myOtos = hwMap.get(SparkFunOTOS.class, "otos");
//        myOtos.setSignalProcessConfig(new SparkFunOTOS.SignalProcessConfig((byte) 0x0D));// disables accelerometer (only use if issues. ask ben first)
//        myOtos.setOffset(new SparkFunOTOS.Pose2D(-7.304, -1.636, 0)); //use this to set the offset of the otos on the robot

        OTOSConfig.configureOtos();


        //RESET FOR NEW ROBOT
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void drive(double forward, double strafe, double rotate) {


        double fl = forward + strafe + rotate;
        double bl = forward - strafe + rotate;
        double fr = forward - strafe - rotate;
        double br = forward + strafe - rotate;


        double maxPower = 1.0; //motor max power
        double maxSpeed = 1.0;// limits the max speed the controller can command.

        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(bl),
                                Math.max(Math.abs(fr),
                                        Math.abs(br)))));

        flPower = fl / max;
        blPower = bl / max;
        frPower = fr / max;
        brPower = br / max;

        frontLeft.setPower(flPower);
        backLeft.setPower(blPower);
        frontRight.setPower(frPower);
        backRight.setPower(brPower);
    }

    public void FieldOrientedTranslate(double targetPowerX, double targetPowerY, double rotation, boolean reset) {

        double deadband = 0.05; //percent of joystick range of motion

        double magnitude = Math.hypot(targetPowerX, targetPowerY); // use hypot as magnitude to solve diagonal input escaping dead band

        if (magnitude < deadband) {
            targetPowerX = 0;
            targetPowerY = 0;
        } else {
            // Remove the deadband and rescale deadband-1.00 back to 0.00-1.00
            double newMagnitude = (magnitude - deadband) / (1.0 - deadband);

            targetPowerX = targetPowerX / magnitude * newMagnitude;
            targetPowerY = targetPowerY / magnitude * newMagnitude;
        }

        SparkFunOTOS.Pose2D currentPose = OTOSConfig.myOtos.getPosition();

        double yaw = currentPose.h;
//        double yaw = 0;
        if (reset) {
            OTOSConfig.myOtos.setPosition(new SparkFunOTOS.Pose2D(currentPose.x, currentPose.y, 0));
        }

        double stickRotation = 0;
        if (targetPowerY > 0 && targetPowerX < 0) //quad2
        {
            stickRotation = (Math.atan2(Math.abs(targetPowerY), Math.abs(targetPowerX)) + Math.PI / 2) * 180 / Math.PI;
        } else if (targetPowerY < 0 && targetPowerX < 0) //quad3
        {
            stickRotation = (Math.atan2(Math.abs(targetPowerY), Math.abs(targetPowerX)) + Math.PI) * 180 / Math.PI;
        } else //quad1 and quad4
        {
            stickRotation = Math.atan2(targetPowerY, targetPowerX) * 180 / Math.PI;
        }

        // angle of imu yaw supplemented by the stick's rotation, determined by atan
        double theta = (360 - yaw) + stickRotation;
        double power = Math.hypot(targetPowerX, targetPowerY); //get hypotenuse of x and y tgt, getting the power

        // if at max power diag, limit to magnitude of 1
        // with the normalizing code, the diag movement had a bug where max power (being magnitude sqrt(2))--
        // --would cause wheels to flip polarity
        // to counteract this the power is limited to a proper magnitude
        if (power > 1) {
            power = 1;
        } else if (power < -1) {
            power = -1;
        }

        //get the sin and cos of theta
        //math.pi/4 represents 45 degrees, accounting for angular offset of mechanum
        double sin = Math.sin((theta * (Math.PI / 180)) - (Math.PI / 4));
        double cos = Math.cos((theta * (Math.PI / 180)) - (Math.PI / 4));
        //max of sin and cos, used to normalize the values for maximum efficiency
        double maxSinCos = Math.max(Math.abs(sin), Math.abs(cos));

        //same sign flip is to account for the inability of atan2, it typically only works for quadrants 1 and 4
        //by flipping the polarity when x < 0, we can use atan for every quadrant
        double flPower = 0;
        double frPower = 0;
        double blPower = 0;
        double brPower = 0;

        rotation *= -1;

        flPower = power * cos / maxSinCos + rotation;
        frPower = power * sin / maxSinCos + rotation;
        blPower = power * sin / maxSinCos - rotation;
        brPower = power * cos / maxSinCos - rotation;

//        double frontMax = Math.max(Math.abs(flPower),Math.abs(frPower));
//        double backMax = Math.max(Math.abs(blPower),Math.abs(brPower));

        //another normalization
        if ((power + Math.abs(rotation)) > 1) {
            flPower /= power + Math.abs(rotation);
            frPower /= power - Math.abs(rotation);
            blPower /= power + Math.abs(rotation);
            brPower /= power - Math.abs(rotation);
        }

        if (frontLeft != null && frontRight != null && backLeft != null && backRight != null) {
            double moveSpeed = 1;

            frontLeft.setPower(flPower * moveSpeed);
            frontRight.setPower(frPower * moveSpeed);
            backLeft.setPower(blPower * moveSpeed);
            backRight.setPower(brPower * moveSpeed);
        }

    }

    public void FieldOrientedTranslate(double targetPowerX, double targetPowerY, double rotation) {
        FieldOrientedTranslate(targetPowerX, targetPowerY, rotation, false);
    }
}