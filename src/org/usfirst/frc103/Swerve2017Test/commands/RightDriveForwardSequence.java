package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class RightDriveForwardSequence extends CommandGroup {
	
	public RightDriveForwardSequence() {
		requires(Robot.drive);
		requires(Robot.gearManipulator);
		
		addSequential(new DriveForward(-60.0, 4500)); //Distance was 3800
		addSequential(new VisionPlaceGear(-60.0));
		addSequential(new VisionLeaveGear());
	}
	
}
