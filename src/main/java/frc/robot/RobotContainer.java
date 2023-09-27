// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.DriveWithLimelight;
import frc.robot.commands.FreeClimb;
import frc.robot.commands.FreeIntake;
import frc.robot.commands.RunIntakeCommand;
import frc.robot.commands.ScoreLow;
import frc.robot.commands.SetFlywheelToLimelightShot;
import frc.robot.commands.SetFlywheelToSetShot;
import frc.robot.commands.SetFlywheelToSetShotHigh;
import frc.robot.commands.SetFlywheelToSetShotLow;
import frc.robot.commands.Shoot;
import frc.robot.commands.TurnForAngle;
import frc.robot.commands.ZeroYaw;
import frc.robot.commands.SetYaw;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShootingSubsystem;
import frc.robot.subsystems.VisionSubsytem;
import frc.robot.subsystems.IndexerSubsystem;
//import frc.robot.commands.ClimbOrient;
//import frc.robot.commands.ClimberDown;
//import frc.robot.commands.ClimberUp;
import frc.robot.commands.DriveForTime;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final DrivetrainSubsystem m_drivetrainSubsystem = new DrivetrainSubsystem();
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final IndexerSubsystem indexer = new IndexerSubsystem();
  private final ShootingSubsystem shooter = new ShootingSubsystem();
  private final VisionSubsytem vision = new VisionSubsytem();
  private final Climber climber = new Climber();
  public final static XboxController driverJoyStick = new XboxController(0);
  public final static XboxController operatorJoyStick = new XboxController(1);
  public static Double speedmultiplier;
  private final SendableChooser<Command> autoChooser;
  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {

    autoChooser = new SendableChooser<>();
    autoChooser.addOption("Two Ball Hangar Auto", TwoBallHangarAuto());
    autoChooser.addOption("One Ball Auto", OneBallAuto());
    autoChooser.addOption("Two Ball Open Auto", TwoBallOpenAuto());
    autoChooser.addOption("Two Ball Wall Auto", TwoBallWallAuto());
    autoChooser.addOption("OneCubeBalanceAuto", OneCubeBalanceAuto());
    autoChooser.setDefaultOption("OneCubeBalanceAuto", OneCubeBalanceAuto());
    SmartDashboard.putData(autoChooser);
    // Set up the default command for the drivetrain.
    // The controls are for field-oriented driving:
    // Left stick Y axis -> forward and backwards movement
    // Left stick X axis -> left and right movement
    // Right stick X axis -> rotation
    //intake.setDefaultCommand(new RunCommand(()->intake.StopIntake(),intake));
   // indexer.setDefaultCommand(new RunCommand(()->indexer.RunIndexer(),indexer));
    m_drivetrainSubsystem.setDefaultCommand(new DefaultDriveCommand(
            m_drivetrainSubsystem,
            () -> -modifyAxis(driverJoyStick.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
            () -> -modifyAxis(driverJoyStick.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
            () -> -modifyAxis(driverJoyStick.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND
    ));
      climber.setDefaultCommand(new FreeClimb(climber));
      intake.setDefaultCommand(new FreeIntake(intake));
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Back button zeros the gyroscope
    // deprecated commands still work
  
    new Button(driverJoyStick::getStartButton).whenPressed(m_drivetrainSubsystem::zeroGyroscope);

   Button driverAButton = new JoystickButton(driverJoyStick, XboxController.Button.kA.value);
    new Button(driverJoyStick::getBackButton).whenPressed(intake::PurgeIntake).whenReleased(intake::StopIntake);
    new Button(driverJoyStick::getBackButton).whileHeld(indexer::PurgeIndexer).whenReleased(indexer::StopIndexer);


    
  
    new Button(operatorJoyStick::getRightBumper).whileHeld(intake::RunIntake).whenReleased(intake::StopIntake);
    new Button(operatorJoyStick::getRightBumper).whileHeld(indexer::RunIndexer).whenReleased(indexer::StopIndexer);
    new Button(operatorJoyStick::getLeftBumper).whenPressed(intake::PurgeIntake).whenReleased(intake::StopIntake);
   // new Button(driverJoyStick::getYButton).whenPressed(intake::RaiseIntake);
   // new Button(driverJoyStick::getXButton).whenPressed(intake::LowerIntake);
    new Button(driverJoyStick::getRightBumper).whileHeld(indexer::PurgeIndexer).whenReleased(indexer::StopIndexer);
   // new Button(driverJoyStick::getRightBumper).whileHeld(indexer::RunIndexer).whenReleased(indexer::StopIndexer);
    new Button(operatorJoyStick::getXButton).whenHeld(new SetFlywheelToSetShot(shooter)); // added for single joystick control
    new Button(operatorJoyStick::getYButton).whenHeld(new SetFlywheelToSetShotHigh(shooter));
    new Button(operatorJoyStick::getBButton).whenHeld(new SetFlywheelToSetShotLow(shooter));
    Button driverRightTrigger = new Button(() -> driverJoyStick.getRightTriggerAxis() >= .5);
     driverRightTrigger.whenPressed(indexer::ShootIndexer).whenReleased(indexer::StopIndexer);
     // driverRightTrigger.whileHeld(new Shoot(shooter, indexer, vision)).whenReleased(indexer::StopIndexer);
  Button driverLeftTrigger = new Button(() -> driverJoyStick.getLeftTriggerAxis() >= .5);
     // driverLeftTrigger.whileHeld(intake::RunIntake).whenReleased(intake::StopIntake);
     // driverLeftTrigger.whileHeld(indexer::RunIndexer).whenReleased(indexer::StopIndexer);
     
     
  }

public Command TwoBallOpenAuto(){
  return new SequentialCommandGroup(
      new ZeroYaw(m_drivetrainSubsystem), 
      new WaitCommand(1),
      new ParallelCommandGroup( 
        new DriveForTime(m_drivetrainSubsystem, .6).withTimeout(2.75), 
        new RunIntakeCommand(intake, indexer).withTimeout(3)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 180),
      new ParallelCommandGroup(
        new DriveWithLimelight(m_drivetrainSubsystem, vision, 
       () -> modifyAxis(driverJoyStick.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND).withTimeout(2),
       new SetFlywheelToLimelightShot(shooter, vision).withTimeout(2)
      ),
      new ParallelCommandGroup(
        new SetFlywheelToLimelightShot(shooter, vision).withTimeout(3),
        new Shoot(indexer).withTimeout(3)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 226.5),
      new WaitCommand(.5),
      new ZeroYaw(m_drivetrainSubsystem)
      );
}

public Command OneCubeBalanceAuto(){
  return new SequentialCommandGroup(
    new SetYaw(m_drivetrainSubsystem, 180),
    new WaitCommand(1),
    new SequentialCommandGroup(
      new ScoreLow(indexer).withTimeout(3),
      new DriveForTime(m_drivetrainSubsystem, -1).withTimeout(4.5)
    )
  );
}
public Command TwoBallHangarAuto(){
  return new SequentialCommandGroup(
      new ZeroYaw(m_drivetrainSubsystem), 
      new WaitCommand(1),
      new ParallelCommandGroup( 
        new DriveForTime(m_drivetrainSubsystem, .6).withTimeout(2.5), 
        new RunIntakeCommand(intake, indexer).withTimeout(3)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 180),
      new ParallelCommandGroup(
        new DriveWithLimelight(m_drivetrainSubsystem, vision, 
       () -> modifyAxis(driverJoyStick.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND).withTimeout(2),
       new SetFlywheelToLimelightShot(shooter, vision).withTimeout(2)
      ),
      new ParallelCommandGroup(
        new SetFlywheelToLimelightShot(shooter, vision).withTimeout(3),
        new Shoot(indexer).withTimeout(3)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 136.5),
      new WaitCommand(.5),
      new ZeroYaw(m_drivetrainSubsystem)
      );
}

public Command OneBallAuto(){
  return new SequentialCommandGroup(
      new ZeroYaw(m_drivetrainSubsystem), 
      new WaitCommand(1),
      new ParallelCommandGroup( 
        new DriveForTime(m_drivetrainSubsystem, .6).withTimeout(2.5)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 180),
      new ParallelCommandGroup(
        new DriveWithLimelight(m_drivetrainSubsystem, vision, 
       () -> modifyAxis(driverJoyStick.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
       () -> modifyAxis(driverJoyStick.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND).withTimeout(2),
       new SetFlywheelToLimelightShot(shooter, vision).withTimeout(2)
      ),
      new ParallelCommandGroup(
        new SetFlywheelToLimelightShot(shooter, vision).withTimeout(3),
        new Shoot(indexer).withTimeout(3)
      ),
      new TurnForAngle(m_drivetrainSubsystem, 178.5), 
      new WaitCommand(.5),
      new ZeroYaw(m_drivetrainSubsystem)
      );
}

public Command TwoBallWallAuto(){
  return new SequentialCommandGroup(
    new ZeroYaw(m_drivetrainSubsystem), 
    new WaitCommand(1),
    new ParallelCommandGroup(
      new DriveForTime(m_drivetrainSubsystem, .6).withTimeout(1.7), 
      new RunIntakeCommand(intake, indexer).withTimeout(3)
    ),
    new WaitCommand(1),
    new DriveForTime(m_drivetrainSubsystem, .6).withTimeout(.5),
    new TurnForAngle(m_drivetrainSubsystem, 180),
    new ParallelCommandGroup(
      new DriveWithLimelight(m_drivetrainSubsystem, vision, 
     () -> modifyAxis(driverJoyStick.getLeftY()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
     () -> modifyAxis(driverJoyStick.getLeftX()) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND, 
     () -> modifyAxis(driverJoyStick.getRightX()) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND).withTimeout(2),
     new SetFlywheelToLimelightShot(shooter, vision).withTimeout(2)
    ),
    new ParallelCommandGroup(
      new SetFlywheelToLimelightShot(shooter, vision).withTimeout(3),
      new Shoot(indexer).withTimeout(3)
    ),
    new TurnForAngle(m_drivetrainSubsystem, 271.5),
    new WaitCommand(.5),
    new ZeroYaw(m_drivetrainSubsystem)
    );
}
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous 
      return autoChooser.getSelected();
  }

  private static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

  private static double modifyAxis(double value) {
    // Deadband
    value = deadband(value, 0.05);

    // Square the axis
    value = Math.copySign(value * value, value);

    return value;
  }

}
