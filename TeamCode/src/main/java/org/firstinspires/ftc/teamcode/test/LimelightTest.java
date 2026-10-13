//limelight test stuff here
package org.firstinspires.ftc.teamcode.test;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="LimelightTest", group="Linear Opmode")
public class LimelightTest extends OpMode {
    private Limelight3A limelight;

    public void init(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
    }

    @Override
    public void loop(){
    telemetry.addData("running","true");

    }

}
