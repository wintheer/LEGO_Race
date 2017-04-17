import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.util.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.Math;


public class UdenGearing implements ButtonListener
{
	private static Connector con;
	
	private static int powerA = 0;
	private static int powerC = 0;
	
	private float elapsedTime = 0;
	private float startTime;
	private boolean turnedRightAlready = false;
	private boolean turnedLeftAlready = false;
	
	private boolean somethingNear = false;
	private boolean somethingFar = false;
	
	private int stage = 1;
	private int t�ller = 0;
	
	private float samletTid;
	
	private float tp;
	
	private boolean skiftSide = false;
	
	private float skiftSideTid;
	
	UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
	BlackWhiteSensor sensor = new BlackWhiteSensor(SensorPort.S3);
	


private void Run() throws InterruptedException {
		//DataLogger logger = new DataLogger("Simple.txt");
	skiftSide = false;	
	
	stage = 1;
		somethingNear = false;
		somethingFar = false;
		sensor.calibrate();
		
		if(stage < 6){
		LCD.clear();}

			con.listen();
	    	 LCD.drawString(String.valueOf(con.KP), 0, 1);
				LCD.drawString(String.valueOf(con.KI), 0, 2);
				LCD.drawString(String.valueOf(con.KD), 0, 3);
				LCD.drawInt((int) con.tp, 0, 4);

				LCD.refresh();
		
		
	     
	     float offset = sensor.threshold();
	     float integral = 0f;
	     float lastError = 0f;
	     float derivative = 0f;
	     
	     tp = con.tp;
	     samletTid = System.currentTimeMillis();
	     forward(700, 100);
	     findStregenh�jre(80);
	     
	    long time = System.currentTimeMillis();
		 
	     while (! Button.LEFT.isDown())
	     {
	    	 
	    	 if(stage == 3 && skiftSide == true){
	    		 if((System.currentTimeMillis() - skiftSideTid) > 500){
	    			 findStregenvenstre(80);
	    			 forwardVenstre(500, 80);
	    			 skiftSide = false;
	    			 findStregenh�jre(80);
	    		 }
	    			 
	    	 }
	    		 
	    		 
	    		 
	    	 if(stage < 4){
	    	 k�rOpad();
	    	 }
	    	 else{
	    		 k�rNedad();
	    	 }
	    	 
	    	 
	    	 LCD.clear();
	    	LCD.drawString("stage: " + stage, 0,3);
	    	LCD.drawInt(sonic.getDistance(), 0, 1) ;
	    	LCD.refresh();
	    	
	    	
		     float lightValue = sensor.light();
			//	logger.writeSample((int) lightValue);

		     float error = lightValue - offset;
		     integral = integral + error;
		     derivative = error - lastError;
		     float turn = con.KP * error + con.KI * integral + con.KD * derivative;
		     		    	 
		    if(skiftSide == true){

			      powerA = Math.round(tp - turn);
			      powerC = Math.round(tp + turn); 
		    }
		    else{
		    	powerA = Math.round(tp + turn);
			      powerC = Math.round(tp - turn);
		    }
		    	
		    
		     Car.forward(powerA, powerC);

		     lastError = error;
		   	

	     }
	     //logger.close();


	     Car.stop();
	     LCD.clear();
	     LCD.drawString("Program stopped", 0, 0);
	     LCD.refresh();
		
	}


  public static void main (String[] aArg)
  throws Exception
  {
	  
	  UdenGearing SLF = new UdenGearing();
	  con = new Connector();
		
		con.connect();
	     

		
		while(!Button.ESCAPE.isDown()){
			Sound.beepSequence();			
			SLF.Run();
		
		}
     

   }

  public void left180( int tid) {
		startTime = System.currentTimeMillis();
		elapsedTime = 0;
		while (elapsedTime <= tid) {
			
			elapsedTime = Math.abs(startTime - System.currentTimeMillis());
		
			Car.forward(74, 100);
		}	
	}
	
	public void right180(int tid) {
		startTime = System.currentTimeMillis();
		elapsedTime = 0;
		while (elapsedTime <= tid) {
			//Turn right
			elapsedTime = Math.abs(startTime - System.currentTimeMillis());
		
			Car.forward(97, 73);
		}
	}
	public void forward(int tid, int fart){
		startTime = System.currentTimeMillis();
		elapsedTime = 0;
		while (elapsedTime <= tid) {
			//Turn right
			elapsedTime = Math.abs(startTime - System.currentTimeMillis());
		
			Car.forward(fart, fart+2);
		}
		
	}
	
