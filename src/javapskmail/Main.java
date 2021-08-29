/*
 * Main.java
 * 
 * Copyright (C) 2008 Pär Crusefalk and Rein Couperus
 * Copyright (C) 2018-2021 Pskmail Server and RadioMsg sections by John Douyere (VK2ETA) 
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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author per
 */
public class Main {

    //VK2ETA: Based on "jpskmail 1.7.b";
    static String version = "0.9.4.a25-RMSG";
    static String application = "jpskmailserver " + version;// Used to preset an empty status
    static String versionDate = "20210830";
    static String host = "localhost";
    static int port = 7322;
    static boolean modemTestMode = false; //For when we check that Fldigi is effectively running as expected
    static long lastModemCharTime = 0L;
    static boolean requestModemRestart = false;
    static int modemAutoRestartDelay = 0; //In minutes, 10080 = 7 days = once per week by default
    public static boolean justReceivedRSID = false;
    static boolean onWindows = true;
    static String ModemPreamble = "";  // String to send before each frame
    static String ModemPostamble = ""; // String to send after each frame
    static modemmodeenum Mymode = modemmodeenum.PSK500R;
    static modemmodeenum[] modeprofile;
    static modemmodeenum linkmode = modemmodeenum.PSK500R;
    static int modemnumber = 0;
    static modemmodeenum defaultmode = modemmodeenum.PSK500R;
    static String CurrentModemProfile = "0";
    static int sending_link = 5;
    static int sending_beacon = 0;
    static boolean CBeacon = true;
    static String HomePath = "";
    static String InstallPath = "";
    static String Dirprefix = "/.pskmail/";
    static String Separator = "/";
    //RadioMsg directories
    static final String DirInbox = "RadioMsgInbox";
    static final String DirArchive = "RadioMsgArchive";
    static final String DirSent = "RadioMsgSentbox";
    //static final String DirTemp = "Temp";
    //static final String DirLogs = "Logs";
    static final String DirImages = "RadioMsg-Images";
    static final String messageLogFile = "RadioMsg.log";
    //
    static String Mailoutfile = "";
    static File pending = null;
    static String pendingstr = "";
    static String Pendingdir = "";
    static String Outpendingdir = "";
    static String filetype = "";
    static String myfile = "";
    static String fileDestination = "";
    static File consolelog = null;
    static String LogFile = "client.log";
    static String Transactions = "";
    static boolean compressedmail = false;
    static boolean Bulletinmode = false;
    static boolean IACmode = false;
    static boolean comp = false;
    static boolean debug = false;
    static String Sendline = "";
    static String SendCommand = "";
    static String telnethost = "";
    static String telnetport = "";
    static String userid = "";
    static String pass = "";
    static int DCD = 0;
    static int MAXDCD = 3;
    static int Persistence = 4;
    //static boolean BlockActive = false;
    static boolean EOTrcv = false;
    static String TXblocklength = "5";
    static int Maxblocks = 8;
    static int RXBlocksize = 0;
    static int Totalbytes = 0;
    static boolean TXActive = false;
    static int Second = 30;  // Beacon second
    static String[] Modes = {"       ", "THOR8", "MFSK16", "THOR22",
        "MFSK32", "PSK250R", "PSK500R", "PSK500",
        "PSK250", "PSK63", "PSK125R", "MFSK64", "THOR11",
        "DOMINOEX5", "CTSTIA", "DOMINOEX22", "DOMINOEX11"};
    static String[] AltModes = {"       ", "THOR8", "MFSK16", "THOR22",
        "MFSK32", "PSK125R", "PSK250R", "PSK250", "PSK250", "CTSTIA"};
    @SuppressWarnings("StaticNonFinalUsedInInitialization")
    static String[] Currentmodes = Modes;
    static boolean UseAlttable = false;
    static String modes = "7654321";
    static int Bulletin_time = 0;
    static modemmodeenum TxModem = modemmodeenum.PSK500R;
    static modemmodeenum RxModem = modemmodeenum.PSK500R;
    static modemmodeenum Modemarray[];
    static String RxModemString = "PSK500R";
    static String DefaultTXmodem = "PSK500R";
    static String LastRxModem = "PSK500R";
    static String LastTxModem = "PSK500R";
    static boolean wantbulletins = true;

    // globals to pass info to gui windows
    static String monitor = "";
    static boolean Monitor = false;
    static boolean monmutex = false;
    static String mainwindow = "";
    static boolean mainmutex = false;
    static String MSGwindow = "";
    static String Mailheaderswindow = "";
    static String FilesTextArea = "";
    static String Status = "Listening";
    static String Statusline = "";
    static String Accu = "";
    static int StatusLineTimer;
    static boolean txbusy = false;
    static boolean rxbusy = false;
    static boolean autolink = true;
    static int protocol = 1;
    static String protocolstr = "1";

