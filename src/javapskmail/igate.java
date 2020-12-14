/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javapskmail;

    import java.io.*;
    import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rein
 */
public class igate {

        static Socket aprsSocket = null;
        static PrintWriter out = null;
        static BufferedReader in = null;
        static String[] aprshosts = {"netherlands.aprs2.net", "germany.aprs2.net", "italys.aprs2.net"};
        static int aprsindex = 0;
        static String aprscall = "";
        static String aprscheck = "";
        static String aprspass = "";
        static String aprsversion = "";
        static boolean connected = false;
        static String aprs_output = "";
        static String[] status = {"", "Testing"};
        static int maxstatus = 2;
        static boolean aprsavailable = true;
        

    public static void start() throws IOException {

        aprsversion = Main.application;

        //Main.wantigate becomes true after connection, so never connects
        //while (!connected & Main.wantigate) {
        while (!connected) {

            //aprscall = Main.configuration.getPreference("CALL");
            aprscall = Main.configuration.getPreference("CALLSIGNASSERVER");
            aprspass = getHash (aprscall);

            connected = true;
            try {
                aprsSocket = new Socket(Main.APRS_Server, 14580);
                out = new PrintWriter(aprsSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                                        aprsSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host:" + Main.APRS_Server + " " + e);
                connected = false;
                aprsindex++;

            } catch (IOException e) {
                System.out.println("Couldn't get I/O for "
                               + "the connection to:" + Main.APRS_Server + " " + e);
                connected = false;
                aprsindex++;

            }



    //            String initaprs = "user " + aprscall + " pass " + aprspass + " vers " + aprsversion + " filter u/" + aprscall + "\n";
                String initaprs = "user " + aprscall + " pass " + aprspass + " vers " + aprsversion + " filter u/PSKAPR"  + "\n";

                // send init string to the aprs server

                if (out != null) {
                    out.println(initaprs);
                }

                String line = "";

                try {
                    while(connected & (line = in.readLine()) != null) {
                             Main.q.Message("APRS connected to " + Main.APRS_Server, 10);
                             break;
                        }
                    }
                    catch (NullPointerException np) {
                             Main.q.Message("APRS server " + Main.APRS_Server + " not available", 10);

                    }

                if (connected) {
                    break;
                }
                else if (!connected & aprsindex > 0) {
                    System.out.println("No APRS connection, giving up...");
//                    Main.mainui.IgateSwitch.setSelected(false);
//                    Main.mainui.IgateSwitch.setText("OFF");
                    aprsavailable = false;
                    break;
                } else {
                    System.out.println("reconnecting aprs");
                }



             try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(igate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void read() throws IOException {
        String line = "";
     
        if (in.ready()) {
            while((line = in.readLine()) != null) {
                if (line.length() > 0 & line.contains("PSKAPR") & !line.startsWith("#") & Main.mainui.APRS_IS.isSelected()) {
//                     System.out.println(line);
                     Main.aprsbeacontxt += line + "\n";
                     Main.mapsock.sendmessage(line + "\n");
                     break;
                } else {
                    break;
                }
            }
        }
    }


    public static void write(String aprs_out) throws IOException {

        if (connected) {

            if (aprs_out.length() > 0) {
                out.println(aprs_out);
                Main.aprsbeacontxt = aprs_out + "\n";
                aprs_out = "";
            }

        }
    }

   public void close() throws IOException {
             out.close();
            in.close();
            aprsSocket.close();
   }

     public static void Loadservers () {
        File f = new File(Main.HomePath + Main.Dirprefix +  "aprsservers");
        if (f.exists()) {
            try{
    // Open the file
                FileInputStream fstream = new FileInputStream(Main.HomePath + Main.Dirprefix +  "aprsservers");
    // Get the object of DataInputStream
                DataInputStream lfin = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(lfin));
                String strLine;
    //Read File Line By Line
                int i;
                for (i = 0; i < 3; i++) {
                    strLine = br.readLine();
                    Main.APRS_Server = strLine;
                    System.out.println(Main.APRS_Server + "|");
                }


    //Close the input stream
                lfin.close();
            }catch (Exception e){//Catch exception if any
                 System.err.println("Error: " + e.getMessage());
            }

        }

    }


      private static String getHash (String call) {

          int Out = 0;
          int i;

            Pattern fm = Pattern.compile("([A-Z0-9]+).*");
            Matcher fmm = fm.matcher(call);
            if (fmm.lookingAt()) {
                call = fmm.group(1);
            }

          byte k[] = new byte[2];

          k[0] = 115;
          k[1] = (byte) 226;

          int len = call.length();

          byte c[] = new byte [10];
          char d[] = new char [10];

          for (i = 0; i < len; i++) {
            d[i] = 0;
          } 

          for (i = 0; i < len; i++) {
            d[i] = call.charAt(i);
          }
          
          for (i = 0; i < len; i++) {
            c[i] = (byte) d[i];
          }

          for (i = 0; i < len; i += 2) {
            k[0] ^= c[i];
            k[1] ^= c[i + 1];
          }


          k[0] &= 127;
          int highbyte = (int) k[0];

          if (highbyte < 0) {
              highbyte += 256;
          }

          k[1] &= 255;

          int lowerbyte = (int) k[1];

          if (lowerbyte < 0) {
              lowerbyte += 256;
          }

          Out = highbyte * 256 + lowerbyte;

          return Integer.toString(Out);
      }

}


