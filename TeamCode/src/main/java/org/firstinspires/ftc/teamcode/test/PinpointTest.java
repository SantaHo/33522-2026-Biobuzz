package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import org.firstinspires.ftc.teamcode.hardware.GoBildaPinpointDriver;

@TeleOp(name = "Pinpoint Test", group = "Sensor")
public class PinpointTest extends LinearOpMode {

    private GoBildaPinpointDriver pinpoint;

    @Override
    public void runOpMode() {
        //Very hard hardwareMap
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        //Change once bot built
        pinpoint.setOffsets(0.0, 0.0, DistanceUnit.MM);

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        //Telemetry stuff
        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", pinpoint.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", pinpoint.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", pinpoint.getDeviceVersion());
        telemetry.addData("Heading Scalar", pinpoint.getYawScalar());
        telemetry.addLine("Keep robot still to calibrate IMU...");
        telemetry.update();

        //Press the big button
        waitForStart();
        resetRuntime();

        while (opModeIsActive()) {
            pinpoint.update();

            //Get Posititon
            Pose2D pos = pinpoint.getPosition();

            //Get Velocity
            Pose2D vel = pinpoint.getVelocity();

            telemetry.addData("X (mm)", pos.getX(DistanceUnit.MM));
            telemetry.addData("Y (mm)", pos.getY(DistanceUnit.MM));
            telemetry.addData("Heading (Deg)", pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Robot Ang. Velocity (Deg/sec)", vel.getHeading(AngleUnit.DEGREES));
            telemetry.update();
        }
    }
}