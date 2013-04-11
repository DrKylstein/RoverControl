/**
 * 
 */
package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;
import com.example.rovercontrol.io.GrabberPiston;
import com.example.rovercontrol.io.IRSensor;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * @author kyle
 *
 */
public class RetrievePuckState implements State<Robot> {

	public RetrievePuckState(IRSensor irSensor, GrabberPiston piston) {
		irSensor_ = irSensor;
		piston_ = piston;
	}
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onExit()
	 */
	@Override
	public void onExit(Robot robot) {

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#update(long, com.example.rovercontrol.StateMachine)
	 */
	@Override
	public void update(long dtNanos, Robot robot) {
		try {
			if(irSensor_.voltage() > 0.8) {
				piston_.grab();
				// TODO state to transition to
				robot.stateMachine.changeState(null);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private IRSensor irSensor_;
	private GrabberPiston piston_;
	@Override
	public String getName() {
		return "Retrieve Puck";
	}
}
