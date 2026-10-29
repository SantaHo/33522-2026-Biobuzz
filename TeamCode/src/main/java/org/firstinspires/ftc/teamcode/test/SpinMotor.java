//This code spins a motor
package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "SpinMotor", group = "Linear Opmode")
public class SpinMotor extends OpMode {

    private DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        motor.setPower(0.5);
        telemetry.update();
    }

    @Override
    public void stop() {
        motor.setPower(0);
    }
}