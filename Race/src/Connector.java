import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;


public class Connector {
	public static boolean start = false;
	private String connected = "Connected";
	private String waiting = "Waiting...";
	private String closing = "Closing...";

	private BTConnection btc;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	// PID constants
	public static float KP = 0.9f;
	public static float KI = 0.033f;
	public static float KD = 6.19f;
	public 	static float tp = 75f;
	
	public static int turnRight = 1;
	public static int turnLeft = 1;
	public static int turnAround = 1;
	public static int turnRightDown = 1;
	public static int turnLeftDown = 1;

	public static int fartNed = 1;
	public static int tætPå = 1;


		
	public void connect(){
		PIDLineFollower listener = new PIDLineFollower();
		Button.ESCAPE.addButtonListener(listener);

		LCD.drawString(waiting, 0, 0);

		btc = Bluetooth.waitForConnection();

		LCD.clear();
		LCD.drawString(connected, 0, 0);

		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
		
		Sound.beep();
		
		
	}

public void listen() {

	while (start != true) {
		try {
			
		
			KP = dis.readFloat();
            KI = dis.readFloat();
            KD = dis.readFloat();
            tp = dis.readInt();
            
            turnRight = dis.readInt();
            turnLeft = dis.readInt();
            turnAround = dis.readInt();
            turnRightDown = dis.readInt();
            turnLeftDown = dis.readInt();
            
            fartNed = dis.readInt();
            tætPå = dis.readInt();

			int startknap = dis.readInt();
			
			if (startknap == 1234) {
				start = true;
				startknap = 0;
			}
		}

		catch (Exception e) {
		}
		
		LCD.clear();
		LCD.drawString("Start!", 0, 0);
		LCD.drawString(String.valueOf(KP),0,1);
		LCD.drawString(String.valueOf(KI),0,2);
		LCD.drawString(String.valueOf(KD),0,3);
		
		LCD.refresh();
		
		
		
	}
	start = false;

}


}
