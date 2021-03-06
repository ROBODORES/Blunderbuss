// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.wrappers.TalonFXEncoder;

public class Shooter extends SubsystemBase {
  /** Creates a new Shooter. */

  public static enum STATES {SHOOT, EXPEL, STOP};

  public static STATES State = STATES.STOP;

  WPI_TalonFX leftMotor;
  WPI_TalonFX rightMotor;

  TalonFXEncoder leftEncoder;
  TalonFXEncoder rightEncoder;

  public static double shootSetpoint = 1700.0;
  public static double hailMarySetpoint = 1900.0;
  public static double setpoint = shootSetpoint;
  public static double expelSetpoint = 600.0;

  public Shooter() {

    leftMotor = new WPI_TalonFX(Constants.Shooter.leftMotor);
    rightMotor = new WPI_TalonFX(Constants.Shooter.rightMotor);

    rightMotor.follow(leftMotor);

    leftMotor.setInverted(TalonFXInvertType.Clockwise);
    rightMotor.setInverted(TalonFXInvertType.OpposeMaster);

    leftEncoder = new TalonFXEncoder(leftMotor);
    rightEncoder = new TalonFXEncoder(rightMotor);
  }

  public void set(double speed){
    double limit = 1.0;

    if(limit < speed){
      speed = limit;
    } else if(speed < 0.0){
      speed = 0.0;
    }

    leftMotor.set(speed);
  }

  public void setVoltage(double voltage){

    leftMotor.setVoltage(voltage);
    rightMotor.setVoltage(voltage);

  }

  public void stop(){
    set(0.0);
  }

  public double getRPM(){

    double rpm = 0.0;

    rpm += leftEncoder.getRPM();
    rpm += rightEncoder.getRPM();
    rpm /= 2.0;

    return rpm;

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
