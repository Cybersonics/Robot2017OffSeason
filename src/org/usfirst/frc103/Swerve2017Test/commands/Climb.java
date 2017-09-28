package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climb extends Command {
	
	public Climb() {
		requires(Robot.climber);
	}
	
	@Override
	protected void execute() {
		try {
			double speed = -Robot.oi.controller.getY(Hand.kLeft);
			speed = (Math.abs(speed) > 0.1 ? speed : 0.0);
			/*double max = (Math.abs(RobotMap.climberLiftWinch.getOutputCurrent()) > 5.0 ? -1.0 : -0.35);
			speed = Math.max(speed, max);*/
			SmartDashboard.putNumber("Climber Speed", -speed);
			SmartDashboard.putNumber("Climber Current", RobotMap.climberLiftWinch.getOutputCurrent());
			Robot.climber.setClimber(Math.max(-speed, 0.0));
			//Robot.climber.setClimber(-speed);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

    @Override
	protected void end() {
    }

    @Override
	protected void interrupted() {
    	end();
    }

}
