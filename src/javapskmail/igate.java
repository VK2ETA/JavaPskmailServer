/*
 * igate.java
 * 
 * Copyright (C) 2008 Pär Crusefalk and Rein Couperus
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javapskmail;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Locale;
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
    //VK2ETA add Linked station service
    static ArrayList<String> linkedStationsList = new ArrayList<String>();
    
    public static void start() throws IOException {

        aprsversion = Main.application;

        //Main.wantigate becomes true after connection, so never connects
        //while (!connected & Main.wantigate) {
        while (!connected) {

            //aprscall = Main.configuration.getPreference("CALL");
            aprscall = Main.cleanCallForAprs(Main.configuration.getPreference("CALLSIGNASSERVER"));
            aprspass = getHash(aprscall);

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
            String initaprs = "user " + aprscall + " pass " + aprspass + " vers " + aprsversion + " filter u/PSKAPR" + "\n";

            // send init string to the aprs server
            if (out != null) {
                out.println(initaprs);
            }

            String line = "";

            try {
                while (connected & (line = in.readLine()) != null) {
                    Main.q.Message("APRS connected to " + Main.APRS_Server, 10);
                    break;
                }
            } catch (NullPointerException np) {
                Main.q.Message("APRS server " + Main.APRS_Server + " not available", 10);

            }

            if (connected) {
                break;
            } else if (!connected & aprsindex > 0) {
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
            while ((line = in.readLine()) != null) {
                //VK2ETA Add listening for and forwarding of APRS messages
                //if (line.length() > 0 & line.contains("PSKAPR") & !line.startsWith("#") & Main.mainui.APRS_IS.isSelected()) {
                if (line.length() > 0 & !line.startsWith("#")) {
//                     System.out.println(line);
                    if (line.contains("PSKAPR") && line.contains("GATING")) {
                        checkIfNeedsRemoval(line);
                    } else { 
                        //Must be a message to be forwarded
                        //VK2ETA check what other frames may make it here and filter out if required
                        Main.mapsock.sendmessage(line + "\n");
                        //Do we have an APRS message to forward?
                        //elsif ($line =~ /^(\w*\-*\d*)>(\w*),*(.*?):(.)(\w*\-*\d*)\s*:(.*)(\{*.*)/) {	# message
                        doAprsRfForward(line);
                    }
                    if (Main.mainui.APRS_IS.isSelected()) {
                        Main.aprsbeacontxt += line + "\n";
                    }
                    break;
                } else {
                    break;
                }
            }
        }
    }

    public static boolean write(String aprs_out) throws IOException {

        if (connected) {
            if (aprs_out.length() > 0) {
                out.println(aprs_out);
                Main.aprsbeacontxt = aprs_out + "\n";
                aprs_out = "";
            }
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        out.close();
        in.close();
        aprsSocket.close();
    }

    public static void Loadservers() {
        File f = new File(Main.HomePath + Main.Dirprefix + "aprsservers");
        if (f.exists()) {
            try {
                // Open the file
                FileInputStream fstream = new FileInputStream(Main.HomePath + Main.Dirprefix + "aprsservers");
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
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }

        }

    }

    private static String getHash(String call) {

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

        byte c[] = new byte[10];
        char d[] = new char[10];

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
    
    private static void checkIfNeedsRemoval(String line) {
        //To-Do filter and check for removal
    }

    //Check that station is in the list and was last linked less than 30 minutes ago
    public static boolean isStationLinked(String stationRaw) {
        boolean inList = false;
        String station = stationRaw.toUpperCase(Locale.US);

        //Look in list if already registered
        if (station.length() > 0) {
            for (int i = 0; i < linkedStationsList.size(); i++) {
                if (linkedStationsList.get(i).contains(station)) {
                    //Was it less than 30 minutes ago?
                    long lastLinkTime = 0L;
                    String entry = linkedStationsList.get(i);
                    String[] linkRecord = entry.split("\\|");
                    if (linkRecord.length == 2) { //Valid entries only
                        try {
                            lastLinkTime = Long.parseLong(linkRecord[1]);
                        } catch (NumberFormatException e) {
                            //Nothing
                        }
                        //Max 30 minutes since last interaction as APRS-IS server seem to 
                        //   hold the link for one hour approx.    
                        if (System.currentTimeMillis() - lastLinkTime > 30 * 60000) {
                            //Too old, remove
                            linkedStationsList.remove(i);
                        } else {
                            //All good, valid link from that station
                            inList = true;                            
                        }
                    }
                    break;
                }
            }
        }
        return inList;
    }

    private static void doAprsRfForward(String line) {
        //elsif ($line =~ /^(\w*\-*\d*)>(\w*),*(.*?):(.)(\w*\-*\d*)\s*:(.*)(\{*.*)/) {	# message
        Pattern pl = Pattern.compile("^(\\w*\\-*\\d*)>(\\w*),*(.*?):(.)(\\w*\\-*\\d*)\\s*:(.*)(\\{*.*)");
        Matcher ml = pl.matcher(line);
        if (ml.lookingAt()) {
            String fromcall = ml.group(1).toUpperCase(Locale.US);
            String groupcall = ml.group(2);
            String path = ml.group(3);
            String type = ml.group(4);
            String tocall = ml.group(5).toUpperCase(Locale.US);
            String message = ml.group(6);
            if (isStationLinked(tocall)) {
                //Send over RF (we do not check for duplicates here (YET?)
                //$TXServerStatus = "TXaprsmessage";
                //send_frame($message);
                try {
                    String sendMessage = fromcall + ">PSKAPR::" + tocall + " :" + message;//For now + mesgnumber;
                    Main.q.setCallsign(tocall);
                    Main.q.send_aprsmessage(sendMessage);
                } catch (InterruptedException e) {
                    //Nothing
                }     
           }
        }
    }

   
    //Remove a Station to the linked stations list
    public static void removeStationFromList(String stationRaw) {
        String station = stationRaw.toUpperCase(Locale.US);

        if (station.length() > 0) {
            for (int i = 0; i < linkedStationsList.size(); i++) {
                if (linkedStationsList.get(i).contains(station)) {
                    linkedStationsList.remove(i);
                    break;
                }
            }
        }
    }    

    //Add a Station to the linked stations list
    public static void addStationToList(String newStationRaw) {
        boolean inList = false;
        String newStation = newStationRaw.toUpperCase(Locale.US);
        //Look in list if already registered
        if (newStation.length() > 0) {
            for (int i = 0; i < linkedStationsList.size(); i++) {
                if (linkedStationsList.get(i).contains(newStation)) {
                    inList = true;
                    //Update the time
                    linkedStationsList.set(i, newStation + "|" + System.currentTimeMillis());
                    break;
                }
            }
            //Not in list yet, add
            if (!inList) {
                linkedStationsList.add(newStation + "|" + System.currentTimeMillis());
            }
        }
        //Now purge the list of obsolete stations
        for (int i = 0; i < linkedStationsList.size(); i++) {
            long lastLinkTime = 0L;
            String entry = linkedStationsList.get(i);
            String[] linkRecord = entry.split("\\|");
            if (linkRecord.length == 2) { //Valid entries only
                try {
                    lastLinkTime = Long.parseLong(linkRecord[1]);
                } catch (NumberFormatException e) {
                    //Nothing
                }
                //Max 30 minutes since last interaction as APRS-IS server seem to 
                //   hold the link for one hour approx.    
                if (System.currentTimeMillis() - lastLinkTime > 30 * 60000) {
                    //Too old, remove
                    linkedStationsList.remove(i);
                }
            }
        }
    }

}
