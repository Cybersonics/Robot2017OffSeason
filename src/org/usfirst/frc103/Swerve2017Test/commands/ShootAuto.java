package org.usfirst.frc103.Swerve2017Test.commands;

import org.usfirst.frc103.Swerve2017Test.Robot;
import org.usfirst.frc103.Swerve2017Test.RobotMap;
import org.usfirst.frc103.Swerve2017Test.subsystems.Vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Relay;

public class ShootAuto extends Command {

	//public static final double MAX_FLYWHEEL_SPEED = 3600.0;
	
	// number of consecutive frames where the shooter is on target before firing begins
	public static final int LOCK_ON_FRAME_COUNT_THRESHOLD = 10;
	// elevator speed for shooting
	public static final double ELEVATOR_SPEED = 0.5;
	// agitator speed for shooting
	public static final double AGITATOR_SPEED = -0.6;
	// minimum delay (in seconds) before shooting can begin
	public static final double SHOOT_DELAY = 0.5;
	
	private double waitTime;
	private boolean isShooting;
	private int onTargetCount;
	private double speed;

	public ShootAuto(double speed) {
		requires(Robot.drive);
		requires(Robot.shooter);
		this.speed = speed;
	}
	
	@Override
	protected void initialize() {
		isShooting = false;
		onTargetCount = 0;
		RobotMap.shooterLEDRelay0.set(Relay.Value.kOn);
		waitTime = Timer.getFPGATimestamp() + SHOOT_DELAY;
	}

	@Override
	protected void execute() {
		double flywheelSpeed = speed;
		//double elevatorSpeed = 0.5;
		
		Robot.shooter.setFlywheelSpeed(flywheelSpeed);
		
		if (!isShooting) {
			if (Vision.hasShooterTarget()) {
				double omega = Vision.getShooterOmega(0.0, 0.0);
				if (omega == 0.0) {
					onTargetCount++;
					if (onTargetCount >= LOCK_ON_FRAME_COUNT_THRESHOLD) {
						isShooting = true;
					}
				} else {
					onTargetCount = 0;
				}
				Robot.drive.swerveDrive(0.0, 0.0, omega);
			} else {
				onTargetCount = 0;
			}
		}
		
		if (Timer.getFPGATimestamp() > waitTime && isShooting) {
			//RobotMap.ultrasonic.setEnabled(true);
			//distanceFeet = RobotMap.ultrasonic.getDistance() / 304.8; // converting to feet from millimeters
			//predictedFlywheelSpeed = distanceFeet * 160 + 2000;
			
			Robot.shooter.setElevator(ELEVATOR_SPEED);
			Robot.shooter.setAgitator(AGITATOR_SPEED);
		}
		
		SmartDashboard.putNumber("ShooterTargetSpeed", flywheelSpeed);
		//SmartDashboard.putNumber("Elevator", elevatorSpeed);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		Robot.shooter.setAgitator(0);
		Robot.shooter.setElevator(0);
		Robot.shooter.setFlywheelSpeed(0);
		RobotMap.shooterLEDRelay0.set(Relay.Value.kOff);
	}

	@Override
	protected void interrupted() {
		end();
	}

}
