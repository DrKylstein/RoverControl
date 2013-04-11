/**
 * 
 */
package com.example.rovercontrol;

/**
 * @author kyle
 *
 */
public class DrunkTestState implements State<Robot> {
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit(Robot robot) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, Robot robot) {
		// TODO Auto-generated method stub
		robot.motion.setSpeed(0.5);
		robot.motion.setRotationSpeed(0.0);		
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
