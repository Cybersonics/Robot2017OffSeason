package org.usfirst.frc103.Swerve2017Test.subsystems;

import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveLeftFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveLeftRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveRightFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.driveRightRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerLeftFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerLeftRear;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerRightFront;
import static org.usfirst.frc103.Swerve2017Test.RobotMap.steerRightRear;

import java.util.Arrays;
import java.util.Collections;

import org.usfirst.frc103.Swerve2017Test.commands.FieldCentricSwerveDrive;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Drive extends Subsystem {

	public static final double WHEEL_BASE_LENGTH = 28.0;
	public static final double WHEEL_BASE_WIDTH = 22.0;
	public static final double ENCODER_COUNT_PER_ROTATION = 1024.0;
	
	public void swerveDrive(double strafe, double forward, double omega) {
        double omegaL2 = omega * (WHEEL_BASE_LENGTH / 2.0);
        double omegaW2 = omega * (WHEEL_BASE_WIDTH / 2.0);
        
        // Compute the constants used later for calculating speeds and angles
        double A = strafe - omegaL2;
        double B = strafe + omegaL2;
        double C = forward - omegaW2;
        double D = forward + omegaW2;
        
        // Compute the drive motor speeds
        double speedLF = speed(B, D);
        double speedLR = speed(A, D);
        double speedRF = speed(B, C);
        double speedRR = speed(A, C);
        
        // ... and angles for the steering motors
    	double angleLF = angle(B, D);
    	double angleLR = angle(A, D);
    	double angleRF = angle(B, C);
    	double angleRR = angle(A, C);
    	
    	// Compute the maximum speed so that we can scale all the speeds to the range [0, 1]
    	double maxSpeed = Collections.max(Arrays.asList(speedLF, speedLR, speedRF, speedRR, 1.0));

    	// Set each swerve module, scaling the drive speeds by the maximum speed
    	setSwerveModule(steerLeftFront, driveLeftFront, angleLF, speedLF / maxSpeed);
    	setSwerveModule(steerLeftRear, driveLeftRear, angleLR, speedLR / maxSpeed);
    	setSwerveModule(steerRightFront, driveRightFront, angleRF, speedRF / maxSpeed);
    	setSwerveModule(steerRightRear, driveRightRear, angleRR, speedRR / maxSpeed);
	}
	
	private double speed(double val1, double val2){
    	return Math.hypot(val1, val2);
    }
    
    private double angle(double val1, double val2){
    	return Math.toDegrees(Math.atan2(val1, val2));
    }
    
    private void setSwerveModule(CANTalon steer, CANTalon drive, double angle, double speed) {
    	double currentPosition = steer.getPosition();
    	double currentAngle = (currentPosition * 360.0 / ENCODER_COUNT_PER_ROTATION) % 360.0;
    	// The angle from the encoder is in the range [0, 360], but the swerve computations
    	// return angles in the range [-180, 180], so transform the encoder angle to this range
    	if (currentAngle > 180.0) {
    		currentAngle -= 360.0;
    	}
    	// TODO: Properly invert the steering motors so this isn't necessary
    	// This is because the steering encoders are inverted
    	double targetAngle = -angle;
    	double deltaDegrees = targetAngle - currentAngle;
    	// If we need to turn more than 180 degrees, it's faster to turn in the opposite direction
    	if (Math.abs(deltaDegrees) > 180.0) {
    		deltaDegrees -= 360.0 * Math.signum(deltaDegrees);
    	}
    	// If we need to turn more than 90 degrees, we can reverse the wheel direction instead and
    	// only rotate by the complement
    	if (Math.abs(deltaDegrees) > 90.0) {
    		deltaDegrees -= 180.0 * Math.signum(deltaDegrees);
    		speed = -speed;
    	}
    	double targetPosition = currentPosition + deltaDegrees * ENCODER_COUNT_PER_ROTATION / 360.0;
    	steer.setSetpoint(targetPosition);
    	drive.set(speed);
    }
    
    public void encoderReset() {
    	
    	driveLeftFront.setPosition(0);
    	driveRightFront.setPosition(0);
    	driveLeftRear.setPosition(0);
    	driveRightRear.setPosition(0);
    }
	
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new FieldCentricSwerveDrive());
    }
	
}
