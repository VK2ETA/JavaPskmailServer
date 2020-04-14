/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javapskmail;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author rein
 */
public class Timer_engine {
    Timer timer;
    static private int oldminute = 0;
    /**
     * 
     */
    static public int systemMinute = 0;
    
    /**
     * 
     */
    public  void Timer_engine(){
     timer = new Timer();
    timer.schedule(new DoSecond(), 0, 1000); //subsequent rate
 }
    /**
     * 
     */
    public class DoSecond extends TimerTask{
     
     
     
     /**
      * 
      */
     public void run() {
         try {
             Calendar cal = Calendar.getInstance();
             String Beaconqrg = Main.configuration.getPreference("BEACONQRG");
             int BeaconQrg = Integer.parseInt(Beaconqrg);
             int Minute = cal.get(Calendar.MINUTE);
             int Second = cal.get(Calendar.SECOND);

             // minute timer
             if (Minute != oldminute) {
                 arq myarq = new arq();
                 oldminute = Minute;
                 systemMinute = Minute % 5;
                 // minute timer here
                 int i = systemMinute;

                 //         mainpskmailui.setTxtSpeed("here");
                 System.out.println("TEST" + systemMinute);

                 if (Minute == (i + 10 | i + 20 | i + 30 | i + 40 | i + 50)) {

                     myarq.send_beacon();
                 }

             } // minute timer end
         } catch (Exception ex) {
             // Temporary error handling, bad.
             System.out.println("Fatal error"+ex.getMessage().toString());
         }
     }

           
            
        }
     
 }



/*
public class AnnoyingBeep {
  Toolkit toolkit;

  Timer timer;

  public AnnoyingBeep() {
    toolkit = Toolkit.getDefaultToolkit();
    timer = new Timer();
    timer.schedule(new RemindTask(), 0, 1000); //subsequent rate
  }

  class RemindTask extends TimerTask {
    int numWarningBeeps = 3;

    public void run() {
      if (numWarningBeeps > 0) {
        toolkit.beep();
        System.out.println("Beep!");
        numWarningBeeps--;
      } else {
        toolkit.beep();
        System.out.println("Time's up!");
        //timer.cancel(); //Not necessary because we call System.exit
        System.exit(0); //Stops the AWT thread (and everything else)
      }
    }
  }

  public static void main(String args[]) {
    System.out.println("About to schedule task.");
    new AnnoyingBeep();
    System.out.println("Task scheduled.");
  }
}
 */
