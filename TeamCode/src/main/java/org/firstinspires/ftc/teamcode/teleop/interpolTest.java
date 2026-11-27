package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.interpolate;
@TeleOp(name = "interpolTest", group = "TeleOp")
public class interpolTest extends OpMode {
        Limelight3A limelight;
        interpolate test = new interpolate();
        LLResult llresult;
        private DcMotor flywheel;
        private Servo hood;

        @Override
        public void init() {
            flywheel = hardwareMap.get(DcMotor.class, "flywheel");
            hood = hardwareMap.get(Servo.class,"hood");
        }

        @Override
        public void loop() {
            telemetry.addData("Speed: ", interpolate.rpm(llresult.getTy()));

            telemetry.addData("TA: ", llresult.getTa());
            telemetry.addData("HoodPos: ", interpolate.hood(llresult.getTa()));
            //flywheel.setPower(interpolate.rpm(llresult.getTy()));
            flywheel.setPower(interpolate.rpm(1));
            hood.setPosition(interpolate.hood(llresult.getTa()));
        }


}