    // globals for communication
    static String Icon;
    static String ICONlevel;
    static int APRSMessageNumber;
    static String APRS_Server = "netherlands.aprs2.net";
    static String APRSCall = "";
    static String mycall;     // mycall from options
    static String myserver;    // myserver from options
    static String TTYCaller;     // TTY caller
    static String TTYConnected = "";
    static String TTYmodes = "6";
    static String Motd = "";
    static boolean disconnect = false;
    static long Systime;
    static int DCDthrow;
    //RxDelay is the measured delay between the return to Rx of the server and the end of the RSID tx by the client
    static final double initialRxDelay = 2.0f;//Initial 2 seconds delay of RX just in case
    static double RxDelay = initialRxDelay; 
    static double RxDelayCount = initialRxDelay;
    static String connectsecond;
    static long oldtime = 0L;
    static int Missedblocks = 0;
    static long blockval = 0; //msec 
    static int charval = 0; //msecs
    static int chartime = 0;
    static int blocktime; // seconds
    static int idlesecs = 0;
    static String LastBlockExchange = "  ";
    static long LastSessionExchangeTime = 0;
    static boolean isDisconnected = false;
    static boolean Connected = false;
    public static boolean connectingPhase = false; //True from connect request until receipt of greeting/Server info
    public static boolean Connecting = false; //True until first acknowledgment of server's connect ack
    static int Connecting_time = 0;
    static boolean Aborting = false;
    static boolean Scanning = false;
    static boolean linked = false; // flag for link ack
    static String linkedserver = "";
    static int[][] rxdata = new int[10][10];
    static String session = ""; // present session
    static String oldsession = "";
    static String[] sessions = new String[64];
    static boolean validblock = true;
    static String myrxstatus = "   "; // last rx status
    static String TX_Text; // output queue
    static int Progress = 0;
    static String DataSize = "";
    static String Servers[] = {"", "", "", "", "", "", "", "", "", ""};
    static double AvgTxMissing = 0;
    static double AvgRxMissing = 0;
    static double hiss2n = 50;
    static double mys2n = 50;
    static double snr = 0.0;
    static String rx_snr = "";
    static double SNR[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    static String Lastheard[] = {"", "", "", "", "", "", "", "", "", ""};
    static int packets_received[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static String modes_received[] = {"", "", "", "", "", "", "", "", "", ""};
    static int strength[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int snr_db = 0;
    static int TimeoutPolls = 0;
    static boolean JustDowngradedRX = false;
    static boolean status_received = false;
    static final int maxNumberOfAcks = 5;
    static int NumberOfAcks = maxNumberOfAcks;
    static int Freq_offset = 1000;
    static int Quality = 0;
    static int sql = 30;
    static int sqlfloor = 1;
    static String statustxt = "";
    static boolean stxflag = false;

    // Positions
    static String Positions[][] = new String[100][5];
    // GPS handles
    static serialport gpsport;    // Serial port object
    static nmeaparser gpsdata;    // Parser for nmea data
    // gpsd data
    static boolean HaveGPSD = false;
    static boolean WantGpsd = false;
    static boolean NewGPSD = false;
    static boolean WantRigctl = false;
    static boolean wantScanner = false;
    static boolean Scanenabled = true;
    static String CurrentFreq = "0";
    static String ServerFreq = "0";
    static String freqstore = "0";
    static boolean summoning = false;
    static String GPSD_latitude = "";
    static String GPSD_longitude = "";
    static Socket gpsdSocket = null;
    static PrintWriter gpsdout = null;
    static BufferedReader gpsdin = null;
    static String gpsd_data[] = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    static long t1 = System.currentTimeMillis();
    static boolean wantigate = false;
    //Pskmail server and RadioMsg 
    static boolean WantServer = false;
    static boolean WantRelayOverRadio = false;
    static boolean WantRelayEmails = false;
    static boolean WantRelayEmailsImmediat = false;
    static boolean WantRelaySMSs = false;
    static boolean WantRelaySMSsImmediat = false;
    //Time at which to re-start scanning if we disabled it by command over the air
    static long restartScanAtEpoch = 0L;

    static String callsignAsServer = "";
    static String accessPassword = "";

    //crypto
    static String strkey = "1234";
    static String Passwrd = "password";
    static String hispubkey = "";
    static crypt cr = null;
    static String serverversion = "1.1";
    static double sversion = 1.1;

    static Session sm = null;
    static String aprsbeacontxt = "";
    static boolean Serverbeacon = false;

    static String XmlRpc_URL = "http://127.0.0.1:7362/RPC2";

    // arq object
    static arq q;
    // Config object
    public static config configuration; // Static config object
    // Error handling and logging object
    static loggingclass log;

    // Our main window
    static mainpskmailui mainui;
    
    // Modem handle
    static public Modem m;
    static String RXmodemindex = "";

    // File handles
    static FileWriter bulletin = null;
    static FileReader hdr = null;
    // DCD
    static String DCDstr;
    // APRS server socket
    static aprsmapsocket mapsock;
    static boolean aprsserverenabled = true;
    static Integer aprsserverport = 8063;
    static Amp2 Bul;

    //Radio Messages variables
    static public boolean receivingRadioMsg = false;
    static public long possibleRadioMsg = 0L; //Time at which we just an RSID, a Radio message is possible.
    static public String lastRsidReceived = ""; //Last RSID received from modem (string)
    static public boolean radioMsgWorking = false; //Radiomsg processing emails or web pages - do not scan
    static public String FileNameString = "";
    static public long lastCharacterTime = 0L;//Time of last character received from Modem
    static public boolean haveSOH = false;

    /**
     * @param args the command line arguments
     */
    //@SuppressWarnings("empty-statement")
    public static void main(String[] args) throws InterruptedException {

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // handle exception
//       System.out.println("Problem setting look and feel.");
        }

        // Create error handling class
        log = new loggingclass("jpskmail.log");

        try {
            String Blockline = "";

            // Call the folder handling method
            handlefolderstructure();

            // Create config object
            configuration = new config(HomePath + Dirprefix);

            host = configuration.getPreference("MODEMIP");

            // Get settings and initialize
            handleinitialization();
            /* Moved modem init to after gui to allow for auto launch of Fldigi without slowing 
            the gui down and allows for a message to be displayed while fldigi is launched
            // start the modem thread
            System.out.println("About to create new Modem.");
            m = new Modem(host, port);
            Thread myThread = new Thread(m);
            // Start the modem thread
            myThread.setDaemon(true);
            System.out.println("Launching modem thread.");            
            myThread.start();
             */
            // Make arq object
            //System.out.println("About to create new ARQ.");            
            q = new arq();

            q.Message(version, 10);

            // Make calendar object
            //           Calendar cal = Calendar.getInstance();
            // Make amp2 object
            Bul = new Amp2();
            Bul.init();
            Thread.sleep(1000);

            if (WantGpsd & !onWindows) {
                handlegpsd();
            }

            // Handle GPS
            if (!HaveGPSD) {
                handlegps();
            }
            /*
            if (want_igate) {
               try {
                    igate.start();
               } catch (IOException ex) {
 //                  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
              }
            }
             */
            // Make session object
            //System.out.println("About to create new Session.");            
            sm = new Session();  // session, class

            // Show the main window (center screen)
            //System.out.println("about to sleep 20sec");
            //Thread.sleep(20000);
            System.out.println("about to new mainpskmailui");
            mainui = new mainpskmailui();
            System.out.println("about to pack");
//vk2eta debug            
            mainui.pack();
            System.out.println("about to setLocationRelativeTo");
            mainui.setLocationRelativeTo(null); // position in the center of the screen
            System.out.println("about to setDefaultCloseOperation");
            mainui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            System.out.println("about to setVisible");
            mainui.setVisible(true);
//            mainui.disableMboxMenu();

            // start the modem thread
            System.out.println("About to create new Modem.");
            m = new Modem(host, port);
            Thread myThread = new Thread(m);
            // Start the modem thread
            myThread.setDaemon(true);
            System.out.println("Launching modem thread.");
            myThread.start();

            //vk2eta debug
            System.out.println("Starting UI timers");
            //VK2ETA locking up at startup when heavy CPU load. Solution: Delayed timer's
            //  start until after main gui init to avoid firing gui actions before it is initilised.
            //Ok to start UI timers here as the events run on the EDT thread and coalesces the 
            //  events if the load is too high
            mainui.u.start();
            mainui.timer200ms.start();
            System.out.println("Timers started");

            // Start the aprs server socket
            mapsock = new aprsmapsocket();
            mapsock.setPort(aprsserverport);
            mapsock.setPortopen(aprsserverenabled);
            mapsock.start();

            // init modemarray
            Modemarray = m.pmodes;

            // init rxdata array
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 0; j++) {
                    rxdata[i][j] = 0;
                }
            }

            // init random generator for DCD
            Random generator = new Random();

            //Always have RXid ON so that TTY connects and igates beacons can be heard on any mode
            q.send_rsid_command("ON");

            // Main  loop
            q.send_rsid_command("ON");
            //q.send_txrsid_command("ON");

            //Launch separate thread to monitor and relay incoming emails and messages if required
            RMsgProcessor.startEmailsAndSMSsMonitor();

            //vk2eta debug
            System.out.println("entering receive loop");
            while (true) {
                // anything to send to the modem?
                // Send a command to the modem ?

                while (TXActive) {
                    Thread.sleep(50);
                }

                m.Sendln(SendCommand);
                Thread.sleep(50);

                SendCommand = "";

                //Handle RadioMsg messages only when fully idle
                if (Sendline.length() == 0 & !TXActive
                        & !Connected & !Connecting & !Aborting
                        & TTYConnected.equals("") & !receivingRadioMsg) {
                    if (RMsgTxList.getAvailableLength() > 0) {
                        Main.TXActive = true; //Moved up to prevent change in mode when replying
                        m.txMessage = RMsgTxList.getOldest();
                        //Set Mode
                        SendCommand += "<cmd><mode>" + m.txMessage.rxMode + "</mode></cmd>";
                        m.Sendln(SendCommand);
                        SendCommand = "";
                        Thread.sleep(250);
                        //Set TX Rsid
                        q.send_txrsid_command("ON");
                        m.Sendln(SendCommand);
                        SendCommand = "";
                        Thread.sleep(100);
                        //Send message
                        Sendline = "\n\n" + m.txMessage.formatForTx(false) + "\n"; //No CCIR modes for now
                        m.Sendln(Sendline);
                        //Log in monitor screen
                        Main.monitor += "\n*TX*  " + "<SOH>"
                                + Sendline.replace(Character.toString((char) 1), "")
                                        .replace(Character.toString((char) 4), "") + "<EOT>";
                        Sendline = "";
                    }
                }
                //Always reset the radioMsgWorking flag regardless (allows for change of frequency/mode)
                radioMsgWorking = false;

// if (Sendline.length() > 0) {System.out.println("MAIN:" + Sendline);   }        
                // see if tx active and DCD is off and we have exhausted the extra reception delay
                if (Sendline.length() > 0 & !TXActive & DCD == 0 & RxDelayCount < 0.1f) {
//                    System.out.println("MAIN2:" + Sendline);
                    //VK2ETA DCDthrow not used 
                    //DCDthrow = generator.nextInt(Persistence);
                    //     System.out.println("DCD:" + DCDthrow);               
                    if (Connected | Connecting | Aborting | !TTYConnected.equals("")) {
                        //We are in some session as client or server
                        if (Aborting) {
                            Aborting = false;
                        }
                    } else {
                        //Reset DCD for next round if we are not in a session
                        MAXDCD = Integer.parseInt(configuration.getPreference("DCD", "0"));
                    }
                    //VK2ETA DCDthrow not used 
                    //if (DCDthrow == 0) {
                    //      System.out.println("MAIN3:" + Sendline); 
                    String Sendline_cp = Sendline;
                    try {
//       System.out.println("MAIN4:" + Sendline_cp);                   
                        int TxDelay = 0;
                        String TxDelayStr = configuration.getPreference("TXDELAY", "0");
//System.out.println("TXDELAY:" + TxDelayStr);  
                        if (TxDelayStr.length() > 0) {
                            TxDelay = Integer.parseInt(TxDelayStr);
                        }
                        if (TxDelay > 0) {
                            Thread.sleep(TxDelay * 1000);
                        }
                        // System.out.println("MAIN5" );                        
                        //  Add a 2 seconds delay when mode is MFSK16 (1 sec for MFSK32) to prevent overlaps as
                        //  the trail of MFSK is very long
                        //if (Main.RxModem.equals(modemmodeenum.MFSK16)) {
                        //    Thread.sleep(2000);
                        //} else if (Main.RxModem.equals(modemmodeenum.MFSK32)) {
                        //    Thread.sleep(1000);
                        //}
                        //Try to stop Fldigi locking up by having a delay between the mode change and the data
                        //  block as Fldigi needs to re-initialize the modem at each mode change
                        String SendMode = "<cmd><mode>" + getTXModemString(TxModem) + "</mode></cmd>";
                        //System.out.println("TXMODEM for connect:" + getTXModemString(TxModem));
                        //       System.out.println("MAIN6:" + Sendline_cp);
                        Main.TXActive = true;
                        m.Sendln(SendMode);
                        Thread.sleep(250);
                        m.Sendln(Sendline_cp);
                        Sendline_cp = "";
                        Sendline = "";
                    } catch (Exception e) {
                        Main.monitor += ("\nModem problem. Is fldigi running?");
                        log.writelog("Modem problem. Is fldigi running?", e, true);
                    }
                    //} else {
                    //    if (!Connected & !Connecting) {
                    //        DCD = MAXDCD;
                    //    }
                    //}
                }
                // receive block

                try {
                    if (m.checkBlock()) {

                        Blockline = m.getMessage();
// System.out.println("BLOCK=" + Blockline);
                        RXBlock rxb = new RXBlock(Blockline);
                        if (!rxb.valid && !rxb.validWithPW) {
                            validblock = false;
                        } else {
                            validblock = true;
                        }

                        if (validblock & Monitor) {
                            char c = Blockline.charAt(6);

                            int i = (int) c - 32;
                            String calls = "";
                            if (i < 64) {
                                calls = sessions[i];

                                if (calls != null) {
                                    mainui.appendMainWindow(mainui.getClock() + " " + calls + Blockline + "\n");
                                } else {
                                    mainui.appendMainWindow(mainui.getClock() + " " + Blockline + "\n");
                                }
                            }

                        }

                        if (!Bulletinmode & !IACmode) {

                            if (Connected) {

                                // status block from server
                                if (rxb.type.equals("s")
                                        & rxb.valid & rxb.session.equals(session)) {
                                    oldsession = session;
                                    idlesecs = 0;      // reset idle timer
                                    Main.TimeoutPolls = 0; // Reset timeout polls count
                                    // set blocktime for idle time measurement...
                                    if (Blockline.length() > 8) {
                                        charval = (int) (blockval / (Blockline.length() - 4)); // msec
                                        //Must account for Client's TX delay, RSID and silences, FEC/interleaver delay, decoding delay
                                        //Done at connect time, should be enough 
                                        //blocktime = (charval * 64 / 1000) + m.firstCharDelay;
                                    }
                                    //Move processing of block before decision on mode upgrade
                                    sm.RXStatus(rxb.payload);   // parse incoming status packet
                                    // get the tx status
                                    myrxstatus = sm.getTXStatus();
                                    if (!LastBlockExchange.equals(sm.getBlockExchanges())) {
                                        LastSessionExchangeTime = System.currentTimeMillis() / 1000;
                                        LastBlockExchange = sm.getBlockExchanges();
                                    }
                                    // set the modem type for TX if client. For TTY server, adjust TX mode based on received s2n from TTY client.
                                    //Common data needed for later
                                    String pbyte = rxb.protocol;

                                    char pchr = pbyte.charAt(0);
                                    int pint = (int) pchr;
                                    if (TTYConnected.equals("Connected")) {
                                        //Auto speed/mode adjustment
                                        //I am a TTY server (protocol byte = quality of receive by client)
                                        //Turn RXid and TXid OFF as I am a server and I received a good "s" block
                                        q.send_rsid_command("OFF");
                                        //Exception is for frequency sensitive modes like MFSK16, MFSK8, DOMINOEX5
                                        if (Main.TxModem == modemmodeenum.MFSK16 
                                                || Main.TxModem == modemmodeenum.MFSK8
                                                || Main.TxModem == modemmodeenum.DOMINOEX5) {
                                            q.send_txrsid_command("ON");
                                        //} else {
                                        //    q.send_txrsid_command("OFF");
                                        }
                                        //Adjust my TX mode (as a server) AND the Client's TX modes
                                        pint = (pint - 32) * 100 / 90;
                                        hiss2n = decayaverage(hiss2n, pint, 2);
                                        //Get current RX modem position in table
                                        int currentmodeindex = 0;
                                        //Should I upgrade/downgrade my TX mode?
                                        AvgTxMissing = decayaverage(AvgTxMissing, Session.tx_missing.length(), 2);
                                        if (AvgTxMissing > 3) {
                                            //Downgrade Tx mode
                                            currentmodeindex = getClientModeIndex(Main.TxModem);
                                            if (currentmodeindex < TTYmodes.length() - 1) { //List in decreasing order of speed
                                                Main.TxModem = getClientMode(currentmodeindex + 1);
                                                q.send_txrsid_command("ON");
                                                sm.SetBlocklength(5); //restart with medium block length
                                                JustDowngradedRX = false; // Make RX mode downgrade first (if necessary)
                                            }
                                            //Reset link quality indicators
                                            AvgTxMissing = 0;
                                            Main.hiss2n = 50; //Reset to mid-range
                                        } else {
                                            if ((hiss2n > 85) & (AvgTxMissing < 1)) {
                                                //Upgrade Rx speed
                                                currentmodeindex = getClientModeIndex(Main.TxModem);
                                                if (currentmodeindex > 0) { //List in decreasing order of speed
                                                    Main.TxModem = getClientMode(currentmodeindex - 1);
                                                    q.send_txrsid_command("ON");
                                                    sm.SetBlocklength(4); //restart with small block length
                                                    JustDowngradedRX = true; // Make TX mode downgrade first (if necessary)
                                                }

                                                Main.hiss2n = 50; //Reset to mid-range
                                            }
                                        }
                                        //Should I upgrade the client's TX mode?
                                        mys2n = decayaverage(mys2n, Main.snr, 2);
                                        currentmodeindex = 0;
                                        AvgRxMissing = decayaverage(AvgRxMissing, Session.rx_missing.length(), 2);
                                        if (AvgRxMissing > 3) {
                                            //Downgrade Rx speed
                                            currentmodeindex = getClientModeIndex(Main.RxModem);
                                            if (currentmodeindex < TTYmodes.length() - 1) { //List in decreasing order of speed
                                                Main.RxModem = getClientMode(currentmodeindex + 1);
                                                Main.RxModemString = m.getModemString(Main.RxModem);
                                                blocktime = m.getBlockTime(Main.RxModem);
                                                JustDowngradedRX = true; // Make TX mode downgrade first (if necessary)
                                            }
                                            //Reset link quality indicators
                                            AvgRxMissing = 0;
                                            Main.mys2n = 50; //Reset to mid-range
                                        } else {
                                            if ((Main.mys2n > 85) & (AvgRxMissing < 1)) {
                                                //Upgrade Rx speed
                                                currentmodeindex = getClientModeIndex(Main.RxModem);
                                                if (currentmodeindex > 0) { //List in decreasing order of speed
                                                    Main.RxModem = getClientMode(currentmodeindex - 1);
                                                    Main.RxModemString = m.getModemString(Main.RxModem);
                                                    blocktime = m.getBlockTime(Main.RxModem);
                                                    JustDowngradedRX = false; // Make RX mode downgrade first (if necessary)
                                                }
                                                Main.mys2n = 50; //Reset to mid-range
                                            }
                                        }

                                    } else { //I am a client (protocol byte = my TX mode)
                                        //Turn RXid ON as I am a client
                                        q.send_rsid_command("ON");
                                        pint = (int) pchr - 48;
                                        if (pint < 9 & pint > 0) {
                                            TxModem = m.getnewmodem(pint);
                                            if (CurrentModemProfile.equals("0")) {
                                                TxModem = RxModem;
                                            }
                                        } else if (pint == 0) {
                                            TxModem = RxModem;
                                        }
                                    }

                                    //Moved processing of block before decision on mode upgrade
                                    if (Session.tx_missing.length() > 0 | Main.TX_Text.length() > 0) {
                                        String outstr = sm.doTXbuffer();
                                        q.send_data(outstr);
                                    } else {
                                        myrxstatus = sm.getTXStatus();

                                        q.send_status(myrxstatus);  // send our status
                                    }
                                    Main.validblock = true;

                                } else if (Connected & (rxb.type.equals("p"))
                                        & rxb.valid & rxb.session.equals(session)) {
                                    sm.RXStatus(rxb.payload);   // parse incoming status packet

                                    myrxstatus = sm.getTXStatus();
                                    q.send_status(myrxstatus);  // send our status
                                    Main.txbusy = true;
                                    // disconnect request
                                } else if (Connected & rxb.type.equals("d") & (rxb.session.equals(session) | rxb.session.equals("0"))) {
                                    Status = "Listening";
                                    Connected = false;
                                    mainui.disableMboxMenu();
                                    session = "";
                                    Totalbytes = 0;
                                    sm.FileDownload = false;
                                    try {
                                        if (sm.pFile != null) {
                                            sm.pFile.close();
                                        }
                                    } catch (IOException e) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                                    }
//                                q.send_rsid_command("OFF");
                                    // ident block
                                } else if (rxb.session.equals(session) & rxb.type.equals("i")) {
                                    // discard
                                    // info block
                                } else if (rxb.valid & rxb.session.equals(session)) {
                                    myrxstatus = sm.doRXBuffer(rxb.payload, rxb.type);
                                } else if (rxb.session.equals(session)) {

                                    myrxstatus = sm.doRXBuffer("", rxb.type);

                                }

                                // PI4TUE 0.9.33-13:28:52-IM46>
                                if (Blockline.contains(q.servercall)) {
                                    //Pattern ppc = Pattern.compile(".*(\\d\\.\\d).*\\-\\d+:\\d+:(\\d+)\\-(.*)M(\\d+)");
                                    Pattern ppc = Pattern.compile(".*\\S+\\s\\S+\\s(\\S{3}).*\\-\\d+:\\d+:(\\d+)\\-(.*)M(\\d+)");
                                    //System.out.println(Blockline);
                                    Matcher mpc = ppc.matcher(Blockline);
                                    connectsecond = "";
                                    String localmail = "";
                                    if (mpc.lookingAt()) {
                                        serverversion = mpc.group(1);
                                        sm.serverversion = mpc.group(1);
                                        sversion = Double.parseDouble(serverversion);
//     System.out.println(sversion);
                                        connectsecond = mpc.group(2);
                                        localmail = mpc.group(3);
                                        if (localmail.contains("L")) {
                                            mainui.enableMboxMenu();
                                        }
                                        if (sversion > 1.1) {
                                            //     System.out.println("success");
                                            sm.hispubkey = mpc.group(4);
                                            hispubkey = sm.hispubkey;

                                            cr = new crypt();

                                            String output = cr.encrypt(sm.hispubkey, Passwrd);

                                            Main.TX_Text += "~Mp" + output + "\n";
                                            //   System.out.println(Main.TX_Text);                                        
                                        }
                                    } else {
                                        //Mini-server connection, "Hi" message
                                        Pattern pps = Pattern.compile(".*" + q.servercall + " V(\\d{1,2}\\.\\d{1,2}\\.\\d{1,2})(.\\d{1,2}){0,1}, Hi.*");
                                        //System.out.println(Blockline);
                                        Matcher mps = pps.matcher(Blockline);
                                        if (mps.lookingAt()) {
                                            Main.connectingPhase = false;
                                        }
                                    }
                                }
                            } //End if (connected)

                            if (!Connected & Blockline.contains("QSL") & Blockline.contains(q.callsign)) {
                                String pCheck = "";
                                //Pattern psc = Pattern.compile(".*de ([A-Z0-9\\-]+)\\s(?:(\\d*)|((\\d+)\\s+(\\d+))\\s)([0123456789ABCDEF]{4}).*");
                                Pattern psc = Pattern.compile(".*de ([A-Z0-9\\-]+)\\s*(?:(?:(\\d+\\s)(\\d+\\s))|(\\d*\\s))([0123456789ABCDEF]{4}).*");
                                Matcher msc = psc.matcher(Blockline);
                                String scall = "";
                                rx_snr = "";
                                String numberOfMails = "";
                                if (msc.lookingAt()) {
                                    scall = msc.group(1);
                                    if (msc.group(4) != null) {
                                        rx_snr = msc.group(4).trim();
                                    } else {
                                        rx_snr = msc.group(2).trim();
                                        numberOfMails = msc.group(3).trim();
                                    }
                                    pCheck = msc.group(5);
                                }
                                // fill the servers drop down list
                                char soh = 1;
                                String sohstr = Character.toString(soh);
                                String checkstring = "";
                                if (rx_snr.equals("")) {
                                    checkstring = sohstr + "00uQSL " + q.callsign + " de " + scall + " ";
                                } else if (!rx_snr.equals("") && !numberOfMails.equals("")) {
                                    checkstring = sohstr + "00uQSL " + q.callsign + " de " + scall + " " + rx_snr + " " + numberOfMails + " ";
                                    //System.out.println("RX_SNR:" + rx_snr);
                                    mainui.appendMainWindow("From " + scall + ": " + rx_snr + "%, " + numberOfMails + " mails\n");
                                    setrxdata(scall, Integer.parseInt(rx_snr));
                                } else if (!rx_snr.equals("") && numberOfMails.equals("")){
                                    checkstring = sohstr + "00uQSL " + q.callsign + " de " + scall + " " + rx_snr + " ";
                                    //System.out.println("RX_SNR:" + rx_snr);
                                    mainui.appendMainWindow("From " + scall + ": " + rx_snr + "%\n");
                                    setrxdata(scall, Integer.parseInt(rx_snr));
                                }
                                String check = q.checksum(checkstring);
                                if (check.equals(pCheck)) {
                                    rxb.get_serverstat(scall);
                                    int i = 0;
                                    boolean knownserver = false;
                                    for (i = 0; i < 10; i++) {
                                        //                              System.out.println(Servers[i] + scall);
                                        if (scall.equals(Servers[i])) {
                                            knownserver = true;
                                            break;
                                        }
                                    }
                                    if (!knownserver) {
                                        mainui.addServer(scall); // add to servers drop down list
                                    }
                                }
                            } else if (!Connected & Blockline.contains(":71 ")) {
                                Pattern psc = Pattern.compile(".*00u(\\S+):71\\s(\\d*)\\s([0123456789ABCDEF]{4}).*");
                                Matcher msc = psc.matcher(Blockline);
                                String scall = "";
                                String pCheck = "";
                                rx_snr = "";
                                if (msc.lookingAt()) {
                                    scall = msc.group(1);
                                    rx_snr = msc.group(2);
                                    pCheck = msc.group(3);
                                }
                                // fill the servers drop down list
                                String checkstring = "";
                                if (!rx_snr.equals("")) {
                                    checkstring = "00u" + scall + ":71 " + rx_snr + " ";
                                    //                                       System.out.println("RX_SNR:" + rx_snr);
                                    mainui.appendMainWindow("From " + scall + ": " + rx_snr + "%\n");
                                    setrxdata(scall, Integer.parseInt(rx_snr));
                                } else {
                                    checkstring = "00u" + scall + ":71 ";
                                }

                                String check = q.checksum(checkstring);
                                if (check.equals(pCheck)) {
                                    rxb.get_serverstat(scall);

                                    // switch off txrsid
                                    //                                      q.send_txrsid_command("OFF");
                                } else {
//                                        System.out.println("check not ok.\n");
                                }
                            } else if (!Connected & Blockline.contains(":26 ")) {
                                // System.out.println(Blockline);
                                //Uncompressed APRS Beacon or APRS message
                                Pattern bsc = Pattern.compile(".*00u(\\S+):26\\s(.)(.*)(.)([0123456789ABCDEF]{4}).*");
                                Matcher bmsc = bsc.matcher(Blockline);
                                String scall = "";
                                String binfo = "";
                                if (bmsc.lookingAt()) {
                                    scall = bmsc.group(1);
                                    String type = bmsc.group(2);
                                    binfo = bmsc.group(3);
                                    String nodetype = bmsc.group(4);
                                    String pCheck = bmsc.group(5);

                                    binfo += nodetype;
                                    String checkstring = "00u" + scall + ":26 " + type + binfo;
                                    String check = q.checksum(checkstring);
                                    String outstring = "";
                                    if (check.equals(pCheck)) {
//                             System.out.println("CHECKED, type=" + type);
                                        if (type.equals("!")) {
                                            //Uncompressed APRS Beacon
                                            //E.g. <SOH>00uVK2ETA:26 !2700.00S/13300.00E.Test 1FDF9<EOT>
                                            //VK2ETA>PSKAPR,TCPIP*,qAC,T2SYDNEY:!2712.85S/15303.72E.test aprs 2
                                            outstring = scall + ">PSKAPR,TCPIP*:" + type + binfo;
//                                                System.out.println(outstring);
                                            boolean igateSendOk = igate.write(outstring);
                                            // Push this to aprs map too
                                            mapsock.sendmessage(outstring);
                                            //If I run as server, send QSL
                                            if (Main.WantServer && igateSendOk) {
                                                q.send_QSL_reply();
                                            }
                                            //record heard server stations?????
                                            if (nodetype.equals("&")) {
                                                // is serverbeacon
                                                Serverbeacon = true;
                                                int i;
                                                boolean knownserver = false;
                                                for (i = 0; i < 10; i++) {
                                                    if (scall.equals(Servers[i])) {
                                                        knownserver = true;
                                                        break;
                                                    }
                                                }

                                                if (!knownserver) {
                                                    for (i = 0; i < 10; i++) {
                                                        if (Servers[i].equals("")) {
                                                            Servers[i] = scall;
                                                            mainui.addServer(scall);
                                                            break;
                                                        }
                                                    }
                                                }

                                            }
                                        } else if (type.equals(":")) {
                                            //System.out.println(type + binfo);
                                            Pattern gc = Pattern.compile("(\\S+)>PSKAPR.::(\\S+)\\s*:(.*)(\\{\\d+)");
                                            Matcher gmc = gc.matcher(type + binfo);
                                            if (gmc.lookingAt()) {
                                                String outcall = gmc.group(2);
                                                binfo = gmc.group(3);
                                                String mnumber = gmc.group(4);
                                                outstring = scall + ">PSKAPR,TCPIP*::" + outcall;
                                                //     System.out.println("MSG:" + outstring);

                                                String padder = "        ";
                                                outstring += padder.substring(0, 8 - outcall.length());
                                                outstring += ":";
                                                outstring += binfo;
                                                outstring += mnumber;
                                                boolean igateSendOk = igate.write(outstring);
                                                // Push this to aprs map too
                                                mapsock.sendmessage(outstring);
                                                mainui.appendMainWindow(outstring);
                                            }
                                        } else {
                                            //APRS message to another callsign
                                            // message PA0R-2:26 PA0R test
                                            //System.out.println("IS :" + Blockline);
                                            Pattern gm = Pattern.compile(".*00u(\\S+):26\\s(\\S+)\\s(.*)([0123456789ABCDEF]{4}).*");
                                            Matcher gmm = gm.matcher(Blockline);
                                            if (gmm.lookingAt()) {
                                                //                                   System.out.println("FOUND:" +  Blockline);
                                                String fromcall = gmm.group(1);// + "         ";
                                                //fromcall = fromcall.substring(0, 9);
                                                String outcall = gmm.group(2) + "         ";
                                                outcall = outcall.substring(0, 9);
                                                binfo = gmm.group(3);
                      
                                                String toxastir = gmm.group(2) + ">PSKAPR,TCPIP*,qAC," + gmm.group(1) + "::" + fromcall + "  " + ":" + gmm.group(3) + "\n";
                                                mapsock.sendmessage(toxastir);
                                                //test: VK2ETA>PSKAPR,TCPIP*::vk2eta-1 :test aprs 1
                                               //VK2ZZZ>APWW11,TCPIP*,qAC,T2LUBLIN::VK2XXX-8 :Hello Jack Long time no see!{21}
                                                String aprsmessage = fromcall + ">PSKAPR,TCPIP*::" + outcall + ":" + binfo;
                                                boolean igateSendOk = igate.write(aprsmessage);
                                                //System.out.println(aprsmessage);
                                                //If I run as server, send QSL
                                                if (Main.WantServer && igateSendOk) {
                                                    q.send_QSL_reply();
                                                }
                                            }
                                        }
                                        outstring = "";
                                    }

                                }
                            } else if (!Connected & Blockline.contains(":25 ")) {
                                // System.out.println(Blockline);
                                //Unproto email message
                                Pattern bsc = Pattern.compile(".*00u(\\S+):25(\\s+)([\\w.-]+@\\w+\\.[\\w.-]+)(\\s+)(.+)\\n([0123456789ABCDEF]{4}).*");
                                Matcher bmsc = bsc.matcher(Blockline); //
                                String scall = "";
                                String spaces1 = "";
                                String email = "";
                                String spaces2 = "";
                                String body = "";
                                if (bmsc.lookingAt()) {
                                    scall = bmsc.group(1);
                                    spaces1 = bmsc.group(2);
                                    email = bmsc.group(3);
                                    spaces2 = bmsc.group(4);
                                    body = bmsc.group(5);
                                    String pCheck = bmsc.group(6);
                                    String checkstring = "00u" + scall + ":25" + spaces1 + email + spaces2 + body + "\n";
                                    String check = q.checksum(checkstring);
                                    //Only authorized if the server is left open (without a password)
                                    // Otherwise use the RadioMsg app to send short messages
                                    if (Main.WantServer && Main.accessPassword.length() == 0 && check.equals(pCheck)) {
                                        String subject = "Short email from " + scall;
                                        String resultStr = serverMail.sendMail(scall, email, subject, body, ""); //last param is attachementFileName
                                        //If I run as server, send QSL
                                        if (resultStr.contains("Message sent")) {
                                            q.send_QSL_reply();
                                        }
                                    }
                                }
                            } else if (!Connected & Blockline.contains(":6 ")) {
                                //Compressed beacon
                                Pattern cbsc = Pattern.compile(".*00u(\\S+):6\\s(.*)([0123456789ABCDEF]{4}).*");
                                Matcher cbmsc = cbsc.matcher(Blockline);
                                String scall = "";
                                String binfo = "";
                                if (cbmsc.lookingAt()) {
                                    scall = cbmsc.group(1);
                                    binfo = cbmsc.group(2);
                                    String pCheck = cbmsc.group(3);
                                    String checkstring = "00u" + scall + ":6 " + binfo;
                                    String check = q.checksum(checkstring);

                                    if (check.equals(pCheck)) {
                                        byte[] cmps = binfo.substring(0, 11).getBytes();
                                        int flg = cmps[0] - 32;
                                        int latdegrees = cmps[1] - 32;
                                        String s_latdegrees = String.format("%02d", latdegrees);
                                        int latminutes = cmps[2] - 32;
                                        String s_latminutes = String.format("%02d", latminutes);
                                        int latrest = cmps[3] - 32;
                                        String s_latrest = String.format("%02d", latrest);
                                        int londegrees = cmps[4] - 32;
                                        String s_londegrees = String.format("%03d", londegrees);
                                        int lonminutes = cmps[5] - 32;
                                        String s_lonminutes = String.format("%02d", lonminutes);
                                        int lonrest = cmps[6] - 32;
                                        String s_lonrest = String.format("%02d", lonrest);
                                        int course = cmps[7] - 32;
                                        String s_course = String.format("%03d", course);
                                        int speed = cmps[8] - 32;
                                        String s_speed = String.format("%03d", speed);
                                        char c = (char) cmps[9];
                                        String symbol = Character.toString(c);
                                        int statusinx = cmps[10] - 32;
                                        String statusmessage = binfo.substring(11);
                                        if (statusinx <= igate.maxstatus) {
                                            statusmessage = igate.status[statusinx] + statusmessage;
                                        }
                                        String latstr = "S";
                                        String lonstr = "W";

                                        int x = flg & 32;
                                        if (x == 32) {
                                            course += 180;
                                        }
                                        x = flg & 16;
                                        if (x == 16) {
                                            speed += 90;
                                        }
                                        x = flg & 8;
                                        if (x == 8) {
                                            latstr = "N";
                                        }
                                        x = flg & 4;
                                        if (x == 4) {
                                            lonstr = "E";
                                        }
                                        x = flg & 2;
                                        if (x == 2) {
                                            londegrees += 90;
                                            s_londegrees = String.format("%03d", londegrees);
                                        }
                                        String linfo = "!";
                                        linfo += s_latdegrees;
                                        linfo += s_latminutes;
                                        linfo += ".";
                                        linfo += s_latrest;
                                        linfo += latstr;
                                        linfo += "/";
                                        linfo += s_londegrees;
                                        linfo += s_lonminutes;
                                        linfo += ".";
                                        linfo += s_lonrest;
                                        linfo += lonstr;
                                        linfo += symbol;
                                        linfo += s_course;
                                        linfo += "/";
                                        linfo += s_speed;
                                        linfo += "/";
                                        linfo += statusmessage;

                                        String outstring = scall + ">PSKAPR,TCPIP*:" + linfo;

//                                            System.out.println(outstring);
                                        boolean igateSendOk = igate.write(outstring);
                                        // Push this to aprs map too
                                        mapsock.sendmessage(outstring);
                                        outstring = "";
                                        //If I run as server, send QSL
                                        if (Main.WantServer && igateSendOk) {
                                            q.send_QSL_reply();
                                        }
                                    }
                                }
                            } else if (!Connected & WantServer & Blockline.contains(":7 ")) {
                                //Ping request
                                //<SOH>00uVK2ETA:7 1830<EOT>
                                Pattern cbsc = Pattern.compile(".*00u(\\S+):7\\s([0123456789ABCDEF]{4}).*");
                                Matcher cbmsc = cbsc.matcher(Blockline);
                                String scall = "";
                                if (cbmsc.lookingAt()) {
                                    scall = cbmsc.group(1);
                                    if (scall.length() > 1) {
                                        //Some callsign present, reply with s/n
                                        String uiMsg = "Ping request from " + scall;
                                        q.Message(uiMsg, 10);
                                        try {
                                            Thread.sleep(10);
                                        } catch (Exception e) {
                                            //Nothing
                                        }
                                        q.set_txstatus(txstatus.TXPingReply);
                                        q.send_ping_reply();
                                    }
                                }
                            } else if (!Connected & WantServer & Blockline.contains(":8 ")) {
                                //Inquire request
                                //<SOH>00uVK2ETA:8 VK2ETA-5 B848<EOT>
                                Pattern cbsc = Pattern.compile(".*00u(\\S+):8\\s(\\S+)\\s([0123456789ABCDEF]{4}).*");
                                Matcher cbmsc = cbsc.matcher(Blockline);
                                String scall;
                                String reqcall;
                                if (cbmsc.lookingAt()) {
                                    reqcall = cbmsc.group(1);
                                    scall = cbmsc.group(2);
                                    String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
                                    if (scall.length() > 1 && reqcall.length() > 1 && scall.equals(serverCall)) {
                                        //Some callsigns present and match my call as sever, reply with s/n
                                        String uiMsg = "Inquire request from " + reqcall;
                                        q.Message(uiMsg, 5);
                                        q.set_txstatus(txstatus.TXInqReply);
                                        q.setReqCallsign(reqcall);
                                        q.send_inquire_reply();
                                    }
                                }
                            }

                            //System.out.println(Blockline);
                            // unproto packets
                            if (rxb.type.equals("u")) {
                                //Display received APRS message???? - Check the callsigns used
                                if (rxb.port.equals("26") & !Serverbeacon) {
                                    if (rxb.call.equals(configuration.getPreference("CALL")) || rxb.call.equals(configuration.getPreference("PSKAPRS"))) {
                                        //                                       q.send_txrsid_command("OFF");
                                        //                                       Thread.sleep(500);                          

                                        if (rxb.msgtext.indexOf("ack") != 0 & rxb.msgtext.indexOf(":") != 0) {
                                            MSGwindow += rxb.from + ": " + rxb.msgtext + "\n";
                                            if (!Connected) {
                                                mainwindow += rxb.from + ": " + rxb.msgtext + "\n";
                                            } else {
                                                q.Message("You received a message", 10);
                                            }
                                        }
                                    }
                                } else if (rxb.port.equals("71") | rxb.port.equals("72")) {
                                    //Ping reply from server
                                    int i;
                                    boolean knownserver = false;
                                    Calendar cal = Calendar.getInstance();
                                    int Hour = cal.get(Calendar.HOUR_OF_DAY);
                                    int Minute = cal.get(Calendar.MINUTE);
                                    String formathour = "0" + Integer.toString(Hour);
                                    formathour = formathour.substring(formathour.length() - 2);
                                    String formatminute = "0" + Integer.toString(Minute);
                                    formatminute = formatminute.substring(formatminute.length() - 2);
                                    String lh = formathour + ":" + formatminute;
                                    for (i = 0; i < 10; i++) {

                                        if (rxb.server.equals(Servers[i])) {
                                            knownserver = true;
                                            SNR[i] = snr;
                                            Lastheard[i] = lh;
                                            packets_received[i]++;
                                            modes_received[i] = RxModemString;
                                            strength[i] = snr_db;
                                            break;
                                        }
                                    }
                                    if (!knownserver) {
                                        for (i = 0; i < 10; i++) {
                                            if (Servers[i].equals("")) {
                                                Pattern sw = Pattern.compile("[A-Z0-9]+\\-*\\[0-9]*");
                                                Matcher ssw = sw.matcher(rxb.server);
                                                if (ssw.lookingAt() & rxb.server.length() > 3) {
                                                    Servers[i] = rxb.server;
                                                    SNR[i] = snr;
                                                    Lastheard[i] = lh;
                                                    packets_received[i]++;
                                                    strength[i] = snr_db;
                                                    mainui.addServer(rxb.server);
                                                    break;
                                                }
                                            }
                                        }

                                    }

                                }
                                // reject
                            } else if (rxb.type.equals("r") & rxb.valid) {  // reject
                                String rejectcall = "";
                                String rejectreason = "";
                                Pattern pr = Pattern.compile("^(\\S+):(.*)");
                                Matcher mr = pr.matcher(rxb.payload);
                                if (mr.lookingAt()) {
                                    rejectcall = mr.group(1);
                                    rejectreason = mr.group(2);
                                }

                                if (rejectcall.equals(mycall)) {
                                    Status = "Listening";
                                    Connected = false;
                                    mainui.disableMboxMenu();
                                    Bulletinmode = false;
                                    Connecting = false;
                                    Main.connectingPhase = false;
                                    Main.Connecting_time = 0;
                                    Scanning = false;
                                    session = "";
                                    Totalbytes = 0;
//                                        q.send_rsid_command("OFF");
                                    q.Message("Rejected:" + rejectreason, 10);
                                }
                                // connect_ack
                            } else if (rxb.type.equals("k") & rxb.valid) {

                                Pattern pk = Pattern.compile("^(\\S+):\\d+\\s(\\S+):\\d+\\s(\\d)$");
                                Matcher mk = pk.matcher(rxb.payload);
                                if (mk.lookingAt()) {
                                    rxb.server = mk.group(1);
                                    rxb.call = mk.group(2);
                                    rxb.serverBlocklength = mk.group(3);
                                    String session = rxb.session;
                                    char c = session.charAt(0);
                                    if (c != 0) {
                                        int i = (int) c - 32;
                                        if (i < 64) {
                                            sessions[i] = rxb.call + "<>" + rxb.server + ": ";
                                        } else {
//                                                System.out.println("session out of range:" + i);
                                        }
                                    }
                                }
                                // are we  connected?
                                if (rxb.call.equals(rxb.mycall) & rxb.server.equals(configuration.getPreference("SERVER"))) {
                                    //txid on, rxid off. Not yet, we now wait until full connect exchange
                                    //q.send_txrsid_command("OFF");
                                    //q.send_rsid_command("ON"); 
                                    Status = "Connected";
                                    Connected = true;
                                    Connecting = false;
                                    Main.Connecting_time = 0;
                                    Scanning = false;
                                    summoning = false;
                                    Main.linked = true;
                                    Main.linkedserver = rxb.server;
                                    mainui.disableMonitor();

                                    // reset tx queue 
                                    TX_Text = "";
                                    Totalbytes = 0;
                                    sm.initSession();
                                    session = rxb.session;
                                    sm.session_id = rxb.session;
                                    sm.myserver = rxb.server;
                                    protocolstr = rxb.protocol;
                                    protocol = protocolstr.charAt(0) - 48;

                                    File outb1 = new File(Main.HomePath + Main.Dirprefix + "Outbox");
                                    int i1 = outb1.list().length;
                                    if (i1 > 0) {
                                        Main.mainwindow += "\nWaiting in outbox:" + Integer.toString(i1) + "\n";
                                    }

                                    File outb = new File(Main.Pendingdir);
                                    int i = outb.list().length;
                                    if (i > 0) {
                                        Main.mainwindow += "Incomplete downloads:" + Integer.toString(i) + "\n\n";
                                    }

                                }
                                //Status block, are we a server?
                            } else if (!Connected & (rxb.type.equals("s"))
                                    & rxb.valid & rxb.session.equals(session)) {

                                if (Main.TTYConnected.equals("Connecting")) {
                                    Main.TTYConnected = "Connected";
                                    Main.Connected = true;
                                    status_received = true;
                                    NumberOfAcks = maxNumberOfAcks;
                                    sm.initSession();
                                    String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
                                    //Main.TX_Text = "\nHi, this is the PSKmail Server of " + serverCall + "\nVersion is " + application + "\n\n";
                                    Main.TX_Text = serverCall + " V" + version + ", Hi\n";
                                    Main.TX_Text += serverMail.getPendingList(serverCall, TTYCaller);
                                    Main.TX_Text += Motd + "\n";
                                    //We are now fully connected, stop TxIDs
                                    //q.send_txrsid_command("OFF");
                                    myrxstatus = sm.getTXStatus();
                                    q.send_status(myrxstatus);  // send our status
                                }
                                // we got an abort, clean up...
                            } else if (TTYConnected.equals("Connected")
                                    & rxb.session.equals(session) & rxb.type.equals("a") | disconnect) {
                                q.send_disconnect();
                                disconnect = false;
                                Status = "Listening";
                                Connected = false;
                                TTYConnected = "";
                                session = "";
                                TX_Text = "";
                                Totalbytes = 0;
                                sm.initSession();
                                int i;
                                for (i = 0; i < 64; i++) {
                                    Session.txbuffer[i] = "";
                                }
                                isDisconnected = true;
                                sm.FileDownload = false;
                                try {
                                    if (sm.pFile != null) {
                                        sm.pFile.close();
                                    }
                                } catch (IOException e) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                                }
                                //Set RXid ON for next connect request
                                q.send_txrsid_command("ON");
                                q.send_rsid_command("ON");
                                // send disconnect packet to caller...
                                //VK2ETA moved up to be first in sequence
                                //q.send_disconnect();
                                Main.RxDelay = Main.initialRxDelay;
                                //TTY connect request from other client (I become a TTY server)
                                //} else if (rxb.valid & rxb.type.equals("c")) { //now with access password
                            } else if (rxb.type.equals("c")) {
                                //Connect request
                                //Pattern cmsg = Pattern.compile("<SOH>.0c(\\S+):1024\\s(\\S+):24\\s(\\d).*");
                                Pattern cmsg = Pattern.compile("<SOH>.0c(\\S+):1024\\s(\\S+):24\\s(.*)[0-9A-F]{4}<EOT>.*");
                                Matcher getcl = cmsg.matcher(Blockline);
                                if (getcl.lookingAt()) {
                                    //No access password and standard CRC
                                    if (getcl.group(2).equals(q.callsignAsServer)) {
                                        //Pass or need access password
                                        String newCaller = getcl.group(1);
                                        if (TTYConnected.equals("Connected") && !newCaller.equals(TTYCaller)) {
                                            //I am already in a session and this request is not from the same client, ignore
                                            q.Message("Con. request from " + newCaller + ". Ignored...", 5);
                                        } else {
                                            //I am not in a session, or the current client is connecting again, try to accept connection connect
                                            TTYCaller = newCaller; 
                                            if (rxb.valid && Main.accessPassword.length() == 0
                                                    || rxb.validWithPW && Main.accessPassword.length() > 0) {
                                                //Clean any previous session data
                                                //
                                                disconnect = false;
                                                Status = "Listening";
                                                Connected = false;
                                                TTYConnected = "";
                                                //Reset RxDelay too
                                                RxDelay = initialRxDelay;
                                                //
                                                session = "";
                                                TX_Text = "";
                                                Totalbytes = 0;
                                                sm.initSession();
                                                for (int i = 0; i < 64; i++) {
                                                    Session.txbuffer[i] = "";
                                                }
                                                //isDisconnected = true;
                                                sm.FileDownload = false;
                                                try {
                                                    if (sm.pFile != null) {
                                                        sm.pFile.close();
                                                    }
                                                } catch (IOException e) {
                                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                                                }
                                                // send TTY acknowledge
                                                String tmp = "Connect request from " + TTYCaller;
                                                //Set TX mode to mode requested by client
                                                TTYmodes = getcl.group(3);
                                                //Old protocol, simulate symetric modes
                                                if (TTYmodes == null) {
                                                    TTYmodes = "0";
                                                }
                                                String myTxmodem = TTYmodes.substring(0, 1);
                                                if (myTxmodem.equals("0")) {
                                                    Main.TxModem = Main.RxModem;
                                                } else if (TTYmodes.length() > 1) {
                                                    TTYmodes = TTYmodes.substring(1);
                                                    Main.TxModem = getmodem(myTxmodem);
                                                }
                                                LastSessionExchangeTime = System.currentTimeMillis() / 1000; //Set initial value of session timeout
                                                q.send_txrsid_command("ON");
                                                q.send_rsid_command("ON");
                                                q.Message(tmp, 10);
                                                q.send_ack(TTYCaller);
                                                status_received = false;
                                                TimeoutPolls = 0;
                                                if (Blockline.length() > 8) {
                                                    charval = (int) (blockval / (Blockline.length() - 4)); // msec
                                                    blocktime = m.getBlockTime(Main.RxModem); //Use pre-calculated value
                                                    //blocktime = (charval * 64 / 1000) + 4;
                                                }
                                                log("Connect request from " + TTYCaller);
                                            } else {
                                                //Send a reject block with a reason
                                                q.send_txrsid_command("ON");
                                                q.send_rsid_command("ON");
                                                log("Connect attempted with damaged transmission or wrong/missing password");
                                                q.send_reject(TTYCaller, "Damaged transmission or access password incorrect\n");
                                            }
                                        }
                                    }
                                }
                            } else if (rxb.radioMsgBlock) {//process RadioMsg message
                                if (WantRelayOverRadio | WantRelaySMSs | WantRelayEmails) {
                                    radioMsgWorking = true;//Use either last RSID modem used if any or the default mode
                                    RMsgProcessor.processBlock(Blockline, RMsgProcessor.FileNameString,
                                            Main.lastRsidReceived.length() > 0 ? Main.lastRsidReceived : Main.RxModemString);
                                    Main.lastRsidReceived = ""; //Reset for next RSID.
                                }
                            }

                            if (debug) {
                                System.out.println(rxb.server);
                                System.out.println(rxb.test);
                                System.out.println(rxb.protocol);
                                System.out.println(rxb.session);
                                System.out.println(rxb.type);
                                System.out.println(rxb.crc);
                                System.out.println(rxb.port);

                                if (rxb.valid == true) {
//                                System.out.println("valid");
                                }
                            }
                            //End of //if  NO bulletin mode & NO IAC mode
                        } else if (Main.Bulletinmode) {
                            // Bulletin mode
                            Blockline = Blockline.substring(5);
                            if (Blockline.length() > 9) {
                                Blockline = Blockline.substring(0, Blockline.length() - 9);
                            }
                            Pattern pb = Pattern.compile("NNNN");
                            Matcher mb = pb.matcher(Blockline);
                            if (mb.find()) {
                                Blockline = "\n----------\n";
                                bulletin.write(Blockline);
                                Main.Bulletinmode = false;
                                Main.Status = "Listening";
                            }

                            mainwindow += Blockline;
                            Bulletin_time = 30;

                            // write to bulletins file...
                            bulletin.write(Blockline);
                            bulletin.flush();

                        } else if (Main.IACmode) {
                            sm.parseInput(Blockline);
                        }
                    } else { // if NO (m.checkBlock())
                        // no block coming...and we are server (or received a connect request)
                        if (!Main.TXActive & (TTYConnected.equals("Connected")
                                | TTYConnected.equals("Connecting"))) {  //Allow timeouts when in connecting phase as well
                            //long now = System.currentTimeMillis();
                            Systime = System.currentTimeMillis();
                            idlesecs = (int)((Systime - oldtime) / 1000);
                            //Overall session idle timeout
                            String IdleTimeStr = configuration.getPreference("IDLETIME", "120");
                            int MaxSessionIdleTime = Integer.parseInt(IdleTimeStr); //In seconds
                            if (MaxSessionIdleTime > 0 & MaxSessionIdleTime < 60) {
                                MaxSessionIdleTime = 60; //Minimum 1 minute
                            }
                            if (MaxSessionIdleTime > 0) {  //Zero mean never disconnect, use with care
                                long SessionIdleSec = (Systime / 1000) - LastSessionExchangeTime;
                                if (SessionIdleSec > MaxSessionIdleTime) {
                                    //Disconnect session
                                    disconnect = false;
                                    Status = "Listening";
                                    Connected = false;
                                    mainui.disableMboxMenu();
                                    TTYConnected = "";
                                    session = "";
                                    TX_Text = "";
                                    Totalbytes = 0;
                                    int i;
                                    for (i = 0; i < 64; i++) {
                                        Session.txbuffer[i] = "";
                                    }
                                    isDisconnected = true;
                                    //Set RXid ON for next connect request
                                    //q.send_txrsid_command("OFF");
                                    q.send_rsid_command("ON");
                                    // send disconnect packet to caller...
                                    q.send_disconnect();
                                }
                            }
                            //Check timeouts on status replies
                            //If we have seen an <SOH> wait for full block, otherwise assume we didn't hear anything
                            //if (((Main.BlockActive | TTYConnected.equals("Connecting")) & (idlesecs > (blocktime * 3) + 8))
                            //        | (!Main.BlockActive & (idlesecs > (blocktime * 0.8) + 8))) {

                            /*
                            //Split the below logical test by taking a snapshot as the modem thread changes variables on us
                            if ((m.BlockActive  && !m.receivingStatusBlock && (idlesecs > (blocktime * 2.2 + m.firstCharDelay)))) {
                                //Normal data block reception
                                System.out.println("Normal block t/o");
                            }
                            if(m.BlockActive && m.receivingStatusBlock && (idlesecs > (blocktime * 0.3 + m.firstCharDelay))) {
                                boolean ba = m.BlockActive;
                                boolean rsb = m.receivingStatusBlock;
                                int is = idlesecs;
                                int bt = blocktime;
                                //Status block reception
                                System.out.println("Status block T/O");
                            }
                            if (!m.BlockActive && (idlesecs > blocktime * 0.5 + m.firstCharDelay)) {
                                // No data block received
                                System.out.println("test 3");
                            }
                             */
                            if ((m.BlockActive && !m.receivingStatusBlock && (idlesecs > (blocktime * 2.2 + m.firstCharDelay + RxDelay))) //Normal data block reception
  //DEBUG 
                                    | (m.BlockActive && m.receivingStatusBlock && (idlesecs > (blocktime * (0.3) + m.firstCharDelay + RxDelay))) //Data block is status block
  //DEBUG                           
                                    | (!m.BlockActive && (idlesecs > blocktime * (0.5) + m.firstCharDelay + RxDelay)) // No data block received
                                    ) {
                                oldtime = Systime;
                                Main.TimeoutPolls += 1;
                                // Check if we need to downgrade modes
                                if (Main.TimeoutPolls > 1) {
                                    DowngradeOneMode();
                                    TimeoutPolls = 0;
                                }
                                if (TTYConnected.equals("Connecting") & !status_received & NumberOfAcks > 0) {
                                    // repeat sending ack...
                                    //Turn RXid and TXid ON as I am repeating a connect ack
                                    q.send_rsid_command("ON");
                                    q.send_txrsid_command("ON");
                                    q.send_ack(TTYCaller);
                                    status_received = false;
                                    idlesecs = 0;
                                    NumberOfAcks--;
                                    //Add 3 seconds to the rxDelay in case the client is slow to respond or has a long Tx delay (i.e. through a repeater)
                                    Main.RxDelay += 3;
                                    if (RxDelay > 9) RxDelay = 9; //Max delay
                                } else if (TTYConnected.equals("Connecting") & !status_received) {
                                    //Abandon connect trial
                                    Status = "Listening";
                                    Connected = false;
                                    mainui.disableMboxMenu();
                                    TTYConnected = "";
                                    session = "";
                                    TX_Text = "";
                                    Totalbytes = 0;
                                    int i;
                                    for (i = 0; i < 64; i++) {
                                        Session.txbuffer[i] = "";
                                    }
                                    isDisconnected = true;
                                    Main.RxDelay = Main.initialRxDelay;
                                    //Set RXid ON for next connect request
                                    //q.send_txrsid_command("OFF");
                                    q.send_rsid_command("ON");
                                } else if (TTYConnected.equals("Connected")) {
                                    // We are in a session, send a poll
                                    //Turn RXid and TXid ON as I am repeating a status request
                                    q.send_rsid_command("ON");
                                    q.send_txrsid_command("ON");
                                    myrxstatus = sm.getTXStatus();
                                    q.send_status(myrxstatus);  // send our status
                                    idlesecs = 0;
                                }
                            }
                        }
                    }
                    Thread.sleep(50);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } // end while

        } finally {
            try {
                if (!(bulletin == null)) {
                    bulletin.close();
                }
            } catch (IOException ex) {
                log.writelog("IO Exception when closing bulletins!", ex, true);
            }

        }
    } // end Main

    /**
     * Add a server to the array of known servers, for instance as written by
     * the user
     *
     * @param MyServer
     */
    public static void AddServerToArray(String myServer) {
        try {
            int i;
            boolean knownserver = false;
            for (i = 0; i < 10; i++) {
                if (myServer.equals(Servers[i])) {
                    knownserver = true;
                    break;
                }
            }

            if (!knownserver) {
                for (i = 0; i < 10; i++) {
                    if (Servers[i].equals("")) {
                        Servers[i] = myServer;
                        mainui.addServer(myServer);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.writelog("Had problem adding server to array, full?", e, true);
        }
    }

    /**
     * Create or check the necessary folder structure (.pskmail)
     */
    private static void handlefolderstructure() {

        // are we on Linux?
        try {
            HomePath = System.getProperty("user.home");
            if (File.separator.equals("/")) {
                Dirprefix = "/.pskmail/";
                Separator = "/";
                onWindows = false;
            } else {
                Dirprefix = "\\pskmail\\";
                Separator = "\\";
                onWindows = true;
            }

            // Where is this jar installed? Needs updating for windows?
            String path = mainpskmailui.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(0, path.lastIndexOf("/") + 1);
            Main.InstallPath = path;

            //Check if pskmail directory exists, create if not
            File dir = new File(HomePath + Dirprefix);
            if (!dir.isDirectory()) {
                dir.mkdir();
            }
            //Check if Outbox directory exists, create if not
            if (File.separator.equals("/")) {
                Separator = "/";
            } else {
                Separator = "\\";
            }
            File outbox = new File(HomePath + Dirprefix + "Outbox" + Separator);
            if (!outbox.isDirectory()) {
                outbox.mkdir();
            }
            File sentbox = new File(HomePath + Dirprefix + "Sent" + Separator);
            if (!sentbox.isDirectory()) {
                sentbox.mkdir();
            }
            File pendingfl = new File(HomePath + Dirprefix + "Pending" + Separator);

            if (!pendingfl.isDirectory()) {
                pendingfl.mkdir();
            }
            File outpendingfl = new File(HomePath + Dirprefix + "Outpending" + Separator);

            if (!outpendingfl.isDirectory()) {
                outpendingfl.mkdir();
            }

            //Check if Downloads directory exists, create if not
            if (File.separator.equals("/")) {
                Separator = "/";
            } else {
                Separator = "\\";
            }
            File downloads = new File(HomePath + Dirprefix + "Downloads" + Separator);
            if (!downloads.isDirectory()) {
                downloads.mkdir();
            }

            //Check if Pending directory exists, create if not
            if (File.separator.equals("/")) {
                Separator = "/";
            } else {
                Separator = "\\";
            }
            pendingstr = HomePath + Dirprefix + "Pending" + Separator;
            pending = new File(pendingstr);
            if (!pending.isDirectory()) {
                pending.mkdir();
            }
            Pendingdir = HomePath + Dirprefix + "Pending" + Separator;
            Outpendingdir = HomePath + Dirprefix + "Outpending" + Separator;
            Transactions = HomePath + Dirprefix + "Transactions";

            // Check if bulletin file  exists, create if not
            File fFile = new File(Main.HomePath + Main.Dirprefix + "Downloads" + Separator + "bulletins");
            if (!fFile.exists()) {
                fFile.createNewFile();
            }

            bulletin = new FileWriter(fFile, true);

            // check if headers file exists, and read in contents 
            File fh = new File(HomePath + Dirprefix + "headers");
            if (!fh.exists()) {
                fh.createNewFile();
            }

            hdr = new FileReader(fh);
            BufferedReader br = new BufferedReader(hdr);
            String s;
            while ((s = br.readLine()) != null) {
                String fl = s + "\n";
                Mailheaderswindow += fl;
            }
            br.close();
            
            //Create RadioMsgInbox
            File RadioMsgInbox = new File(HomePath + Dirprefix + DirInbox + Separator);
            if (!RadioMsgInbox.isDirectory()) {
                RadioMsgInbox.mkdir();
            }
            //Create RadioMsgSentbox
            File RadioMsgSentbox = new File(HomePath + Dirprefix + DirSent + Separator);
            if (!RadioMsgSentbox.isDirectory()) {
                RadioMsgSentbox.mkdir();
            }
            //Create RadioMsgImages
            File RadioMsgImages = new File(HomePath + Dirprefix + DirImages + Separator);
            if (!RadioMsgImages.isDirectory()) {
                RadioMsgImages.mkdir();
            }
            //Create RadioMsgArchive Directory
            File RadioMsgArchive = new File(HomePath + Dirprefix + DirArchive + Separator);
            if (!RadioMsgArchive.isDirectory()) {
                RadioMsgArchive.mkdir();
            }
        } catch (Exception ex) {
            log.writelog("Problem when handling pskmail folder structure.", ex, true);
        }
    }

    private static void handleinitialization() {

        try {
            /*
                File f1 = new File(Main.HomePath + Main.Dirprefix + "configuration.xml");
                File f2 = new File("configuration.xml");

                if (f1.isFile()) {

                    InputStream in = new FileInputStream(f1);

                    //Overwrite the file.
                    OutputStream out = new FileOutputStream(f2);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    q.Message("Config File copied.", 10);
                }
             */

            // get password
            Passwrd = configuration.getPreference("PASSWORD");
            // try to initialize MAXDCD from Prefs
            DCDstr = configuration.getPreference("DCD", "3");
            MAXDCD = Integer.parseInt(DCDstr);
            // try to initialize Icon from Prefs
            Icon = configuration.getPreference("ICON", "y");
            ICONlevel = configuration.getPreference("ICON2", "/");
            // Initialize APRSMessageNumber
            APRSMessageNumber = 0;
            // Initialize send queue
            TX_Text = "";

            // init logfile
            Main.LogFile = configuration.getPreference("LOGFILE", "client.log");

            // Modem settings
            host = configuration.getPreference("MODEMIP");
            port = Integer.parseInt(configuration.getPreference("MODEMIPPORT", "7322"));
            ModemPreamble = configuration.getPreference("MODEMPREAMBLE", "0");
            ModemPostamble = configuration.getPreference("MODEMPOSTAMBLE", "0");
            // Mail settings
            if (configuration.getPreference("COMPRESSED", "yes").equals("yes")) {
                compressedmail = true;
            } else {
                compressedmail = false;
            }

            String profile = configuration.getPreference("BLOCKLENGTH", "5");
            CurrentModemProfile = profile;
            Character c = profile.charAt(0);

            // Get the default modem and the selected mode list
            // If this is not set by the client then set a decent default as
            // they will end up in THOR8 hell most often otherwise!!!
            String strunt = configuration.getPreference("DEFAULTMODE");
            if (!strunt.isEmpty()) {
                DefaultTXmodem = configuration.getPreference("DEFAULTMODE");
                Main.defaultmode = convmodem(DefaultTXmodem);
            } else {
                DefaultTXmodem = "PSK250R";
                Main.defaultmode = convmodem(DefaultTXmodem);
                configuration.setPreference("DEFAULTMODE", Main.DefaultTXmodem);
            }

            // Check if its empty, if so then set a decent default
            strunt = configuration.getPreference("MODES");
// System.out.println("Defaultmode=" + strunt);                  
            if (!strunt.isEmpty()) {
                modes = configuration.getPreference("MODES");
            } else {
                modes = "85b3"; // PSK250R, PSK250, PSK125R, THOR22
                configuration.setPreference("MODES", Main.modes);
            }

            modeprofile = new modemmodeenum[10];

            if (configuration.getPreference("GPSD", "0").equals("1")) {
                WantGpsd = true;
            }
            if (configuration.getPreference("SCANNER", "no").equals("yes")) {
                wantScanner = true;
            }
            // APRSServerSettings
            //
            aprsserverport = Integer.parseInt(configuration.getPreference("APRSSERVERPORT", "8063"));
            if (configuration.getPreference("APRSSERVER", "yes").equals("yes")) {
                aprsserverenabled = true;
            } else {
                aprsserverenabled = false;
            }

            //Server and RadioMsg globals
            if (configuration.getPreference("ENABLESERVER", "no").equals("yes")) {
                WantServer = true;
            } else {
                WantServer = false;
            }
            if (configuration.getPreference("RELAYOVERRADIO", "no").equals("yes")) {
                WantRelayOverRadio = true;
            } else {
                WantRelayOverRadio = false;
            }
            if (configuration.getPreference("RELAYEMAILS", "no").equals("yes")) {
                WantRelayEmails = true;
            } else {
                WantRelayEmails = false;
            }
            if (configuration.getPreference("RELAYEMAILSIMMEDIATELY", "no").equals("yes")) {
                WantRelayEmailsImmediat = true;
            } else {
                WantRelayEmailsImmediat = false;
            }
            if (configuration.getPreference("RELAYSMSS", "no").equals("yes")) {
                WantRelaySMSs = true;
            } else {
                WantRelaySMSs = false;
            }
            if (configuration.getPreference("RELAYSMSSIMMEDIATELY", "no").equals("yes")) {
                WantRelaySMSsImmediat = true;
            } else {
                WantRelaySMSsImmediat = false;
            }
            callsignAsServer = configuration.getPreference("CALLSIGNASSERVER", "N0CAL");
            accessPassword = Main.configuration.getPreference("ACCESSPASSWORD").trim();

        } catch (Exception e) {
            MAXDCD = 3;
//                q.backoff = "5";
            Icon = "y";
            ICONlevel = "/";
            log.writelog("Problems with config parameter.", e, true);
        }

        // Send Link request
        //           q.set_txstatus(txstatus.TXlinkreq);
        //           q.send_link();
        Servers[0] = configuration.getPreference("SERVER");
        Main.myserver = Servers[0];
        Main.mycall = configuration.getPreference("CALL");
        Freq_offset = Integer.parseInt(Main.configuration.getPreference("RIGOFFSET", "0"));

        String XMLIP = configuration.getPreference("MODEMIP", "127.0.0.1");

        if (XMLIP.equals("localhost")) {
            XMLIP = "127.0.0.1";
        }

        XmlRpc_URL = "http://" + XMLIP + ":7362/RPC2";

    }
    
    private static void handlegpsd() {
        try {
            // Connect to gpsd at port 2947 on localhost
            InetAddress addr = InetAddress.getByName("localhost");
            int target = 2947;
            SocketAddress sockaddr = new InetSocketAddress(addr, target);

            // Block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs = 2000;   // 2 seconds

            gpsdSocket = new Socket();
            gpsdSocket.connect(sockaddr, timeoutMs);
            gpsdout = new PrintWriter(gpsdSocket.getOutputStream(), true);
            gpsdin = new BufferedReader(new InputStreamReader(
                    gpsdSocket.getInputStream()));

            String outgps = "?WATCH={\"enable\":true, \"nmea\":true };";
            gpsdout.println(outgps);

            long t0 = System.currentTimeMillis();
            t1 = t0;

            boolean ready = false;

            while (t1 - t0 < 2000 & !ready) {

                t1 = System.currentTimeMillis();

                String myRead = "";

                if (gpsdin.ready()) {
                    myRead = gpsdin.readLine();

                    if (myRead.substring(0, 6).equals("$GPRMC")) {
                        HaveGPSD = true;
                        ready = true;
                    }
                }

            }

            if (!HaveGPSD) {
                q.Message("Problem with GPSD", 10);
            }

        } catch (UnknownHostException e) {
            q.Message("Cannot find GPSD", 10);
            HaveGPSD = false;
        } catch (IOException e) {
            q.Message("Cannot find gpsd", 10);
            HaveGPSD = false;
        }

        if (HaveGPSD) {
            gpsdata = new nmeaparser();     // Parser for nmea data
            q.Message("Connected to GPSD", 10);
        }

    }

    static public void getgpsddata() {
        String myRead = "";
        Boolean ready = false;

        while (HaveGPSD & !ready) {

            try {
                myRead = gpsdin.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (myRead.length() > 6) {
                if (myRead.substring(0, 6).equals("$GPRMC")) {
                    gpsd_data = myRead.split(",");
                    if (gpsd_data[1].length() > 2) {
                        gpsdata.validfix = true;
                        gpsdata.fixat = gpsd_data[1];
                    }

                    gpsdata.latitude = gpsd_data[3];
                    float latdata = Float.valueOf(gpsdata.latitude) / 100;
                    int degr = (int) latdata;
                    float mindata = (latdata - degr) / 60 * 100;
                    mindata = degr + mindata;
                    if (gpsd_data[4].equals("S")) {
                        mindata *= -1;
                    }
                    GPSD_latitude = Float.toString(mindata);

                    gpsdata.longitude = gpsd_data[5];
                    float longdata = Float.valueOf(gpsdata.longitude) / 100;
                    degr = (int) longdata;
                    mindata = (longdata - degr) / 60 * 100;
                    mindata = degr + mindata;
                    if (gpsd_data[6].equals("W")) {
                        mindata *= -1;
                    }
                    GPSD_longitude = Float.toString(mindata);

                    gpsdata.speed = gpsd_data[7];
                    gpsdata.course = gpsd_data[8];

                    char[] buffer = new char[4000];
                    try {
                        int cnt = gpsdin.read(buffer);
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                configuration.setLatitude(GPSD_latitude);
                configuration.setLongitude(GPSD_longitude);

                ready = true;
            }

        }

    }

    static public void parsenmeadata(String nmeadata) {
        gpsdata.newdata(nmeadata);
        if (gpsdata.getFixStatus()) {
            configuration.setLatitude(gpsdata.getLatitude());
            configuration.setLongitude(gpsdata.getLongitude());
            configuration.setSpeed(gpsdata.getSpeed());
            configuration.setCourse(gpsdata.getCourse());
        }
    }

    /**
     * Open a GPS connection, if that should be used
     */
    private static void handlegps() {
        // GPS
        gpsport = new serialport();       // Serial port object
        gpsdata = new nmeaparser();     // Parser for nmea data
        String portforgps = configuration.getPreference("GPSPORT");

        // Make sure the selected port still exists!
        if (configuration.getPreference("GPSENABLED").equals("yes")) {
            if (!gpsport.checkComPort(portforgps)) {
                Main.log.writelog("Serial port " + portforgps + " does not exist! Was the GPS removed? Disabling GPS.", true);
                configuration.setPreference("GPSENABLED", "no");
            }
        }

        if (configuration.getPreference("GPSENABLED", "no").equals("yes")) {
            try {
                String speedforgps = configuration.getPreference("GPSSPEED", "4800");
                int speedygps = Integer.parseInt(speedforgps);
                gpsport.connect(portforgps, speedygps);
                // Check if the port is open
                if (!gpsport.curstate) {
                    // Disconnect and set it off
                    gpsport.disconnect();
                    configuration.setPreference("GPSENABLED", "no");
                }
                /*if (portforgps.contains("USB"))
            // Here is the code for getting a gps out of sirf mode
            gpsdata.writehexsirfmsg("8102010100010101050101010001000100010001000112c0"); //Set 4800 bps nmea*/
            } catch (Exception ex) {
                log.writelog("Error when trying to connect to the GPS.", ex, true);
            }
        }
    }

    static String getTXModemString(modemmodeenum mode) {
        try {
            String Txmodemstring = "";
            Txmodemstring = m.getModemString(mode);
            return Txmodemstring;
        } catch (Exception e) {
            return "";
        }
    }

    static String getAltTXModemString(modemmodeenum mode) {
        String Txmodemstring = "";
        Txmodemstring = m.getAltModemString(mode);
        return Txmodemstring;
    }

    static void setFreq(String freq) {
        if (Rigctl.opened) {
            int fr = Integer.parseInt(Main.CurrentFreq) + Rigctl.OFF;
            freqstore = Integer.toString(fr);
            Rigctl.Setfreq(freq);
            summoning = true;
        }
    }

    static void savePreferences() {
        /*
     try {
           // store the config file if present

             File f1 = new File( "configuration.xml");
             File f2 = new File(Main.HomePath + Main.Dirprefix + "configuration.xml");

            if (f1.isFile()) {

            InputStream in = new FileInputStream(f1);

      //Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            q.Message("Config File stored.", 10);
         }
        }
        catch (Exception e) {
                 q.Message("problem writing the config file", 10);
         }

         */
    }

    static double decayaverage(double oldaverage, double newvalue, double factor) {

        double newaverage = oldaverage;
        if (factor > 1) {
            newaverage = (oldaverage * (1 - 1 / factor)) + (newvalue / factor);
        }
        return newaverage;
    }

//Downgrade RX and TX modes alternatively (until we recover the link)
    static void DowngradeOneMode() {
        int currentmodeindex = 0;

        //Alternate Rx and Tx speed downgrades
        if (Main.JustDowngradedRX) {
            JustDowngradedRX = false;
            Main.hiss2n = 50; //Reset to mid-range
            //Downgrade Tx mode
            currentmodeindex = getClientModeIndex(Main.TxModem);
            if (currentmodeindex < TTYmodes.length() - 1) { //List in decreasing order of speed
                Main.TxModem = getClientMode(currentmodeindex + 1);
                sm.SetBlocklength(5); //restart with medium block length
                q.send_txrsid_command("ON");
                String TXmd = getTXModemString(Main.TxModem);
                String SendMode = "<cmd><mode>" + TXmd + "</mode></cmd>";
                m.Sendln(SendMode);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            JustDowngradedRX = true; // Make TX mode downgrade first (if necessary)
            Main.mys2n = 50; //Reset to mid-range
            currentmodeindex = getClientModeIndex(Main.RxModem);
            if (currentmodeindex < TTYmodes.length() - 1) { //List in decreasing order of speed
                Main.RxModem = getClientMode(currentmodeindex + 1);
                Main.RxModemString = m.getModemString(Main.RxModem);
                blocktime = m.getBlockTime(Main.RxModem);
                String TXmd = getTXModemString(Main.TxModem);
                String SendMode = "<cmd><mode>" + TXmd + "</mode></cmd>";
                m.Sendln(SendMode);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static void ChangeMode(modemmodeenum Modem) {
        String SendMode = "";

        String TXmd = getTXModemString(Modem);
        String rxstring = Main.getTXModemString(Main.defaultmode);
        rxstring += "        ";
        rxstring = rxstring.substring(0, 7);
        mainui.RXlabel.setText(rxstring);
        SendMode = "<cmd><mode>" + TXmd + "</mode></cmd>";

        m.Sendln(SendMode);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Sendline.length() > 0) {
            try {
                //VK2ETA add TX delay here from config popup (allows on-the-fly changes)
                int TxDelay = 0;
                String TxDelayStr = configuration.getPreference("TXDELAY", "0");
                //System.out.println("TXDELAY:" + TxDelayStr);  
                if (TxDelayStr.length() > 0) {
                    TxDelay = Integer.parseInt(TxDelayStr);
                }

                if (TxDelay > 0) {
                    Thread.sleep(TxDelay * 1000);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            m.Sendln(Sendline);
        }
        Sendline = "";

    }

    static modemmodeenum convmodem(String mymodem) {
        modemmodeenum mode = modemmodeenum.THOR8;

        if (mymodem.equals("THOR8")) {
            mode = modemmodeenum.THOR8;
        } else if (mymodem.equals("MFSK16")) {
            mode = modemmodeenum.MFSK16;
        } else if (mymodem.equals("THOR22")) {
            mode = modemmodeenum.THOR22;
        } else if (mymodem.equals("MFSK32")) {
            mode = modemmodeenum.MFSK32;
        } else if (mymodem.equals("PSK250R")) {
            mode = modemmodeenum.PSK250R;
        } else if (mymodem.equals("PSK500R")) {
            mode = modemmodeenum.PSK500R;
        } else if (mymodem.equals("PSK1000")) {
            mode = modemmodeenum.PSK1000;
        } else if (mymodem.equals("PSK500")) {
            mode = modemmodeenum.PSK500;
        } else if (mymodem.equals("PSK250")) {
            mode = modemmodeenum.PSK250;
        } else if (mymodem.equals("PSK125")) {
            mode = modemmodeenum.PSK125;
        } else if (mymodem.equals("PSK63")) {
            mode = modemmodeenum.PSK63;
        } else if (mymodem.equals("PSK125R")) {
            mode = modemmodeenum.PSK125R;
        } else if (mymodem.equals("DOMINOEX5")) {
            mode = modemmodeenum.DOMINOEX5;
        } else if (mymodem.equals("CTSTIA")) {
            mode = modemmodeenum.CTSTIA;
        } else if (mymodem.equals("DOMINOEX22")) {
            mode = modemmodeenum.DOMINOEX22;
        } else if (mymodem.equals("DOMINOEX11")) {
            mode = modemmodeenum.DOMINOEX11;
        } else if (mymodem.equals("PSK63RC5")) {
            mode = modemmodeenum.PSK63RC5;
        } else if (mymodem.equals("PSK63RC10")) {
            mode = modemmodeenum.PSK63RC10;
        } else if (mymodem.equals("PSK250RC3")) {
            mode = modemmodeenum.PSK250RC3;
        } else if (mymodem.equals("PSK125RC4")) {
            mode = modemmodeenum.PSK125RC4;
        }

        return mode;
    }

    static modemmodeenum getmodem(String mymodem) {
        modemmodeenum mode = modemmodeenum.THOR8;

        if (mymodem.equals("1")) {
            mode = modemmodeenum.THOR8;
        } else if (mymodem.equals("2")) {
            mode = modemmodeenum.MFSK16;
        } else if (mymodem.equals("3")) {
            mode = modemmodeenum.THOR22;
        } else if (mymodem.equals("4")) {
            mode = modemmodeenum.MFSK32;
        } else if (mymodem.equals("5")) {
            mode = modemmodeenum.PSK250R;
        } else if (mymodem.equals("6")) {
            mode = modemmodeenum.PSK500R;
        } else if (mymodem.equals("g")) {
            mode = modemmodeenum.PSK1000;
        } else if (mymodem.equals("7")) {
            mode = modemmodeenum.PSK500;
        } else if (mymodem.equals("8")) {
            mode = modemmodeenum.PSK250;
        } else if (mymodem.equals("9")) {
            mode = modemmodeenum.PSK125;
        } else if (mymodem.equals("a")) {
            mode = modemmodeenum.PSK63;
        } else if (mymodem.equals("b")) {
            mode = modemmodeenum.PSK125R;
        } else if (mymodem.equals("n")) {
            mode = modemmodeenum.DOMINOEX5;
        } else if (mymodem.equals("f")) {
            mode = modemmodeenum.CTSTIA;
        } else if (mymodem.equals("h")) {
            mode = modemmodeenum.PSK63RC5;
        } else if (mymodem.equals("i")) {
            mode = modemmodeenum.PSK63RC10;
        } else if (mymodem.equals("j")) {
            mode = modemmodeenum.PSK250RC3;
        } else if (mymodem.equals("k")) {
            mode = modemmodeenum.PSK125RC4;
        } else if (mymodem.equals("l")) {
            mode = modemmodeenum.DOMINOEX22;
        } else if (mymodem.equals("m")) {
            mode = modemmodeenum.DOMINOEX11;
        }

        return mode;
    }

    //Returns the index of the parameter mode in the client supplied list of modes
    public static int getClientModeIndex(modemmodeenum mode) {
        int index = 0;

        if (TTYmodes.length() > 0) {
            for (int i = 0; i < TTYmodes.length(); i++) {
                if (Main.getmodem(TTYmodes.substring(i, i + 1)) == mode) {
                    index = i;
                }
            }
        }

        return index;
    }

    //Returns the modemmodeenum pointed to by the parameter in the client's array of selected modes
    public static modemmodeenum getClientMode(int index) {

        if (index >= 0 && index < TTYmodes.length()) {
            return Main.getmodem(TTYmodes.substring(index, index + 1));
        }
        //No match return something (MFSK16)
        return Main.getmodem("2");
    }

    static int getserverindex(String server) {
        int i = 0;
        int o = 10;
        for (i = 0; i < 10; i++) {
            if (server.equals(Servers[i])) {
                o = i;
            }
        }
        return o;
    }

    static void setrxdata(String server, int data) {
        int i = getserverindex(server);
        if (i < 9) {
            System.arraycopy(rxdata[i], 0, rxdata[i], 1, 8);
            rxdata[i][0] = data;
        }
    }

    static int getrxdata_avg(int index) {
        int i = 0;
        int acc = 0;
        int avg = 0;
        for (i = 0; i < 10; i++) {
            if (rxdata[index][i] > 0) {
                acc += rxdata[index][i];
                avg = acc / (i + 1);
            }
        }
        return avg;
    }

    static String myTime() {
        // create a java calendar instance
        Calendar calendar = Calendar.getInstance();

        // get a java.util.Date from the calendar instance.
        // this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // a java current time (now) instance
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        return currentTimestamp.toString().substring(0, 16);
    }

    static void shown(String st) {
        System.out.println(st);
    }

    static void show(String st) {
        System.out.print(st);
    }

    static void log(String logtext) {

        try {
            // Create file
            FileWriter logstream = new FileWriter(HomePath + Dirprefix + LogFile, true);
            BufferedWriter out = new BufferedWriter(logstream);

            out.write(myTime() + " " + logtext + "\n");
            //Close the output stream
            out.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("LogError: " + e.getMessage());
        }
        Main.mainwindow += (myTime() + " " + logtext + "\n");
    }

} // end Main class

