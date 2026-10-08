package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name="new main ", group="Linear Opmode")
public class mikeyTest extends LinearOpMode {

    private DcMotor frontLeft, backLeft, frontRight, backRight;

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
            strafe = gamepad1.left_stick_y;
            forward = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;
            fl = -forward + strafe - rotate;
            bl = +forward + strafe - rotate;
            fr = -forward + strafe + rotate;
            br = +forward + strafe + rotate;
            if(gamepad1.a){
                fl = (-forward + strafe - rotate) * 2;
                bl = (+forward + strafe - rotate) * 2;
                fr = (-forward + strafe + rotate) * 2;
                br = (+forward + strafe + rotate) * 2;
            }

            frontRight.setPower(fr);
            frontLeft.setPower(fl);
            backLeft.setPower(bl);
            backRight.setPower(br);

            telemetry.addData("Status", "Running");
            telemetry.update();

        }

    }
}
