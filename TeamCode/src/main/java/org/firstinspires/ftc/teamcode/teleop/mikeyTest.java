package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="new main ", group="Linear Opmode")
public class mikeyTest extends LinearOpMode {

    private DcMotor frontLeft, backLeft, frontRight, backRight;
    // deadzone method
    private double applyDeadzone(double input, double deadzone) {
        if (Math.abs(input) < deadzone) {
            return 0;
        } else {
            return (input - Math.signum(input) * deadzone) / (1.0 - deadzone);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // --- Hardware Map ---
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        double forward, strafe, rotate;
        double fl,fr,bl,br;

        // --- Run without encoders ---
        DcMotor[] motors = {frontLeft, backLeft, frontRight, backRight};
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        waitForStart();

        while(opModeIsActive()) {
            // assign values to variables associated with motor control
            // includes deadzones
            strafe = applyDeadzone(gamepad1.left_stick_y, 0.05);
            forward = applyDeadzone(gamepad1.left_stick_x, 0.05);
            rotate = applyDeadzone(gamepad1.right_stick_x, 0.05);
            //set power variables for each motor
            fl = -forward + strafe - rotate;
            bl = forward + strafe - rotate;
            fr = -forward + strafe + rotate;
            br = forward + strafe + rotate;
            //increase motor power
            if(gamepad1.right_trigger_pressed){
                fl *= 0.5;
                bl *= 0.5;
                fr *= 0.5;
                br *= 0.5;
            }
            //slow down motors
            else if(gamepad1.left_trigger_pressed){
                fl *= 0.5;
                bl *= 0.5;
                fr *= 0.5;
                br *= 0.5;
            }
            //define variables for gradual motor change
            // variables used as final power amount
            double frp = 0,flp = 0,blp = 0,brp = 0;
            // variable to control increase
            final double STEP = 0.02;

            double[] power = {frp,flp,blp,brp};
            double[] goal = {fr,fl,bl,br};

//            double i = 0;
//            int j=0;
//            while (j < 4){
//                while(goal[j]>power[j]){
//                    power[j] += 0.001;
//                }
//                j+=1;
//            }
            // has power gradually increase to what the power is supposed to be set as
            for (int j = 0; j < 4; j++) {
                double diff = goal[j] - power[j];
                if (Math.abs(diff) <= STEP) {
                    power[j] = goal[j];
                } else {
                    power[j] += Math.copySign(STEP, diff);  // works for speeding up, slowing down, reversing
                }
            }
            // sets power variables from for loop as power

            frp = power[0];
            flp = power[1];
            blp = power[2];
            brp = power[3];


            frontRight.setPower(frp);
            frontLeft.setPower(flp);
            backLeft.setPower(blp);
            backRight.setPower(brp);

            telemetry.addData("Status", "Running");
            telemetry.update();

        }


    }
}
