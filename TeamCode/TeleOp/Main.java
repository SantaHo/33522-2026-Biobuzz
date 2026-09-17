package org.firstinspires.ftc.teamcode;
//can android studio see this ?
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name="BioBuzzTele")
public class main extends OpMode {
    //region Hardware Declarations
    private DcMotor turret, intake, index;
    private DcMotor frontRight, frontLeft, backRight, backLeft;
    private DcMotorEx flyWheel;
    private Servo flicker, hood, blocker;
    private Limelight3A limelight;
    private IMU imu;
    //endregion
    //region Variables
    double forward, strafe, rotate, turretPower, rpm, lastError, lastTime,tagDistanceInchs;
    double fl, fr, br, bl;
    final double fireTime = 300;
    //endregion
    //region Turret rotation PIDF
    double turretP = 0.019;
    double turretD = 0.002;
    double turretF = 0;
    //endregion
    //region Flyhwheel PIDF
    double flyWheelP = 0.002;
    double flyWheelI = 0;
    double flyWheelI_Tune = 0.00005;
    //endregion
    //region hood config
    double hoodPosition = 0.45;
    double HOOD_FAR = 0.75;
    double HOOD_CLOSE = 0.24;
    double HOOD_MED = 0.55;
    //endregion
    //region RPM Config
    double rpmTarget = 0;
    double rpmTolerance = 100;
    boolean flywheelAtSpeed = false;

    double RPM_HIGH = 6000;
    double RPM_MED = 1700;
    double RPM_LOW = 1350;    //endregion
    //region LimeLight Results
    LLResult llResult;
    public void getLatestResult() {
        llResult = limelight.getLatestResult();
    }
    //endregion
    //region FlyWheelRPM
    private void flywheelRPM() {
        rpm = flyWheel.getVelocity();
        double error = rpmTarget - rpm;
        double power = error * flyWheelP;// + flyWheelI; // math for getting the power to the motor
        if (rpm > 900) {
            flyWheelI += error * flyWheelI_Tune; // integrate the error over time
            telemetry.addData("current Intigral", flyWheelI);
        } else {
            flyWheelI = 0;
        }
        flywheelAtSpeed = rpm>=rpmTarget-rpmTolerance;
    }
    //endregion
    ElapsedTime indexTimer;
    //region T||F
    boolean lastX = false;
    boolean lastA = false;
    boolean intakeOn = false;
    boolean isShooting = false;
    boolean readyToTrack = false;
    //endregion

    //prereqs1 drive = new prereqs1();
    //LimelightMath limelightMath = new LimelightMath();
    //region Init
    @Override
    public void init() {
        //region HARDWARE MAP
        //drive.init(hardwareMap);
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        intake = hardwareMap.get(DcMotor.class, "Intake");
        index = hardwareMap.get(DcMotor.class, "index");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        flyWheel = hardwareMap.get(DcMotorEx.class, "flyWheel");
        flicker = hardwareMap.get(Servo.class, "servo");
        hood = hardwareMap.get(Servo.class, "hood");
        blocker = hardwareMap.get(Servo.class, "blocker");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        //endregion

        // ODO RECALIBRATE
        //drive.configureOtos();


        //region MOTOR DIRECTIONS
        turret.setDirection(DcMotor.Direction.REVERSE); // negative clockwise, positive counterclockwise
        intake.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotor.Direction.REVERSE);
        index.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        //endregion

        //region encoders
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        index.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        flyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //endregion

