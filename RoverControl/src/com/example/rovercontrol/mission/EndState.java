/**
 * 
 */
package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

/**
 * @author kyle
 *
 */
public class EndState implements State<Robot> {

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

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#getName()
	 */
	@Override
	public String getName() {
		return "End of Mission";
	}

}
