package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Autom;

// different methods that can be called
//  robot.drive_forward(power,time)
// robot.drive_backwards(power,time)
// robot.strafe_right(power,time)
// robot.strafe_left(power,time)

@Autonomous(name = "Mikey subsystem Auto")
public class MikeytestAuto extends LinearOpMode {

    @Override
    public void runOpMode(){
        Autom robot = new Autom(hardwareMap, this);

        waitForStart();

        //robot.shoot(1,5000);

        robot.drive_forward(1,1000);

    }

}
