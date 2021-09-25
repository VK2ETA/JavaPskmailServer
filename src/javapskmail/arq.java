/*
 * arq.java
 *
 * Copyright (C) 2008 Pär Crusefalk (SM0RWO)
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Per Crusefalk <per@crusefalk.se>
 */
public class arq {

    // Frame handling variables
    char Unproto = 'u';
    char Connect = 'c';
    char Summon = 'n';
    char Status = 's';
    char Abort = 'a';
    char Acknowledge = 'k';
    char Reject = 'r';
    String Streamid;
    char SOC = (char) 26; // Start of command
    char EOC = (char) 27; // End of command
    char NUL = (char) 0; // Null character
    char StartHeader = (char) 1;                    // <soh>, start of frame
    String FrameEnd = "" + (char) 4 + (char) 10 + "    ";     // <eot> + lf, end of frame
    char sendstring = (char) 31;                    // <us>, used instead of dcd. Sent first
    int Lastblockinframe = 0;

    // Common objects
    String path = Main.HomePath + Main.Dirprefix;
    config cf = new config(path); // Configuration object
    public String callsign = cf.getCallsign();
    //Pskmail Server
    public String callsignAsServer = cf.getCallsignAsServer();
    //public String servercall = cf.getServer();
    private String servercall = "NOCALL"; //Set to default no-call
    private String serverPassword = ""; //Default to blank (= no password)
    public String statustxt = cf.getStatus();
    public String backoff = cf.getBlocklength();
    static private Modem m;
    private String summonsfreq = "0";
    private String reqCallSign = ""; //Requester's call sign, typically a client making an inquiry

    String Modem = "PSK500R";    // Current modem mode
    modemmodeenum mode = modemmodeenum.PSK500R;

    //Counts the number of received RSID for frequency sensitive modes like MFSK16 so that we can decide to send an RSID to re-align the server's RX
    private int rxRsidCounter = 0;

    /**
     *
     * @param incall
     */
    public void setCallsign(String incall) {
        callsign = incall;
    }
    /**
     *
     * @param reqCallSign
     */
    public void setReqCallsign(String reqCallSign) {
        this.reqCallSign = reqCallSign;
    }

    /**
     *
     * @param server
     */
    public void setServerAndPassword(String server) {
        this.servercall = server;
        boolean foundCall = false;
        for (int i = 0; i <  Main.MAXNEWSERVERS; i++) {
            if (Main.Servers[i].toLowerCase().equals(this.servercall.toLowerCase())) {
                foundCall = true;
                serverPassword = Main.ServersPasswords[i];
            }
        }
        if (!foundCall) {
            serverPassword = "";
        }
    }

    /**
     * Get the current server to link to
     *
     * @return server callsign
     */
    public String getServer() {
        return servercall;
    }

    //Get the current server's password if any
    public String getPassword() {
        return serverPassword;
    }

    //Set the current server's password
    public void setPassword(String password) {
        this.serverPassword = password;
    }

    /**
     *
     * @param intext
     */
    public void setTxtStatus(String intext) {
        statustxt = intext;
    }

    /* Status enums */
    txstatus txserverstatus;

    /**
     * /
     * Set the tx status, extremly important this gets set the right way
     *
     * @param tx
     */
    public void set_txstatus(txstatus tx) {
        this.txserverstatus = tx;
    }

    public void Message(String msg, int time) {
        Main.Statusline = msg;
        Main.StatusLineTimer = time;
    }

    /**
     *
     */
    public arq() {
        String mypath = Main.HomePath + Main.Dirprefix;
        config ca = new config(mypath);
        callsign = ca.getCallsign();
    }

    public String getAPRSMessageNumber() {
        Main.APRSMessageNumber++;
        if (Main.APRSMessageNumber > 99) {
            Main.APRSMessageNumber = 0;
        }
        String outnumber = Integer.toString(Main.APRSMessageNumber);
        if (Main.APRSMessageNumber < 10) {
            outnumber = "0" + outnumber;
        }
        return "{" + outnumber;
    }

    public String getLastAPRSMessageNumber() {
        String outnumber = Integer.toString(Main.APRSMessageNumber);
        if (Main.APRSMessageNumber < 10) {
            outnumber = "0" + outnumber;
        }
        return "{" + outnumber;
    }

