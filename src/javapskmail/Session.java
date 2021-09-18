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
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author rein
 */
public class Session {

    public String mycall;
    public String myserver;
    private static String blocklength;
    private static int Blocklength;
    public String session_id;
    private String Lineoutstring;

    // service flags
    public boolean Headers = false;
    public boolean FileDownload = false;
    public boolean WWWDownload = false;
    public boolean CwwwDownload = false;
    public boolean FileList = false;
    public String ThisFile = "";
    public File Trfile = null;
    public String Transaction = "";
    public String ThisFileLength = "";
    public boolean MsgDownload = false;
    public boolean CMsgDownload = false;
    public boolean base64attachment = false;
    public boolean plaintextattachment = false;
    public String attachmentFilename = null;
    //Server service flags
    public boolean emailUpload = false;
    public boolean CompressedEmailUpload = false;

    //rx is my machine, tx is the other machine
    public static String tx_lastsent;    //Last block sent to me
    public static String tx_missing; //List of repeats  I need to resend.
    public static String tx_ok;  //last block received conseq ok at other end.
    public static String tx_lastreceived;  // at other end

    private static String rx_lastsent;    // Last block I sent
    private static String rx_ok;   //  Text o.k  until this one
    private static String rx_lastreceived;   // Last block I received
    public static String rx_missing; //List of repeat requests I need to send to other side
    private static boolean rx_lastBlock; // Flag for end of frame

    private static String[] rxbuffer = new String[64];
    //private static int beginblock;
    private static int goodblock;
    private static int thisblock;
    private static int lastblock;
    public static int lastdisplayed = 0;
    private int lastgoodblock_received;
    public static boolean gooddata;
    public String serverversion;
    public String hispubkey = "";

    public static String[] txbuffer = new String[64];
    private static int lastqueued = 0;

    // progress bar values
    public static int DataReceived = 0;
    public static int DataSize = 0;

    private FileWriter headers = null;
    public FileWriter dlFile = null;
    public FileWriter pFile = null;
    private FileWriter tmpmessage = null;
    private FileWriter inbox = null;
    private BufferedWriter iacout;
    private fastfec f;
    //Server files
    private FileWriter tempEmailFile = null;

    //  private  String lastqueued;  //Last block in my send queue
    public Session() {
        String path = Main.HomePath + Main.Dirprefix;
        config cf = new config(path);
        f = new fastfec();
        myserver = cf.getServer();
        blocklength = "6";
        try {
            blocklength = cf.getBlocklength();
        } catch (Exception e) {
            blocklength = "6";
        }
//             Blocklength = Integer.parseInt(blocklength);
        Blocklength = 6;
        Main.TX_Text = "";

        initSession();
    }

    public void sendUpdate() {
        String output;
        String record;
        String rec1 = "";
        String pophost = Main.configuration.getPreference("POPHOST");
        String popuser = Main.configuration.getPreference("POPUSER");
        String poppass = Main.configuration.getPreference("POPPASS");
        String returnaddr = Main.configuration.getPreference("RETURNADDRESS");
        String recinfo = pophost + "," + popuser + "," + poppass
                + "," + returnaddr;

        // Server version 1.5+ understands encrypted data
        if (Main.sversion > 1.5) {

            String rec64 = base_64.base64Encode(recinfo);
            output = Main.cr.encrypt(Main.sm.hispubkey, rec64);
            record = "~RECx" + output + "\n";
            while (record.length() > 30) {
                Main.TX_Text += record.substring(0, 30) + "\n";
                record = record.substring(30);
            }
            Main.TX_Text += record + "Q\n";
        } else {
            record = "~RECx" + base_64.base64Encode(recinfo);
            int eol_loc = -1;
            String frst = null;
            String secnd = null;
            eol_loc = record.indexOf(10);
            if (eol_loc != -1) {
                frst = record.substring(0, eol_loc - 1);
                secnd = record.substring(eol_loc + 1, record.length());
                record = frst + secnd;
                Main.TX_Text += record + "\n";
            } else // So we have an old version but could not find user settings, warn about that!
            {
                Message("Missing user record to send!", 5);
            }
        }
    }

    public void Message(String msg, int time) {
        Main.Statusline = msg;
        Main.StatusLineTimer = time;
    }

    public void RXStatus(String text) {
        if (text.length() > 2) {
            tx_lastsent = text.substring(0, 1);
            tx_lastreceived = text.substring(1, 2);
            tx_ok = text.substring(2, 3);
            tx_missing = text.substring(3);
            if (tx_missing.length() > 0 & Main.Connected) {
                Main.txbusy = true;
            } else {
                Main.txbusy = false;
            }
        }
    }

    @SuppressWarnings("empty-statement")
    public String getTXStatus() {
        rx_missing = "";
        int endblock;
        lastblock = (tx_lastsent.charAt(0) - 32);

        endblock = (lastgoodblock_received + 64) % 64;;

        int i = 0;
        int index = 0;

        int runvar = 0;
        if (lastblock < lastdisplayed) {
            runvar = lastblock + 64;
        } else {
            runvar = lastblock;
        }

        for (i = lastdisplayed + 1; i <= runvar; i++) {
            index = i % 64;

            if (rxbuffer[index].equals("")) {;

                char m = (char) (index + 32);
                rx_missing += Character.toString(m);
            }
        }
        // generate the status block      

        if (rx_missing.length() == 0) {
            Main.rxbusy = false;
        } else {
            Main.rxbusy = true;
        }

        goodblock = lastdisplayed;
        char last = (char) (lastgoodblock_received + 32);
        rx_lastreceived = Character.toString(last);
        char ok = (char) (goodblock + 32);
        rx_ok = Character.toString(ok);

        int Missedblocks = rx_missing.length();
        Main.Missedblocks = Missedblocks;

        if (Missedblocks > 8) {
            rx_missing = rx_missing.substring(0, 8);
        }
        String outstr = rx_lastsent + rx_ok + rx_lastreceived + rx_missing;
        return outstr;
    }

    public String getRXmissing() {
        rx_missing = "";
        int i = 0;
        int end = thisblock;
        if (thisblock <= lastdisplayed & lastdisplayed - thisblock > 49) {
            end = (thisblock + 64) % 64;
        }

        for (i = lastdisplayed + 1; i < end; i++) {
            char m = (char) ((i % 64) + 32);
            rx_missing += Character.toString(m);
        }

        rx_missing = rx_missing.substring(0, 8);

        return rx_missing;
    }

    //Used in Main loop for detecting idle TTY sessions by the TTY server
    public String getBlockExchanges() {
        //rx is my machine, tx is the other machine
        return (tx_lastsent + rx_lastsent);    //Last block sent to me  + Last block I sent
    }

    //Used in Main loop for measuring the link quality (from a data perspective)
    //public double RXGoodBlocksRatio() {
    //rx is my machine, tx is the other machine
    //  return(1);
    // }
    public void initSession() {
        tx_lastsent = " ";
        tx_lastreceived = " ";
        tx_ok = " ";
        tx_missing = "";
        rx_lastsent = " ";
        rx_ok = " ";
        rx_lastreceived = " ";
        rx_missing = "";
        rx_lastBlock = false;
        //beginblock = 0;
        goodblock = 0;
        thisblock = 0;
        lastblock = 0;
        lastqueued = 1; // to make sure the first command is transmitted...
        lastdisplayed = 0;
        gooddata = true;
        lastgoodblock_received = 0;
        serverversion = "1.0";
        hispubkey = "";

        for (int i = 0; i < 64; i++) {
            rxbuffer[i] = "";
            txbuffer[i] = "";
        }
        Lineoutstring = " ";
        Main.rxbusy = false;
        Main.txbusy = false;

        // compile the matchers
    }

