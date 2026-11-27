package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.SerialNumber;

@TeleOp(name = "USE THIS Comp2Tele", group = "TeleOp")
public class Comp2Tele extends LinearOpMode {

    //Hardware declarations
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor index, intake;
    private DcMotorEx flywheel;
    private Servo hood;
    private DcMotor turret;
    private Limelight3A limelight;
    private Servo intake1,intake2;

    //Turret control constants
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3;
    private static final double MIN_POWER = 0.08;

    //Toggle states
    private boolean lastA = false;
    private boolean indexOn = false;

    @Override
    public void runOpMode() {
        //Map Drivetrain motors
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Map Intake and Index motors
        intake = hardwareMap.get(DcMotor.class, "intake");
        index = hardwareMap.get(DcMotor.class, "index");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        index.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intake1 = hardwareMap.get(Servo.class, "intake1");
        intake2 = hardwareMap.get(Servo.class, "intake2");

        //Map Shooter hardware
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        hood = hardwareMap.get(Servo.class, "hood");
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        //Map Turret and Limelight
        turret = hardwareMap.get(DcMotor.class, "motor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight.pipelineSwitch(0);
        limelight.start();

        telemetry.addData("Status", "Ready to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double intake1angle = intake1.getPosition();
            double intake2angle = intake2.getPosition();
            intake1.setPosition(intake1angle - 90);
            intake2.setPosition(intake2angle - 90);

            //Driving (Robot-centric tank/arcade style)
            double strafe = -gamepad1.left_stick_x;
            double forward = gamepad1.left_stick_y;
            double rotate = -gamepad1.right_stick_x;

            double fl, bl, fr, br;
            fl = -forward - strafe - rotate;
            bl = +forward - strafe + rotate;
            fr = -forward + strafe + rotate;
            br = +forward + strafe - rotate;

            double max = Math.max(1.0, Math.max(
                    Math.max(Math.abs(fl), Math.abs(fr)),
                    Math.max(Math.abs(bl), Math.abs(br))
            ));

            frontLeft.setPower(fl / max);
            backLeft.setPower(bl / max);
            frontRight.setPower(fr / max);
            backRight.setPower(br / max);

            //Intake control
            if (gamepad1.b) {
                intake.setPower(-1.0); //Reverse intake
            } else if (gamepad1.x) {
                intake.setPower(1.0); //Intake
            } else {
                intake.setPower(0.0);
            }

            //Index control (toggle with A button)
            boolean aPressed = gamepad1.a && !lastA;
            if (aPressed) {
                indexOn = !indexOn;
            }
            lastA = gamepad1.a;

            boolean rightTriggerPressed = (gamepad1.right_trigger > 0.1);

            if (indexOn) {
                index.setPower(1.0);
            } else if (rightTriggerPressed) {
                index.setPower(1.0);
            } else {
                index.setPower(0.5);
            }

            //Limelight target check for shooter and turret tracking
            LLResult result = limelight.getLatestResult();
            boolean hasValidTarget = (result != null && result.isValid());

            //Flywheel spin up if valid target OR right trigger pressed
            if (hasValidTarget || rightTriggerPressed) {
                flywheel.setPower(0.8);
            } else {
                flywheel.setPower(0.2);
            }

            //Turret tracking logic using Limelight tx
            if (hasValidTarget) {
                double tx = result.getTx();
                double error1 = tx;
                double power = 0;

                if (Math.abs(error1) > DEADZONE) {
                    power = KP * error1;
                    power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

                    if (Math.abs(power) < MIN_POWER) {
                        power = Math.signum(power) * MIN_POWER;
                    }

                    turret.setPower(power);
                } else {
                    turret.setPower(0);
                }
            } else {
                turret.setPower(0);
            }

            telemetry.addData("Status", "Running");
            telemetry.addData("Valid Target", hasValidTarget);
            telemetry.update();
        }
    }
}