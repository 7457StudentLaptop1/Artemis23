// Copyright (c) 2023 FRC 6328
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.


package frc.robot.commands;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DrivetrainSubsystem;

public class AutoBalance extends CommandBase {
  private static final Double speedMetersPerSec = 0.254;
  private static final Double positionThresholdDegrees = 1.0;
  //private static final Double velocityThresholdDegreesPerSec = 6.0;


  private final DrivetrainSubsystem drive;
  private double angleDegrees;

  public AutoBalance(DrivetrainSubsystem drive) {
    this.drive = drive;
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    angleDegrees = Double.POSITIVE_INFINITY;
  }

  @Override
  public void execute() {
    // Calculate charge station angle and velocity
    angleDegrees =
        drive.getGyroscopeRotation().getCos() * drive.getPitch();
           // + drive.getGyroscopeRotation().getSin() * drive.getRoll().getDegrees();
    //double angleVelocityDegreesPerSec =
    //    drive.getGyroscopeRotation().getCos() * Units.radiansToDegrees(drive.getPitchVelocity())
    //        + drive.getGyroscopeRotation().getSin() * Units.radiansToDegrees(drive.getRollVelocity());
    boolean shouldStop = ((angleDegrees < positionThresholdDegrees) && (angleDegrees > -positionThresholdDegrees));
        //(angleDegrees < 0.0 && angleVelocityDegreesPerSec > velocityThresholdDegreesPerSec)
         //   || (angleDegrees > 0.0
         //       && angleVelocityDegreesPerSec < -velocityThresholdDegreesPerSec);

    SmartDashboard.putNumber("AngleDegrees", angleDegrees);
    SmartDashboard.putNumber("Yaw", drive.getGyroscopeRotation().getDegrees());
    SmartDashboard.putNumber("Pitch", drive.getPitch());
    SmartDashboard.putNumber("Roll", drive.getRoll());
    
    // Send velocity to drive
    if (shouldStop) {
      drive.stop();
    } else {
      if (Math.abs(angleDegrees) > 5){
        drive.drive(
          ChassisSpeeds.fromFieldRelativeSpeeds(
              speedMetersPerSec * (angleDegrees > 0.0 ? -1.0 : 1.0),
              0.0,
              0.0,
              drive.getGyroscopeRotation()));
            } else{
              drive.drive(
          ChassisSpeeds.fromFieldRelativeSpeeds(
              0.3*speedMetersPerSec * (angleDegrees > 0.0 ? -1.0 : 1.0),
              0.0,
              0.0,
              drive.getGyroscopeRotation()));
            }
    }
  }

  @Override
  public void end(boolean interrupted) {
    drive.stop();
  }

  @Override
  public boolean isFinished() {
    return false;
    //return Math.abs(angleDegrees) < positionThresholdDegrees;
  }
}
