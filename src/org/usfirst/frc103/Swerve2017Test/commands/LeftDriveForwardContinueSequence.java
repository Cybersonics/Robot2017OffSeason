package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LeftDriveForwardContinueSequence extends CommandGroup {
	
	public LeftDriveForwardContinueSequence() {
		requires(Robot.drive);
		requires(Robot.gearManipulator);

		addSequential(new LeftDriveForwardSequence());
		addSequential(new DriveFieldCentric(0.0, 10000.0, 0.0));
	}

}
