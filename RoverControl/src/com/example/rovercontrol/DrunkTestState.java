/**
 * 
 */
package com.example.rovercontrol;

/**
 * @author kyle
 *
 */
public class DrunkTestState implements State {

	private RobotMotion robotMotion_;
	
	DrunkTestState(RobotMotion motion) {
		robotMotion_ = motion;
	}
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, StateMachine machine) {
		// TODO Auto-generated method stub
		robotMotion_.setSpeed(0.5);
		robotMotion_.setRotationSpeed(0.0);		
		System.out.println("rover_debug drunktest_loop");
	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Sir, have you been drinking?";
	}

}
