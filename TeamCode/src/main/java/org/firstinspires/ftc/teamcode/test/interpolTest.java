package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.interpolate;

public class interpolTest {
    public class LimelightTest extends OpMode {
        Limelight3A limelight;
        interpolate test = new interpolate();
        LLResult llresult;
        private DcMotor flywheel;

        @Override
        public void init() {
            flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        }

        @Override
        public void loop(){
            telemetry.addData("Speed: ",interpolate.rpm(llresult.getTy()));
            telemetry.addData("TA: ",llresult.getTy());
            flywheel.setPower(interpolate.rpm(llresult.getTy()));
        }
    }


}