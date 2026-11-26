//Actual main teleop
package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.FieldCentricDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import com.qualcomm.robotcore.hardware.HardwareMap;




@TeleOp(name = "RealMain", group = "TeleOp")
public class RealMain extends LinearOpMode {


    private DcMotor turret;
    private Limelight3A limelight;
    private static final double KP = 0.015;
    private static final double DEADZONE = 1.0;
    private static final double MAX_POWER = 0.3;
    private static final double MIN_POWER = 0.08;

    @Override
    public void runOpMode() {
        turret = hardwareMap.get(DcMotor.class, "motor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //HardwareMap stuff
        //FieldCentricDrivetrain FieldCentricDrivetrain = new FieldCentricDrivetrain(hardwareMap);
        FieldCentricDrivetrain fielddrivetrain = new FieldCentricDrivetrain(hardwareMap);
        Drivetrain drivetrain = new Drivetrain(hardwareMap);
        //Turret turret = new Turret(hardwareMap);


        limelight.pipelineSwitch(0);
        limelight.start();

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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


            boolean resetfieldcentric  = gamepad1.a;
            if (resetfieldcentric) {
                fielddrivetrain.resetYaw();

            }

            //Turret and Shooter
            //Placeholder stuff
            boolean error = false; // :]

            telemetry.addData("Status", "Running");
            telemetry.update();

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                double tx = result.getTx();
                double error1 = tx;
                double power = 0;

                if (Math.abs(error1) > DEADZONE) {
                    power = KP * error1;

                    //Limit motor power
                    power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

                    //Minimum power to overcome friction
                    if (Math.abs(power) < MIN_POWER) {
                        power = Math.signum(power) * MIN_POWER;
                    }

                    turret.setPower(power);
                } else {
                    turret.setPower(0);
                }
            } else {
                turret.setPower(0);
            }
        }
    }
}