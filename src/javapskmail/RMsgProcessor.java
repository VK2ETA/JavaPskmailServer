/*
 * RMsgProcessor.java
 *
 * Copyright (C) 2011-2022 John Douyere (VK2ETA)
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.io.*;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import javax.mail.*;
import java.util.*;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import javax.swing.SwingUtilities;

public class RMsgProcessor {

    static boolean onWindows = true;

    static String FileNameString = "";

    //File name of last message received for appending a newly received picture
    static public String lastReceivedMessageFname = "";
    static public long lastMessageEndRxTime = 0;
    //Less than 20 seconds between the end of the text message and
    //	the start of mfsk picture transmission
    static boolean pictureRxInTime = false;
    static RMsgObject lastTextMessage = null;

    //Stop gap measure: init as first modem in list
    static int TxModem = Modem.customModeListInt[0];
    static int RxModem = Modem.customModeListInt[0];

    static int imageTxModemIndex = 0;

    //Semaphores to instruct the RxTx Thread to start or stop
    public static Semaphore restartRxModem = new Semaphore(1, false);

    // globals to pass info to gui windows
    static String monitor = "";
    static String TXmonitor = "";
    static String TermWindow = "";
    static String status = "Listening";
    static int cpuload;

    // Error handling and logging object
    static LoggingClass log;

    static boolean alreadyWarned = false;

    public static void processor() {
        //Nothing as this is a service
    }

    //Post to main terminal window
    public static void PostToTerminal(String text) {
        RMsgProcessor.TermWindow += text;
        //RadioMSG.mHandler.post(RadioMSG.addtoterminal);
    }

    //Post to main terminal window
    public static void PostToModem(String text) {
        RMsgProcessor.monitor += text;
        //RadioMSG.mHandler.post(RadioMSG.addtomodem);
    }

    //Check if is hf-Clubs
    public static boolean opModeIsHfClubs() {

        return false; //config.getPreferenceS("LASTMODEUSED", "HF-Clubs").equals("HF-Clubs");
    }

    //return call to be used in From and Relay info
    public static String getCall() {

        return Main.callsignAsServer.trim();
    }

    //Check if we have a match (strict or with ALL allowed) to our call or selcallview
    public static boolean matchMyCallWith(String call, boolean allowALL) {

        //boolean mMatch1 = (allowALL && call.equals("0000")) || call.equals(config.getPreferenceS("SELCALL", "9999").trim());
        boolean mMatch2 = (allowALL && call.equals("*"))
                || call.toLowerCase(Locale.US).equals(Main.callsignAsServer.trim().toLowerCase(Locale.US));

        return mMatch2;
    }

    //Check if we have a match (strict or with ALL allowed) to our call or selcallview
    public static boolean matchThisCallWith(String thisCall, String call, boolean allowALL) {

        //boolean mMatch1 = (allowALL && call.equals("0000")) || call.equals(thisCall.trim());
        boolean mMatch2 = (allowALL && call.equals("*"))
                || call.toLowerCase(Locale.US).equals(thisCall.trim().toLowerCase(Locale.US));

        return mMatch2;
    }

    //Tries to find a match for a given combination of phone number and time
    // If none are found, create an entry for that Cellular number and now time combination
    // Automatically generated entries look like:
    // 0412345678,joe,1524632518000
    // This entry links "joe", the sender of a radio message, with the cellular number 0412345678
    //   it was relayed to, counting time from the epoch as shown after the last comma.
    public static void updateSMSFilter(String cellularNumber, String from) {
        //String[] filterList = {"*,*,0"}; //for now config.getPreferenceS("SMSLISTENINGFILTER", "*,*,0").split("\\|");
        String[] filterList = Main.configuration.getPreference("SMSLISTENINGFILTER", "").split("\\|");
        boolean haveMatch = false;
        Long nowTime = System.currentTimeMillis();

        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                long lastCommTime;
                try {
                    lastCommTime = Long.parseLong(thisFilter[2].trim());
                } catch (Exception e) {
                    lastCommTime = 1; //Any number non zero
                }
                String fromFilter = thisFilter[1].trim();
                String phoneFilter = thisFilter[0].trim();
                String maxHoursStr = Main.configuration.getPreference("HOURSTOKEEPLINK", "24");
                int maxHoursSinceLastComm = 24;
                try {
                    maxHoursSinceLastComm = Integer.parseInt(maxHoursStr.trim());
                } catch (Exception e) {
                    maxHoursSinceLastComm = 24;
                }
                if ((fromFilter.equals("*") || from.trim().equals(fromFilter))
                        && (phoneFilter.equals("*") || RMsgMisc.lTrimZeros(phoneFilter).endsWith(RMsgMisc.lTrimZeros(cellularNumber)))) {
                    //Do we need to update that last communication time
                    if (lastCommTime > 1) {
                        //We had a time in here, not a zero or a mis-typed number
                        filterList[i] = phoneFilter + "," + thisFilter[1].trim() + "," + nowTime.toString();
                    }
                    haveMatch = true;
                    break;
                }
            }
        }
        String newSmsFilter = "";
        if (!haveMatch) {
            //add new entry at the top
            newSmsFilter = cellularNumber + "," + from + "," + nowTime.toString() + "|";
        }
        for (int j = 0; j < filterList.length; j++) {
            newSmsFilter = newSmsFilter + filterList[j] + "|";
        }
        newSmsFilter = newSmsFilter.replace("||", "|");
        if (newSmsFilter.length() > 0) {
            Main.configuration.setPreference("SMSLISTENINGFILTER", newSmsFilter);
        }
    }

    //Tries to find a match for a given combination of email address and time
    // If none are found, create an entry for that email address and now time combination
    // Automatically generated entries look like:
    // myemail@myprovider.com.au,joe,1524632518000
    // This entry links "joe", the sender of a radio message, with the email address myemail@myprovider.com.au with joe
    //   it was relayed to, counting time from the epoch as shown after the last comma.
    public static void updateEmailFilter(String emailAddress, String from) {
        String[] filterList = Main.configuration.getPreference("EMAILLISTENINGFILTER", "").split("\\|");
        //String[] filterList = {"*,*,0"}; //tbf config.getPreferenceS("EMAILLISTENINGFILTER", "*,*,0").split("\\|");
        boolean haveMatch = false;
        Long nowTime = System.currentTimeMillis();

        //Not required as done before calling this
        //Check valid email address format
        //if (!emailAddress.matches("^[\\w.-_+]+@\\w+\\.[\\w.-]+")) {
        //    return;
        //}
        //Iterate through list of entries
        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                long lastCommTime;
                try {
                    lastCommTime = Long.parseLong(thisFilter[2].trim());
                } catch (Exception e) {
                    lastCommTime = 1; //Any number non zero
                }
                String fromFilter = thisFilter[1].trim();
                String emailFilter = thisFilter[0].trim();
                String maxHoursStr = Main.configuration.getPreference("HOURSTOKEEPLINK", "24");
                int maxHoursSinceLastComm = 24;
                try {
                    maxHoursSinceLastComm = Integer.parseInt(maxHoursStr.trim());
                } catch (Exception e) {
                    maxHoursSinceLastComm = 24;
                }
                if ((fromFilter.equals("*") || from.trim().equals(fromFilter))
                        && (emailFilter.equals("*") || emailFilter.toLowerCase(Locale.US).equals(emailAddress.toLowerCase(Locale.US)))) {
                    //Do we need to update that last communication time
                    if (lastCommTime > 1) {
                        //We had a time in here, not a zero or a mis-typed number
                        filterList[i] = emailFilter + "," + thisFilter[1].trim() + "," + nowTime.toString();
                    }
                    haveMatch = true;
                    break;
                }
            }
        }
        String newEmailFilter = "";
        if (!haveMatch) {
            //add new entry at the top
            newEmailFilter = emailAddress + "," + from + "," + nowTime.toString() + "|";
        }
        for (int j = 0; j < filterList.length; j++) {
            newEmailFilter = newEmailFilter + filterList[j] + "|";
        }
        newEmailFilter = newEmailFilter.replace("||", "|");
        if (newEmailFilter.length() > 0) {
            Main.configuration.setPreference("EMAILLISTENINGFILTER", newEmailFilter);
        }
    }

    //Called when a client asked a callsign to be unlinked from am email or SMS
    public static boolean removeFilterEntries(String address, String from) {

        //In case the address is an Alias, find also the full address
        String fullAddress = Main.mainui.msgDisplayList.
                getReceivedAliasAndDestination(address + "=", from);
        if (fullAddress.contains("=")) {
            //extract the destination only (it can be an email or a cellular number
            fullAddress = RMsgUtil.extractDestination(fullAddress);
        }
        //First on Email filter
        String[] filterList = Main.configuration.getPreference("EMAILLISTENINGFILTER", "").split("\\|");
        //String[] filterList = {"*,*,0"}; //tbf config.getPreferenceS("EMAILLISTENINGFILTER", "*,*,0").split("\\|");
        boolean haveMatch = false;
        String newFilter = "";
        //Iterate through list of entries
        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                String fromFilter = thisFilter[1].trim();
                String emailFilter = thisFilter[0].trim();
                if (fromFilter.toLowerCase(Locale.US).equals(from.toLowerCase(Locale.US))
                        && (emailFilter.toLowerCase(Locale.US).equals(address.toLowerCase(Locale.US))
                        || emailFilter.toLowerCase(Locale.US).equals(fullAddress.toLowerCase(Locale.US)))) {
                    //Match, we delete by skipping it
                    haveMatch = true;
                } else {
                    //No match, store that entry
                    newFilter = newFilter + filterList[i] + "|";
                }
            }
        }
        //Store updated filter
        newFilter = newFilter.replace("||", "|");
        Main.configuration.setPreference("EMAILLISTENINGFILTER", newFilter);

        //Second on SMS filter
        filterList = Main.configuration.getPreference("SMSLISTENINGFILTER", "").split("\\|");
        //String[] filterList = {"*,*,0"}; //tbf config.getPreferenceS("EMAILLISTENINGFILTER", "*,*,0").split("\\|");
        newFilter = "";
        //Iterate through list of entries
        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                String fromFilter = thisFilter[1].trim();
                String smsFilter = thisFilter[0].trim();
                if (fromFilter.toLowerCase(Locale.US).equals(from.toLowerCase(Locale.US))
                        && (smsFilter.toLowerCase(Locale.US).equals(address.toLowerCase(Locale.US))
                        || smsFilter.toLowerCase(Locale.US).equals(fullAddress.toLowerCase(Locale.US)))) {
                    //Match, we delete by skipping it
                    haveMatch = true;
                } else {
                    //No match, store that entry
                    newFilter = newFilter + filterList[i] + "|";
                }
            }
        }
        //Store updated filter
        newFilter = newFilter.replace("||", "|");
        Main.configuration.setPreference("SMSLISTENINGFILTER", newFilter);

        return haveMatch;
    }

    //Forward message as email message
    private static void sendMailMsg(RMsgObject mMessage, String sendTo, String filterTo) {

        String smtpServer = Main.configuration.getPreference("SERVERSMTPHOST");//"smtp.gmail.com";
        String socketFactoryPort = "465";
        //String socketFactoryPort = "587";
        String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        String smtpAuth = "true";
        String smtpPort = "465";
        //String smtpPort = "587";
        final String fromAddress = Main.configuration.getPreference("SERVEREMAILADDRESS");
        final String userName = Main.configuration.getPreference("SERVERUSERNAME");
        final String password = Main.configuration.getPreference("SERVERPASSWORD");

        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.socketFactory.port", socketFactoryPort);
            props.put("mail.smtp.socketFactory.class", socketFactoryClass);
            props.put("mail.smtp.auth", smtpAuth);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.from", fromAddress);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.socketFactory.fallback", "false");
            //Get a new instance each time as default instance conflicts with the email read section
            //Session session = Session.getDefaultInstance(props, null);
            Session session = Session.getInstance(props, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }

            });
            // -- Create a new message --
            Message msg = new MimeMessage(session);
            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(fromAddress));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendTo, false));
            // -- We could include CC recipients too --
            // if (cc != null)
            // msg.setRecipients(Messaging.RecipientType.CC
            // ,InternetAddress.parse(cc, false));
            // -- Set the subject and body text --
            msg.setSubject("Radio Message from " + mMessage.from);
            // -- Set some other header information --
            msg.setHeader("X-Mailer", "Radio Message Relay");
            msg.setSentDate(new Date());
            String body = mMessage.formatForSmsOrEmail();
            //msg.setText(body);
            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/plain");
            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // adds attachments
            if (mMessage.picture != null) {
                MimeBodyPart attachPart = new MimeBodyPart();
                try {
                    String fullPath = Main.homePath
                            + Main.dirPrefix + Main.dirImages
                            + Main.separator + mMessage.fileName.replace(".txt", ".png");
                    attachPart.attachFile(fullPath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                multipart.addBodyPart(attachPart);
            }
            // sets the multi-part as e-mail's content
            msg.setContent(multipart);
            // -- Send the message --
            Transport.send(msg);
            //System.out.println("Messaging sent OK.");
            //Update filter
            updateEmailFilter(filterTo, mMessage.from);
        } catch (Exception ex) {
            //RadioMSG.middleToastText("Error relaying message as Email: " + ex.toString());
            //Save in log for debugging
            RMsgUtil.addEntryToLog("Error relaying message as Email: \n" + ex.toString());
        }

    }

    //Starts a new thread and register's with the email provider to alert of new 
    //   incoming emails. If requested, send them over the air as they arrive
    public static void startEmailsAndSMSsMonitor() {

        //Listener for mew emails if I relay emails and requested to listen for
        //  new email (and maybe send them immediately over radio)
        //String password = Main.configuration.getPreference("SERVERPASSWORD");
        if (Main.configuration.getPreference("RELAYSMSSIMMEDIATELY").equals("yes")
                || Main.configuration.getPreference("RELAYEMAILSIMMEDIATELY").equals("yes")) {
            Thread myThread = new Thread() {
                @Override
                public void run() {
                    Boolean keepInLoop = true;
                    while (keepInLoop) {
                        //Normally one shot
                        keepInLoop = false;
                        //Request emails from server
                        IMAPFolder folder = null;
                        Store store = null;
                        try {
                            Properties props = System.getProperties();
                            props.setProperty("mail.store.protocol", "imaps");

                            props.setProperty("mail.imap.starttls.enable", "true");
                            props.setProperty("mail.imap.ssl.enable", "true");

                            MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
                            socketFactory.setTrustAllHosts(true);
                            props.put("mail.imaps.ssl.socketFactory", socketFactory);
                            //conflict with default instance, create a new one each time
                            //Session session = Session.getDefaultInstance(props, null);
                            Session session = Session.getInstance(props, null);
                            store = session.getStore("imaps");
                            //String emailAddress = Main.configuration.getPreference("SERVEREMAILADDRESS", "");
                            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD", "");
                            String emailUsername = Main.configuration.getPreference("SERVERUSERNAME", "");
                            String imapHost = Main.configuration.getPreference("SERVERIMAPHOST", "");
                            store.connect(imapHost, emailUsername, emailPassword);
                            folder = (IMAPFolder) store.getFolder("inbox");
                            if (!folder.isOpen()) {
                                folder.open(Folder.READ_WRITE);
                            }

                            // Add messageCountListener to listen for new messages
                            folder.addMessageCountListener(new MessageCountAdapter() {
                                public void messagesAdded(MessageCountEvent ev) {
                                    Message[] messages = ev.getMessages();
                                    //Iterate through all the messages received
                                    for (int i = messages.length; i > 0; i--) {
                                        String senderAddress = "";
                                        String smsString = "";
                                        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                        try {
                                            Message msg = messages[i - 1];
                                            //From email address
                                            senderAddress = msg.getFrom()[0].toString();
                                            //Remove name and only keep email address proper
                                            String[] emailAdresses = senderAddress.split("[ <>]+");
                                            for (String fromPart : emailAdresses) {
                                                if (fromPart.indexOf("@") > 0) {
                                                    senderAddress = fromPart;
                                                    break;
                                                }
                                            }
                                            //Save message time for making filenames later on
                                            //Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
                                            Date msgDateTime = msg.getReceivedDate();
                                            c1.setTime(msgDateTime);
                                            //Body
                                            smsString = getBodyTextFromMessage(msg);
                                            if (smsString.startsWith("\n")) {
                                                smsString = smsString.substring(1);
                                            }
                                            if (smsString.endsWith("\r\n\r\n")) {
                                                smsString = smsString.substring(0, smsString.length() - 4);
                                            }
                                            if (smsString.length() > 155) {
                                                smsString = smsString.subSequence(0, 155 - 1) + " ...>";
                                            }
                                            String phoneNumber = "";
                                            String smsSubject = msg.getSubject();
                                            //SMS from gateway, convert phone number to E.164 format always
                                            String smsGatewayDomain = Main.configuration.getPreference("SMSEMAILGATEWAY", "").trim();
                                            if (smsGatewayDomain.length() > 0 && senderAddress.endsWith(smsGatewayDomain)) {
                                                phoneNumber = senderAddress.replace("@" + smsGatewayDomain, "");
                                                //Only store the phone number in international format, not the sms gateway
                                                senderAddress = convertNumberToE164(phoneNumber);// + "@" + smsGatewayDomain;
                                            } else {
                                                //Not a reply to a previous radio message, add the subject line
                                                if (!smsSubject.contains("Radio Message from ")
                                                        && !smsSubject.contains("Reply from ")
                                                        && !smsSubject.trim().equals("")) {
                                                    smsString = smsSubject
                                                            + "\n" + smsString;
                                                }
                                            }
                                        } catch (Exception e) {
                                            continue; //Skip processing that message
                                        }
                                        String emailFilterTo[] = passEmailFilter(senderAddress);
                                        //Iterate through all the linked "to" stations as found in the SMS filter
                                        for (String toString : emailFilterTo) {
                                            //Blank string means nobody to send to
                                            if (toString != null && !toString.equals("")) {
                                                // Create message from cellular SMS
                                                RMsgObject radioEmailMessage = new RMsgObject();
                                                radioEmailMessage.to = toString;
                                                radioEmailMessage.relay = Main.configuration.getPreference("CALLSIGNASSERVER", "");
                                                radioEmailMessage.sms = smsString;
                                                radioEmailMessage.via = ""; //only direct send
                                                radioEmailMessage.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                                                radioEmailMessage.receiveDate.setTime(c1.getTime());
                                                //String smsGatewayDomain = Main.configuration.getPreference("SMSEMAILGATEWAY", "").trim();
                                                //Are we still asked to send immediately? Treat SMSs and emails individually. 
                                                if (Main.configuration.getPreference("RELAYSMSSIMMEDIATELY", "").equals("yes")
                                                        && isCellular(senderAddress)) {
                                                    //Keep only the phone number and get the alias if any
                                                    radioEmailMessage.from = Main.mainui.msgDisplayList.getAliasFromOrigin(senderAddress, toString);
                                                    //Create message and add in outbox list
                                                    RMsgTxList.addMessageToList(radioEmailMessage);
                                                }
                                                if (Main.configuration.getPreference("RELAYEMAILSIMMEDIATELY", "").equals("yes")
                                                        && isEmail(senderAddress)) {
                                                    //Create message and add in outbox list
                                                    radioEmailMessage.from = Main.mainui.msgDisplayList.getAliasFromOrigin(senderAddress, toString);
                                                    RMsgTxList.addMessageToList(radioEmailMessage);
                                                }
                                                /* We do not save the messages received from the internet in the incoming list as we (re)fetch them at each request
                                                FileWriter out = null;
                                                //Create a file name for this received cellular SMS
                                                Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
                                                radioEmailMessage.fileName = String.format(Locale.US, "%04d", c1.get(Calendar.YEAR)) + "-" +
                                                        String.format(Locale.US, "%02d", c1.get(Calendar.MONTH) + 1) + "-" +
                                                        String.format(Locale.US, "%02d", c1.get(Calendar.DAY_OF_MONTH)) + "_" +
                                                        String.format(Locale.US, "%02d%02d%02d", c1.get(Calendar.HOUR_OF_DAY),
                                                                c1.get(Calendar.MINUTE), c1.get(Calendar.SECOND)) + ".txt";
                                                //Save message in file
                                                boolean isCCIRMode = Modem.isCCIR476();
                                                String resultString = radioEmailMessage.formatForTx(isCCIRMode);
                                                String inboxFolderPath = Processor.HomePath +
                                                        Processor.Dirprefix + Processor.DirInbox + Processor.Separator;
                                                try {

                                                    File msgReceivedFile = new File(inboxFolderPath + radioEmailMessage.fileName);
                                                    if (msgReceivedFile.exists()) {
                                                        msgReceivedFile.delete();
                                                    }
                                                    out = new FileWriter(msgReceivedFile, true);
                                                    out.write(resultString);
                                                    out.close();
                                                } catch (Exception e) {
                                                    loggingclass.writelog("Exception Error in 'OnReceive Email Message' " + e.getMessage(), null, true);
                                                }
                                                //Add to listadapter
                                                final MsgObject finalMessage = radioEmailMessage;
                                                RadioMSG.myInstance.runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        msgArrayAdapter.addNewItem(finalMessage, false); //Is NOT my own message
                                                    }
                                                });
                                                //update the list if we are on that screen
                                                RadioMSG.mHandler.post(RadioMSG.updateList);
                                                Processor.PostToTerminal("\nSaved File: " + radioEmailMessage.fileName);
                                                Messaging.addEntryToLog("Received Email " + radioEmailMessage.fileName);
                                                //Perform notification on speaker/lights/vibrate of new message
                                                RadioMSG.middleToastText("Email Received: " + radioEmailMessage.fileName);
                                                //Need to forward it over Radio too?
                                                 */
                                                //Sleep 1 second to ensure all SMSes have a different file name (1 second resolution)
                                                //try {
                                                //    Thread.sleep(1000);
                                                //} catch (InterruptedException e) {
                                                //}
                                            }
                                        }
                                    }
                                }
                            });
                            // Check mail once in "freq" MILLIseconds
                            //int freq = config.getPreferenceI("CHECKEMAILSEVERY", 600) * 1000; //converted to milliseconds
                            int freq = 30000; //every 60 seconds for now 
                            //Minimum every 10 seconds
                            freq = freq < 30000 ? 30000 : freq;
                            boolean supportsIdle = false;
                            try {
                                if (folder instanceof IMAPFolder) {
                                    IMAPFolder f = (IMAPFolder) folder;
                                    //System.out.println("Entering first idle()");
                                    Main.q.Message("Monitoring New Email Messages", 5);
                                    f.idle();
                                    //System.out.println("First idle() completed, must have got new mail");
                                    supportsIdle = true;
                                }
                            } catch (FolderClosedException fex) {
                                throw fex;
                            } catch (MessagingException mex) {
                                supportsIdle = false;
                            }
                            for (;;) {
                                if (supportsIdle && folder instanceof IMAPFolder) {
                                    IMAPFolder f = (IMAPFolder) folder;
                                    f.idle();
                                    //System.out.println("idle() completed, must have got new mail");
                                } else {
                                    Thread.sleep(freq); // sleep for freq milliseconds
                                    // This is to force the IMAP server to send us
                                    // EXISTS notifications.
                                    folder.getMessageCount();
                                }
                            }
                        } catch (Exception e) {
                            Exception e1 = e; //for debug
                            //We have lost the connection with the server, restart the NewMailMonitor processing
                            keepInLoop = true;
                            if (!alreadyWarned) {
                                Main.q.Message("Can't connect to Imap server", 8);
                                alreadyWarned = true;
                            }
                            try {
                                Thread.sleep(10000); //Retry in 10 seconds
                            } catch (InterruptedException e2) {
                                //Nothing
                            }
                            //System.out.println("Restarting NewMailMonitor loop");
                        } finally {
                            try {
                                if (folder != null && folder.isOpen()) {
                                    folder.close(true);
                                }
                                if (store != null) {
                                    store.close();
                                }
                            } catch (Exception e) {
                                //do nothing
                            }
                        }
                    }
                }
            };
            myThread.start();
            myThread.setName("NewMailMonitor");
        }
    }

    //Forward message as SMS message via a gateway
    private static void sendCellularMsg(RMsgObject mMessage) {

        String email2SmsGateway = Main.configuration.getPreference("SMSEMAILGATEWAY", "").trim();
        if (email2SmsGateway.length() > 3 && email2SmsGateway.contains(".")) {
            RMsgObject mailMessage = mMessage;
            String resultNum = mailMessage.to;
            //Need to send in local phone format?
            if (Main.configuration.getPreference("SENDUSINGLOCALNUMBER", "no").equals("yes")) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    String countryCode = Main.configuration.getPreference("GATEWAYISOCOUNTRYCODE").trim();
                    if (countryCode.length() == 0) {
                        //Try via the Locale set on this computer as a last resort
                        Locale currentLocale = Locale.getDefault();
                        countryCode = currentLocale.getCountry();
                    }
                    PhoneNumber mPhoneNum = phoneUtil.parse(mailMessage.to, countryCode);
                    resultNum = phoneUtil.format(mPhoneNum, PhoneNumberFormat.NATIONAL).replace(" ", "");
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
            }
            String sendTo = resultNum + "@" + email2SmsGateway;
            //Send via the email to SMS gateway
            sendMailMsg(mailMessage, sendTo, mailMessage.to);
            //Redundant, done in sendMailMsg. updateSMSFilter(mailMessage.to, mailMessage.from);
        }
    }

    private static void soundAlarm() {
        /*To-Do
        Thread myThread = new Thread() {
            @Override
            public void run() {
                int counter = 0;
                while (Processor.TXActive) {
                    try {
                        //Wait until we stop Txing otherwise we send the ringtone to the transceiver
                        Thread.sleep(100);
                    } catch (Exception e) {
                    }
                    if (++counter > 200) { //Max 20 seconds then give up
                        RadioMSG.topToastText("Waited too long for TXActive");
                        return;
                    }
                }
                try {
                    //Wait to clear to tx buffer
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone myRingtone = RingtoneManager.getRingtone(RadioMSG.myContext.getApplicationContext(), notification);
                myRingtone.play();
                //Wait to clear to audio buffer
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        };
        myThread.start();
         */
    }

    //Process one block of received data. Called from Modem.RxBlock when a complete block is received.
    //Block format is:
    // <SOH>FROM:*|DEST\n with "*" meaning to ALL
    //[via:RELAYSTATION\n]
    //[sms:MESSAGE CONTENT]\n] stripped of CR and with escaped LF
    //[pos:[-]ll.lllllll,[-]LLL.LLLLLL,DDD,SSS]\n] with ll = latitude, LLL = longitude in decimal degrees, DDD = delay in acquiring GPS fix in seconds, SSS = speed in Km/h
    //[pos:PPPPPPPPDDD,SSS\n] with PPPPPPPP = compressed position as per APRS format, DDD = delay in acquiring GPS fix in seconds, SSS = speed in Km/h
    //[pic:WWWxHHH,B&W|Col,NX,MMMMM\n] Picture size in pixels, Greyscale or Colour, N = speed to transfer, MMMMM = mode used
    //NNNN<EOT> with NNNN = crc16 remapped to lowercase characters ONLY as they transmit faster via varicode
    public static void processBlock(String blockLine, String fileName, String rxMode) {

        //For the java version we need to convert the Strings <SOH> and <EOT> as received from FLdigi to '
        //  their propper characters so that internally we are dealing with the same data
        blockLine = blockLine.replaceFirst("^<SOH>", Character.toString((char) 1));
        blockLine = blockLine.replaceFirst("<EOT>$", Character.toString((char) 4));

        //Create text part of message and see if we need to wait for other data like a picture
        RMsgObject mMessage = RMsgObject.extractMsgObjectFromString(blockLine, false, fileName, rxMode);//Only text information
        if (mMessage.to.contains("=")) {
            //Replace alias only with alias=destination. If we received an alias with a phone number convert to E164 international format
            mMessage.to = Main.mainui.msgDisplayList.getReceivedAliasAndDestination(mMessage.to, mMessage.from);
        }
        if (mMessage.from.contains("=")) {
            //Replace alias only with alias=destination
            mMessage.from = Main.mainui.msgDisplayList.getSentDestinationFromAlias(mMessage.from);
        }
        //If is a phone number (sms message)  gateway, convert the phone number to the international format
        String mDestination = RMsgUtil.extractDestination(mMessage.to);
        if (isCellular(mDestination)) {
            mDestination = convertNumberToE164(mDestination);
            mMessage.to = RMsgUtil.extractAliasOnly(mMessage.to) + mDestination;
        }
        if (!mMessage.pictureString.equals("")) {
            //We have a picture coming, save this message for later
            //Message will be saved when either Picture is received or Timeout occurred
            //Therefore save the time of end of Rx (any attached picture must be send within a set time)
            lastMessageEndRxTime = System.currentTimeMillis();
            lastTextMessage = mMessage;
        } else {
            //Text only, process immediately
            lastMessageEndRxTime = 0L;
            processTextMessage(mMessage);
            //Reset stored message
            lastTextMessage = null;
        }
    }

    //Tries to find a match for a given combination of phone number and time
    //Alows wildcards "*" for any incoming numbers and "0" for time-persistent filters
    // Returns "" if no match
    //Examples:
    // *,*,0 (the default, matches any incoming cellular number at any time and sends SMSs to ALL Radio recipients
    // *,vk2eta,0  send any incoming SMS to "vk2eta" only, any time
    // 0412345678,vk2eta,0 sends SMSes from 0412345678 to vk2eta, any time
    // Automatically generated entries look like:
    // 0412345678,vk2eta,1524632518000
    // This entry links the sender of a radio message with the nominated cellular number it was
    //   relayed to, counting time from the epoch as shown after the last comma.
    // Receipts of incoming cellular SMSs which result in a match will update the time of the last exchange to keep the link alive (unless
    //   the link is timeless, i.e. "0" time).
    private String[] passSMSFilter(String senderNo) {
        String myResult[] = new String[20]; //Max 20 links = max 20 messages to send on receipt of this SMS
        String[] filterList = {"*,*,0"}; // tbf config.getPreferenceS("SMSLISTENINGFILTER", "0,*,*").split("\\|");
        int toCount = 0;
        int maxHoursSinceLastComm = 24; // tbf config.getPreferenceI("MAXHOURSSINCELASTCOMM", 24);
        Long nowTime = System.currentTimeMillis();
        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                long lastCommTime;
                try {
                    lastCommTime = Long.parseLong(thisFilter[2].trim());
                } catch (Exception e) {
                    lastCommTime = 1; //Any small number non zero
                }
                String phoneFilter = thisFilter[0].trim();
                if ((lastCommTime == 0 || lastCommTime + maxHoursSinceLastComm * 3600000L > nowTime)
                        && (phoneFilter.equals("*") || senderNo.endsWith(RMsgMisc.lTrimZeros(phoneFilter)))) {
                    //Do we need to update that last communication time
                    if (lastCommTime > 1) {
                        //We had a real time stamp in here, not a zero or a mis-typed number
                        filterList[i] = phoneFilter + "," + thisFilter[1].trim() + "," + nowTime.toString();
                    }
                    //Add the destination callsign for this number and time combination
                    myResult[toCount++] = thisFilter[1].trim();
                    //Make sure we don't max out
                    if (toCount >= myResult.length) {
                        break;
                    }
                } else if (lastCommTime > 1 && lastCommTime + maxHoursSinceLastComm * 3600000L <= nowTime) {
                    //Remove obsolete filters with valid time stamps
                    filterList[i] = "";
                }
            }
        }
        //Rebuild the new filter list with the updated link times, minus the time obsolete filters
        String newSmsFilter = "";
        for (int j = 0; j < filterList.length; j++) {
            if (!filterList[j].equals("")) { //Skip blanked ones
                newSmsFilter = newSmsFilter + filterList[j] + "|";
            }
        }
        //tbf SharedPreferences.Editor editor = RadioMSG.mysp.edit();
        //editor.putString("SMSLISTENINGFILTER", newSmsFilter.replace("||", "|"));
        //editor.commit();
        //Return resulting matches in an array
        return myResult;
    }

    //Tries to find a match for a given combination of email address and time
    //Alows wildcards "*" for any incoming email address and "0" for timeless filters
    // Returns "" if no match
    //Examples:
    // *,*,0 (matches any incoming email address at any time and sends email to ALL Radio recipients
    // *,vk2eta,0  send any incoming email to "vk2eta" only, any time
    // myaddress@myprovider.com.au,vk2eta,0 sends SMSes from 0412345678 to vk2eta, any time
    // Automatically generated entries look like:
    // myaddress@myprovider.com.au,vk2eta,1524632518000
    // This entry links the sender of a radio message with the nominated email address it was
    //   relayed to, counting time from the epoch as shown after the last comma.
    // Receipts of incoming emails which result in a match will update the time of the last exchange to keep the link alive (unless
    //   the link is timeless, i.e. "0" time).
    public static String[] passEmailFilter(String emailAddress) {
        String myResult[] = new String[20]; //Max 20 links = max 20 messages to send on receipt of this email
        //String[] filterList = {"*,*,0"}; // tbf config.getPreferenceS("EMAILLISTENINGFILTER", "*,*,0").split("\\|");
        String[] filterList = Main.configuration.getPreference("EMAILLISTENINGFILTER", "").split("\\|");
        int toCount = 0;
        int maxHoursSinceLastComm;//tbf config.getPreferenceI("MAXHOURSSINCELASTCOMM", 24);
        String maxHoursStr = Main.configuration.getPreference("HOURSTOKEEPLINK", "24");
        try {
            maxHoursSinceLastComm = Integer.parseInt(maxHoursStr.trim());
        } catch (Exception e) {
            maxHoursSinceLastComm = 24;
        }
        Long nowTime = System.currentTimeMillis();
        for (int i = 0; i < filterList.length; i++) {
            //Match on time, then on incoming phone number
            String[] thisFilter = filterList[i].split(",");
            //Only properly formed filters
            if (thisFilter.length == 3) {
                long lastCommTime;
                try {
                    lastCommTime = Long.parseLong(thisFilter[2].trim());
                } catch (Exception e) {
                    lastCommTime = 1; //Any small number non zero
                }
                String emailFilter = thisFilter[0].trim();
                if ((lastCommTime == 0 || lastCommTime + maxHoursSinceLastComm * 3600000L > nowTime)
                        && (emailFilter.equals("*") || emailFilter.toLowerCase(Locale.US).equals(emailAddress.toLowerCase(Locale.US)))) {
                    //Do we need to update that last communication time
                    if (lastCommTime > 1) {
                        //We had a real time stamp in here, not a zero or a mis-typed number
                        filterList[i] = emailFilter + "," + thisFilter[1].trim() + "," + nowTime.toString();
                    }
                    //Add the destination callsign for this number and time combination
                    myResult[toCount++] = thisFilter[1].trim();
                    //Make sure we don't max out
                    if (toCount >= myResult.length) {
                        break;
                    }
                } else if (lastCommTime > 1 && lastCommTime + maxHoursSinceLastComm * 3600000L <= nowTime) {
                    //Remove obsolete filters with valid time stamps
                    filterList[i] = "";
                }
            }
        }
        //Rebuild the new filter list with the updated link times, minus the time obsolete filters
        String newEmailFilter = "";
        for (int j = 0; j < filterList.length; j++) {
            if (!filterList[j].equals("")) { //Skip blanked ones
                newEmailFilter = newEmailFilter + filterList[j] + "|";
            }
        }
        //tbf SharedPreferences.Editor editor = RadioMSG.mysp.edit();
        //editor.putString("EMAILLISTENINGFILTER", newEmailFilter.replace("||", "|"));
        //editor.commit();
        Main.configuration.setPreference("EMAILLISTENINGFILTER", newEmailFilter);
        //Return resulting matches in an array
        return myResult;
    }

    //Builds the list of messages to send to the qtc request. For but to email requests as the messages are already in the received list of the app.
    private static ArrayList<RMsgObject> buildNonEmailResendList(RMsgObject mMessage, int numberOf, Long forLast, Boolean forAll, Boolean positionsOnly) {

        //Get list of messages to send
        int listCount = Main.mainui.msgDisplayList.getLength();
        ArrayList<RMsgObject> resendList = new ArrayList<RMsgObject>();
        RMsgDisplayItem recDisplayItem;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date dateNow = localCalendar.getTime();
        Boolean foundPreviousQtc = false;
        int reSentCount = 0;
        Boolean goodToResend;
        for (int i = listCount - 2; i >= 0; i--) {//Skip just received qtc? message
            goodToResend = false;
            recDisplayItem = Main.mainui.msgDisplayList.getItem(i);
            //      Must be a received message
            if (matchThisCallWith(mMessage.from, recDisplayItem.mMessage.from, false)
                    && recDisplayItem.mMessage.sms.startsWith("*qtc? ")) { //Ignore simple qtc requests "*qtc?"
                foundPreviousQtc = true;
            }
            if ((!recDisplayItem.myOwn && !matchMyCallWith(recDisplayItem.mMessage.from, false)) //Not my own message
                    //AND for all OR for this requesting callsign
                    && (forAll || matchThisCallWith(mMessage.from, recDisplayItem.mMessage.to, true))
                    //AND not from this requesting callsign
                    && (!matchThisCallWith(mMessage.from, recDisplayItem.mMessage.from, false))
                    //AND be only a position message if requested so
                    && (!positionsOnly || recDisplayItem.mMessage.msgHasPosition)
                    //AND must not be a command, position request, QTC or "No messages" message
                    && (!recDisplayItem.mMessage.sms.startsWith("*cmd"))
                    && (!recDisplayItem.mMessage.sms.startsWith("*pos?"))
                    && (!recDisplayItem.mMessage.sms.startsWith("*qtc?"))
                    && (!recDisplayItem.mMessage.sms.equals("No Messages"))
                    //AND must not be an already re-sent message
                    && (!recDisplayItem.mMessage.sms.startsWith("Re-Sending ")) //old method
                    && (recDisplayItem.mMessage.receiveDate == null)) { //New method
                if (forLast > 0L) { //We have a time-based request
                    Date recMsgDate;
                    try {
                        recMsgDate = formatter.parse(recDisplayItem.mMessage.fileName.replaceAll(".txt", ""));
                    } catch (ParseException e) {
                        //Dummy date just to prevent failure
                        recMsgDate = dateNow;
                    }
                    if ((dateNow.getTime() - recMsgDate.getTime()) <= forLast) { //In the last X min, hours
                        goodToResend = true;
                    } else {
                        break; //No point in looking any further
                    }
                } else if (numberOf == -1) { //We want all messages since last QTC
                    if (!foundPreviousQtc) {
                        goodToResend = true;
                    } else {
                        break; //No point in looking any further
                    }
                } else { //Then we must have a number based request
                    if (reSentCount < numberOf) {
                        goodToResend = true;
                    } else if (numberOf > 0) { //We found the number of messages required
                        break; //No point in looking any further
                    }
                }
                if (goodToResend) {
                    if (++reSentCount >= 20) {
                        break; //Hard stop: enough to send
                    }
                    //Enqueue message for sorting. Get full message with binary data.
                    RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirInbox, recDisplayItem.mMessage.fileName, true);
                    //Coming from this relay station
                    fullMessage.relay = Main.callsignAsServer.trim();
                    //Remove via information to make sure it is not forwarded
                    fullMessage.via = "";
                    //Re-send/relay in the same mode we received in
                    fullMessage.rxMode = mMessage.rxMode;
                    //Add text to specify it was received not relayed
                    //Not needed anymore as we send the ro: field
                    //fullMessage.sms = "Re-Sending " + recDisplayItem.mMessage.fileName.replaceAll(".txt", "")
                    //        + ": " + fullMessage.sms;
                    //Set the receivedDate for the "ro:" information to be sent at TX time
                    try {
                        //Example = "2017-10-25_113958";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss", Locale.US);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate.setTime(sdf.parse(fullMessage.fileName.replaceAll(".txt", "")));
                    } catch (ParseException e) {
                        //Debug
                        e.printStackTrace();
                    }
                    //Then send save Message in list for sorting and limiting the number
                    resendList.add(fullMessage);
                }
            }
        }

        return resendList;
    }

    //Builds the list of messages to send to the qtc request. Specific to email requests.
    private static ArrayList<RMsgObject> buildEmailResendList(RMsgObject mMessage, int numberOf, Long forLast, boolean fullSize, boolean forAll) {

        //Get list of messages to send
        int listCount = Main.mainui.msgDisplayList.getLength();
        ArrayList<RMsgObject> resendList = new ArrayList<RMsgObject>();
        //MsgObject recDisplayItem;
        RMsgObject recMessage;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        //Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        //Date dateNow = localCalendar.getTime();
        Date recMsgDate = null;
        //If requested since last QTC, find the previous one first and extract the time filter
        if (numberOf == -1 && forLast == 0L) {
            //Iterate from the 2nd last message received, backwards (back in time), skipping just received "*qtc?" message
            for (int i = listCount - 2; i >= 0; i--) {
                recMessage = Main.mainui.msgDisplayList.getItemMessage(i);
                //      Must be a received message
                if (matchThisCallWith(mMessage.from, recMessage.from, false)
                        && recMessage.sms.startsWith("*qtc? ")) { //Ignore simple qtc requests "*qtc?"
                    //Extract time to use as filter on the imap email list
                    try {
                        recMsgDate = formatter.parse(recMessage.fileName.replaceAll(".txt", ""));
                    } catch (ParseException e) {
                        //Bad date format, ignore
                    }
                    break; //No point in looking any further
                }
            }
            //No previous matching "*qtc?" message found AND we wanted to find one, return an empty list
            if (!((numberOf == -1 && forLast == 0L && recMsgDate != null)
                    || numberOf != -1 || forLast != 0L)) {
                return resendList;
            }
        } else if (forLast != 0L) {
            //We have a time based request
            recMsgDate = new Date(System.currentTimeMillis() - forLast);
        }
        //Request emails from server
        IMAPFolder folder = null;
        Store store = null;
        int charLimit = fullSize ? 400 : 150;
        //Store current UTC time. To be used with increments of 1 seconds to differentiate the messages
        Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imap.starttls.enable", "true");
            props.setProperty("mail.imap.ssl.enable", "true");
            MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
            socketFactory.setTrustAllHosts(true);
            props.put("mail.imaps.ssl.socketFactory", socketFactory);
            //conflict with default instance, create a new one each time
            //Session session = Session.getDefaultInstance(props, null);
            javax.mail.Session session = javax.mail.Session.getInstance(props, null); //Conflicts with local Session.java, must be explicit
            store = session.getStore("imaps");
            String imapHost = Main.configuration.getPreference("SERVERIMAPHOST");
            String userName = Main.configuration.getPreference("SERVERUSERNAME");
            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD");
            //store.connect("imap.googlemail.com",emailAddress, emailPassword);
            store.connect(imapHost, userName, emailPassword);
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
            }
            Message[] messages;
            if (numberOf == -1) {
                //We must have a valid date to select messages from (date only, then select individually
                SearchTerm st = new ReceivedDateTerm(ComparisonTerm.GE, recMsgDate);
                messages = folder.search(st);
            } else {
                //Fetch all messages
                messages = folder.getMessages();
                //localCalendar.add(Calendar.YEAR, -1);
                //Date oldDate = localCalendar.getTime();
                //SearchTerm st = new ReceivedDateTerm(ComparisonTerm.GE,oldDate);
                //messages = folder.search(st);
            }
            int msgCount = 0;
            //Process in the order of most recent to oldest
            //for (int i=0; i < messages.length;i++)
            for (int i = messages.length; i > 0; i--) {
                //Filtering by date and by requester
                Message msg = messages[i - 1];
                if (numberOf == -1) {
                    //Check the time as the javamail filter only checks the date, not date and time
                    if (msg.getReceivedDate().before(recMsgDate)) {
                        continue;
                    }
                }
                //Save message time for making filenames later on
                Date msgDateTime = msg.getReceivedDate();
                c1.setTime(msgDateTime);
                //Check reply messages count
                if (++msgCount > 20 //Hard stop
                        || (numberOf > 0 && msgCount > numberOf)) {
                    break; //Stop: enough to send
                }
                //From email address
                String fromString = msg.getFrom()[0].toString();
                //Remove name and only keep email address proper
                String[] emailAdresses = fromString.split("[ <>]+");
                for (String fromPart : emailAdresses) {
                    if (fromPart.indexOf("@") > 0) {
                        fromString = fromPart;
                        break;
                    }
                }
                String phoneNumber = "";
                //SMS from gateway, convert phone number to E.164 format always. To match stored number format.
                String smsGatewayDomain = Main.configuration.getPreference("SMSEMAILGATEWAY", "").trim();
                if (smsGatewayDomain.length() > 0 && fromString.endsWith(smsGatewayDomain)) {
                    phoneNumber = fromString.replace("@" + smsGatewayDomain, "");
                    //Reformat as +614123456789@mysmsgateway.com.au..NO, filters for SMSs are stored as phonenumber only
                    fromString = convertNumberToE164(phoneNumber);  // + "@" + smsGatewayDomain;
                }
                String[] tos;
                if (forAll) {
                    //Option of requesting all emails regardless of who they are for
                    //Just add the requester of the QTC
                    tos = new String[1];
                    tos[0] = mMessage.from;
                } else {
                    //Add to reply list for each matching filter
                    tos = passEmailFilter(fromString);
                }
                for (int j = 0; j < tos.length; j++) {
                    //Only if the filter matches the requesting callsign
                    if (tos[j] != null && (tos[j].equals("*") || mMessage.from.toLowerCase(Locale.US).equals(tos[j].toLowerCase(Locale.US)))) {
                        String smsString = getBodyTextFromMessage(msg);
                        if (smsString.startsWith("\n")) {
                            smsString = smsString.substring(1);
                        }
                        if (smsString.endsWith("\r\n\r\n")) {
                            smsString = smsString.substring(0, smsString.length() - 4);
                        }
                        if (smsString.length() > charLimit) {
                            smsString = smsString.subSequence(0, charLimit - 1) + " ...>";
                        }
                        String smsSubject = msg.getSubject();
                        //If is NOT an SMS reply, add subject line
                        if (phoneNumber.equals("")) {
                            //Not a reply to a previous radio message, add the subject line
                            if (!smsSubject.contains("Radio Message from ")
                                    && !smsSubject.contains("SMS received from ")
                                    && !smsSubject.trim().equals("")) {
                                smsString = "Subj: " + smsSubject + "\n" + smsString;
                            }
                        }
                        //Debug
                        //smsString = smsString + " Rec Date: " + msg.getReceivedDate() + "\n";

                        RMsgObject fullMessage = new RMsgObject(tos[j], "", smsString,
                                null, 0, false, 0, false, null, 0L, null);
                        //Coming from this relay station
                        fullMessage.relay = Main.callsignAsServer.trim();
                        //Remove via information to make sure it is not forwarded
                        //fullMessage.via = "";
                        //Re-send/relay in the same mode we received in
                        fullMessage.rxMode = mMessage.rxMode;
                        //From address: email address or cellular number replying?
                        if (smsGatewayDomain.length() > 0 && fromString.endsWith(smsGatewayDomain)) {
                            //Keep only the phone number and get the alias if any
                            fullMessage.from = Main.mainui.msgDisplayList.getAliasFromOrigin(fromString.replace("@" + smsGatewayDomain, ""), tos[j]);
                        } else {
                            //Other email addresses
                            fullMessage.from = Main.mainui.msgDisplayList.getAliasFromOrigin(fromString, tos[j]);
                        }
                        //Save received date for incoming message
                        //Date recDate = c1.getTime();
                        fullMessage.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate.setTime(c1.getTime()); //= c1;
                        //Then save Message date/time in list for sorting (and limit the number of messages)
                        fullMessage.fileName = String.format(Locale.US, "%04d", c1.get(Calendar.YEAR)) + "-"
                                + String.format(Locale.US, "%02d", c1.get(Calendar.MONTH) + 1) + "-"
                                + String.format(Locale.US, "%02d", c1.get(Calendar.DAY_OF_MONTH)) + "_"
                                + String.format(Locale.US, "%02d%02d%02d", c1.get(Calendar.HOUR_OF_DAY),
                                        c1.get(Calendar.MINUTE), c1.get(Calendar.SECOND)) + ".txt";
                        //No need ????, only used for comparison, not for storage (storage time stamp is Tx time related)
                        c1.add(Calendar.SECOND, 1);
                        resendList.add(fullMessage);
                        break; //No more messages for this email as they would be redundant
                    }
                }
            }
        } catch (Error err) {
            err.printStackTrace();
            Main.log.writelog("Error accessing Folder: " + err.getMessage() + "\n", false);
            //throw (err);
        } catch (FolderNotFoundException e) {
            Exception e1 = e;
            Main.log.writelog("E-mail folder not found: " + e1.getMessage() + "\n", false);
        } catch (MessagingException e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", false);
        } catch (Exception e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", false);
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                //do nothing
            }
        }
        return resendList;
    }

    //Builds the list of messages to send to the qtc request. For but to email requests as the messages are already in the received list of the app.
    //private static ArrayList<RMsgObject> buildNonEmailResendList(RMsgObject mMessage, int numberOf, Long forLast, Boolean forAll, Boolean positionsOnly) {
    //Builds the list of SENT messages to reply to the qtc, instead of looking at the received list and exclude already re-sent messages
    private static ArrayList<RMsgObject> buildTXedResendList(RMsgObject mMessage, int numberOf, Long forLast, Boolean forAll, Boolean positionsOnly) {

        //Get list of messages to send
        int listCount = Main.mainui.msgDisplayList.getLength();
        ArrayList<RMsgObject> resendList = new ArrayList<RMsgObject>();
        RMsgDisplayItem recDisplayItem;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date dateNow = localCalendar.getTime();
        Boolean foundPreviousQtc = false;
        int reSentCount = 0;
        Boolean goodToResend;
        for (int i = listCount - 2; i >= 0; i--) {//Skip just received qtc? message
            goodToResend = false;
            recDisplayItem = Main.mainui.msgDisplayList.getItem(i);
            //Must be a received message
            if (matchThisCallWith(mMessage.from, recDisplayItem.mMessage.from, false)
                    //Changed to complex QTC request, ignoring simple resend requests
                    && recDisplayItem.mMessage.sms.startsWith("*qtc? ")) {
                foundPreviousQtc = true;
            }
            if (recDisplayItem.myOwn //My own message (a Sent message)
                    //AND for all OR for this requesting callsign
                    && (forAll || matchThisCallWith(mMessage.from, recDisplayItem.mMessage.to, true))
                    //AND not from this requesting callsign
                    && (!matchThisCallWith(mMessage.from, recDisplayItem.mMessage.from, false))
                    //AND be only a position message if requested so
                    && (!positionsOnly || recDisplayItem.mMessage.msgHasPosition)
                    //AND must not be a qtc?, command or previously re-sent message
                    && (!recDisplayItem.mMessage.sms.startsWith("*qtc?"))
                    && (!recDisplayItem.mMessage.sms.startsWith("*cmd"))
                    && (!recDisplayItem.mMessage.sms.startsWith("*pos?"))
                    && (!recDisplayItem.mMessage.sms.startsWith("No messages"))
                    && (!recDisplayItem.mMessage.sms.startsWith("Re-Sending "))
                    && (recDisplayItem.mMessage.receiveDate == null)) {
                if (forLast > 0L) { //We have a time-based request
                    Date recMsgDate;
                    try {
                        recMsgDate = formatter.parse(recDisplayItem.mMessage.fileName.replaceAll(".txt", ""));
                    } catch (ParseException e) {
                        //Dummy date just to prevent failure
                        recMsgDate = dateNow;
                    }
                    if ((dateNow.getTime() - recMsgDate.getTime()) <= forLast) { //In the last X min, hours
                        goodToResend = true;
                    } else {
                        break; //No point in looking any further
                    }
                } else if (numberOf == -1) { //We want all messages since last QTC
                    if (!foundPreviousQtc) {
                        goodToResend = true;
                    } else {
                        break; //No point in looking any further
                    }
                } else { //Then we must have a number based request
                    if (reSentCount < numberOf) {
                        goodToResend = true;
                    } else if (numberOf > 0) { //We found the number of messages required
                        break; //No point in looking any further
                    }
                }
                //Discard email or sms messages that have been previously sent as we always re-check for these on the internet
                String msgFromAddress = recDisplayItem.mMessage.from;
                if (msgFromAddress.contains("=") || isEmail(msgFromAddress) || isCellular(msgFromAddress)) {
                    //Is an Alias OR is an email Address (w/o and alias) OR is a cellular number (w/o an alias), discard
                    goodToResend = false;
                }
                if (goodToResend) {
                    if (++reSentCount >= 20) {
                        break; //Hard stop: enough to send
                    }
                    //Enqueue message for sorting. Get full message with binary data.
                    RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirSent, recDisplayItem.mMessage.fileName, true);
                    //Coming from this relay station
                    if (fullMessage.from.equals(Main.callsignAsServer.toLowerCase(Locale.US).trim())) {
                        fullMessage.relay = "";
                    } else {
                        fullMessage.relay = Main.callsignAsServer.toLowerCase(Locale.US).trim();
                    }
                    //Remove via information to make sure it is not forwarded
                    fullMessage.via = "";
                    //Re-send/relay in the same mode we received in
                    fullMessage.rxMode = mMessage.rxMode;
                    //Add text to specify it was received not relayed
                    //Not needed anymore as we now send the ro: field
                    //fullMessage.sms = "Re-Sending " + recDisplayItem.mMessage.fileName.replaceAll(".txt", "")
                    //        + ": " + fullMessage.sms;
                    //Set the receivedDate for the "ro:" information to be sent at TX time
                    try {
                        //Example = "2017-10-25_113958";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss", Locale.US);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate.setTime(sdf.parse(fullMessage.fileName.replaceAll(".txt", "")));
                        //Log.i(TAG, "time = " + cal.getTimeInMillis()); 
                    } catch (ParseException e) {
                        //Debug
                        e.printStackTrace();
                    }
                    //Then send save Message in list for sorting and limiting the number
                    resendList.add(fullMessage);
                }
            }
        }

        return resendList;
    }

    //Returns the last Txed message (any type) in response a strait "*qtc?" request
    private static RMsgObject getLastTxed(RMsgObject mMessage) {

        //Get list of messages to send
        int listCount = Main.mainui.msgDisplayList.getLength();
        RMsgDisplayItem recDisplayItem;
        RMsgObject fullMessage = null;
        for (int i = listCount - 2; i >= 0; i--) {//Skip just received *qtc? message
            recDisplayItem = Main.mainui.msgDisplayList.getItem(i);
            //My own message (a Sent message) and is for the requester or for ALL
            if (recDisplayItem.myOwn && matchThisCallWith(mMessage.from, recDisplayItem.mMessage.to, true)) {
                //Enqueue message for sorting. Get full message with binary data.
                fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirSent, recDisplayItem.mMessage.fileName, true);
                //Coming from this relay station
                if (fullMessage.from.equals(Main.callsignAsServer.toLowerCase(Locale.US).trim())) {
                    fullMessage.relay = "";
                } else {
                    fullMessage.relay = Main.callsignAsServer.toLowerCase(Locale.US).trim();
                }
                //Remove via information to make sure it is not forwarded
                fullMessage.via = "";
                //Re-send/relay in the same mode we received in
                fullMessage.rxMode = mMessage.rxMode;
                if (fullMessage.receiveDate == null) {
                    //Get the receivedDate for the "ro:" information to be sent at TX time
                    try {
                        //Example = "2017-10-25_113958";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss", Locale.US);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        fullMessage.receiveDate.setTime(sdf.parse(fullMessage.fileName.replaceAll(".txt", "")));
                        //Log.i(TAG, "time = " + cal.getTimeInMillis()); 
                    } catch (ParseException e) {
                        //Debug
                        e.printStackTrace();
                    }
                }
                //Then send save Message in list for sorting and limiting the number
                return fullMessage;
            }
        }

        return fullMessage;
    }

    //Extract the first part's body and return a plain text string
    public static String getBodyTextFromMessage(Message message) throws Exception {
        // general spacers for time and date
        //String spacers = "[\\s,/\\.\\-]";
        // matches times
        //String timePattern  = "(?:[0-2])?[0-9]:[0-5][0-9](?::[0-5][0-9])?(?:(?:\\s)?[AP]M)?";
        // matches day of the week
        //String dayPattern   = "(?:(?:Mon(?:day)?)|(?:Tue(?:sday)?)|(?:Wed(?:nesday)?)|(?:Thu(?:rsday)?)|(?:Fri(?:day)?)|(?:Sat(?:urday)?)|(?:Sun(?:day)?))";
        // matches day of the month (number and st, nd, rd, th)
        //String dayOfMonthPattern = "[0-3]?[0-9]" + spacers + "*(?:(?:th)|(?:st)|(?:nd)|(?:rd))?";
        // matches months (numeric and text)
        //String monthPattern = "(?:(?:Jan(?:uary)?)|(?:Feb(?:ruary)?)|(?:Mar(?:ch)?)|(?:Apr(?:il)?)|(?:May)|(?:Jun(?:e)?)|(?:Jul(?:y)?)" +
        //                                      "|(?:Aug(?:ust)?)|(?:Sep(?:tember)?)|(?:Oct(?:ober)?)|(?:Nov(?:ember)?)|(?:Dec(?:ember)?)|(?:[0-1]?[0-9]))";
        // matches years (only 1000's and 2000's, because we are matching emails)
        //String yearPattern  = "(?:[1-2]?[0-9])[0-9][0-9]";
        // matches a full date
        //String datePattern     = "(?:" + dayPattern + spacers + "+)?(?:(?:" + dayOfMonthPattern + spacers + "+" + monthPattern + ")|" +
        //                                        "(?:" + monthPattern + spacers + "+" + dayOfMonthPattern + "))" +
        //                                         spacers + "+" + yearPattern;
        // matches a date and time combo (in either order)
        //String dateTimePattern = "(?:" + datePattern + "[\\s,]*(?:(?:at)|(?:@))?\\s*" + timePattern + ")|" +
        //                                        "(?:" + timePattern + "[\\s,]*(?:on)?\\s*"+ datePattern + ")";
        // matches a leading line such as
        // ----Original Message----
        // or simply
        // ------------------------
        // Allows underscores, = and + in a raw also
        //
        //String leadInLine    = "[-_+=]+\\s*(?:Original(?:\\sMessage)?)?\\s*[-_+=]+\n";
        // matches a header line indicating the date
        //String dateLine    = "(?:(?:date)|(?:sent)|(?:time)):\\s*"+ dateTimePattern + ".*\n";
        //String dateLine    = "(?:(?:date)|(?:sent)|(?:time)):.*\n";
        // matches a subject or address line
        //Correction , removed the "|"
        //String subjectOrAddressLine    = "((?:from)|(?:subject)|(?:b?cc)|(?:to))|:.*\n";
        //String subjectOrAddressLine    = "((?:from)|(?:subject)|(?:b?cc)|(?:to)):.*\n";
        // matches gmail style quoted text beginning, i.e.
        //On Mon Jun 7, 2010 at 8:50 PM, Simon wrote:
        //String gmailQuotedTextBeginning = "(On\\s+" + dateTimePattern + ".*wrote:\n)";
        // matches the start of a quoted section of an email
        //Pattern quotedTextStart = Pattern.compile("(?i)(?:(?:" + leadInLine + ")?" +
        //    "(?:(?:(>\\s{0,3})?" + subjectOrAddressLine + ")|(?:" + dateLine + ")){2,6})");

        //first extract complete message text
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break;  //without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                }
            }
        }
        //Remove the original message if this was a reply to an email
        //Essential for reducing message size and for keeping privacy (as To: and From: 
        //  details are included in appended original message)
        Pattern omPatterm = Pattern.compile("(?:(?:^[-_+=]+(?:\\s*Original(?:\\s*Message)?\\s*)?[-_+=]+$)"
                + "|(?:^\\s*(?:>\\s{0,7})?(?:(?:(?:(?:from)|(?:subject)"
                + "|(?:b?cc)|(?:to)):.*)|(?:(?:(?:date)"
                + "|(?:sent)|(?:time)):.*$))))", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern gmailPatterm = Pattern.compile("(?:^On.{1,500}wrote:)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        if (result.length() > 0) {
            Matcher omMatcher = omPatterm.matcher(result);
            Matcher gmailMatcher = gmailPatterm.matcher(result);
            if (gmailMatcher.find()) {
                int quotedTextPos = gmailMatcher.start();
                result = result.substring(0, quotedTextPos);
            } else if (omMatcher.find()) {
                int quotedTextPos = omMatcher.start();
                int matchCount = 1;
                while (omMatcher.find()) {
                    matchCount++;
                }
                if (matchCount > 1) {
                    result = result.substring(0, quotedTextPos);
                }
            }
        }
        return result;
    }

    /*
    Parked code
    private boolean textIsHtml = false;

    /**
     * Return the primary text content of the message.
     *
    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }
     */
    //Is the string representative of an email
    public static boolean isEmail(String destination) {

        return destination.matches("^[\\w.-]+@\\w+\\.[\\w.-]+");
    }

    //Is the string representative of a cellular number
    public static boolean isCellular(String destination) {

        return destination.matches("^([\\w-]+=)?\\+?\\d{7,16}"); //"^\\+?\\d{8,16}"
    }

    //Search through current list of via stations for the one contained in the message
    public static String getRequiredAccessPassword(RMsgObject txMessage) {
        String accessPw = "";

        //For all relay requests
        //if (!txMessage.via.equals("") &&
        //        (txMessage.sms.contains("*qtc?") || txMessage.sms.contains("*cmd") || isCellular(txMessage.to)
        //                || isEmail(txMessage.to) || txMessage.to.matches(".+=.*"))) {
        if (!txMessage.via.equals("")) {
            for (int i = 0; i < Main.mainui.viaArray.length; i++) {
                if (Main.mainui.viaArray[i].equals("via " + txMessage.via)) {
                    accessPw = Main.mainui.viaPasswordArray[i];
                    break;
                }
            }
        }
        return accessPw;
    }

    public static String convertNumberToE164(String phoneNumber) {
        String resultNum = phoneNumber.replaceAll(" ", "");
        if (resultNum.startsWith("+")) {
            //Already formatted, return as-is with spaces removed
            return resultNum;
        }
        //Must be formatted as a National number, use library to format to E.164 format (+1888999...)
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String countryCode = Main.configuration.getPreference("GATEWAYISOCOUNTRYCODE");
            if (countryCode.length() == 0) {
                //Try via the Locale set on this computer as a last resort
                Locale currentLocale = Locale.getDefault();
                countryCode = currentLocale.getCountry();
            }
            PhoneNumber mPhoneNum = phoneUtil.parse(resultNum, countryCode);
            resultNum = phoneUtil.format(mPhoneNum, PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return resultNum;
    }

    //Process the message now that it is complete (may have had to wait for the picture component)
    @SuppressWarnings("unchecked")
    public static void processTextMessage(final RMsgObject mMessage) {
        boolean saveAndDisplay;

        //Check message is non null
        if (mMessage == null) {
            return;
        }
        //Launch in new thread so that we don't block the receiving of messages
        //Check if message to ALL or to me only
        // or asked to be a relay
        // BUT is not a message from me forwarded/relayed by another station
        //AND is not a "*qtc?" message meaning request to repeat
        // save message and process it, ignore otherwise
        //Note: the test is case IN-Sensitive
        if ((matchMyCallWith(mMessage.to, true)
                || matchMyCallWith(mMessage.via, false))
                && !matchMyCallWith(mMessage.from, false)) {
            saveAndDisplay = true;
            //Check if this is a duplicate. E.g. We received the message direct and now via a relay OR
            // the email/SMS message was already received
            if (!mMessage.timeId.equals("") || (mMessage.receiveDate != null)) {
                //Relayed and with timeId, check if it is a duplicate
                if (Main.mainui.msgDisplayList.isDuplicate(mMessage)) {
                    saveAndDisplay = false;
                    Main.q.Message("Duplicate Message. Ignoring.", 8);
                }
            }
            //To-Do: Add check for messages that are resent as the sms field contains the sender's filename
        } else {
            saveAndDisplay = false;
        }
        final boolean finalSaveAndDisplay = saveAndDisplay;

        Thread myThread = new Thread() {
            @Override
            public void run() {
                FileWriter out = null;

                try {
                    //save if need be
                    if (finalSaveAndDisplay) {

                        String resultString = "";
                        if ((mMessage.crcValid || mMessage.crcValidWithPW)) {
                            //Re-build the message (includes the potential alias translated to alias=destination format)
                            resultString = mMessage.formatForRx(false); //Always rebuild CRC without access password
                        } else {
                            //Save message as received, display it and see what we can do with it
                            resultString = mMessage.rawRxString;
                        }
                        String inboxFolderPath = Main.homePath
                                + Main.dirPrefix + Main.dirInbox + Main.separator;
                        File msgReceivedFile = new File(inboxFolderPath + mMessage.fileName);
                        if (msgReceivedFile.exists()) {
                            msgReceivedFile.delete();
                        }
                        out = new FileWriter(msgReceivedFile, true);
                        out.write(resultString);
                        out.close();
                        //Add to listadapter
                        //final RMsgObject finalMessage = mMessage;
                        //tbf announce and display new message
                        Main.mainui.msgDisplayList.addNewItem(mMessage, false); //Not my own
                        Main.log(mMessage.formatForList(false));
                        //Update displayed jtable
                        final RMsgDisplayItem mDisplayItem = new RMsgDisplayItem(mMessage, 0.0f, 0.0f, false, false);
                        //Add to displayed table of messages on GUI thread
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Main.mainui.mRadioMSgTblModel.addRow(new Object[]{mDisplayItem});
                                //Scroll to bottom
                                Main.mainui.scrollRadioMsgsTableToLast();
                            }
                        });
                        /*
                        RadioMSG.myInstance.runOnUiThread(new Runnable() {
                            public void run() {
                                msgArrayAdapter.addNewItem(finalMessage, false); //Is NOT my own message
                            }
                        });
                        //update the list if we are on that screen
                        RadioMSG.mHandler.post(RadioMSG.updateList);
                         */
                        //Advise reception of message
                        PostToTerminal("\nSaved File: " + mMessage.fileName);
                        RMsgUtil.addEntryToLog("Received Messaging " + mMessage.fileName);
                        //Perform notification on speaker/lights/vibrate of new message
                        //RadioMSG.middleToastText("Message Received: " + mMessage.fileName);
                        //tbf if ((config.getPreferenceI("ALARM", 0) & 2) != 0) { //Sound ok?
                        //    soundAlarm();
                        //}
                        /* tbf
                        //Alert pattern contained in sms part
                        if (!config.getPreferenceB("UNATTENDEDRELAY", false)) {
                            if (mMessage.sms.matches("(?i:.*(Alert[:; ,=]).*)")) {
                                final MsgObject fullMessage = MsgObject.extractMsgObjectFromFile(DirInbox, mMessage.fileName, true);
                                RadioMSG.myInstance.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //Display the Alert popup window
                                        RadioMSG.myInstance.alertPopup(fullMessage);
                                    }
                                });
                            }
                        }
                         */
                        //Is it a valid message
                        boolean messageAuthorized = ((Main.accessPassword.length() == 0 && mMessage.crcValid)
                                || (Main.accessPassword.length() > 0 && mMessage.crcValidWithPW));
                        //Command message?
                        if (mMessage.sms.startsWith("*cmd")) {
                            if (matchMyCallWith(mMessage.via, false) || (mMessage.via.length() == 0
                                    && matchMyCallWith(mMessage.to, false))) {
                                //A scan on/off command? e.g. "*cmd s off 30 m" or "*cmd s on"
                                //Scan off can also have a duration after which it restarts automatically. E.g. "s off 3h"
                                boolean cmdOk = false;
                                String replyString = "";
                                Pattern psc = Pattern.compile("^\\*cmd\\s((?:s\\son)|(?:s\\soff))(?:\\s(\\d{1,3})\\s*([mh]))?$");
                                Matcher msc = psc.matcher(mMessage.sms);
                                if (messageAuthorized && msc.find()) {
                                    String onOff = msc.group(1);
                                    String numberOff = "";
                                    if (msc.group(2) != null) {
                                        numberOff = msc.group(2);
                                    }
                                    String units = "";
                                    if (msc.group(3) != null) {
                                        units = msc.group(3);
                                    }
                                    if (onOff.equals("s on")) {
                                        Main.wantScanner = true;
                                        Main.restartScanAtEpoch = 0L;
                                        //Set checkbox on GUI
                                        Main.mainui.setScannerCheckbox(true);
                                        cmdOk = true;
                                        replyString = "Scan is On";
                                    } else if (onOff.equals("s off") && !numberOff.equals("") && !units.equals("")) {
                                        cmdOk = true;
                                        try {
                                            int durationToRestart = Integer.parseInt(numberOff);
                                            int finalDurationToRestart = durationToRestart;
                                            if (units.equals("h")) {
                                                //Time in hours in fact, max 24 hours
                                                finalDurationToRestart = durationToRestart > 24 ? 24 : durationToRestart;
                                                finalDurationToRestart *= 60;  //convert hours to minutes
                                                cmdOk = true;
                                            } else if (!units.equals("m")) {
                                                Main.restartScanAtEpoch = 0L;
                                                cmdOk = false;
                                            }
                                            if (cmdOk) {
                                                Main.restartScanAtEpoch = System.currentTimeMillis() + (finalDurationToRestart * 60000);
                                                Main.wantScanner = false;
                                                //Set checkbox on GUI
                                                Main.mainui.setScannerCheckbox(false);
                                                replyString = "Scan is Off for " + numberOff + " " + units;
                                            }
                                        } catch (Exception e) {
                                            //Bad syntax, re-enable scanning
                                            Main.restartScanAtEpoch = 0L;
                                            cmdOk = false;
                                        }
                                    }
                                }
                                //A "mute"/"unmute" command to allow/disallow immediate forwarding of SMSs and Emails
                                //E.g. "*cmd mute" or "*cmd unmute"
                                psc = Pattern.compile("^\\*cmd\\s((?:mute)|(?:unmute))$");
                                String muteUnmute = "";
                                msc = psc.matcher(mMessage.sms);
                                if (messageAuthorized && msc.find()) {
                                    muteUnmute = msc.group(1);
                                    if (muteUnmute.equals("mute")) {
                                        Main.wantRelayEmailsImmediat = false;
                                        Main.wantRelaySMSsImmediat = false;
                                        cmdOk = true;
                                    } else if (muteUnmute.equals("unmute")) {
                                        if (Main.configuration.getPreference("RELAYEMAILSIMMEDIATELY", "no").equals("yes")) {
                                            Main.wantRelayEmailsImmediat = true;
                                        }
                                        if (Main.configuration.getPreference("RELAYSMSSIMMEDIATELY", "no").equals("yes")) {
                                            Main.wantRelaySMSsImmediat = true;
                                        }
                                        cmdOk = true;
                                    }
                                    replyString = muteUnmute + "d"; //muted/unmuted
                                }
                                //An "unlink" command to unsubscibe this callsign / address combination.
                                // Stops any auto-forwarding or received emails or SMSs for this callsign.
                                // Received emails/SMSs can still be accessed with a QTC request.
                                //E.g. "*cmd unlink vk2eta-2 joemail" OR "*cmd unlink vk2eta-2 joesemailaddress@hisprovider.com"
                                psc = Pattern.compile("^\\*cmd\\sunlink\\s(\\S+)\\s(\\S+)$");
                                String callToUnlink = "";
                                String addressToUnlink = "";
                                msc = psc.matcher(mMessage.sms);
                                if (messageAuthorized && msc.find()) {
                                    callToUnlink = msc.group(1);
                                    addressToUnlink = msc.group(2);
                                    cmdOk = true;
                                    if (removeFilterEntries(addressToUnlink, callToUnlink)) {
                                        replyString = "Unsubscribed for " + addressToUnlink;
                                    } else {
                                        replyString = "Address/Number/Alias not in subscriptions";
                                    }
                                }
                                RMsgUtil.sendBeeps(cmdOk);
                                //Reply with acknowledgment message
                                if (!replyString.equals("")) {
                                    RMsgUtil.replyWithText(mMessage, replyString);
                                }
                            }
                        } else if (mMessage.sms.contains("*pos?")) { //Position request?
                            //For me, and ONLY for me? (positions requests from ALL are not allowed)
                            if (matchMyCallWith(mMessage.to, false)) {
                                if (mMessage.via.length() == 0) {
                                    //NO VIA information, sent direct or received via relay, reply ASAP
                                    //Otherwise I may send my reply as the relay station forwards this 
                                    // request to me (and obviously I heard it direct but I have to wait for the relay)
                                    RMsgUtil.sendBeeps(true);
                                    //Wait for the ack to pass first
                                    Thread.sleep(500);
                                    //Reply to ALL and may need to reply via relay station
                                    RMsgUtil.replyWithPosition("*", mMessage.relay, mMessage.rxMode);
                                }
                            }
                        //Time Sync request
                        } else if (mMessage.sms.startsWith("*tim?")) {
                            if (matchMyCallWith(mMessage.to, false)
                                    || (matchMyCallWith(mMessage.via, false) && mMessage.to.equals("*"))) {
                                RMsgUtil.sendBeeps(true);
                                //Reply to the requesting station only
                                RMsgUtil.replyWithTime(mMessage);
                            }
                        //Remote station SNR request (for last message)
                        } else if (mMessage.sms.startsWith("*snr?")) {
                            if (matchMyCallWith(mMessage.to, false)
                                    || (matchMyCallWith(mMessage.via, false) && mMessage.to.equals("*"))) {
                                RMsgUtil.sendBeeps(true);
                                //Reply to the requesting station only
                                RMsgUtil.replyWithSNR(mMessage);
                            }
                        //Time Sync data received, notify
                        } else if (mMessage.sms.toLowerCase(Locale.US).equals("*time reference received*")) {
                            if (matchMyCallWith(mMessage.to, false) && Main.refTimeSource.length() > 0) {
                                RMsgUtil.sendBeeps(true);
                                if (Main.deviceToRefTimeCorrection == 0) {
                                    Main.mainui.appendMainWindow("This device clock is the same as " + Main.refTimeSource + "'s clock\n");
                                } else if (Main.deviceToRefTimeCorrection < 0) {
                                    Main.mainui.appendMainWindow("This device clock is " + (-Main.deviceToRefTimeCorrection) + " seconds in front of " + Main.refTimeSource + "'s clock\n");
                                } else {
                                    Main.mainui.appendMainWindow("This device clock is " + Main.deviceToRefTimeCorrection + " seconds behind " + Main.refTimeSource + "'s clock\n");
                                }
                                //Reset source to mean "processed"
                                Main.refTimeSource = "";
                            }
                        } else if (mMessage.sms.startsWith("*qtc?")) {
                            //Simplified QTC processing:
                            //By default resends the last of any message (sent directly, heard as relay, email, sms). 
                            //Filtered by selected relaying flags in preferences (E.g. will not check for emails if email relaying is not enabled)
                            //Modifiers: n messages, l = since last QTC, e or f = emails/SMSs ONLY, r = force relay of QTC command
                            // m = in the last X minutes, h = in the last X hours, d = in the last X days
                            //QTC message VIA is my call, but TO is not my call OR TO is my call but VIA is blank
                            //Examples: - "*qtc? 3 e" = resend the last 3 email/SMSs messages (short version, "3 f" would be full version)
                            //          - "*qtc? l" = resend all messages since the last resend request I made (good to catch-up on missed messages)
                            //          - "*qtc? le" = resend all email/SMSs messages since the last resend request
                            //          - "*qtc? 2 h" = resend all messages in the last 2 hours
                            //Resend limit is hard coded to 20 messages to be resent
                            if (matchMyCallWith(mMessage.via, false)
                                    || (matchMyCallWith(mMessage.to, false) && mMessage.via.equals(""))) {
                                //Only properly received (and secured) messages
                                if (messageAuthorized) {
                                    RMsgUtil.sendBeeps(true);
                                    Boolean resendLast = false;
                                    //Read request (last X minutes or last N messages). Default is last ONE message if nothing is sent.
                                    int numberOf = 1;
                                    if (mMessage.sms.length() > 6 && mMessage.sms.substring(5, 6).equals(" ")) {
                                        try {
                                            //First remove all non digit characters after the "*qtc? "
                                            String extractedStr = mMessage.sms.substring(6).replaceAll("[^0-9]", "");
                                            //Any number following or preceding
                                            numberOf = Integer.valueOf(extractedStr);
                                        } catch (NumberFormatException e) {
                                            numberOf = 1;
                                        }
                                    } else if (mMessage.sms.toLowerCase(Locale.US).trim().equals("*qtc?")) {
                                        resendLast = true;
                                    }
                                    //Make sure we have at least 1
                                    numberOf = numberOf == 0 ? 1 : numberOf;
                                    //Any modifier after the numbers like "m" for last X minutes, "h" for last X hours, "d" for last X days,
                                    // "p" for last X position reports only, "a" for last X sms from "all" as in for a third party,
                                    // "e" for last e-Mails (short version), "f" for last e-Mails (fuller version),
                                    // "le" for last e-Mails since last qtc (short version), "lf" for last e-Mails since last qtc (fuller version),
                                    //"w" for Radio based messages only (no email, no Sms)
                                    Long forLast = 0L;
                                    Boolean forAll = false;
                                    Boolean emailRequest = false;
                                    Boolean positionsOnly = false;
                                    Boolean forceRelaying = false;
                                    Boolean radioWaveOnly = false;
                                    String extractedStr = "";
                                    if (mMessage.sms.length() > 6) {
                                        extractedStr = mMessage.sms.substring(6).replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.US);
                                        if (extractedStr.startsWith("m")) { //Minutes
                                            forLast = numberOf * 60000L;
                                            numberOf = 0;
                                        } else if (extractedStr.startsWith("h")) { //Hours
                                            forLast = numberOf * 3600000L;
                                            numberOf = 0;
                                        } else if (extractedStr.startsWith("d")) { //Days
                                            forLast = numberOf * 24 * 3600000L;
                                            numberOf = 0;
                                        } else if (extractedStr.startsWith("p")) { //Last X positions
                                            positionsOnly = true;
                                        } else if (extractedStr.equals("l")) { // "L" for last messages since last QTC request
                                            numberOf = -1;
                                        } else if (extractedStr.contains("a")) { //All messages even if for someone else
                                            forAll = true;
                                        } else if (extractedStr.startsWith("e") & Main.wantRelayEmails) { //E-Mail messages request (short version)
                                            emailRequest = true;
                                        } else if (extractedStr.startsWith("f") & Main.wantRelayEmails) { //E-Mail messages request (Full(er) version)
                                            emailRequest = true;
                                        } else if (extractedStr.startsWith("le") & Main.wantRelayEmails) { //E-Mail messages request (short version)
                                            emailRequest = true;
                                            numberOf = -1;
                                        } else if (extractedStr.startsWith("lf") & Main.wantRelayEmails) { //E-Mail messages request (Full(er) version)
                                            emailRequest = true;
                                            numberOf = -1;
                                        }
                                        if (extractedStr.contains("w")) { //only radio received/transmitted messages (no-email)
                                            radioWaveOnly = true;
                                        }
                                        if (extractedStr.contains("r")) { //Just force relaying this message to the final destination
                                            forceRelaying = true;
                                        }
                                    }
                                    //Are we asked to relay that QTC message to it's destination?
                                    if (Main.wantRelayOverRadio && forceRelaying && !matchMyCallWith(mMessage.to, false)
                                            && matchMyCallWith(mMessage.via, false)) {
                                        //Remove the "r" from the string and forward the rest of the QTC request as is
                                        extractedStr = mMessage.sms.substring(6).replaceAll("r", "");
                                        //Get full message with binary data
                                        RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirInbox, mMessage.fileName, true);
                                        //Add relay and remove via information
                                        fullMessage.relay = fullMessage.via;
                                        fullMessage.via = "";
                                        //Relay in the same mode we received in
                                        fullMessage.rxMode = mMessage.rxMode;
                                        fullMessage.sms = "*qtc? " + extractedStr;
                                        //Send the last 3 digits of the timestamp contained in the file name
                                        // This allows receiving stations to eliminate duplicates (direct Rx and Relayed Rx)
                                        int strLen = mMessage.fileName.length();
                                        fullMessage.timeId = mMessage.fileName.substring(strLen - 7, strLen - 4);
                                        //Wait for the ack to pass first
                                        Thread.sleep(500);
                                        //Then send Message
                                        RMsgTxList.addMessageToList(fullMessage);
                                    } else {
                                        //We are resending from this relay (default). Create blank list
                                        ArrayList<RMsgObject> resendList = new ArrayList<RMsgObject>();
                                        //What type of qtc did we receive?
                                        if (resendLast) {
                                            //We only want the last transmission (one messages only)
                                            RMsgObject lastOne = getLastTxed(mMessage);
                                            if (lastOne != null) {
                                                resendList.add(lastOne);
                                            }
                                        } else {
                                            //More complex request, start by processing non-email request
                                            if (Main.wantRelayOverRadio && !emailRequest) {
                                                resendList = buildNonEmailResendList(mMessage, numberOf, forLast, forAll, positionsOnly);
                                            }
                                            //We are repeating existing received messages
                                            // & (Main.WantRelayEmails | Main.WantRelaySMSs | Main.WantRelayOverRadio)
                                            ArrayList<RMsgObject> emailList;
                                            if ((Main.wantRelayEmails || Main.wantRelaySMSs) && !positionsOnly && !radioWaveOnly) {
                                                emailList = buildEmailResendList(mMessage, numberOf, forLast, extractedStr.endsWith("f"), forAll);
                                                resendList.addAll(emailList);
                                            }
                                            //Do not add if we ask for emails/sms only
                                            if (!emailRequest) {
                                                ArrayList<RMsgObject> TxList;
                                                TxList = buildTXedResendList(mMessage, numberOf, forLast, forAll, positionsOnly);
                                                resendList.addAll(TxList);
                                            }
                                        }
                                        //Now sort and send the list if it contains at least one element
                                        if (resendList.size() > 0) {
                                            Collections.sort(resendList, new Comparator() {
                                                public int compare(Object o1, Object o2) {
                                                    RMsgObject m1 = (RMsgObject) o1;
                                                    RMsgObject m2 = (RMsgObject) o2;
                                                    return m1.fileName.compareToIgnoreCase(m2.fileName);
                                                }
                                            });
                                            //Send requested number of messages with a max of 20 messages. If numberof is <0, we have a time based request, limit to 20 too.
                                            int iMax = (numberOf > 0 && numberOf < 21) ? numberOf : 20;
                                            int count = 0;
                                            int i = 0;
                                            //If we have more than requested, start down the list to send the n most recent messages as they are sorted in increasing date order
                                            if (numberOf > 0 && (resendList.size() > numberOf)) {
                                                i = resendList.size() - numberOf;
                                            }
                                            //Copy up to requested number or in any case max 20
                                            for (; i < resendList.size() && count++ < iMax; i++) { //i is already initialised
                                                RMsgTxList.addMessageToList(resendList.get(i));
                                            }
                                        } else {
                                            //Notify of nothing to send
                                            mMessage.via = ""; //Blank via as it's from this device
                                            RMsgUtil.replyWithText(mMessage, "No messages");
                                        }
                                    }
                                } else {
                                    //Alert with low beep to indicate invalid message
                                    RMsgUtil.sendBeeps(false);
                                    //We have an access password missing
                                    //RMsgUtil.replyWithText(mMessage, "Sorry...Missing Access Password");
                                }
                            }
                        } else if (matchMyCallWith(mMessage.via, false)) { //Am I asked to Relay messages?
                            //Check that if we received an alias we know what the real address is
                            if (!mMessage.to.contains("**unknown**")) {
                                //Extract final destination  (remove alias details)
                                String toStr = RMsgUtil.extractDestination(mMessage.to);
                                //Looks like an email address? Send via internet as email
                                if (isEmail(toStr)) {
                                    if (messageAuthorized & Main.wantRelayEmails) {
                                        RMsgUtil.sendBeeps(true);
                                        //Get full message with binary data
                                        RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirInbox, mMessage.fileName, true);
                                        //remove alias prefix 
                                        fullMessage.to = RMsgUtil.extractDestination(fullMessage.to);
                                        //Only forward properly received (and secured) messages
                                        sendMailMsg(fullMessage, fullMessage.to, fullMessage.to);
                                    } else {
                                        RMsgUtil.sendBeeps(false);
                                        //We have an access password missing
                                        //RMsgUtil.replyWithText(mMessage, "Sorry...Missing Access Password");
                                    }
                                } else if (isCellular(toStr)) { //Relaying messages as cellular SMS
                                    //At least 8 digits phone number? Send via SMS
                                    //Added +XXXnnnnnnnnnnn country code style
                                    //tbf if (config.getPreferenceB("SMSSENDRELAY", false)) {
                                    //Get the full message including any picture (as saved on file)
                                    if (messageAuthorized & Main.wantRelaySMSs) {
                                        RMsgUtil.sendBeeps(true);
                                        RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirInbox, mMessage.fileName, true);
                                        //remove alias prefix 
                                        fullMessage.to = RMsgUtil.extractDestination(fullMessage.to);
                                        //Only forward properly received (and secured) messages
                                        sendCellularMsg(fullMessage);
                                    } else {
                                        RMsgUtil.sendBeeps(false);
                                        //We have an access password missing
                                        //RMsgUtil.replyWithText(mMessage, "Sorry...Missing Access Password");
                                    }
                                    //}
                                } else if (toStr.equals("*") || mMessage.to.length() > 0) { //To ALL or at least one character call-sign or name? Send over Radio
                                    //Only forward properly received messages (allows relay over radio even if access password is not supplied)
                                    if ((mMessage.crcValid || mMessage.crcValidWithPW) && Main.wantRelayOverRadio) {
                                        RMsgUtil.sendBeeps(true);
                                        //Get full message with binary data
                                        RMsgObject fullMessage = RMsgObject.extractMsgObjectFromFile(Main.dirInbox, mMessage.fileName, true);
                                        //Add relay and remove via information
                                        fullMessage.relay = fullMessage.via;
                                        fullMessage.via = "";
                                        //Relay in the same mode we received in
                                        fullMessage.rxMode = mMessage.rxMode;
                                        //Send the last 3 digits of the timestamp contained in the file name
                                        // This allows receiving stations to eliminate duplicates (direct Rx and Relayed Rx)
                                        int strLen = mMessage.fileName.length();
                                        fullMessage.timeId = mMessage.fileName.substring(strLen - 7, strLen - 4);
                                        //Wait for the ack to pass first
                                        Thread.sleep(500);
                                        //Then send Message
                                        RMsgTxList.addMessageToList(fullMessage);
                                    } else {
                                        RMsgUtil.sendBeeps(false);
                                        //We have an access password missing
                                        //RMsgUtil.replyWithText(mMessage, "Sorry...Missing Access Password");
                                    }
                                }
                            } else {
                                //We have an unknown alias, reply with a warning
                                RMsgUtil.replyWithText(mMessage, "I don't know who \""
                                        + mMessage.to.replace("=**unknown**", "")
                                        + "\" is. Send full Alias details.");
                            }
                            //When re-sent pos request may not be in the beginning of the text
                            // } else if (mMessage.sms.startsWith("*pos?")) { //Position request?

                        } else {
                            //Not an action message, send ack as appropriate if directed to me ONLY
                            if (matchMyCallWith(mMessage.to, true)) {
                                RMsgUtil.sendBeeps(mMessage.crcValid || mMessage.crcValidWithPW);
                            }
                        }
                    }
                } catch (Exception e) {
                    //loggingclass.writelog("Exception Error in 'processTextMessage' " + e.getMessage(), null, true);
                }
            }
        };
        myThread.start();
        myThread.setName("SavingRMsg");
    }