        // zero power action
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //misc
        limelight.pipelineSwitch(0);
        indexTimer = new ElapsedTime();
        lastTime = getRuntime();
    }
    //endregion
    //region Start
    public void start() {
        limelight.start();
        hood.setPosition(hoodPosition);
        flicker.setPosition(0.6);
        blocker.setPosition(1);
    }
    //endregion
    //region LimeLight Distance
    public double distanceFromTag(LLResult llresult) {
        if (llresult != null && llresult.isValid()) {
            // Since it's upside down, Ty needs to be inversed
            double ty = llresult.getTy();

            double LLAngle = -20; //mounting angle
            double theta = Math.toRadians(ty + LLAngle);

            double tagHeight = 762; // mm
            double LLHeight = 275.4; // mm
            double LIMELIGHTCONSTANT = 1;

            // Trigonometry
            double heightDiff = tagHeight - LLHeight;
            double DIST_M = heightDiff / Math.tan(theta);

            // Convert to inches and subtract offset
            return (DIST_M / 25.4) - LIMELIGHTCONSTANT;
        }
        return -1;
    }
    //endregion
    @Override
    public void loop() {

        //region DRIVE CONTROL
        strafe = -gamepad1.left_stick_y;
        forward = -gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;


        //region Sensitivity
        //rotation
        if(gamepad1.right_stick_x<.2 &&gamepad1.right_stick_x>0||gamepad1.right_stick_x>-.2 && gamepad1.right_stick_x>0){
            rotate = rotate/100;
            rotate = rotate*70;
        }
        //Strafing
        if(gamepad1.left_stick_x<.2 &&gamepad1.left_stick_x>0||gamepad1.left_stick_x>-.2 && gamepad1.left_stick_x>0){
            strafe = strafe/100;
            strafe = strafe*70;
        }
        //Forward back
        if(gamepad1.left_stick_y<.2 &&gamepad1.left_stick_y>0||gamepad1.left_stick_y>-.2 && gamepad1.left_stick_y>0){
            forward = forward/100;
            forward = forward*70;
        }

        fl = forward + strafe - rotate;
        bl = forward - strafe + rotate;
        fr = forward - strafe - rotate;
        br = forward + strafe + rotate;
        frontRight.setPower(fr);
        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        backRight.setPower(br);

        //endregion
        //endregion

        //drive.FieldOrientedTranslate(forward, strafe, rotate);
        //region TURRET ROTATION
        getLatestResult();
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw(AngleUnit.DEGREES));

        //tracking, PID and allat i dont wanna explain it
        if (llResult != null && llResult.isValid()) {

            double error = -llResult.getTx();   // degrees
            double currentTime = getRuntime();
            double dt = currentTime - lastTime;
            double derivative = (error - lastError+.25) / dt; //Use last number for tuning, lower is less powerfull */;
            turretPower = (turretP * error);// * Math.min((derivative/turretD),1);
            turretPower = Math.max(-1, Math.min(1, turretPower));
            lastError = error;
            lastTime = currentTime;
        }
        else {
            turretPower = 0;
        }
        //Manual overides
        if(gamepad1.right_bumper){
            turretPower = 0.5;
        }
        else if(gamepad1.left_bumper){
            turretPower = -0.5;
        }
        if(gamepad2.right_bumper){
            turretPower = 0.25;
        }
        else if(gamepad2.left_bumper){
            turretPower = -0.25;
        }
        turret.setPower(turretPower);
        //endregion
        //region INTAKE
        boolean aPressed = gamepad1.a && !lastA;

        //toggle
        if (aPressed) {
            intakeOn = !intakeOn;
            gamepad1.rumble(50); // rumble for when the intake toggles
        }

        //motor control
        if (!intakeOn) {
            intake.setPower(0);
        }else if(gamepad1.b) {
            intake.setPower(-1);// reverse override
            gamepad1.rumble(50);// constant vibration bc its always being called
        } else {
            intake.setPower(1);  //else normal
        }
        lastA = gamepad1.a;
        //endregion
        //region Firing
        if (gamepad1.left_trigger > 0.1) { //Timings for stuff in the firing proceder
            if(rpmTarget<2200) {
                if (!isShooting) {
                    indexTimer.reset();
                    isShooting = true;
                }
                index.setPower(1);
                blocker.setPosition(0.3);
                if (indexTimer.milliseconds() > 475) {
                    flicker.setPosition(0.3);
                }
                if (indexTimer.milliseconds() > 525) {
                    isShooting = false;
                }
            }
            if(rpmTarget>2200){
                flyWheel.setPower(1);
                if (!isShooting) {
                    indexTimer.reset();
                    isShooting = true;
                }
                if (indexTimer.milliseconds() > 0 && indexTimer.milliseconds() < 50 ) {
                    index.setPower(1);
                    blocker.setPosition(0.3);
                }
                if (indexTimer.milliseconds() > 50 && indexTimer.milliseconds() < 150) {
                    index.setPower(0);
                    blocker.setPosition(1);
                }
                if (indexTimer.milliseconds() > 150) {
                    index.setPower(1);
                    blocker.setPosition(.3);
                }
                if (indexTimer.milliseconds() > 650) {
                    flicker.setPosition(0.3);
                }
                if (indexTimer.milliseconds() > 700) {
                    isShooting = false;
                }
            }
        }else{
            isShooting = false;
            index.setPower(0);
            blocker.setPosition(1);
            flicker.setPosition(0.6);
        }
        if(isShooting == false){
            blocker.setPosition(1);
        }
        if(!(gamepad1.left_trigger  > .1)){
            flicker.setPosition(0.6);
        }
        //endregion
        //region LimeLight Hood and RPM
        double targetRPM =1600;
        double targetHood = 300;
        if(llResult.isValid()) {
                double ta = llResult.getTa();

                // Close → Mid
                if (ta >= 2.28) {
                    targetRPM = interpolate(ta, 3.9, 2.28, RPM_LOW, RPM_MED);
                    targetHood = interpolate(ta, 3.9, 2.28, HOOD_CLOSE, HOOD_MED);
                    targetHood = targetHood-.2;
                }

                // Mid → Far
                else if (ta >= .3) {
                    targetRPM = interpolate(ta, 2.28, 1.0, RPM_LOW, RPM_MED);
                    targetHood = interpolate(ta, 2.28, 1.0, HOOD_CLOSE, HOOD_MED);
                }
                else if (ta >= 2.7&& ta<=.7) {
                    targetRPM = targetRPM-150;
                    targetHood = targetHood-0.17;
                }
                else if (ta >= 1.7&& ta<=.7) {
                    targetRPM = targetRPM+50;
                    targetHood = targetHood+0.15;
                }
                else if (ta<1.1) {
                    targetRPM = targetRPM+75;
                    targetHood = targetHood+0.05;
                }
                else if (ta< .7) {
                    targetRPM = targetRPM+150;
                    targetHood = targetHood+0.19;
                }


                // Very far fallback
                else {
                    targetRPM = RPM_HIGH;
                    targetHood = HOOD_FAR;
                }

        }
        else {
            // no tag short range just in case
            targetRPM = RPM_MED;
            targetHood = HOOD_MED;
        }
        if(gamepad1.right_trigger>0.1){
            targetRPM = RPM_HIGH;
            targetHood = HOOD_FAR;
            flyWheel.setPower(1);
        }
        if(targetHood>0.75){
            hood.setPosition(0.75);
        }
        if(targetHood<.2){
            hood.setPosition(0.2);
        }
        hood.setPosition(targetHood);
        flyWheel.setVelocity(targetRPM);
        if(gamepad1.dpad_down){
            RPM_MED = RPM_MED-10;
            RPM_LOW = RPM_LOW - 10;
            HOOD_CLOSE = HOOD_CLOSE - 0.05;
            HOOD_MED = HOOD_MED - 0.05;

        }
        if(gamepad1.dpad_up){
            RPM_MED = RPM_MED + 10;
            RPM_LOW = RPM_LOW + 10;
            HOOD_CLOSE = HOOD_CLOSE + 0.05;
            HOOD_MED = HOOD_MED + 0.05;

        }
        //endregion
        //region Telemetry
        //telemetry.addData("Calculated Distance", distance);
        double currentTa = llResult.getTa();
        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Target Hood", targetHood);
        telemetry.addData("TA", currentTa);
        lastX = gamepad1.x;

        //telemetry.addData("distance", limelightMath.distanceFromTag());
        telemetry.addData("rpm", rpm);
        telemetry.addData("turret power", turretPower);
        telemetry.addData("target rpm", rpmTarget);
        telemetry.addData("hood pose", hoodPosition);
        telemetry.addData("Valid", llResult != null);
        telemetry.addData("blocker pos", blocker.getPosition());
        telemetry.addData("flicker", flicker.getPosition());
        telemetry.update();
        //endregion
    }
    //region Interpolation
    // Standard Linear Interpolation Formula
    // It calculates a value (y) between two points (y1, y2) based on where x sits between x1 and x2.
    public double interpolate(double x, double x1, double x2, double y1, double y2) {
        return y1 + (x - x1) * (y2 - y1) / (x2 - x1);
    }
    //endregion
}
