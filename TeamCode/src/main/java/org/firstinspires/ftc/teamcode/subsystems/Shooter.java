package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Shooter {
    private DcMotorEx flywheel;
    private Servo hood;
    private Limelight3A limelight;

    public Shooter(HardwareMap hardwareMap) {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        hood = hardwareMap.get(Servo.class, "hood");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    //Probably some overcomplicated code that does nothing useful
}