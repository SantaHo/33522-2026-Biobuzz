package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Color Sensor Telemetry", group = "Sensor")
public class ColorSensorTelemetry extends LinearOpMode {

    // Declare the color sensor hardware variable
    private ColorSensor colorSensor;
    private DcMotor motor;

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        // Initialize the color sensor from the hardware map
        // Note: Make sure "colorSensor" matches the exact name given in your robot configuration!
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");

        telemetry.addData("Status", "Initialized. Waiting for Start...");
        telemetry.update();

        // Wait for the driver to press PLAY
        waitForStart();

        while (opModeIsActive()) {
            // Read and display RGB and Alpha values on the Driver Station
            telemetry.addData("Red Value", colorSensor.red());
            telemetry.addData("Green Value", colorSensor.green());
            telemetry.addData("Blue Value", colorSensor.blue());
            telemetry.addData("Alpha (Light)", colorSensor.alpha());
            if (colorSensor.green()/2>colorSensor.red()){
                motor.setPower(1);
            }
            else if (colorSensor.green()/2<colorSensor.red()) {
                motor.setPower(1);
            }
            else{
                motor.setPower(0);
            }
            
            // Push telemetry updates to the driver station screen
            telemetry.update();
        }
    }
}
