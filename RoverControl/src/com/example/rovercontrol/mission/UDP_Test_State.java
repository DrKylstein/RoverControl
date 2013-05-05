
package com.example.rovercontrol.mission;

import com.example.rovercontrol.Robot;
import com.example.rovercontrol.control.State;

public class UDP_Test_State implements State<Robot> {
	
	
	// TO DO
	
	// ADD TIMER OR SOMETHING SO NOT SPAMMING SERVER
	// ADD CENTRAL PLACE FOR INFORMATION?
	// STORE RESPONSE FROM GREY GOOSE SOMEWHERE
	
	
	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#onEnter()
	 */
	@Override
	public void onEnter(Robot robot) {
		// TODO Auto-generated method stub
		
		System.out.println("onEnter - UDPtestState\n");
		
		robot.udpClient.setMessage("What should I do?");
		
		try {
			robot.udpClient.udpClientSendReceive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
		
		/*
		robot.udpClient.setMessage("What should I do?");
		
		try {
			robot.udpClient.udpClientSendReceive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

	}

	/* (non-Javadoc)
	 * @see com.example.rovercontrol.State#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "UDP Test State";
	}

}