	public void turnAround() {
		startTime = System.currentTimeMillis();
		Car.forward(0, 0);
			
		while (elapsedTime <= con.turnAround) {
			elapsedTime = Math.abs(startTime - System.currentTimeMillis());
			MotorPort.C.controlMotor(80, 1);
			MotorPort.A.controlMotor(80, 2);
		}
	}
	
	public void findStregenh�jre(int fart){
		 while(sensor.getValue() > sensor.threshold() ){
			  Car.forward(fart+2, fart);
			 }
	}
	public void findStregenvenstre(int fart){
		 while(sensor.getValue() > sensor.threshold() ){
			  Car.forward(fart, fart+8);
			 }
	}
  
	public void k�rOpad(){
		int afstand = 20;
		if(sonic.getDistance() > afstand && somethingFar == false){
  		  if(t�ller > 5){
  			  somethingFar = true;
  			  if (stage == 1){
  				Sound.beep();
  				  right180(con.turnRight);
  				  
  				  findStregenh�jre(85);
  				  somethingFar = false;
  			  }
  			  if(stage == 2){
  				Sound.beep();
  				left180(con.turnLeft);
  				
				 skiftSide = true;
				 skiftSideTid = System.currentTimeMillis();
				  somethingFar = false;
				 

  			  }
  				if(stage == 3){
  					Sound.beep();
  					forward(500, 90);
  					turnAround();
  					forward(800, 75);
  					Sound.beepSequenceUp();
  					findStregenh�jre(con.fartNed); 
  					Sound.twoBeeps();

  					tp = con.fartNed - 4;
  					
  				}
	    		 stage++; 
	    		 t�ller = 0;
  		  }
  		  else{
  			  t�ller++;
  		  }
  	 }}
	
public void k�rNedad(){
	if(sonic.getDistance() < con.t�tP� && somethingNear == false){
		  if(t�ller > 5){
			  somethingNear = true;
			  if (stage == 4){
				  Sound.beep();
				  right180(con.turnRightDown);
				 
				  findStregenvenstre(con.fartNed);
				  somethingNear = false;
			  }
			  if(stage == 5){
				  Sound.beep();
				left180(con.turnLeftDown);
				skiftSide = true;
				 findStregenvenstre(con.fartNed);
				  somethingNear = false;
			}
				if(stage == 6){
					Sound.beep();
					forward(250, 100);
					float F�rdig = System.currentTimeMillis() - samletTid;
					LCD.clear();
					LCD.drawString(String.valueOf(F�rdig), 1, 1);
					Car.forward(0,	0);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
	    		 stage++; 
	    		 t�ller = 0;
		  }
		  else{
			  t�ller++;
		  }
}
		}
		/*
  	 if(sonic.getDistance() <= 18 && somethingFar == false){
  		 t�ller = 0;
  	 }
  	 
  	 if(sonic.getDistance() >= 18 && somethingNear == true ){
  		 if(t�ller > 5){
  			 somethingNear = false;
  			 stage++;
  			 t�ller = 0;
  		 }
  		 else{
  			 t�ller++;
  		 }
  	 }
  	 
  	 if(sonic.getDistance() < 8 && somethingNear == true){
  		 t�ller = 0;
  	 }
  	 */

	/*
public void k�rNedad(){
	if(sonic.getDistance() > 12 && somethingFar == false){
		  if(t�ller > 5){
			  somethingNear = true;
	    		 stage++; 
	    		 t�ller = 0;
		  }
		  else{
			  t�ller++;
		  }
	 }
	 if(sonic.getDistance() >= 8 && somethingNear == false){
		 t�ller = 0;
	 }
	 
	 if(sonic.getDistance() >= 8 && somethingNear == true ){
		 if(t�ller > 5){
			 somethingNear = false;
			 stage++;
			 t�ller = 0;
		 }
		 else{
			 t�ller++;
		 }
	 }
	 
	 if(sonic.getDistance() < 8 && somethingNear == true){
		 t�ller = 0;
	 }

}
  */
public void buttonPressed(Button b) {
	// TODO Auto-generated method stub
	
}
public void buttonReleased(Button b) {
	// TODO Auto-generated method stub
	
}
public void forwardVenstre(int tid, int fart){
	startTime = System.currentTimeMillis();
	elapsedTime = 0;
	while (elapsedTime <= tid) {
		//Turn right
		elapsedTime = Math.abs(startTime - System.currentTimeMillis());
	
		Car.forward(fart, fart+4);
	}
	
}
}