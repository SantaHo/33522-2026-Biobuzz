package org.firstinspires.ftc.teamcode.ideas;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
public class Limelight {
    OTOSTest OTOSConfig = new OTOSTest();
    private Limelight3A limelight;
    public LLResult llResult;

    public void init(HardwareMap hwMap){
        limelight = hwMap.get(Limelight3A.class, "limelight");
    }

    public void setupLimelight(){
        limelight.start();
        limelight.pipelineSwitch(0);
    }

    public void getLimelightResult(){
        llResult = limelight.getLatestResult();
    }

    public void updateRobotPosition() {
        if (llResult != null && llResult.isValid() && llResult.getBotposeTagCount() >= 2) {

            Pose3D pose3D = llResult.getBotpose_MT2();

            double x = pose3D.getPosition().x * 39.3701;
            double y = pose3D.getPosition().y * 39.3701;
            double heading = pose3D.getOrientation().getYaw();

            SparkFunOTOS.Pose2D pose2D =
                    new SparkFunOTOS.Pose2D(x, y, heading);

            OTOSConfig.myOtos.setPosition(pose2D);
        } else if (llResult != null && llResult.isValid() && llResult.getBotposeTagCount() == 1){
            Pose3D pose3D = llResult.getBotpose();

            double x = pose3D.getPosition().x * 39.3701;
            double y = pose3D.getPosition().y * 39.3701;
            double heading = pose3D.getOrientation().getYaw();

            SparkFunOTOS.Pose2D pose2D =
                    new SparkFunOTOS.Pose2D(x, y, heading);
        }
    }

}