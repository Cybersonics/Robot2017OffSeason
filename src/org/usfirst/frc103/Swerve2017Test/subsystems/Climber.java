package org.usfirst.frc103.Swerve2017Test.subsystems;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.climberLiftWinch;

import org.usfirst.frc103.Swerve2017Test.commands.Climb;

import edu.wpi.first.wpilibj.command.Subsystem;


public class Climber extends Subsystem {
	
    public void setClimber(double speed){
    	climberLiftWinch.set(speed);
    }

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new Climb());
	}
	
}

