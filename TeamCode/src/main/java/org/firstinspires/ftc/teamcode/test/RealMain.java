
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.IMU;

@TeleOp(name = "RealMain", group = "Linear Opmode")
public class RealMain extends OpMode {
    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private DcMotorEx turret;
    private DcMotor intake;
    private DcMotor shooter;
    private Servo hood;
    private Limelight3A limelight;
    private IMU imu;

    //Drive variables
    double forward, strafe, rotate;
    double fl, fr, br, bl;

    //Turret variables
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3; //CHANGE ONCE BUILT, this is max power of turret motor
    private static final double MIN_POWER = 0.02; //However much is needed, this is for friction

    //Intake variables
    private boolean lastA = false;
    private boolean intakeOn = false;

    @Override
    public void init() {
        //Hardware Map
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        intake = hardwareMap.get(DcMotor.class, "intake");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        hood = hardwareMap.get(Servo.class, "hood");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");

        //Motor Directions
        frontLeft.setDirection(DcMotor.Direction.FORWARD);   // Direct drive
        frontRight.setDirection(DcMotor.Direction.REVERSE);  // Direct drive
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // Direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   // Direct drive

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void start() {
        limelight.start();
        limelight.pipelineSwitch(0); //0 IS BLUE ALLIANCE, 1 IS RED ALLIANCE

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running");

        LLResult result = limelight.getLatestResult();

        //Turret Control (Limelight Aim)
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
                turret.setPower(power);
            } else {
                turret.setPower(0);
            }

            telemetry.addData("Target", "Visible");
            telemetry.addData("tx", tx);
            telemetry.addData("Turret Power", turret.getPower());

        } else {
            turret.setPower(0);
            telemetry.addData("Target", "Not visible");
            telemetry.addData("Turret Power", 0);
        }

        //Intake
        boolean aPressed = gamepad1.a && !lastA;

        if (aPressed) {
            intakeOn = !intakeOn;
            gamepad1.rumble(50); //Rumble when intake toggles
        }

        // Motor control
        if (gamepad1.b) {
            intake.setPower(-0.5); //Reverse intake
        } else if (intakeOn) {
            intake.setPower(0.5);  //Normal intake
        } else {
            intake.setPower(0);    //Stop intake
        }

        lastA = gamepad1.a;

        //Shooter
        if (gamepad1.right_bumper) {
            shooter.setPower(0.5);
        } else {
            shooter.setPower(0);
        }

        //Hood
        if (gamepad1.left_bumper) {
            hood.setPosition(0.5);
        } else {
            hood.setPosition(0);
        }

        //region DRIVE CONTROL
        strafe = gamepad1.left_stick_x;
        forward = -gamepad1.left_stick_y;
        rotate = gamepad1.right_stick_x;

        //Mecanum Math and drivetrain control
        fl = -forward + strafe - rotate;
        bl = +forward + strafe - rotate;
        fr = -forward + strafe + rotate;
        br = +forward + strafe + rotate;

        //Normalize motor powers
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        ));

        fl /= max;
        fr /= max;
        bl /= max;
        br /= max;

        frontRight.setPower(fr);
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        backRight.setPower(br);

        telemetry.addData("Front Left", fl);
        telemetry.addData("Front Right", fr);
        telemetry.addData("Back Left", bl);
        telemetry.addData("Back Right", br);
        telemetry.update();
    }

    @Override
    public void stop() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        turret.setPower(0);
        intake.setPower(0);
        shooter.setPower(0);
        hood.setPosition(0);
        limelight.stop();
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}
