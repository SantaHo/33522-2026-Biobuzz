package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name="new main ", group="Linear Opmode")
public class Motortest extends LinearOpMode {


    private DcMotor testmotor;

    @Override
    public void runOpMode(){
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        testmotor = hardwareMap.get(DcMotor.class, "testmotor");

        testmotor.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();
        while (opModeIsActive()){
            boolean run  = gamepad1.a;
            if (run) {
                testmotor.setPower(1);
            }

        }
    }

}
