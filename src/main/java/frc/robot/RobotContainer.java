// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.DTProperties;
import frc.robot.Robot.FourBallAuto;
import frc.robot.Robot.TwoBallAuto;
import frc.robot.autonomous.AutoMechManager;
import frc.robot.commands.Climb;
import frc.robot.commands.Drive;
import frc.robot.commands.Funnel;
import frc.robot.commands.ShineBrightLikeADiamond;
import frc.robot.commands.Convey;
import frc.robot.commands.Shoot;
import frc.robot.commands.Suck;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.MechManager.AUTO_STATES;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Conveyor;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Shooter m_shooter = new Shooter();
  private final Drivetrain m_drivetrain = new Drivetrain();
  private final Intake m_intake = new Intake();
  private final Hopper m_hopper = new Hopper();
  private final Conveyor m_conveyor = new Conveyor();
  private final Climber m_climber = new Climber();
  private final LEDs m_leds = new LEDs();

  // Controllers
  public static final XboxController driveController = new XboxController(0);
  public static final XboxController mechController = new XboxController(1);

  // Buttons
  public final JoystickButton shootButton = new JoystickButton(driveController, Button.kRightBumper.value);
  public final JoystickButton intakeButton = new JoystickButton(driveController, Button.kLeftBumper.value);
  public final JoystickButton expelButton = new JoystickButton(driveController, Button.kB.value);
  public final JoystickButton hailMaryButton = new JoystickButton(driveController, Button.kY.value);

  public final JoystickButton hopperIntakeButton = new JoystickButton(mechController, Button.kX.value);
  public final JoystickButton liftButton = new JoystickButton(mechController, Button.kY.value);
  public final JoystickButton lowerButton = new JoystickButton(mechController, Button.kA.value);
  public final JoystickButton armUpButton = new JoystickButton(mechController, Button.kRightBumper.value);
  public final JoystickButton armDownButton = new JoystickButton(mechController, Button.kLeftBumper.value);
  public final JoystickButton releaseStopperButton = new JoystickButton(mechController, Button.kB.value);

  // AUTO CHOOSER

  public SendableChooser <String> autoChooser;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    // Configure default commands
    m_drivetrain.setDefaultCommand(new Drive(m_drivetrain));
    m_conveyor.setDefaultCommand(new Convey(m_conveyor));
    m_hopper.setDefaultCommand(new Funnel(m_hopper));
    m_intake.setDefaultCommand(new Suck(m_intake));
    m_shooter.setDefaultCommand(new Shoot(m_shooter));
    m_climber.setDefaultCommand(new Climb(m_climber));
    m_leds.setDefaultCommand(new ShineBrightLikeADiamond(m_leds));


    // AUTO STUFF

    String fB = "fourBall";
    String tB = "twoBall";
    String nB = "noBall";

    autoChooser = new SendableChooser<>();
    autoChooser.setDefaultOption("4 Ball", fB);
    autoChooser.addOption("2 Ball", tB);
    autoChooser.addOption("Do Nothing", nB);

    SmartDashboard.putData(autoChooser);
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    hailMaryButton.whileHeld(() -> Shooter.setpoint = Shooter.hailMarySetpoint);
    hailMaryButton.whenReleased(() -> Shooter.setpoint = Shooter.shootSetpoint);

    intakeButton.whenHeld(new InstantCommand(() -> Intake.State = Intake.STATES.INTAKE)).whenHeld(new InstantCommand(() -> Hopper.State = Hopper.STATES.FUNNEL));
    intakeButton.whenReleased(() -> Intake.State = Intake.STATES.STOP).whenReleased(() -> Hopper.State = Hopper.STATES.POST_FUNNEL);

    expelButton.whileHeld(() -> Intake.State = Intake.STATES.EXPEL).whileHeld(() -> Hopper.State = Hopper.STATES.EXPEL);
    expelButton.whenReleased(() -> Intake.State = Intake.STATES.STOP).whenReleased(() -> Hopper.State = Hopper.STATES.STOP);

    shootButton.whileHeld(() -> Shooter.State = Shooter.STATES.SHOOT);
    shootButton.whenReleased(() -> Shooter.State = Shooter.STATES.STOP).whenReleased(
                             () -> Conveyor.State = Conveyor.STATES.INDEX).whenReleased(
                             () -> Hopper.State = Hopper.STATES.STOP);

    hopperIntakeButton.whileHeld(() -> Hopper.State = Hopper.STATES.FUNNEL);
    hopperIntakeButton.whenReleased(() -> Hopper.State = Hopper.STATES.STOP);

    armUpButton.whileHeld(() -> Climber.State = Climber.STATES.ARMUP);
    armUpButton.whenReleased(() -> Climber.State = Climber.STATES.STOP);

    armDownButton.whileHeld(() -> Climber.State = Climber.STATES.ARMDOWN);
    armDownButton.whenReleased(() -> Climber.State = Climber.STATES.STOP);

    releaseStopperButton.whileHeld(() -> Climber.State = Climber.STATES.RELEASE);
    releaseStopperButton.whenReleased(() -> Climber.State = Climber.STATES.STOP);
  }

  private RamseteCommand getRamseteCommand(Trajectory trajectory){

    RamseteCommand ramseteCommand = new RamseteCommand(trajectory, m_drivetrain::getPose,
        new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
        new SimpleMotorFeedforward(DTProperties.ksVolts, DTProperties.kvVoltSecondsPerMeter, DTProperties.kaVoltSecondsSquaredPerMeter),
        DTProperties.kDriveKinematics,
        m_drivetrain::getWheelSpeeds,
        new PIDController(DTProperties.kPDriveVel, 0, 0),
        new PIDController(DTProperties.kPDriveVel, 0, 0),
        // RamseteCommand passes volts to the callback
        m_drivetrain::tankDriveVolts,
        m_drivetrain
    );

    return ramseteCommand;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {

    // CREATE PATHS
    // 4 BALL PATHS
    RamseteCommand ball1 = getRamseteCommand(FourBallAuto.ball1Trajectory);
    RamseteCommand shoot1 = getRamseteCommand(FourBallAuto.shoot1Trajectory);
    RamseteCommand ball2and3 = getRamseteCommand(FourBallAuto.ball2and3Trajectory);
    RamseteCommand shoot2 = getRamseteCommand(FourBallAuto.shoot2Trajectory);

    // 2 BALL PATHS
    RamseteCommand ball1_TWO_BALL = getRamseteCommand(TwoBallAuto.ball1Trajectory);
    RamseteCommand shoot1_TWO_BALL = getRamseteCommand(TwoBallAuto.shoot1Trajectory);


    // COMMANDS
    Command fourBall = new AutoMechManager(AUTO_STATES.ENABLE_INTAKE).andThen(
                  ball1).andThen(
                  shoot1).andThen(
                  new AutoMechManager(AUTO_STATES.SHOOT)).andThen(
                  new AutoMechManager(AUTO_STATES.ENABLE_INTAKE)).andThen(
                  ball2and3).andThen(
                  new AutoMechManager(AUTO_STATES.INTAKE)).andThen(
                  shoot2).andThen(
                  new AutoMechManager(AUTO_STATES.DISABLE_INTAKE)).andThen(
                  new AutoMechManager(AUTO_STATES.SHOOT));


    Command twoBall = new AutoMechManager(AUTO_STATES.ENABLE_INTAKE).andThen(
                      ball1_TWO_BALL).andThen(
                      new AutoMechManager(AUTO_STATES.INTAKE)).andThen(
                      shoot1_TWO_BALL).andThen(
                      new AutoMechManager(AUTO_STATES.DISABLE_INTAKE)).andThen(
                      new AutoMechManager(AUTO_STATES.SHOOT));

    // GET SELECTED AUTO

    String choice;
    
    try{
      choice = autoChooser.getSelected();
    } catch(Exception e){
      SmartDashboard.putString("Worked?", "Nah");
      choice = "Didn't Choose";
    }

    if(choice == "fourBall"){
    
      // 4 BALL
    
      // Reset odometry to the starting pose of the trajectory.
      m_drivetrain.resetOdometry(FourBallAuto.ball1Trajectory.getInitialPose());
    
      return fourBall;
    } else if(choice == "twoBall"){

      // 2 BALL

      // Reset odometry to the starting pose of the trajectory.
      m_drivetrain.resetOdometry(TwoBallAuto.ball1Trajectory.getInitialPose());

      return twoBall;
    } else if(choice == "noBall"){

      // DON'T MOVE

      return new InstantCommand();

    } else{

      // 4 BALL, AS FALL BACK OPTION

      // Reset odometry to the starting pose of the trajectory.
      m_drivetrain.resetOdometry(FourBallAuto.ball1Trajectory.getInitialPose());

      return fourBall;

    }
  }
}