//Check that we have started receiving the picture associated with a text message within the allocated time.
    public static void checkPictureReceptionTimeout() {
        //We have received a text message with expectation of a picture to follow
        //..and we have started the countdown to timeout
        //.. and we haven't started receiving an image
        //..and more than the timeout seconds have passed
        if (lastTextMessage != null
                && lastMessageEndRxTime != 0L
                && !pictureRxInTime
                && (System.currentTimeMillis() - RMsgProcessor.lastMessageEndRxTime > 22000)) {
            //We timed out (the picture RX failed), save the text part of the message
            processTextMessage(lastTextMessage);
            //Reset stored message
            RMsgProcessor.lastTextMessage = null;
            //Reset the time counter
            RMsgProcessor.lastMessageEndRxTime = 0L;
            //Reset timeout flag
            RMsgProcessor.pictureRxInTime = false;
        }
    }

    static double decayaverage(double oldaverage, double newvalue, double factor) {

        double newaverage = oldaverage;
        if (factor > 1) {
            newaverage = (oldaverage * (1 - 1 / factor)) + (newvalue / factor);
        }
        return newaverage;
    }

    //Return current time as a String
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

    //Logging
    static void log(String logtext) {
        //           File consolelog = new File (HomePath + Dirprefix + "logfile");
        try {
            // Create file
            FileWriter logstream = new FileWriter(Main.homePath + Main.dirPrefix + "logfile", true);
            BufferedWriter out = new BufferedWriter(logstream);

            out.write(myTime() + " " + logtext + "\n");
            //Close the output stream
            out.close();

        } catch (Exception e) {//Catch exception if any
            //loggingclass.writelog("LogError " + e.getMessage(), null, true);
        }
        RMsgProcessor.PostToTerminal(myTime() + " " + logtext + "\n");
    }

}