    // handles the rx buffer and calculates the new TXStatus after every Block
    public String doRXBuffer(String block, String index) throws FileNotFoundException, IOException, Exception {

        if (block.length() > 0) { //valid block
            thisblock = index.charAt(0) - 32;
            if (thisblock < 64) {
                rxbuffer[thisblock] = block;
            }

            if (lastdisplayed > 63) {
                lastdisplayed -= 64;
            }

            if (thisblock > lastgoodblock_received | (lastgoodblock_received - thisblock) > (64 - 24)) {
                lastgoodblock_received = thisblock;
            }

            while (!rxbuffer[(lastdisplayed + 1) % 64].equals("")) {
                // display this block

                lastdisplayed++;
                lastdisplayed %= 64;

                // set goodblock
                goodblock = lastdisplayed;

                if ((lastdisplayed > lastgoodblock_received) | (lastgoodblock_received - lastdisplayed) > (64 - 17)
                        | (lastdisplayed == 0 & thisblock == 0)) {

                    lastgoodblock_received = thisblock;
                }
                // output to main window
                int maxwait = 0;
                while (Main.mainmutex) {
                    try {
                        Thread.sleep(5);
                        maxwait++;
                        if (maxwait > 100) {
                            break;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (!rxbuffer[lastdisplayed].startsWith("~FA:")) {
                    Main.mainwindow += rxbuffer[lastdisplayed];
                }

                // parse commands
                Lineoutstring += rxbuffer[lastdisplayed];

                int Linebreak = -1;
                while (Lineoutstring.indexOf("\n") >= 0) {
                    Linebreak = Lineoutstring.indexOf("\n");
                    if (Linebreak >= 0) {
                        String fullLine = Lineoutstring.substring(0, Linebreak);
                        Lineoutstring = Lineoutstring.substring(Linebreak + 1);
                        parseInput(fullLine);
                    }
                }
            }

            // make room for more data
            if (lastdisplayed == lastgoodblock_received) {
                int i = 0;
                for (i = lastdisplayed + 8; i < lastdisplayed + 32; i++) {
                    rxbuffer[i % 64] = "";
                }
            }

        }

        return "";
    }

    public void parseInput(String str) throws NoClassDefFoundError, FileNotFoundException, IOException, Exception {
        boolean Firstline = false;
        boolean foundMatchingCommand = false;

        // ~STOP: <transaction> for TTY session...
        //"~STOP:" + Main.sm.Transaction + "\n"
        Pattern STOPm = Pattern.compile("^\\s*~STOP:([A-Za-z0-9]+)?");
        Matcher stopm = STOPm.matcher(str);
        if (Main.TTYConnected.equals("Connected") & stopm.lookingAt()) {
            foundMatchingCommand = true;
            String transaction = stopm.group(1);
            if (transaction != null && !transaction.equals("")) {
                Main.log("Stopping Transaction: " + transaction);
                //Delete specified transaction (the one in progress)
                //Look in the Outbox for partial upload to client. Files are in the format VK2ETA_-w-_12345
                //Since we don't know the file type from the ~FY command (s,w,f etc...),
                //  we scan the directory for a match
                String caller = Main.TTYCaller;
                File[] filesOutbox;
                // Get the list of files in the designated folder
                File dir = new File(Main.HomePath + Main.Dirprefix + "Outbox");
                //filesOutbox = dir.listFiles();
                FileFilter fileFilter = new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                };
                //Generates an array of strings containing the file names to resume downloading
                filesOutbox = dir.listFiles(fileFilter);
                for (int i = 0; i < filesOutbox.length; i++) {
                    String pendingCaller = "";
                    String pendingType = "";
                    String pendingToken = "";
                    String pendingFn = filesOutbox[i].getName();
                    if (pendingFn.contains("_-")) {
                        int firstSep = pendingFn.indexOf("_-");
                        int secondSep = pendingFn.indexOf("-_");
                        if (firstSep > 0 && secondSep > 0) {
                            pendingCaller = pendingFn.substring(0, firstSep);
                            pendingType = pendingFn.substring(firstSep + 2, secondSep);
                            pendingToken = pendingFn.substring(secondSep + 2);
                        }
                        if (pendingCaller.equals(caller) && transaction.equals(pendingToken)) {
                            //Found a match for callsign and token combination, delete file
                            File penfOut = filesOutbox[i].getAbsoluteFile();
                            if (penfOut.exists()) {
                                penfOut.delete();
                            }
                        }
                    }
                }
            }
            //Clear everything in the current session (as if we had just concluded the connection)
            //tx_lastsent = " ";
            tx_lastreceived = tx_lastsent;
            //tx_ok = " ";
            tx_missing = "";
            //rx_lastsent = " ";
            rx_ok = " ";
            rx_lastreceived = rx_lastsent;
            rx_missing = "";
            Blocklength = 6;
            Main.TX_Text = "";
            //VK2ETA should we clear the buffers too?
            for (int i = 0; i < 64; i++) {
                rxbuffer[i] = "";
                txbuffer[i] = "";
            }
        }
        // ~QUIT for TTY session...
        Pattern TTYm = Pattern.compile("^\\s*~QUIT");
        Matcher tm = TTYm.matcher(str);
        if (Main.TTYConnected.equals("Connected") & tm.lookingAt()) {
            foundMatchingCommand = true;
            Main.disconnect = true;
            Main.log("Disconnect request from " + Main.TTYCaller);

        } else if (tm.lookingAt()) {
            Main.TX_Text = "~QUIT\n";
        }
        // ~LISTFILES for TTY session...
        Pattern LFm = Pattern.compile("^\\s*~LISTFILES");
        Matcher lf = LFm.matcher(str);
        //Open both ways                        if (Main.TTYConnected.equals("Connected") & lf.lookingAt()) {
        if (lf.lookingAt()) {
            foundMatchingCommand = true;
            String downloaddir = Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator;
            File dd = new File(downloaddir);
            String[] filelist = dd.list();
            Main.TX_Text += ("Your_files: " + Integer.toString(filelist.length) + "\n");
            for (int i = 0; i < filelist.length; i++) {
                Main.TX_Text += (filelist[i] + "\n");
            }
            Main.TX_Text += "-end-\n";
        }

        // ~QTC? or ~QTC? NN+  for TTY session...
        Pattern MHm = Pattern.compile("^\\s*~QTC\\??\\s*(\\d*)\\+?");
        Matcher mh = MHm.matcher(str);
        //Open both ways                        if (Main.TTYConnected.equals("Connected") & lf.lookingAt()) {
        int fromNumber = 0;
        if (Main.TTYConnected.equals("Connected") & mh.lookingAt()) {
            if (mh.group(1) != null) {
                foundMatchingCommand = true;
                try {
                    fromNumber = Integer.decode(mh.group(1));
                } catch (NumberFormatException e) {
                }
            }
            if (Main.WantServer) {
                Main.TX_Text += serverMail.getHeaderList(fromNumber);
            } else {
                Main.TX_Text += "Sorry, Not enabled\n";
            }
        }
        
        // ~DELETE NN for TTY session...delete specific message (one at a time)
        Pattern pdc = Pattern.compile("^\\s*~DELETE\\s+(\\d+)\\S*");
        Matcher mdc = pdc.matcher(str);
        int deleteNumber = 0;
        if (Main.TTYConnected.equals("Connected") & mdc.lookingAt()) {
            if (mdc.group(1) != null) {
                foundMatchingCommand = true;
                try {
                    deleteNumber = Integer.decode(mdc.group(1));
                } catch (NumberFormatException e) {
                }
            }
            if (Main.WantServer) {
                Main.TX_Text += serverMail.deleteMail(deleteNumber);
            } else {
                Main.TX_Text += "Sorry, Not enabled\n";
            }
        }

        // ~READ NN or ~READZIP NN  for TTY session...
        Pattern RMm = Pattern.compile("^\\s*~READ(ZIP)?\\s+(\\d+)");
        Matcher rm = RMm.matcher(str);
        //Open both ways                        if (Main.TTYConnected.equals("Connected") & lf.lookingAt()) {
        int emailNumber = 0;
        boolean compressed = false;
        if (Main.TTYConnected.equals("Connected") & rm.lookingAt()) {
            if (rm.group(2) != null) {
                foundMatchingCommand = true;
                try {
                    emailNumber = Integer.decode(rm.group(2));
                    compressed = (rm.group(1) != null);
                } catch (NumberFormatException e) {
                    emailNumber = 0;
                }
            }
            if (Main.WantServer) {
                if (emailNumber > 0) {
                    Main.TX_Text += serverMail.readMail(emailNumber, compressed);
                }
            } else {
                Main.TX_Text += "Sorry, Not enabled\n";
            }
        }

        // ~TGET url or ~TGETZIP url  for TTY session...
        //e.g.: "~TGET www.bom.gov.au/nsw/forecasts/centralwestslopes.shtml begin:Forecast issued at end:The next routine forecast"
        //e.g.: "~TGET www.bom.gov.au/nsw/forecasts/upperwestern.shtml end:The next routine forecast"
        //e.g.: "~TGET www.bom.gov.au/nsw/forecasts/centralwestslopes.shtml begin:Forecast issued at"       
        Pattern TGm = Pattern.compile("^(\\s*~TGET(ZIP)?)\\s+([^\\s]+)\\s*(.*)");
        Matcher tg = TGm.matcher(str);
        String startStopStr = "";
        if (Main.TTYConnected.equals("Connected") & tg.lookingAt()) {
            if (tg.group(3) != null) {
                foundMatchingCommand = true;
                if (tg.group(4) != null) {
                    startStopStr = tg.group(4).trim();
                }
                Boolean tgetZip = (tg.group(2) != null);
                if (Main.WantServer) {
                    Main.TX_Text += serverMail.readWebPage(tg.group(3), startStopStr, tgetZip);
                } else {
                    Main.TX_Text += "Sorry, Not enabled\n";
                }
            }
        }

        // Record updated
        Pattern SPC = Pattern.compile("^Updated data");
        Matcher spc = SPC.matcher(str);
        if (spc.lookingAt()) {
            if (Main.sm.hispubkey.length() > 0) {
                String mailpass = Main.configuration.getPreference("POPPASS");
                if (mailpass.length() > 0 & Main.Passwrd.length() > 0) {
                    String intext = Main.cr.encrypt(Main.sm.hispubkey, mailpass + "," + Main.Passwrd);
                    Main.TX_Text += ("~Msp" + intext + "\n");
                    Main.mainwindow += "\n=>>" + intext + "\n";
                } else {
                    Main.mainwindow += "\n=>>" + "No POP password or link password set?\n";
                }
            } else {
                Main.mainwindow += "\n=>>" + "No server public key... reconnect....\n";
            }

        }

        // ~GETBIN for TTY session...
        Pattern GBm = Pattern.compile("^\\s*~GETBIN\\s(\\S+)");
        Matcher gb = GBm.matcher(str);
        //Open both ways                                    if (Main.TTYConnected.equals("Connected") & gb.lookingAt()) {
        if (gb.lookingAt()) {
            foundMatchingCommand = true;
            String downloaddir = Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator;

            String codedFile = "";
            String token = "";
            String myfile = gb.group(1);
            String mypath = downloaddir + gb.group(1);

            if (mypath.length() > 0) {

                String Destination = myserver;

                FileInputStream in = null;

                File incp = new File(mypath);
                File outcp = new File(Main.HomePath + Main.Dirprefix + "tmpfile");

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(incp);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outcp);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    byte[] buf = new byte[1024];
                    int i = 0;
                    while ((i = fis.read(buf)) != -1) {
                        fos.write(buf, 0, i);
                    }
                } catch (Exception e) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException ex) {
                            Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException ex) {
                            Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                String mysourcefile = Main.HomePath + Main.Dirprefix + "tmpfile";

                try {
                    in = new FileInputStream(mysourcefile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }

                GZIPOutputStream myzippedfile = null;

                String tmpfile = Main.HomePath + Main.Dirprefix + "tmpfile.gz";

                try {
                    myzippedfile = new GZIPOutputStream(new FileOutputStream(tmpfile));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ioe) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ioe);
                }

                byte[] buffer = new byte[4096];
                int bytesRead;

                try {
                    while ((bytesRead = in.read(buffer)) != -1) {
                        myzippedfile.write(buffer, 0, bytesRead);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    in.close();
                    myzippedfile.close();
                } catch (IOException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }

                Random r = new Random();
                token = Long.toString(Math.abs(r.nextLong()), 12);
                token = "tmp" + token;

                codedFile = Main.HomePath + Main.Dirprefix + "Outpending" + Main.Separator + token;

                Base64.encodeFileToFile(tmpfile, codedFile);

                File dlfile = new File(tmpfile);
                if (dlfile.exists()) {
                    dlfile.delete();
                }

                String TrString = "";
                File mycodedFile = new File(codedFile);
                if (mycodedFile.isFile()) {
                    TrString = ">FM:" + Main.q.callsign + ":" + Destination + ":"
                            + token + ":u:" + myfile
                            + ":" + Long.toString(mycodedFile.length()) + "\n";
                }

                if (Main.Connected) {
                    if (mycodedFile.isFile()) {
                        Main.TX_Text += "~FO5:" + Main.q.callsign + ":" + Destination + ":"
                                + token + ":u:" + myfile
                                + ":" + Long.toString(mycodedFile.length()) + "\n";
                    }
                }

                File Transactions = new File(Main.Transactions);
                FileWriter tr;
                try {
                    tr = new FileWriter(Transactions, true);
                    tr.write(TrString);
                    tr.close();
                } catch (IOException ex) {
                    Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //Not used at present
        // APRS positions 
        //P14,PE1FTV,51.35300,5.38517
        Pattern APRSm = Pattern.compile("(\\w\\d+),(\\S+),(\\-*\\d+\\.\\d+),(\\-*\\d+\\.\\d+)");
        Matcher am = APRSm.matcher(str);
        if (am.lookingAt()) {
            String wptype = am.group(1);
            String Tag = am.group(2);
            String Lat = am.group(3);
            String Lon = am.group(4);
            long epoch = System.currentTimeMillis() / 1000;

            //String outline ="SK0QO-B>SK0QO-BS:!5914.24ND01814.61E&RNG0050 70cm Voice 434.575 -2.00 MHz";
            String aprsString = "";

            aprsString = convert_to_aprsformat(Tag, Lat, Lon, "", wptype);

            Main.mapsock.sendmessage(aprsString);

            int i = 0;
            for (i = 0; i < 20; i++) {

                if (Main.Positions[i][0] == null) {
                    Main.Positions[i][0] = wptype;
                    Main.Positions[i][1] = Tag;
                    Main.Positions[i][2] = Lat;
                    Main.Positions[i][3] = Lon;
                    Main.Positions[i][4] = Long.toString(epoch);
                    break;
                } else if (Main.Positions[i][1].equals(Tag)) {
                    Main.Positions[i][2] = Lat;
                    Main.Positions[i][3] = Lon;
                    Main.Positions[i][4] = Long.toString(epoch);
                    break;
                }
                if ((epoch - Long.parseLong(Main.Positions[i][4])) / 1000 > 180) {
                    Main.Positions[i][0] = null;
                    Main.Positions[i][1] = null;
                    Main.Positions[i][2] = null;
                    Main.Positions[i][3] = null;
                    Main.Positions[i][4] = null;
                }
                if (Main.Positions[i][0] != null) {

                }
            }
        }

        // mail headers 
        Pattern pm = Pattern.compile("^\\s*(Your\\smail:)\\s(\\d*)");
        Matcher mm = pm.matcher(str);
        if (mm.lookingAt()) {
            if (mm.group(1).equals("Your mail:")) {
                Headers = true;
                Firstline = true;
                DataSize = Integer.parseInt(mm.group(2));
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                try {
                    this.headers = new FileWriter(Main.HomePath + Main.Dirprefix + "headers", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the headers file.", e, true);
                }
            }
        }

        // IAC fleetcodes
        /*
                                    if (!Main.Connected){
                                             Pattern pc = Pattern.compile(".*<SOH>(ZFZF)");
                                            Matcher mc = pc.matcher(str);
                                             if (mc.lookingAt()) {
                                                    if (mc.group(1).equals("ZFZF")){   
                                                    Main.IACmode = true;
                                                    Firstline = true;
                                                    try {                                            
                                                        iacout = new BufferedWriter(new FileWriter(Main.HomePath + Main.Dirprefix + "iactemp", true));
                                                        Main.q.Message("Receiving IAC fleetcode file", 15);
                                                    } 
                                                    catch (Exception e){
                                                        Main.log.writelog("Error when trying to open the iac file.", e, true);
                                                        Main.q.Message("Error opening file...", 15);
                                                    }
                                                }
                                             }
                                    }                                    
         */
        
        // file list
        Pattern pl = Pattern.compile("^\\s*(Your_files:)\\s(\\d+)");
        Matcher ml = pl.matcher(str);
        if (ml.lookingAt()) {
            if (ml.group(1).equals("Your_files:")) {
                FileList = true;
                Firstline = true;
                // set progress indicator here...
                ThisFileLength = ml.group(2);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
            }
        }

        //send mail (~SEND)
        Pattern SMp = Pattern.compile("^(~SEND)$");
        Matcher smm = SMp.matcher(str);
        if (Main.TTYConnected.equals("Connected") & smm.lookingAt()) {
            foundMatchingCommand = true;
            if (smm.group(1).equals("~SEND")) {
                emailUpload = true;
                Main.comp = false;
                Firstline = true;
                try {
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "tempEmail");
                    tmp.delete();
                    this.tempEmailFile = new FileWriter(new File(Main.HomePath + Main.Dirprefix + "tempEmail"), true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the mail file.", e, true);
                }
            }
        }

        // file download
        Pattern pf = Pattern.compile("^\\s*(Your\\sfile:)(.*)\\s(\\d+)");
        Matcher mf = pf.matcher(str);
        if (mf.lookingAt()) {
            if (mf.group(1).equals("Your file:")) {
                FileDownload = true;
                Main.comp = true;
                Firstline = true;
                ThisFile = mf.group(2);
                // set progress indicator here...
                ThisFileLength = mf.group(3);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                try {
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    tmp.delete();
                    this.dlFile = new FileWriter(new File(Main.HomePath + Main.Dirprefix + "TempFile"), true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the download file.", e, true);
                }
            }
        }
        // >FM:PI4TUE:PA0R:Jynhgf:f:test.txt:496
        Pattern fm = Pattern.compile("\\s*>FM:(\\S+):(\\S+):(\\S+):(\\w):(.*):(\\d+)");
        Matcher fmm = fm.matcher(str);
        if (fmm.lookingAt()) {
            foundMatchingCommand = true;
            if (fmm.group(4).equals("f")) {
                FileDownload = true;
                Main.comp = true;
                Firstline = true;
                ThisFile = fmm.group(5);
                Transaction = fmm.group(3);
                // set progress indicator here...
                ThisFileLength = fmm.group(6);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                try {
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    tmp.delete();
                    // open filewriter for TempFile
                    this.dlFile = new FileWriter(new File(Main.HomePath + Main.Dirprefix + "TempFile"), true);
                    // copy pending file into temp
                    File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);

                    if (pending.exists()) {
                        DataReceived = (int) pending.length();
                        FileReader in = new FileReader(pending);
                        int c;
                        // copy the pending file
                        while ((c = in.read()) != -1) {
                            dlFile.write(c);
                        }
                        // close pending file
                        in.close();
                    }
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the download file.", e, true);
                }
                try {
                    Trfile = new File(Main.pendingstr + Transaction);
                    // open pending file (now called pFile) for write
                    this.pFile = new FileWriter(Trfile, true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the pending file.", e, true);
                }
            } else if (fmm.group(4).equals("u")) {
                FileDownload = true;
                Main.comp = true;
                Firstline = true;
                ThisFile = fmm.group(5) + ".gz";
                Transaction = fmm.group(3);
                // set progress indicator here...
                ThisFileLength = fmm.group(6);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                Main.log("Receiving file " + ThisFile + " from " + Main.TTYCaller);
                try {
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    tmp.delete();
                    // open filewriter for TempFile
                    this.dlFile = new FileWriter(new File(Main.HomePath + Main.Dirprefix + "TempFile"), true);
                    // copy pending file into temp
                    File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);

                    if (pending.exists()) {
                        DataReceived = (int) pending.length();
                        FileReader in = new FileReader(pending);
                        int c;
                        // copy the pending file
                        while ((c = in.read()) != -1) {
                            dlFile.write(c);
                        }
                        // close pending file
                        in.close();
                    }
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the download file.", e, true);
                }
                try {
                    Trfile = new File(Main.pendingstr + Transaction);
                    // open pending file (now called pFile) for write
                    this.pFile = new FileWriter(Trfile, true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the pending file.", e, true);
                }
// compressed web page open...
            } else if (fmm.group(4).equals("w")) {
                //Compressed web page
                CwwwDownload = true;
                Main.comp = true;
                Firstline = true;
                ThisFile = fmm.group(5);
                Transaction = fmm.group(3);
                // set progress indicator here...
                ThisFileLength = fmm.group(6);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                try {
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    tmp.delete();
                    // open filewriter for TempFile
                    this.dlFile = new FileWriter(new File(Main.HomePath + Main.Dirprefix + "TempFile"), true);
                    // open File for pending
                    File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);

                    if (pending.exists()) {
                        DataReceived = (int) pending.length();
                        FileReader in = new FileReader(pending);
                        int c;
                        // copy the pending file
                        while ((c = in.read()) != -1) {
                            dlFile.write(c);
                        }
                        // close pending file
                        in.close();
//                                                    pending.delete();
                    }
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the download file.", e, true);
                }
                try {
                    Trfile = new File(Main.pendingstr + Transaction);
                    this.pFile = new FileWriter(Trfile, true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the pending file.", e, true);
                }

                // compressed mail download
            } else if (fmm.group(4).equals("m")) {
                //Mail download
                CMsgDownload = true;
                Transaction = fmm.group(3);
                Main.comp = true;
                Firstline = true;
                try {
                    File F1 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");  // make sure it is empty
                    if (F1.exists()) {
                        F1.delete();
                    }
                    File F2 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");  // make sure it is empty
                    if (F2.exists()) {
                        F2.delete();
                    }
                    tmpmessage = new FileWriter(Main.HomePath + Main.Dirprefix + "tmpmessage", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the headers file.", e, true);
                }
                // set progress indicator here...
                ThisFileLength = fmm.group(6);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                if (pending.exists()) {
                    DataReceived = (int) pending.length();
                    FileReader in = new FileReader(pending);
                    int c;
                    // copy the pending file
                    while ((c = in.read()) != -1) {
                        tmpmessage.write(c);
                    }
                    // close pending file
                    in.close();
                }

                try {
                    Trfile = new File(Main.pendingstr + Transaction);
                    pFile = new FileWriter(Trfile, true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the pending file.", e, true);
                }

            // compressed mail upload (I am a server)
            } else if (fmm.group(4).equals("s")) {
                CompressedEmailUpload = true;
                Transaction = fmm.group(3);
                Main.comp = true;
                Firstline = true;
                try {
                    File F1 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");  // make sure it is empty
                    if (F1.exists()) {
                        F1.delete();
                    }
                    File F2 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");  // make sure it is empty
                    if (F2.exists()) {
                        F2.delete();
                    }
                    tmpmessage = new FileWriter(Main.HomePath + Main.Dirprefix + "tmpmessage", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to upload email (in TTYServer mode)", e, true);
                }
                // set progress indicator here...
                ThisFileLength = fmm.group(6);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
                File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                if (pending.exists()) {
                    DataReceived = (int) pending.length();
                    FileReader in = new FileReader(pending);
                    int c;
                    // copy the pending file
                    while ((c = in.read()) != -1) {
                        tmpmessage.write(c);
                    }
                    // close pending file
                    in.close();
                }
                try {
                    Trfile = new File(Main.pendingstr + Transaction);
                    pFile = new FileWriter(Trfile, true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the pending file.", e, true);
                }
            }
        }

        // >FO5:PI4TUE:PA0R:JhyJkk:f:test.txt:496
        Pattern ofr = Pattern.compile("\\s*>FO(\\d):([A-Z0-9\\-]+):([A-Z0-9\\-]+):([A-Za-z0-9_-]+):(\\w)");
        Matcher ofrm = ofr.matcher(str);
        if (ofrm.lookingAt()) {
            foundMatchingCommand = true;
            if (ofrm.group(5).equals("f") | ofrm.group(5).equals("w") | ofrm.group(5).equals("m")) {
                // get the file ?
                File pending;
                pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + ofrm.group(4));
                long x = 0;
                if (pending.exists()) {
                    x = pending.length();
                }
                if (Main.mainui.jRadioButtonAccept.isSelected()) {
                    Main.TX_Text += "~FY:" + ofrm.group(4) + ":" + Long.toString(x) + "\n";
                } else if (Main.mainui.jRadioButtonReject.isSelected()) {
                    Main.TX_Text += "~FN:" + ofrm.group(4) + "\n";
                } else {
                    Main.TX_Text += "~FA:" + ofrm.group(4) + "\n";
                    if (pending.exists()) {
                        pending.delete();
                    }

                }
            }
        }

        // ~FO5:PI4TUE:PA0R:tmpasdkkdfj:u:test.txt:36
        // ~FO5:PI4TUE:PA0R-1:a30a69:s: :847 //E-mail upload to this TTYserver
        Pattern ofr2 = Pattern.compile("\\s*~FO(\\d):([A-Z0-9\\-]+):([A-Z0-9\\-]+):([A-Za-z0-9_-]+):(\\w).*");
        Matcher ofrm2 = ofr2.matcher(str);
        if (ofrm2.lookingAt()) {
            foundMatchingCommand = true;
            if (ofrm2.group(5).equals("u")) {
                // get the file ?
                File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + ofrm2.group(4));
                long x = 0;
                if (pending.exists()) {
                    x = pending.length();
                }
                if (Main.mainui.jRadioButtonAccept.isSelected()) {
                    Main.TX_Text += "~FY:" + ofrm2.group(4) + ":" + Long.toString(x) + "\n";
                } else if (Main.mainui.jRadioButtonReject.isSelected()) {
                    Main.TX_Text += "~FN:" + ofrm2.group(4) + "\n";
                } else {
                    Main.TX_Text += "~FA:" + ofrm2.group(4) + "\n";
                    if (pending.exists()) {
                        pending.delete();
                    }
                }
            } else if (ofrm2.group(5).equals("s")) { //E-Mail upload to this TTYserver for sending
                // get the file ?
                File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + ofrm2.group(4));
                long x = 0;
                if (pending.exists()) {
                    x = pending.length();
                }
                //Always accept client partial upload
                Main.TX_Text += "~FY:" + ofrm2.group(4) + ":" + Long.toString(x) + "\n";
            }
        }

        // ~FY:tmpjGUytg:request partial email upload 
        Pattern yfr = Pattern.compile("\\s*~FY:([A-Za-z0-9]+):(\\d+)");
        Matcher yfrm = yfr.matcher(str);
        if (yfrm.lookingAt()) {
            foundMatchingCommand = true;
            String partialfile = yfrm.group(1);
            String startingbyte = yfrm.group(2);
//                     System.out.println(partialfile);
            int start = Integer.parseInt(startingbyte);
            File penf = new File(Main.Pendingdir + partialfile);
            File foutpending = new File(Main.Outpendingdir + Main.Separator + partialfile);
            String filename = "";
            if (penf.exists()) {
                int i = 0;
                try {
                    FileInputStream fis = new FileInputStream(penf);
                    char current;
                    String callsign = Main.configuration.getPreference("CALL");
                    callsign = callsign.trim();
                    //String servercall = Main.configuration.getPreference("SERVER");
                    String servercall = Main.q.getServer().trim();
                    long flen = 0;
                    flen = penf.length() - start;
                    Main.TX_Text += ">FM:" + callsign + ":" + servercall + ":" + partialfile + ":s: :" + Long.toString(flen) + "\n";
                    while (fis.available() > 0) {
                        current = (char) fis.read();
                        i++;

                        if (i > start) {
                            Main.TX_Text += current;
                        }
                    }
                    Session.DataSize = Main.TX_Text.length();
                    fis.close();
                } catch (IOException e) {
                    //System.out.println("IO error on pending file");
                }
            } else if (foutpending.exists()) {
                int i = 0;
                try {
                    FileInputStream fis = new FileInputStream(foutpending);
                    char current;
                    String callsign = Main.configuration.getPreference("CALL");
                    callsign = callsign.trim();
                    //String servercall = Main.configuration.getPreference("SERVER");
                    String servercall = Main.q.getServer().trim();
                    File ft = new File(Main.Transactions);
                    String[] ss = null;
                    if (ft.exists()) {
//                                    System.out.println("Transactons exists");
                        FileReader fr = new FileReader(Main.Transactions);
                        BufferedReader br = new BufferedReader(fr);
                        String s;
                        while ((s = br.readLine()) != null) {
//                                   System.out.println("s=:" + s);
                            ss = s.split(":");
                            //                                  System.out.println(ss[5]);
                            if (s.contains(partialfile)) {
//                                    System.out.println(s);
                                filename = ss[5];
                            }
                        }
                        fr.close();
                    }
                    long flen = 0;
                    flen = foutpending.length() - start;
                    Main.TX_Text += ">FM:" + callsign + ":" + servercall + ":" + partialfile + ":" + Main.filetype + ":" + filename + ":" + Long.toString(flen) + "\n";
                    while (fis.available() > 0) {
                        current = (char) fis.read();
                        i++;
                        if (i > start) {
                            Main.TX_Text += current;
                        }
                    }
                    fis.close();
                    Main.TX_Text += "\n-end-\n";
                    Session.DataSize = Integer.parseInt(ss[6]);
                } catch (IOException e) {
//                                                System.out.println("IO error on pending file");
                }
            } else if (Main.TTYConnected.equals("Connected")) {
                //Look in the Outbox for partial upload to client. Files are in the format VK2ETA_-w-_12345
                //Since we don't know the file type from the ~FY command (s,w,f etc...),
                //  we scan the directory for a match
                String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
                String caller = Main.TTYCaller;
                File[] filesOutbox;
                // Get the list of files in the designated folder
                File dir = new File(Main.HomePath + Main.Dirprefix + "Outbox");
                filesOutbox = dir.listFiles();
                FileFilter fileFilter = new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                };
                //Generates an array of strings containing the file names to resume downloading
                filesOutbox = dir.listFiles(fileFilter);
                for (int i = 0; i < filesOutbox.length; i++) {
                    String pendingCaller = "";
                    String pendingType = "";
                    String pendingToken = "";
                    String pendingFn = filesOutbox[i].getName();
                    if (pendingFn.contains("_-")) {
                        int firstSep = pendingFn.indexOf("_-");
                        int secondSep = pendingFn.indexOf("-_");
                        if (firstSep > 0 && secondSep > 0) {
                            pendingCaller = pendingFn.substring(0, firstSep);
                            pendingType = pendingFn.substring(firstSep + 2, secondSep);
                            pendingToken = pendingFn.substring(secondSep + 2);
                        }
                        if (pendingCaller.equals(caller) && partialfile.equals(pendingToken)) {
                            //Found a match, queue for TX at specified offset
                            int j = 0;
                            File penfOut = filesOutbox[i].getAbsoluteFile();
                            try {
                                FileInputStream fis = new FileInputStream(penfOut);
                                char current;
                                long flen = 0;
                                flen = penfOut.length() - start;
                                String mBuffer = ">FM:" + serverCall + ":" + caller + ":" + partialfile + ":" + pendingType + ": :" + Long.toString(flen) + "\n";
                                while (fis.available() > 0) {
                                    current = (char) fis.read();
                                    j++;
                                    //Skip data already received
                                    if (j > start) {
                                        mBuffer += current;
                                    }
                                }
                                Main.TX_Text += mBuffer + "\n-end-\n";
                                Session.DataSize = Main.TX_Text.length();
                                fis.close();
                            } catch (IOException e) {
//                                                System.out.println("IO error on pending file");
                            }
                        }
                    }
                }
            }
            Main.log("Sending file: " + filename);
        }

        // ~FA:tmpjGUytg  delete output file in Outbox
        Pattern afr = Pattern.compile("\\s*~FA:([A-Za-z0-9]+)");
        Matcher afrm = afr.matcher(str);
        if (afrm.lookingAt()) {
            foundMatchingCommand = true;
            String deletefl = afrm.group(1);
            str = "";
            //Are we a server or client?
            if (Main.TTYConnected.equals("Connected")) {
                //Look in the Outbox for partial upload to client. Files are in the format VK2ETA_-w-_12345
                //Since we don't know the file type from the ~FY command (s,w,f etc...),
                //  we scan the directory for a match
                String caller = Main.TTYCaller;
                File[] filesOutbox;
                // Get the list of files in the designated folder
                File dir = new File(Main.HomePath + Main.Dirprefix + "Outbox");
                //filesOutbox = dir.listFiles();
                FileFilter fileFilter = new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                };
                //Generates an array of strings containing the file names to resume downloading
                filesOutbox = dir.listFiles(fileFilter);
                for (int i = 0; i < filesOutbox.length; i++) {
                    String pendingCaller = "";
                    String pendingType = "";
                    String pendingToken = "";
                    String pendingFn = filesOutbox[i].getName();
                    if (pendingFn.contains("_-")) {
                        int firstSep = pendingFn.indexOf("_-");
                        int secondSep = pendingFn.indexOf("-_");
                        if (firstSep > 0 && secondSep > 0) {
                            pendingCaller = pendingFn.substring(0, firstSep);
                            pendingType = pendingFn.substring(firstSep + 2, secondSep);
                            pendingToken = pendingFn.substring(secondSep + 2);
                        }
                        if (pendingCaller.equals(caller) && deletefl.equals(pendingToken)) {
                            //Found a match for callsign and token combination, delete file
                            File penfOut = filesOutbox[i].getAbsoluteFile();
                            if (penfOut.exists()) {
                                penfOut.delete();
                            }
                        }
                    }
                }
            } else {
                //We are a client, we store the file name without transaction information
                try {
                    File df = new File(Main.HomePath + Main.Dirprefix + "Outbox" + Main.Separator + deletefl);
                    if (df.exists()) {
                        // Move to Sent or delete
                        if (!movefiletodir(df, Main.HomePath + Main.Dirprefix + "Sent")) {
                            df.delete();
                        }
                        Main.log("Mail sent to server/client...");
                        // Update the outbox grid
                        Main.mainui.refreshEmailGrid();
                    }
                    File outpenf = new File(Main.Outpendingdir + Main.Separator + deletefl);
                    if (outpenf.exists()) {
                        outpenf.delete();
                        Main.mainwindow += (Main.myTime() + " File stored...\n");
                        Main.FilesTextArea += " File stored...\n";
                    }
                    File penf = new File(Main.Pendingdir + deletefl);
                    if (penf.exists()) {
                        penf.delete();
//                                                Main.mainwindow += "Mail sent on server...\n";
//                                                Main.FilesTextArea += "Mail sent on server...\n";
                    }
                    if (TransactionsExists()) {
                        FileReader trf = new FileReader(Main.Transactions);
                        BufferedReader tr = new BufferedReader(trf);
                        String sta[] = new String[20];
                        String st;
                        int st1 = 0;
                        while ((st = tr.readLine()) != null & st1 < 20) {
                            if (!st.contains(deletefl)) {
                                sta[st1] = st;
                                st1++;
                            }

                        }
                        tr.close();
                        File trw = new File(Main.Transactions);
                        if (trw.exists()) {
                            trw.delete();
                        }
                        try {
                            if (sta[0] != null) {
                                FileWriter trwo = new FileWriter(Main.Transactions, true);
                                int k = 0;

                                while (k <= st1) {
                                    if (!sta[k].contains(deletefl)) {
                                        trwo.write(sta[k]);
                                        k++;
                                    }
                                }
                                trwo.close();
                            }
                        } catch (NullPointerException npe) {
                            //                                 System.out.println("nullpointerproblem:" + npe);
                        }
                    }
                    // reset progress bar
                    Session.DataSize = 0;
                    Session.DataReceived = 0;
                    Main.Progress = 0;
                    Main.q.Message("Upload complete...", 10);
                    str = "";
                    //                                           System.out.println("Deleting temp file " + deletefl);
                } catch (IOException i) {
//                                            System.out.println("IO problem:" + i);
                }
            }
        }
        
        // message receive
        Pattern pmsg = Pattern.compile("^\\s*(Your\\smsg:)\\s(\\d+)");
        Matcher mmsg = pmsg.matcher(str);
        if (mmsg.lookingAt()) {
            foundMatchingCommand = true;
            if (mmsg.group(1).equals("Your msg:")) {
                MsgDownload = true;
                Firstline = true;
                try {
                    this.tmpmessage = new FileWriter(Main.HomePath + Main.Dirprefix + "tmpmessage", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the headers file.", e, true);
                }
                // set progress indicator here...
                ThisFileLength = mmsg.group(2);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
            }
        }
        
        // compresssed message receive
        Pattern cpmsg = Pattern.compile("^\\s*(~ZIPPED64)\\s(\\d+)");
        Matcher cmmsg = cpmsg.matcher(str);
        if (cmmsg.lookingAt()) {
            foundMatchingCommand = true;
            if (cmmsg.group(1).equals("~ZIPPED64")) {
                CMsgDownload = true;
                Main.comp = true;
                Firstline = true;
                try {
                    File F1 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");  // make sure it is empty
                    F1.delete();
                    File F2 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");  // make sure it is empty
                    F2.delete();
                    this.tmpmessage = new FileWriter(Main.HomePath + Main.Dirprefix + "tmpmessage", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the headers file.", e, true);
                }
                // set progress indicator here...
                ThisFileLength = cmmsg.group(2);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
            }
        }

        // web page receive
        Pattern pw = Pattern.compile("^\\s*(Your\\swwwpage:)\\s(\\d+)");
        Matcher mw = pw.matcher(str);
        if (mw.lookingAt()) {
            foundMatchingCommand = true;
            if (mw.group(1).equals("Your wwwpage:")) {
                WWWDownload = true;
                Firstline = true;
                // set progress indicator here...
                ThisFileLength = mw.group(2);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
            }
        }

        //compressed  website Send
        Pattern tgmsg = Pattern.compile("^\\s*(~TGET64)\\s(\\d+)");
        Matcher tgmmsg = tgmsg.matcher(str);
        if (tgmmsg.lookingAt()) {
            foundMatchingCommand = true;
            if (tgmmsg.group(1).equals("~TGET64")) {
                CwwwDownload = true;
                Main.comp = true;
                Firstline = true;
                try {
                    File F1 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");  // make sure it is empty
                    if (F1.exists()) {
                        F1.delete();
                    }
                    File F2 = new File(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");  // make sure it is empty
                    if (F2.exists()) {
                        F2.delete();
                    }
                    this.tmpmessage = new FileWriter(Main.HomePath + Main.Dirprefix + "tmpmessage", true);
                } catch (Exception e) {
                    Main.log.writelog("Error when trying to open the temp file.", e, true);
                }
                // set progress indicator here...
                ThisFileLength = tgmmsg.group(2);
                DataSize = Integer.parseInt(ThisFileLength);
                DataReceived = 0;
                Main.DataSize = Integer.toString(DataSize);
            }
        }

        // Message sent...
        Pattern ps = Pattern.compile("^\\s*(Message sent\\.\\.\\.)");
        Matcher ms = ps.matcher(str);
        if (ms.lookingAt()) {
            foundMatchingCommand = true;
            if (ms.group(1).equals("Message sent...")) {
                try {
                    File fd = new File(Main.Mailoutfile);
                    // Move to Sent or delete
                    if (!movefiletodir(fd, Main.HomePath + Main.Dirprefix + "Sent")) {
                        fd.delete();
                    }
                } catch (Exception e) {
                    Main.log.writelog("Error moving sent mail file.", e, true);
                }
            }
        }

        //Uncompressed E-mail? Look for the single . as mark of end of message
        if (emailUpload) {
            Pattern SMpe = Pattern.compile("^(\\.)$");
            Matcher SMme = SMpe.matcher(str);
            if (SMme.lookingAt()) {
                foundMatchingCommand = true;
                String to = "";
                String subject = "";
                String body = "";
                String from = "";
                if (SMme.group(1).equals(".")) {
                    emailUpload = false;
                    DataReceived = 0;
                    Main.DataSize = Integer.toString(0);
                    try {
                        this.tempEmailFile.close();
                        //Now read and analyse the file
                        FileReader fr = new FileReader(Main.HomePath + Main.Dirprefix + "tempEmail");
                        BufferedReader br = new BufferedReader(fr);
                        String mLine;
                        while ((mLine = br.readLine()) != null) {
                            if (mLine.startsWith("To:")) {
                                to = mLine.substring(3);
                            } else if (mLine.startsWith("Subject:")) {
                                subject = mLine.substring(8);
                            } else if (mLine.startsWith("From:")) {
                                from = mLine.substring(5);
                            } else {
                                //Body
                                body += mLine;
                            }
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to handle the email upload file.", ex, true);
                    }
                    //Make sure we have something to send
                    if (!to.equals("") & (!subject.equals("") | !body.equals(""))) {
                        //No attachment yet
                        Main.TX_Text += serverMail.sendMail(from, to, subject, body, ""); //last param is attachementFileName
                    }
                }
            } else {
                //Just store the data
                if (!Firstline) {
                    this.tempEmailFile.write(str + "\n");
                } else {
                    //Reset flag
                    Firstline = false;
                }
            }
        }

        // -end- command
        Pattern pe = Pattern.compile("^\\s*(\\S+)");
        Matcher me = pe.matcher(str);
        if (me.lookingAt()) {
            if (me.group(1).equals("-end-")) {
                foundMatchingCommand = true;
                if (Headers) {
                    Headers = false;
                    DataReceived = 0;
                    Main.DataSize = Integer.toString(0);
                    try {
                        this.headers.close();
                        Main.mainui.refreshEmailGrid();
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the headers file.", ex, true);
                    }
                }
                if (FileList) {
                    FileList = false;
                }
                if (FileDownload) {
                    FileDownload = false;
                    Main.comp = false;

                    try {
                        this.dlFile.close();
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the download file.", ex, true);
                    }

                    try {
                        Base64.decodeFileToFile(Main.HomePath + Main.Dirprefix + "TempFile", Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator + ThisFile);

                        Unzip.Unzip(Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator + ThisFile);

                        File tmp = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                        if (tmp.exists()) {
                            tmp.delete();
                        }

                        Main.TX_Text += "~FA:" + Transaction + "\n";

                        Main.Progress = 0;

                    } catch (IOException e) {
                        ;
                    } catch (Exception exc) {
                        Main.log.writelog("Error when trying to decode the downoad file.", exc, true);
                    } catch (NoClassDefFoundError exp) {
                        Main.q.Message("problem decoding B64 file", 10);
                    }
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    boolean success = tmp.delete();

                    try {
                        if (pFile != null) {
                            Main.sm.pFile.close();
                            Main.sm.Trfile.delete();
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the pending file.", ex, true);
                    }
                    Main.Progress = 0;

                    Main.log(ThisFile + " received");

                }
                // messages  download      - append tmpmessage to Inbox in mbox format                          
                if (MsgDownload) {
                    MsgDownload = false;
                    this.tmpmessage.close();
                    // append to Inbox file
                    FileReader fr = new FileReader(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    BufferedReader br = new BufferedReader(fr);
                    // all local stuff
                    String s;
                    String From = null;
                    String Date = null;
                    String Sub = null;
                    String outstr = "";
                    String attachment = "";
                    base64attachment = false;
                    plaintextattachment = false;
                    // read tmpmessage line by line
                    while ((s = br.readLine()) != null) {
                        // compile some patterns and set up the matchers
                        Pattern pfrm = Pattern.compile("^\\s*(From:)\\s(.*)");
                        Matcher mfrm = pfrm.matcher(s);
                        Pattern pdate = Pattern.compile("^\\s*(Date:)\\s(\\w{3})\\,*\\s+(\\d+)\\s(\\w{3})\\s(\\d{4})\\s(\\d\\d:\\d\\d:\\d\\d)");
                        Matcher mdate = pdate.matcher(s);
                        Pattern pdate2 = Pattern.compile("^\\s*(Date:)\\s(\\d+)\\s+(\\w{3})\\s+(\\d{4}\\s\\d\\d:\\d\\d:\\d\\d)");
                        Matcher mdate2 = pdate2.matcher(s);
                        Pattern psub = Pattern.compile("^\\s*(Subject:)\\s(.*)");
                        Matcher msub = psub.matcher(s);
                        Pattern p64 = Pattern.compile("^\\s*(content-transfer-encoding: base64)");
                        Matcher m64 = p64.matcher(s.toLowerCase());
                        Pattern pnm = Pattern.compile(".*(filename=)(.*)");
                        Matcher mnm = pnm.matcher(s);
                        if (mfrm.lookingAt()) {
                            if (mfrm.group(1).equals("From:")) {
                                From = mfrm.group(2);
                            }
                        } else if (mdate.lookingAt()) {
                            if (mdate.group(1).equals("Date:")) {
                                Date = mdate.group(2) + " " + mdate.group(3) + " "
                                        + mdate.group(4) + " " + mdate.group(5) + " "
                                        + mdate.group(6);
                            }

                        } else if (mdate2.lookingAt()) {
                            if (mdate2.group(1).equals("Date:")) {
                                Date = mdate2.group(2) + " " + mdate2.group(3) + " "
                                        + mdate2.group(4);
                            }

                        } else if (msub.lookingAt()) {
                            if (msub.group(1).equals("Subject:")) {
                                Sub = msub.group(2);
                            }

                        } else if (m64.lookingAt()) {
                            if (m64.group(1).toLowerCase().contains("content-transfer-encoding: base64")) {
                                // there is an attachment...
                                base64attachment = true;
//                                                                debug ("Attachment");
                            }
                        } else if (mnm.lookingAt()) {
                            if (mnm.group(1).contains("filename=")) {
                                // get the file name
                                attachmentFilename = mnm.group(2);
                                attachmentFilename = attachmentFilename.substring(1, attachmentFilename.length() - 1);

//                                                             debug (attachmentFilename);
                            }
                        } else {
                            if (base64attachment) {
                                if (!s.equals("")) {
                                    if (!s.startsWith("--")) {
                                        attachment += s + "\n";
                                    } else {
                                        base64attachment = false;
                                        if (attachment.length() > 10 & attachmentFilename != null) {
                                            // Never mind if its a grib file
                                            attachmentFilename = "Files" + Main.Separator + attachmentFilename;
                                            attachmentFilename = Main.HomePath + Main.Dirprefix + attachmentFilename;
                                            // SaveAttachmentToFile(attachmentFilename, attachment, true);                                                                            
                                        }
                                    }
                                }
                            }
                        }
                        // body text for Inbox
                        outstr += s + "\n";
                        //                                                 debug ("Out=" + s);
                    } // end while

                    fr.close();
                    this.inbox = new FileWriter(Main.HomePath + Main.Dirprefix + "Inbox", true);
                    inbox.write("From " + From + " " + Date + "\n");
                    inbox.write("From: " + From + "\n");
                    if (Date != null) {
                        inbox.write("Date: " + Date + "\n");
                    }
                    inbox.write("Subject: " + Sub + "\n");
                    // write message body
                    if (outstr != null) {
                        inbox.write(outstr + "\n");
                    }
                    inbox.flush();
                    inbox.close();
                    Main.mainui.refreshEmailGrid();

                    File fl = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    if (fl.exists()) {
                        fl.delete();
                    }

                    Main.q.Message("Added to mbox queue", 10);
                }
                
                // compressed E-mail upload while I am a TTYServer
                if (CompressedEmailUpload) {
                    CompressedEmailUpload = false;
                    Main.comp = false;
                    tmpmessage.close();
                    // decode base 64 and unzip...
                    String cin = Main.HomePath + Main.Dirprefix + "tmpmessage";
                    String cout = Main.HomePath + Main.Dirprefix + "tmpmessage.gz";
                    String cmid = "";
                    try {
                        Base64.decodeFileToFile(cin, cout);
                        cmid = Unzip.Unzip(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");
                    } catch (Exception e) {
                        Main.q.Message("Decoding error!", 10);
                    }
                    //Analyse and Send email
                    String to = "";
                    String from = "";
                    String subject = "";
                    String body = "";
                    try {
                        //Now read and analyse the file
                        FileReader fr = new FileReader(Main.HomePath + Main.Dirprefix + "tmpmessage");
                        BufferedReader br = new BufferedReader(fr);
                        String mLine;
                        while ((mLine = br.readLine()) != null) {
                            if (mLine.startsWith("To:")) {
                                to = mLine.substring(3);
                            } else if (mLine.startsWith("Subject:")) {
                                subject = mLine.substring(8);
                            } else if (mLine.startsWith("From:")) {
                                from = mLine.substring(5);
                            } else if (!mLine.equals(".") && !mLine.startsWith("~SEND")){
                                //Body
                                body += mLine;
                            }
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to handle the email upload file.", ex, true);
                    }
                    //Make sure we have something to send
                    if (!to.equals("") && (!subject.equals("") | !body.equals(""))) {
                        //No attachment yet
                        String resultStr = serverMail.sendMail(from, to, subject, body, ""); //last param is attachementFileName
                        //Format reply depending on sending method (~SEND or compressed & restartable)
                        if (resultStr.contains("Message sent..")) {
                            if (Main.protocol > 0) {
                                Main.TX_Text += "~FA:" + Transaction + "\n";
                            } else {
                                Main.TX_Text += resultStr;
                            }
                            Main.q.Message("Email Sent...", 10);
                        }
                    }
                    Main.Progress = 0;
                }

                // compressed messages  download      - append tmpmessage to Inbox in mbox format                          
                if (CMsgDownload) {
                    CMsgDownload = false;
                    Main.comp = false;
                    tmpmessage.close();
                    // decode base 64 and unzip...
                    String cin = Main.HomePath + Main.Dirprefix + "tmpmessage";
                    String cout = Main.HomePath + Main.Dirprefix + "tmpmessage.gz";
                    String cmid = "";
                    try {
                        Base64.decodeFileToFile(cin, cout);
                        cmid = Unzip.Unzip(Main.HomePath + Main.Dirprefix + "tmpmessage.gz");
                    } catch (Exception e) {
                        Main.q.Message("Decoding error!", 10);
                    }
                    // append to Inbox file
                    FileReader fr = new FileReader(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    BufferedReader br = new BufferedReader(fr);
                    // all local stuff
                    String s;
                    String From = null;
                    String Date = null;
                    String Sub = null;
                    String outstr = "";
                    String attachment = "";
                    base64attachment = false;
                    // make some room on the screen...
                    Main.mainwindow += "\n\n";
                    // read tmpmessage line by line
                    while ((s = br.readLine()) != null) {
                        // show what we've got...
                        Main.mainwindow += s;
                        Main.mainwindow += "\n";
                        // compile some patterns and set up the matchers
                        Pattern pfrm = Pattern.compile("^\\s*(From:)\\s(.*)");
                        Matcher mfrm = pfrm.matcher(s);
                        Pattern pdate = Pattern.compile("^\\s*(Date:)\\s(\\w{3})\\,*\\s+(\\d+)\\s(\\w{3})\\s(\\d{4})\\s(\\d\\d:\\d\\d:\\d\\d)");
                        Matcher mdate = pdate.matcher(s);
                        Pattern pdate2 = Pattern.compile("^\\s*(Date:)\\s(\\d+)\\s+(\\w{3})\\s+(\\d{4}\\s\\d\\d:\\d\\d:\\d\\d)");
                        Matcher mdate2 = pdate2.matcher(s);
                        Pattern psub = Pattern.compile("^\\s*(Subject:)\\s(.*)");
                        Matcher msub = psub.matcher(s);
                        Pattern p64 = Pattern.compile("^\\s*(content-transfer-encoding: base64)");
                        Matcher m64 = p64.matcher(s.toLowerCase());
                        Pattern c64 = Pattern.compile("^\\s*(content-type)");
                        Matcher cc64 = c64.matcher(s.toLowerCase());
                        Pattern pnm = Pattern.compile(".*(filename=)(.*)");
                        Matcher mnm = pnm.matcher(s);
                        Pattern xui = Pattern.compile("X-UI-ATTACHMENT");
                        Matcher mxui = xui.matcher(s);
                        Pattern nmx = Pattern.compile("name=");
                        Matcher mnmx = nmx.matcher(s);
                        if (mfrm.lookingAt()) {
                            if (mfrm.group(1).equals("From:")) {
                                From = mfrm.group(2);
                            }
                        } else if (mdate.lookingAt()) {
                            if (mdate.group(1).equals("Date:")) {
                                Date = mdate.group(2) + " " + mdate.group(3) + " "
                                        + mdate.group(4) + " " + mdate.group(5) + " "
                                        + mdate.group(6);
                            }
                        } else if (mdate2.lookingAt()) {
                            if (mdate2.group(1).equals("Date:")) {
                                Date = mdate2.group(2) + " " + mdate2.group(3) + " "
                                        + mdate2.group(4);
                            }
                        } else if (msub.lookingAt()) {
                            if (msub.group(1).equals("Subject:")) {
                                Sub = msub.group(2);
                            }
                        } else if (m64.lookingAt()) {
                            if (m64.group(1).equals("content-transfer-encoding: base64")) {
                                // there is an attachment...
                                base64attachment = true;
//                                                                debug ("Attachment");
                                outstr += s + "\n";  // write to Inbox
                            }
                        } else if (cc64.lookingAt()) {
                            outstr += s + "\n";  // write to Inbox
                        } else if (mxui.lookingAt()) {
                            outstr += s + "\n";  // write to Inbox
                        } else if (mnm.lookingAt()) {
                            if (mnm.group(1).equals("filename=")) {
                                // get the file name
                                if (mnm.group(2).startsWith("\"")) {
//                                                                    debug (attachmentFilename);
                                    attachmentFilename = mnm.group(2).substring(1, mnm.group(2).length() - 1);
                                    // remove the "
                                } else {
                                    attachmentFilename = mnm.group(2);
                                }
//                                                             debug (attachmentFilename);
                                outstr += s + "\n";  // write to Inbox
                            }
                        } else if (mnmx.lookingAt()) {
                            outstr += s + "\n";  // write to Inbox
                        } else {
                            if (base64attachment) {
                                outstr += s + "\n";  // write to Inbox
                                if (!s.equals("")) {
                                    if (!s.startsWith("--")) {
                                        if (!s.contains("-")) {
                                            attachment += s + "\n";
                                        }
//     System.out.println(s);
                                    } else {
                                        base64attachment = false;
                                        if (attachment.length() > 10 && attachmentFilename != null) {
                                            // is it a grib file?
                                            if (attachmentFilename.endsWith(".grb")) {
                                                // are we on linux?
                                                if (Main.Separator.equals("/")) {
                                                    try {
                                                        File f1 = new File("/opt/zyGrib/grib/");
                                                        // is zygrib installed?
                                                        if (f1.isDirectory()) {
                                                            attachmentFilename = "/opt/zyGrib/grib/" + attachmentFilename;
                                                        } else {
                                                            // no, put it in the Files directory
                                                            File myfiles = new File(Main.HomePath + Main.Dirprefix + "Files");
                                                            if (!myfiles.isDirectory()) {
                                                                myfiles.mkdir();
                                                            }
                                                            attachmentFilename = "Files/" + attachmentFilename;
                                                            attachmentFilename = Main.HomePath + Main.Dirprefix + attachmentFilename;
                                                        }
                                                    } catch (Exception e) {
                                                        Main.q.Message("IO problem", 10);
                                                    }
                                                } else {
                                                    try {
                                                        // put it in the Files directory
                                                        File myfiles = new File(Main.HomePath + Main.Dirprefix + "Files");
                                                        if (!myfiles.isDirectory()) {
                                                            myfiles.mkdir();
                                                        }
                                                        attachmentFilename = "Files\\" + attachmentFilename;
                                                        attachmentFilename = Main.HomePath + Main.Dirprefix + attachmentFilename;
                                                    } catch (Exception e) {
                                                        Main.q.Message("IO problem", 10);
                                                    }
                                                }
                                            } else {
                                                // no grib file
                                                File myfiles = new File(Main.HomePath + Main.Dirprefix + "Files");
                                                if (!myfiles.isDirectory()) {
                                                    myfiles.mkdir();
                                                }
                                                attachmentFilename = "Files" + Main.Separator + attachmentFilename;
                                                attachmentFilename = Main.HomePath + Main.Dirprefix + attachmentFilename;
                                            }
                                            try {
                                                File myfiles = new File(Main.HomePath + Main.Dirprefix + "Files");
                                                if (!myfiles.isDirectory()) {
                                                    myfiles.mkdir();
                                                }
                                                // remove first line if "X-Attachment..."
                                                String[] attlines = attachment.split("\n");
                                                if (attlines[0].startsWith("X-Attachment")) {
                                                    attachment = "";
                                                    for (int i = 1; i < attlines.length; i++) {
                                                        if (!attlines[i].contains("-")) {
                                                            attachment += attlines[i] + "\n";
                                                            //                                                                                           Main.mainwindow += ";;" +attlines[i] + "\n"; // debug
                                                        }
                                                    }
                                                } else {
                                                    attachment = "";
                                                    for (int i = 0; i < attlines.length; i++) {
                                                        if (!attlines[i].contains("-")) {
                                                            attachment += attlines[i] + "\n";
                                                            //                                                                                           Main.mainwindow += ";;" +attlines[i] + "\n"; // debug
                                                        }
                                                    }
                                                }
                                                if (Main.Separator.equals("\\")) {
                                                    attachmentFilename = attachmentFilename.substring(0, attachmentFilename.length() - 1);
                                                }
                                                boolean success = Base64.decodeToFile(attachment, attachmentFilename);
                                                if (success) {
                                                    Main.q.Message("File stored in " + attachmentFilename, 10);
                                                    Main.mainwindow += "File stored in " + attachmentFilename + "\n";
                                                } else {
                                                    Main.q.Message("File not stored in " + attachmentFilename, 10);
                                                    Main.mainwindow += "File not stored in " + attachmentFilename + "?\n";
                                                }
                                            } catch (Exception e) {
                                                Main.q.Message("Problem with decoding, " + e, 10);
                                            }
                                        }
                                    }
                                }
                            } else {
                                outstr += s + "\n";  // write to Inbox
                            }
                        }

                    } // end while
                    fr.close();
                    this.inbox = new FileWriter(Main.HomePath + Main.Dirprefix + "Inbox", true);
                    inbox.write("From " + From + " " + Date + "\n");
                    inbox.write("From: " + From + "\n");
                    if (Date != null) {
                        inbox.write("Date: " + Date + "\n");
                    }
                    inbox.write("Subject: " + Sub + "\n");
                    // write message body
                    if (outstr != null) {
                        inbox.write(outstr + "\n");
                    }
                    inbox.flush();
                    inbox.close();
                    Main.mainui.refreshInbox();

                    File fl = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    if (fl.exists()) {
                        fl.delete();
                    }
                    File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                    if (pending.exists()) {
                        pending.delete();
                    }
                    if (Main.protocol > 0) {
                        Main.TX_Text += "~FA:" + Transaction + "\n";
                    }
                    Main.Progress = 0;
                    Main.q.Message("Added to mbox queue", 10);
                }
                // compressed web pages download
                if (CwwwDownload) {
                    CwwwDownload = false;
                    Main.comp = false;
                    try {
                        this.dlFile.close();
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the download file.", ex, true);
                    }
                    try {
                        try {
                            Base64.decodeFileToFile(Main.HomePath + Main.Dirprefix + "TempFile", Main.HomePath + Main.Dirprefix + "TMP.gz");
                        } catch (Exception ex) {
                            Main.log.writelog("Error when trying to B64-decode the download file.", ex, true);
                        }
                        try {
                            Unzip.Unzip(Main.HomePath + Main.Dirprefix + "TMP.gz");
                        } catch (Exception exz) {
                            Main.log.writelog("Error when trying to unzip the download file.", exz, true);
                        }
                        try {
                            BufferedReader in = new BufferedReader(new FileReader(Main.HomePath + Main.Dirprefix + "TMP"));
                            String str2;
                            while ((str2 = in.readLine()) != null) {
                                Main.mainwindow += (str2 + "\n");
                            }
                            in.close();
                        } catch (IOException e) {
                            Main.q.Message("problem decoding B64 file", 10);
                        }
                        File tmp1 = new File(Main.HomePath + Main.Dirprefix + "TMP");
                        if (tmp1.exists()) {
                            tmp1.delete();
                        }
                        File tmp = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                        if (tmp.exists()) {
                            tmp.delete();
                        }
                        if (Main.protocol > 0) {
                            Main.TX_Text += "~FA:" + Transaction + "\n";
                        }
                        Main.Progress = 0;
                    } catch (Exception exc) {
                        Main.log.writelog("Error handling the download file.", exc, true);
                    } catch (NoClassDefFoundError exp) {
                        Main.q.Message("problem decoding B64 file", 10);
                    }
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    boolean success = tmp.delete();
                    try {
                        if (pFile != null) {
                            Main.sm.pFile.close();
                            Main.sm.Trfile.delete();
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the pending file.", ex, true);
                    }
                    Main.Progress = 0;
                }
                // web pages   download                                      
                if (WWWDownload) {
                    WWWDownload = false;
                }
                Main.q.Message("done...", 10);
                Main.Progress = 0;
                Transaction = "";

            } else if (me.group(1).equals("-abort-")) {
                foundMatchingCommand = true;
                if (Headers) {
                    Headers = false;
                    DataReceived = 0;
                    Main.DataSize = Integer.toString(0);
                    try {
                        this.headers.close();
                        Main.mainui.refreshEmailGrid();
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the headers file.", ex, true);
                    }
                }
                if (FileList) {
                    FileList = false;
                }
                if (FileDownload) {
                    FileDownload = false;
                    Main.comp = false;
                    try {
                        this.dlFile.close();
                        File df = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                        if (df.exists()) {
                            boolean scs = df.delete();
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the download file.", ex, true);
                    }
                    try {
                        File tmp = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                        if (tmp.exists()) {
                            tmp.delete();
                        }
                        Main.TX_Text += "~FA:" + Transaction + "\n";
                        Main.Progress = 0;
                    } catch (Exception exc) {
                        Main.log.writelog("Error when trying to decode the downoad file.", exc, true);
                    } catch (NoClassDefFoundError exp) {
                        Main.q.Message("problem decoding B64 file", 10);
                    }
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    boolean success = tmp.delete();
                    try {
                        if (pFile != null) {
                            Main.sm.pFile.close();
                            Main.sm.Trfile.delete();
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the pending file.", ex, true);
                    }
                    Main.Progress = 0;
                    //                                                    Main.log(ThisFile + " received");
                }
                // messages  download      - append tmpmessage to Inbox in mbox format
                if (MsgDownload) {
                    MsgDownload = false;
                    this.tmpmessage.close();
                    // append to Inbox file
                    FileReader fr = new FileReader(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    File fl = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    if (fl.exists()) {
                        fl.delete();
                    }
                }
                // compressed messages  download or compressed Email upload
                if (CMsgDownload | CompressedEmailUpload) {
                    CMsgDownload = false;
                    CompressedEmailUpload = false;
                    Main.comp = false;
                    tmpmessage.close();
                    // append to Inbox file
                    File fl = new File(Main.HomePath + Main.Dirprefix + "tmpmessage");
                    if (fl.exists()) {
                        fl.delete();
                    }
                    File pending = new File(Main.HomePath + Main.Dirprefix + "Pending" + Main.Separator + Transaction);
                    if (pending.exists()) {
                        pending.delete();
                    }
                }
                // compressed web pages download
                if (CwwwDownload) {
                    CwwwDownload = false;
                    Main.comp = false;
                    try {
                        this.dlFile.close();
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the download file.", ex, true);
                    }
                    File tmp = new File(Main.HomePath + Main.Dirprefix + "TempFile");
                    boolean success = tmp.delete();
                    try {
                        if (pFile != null) {
                            Main.sm.pFile.close();
                            Main.sm.Trfile.delete();
                        }
                    } catch (IOException ex) {
                        Main.log.writelog("Error when trying to close the pending file.", ex, true);
                    }
                    Main.Progress = 0;
                }
                // web pages   download
                if (WWWDownload) {
                    WWWDownload = false;
                }
                Main.q.Message("done...", 10);
                Main.Progress = 0;
                Transaction = "";
            }
        }
        // NNNN 
// fleetcodes
        if (!Main.Connected & Main.IACmode) {
            Pattern pn = Pattern.compile("<SOH>(NNNN)");
            Matcher mn = pn.matcher(str);
            if (mn.lookingAt()) {
                if (mn.group(1).equals("NNNN")) {
                    Main.IACmode = false;
                    Main.q.Message("End of code...", 10);
                    Main.Status = "Listening";
                    try {
                        iacout.close();
                    } catch (Exception e) {
                        Main.log.writelog("Error closing the iac file.", e, true);
                    }
                    f.fastfec2(Main.HomePath + Main.Dirprefix + "iactemp", "");
                    deleteFile("iactemp");
                }
            }
        }
        // bulletin                               
        if (!Main.Connected & Main.Bulletinmode) {
            Pattern pn = Pattern.compile("<SOH>(NNNN)");
            Matcher mn = pn.matcher(str);
            if (mn.lookingAt()) {
                if (mn.group(1).equals("NNNN")) {
                    Main.Bulletinmode = false;
                    Main.q.Message("End of bulletin...", 2);
                }
            }
        }

        // write headers
        if (Headers & !Firstline) {
            Pattern phd = Pattern.compile("^(\\s*\\d+.*)");
            Matcher mhd = phd.matcher(str);
            if (mhd.lookingAt()) {
                String outToWindow = mhd.group(1) + "\n";
                Main.Mailheaderswindow += outToWindow;
                DataReceived += outToWindow.length();
                if (DataSize > 0) {
                    Main.Progress = 100 * DataReceived / DataSize;
                }
                try {
                    this.headers.write(str + "\n");
                } catch (IOException ex) {
                    Main.log.writelog("Error when trying to write to headers file.", ex, true);
                }
            }
        }
        // files list
        if (FileList & !Firstline) {
            Main.FilesTextArea += str + "\n";
            DataReceived += str.length();
            Main.Progress = 100 * DataReceived / DataSize;
        }
        // write file
        if (FileDownload & !Firstline) {
            Main.FilesTextArea += str + "\n";
            DataReceived += str.length();
            if (DataSize > 0) {
                Main.Progress = 100 * DataReceived / DataSize;
            }
            try {
                if (pFile != null) {
                    pFile.write(str + "\n");
                    pFile.flush();
                }
            } catch (IOException ex) {
//                                                Main.log.writelog("Error when trying to write to pending file.", ex, true);
            }
            try {
                this.dlFile.write(str + "\n");
            } catch (IOException ex) {
//                                                Main.log.writelog("Error when trying to write to download file.", ex, true);
            }
        }
// messages                                    
        if (MsgDownload & !Firstline) {
            foundMatchingCommand = true;
            DataReceived += str.length();
            Main.Progress = 100 * DataReceived / DataSize;
            this.tmpmessage.write(str + "\n");
        }

        // compressed messages download or compressed Email upload                                   
        if ((CMsgDownload | CompressedEmailUpload) & !Firstline) {
            foundMatchingCommand = true;
            DataReceived += str.length();
            Main.Progress = 100 * DataReceived / DataSize;

            try {
                if (pFile != null) {
                    pFile.write(str + "\n");
                    pFile.flush();
                }
            } catch (IOException ex) {
                //Main.log.writelog("Error when trying to write to pending file.", ex, true);
                Main.q.Message("Error writing pending file.", 1);
            }
            try {
                tmpmessage.write(str + "\n");
                tmpmessage.flush();
            } catch (IOException ex) {
                Main.log.writelog("Error when trying to write to tmpmessage file.", ex, true);
            }
        }
// compressed www pages
        if (CwwwDownload & !Firstline) {
            foundMatchingCommand = true;
            DataReceived += str.length();
            Main.Progress = 100 * DataReceived / DataSize;
            try {
                if (pFile != null & str.length() > 0) {
                    pFile.write(str + "\n");
                    pFile.flush();
                }
            } catch (IOException ex) {
                //Do nothing as it happens normally when a compressed download is aborted and restarted without closing the app (init issue?)
                //Main.log.writelog("Error when trying to write to pending file.", ex, true);
            }
            try {
                if (str.length() > 0) {
                    dlFile.write(str + "\n");
                    dlFile.flush();
                }
            } catch (IOException ex) {
                //Do nothing as it happens normally when a compressed download is aborted and restarted without closing the app (init issue?)
                //Main.log.writelog("Error when trying to write to tmpmessage file.", ex, true);
            }
        }

        // www pages       WWWDownload     
        if (WWWDownload & !Firstline) {
            foundMatchingCommand = true;
            DataReceived += str.length();
//                                        double ProGress = 100 * DataReceived / DataSize;
            if (DataSize > 0) {
                Main.Progress = 100 * DataReceived / DataSize;
            }
        }
        // iac fleetcode file     
//                                    debug (Integer.toString(str.length()));

        if (!Main.Connected & Main.IACmode & str.length() > 0) {
            try {
                iacout.write(str + "\n");
                iacout.flush();
                Main.mainwindow += str + "\n";
            } catch (IOException exc) {
                Main.log.writelog("Error when trying to write to download file.", exc, true);
            }
        }
        //Do we have an unknown command?
        if (!foundMatchingCommand && Main.TTYConnected.equals("Connected")) {
            Pattern pmc = Pattern.compile("^(\\s*\\~[A-Z]{2,}.*)");
            Matcher mmc = pmc.matcher(str);
            if (mmc.lookingAt()) {
                Main.TX_Text = "\nI don't understand:" + str + "\n";
            }
        }
    }

    /**
     * Move a file. Used to save sent email to the sent folder
     *
     * @param from file name and path, relative path
     * @param todir The new folder
     * @return
     */
    private boolean MoveFile(String from, String todir) {
        try {
            boolean res = false;
            // File  to be moved
            File file = new File(from);
            // Destination directory
            File dir = new File(todir);
            // Move file to new directory
            res = file.renameTo(new File(dir, file.getName()));

            return res;
        } catch (Exception e) {
            Main.log.writelog("Could not move file.", e, true);
            return false;
        }
    }

    public boolean TransactionsExists() {
        boolean result = false;
        String Tr = Main.HomePath + Main.Dirprefix + "Transactions";
        File Transactions = new File(Tr);
        if (Transactions.exists()) {
            result = true;
        }
        return result;
    }

    //Reset private variable Blocklength to middle value of 5 (32 bytes block size). Used when changing modes up and down.
    public void SetBlocklength(int NewBlocklength) {
        Blocklength = NewBlocklength;
    }

    public String doTXbuffer() {

        String Outbuffer = "";
        int nr_missing = tx_missing.length();
        int b[] = new int[nr_missing];  // array of missing blocks
        int i;
        for (i = 0; i < nr_missing; i++) {
            b[i] = (int) tx_missing.substring(i, i + 1).charAt(0) - 32;
        }
        if (tx_missing.length() > 2) {
//With adaptive modes, blocksize of 8 is not effective. Minimum of 16 (2 ** 4)
//  if (Blocklength > 3) {
            if (Blocklength > 4) {
                Blocklength--;
            }
        } else if (tx_missing.length() < 1) {
            if (Blocklength < 6) {
                Blocklength++;
            }
        } else {
            Blocklength = 5;
        }

// add missing blocks
        if (nr_missing > 0) {
            for (i = 0; i < nr_missing; i++) {
                String block = TX_addblock(b[i]);
                Outbuffer += block;
            }
        }

        i = 0;
        Blocklength = Integer.parseInt(Main.TXblocklength);
        int Maxblocklength = 6;
        int Minblocklength = 3;
        int Nrblocks = 8;
        String TXmd = Main.m.getTXModemString(Main.TxModem);
        // System.out.println(TXmd)  ;
        if (TXmd.equals("PSK500")) {
            Nrblocks = 16;
            Maxblocklength = 6;
            Minblocklength = 4;
        } else if (TXmd.equals("PSK500R")) {
            Nrblocks = 8;
            Maxblocklength = 6;
            Minblocklength = 4;
        } else if (TXmd.equals("PSK250R")) {
            Nrblocks = 8;
            Maxblocklength = 5;
            Minblocklength = 4;
        } else {
            Nrblocks = 4;
            Maxblocklength = 5;
            Minblocklength = 3;
        }
        while (i < (Nrblocks - nr_missing) & Main.TX_Text.length() > 0) {
            String newstring = "";
            if (Blocklength < Minblocklength) {
                Blocklength = Minblocklength;
            } else if (Blocklength > Maxblocklength) {
                Blocklength = Maxblocklength;
            }
            double bl = Math.pow(2, Blocklength);
            int queuelen = Main.TX_Text.length();

            if (queuelen > 0) {
                if (queuelen <= (int) bl) {
                    newstring = Main.TX_Text;
                    Main.TX_Text = "";
                } else {
                    newstring = Main.TX_Text.substring(0, (int) bl);
                    Main.TX_Text = Main.TX_Text.substring((int) bl);
                }

//            lastqueued += 1;
//            if (lastqueued > 63) {
//                lastqueued = 0;
//            }
                txbuffer[lastqueued] = newstring;

                for (int j = lastqueued + 17; j < lastqueued + 25; j++) {
                    txbuffer[j % 64] = "";
                }
            }

            String block = TX_addblock(lastqueued);

            char lasttxchar = (char) (lastqueued + 32);
            rx_lastsent = Character.toString(lasttxchar);
            Main.myrxstatus = getTXStatus();
            Outbuffer += block;
            i++;
            lastqueued += 1;
            if (lastqueued > 63) {
                lastqueued = 0;
            }
        }

        return Outbuffer;
    }

    public String TX_addblock(int nr) {
        if (txbuffer[nr].length() > 0) {
            char c = (char) (nr + 32);
            String accum = "0";
            accum += Main.session;
            accum += Character.toString(c);
            accum += Session.txbuffer[nr];
            String blcheck = arq.checksum(accum);
            accum += blcheck;
            return accum + (char) 1;
        } else {
            return "";
        }
    }

    /**
     * Add a line to Main.TX_Text but make sure only one instance gets added
     *
     * @param command
     */
    void addSendCommand(String command) {
        String mymirror = Main.TX_Text;
        if (!mymirror.contains(command)) {
            Main.TX_Text += command;
        }
    }

    void sendQTC(String mailnr) {
        //Main.TX_Text += "~QTC " + mailnr + "+\n";   
        addSendCommand("~QTC " + mailnr + "+\n");
    }

    void sendDelete(String numbers) {
        //Main.TX_Text += "~DELETE " + numbers + "\n";
        addSendCommand("~DELETE " + numbers + "\n");
    }

    void sendRead(String mailnr) {
        if (Main.compressedmail) {
            //Main.TX_Text += "~READZIP " + mailnr + "\n";
            addSendCommand("~READZIP " + mailnr + "\n");
        } else {
            //Main.TX_Text += "~READ " + mailnr + "\n";   
            addSendCommand("~READ " + mailnr + "\n");
        }
    }

    public void makeFile(String filename) {
        String fileName = Main.HomePath + Main.Dirprefix + filename;
        File fl = new File(fileName);
        try {
            boolean success = fl.createNewFile();
        } catch (IOException e) {
            Main.log.writelog("Error creating headers file:", e, true);
        }
    }

    public void deleteFile(String filename) {
        String fileName = Main.HomePath + Main.Dirprefix + filename;
        // A File object to represent the filename
        File fl = new File(fileName);

        // Make sure the file or directory exists and isn't write protected
        try {
            if (!fl.exists()) {
                throw new IllegalArgumentException("Delete: Does not exist: "
                        + fileName);
            }

            if (!fl.canWrite()) {
                throw new IllegalArgumentException("Delete: write protected: "
                        + fileName);
            }

            // If it is a directory, make sure it is empty
            if (fl.isDirectory()) {
                String[] files = fl.list();
                if (files.length > 0) {
                    throw new IllegalArgumentException(
                            "Delete: directory not empty: " + fileName);
                }
            }

            // Attempt to delete it
            boolean success = fl.delete();

            if (!success) {
                throw new IllegalArgumentException("Delete: deletion failed");
            }
        } catch (IllegalArgumentException e) {
            Main.log.writelog("Error deleting headers file:", e, true);
        }

    }

    /**
     * Method used to save attachments
     *
     * @param Filename Filename including path
     * @param attachment The content
     * @param B64 True if its encoded and should be decoded
     */
    private void SaveAttachmentToFile(String Filename, String attachment, Boolean B64) {

        try {

            // Make sure there is a filename, just exit if not
            if (Filename.length() < 1) {
                return;
            }

            // First make sure we have a directory to save the attachments to
            File myfiles = new File(Main.HomePath + Main.Dirprefix + "Files");
            if (!myfiles.isDirectory()) {
                myfiles.mkdir();
            }

            // remove first line if "X-Attachment..."
            String[] attlines = attachment.split("\n");
            if (attlines[0].startsWith("X-Attachment")) {
                attachment = "";
                for (int i = 1; i < attlines.length; i++) {
                    attachment += attlines[i] + "\n";
                    // Main.mainwindow += "::" +attlines[i] + "\n"; // debug
                }
            } else {
                attachment = "";
                for (int i = 0; i < attlines.length; i++) {
                    attachment += attlines[i] + "\n";
                    Main.mainwindow += ";;" + attlines[i] + "\n"; // debug
                }
            }

            if (!B64) {
                // Save to a file
                FileWriter fstream = new FileWriter(Filename);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(attachment);
                //Close the output stream
                out.close();
                Main.q.Message("File stored in " + Filename, 10);
                Main.mainwindow += "File stored in " + Filename + "\n";
            } else {
                boolean success = Base64.decodeToFile(attachment, Filename);
                if (success) {
                    Main.q.Message("File stored in " + Filename, 10);
                    Main.mainwindow += "File stored in " + Filename + "\n";
                } else {
                    Main.q.Message("File not stored in " + Filename, 10);
                    Main.mainwindow += "File not stored in " + Filename + "?\n";
                }
            }
        } catch (Exception e) {
            Main.log.writelog("Had trouble saving the attachment. " + e.getMessage().toString(), e, true);
            Main.q.Message("Problem with decoding, " + e, 10);
        }
    }

    /**
     * read last header number from headers file
     *
     * @param filename
     * @return
     */
    public String getHeaderCount(String filename) {
        FileReader hdr = null;
        String Countstr = "0";
        File fh = new File(filename);

        if (!fh.exists()) {
            return "1";
        }
        try {
            hdr = new FileReader(fh);
            BufferedReader br = new BufferedReader(hdr);
            String s;
            while ((s = br.readLine()) != null) {
                //===================================
                Pattern ph = Pattern.compile("^\\s*(\\d+)");
                Matcher mh = ph.matcher(s);
                if (mh.lookingAt()) {
                    Countstr = mh.group(1);
                    int Count = Integer.parseInt(Countstr);
                    Count++;
                    Countstr = Integer.toString(Count);

                }
//=====================================                        
            }
            br.close();
        } catch (IOException e) {
            Main.log.writelog("Error when trying to read the headers file.", e, true);
        }
        return Countstr;
    }

    private String convert_to_aprsformat(String Tag, String latstring, String lonstring, String statustxt, String Icon) {
        String returnframe = "";
        try {
            float latnum = 0;
            float lonnum = 0;
            String latsign = "N";
            String lonsign = "E";
            String course = "0";
            String speed = "0";

            String callsign = Main.configuration.getPreference("CALL");
            statustxt = Main.configuration.getPreference("STATUS");

            latnum = Float.parseFloat(latstring);
            lonnum = Float.parseFloat(lonstring);
            if (latnum < 0) {
                latnum = Math.abs(latnum);
                latsign = "S";
            }
            if (lonnum < 0) {
                lonnum = Math.abs(lonnum);
                lonsign = "W";
            }

            DecimalFormat twoPlaces = new DecimalFormat("##0.00");
            int latint = (int) latnum;
            int lonint = (int) lonnum;

            latnum = ((latnum - latint) * 60) + latint * 100;
            latstring = twoPlaces.format(latnum);
            latstring = "0000" + latstring;
            int len = latstring.length();
            if (len > 6) {
                latstring = latstring.substring(len - 7, len);
            }

            // Make sure there is a period in there
            latstring = latstring.replace(",", ".");

            lonnum = ((lonnum - lonint) * 60) + lonint * 100;
            lonstring = twoPlaces.format(lonnum);
            lonstring = "00000" + lonstring;

            len = lonstring.length();
            if (len > 7) {
                lonstring = lonstring.substring(len - 8, len);
            }

            //make sure we have a period there
            lonstring = lonstring.replace(",", ".");
//       System.out.println("|" + Icon + "|") ;
            if (Icon.equals("P90")) {
                Icon = "y";
            } else if (Icon.equals("P14")) {
                Icon = "-";
            } else if (Icon.equals("S07")) {
                Icon = "&";
            } else if (Icon.equals("P31")) {
                Icon = ">";
            } else if (Icon.equals("P42")) {
                Icon = "I";
            } else if (Icon.equals("P87")) {
                Icon = "v";
            } else if (Icon.equals("P83")) {
                Icon = "r";
            } else if (Icon.equals("P82")) {
                Icon = "s";
            } else if (Icon.equals("P56")) {
                Icon = "Y";
            } else if (Icon.equals("P52")) {
                Icon = "U";
            } else if (Icon.equals("P26")) {
                Icon = ";";
            } else if (Icon.equals("P60")) {
                Icon = "[";
            } else if (Icon.equals("P78")) {
                Icon = "o";
            } else if (Icon.equals("P02")) {
                Icon = "#";
            } else if (Icon.equals("S04")) {
                Icon = "#";
            } else if (Icon.equals("P86")) {
                Icon = "k";
            } else if (Icon.equals("P87")) {
                Icon = "k";
            } else if (Icon.equals("P64")) {
                Icon = "_";
            } else if (Icon.equals("S87")) {
                Icon = "n";
            } else if (Icon.equals("P13")) {
                Icon = ",";
            } else {
                Icon = "D";
            }

            // Fix stream id, this is wrong
            returnframe = Tag + ">PSKAPR,qAs," + callsign + "*:!" + latstring + latsign + Main.ICONlevel + lonstring + lonsign + Icon;

            return returnframe;
            //           }
        } catch (Exception ex) {
            Main.log.writelog("Error when creating beaconblock", ex, true);
        }

        return returnframe;
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return tmp / p;
    }

    public void debug(String message) {
        System.out.println("Debug:" + message);
    }

    /**
     * Move a file to a new folder. Used for example for moving from Outbox to
     * Sent
     *
     * @param thefile The file to move
     * @param folder Where to move it, complete path
     */
    private boolean movefiletodir(File thefile, String folder) {
        try {
            // Destination directory
            File dir = new File(folder);

            // If that folder does not exist then create it
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Move file to new directory
            boolean success = thefile.renameTo(new File(dir, thefile.getName()));
            if (!success) {
                Main.log.writelog("File was not successfully moved.", true);
            }
            return success;
        } catch (Exception e) {
            Main.log.writelog("Error when trying to move a file.", e, true);
            return false;
        }
    }

} // end of class

