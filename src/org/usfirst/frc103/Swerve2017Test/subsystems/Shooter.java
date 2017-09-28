package org.usfirst.frc103.Swerve2017Test.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.*;

import org.usfirst.frc103.Swerve2017Test.commands.Shoot;

public class Shooter extends Subsystem {
	
	// currently not used
	public static final double FLYWHEEL_SPEED_ACCEPTABLE_ERROR = 30.0;
	
	public void setElevator(double speed) {
		shooterElevator.set(speed);
	}
	
	public double getFlywheelSpeed() {
		return shooterFlyWheel.getSpeed();
	}
	
	public void setFlywheelSpeed(double speed) {
		shooterFlyWheel.setSetpoint(speed);
		//shooterFlyWheel.set(speed);
	}
	
	public void setAgitator(double speed) {
		agitator.set(speed);
	}
	
	public boolean isFlywheelOnTarget() {
		return Math.abs(-shooterFlyWheel.getSpeed() - shooterFlyWheel.getSetpoint()) < FLYWHEEL_SPEED_ACCEPTABLE_ERROR;
	}

	@Override
    public void initDefaultCommand() {
		setDefaultCommand(new Shoot());
    }
    
}