    /**
     *
     * @param outmessage
     */
    public void sendit(String outmessage) {
        String sendtext = "";
        String montext = outmessage.substring(1, outmessage.length() - 6);

        sendtext += outmessage;
        Main.Sendline = Main.ModemPreamble + sendtext + Main.ModemPostamble;

        try {
            while (Main.monmutex) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Modem.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Main.monitor += "\n*TX*  " + "<SOH>" + montext + "<EOT>\n";
        } catch (Exception ex) {
            Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * /
     * Create UI message, used for pskaprs email among things
     *
     * @param intext
     * @return
     */
    private String ui_messageblock(String intext) {
        String returnframe = "";
        returnframe = "00" + Unproto + callsign + ":25 " + intext + "\n";
        return returnframe;
    }

    private String ui_aprsblock(String intext) {
        String returnframe = "";
        returnframe = "0" + "0" + Unproto + callsign + ":26 " + intext;
        return returnframe;
    }

    private String pingblock() {
        String returnframe = "";
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        returnframe = "00" + Unproto + callsign + ":7 ";
        return returnframe;
    }

    private String replyPingblock() {
        String returnframe = "";
        String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
        returnframe = "00" + Unproto + serverCall + ":71 " + (int) Main.snr + " ";
        return returnframe;
    }

    //Send an Inquire block
    private String inquireblock() {
        String returnframe = "";
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        returnframe = "00" + Unproto + callsign + ":8 " + Main.mainui.getcboServer() + " ";
        return returnframe;
    }

    //Reply to an Inquire block
    private String replyInquireblock() {
        String returnframe = "";
        String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
        //Get mail count
        int mailCount = serverMail.getMailCount();
        //my $QSL = sprintf("%cQSL %s de %s %s %d:%d",0x01, $1, $ServerCall, $s2n, $mls, $mlcount);
        returnframe = "00" + Unproto + "QSL " + reqCallSign + " de " + serverCall + " " + (int) Main.snr + " " + mailCount + " ";
        return returnframe;
    }

    //Reply with a QSL block (when I am a server and I received an APRS message, beacon or short email)
    private String replyQSLblock() {
        String returnframe = "";
        String serverCall = Main.configuration.getPreference("CALLSIGNASSERVER");
        returnframe = "00" + Unproto + "QSL de " + serverCall + " " + (int) Main.snr + " ";
        return returnframe;
    }

    private String cqblock() {
        String returnframe = "";
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        returnframe = "00" + Unproto + callsign + ":27 CQ CQ CQ PSKmail ";
        return returnframe;
    }

    private String ui_linkblock() {
        String returnframe = "";
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        //servercall = Main.configuration.getPreference("SERVER");
        returnframe = "00" + Unproto + callsign + "><" + servercall + " ";
        return returnframe;
    }

    private String ui_beaconblock() {
        String returnframe = "";
        try {
            String latstring = "0000.00";
            String lonstring = "00000.00";
            float latnum = 0;
            float lonnum = 0;
            String latsign = "N";
            String lonsign = "E";
            String course = "0";
            String speed = "0";

            callsign = Main.configuration.getPreference("CALL");
            statustxt = getStatusString();

            // Get the GPS position data or the preset data
            if (Main.gpsdata.getFixStatus()) {
                if (!Main.HaveGPSD) {
                    latstring = Main.gpsdata.getLatitude();
                    lonstring = Main.gpsdata.getLongitude();
                } else {
                    latstring = Main.GPSD_latitude;
                    lonstring = Main.GPSD_longitude;
                }
                course = Main.gpsdata.getCourse();
                speed = Main.gpsdata.getSpeed();
            } else {
                // Preset data
                latstring = Main.configuration.getPreference("LATITUDE");
                lonstring = Main.configuration.getPreference("LONGITUDE");
            }
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

            if (Main.CBeacon) {
                int latdegrees = (int) latnum;
                float latminutes = Round(((latnum - latdegrees) * 60), 2);
                int latminuteint = (int) latminutes;
                int latrest = (int) ((latminutes - latminuteint) * 100);
                int londegrees = (int) lonnum;
                float lonminutes = Round(((lonnum - londegrees) * 60), 2);
                int lonminuteint = (int) lonminutes;
                int lonrest = (int) ((lonminutes - lonminuteint) * 100);

                int flg = 0;
                int courseint = Integer.parseInt(course);
                int speedint = Integer.parseInt(speed);

                if (latsign.equals("N")) {
                    flg += 8;
                }
                if (lonsign.equals("E")) {
                    flg += 4;
                }
                if (courseint > 179) {
                    courseint -= 180;
                    flg += 32;
                }
                courseint /= 2;
                if (speedint > 89) {
                    speedint -= 90;
                    flg += 16;
                }
                if (londegrees > 89) {
                    londegrees -= 90;
                    flg += 2;
                }
                if (Main.gpsdata.getFixStatus()) {
                    flg += 1;
                }
                flg += 32;
                latdegrees += 32;
                latminuteint += 32;
                latrest += 32;
                londegrees += 32;
                lonminuteint += 32;
                lonrest += 32;
                courseint += 32;
                speedint += 32;
                int stdmsg = 0;

                Pattern pw = Pattern.compile("^\\s*(\\d+)(.*)");
                Matcher mw = pw.matcher(statustxt);
                if (mw.lookingAt()) {
                    stdmsg = Integer.parseInt(mw.group(1));
                    statustxt = mw.group(2);
                }
                stdmsg += 32;
// System.out.println(Main.Icon);               
                return "00" + Unproto + callsign + ":6 " + (char) flg + (char) latdegrees
                        + (char) latminuteint + (char) latrest + (char) londegrees
                        + (char) lonminuteint + (char) lonrest + (char) courseint + (char) speedint
                        + Main.Icon + (char) stdmsg + statustxt;

            } else {
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

                // Fix stream id, this is wrong
                if (Main.gpsdata.getFixStatus()) {
                    returnframe = "00" + Unproto + callsign + ":26 " + "!" + latstring + latsign + Main.ICONlevel + lonstring + lonsign + Main.Icon
                            + course + "/" + speed + "/" + statustxt;
                } else {
                    returnframe = "00" + Unproto + callsign + ":26 " + "!" + latstring + latsign + Main.ICONlevel + lonstring + lonsign + Main.Icon + statustxt;
                }

                return returnframe;
            }
        } catch (Exception ex) {
            Main.log.writelog("Error when creating beaconblock", ex, true);
        }

        return returnframe;
    }

    private String connectblock() {
        String returnframe = "";
        int j = 0;
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        callsign = callsign.trim();
        //servercall = Main.configuration.getPreference("SERVER");
        servercall = servercall.trim();
        String defmode = Main.configuration.getPreference("DEFAULTMODE");

        Main.CurrentModemProfile = backoff;

//    System.out.println("MODES:" + Main.modes);
        Main.m.setcurrentmodetable(Main.modes);

        for (int i = 0; i < Main.Modes.length - 1; i++) {
//               if (Main.RxModemString .equals(Main.Modes[i])) {           
//             if (defmode.equals(Main.Modes[i])) {
            if (Main.LastRxModem.equals(Main.Modes[i])) {
                backoff = Main.modes.substring(Main.modes.length() - i, Main.modes.length() - i + 1);
                j = i;
                break;
            }
        }

//         String Rxmodem = Main.RxModemString;
//         Main.RxModemString = Main.Modes[j];
// System.out.println(Main.RxModemString);
        if (Main.summoning) {
            String curfreq = "0";
            if (Rigctl.opened) {
                int fr = Integer.parseInt(Main.CurrentFreq) + Rigctl.OFF;
                curfreq = Integer.toString(fr);
            } else {
                int fr = Integer.parseInt(mainpskmailui.ClientFreqTxtfield.getText());
                curfreq = Integer.toString(fr);
            }
            returnframe = "10" + Summon + callsign + ":1024 " + servercall + ":24 " + curfreq + " " + backoff + Main.modes;
        } else {
            returnframe = "10" + Connect + callsign + ":1024 " + servercall + ":24 " + backoff + Main.modes;
        }
        return returnframe;
    }

    private String summonblock() {
        String returnframe = "";
        String curfreq = "0";
        // Fix stream id, this is wrong
        callsign = Main.configuration.getPreference("CALL");
        callsign = callsign.trim();
        //servercall = Main.configuration.getPreference("SERVER");
        //servercall = servercall.trim();
        backoff = Main.configuration.getPreference("BLOCKLENGTH");
        Main.CurrentModemProfile = backoff;
        if (Rigctl.opened) {
            int fr = Integer.parseInt(Main.CurrentFreq) + Rigctl.OFF;
            curfreq = Integer.toString(fr);
        } else {
            int fr = Integer.parseInt(mainpskmailui.ClientFreqTxtfield.getText());
            curfreq = Integer.toString(fr);

        }
        Main.RxModemString = Main.Modes[Integer.parseInt(backoff)];
        returnframe = "00" + Summon + callsign + ":1024 " + servercall + ":24 " + curfreq + " " + backoff + Main.modes;
        return returnframe;
    }

    private String connect_ack(String server) {
        // <US><SOH>1/kPI4TUE:24 PA0R:1024 56663<SOH>0/s   60D3<EOT>
        Random generator = new Random();
        int randomIndex = generator.nextInt(64);
        // get random number between 1...63
        while (randomIndex == 0) {
            randomIndex = generator.nextInt(64) + 32;
        }

        char c = (char) (randomIndex + '0');
        Streamid = Character.toString(c);
        Main.session = Streamid;
        //This block is only used as a server, use different callsign.
        //callsign = Main.configuration.getPreference("CALL");
        callsign = Main.configuration.getPreference("CALLSIGNASSERVER");
        Main.TTYConnected = "Connecting";
        return ("1" + Streamid + Acknowledge + callsign + ":24 " + server + ":1024 5");
    }

    public boolean send_ack(String server) {
//        try {
        String info = "";
        String outstring = "";

        //VK2ETA not automatically, decided elsewhere
        //send_txrsid_command("ON");
        //test Thread.sleep(1000);
        info = connect_ack(server);
        outstring = make_block(info);
        if (Main.TTYConnected.equals("Connecting")) { //I am a TTY server
            //Need index in the array of client modes, starting at position 1
            int myIndex = Main.getClientModeIndex(Main.RxModem);
            //char sprobyte = (char) (48 + Main.getClientModeIndex(Main.RxModem));
            char sprobyte = (char) (48 + Main.TTYmodes.length() - myIndex);
            String protobyte = Character.toString(sprobyte);
            info = protobyte + Main.session + Character.toString(Status) + Main.myrxstatus;
        } else {
            info = "0" + Main.session + Character.toString(Status) + Main.myrxstatus;
        }
        outstring += make_block(info) + FrameEnd;
        Main.TXActive = false; // force transmit
        sendit(outstring);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return true;
    }

    public boolean send_disconnect() {
//        try {
        String info = "";
        String outstring = "";

        Main.m.requestTxRsid("ON");
//            Thread.sleep(1000);
        info = "0" + Streamid + "d";
        outstring += make_block(info) + FrameEnd;
        sendit(outstring);
//        } catch (InterruptedException ex) {
        //           Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return true;
    }

    public boolean send_reject(String clientCallsign, String reasonStr) {
        String info = "00" + Reject + clientCallsign + ":" + reasonStr;
// System.out.println("rejectblock:" + returnframe);
        String outstring = make_block(info) + FrameEnd;
        sendit(outstring);
        return true;
    }

    private String statusblock(String status) {
        String returnframe = "";
        // calculate quality byte

        int usedblocks = Main.Missedblocks + 8;

        int quality = (int) (Main.snr * 8 / usedblocks * 90 / 100) + 32;
        // make the protobyte
        char sprobyte = (char) quality;
        //I am a TTY server - send client's TX mode instead
        if (Main.TTYConnected.equals("Connected")) {
            //sprobyte = (char) (48 + Main.m.getModemPos(Main.RxModem));
            //Need index in the array of client modes, starting at position 1
            int myIndex = Main.getClientModeIndex(Main.RxModem);
            sprobyte = (char) (48 + Main.TTYmodes.length() - myIndex);

        }
        String protobyte = Character.toString(sprobyte);
        returnframe = protobyte + Main.session + Character.toString(Status) + status;
        return returnframe;
    }

    private String abortblock() {
        String returnframe = "";
        if (!Main.oldsession.equals("")) {
            returnframe = "0" + Main.oldsession + Character.toString(Abort);
// System.out.println("abortblock:" + returnframe);
        }
        return returnframe;
    }

    /**
     *
     * @param payload
     */
    public void send_frame(String payload) throws InterruptedException {
        String info = "";
        String outstring = "";

        switch (this.txserverstatus) {

            case TXUImessage:
                // Only UI messages at this point
                Main.m.requestTxRsid("ON");
                //              Thread.sleep(500);
                info = ui_messageblock(payload);
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXPing:
                Main.m.requestTxRsid("ON");
                info = pingblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXPingReply:
                Main.m.requestTxRsid("ON");
                info = replyPingblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXInq:
                Main.m.requestTxRsid("ON");
                //    System.out.println("INQ");
                info = inquireblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXInqReply:
                Main.m.requestTxRsid("ON");
                //    System.out.println("INQ Reply");
                info = replyInquireblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXQSLReply:
                Main.m.requestTxRsid("ON");
                //    System.out.println("QSL Reply");
                info = replyQSLblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXCQ:
                Main.m.requestTxRsid("ON");
                info = cqblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXaprsmessage:
                Main.m.requestTxRsid("ON");
                info = ui_aprsblock(payload);
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXlinkreq:
                Main.m.requestTxRsid("ON");
                //               Thread.sleep(500);
                //               send_mode_command(Main.linkmode);
                //               Thread.sleep(500);
                info = ui_linkblock();
                outstring = make_block(info) + FrameEnd;
                break;
            case TXlinkack:
                //Reply with same info as Inquire block (contains latest email count)
                // This allows for a regular link to be issued and the number of emails reported
                Main.m.requestTxRsid("ON");
                info = replyInquireblock();
                Lastblockinframe = 1;
                outstring = make_block(info) + FrameEnd;
                break;
            case TXBeacon:
                Main.m.requestTxRsid("ON");
                Thread.sleep(500);
                info = ui_beaconblock();
                outstring = make_block(info) + FrameEnd;
                break;
            case TXConnect:
                Main.m.requestTxRsid("ON");
                info = connectblock();
                //VK2ETA Now with Password is connecting to a mini-server
                //outstring = make_block(info) + FrameEnd;
                outstring = make_blockWithPassword(info) + FrameEnd;
                break;
            case TXSummon:
                int fr = Integer.parseInt(Main.ServerFreq) - Rigctl.OFF;
                Main.setFreq(Integer.toString(fr));
                Main.m.requestTxRsid("ON");
//                Thread.sleep(1000);
                info = summonblock();
                outstring = make_block(info) + FrameEnd;
                break;
            case TXAbort:
                Main.Aborting = true;
                Main.m.requestTxRsid("ON");
                Main.m.setRxRsid("ON");
                info = abortblock();
                //  System.out.println("INFO:" + info);           
                if (!info.equals("")) {
                    outstring = make_block(info) + FrameEnd;
                }
                break;
            case TXStat:
                if (Main.justReceivedRSID) {
                    rxRsidCounter++;
                } else {
                    rxRsidCounter = 0;
                }
                if (rxRsidCounter > 1 && (Main.TxModem == modemmodeenum.MFSK32
                        || Main.TxModem == modemmodeenum.MFSK64
                        || Main.TxModem == modemmodeenum.PSK125
                        || Main.TxModem == modemmodeenum.PSK125R)) {
                    Main.m.requestTxRsid("ON");
                    rxRsidCounter = 0;
                } else if (rxRsidCounter > 0 && (Main.TxModem == modemmodeenum.MFSK8
                        || Main.TxModem == modemmodeenum.MFSK16
                        || Main.TxModem == modemmodeenum.PSK63
                        || Main.TxModem == modemmodeenum.DOMINOEX5
                        || Main.TxModem == modemmodeenum.PSK31)) {
                    Main.m.requestTxRsid("ON");
                    rxRsidCounter = 0;
                }
                ;
                //In any case send RSID until we have full connect exchange so that the server can gauge it's tx delay
                if (Main.connectingPhase) {
                    Main.m.requestTxRsid("ON");
                }
                info = statusblock(Main.myrxstatus);
                outstring = make_block(info) + FrameEnd;
                break;
            case TXTraffic:
                outstring = "";
                info = payload;
                outstring = StartHeader + info;
                info = statusblock(Main.myrxstatus);
                outstring += make_block(info) + FrameEnd;
                break;
        }
        if (!outstring.equals("")) {
            outstring = "\n" + outstring;  // add eol before each frame
//   System.out.println("sending:" + outstring);;        
            sendit(outstring);
        }
    }

    /**
     * /
     * Send UI, unnumbered information, message
     *
     * @param msg
     */
    public void send_uimessage(String msg) {
        try {
            this.txserverstatus = txstatus.TXUImessage;
            send_frame(msg);
        } catch (InterruptedException ex) {
            Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * /
     * Send a simple ping, using port 7
     */
    public void send_ping() throws InterruptedException {
        this.txserverstatus = txstatus.TXPing;
        send_frame("");
    }

    /**
     * /
     * Send a reply to a ping, using port 71
     */
    public void send_ping_reply() throws InterruptedException {
        this.txserverstatus = txstatus.TXPingReply;
        send_frame("");
    }

    /**
     * /
     * Send a reply to an Inquire
     */
    public void send_inquire_reply() throws InterruptedException {
        this.txserverstatus = txstatus.TXInqReply;
        send_frame("");
    }

    /**
     * /
     * Send a QSL reply
     */
    public void send_QSL_reply() throws InterruptedException {
        this.txserverstatus = txstatus.TXQSLReply;
        send_frame("");
    }

    /**
     *
     */
    public void send_link() throws InterruptedException {
        this.txserverstatus = txstatus.TXlinkreq;
        send_frame("");
    }

    /**
     *
     */
    public void send_link_ack(String reqCallSign) throws InterruptedException {
        this.reqCallSign = reqCallSign;
        this.txserverstatus = txstatus.TXlinkack;
        send_frame("");
    }

    /**
     *
     */
    public void send_beacon() throws InterruptedException {
        if (!Main.Connected) {
            this.txserverstatus = txstatus.TXBeacon;
            send_frame("");
        }
    }

    /**
     *
     * @param msg
     */
    public void send_aprsmessage(String msg) throws InterruptedException {
        this.txserverstatus = txstatus.TXaprsmessage;
        send_frame(msg);
    }

    public void send_status(String txt) throws InterruptedException {
        this.txserverstatus = txstatus.TXStat;
        send_frame(txt);
    }

    public void send_abort() throws InterruptedException {
        this.txserverstatus = txstatus.TXAbort;
        send_frame("");
//        System.out.println("sending abort");
    }

    public void send_data(String outstr) throws InterruptedException {
        this.txserverstatus = txstatus.TXTraffic;
        send_frame(outstr);
        int datalength = outstr.length();
        Session.DataReceived += datalength;
        if (Session.DataSize > 0) {
            Main.Progress = 100 * Session.DataReceived / Session.DataSize;
        }
    }

    public String TX_addblock(int nr) {
        if (Session.txbuffer[nr].length() > 0) {
            char c = (char) nr;
            String accum = "0";
            accum += Main.session;
            accum += Character.toString(c);
            accum += Session.txbuffer[nr];
            String blcheck = checksum(accum);
            accum += blcheck;

            return make_block(accum);
        } else {
            return "";
        }
    }

    /**
     * /
     * Adds SOH and checksum e.g.: '<SOH>00jThis is data for'akj0
     *
     * @param info
     * @return
     */
    public String make_block(String info) {
        String check = "";
        if (info.length() > 0) {
            check = checksum(info);
        }
        return StartHeader + info + check;
    }

    /** /
     * Adds SOH and checksum with possible password
     * @param info
     * @return
     */
    public String make_blockWithPassword(String info) {
        String check="";
        if (info.length()>0) {
            check = checksum(info + serverPassword);
        }
        return StartHeader + info + check;
    }

    /*
    ############################################################
    # Checksum of header + block
    # Time + password + header + block
    ############################################################
     */
    /**
     *
     * @param intext
     * @return
     */
    public static String checksum(String intext) {
        String Encrypted = "0000";

        int[] table = {
            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,};

        byte[] bytes = intext.getBytes();
        int crc = 0x0000;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
        }

        Encrypted += Integer.toHexString(crc).toUpperCase();
        return Encrypted.substring(Encrypted.length() - 4);
    }

    public static float Round(float Rval, int Rpl) {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return tmp / p;
    }
 
    private String getStatusString() {

        String returnString = Main.configuration.getPreference("STATUS");
        if (returnString.toLowerCase(Locale.US).startsWith("<statustext>")) {
            returnString = ""; //Blank it first
            try {
                //Check if we have a file named "statustext" in the .pskmail folder
                String statusFilename = Main.HomePath + Main.Dirprefix + "statustext";
                File statusFile = new File(statusFilename);
                // First check most common problems
                if (statusFile == null) {
                    throw new IllegalArgumentException("File should not be null.");
                }
                if (!statusFile.isFile()) {
                    throw new IllegalArgumentException("Should not be a directory: " + statusFile);
                }
                // We should have a file now, lets fetch stuff                
                FileReader fin = new FileReader(statusFile);
                BufferedReader br = new BufferedReader(fin);
                String lineString;
                while ((lineString = br.readLine()) != null && returnString.length() < 100) { //Can't find a maximum size, so 100 it is for now
                    returnString += lineString + " "; //Add spacer for each new line
                }
                fin.close();
            } catch (Exception e) {
                Main.log.writelog("Could not fetch Beacon Status information.", true);
                returnString = "Error reading statustext file.";
            }
        }
        return returnString;
    }

}
