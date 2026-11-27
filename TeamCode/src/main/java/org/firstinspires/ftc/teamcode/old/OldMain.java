/*package org.firstinspires.ftc.teamcode.old;
//Check line 102 just to be safe

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "RealMain", group = "Linear Opmode")
public class OldMain extends OpMode {
    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private DcMotor intake, index, turret;
    private DcMotorEx flywheel;
    private Servo hood;
    private Limelight3A limelight;
    private float FlyPower;
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3;
    private static final double MIN_POWER = 0.08;

    private static final double FlyMin = 4000; // In RMP
    private static final double FlyMax = 5000; //In RPM
    //private IMU imu;

    //Drive variables
    double forward, strafe, rotate, hoodpos, hoodstick;
    double fl, fr, br, bl;

    //Index variables
    private boolean lastA = false;
    private boolean indexOn = false;

    @Override
    public void init() {
        //Hardware Map
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        turret = hardwareMap.get(DcMotor.class, "turret");
        index = hardwareMap.get(DcMotor.class, "index");
        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        hood = hardwareMap.get(Servo.class, "hood");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //imu = hardwareMap.get(IMU.class, "imu");

        //Motor Directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   // Gear drive
        frontRight.setDirection(DcMotor.Direction.FORWARD);  // Gear drive
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // Direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   // Direct drive

        //Run Without Encoder
        DcMotor[] motors = {frontLeft, frontRight, backLeft, backRight};
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    @Override
    public void start() {
        limelight.start();
        limelight.pipelineSwitch(0); //0 IS BLUE ALLIANCE, 1 IS RED ALLIANCE

        telemetry.addData("Status", "Initialized");
        hoodpos = hood.getPosition();
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + hoodpos);

        //Read joystick input
        hoodstick = gamepad1.right_stick_y;

        //Hood Control (moves smoothly with the stick and stays within valid 0.0 to 1.0 servo bounds)
        if (Math.abs(hoodstick) > 0.1) {
            hoodpos -= hoodstick * 0.005; //Adjust 0.005 to change speed
            hoodpos = Math.max(0.3, Math.min(0.99, hoodpos)); //Clamp to valid servo range
        }
        //hood.setPosition(hoodpos);

        LLResult result = limelight.getLatestResult();

        //Intake Control
        if (gamepad1.b) {
            intake.setPower(1);  //Reverse intake
        } else if (gamepad1.x) {
            intake.setPower(-1); //Intake
        } else {
            intake.setPower(0);
        }

        //Index Toggle (A Button)
        boolean aPressed = gamepad1.a && !lastA;

        if (aPressed) {
            indexOn = !indexOn;
        }
        lastA = gamepad1.a;

        //Index Motor Control FIX THiS BEFORE COMP WE JUST WANTED IT TO DEFULT OFF FOR TESTING!!!!!!
        if (indexOn) {
            index.setPower(0);
        } else {
            index.setPower(1);
        }



        //region DRIVE CONTROL
        strafe = -gamepad1.left_stick_x;
        forward = gamepad1.left_stick_y;
        rotate = -gamepad1.right_stick_x;
        hoodstick = gamepad1.right_stick_y;

        //Mecanum Math and drivetrain control
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

        frontRight.setPower(fr);
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        backRight.setPower(br);

        telemetry.addData("Front Left", fl);
        telemetry.addData("Front Right", fr);
        telemetry.addData("Back Left", bl);
        telemetry.addData("Back Right", br);
        telemetry.update();


        if (result != null && result.isValid()) {
            double tx = result.getTx();
            double error1 = tx;
            double power = 0;

            if (Math.abs(error1) > DEADZONE) {
                power = KP * error1;

                //Limit motor power
                power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

                //Minimum power to overcome friction
                if (Math.abs(power) < MIN_POWER) {
                    power = Math.signum(power) * MIN_POWER;
                }

                turret.setPower(power);
            } else {
                turret.setPower(0);
            }
            //FlyWheel Interpolation here

        } else {
            turret.setPower(0);
            FlyPower = 4500; // idel speed if not locked
        }
        if (gamepad1.left_trigger > 0) {
            FlyPower = -500;
        }
        setMotorRPM(flywheel, FlyPower);
    }

    @Override
    public void stop() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        intake.setPower(0);
        index.setPower(0);
        flywheel.setPower(0);
        //limelight.stop();
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
    public void setMotorRPM(DcMotorEx motor, double targetRPM) {
        // Convert RPM to Revolutions Per Second, then multiply by ticks per revolution
        double ticksPerSecond = (targetRPM / 60.0) * 28;
        motor.setVelocity(ticksPerSecond);
    }
}
*/