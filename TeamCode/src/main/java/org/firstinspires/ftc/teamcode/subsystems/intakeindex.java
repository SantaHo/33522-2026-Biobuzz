package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class intakeindex {

    private DcMotor index;
    private DcMotor intake;
    private Servo intake1;
    private Servo intake2;


    public void intake(double power){
        intake.setPower(power);
    }
    public void intakereverse(double power){
        intake.setPower(-power);
    }
    public void stopintake(){
        intake.setPower(0);
    }

    /*
    public deployintake(HardwareMap hardwareMap){

    }
    */

    public intakeindex(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        index = hardwareMap.get(DcMotor.class, "index");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        index.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void index(double power){
        index.setPower(power);

    }


}
