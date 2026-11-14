package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Autom {
    private LinearOpMode opMode;
    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private DcMotor intake;
    private DcMotor flywheel;
    private DcMotor index;
    private Servo hood;
    private Limelight3A limelight;

    public Autom(HardwareMap hardwareMap, LinearOpMode opMode){
        this.opMode = opMode;
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

    }

    //drive forwards
    public void drive_forward(double power, int time) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
        opMode.sleep(time);
        stopMotors();
    }

    //drive backwards
    public void drive_backwards(double power,int time){
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
        opMode.sleep(time);
        stopMotors();
    }

    //strafe right
    public void strafe_right(double power,int time){
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(-power);
        backRight.setPower(-power);
        opMode.sleep(time);
        stopMotors();
    }

    //strafe left
    public void strafe_left(double power, int time){
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(power);
        opMode.sleep(time);
        stopMotors();
    }

    //shoot
    public void shoot(double power,int time){
        flywheel.setPower(power);
        opMode.sleep(1700);
        intake.setPower(-power);
        index.setPower(power);
        opMode.sleep(time);
        stopMotors();
    }




    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        intake.setPower(0);
        index.setPower(0);
        flywheel.setPower(0);
    }


}
