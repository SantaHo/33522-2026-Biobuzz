//Actual main teleop
package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.FieldCentricDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@TeleOp(name = "RealMain", group = "TeleOp")
public class RealMain extends LinearOpMode {

    @Override
    public void runOpMode() {
        //HardwareMap stuff
        //FieldCentricDrivetrain FieldCentricDrivetrain = new FieldCentricDrivetrain(hardwareMap);
        FieldCentricDrivetrain resetYaw = new FieldCentricDrivetrain(hardwareMap);
        Drivetrain drivetrain = new Drivetrain(hardwareMap);
        Turret turret = new Turret(hardwareMap);
        Shooter shooter = new Shooter(hardwareMap);

        telemetry.addData("Status", "Ready to start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            //Driving
            double strafe  = -gamepad1.left_stick_x;
            double forward = gamepad1.left_stick_y;
            double rotate  = -gamepad1.right_stick_x;

            //FieldCentricDrivetrain.driveFieldCentric(forward, strafe, rotate);

            drivetrain.drive(forward, strafe, rotate);

            /**
            boolean resetfieldcentric  = gamepad1.a;
            if (resetfieldcentric) {) {
                FieldCentricDrivetrain.resetyaw();

            }
             **/
            //Turret and Shooter
            //Placeholder stuff
            boolean error = false; // :]

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}