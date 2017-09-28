package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionAutoSequence extends CommandGroup {
	
	public VisionAutoSequence() {
		requires(Robot.drive);
		requires(Robot.gearManipulator);
		
		addSequential(new VisionPlaceGear(0.0));
		addSequential(new VisionLeaveGear());
	}
	
}
