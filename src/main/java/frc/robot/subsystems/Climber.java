// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Climber extends SubsystemBase {
  /** Creates a new Climber. */

  public WPI_TalonFX leftMotor;
  public WPI_TalonFX rightMotor;
  public double armUpSpeed = -0.4;
  public double armDownSpeed = 0.4;

  public double max = 142502.0;
  public double min = 0.0;

  boolean holding = false;
  
  public Climber() {
    leftMotor = new WPI_TalonFX(Constants.Climber.leftMotor);
    rightMotor = new WPI_TalonFX(Constants.Climber.rightMotor);

    leftMotor.setNeutralMode(NeutralMode.Brake);
    rightMotor.setNeutralMode(NeutralMode.Brake);
  }
  
  public void setLeft(double power) {
    leftMotor.set(power);
  }

  public void setRight(double power){
    rightMotor.set(power);
  }

  public void stopLeft() {
    setLeft(0.0);
  }

  public void stopRight(){
    setRight(0.0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
