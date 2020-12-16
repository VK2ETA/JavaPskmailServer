/*
 * Processor.java
 *
 * Copyright (C) 2018,2019,2020 John Douyere (VK2ETA)
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

import javax.mail.Message;
import javax.mail.Store;
import java.util.Properties;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import static javapskmail.Main.host;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author jdouyere
 */
public class serverMail {
    
    
    /* send mail
    ~SEND
    From: vk2xyz@gmail.com
    To: vk2abc@gmail.com
    Subject: Test java server
    hello there
    2nd line
    3rd line
    4th line.
    .
    */
    public static String sendMail(String fromStr, String toStr, String subjectStr, 
            String bodyStr, String attachementFileName) {
        String result = "";
        
        
        //Properties for gmail
        String smtpServer = Main.configuration.getPreference("SERVERSMTPHOST");//"smtp.gmail.com";
        String socketFactoryPort = "465";
        String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        String smtpAuth = "true";
        String smtpPort = "465";
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
            if (!fromStr.equals("")) {
                props.put("mail.smtp.from", fromStr);
            } else {
                props.put("mail.smtp.from", fromAddress);
            }
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.socketFactory.fallback", "false");
            //Get a new instance each time as default instance conflicts with the email read section
            //Session session = Session.getDefaultInstance(props, null);
            javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {

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
                    InternetAddress.parse(toStr, false));
            // -- We could include CC recipients too --
            // if (cc != null)
            // msg.setRecipients(Messaging.RecipientType.CC
            // ,InternetAddress.parse(cc, false));
            // -- Set the subject and body text --
            msg.setSubject(subjectStr);
            // -- Set some other header information --
            msg.setHeader("X-Mailer", "Radio Message Relay");
            msg.setSentDate(new Date());
            String body = bodyStr;
            //msg.setText(body);
            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html");
            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // adds attachments
            /* not yet
            if (mMessage.picture != null) {
                MimeBodyPart attachPart = new MimeBodyPart();
                try {
                    String fullPath = Main.HomePath +
                            Main.Dirprefix + Main.DirImages +
                            Main.Separator + mMessage.fileName.replace(".txt", ".png");
                    attachPart.attachFile(fullPath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                multipart.addBodyPart(attachPart);
            }
            */
            // sets the multi-part as e-mail's content
            msg.setContent(multipart);
            // -- Send the message --
            Transport.send(msg);
            result = "\nMessage sent...\n";
        } catch (Exception ex) {
            //RadioMSG.middleToastText("Error relaying message as Email: " + ex.toString());
            //Save in log for debugging
            result = "Error relaying message as Email: " + ex.toString();
        }
        return result;
    }

    //Request emails headers from mail server
    public static String getHeaderList(int fromNumber) {        

        IMAPFolder folder = null;
        Store store = null;
        String mailHeaders = "";

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
            String emailAddress = Main.configuration.getPreference("SERVEREMAILADDRESS");
            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD");
            //store.connect("imap.googlemail.com",emailAddress, emailPassword);
            store.connect(imapHost, emailAddress, emailPassword);
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
            }
            Message[] messages;
            //Get the number of messages in the folder and compare
            if (fromNumber > folder.getMessageCount()) {
                //Sorry not enough
                mailHeaders = "Sorry only " + folder.getMessageCount() + " mails.\n";
                return mailHeaders;
            }
            //Get all messages
            messages = folder.getMessages();
            //add all headers from the requested number. Protocol issue: QTC 0+ should be QTC 1+ as
            //  email number 1 is first email = email index 0 in the list 
            if (fromNumber == 0) fromNumber = 1; 
            for (int i = fromNumber - 1; i < messages.length; i++) { //From oldest to most recent
                //From email address
                String fromString = messages[i].getFrom()[0].toString();
                //Remove name and only keep email address proper
                String[] emailAdresses = fromString.split("[ <>]+");
                for (String fromPart : emailAdresses) {
                    if (fromPart.indexOf("@") > 0) {
                        fromString = fromPart;
                        break;
                    }
                }
                //Build header line for that email
                //Pskmail does not handle unicode characters (it corrupts the CRC). 
                //  Therefore in plain text mode, strip all non ASCII characters 
                String subjectStr = messages[i].getSubject().replaceAll("\u2013", "-");
                subjectStr = subjectStr.replaceAll("[^a-zA-Z0-9\\n\\s\\<\\>\\!\\[\\]\\{\\}\\:\\;\\\\\'\"\\/\\?\\=\\+\\-\\_\\@\\#\\+\\$\\%\\^\\&\\*,\\.\\(\\)\\|]", "~");
                mailHeaders += " " + (i + 1) + " " + fromString + "  " + subjectStr + " " + messages[i].getSize() + "\n";
            }
            //Provide lead and size
            mailHeaders = "Your mail: " + mailHeaders.length() + "\n" + mailHeaders + "-end-\n";
        } catch (Error err) {
            err.printStackTrace();
            Main.log.writelog("Error accessing Folder: " + err.getMessage() + "\n", true);
            //throw (err);
        } catch (Exception e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", true);
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
        return mailHeaders;
    }

    //Request emails headers from mail server
    public static int getMailCount() {        

        IMAPFolder folder = null;
        Store store = null;
        String mailHeaders = "";

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
            String emailAddress = Main.configuration.getPreference("SERVEREMAILADDRESS");
            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD");
            //store.connect("imap.googlemail.com",emailAddress, emailPassword);
            store.connect(imapHost, emailAddress, emailPassword);
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
            }
            //Get the number of messages in the folder
            int count = folder.getMessageCount();
            return count;
        } catch (Error err) {
            err.printStackTrace();
            Main.log.writelog("Error accessing Folder: " + err.getMessage() + "\n", true);
            //throw (err);
        } catch (Exception e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", true);
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
        return -1; //Means error accessing inbox mail count
    }

    //Extract body text from a message (can be multi-parts, plain text/html...)
    public static String getBodyTextFromMessage_test_Only(Message message) throws Exception {
        String result = "";
        InputStream is = message.getInputStream();
        try {
            String tempAttachFN = Main.HomePath + Main.Dirprefix + "tempAttach";
            File f = new File(tempAttachFN);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }
            fos.close();
            //String encodedStr = Base64.encodeFromFile(tempAttachFN);
            result = RMsgUtil.readFile(tempAttachFN);
        } catch (Exception e) {
            Main.log.writelog("Error with attachment file(s).", e, true);
        }
        return result;
    }

 
    //Extract body text from a message (can be multi-parts, plain text/html...)
    public static String getBodyTextFromMessage(Message message) throws Exception {
        String result = "";
        boolean haveText = false;
        int attachmentCount = 0;
        Date sentDate = message.getSentDate();
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String fromString = message.getFrom()[0].toString();
        //Remove name and only keep email address proper
        String[] emailAdresses = fromString.split("[ <>]+");
        for (String fromPart : emailAdresses) {
            if (fromPart.indexOf("@") > 0) {
                fromString = fromPart;
                break;
            }
        }
        //Date sentDate = message.getSentDate();
        //DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        String emailHeader = "From: " + fromString + "\n"
                + "Date: " + dateFormat.format(sentDate).replaceAll("\\.", "") + "\n"
                + "Subject: " + message.getSubject() + "\n";
        if (message.isMimeType("text/plain")) {
            return emailHeader + message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) { //Limit at one attachment for now
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    if (!haveText) {
                        result = emailHeader + result + bodyPart.getContent() + "\n" ;
                        haveText = true;
                    }
                } else if (bodyPart.isMimeType("text/html")) {
                    if (!haveText) {
                        String html = (String) bodyPart.getContent();
                        result = emailHeader + Jsoup.parse(html).text() + "\n";
                        haveText = true;
                    }
                } else {
                    String attachmentFileName = bodyPart.getFileName();
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                            && !bodyPart.getFileName().equals("") 
                            && ++attachmentCount < 2) {
                        result += "filename=\"" + attachmentFileName + "\"\n" 
                                + "Content-Transfer-Encoding: base64\n\n";
                        InputStream is = bodyPart.getInputStream();
                        try {
                            String tempAttachFN = Main.HomePath + Main.Dirprefix + "tempAttach";
                            File f = new File(tempAttachFN);
                            if (f.exists()) {
                                f.delete();
                            }
                            FileOutputStream fos = new FileOutputStream(f);
                            byte[] buf = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buf)) != -1) {
                                fos.write(buf, 0, bytesRead);
                            }
                            fos.close();
                            String encodedStr = Base64.encodeFromFile(tempAttachFN);
                            result += encodedStr + "\n--00000000000059725805933653d8--"; //any end format will do
                        } catch (Exception e) {
                            Main.log.writelog("Error with attachment file(s).", e, true);
                        }
                    }
                }
            }
            return result;
        } else if (message.isMimeType("text/html")) {
            String html = (String) message.getContent().toString();
            result = emailHeader + Jsoup.parse(html).text() + "\n";
            return result;
        }

        return "";
    }

    //Read one email from the IMAP server
    public static String readMail(int emailNumber, boolean compressedMail) {

        IMAPFolder folder = null;
        Store store = null;
        String returnString = "";

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
            String emailAddress = Main.configuration.getPreference("SERVEREMAILADDRESS");
            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD");
            //store.connect("imap.googlemail.com",emailAddress, emailPassword);
            store.connect(imapHost, emailAddress, emailPassword);
            //folder = (IMAPFolder) store.getFolder("[Gmail]/Spam"); // This doesn't work for other email account
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
            }
            Message message;
            //Get the number of messages in the folder and compare
            if (emailNumber > folder.getMessageCount()) {
                //Sorry not enough
                returnString = "Error, only " + folder.getMessageCount() + " mails.\n";
                return returnString;
            }
            //Get all messages - one at a time for now
            //emailNumber--; //Adjust from email number to message array index
            message = folder.getMessage(emailNumber);
            String fromString = message.getFrom()[0].toString();
            //Remove name and only keep email address proper
            String[] emailAdresses = fromString.split("[ <>]+");
            for (String fromPart : emailAdresses) {
                if (fromPart.indexOf("@") > 0) {
                    fromString = fromPart;
                    break;
                }
            }
            if (compressedMail) {
                returnString = readMailZip(message);
            } else {
                //String emailHeader = "From: " + fromString + "\n"
                //        + "Date: " + dateFormat.format(sentDate).replaceAll("\\.", "") + "\n"
                //        + "Subject: " + message.getSubject() + "\n";
                //Pskmail does not handle unicode characters (it corrupts the CRC). 
                //  Therefore in plain text mode, strip all non ASCII characters 
                returnString = getBodyTextFromMessage(message).replaceAll("\u2013", "-");
                returnString = returnString.replaceAll("[^a-zA-Z0-9\\n\\s\\<\\>\\!\\[\\]\\{\\}\\:\\;\\\\\'\"\\/\\?\\=\\+\\-\\_\\@\\#\\+\\$\\%\\^\\&\\*,\\.\\(\\)\\|]", "~");
                //Provide lead, size and end marker
                returnString = "Your msg: " + returnString.length() + "\n"
                        + returnString + "\n-end-\n";
            }
        } catch (Error err) {
            err.printStackTrace();
            Main.log.writelog("Error accessing Folder: " + err.getMessage() + "\n", true);
            //throw (err);
        } catch (Exception e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", true);
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
        return returnString;
    }
   
    
    //Compresses an email message and provides the exchange initiation string (">FM:") to send to the client
    public static String readMailZip(Message mMessage) {
        String returnString = "";

        //String downloaddir = Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator;

        String codedFile = "";
        String token = "";
        FileInputStream in = null;

        if (mMessage != null) {

            String Destination = Main.TTYCaller;

            String mysourcefile = Main.HomePath + Main.Dirprefix + "tmpfile";
            try {
                String emailBody = getBodyTextFromMessage(mMessage);
                RMsgUtil.saveDataStringAsFile("", mysourcefile, emailBody); //no path as it is included in filename
            } catch (Exception e) {
            }
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
            //token = Long.toString(Math.abs(r.nextLong()), 12);
            token = Long.toString(Math.abs(r.nextLong()), 12);
            token = token.substring(token.length()-6);
            //token = "tmp" + token;

            //codedFile = Main.HomePath + Main.Dirprefix + "Outbox" + Main.Separator + token;
            codedFile = Main.HomePath + Main.Dirprefix + "Outbox" + Main.Separator + Destination + "_-m-_" + token;

            Base64.encodeFileToFile(tmpfile, codedFile);

            File dlfile = new File(tmpfile);
            if (dlfile.exists()) {
                dlfile.delete();
            }

            File mycodedFile = new File(codedFile);
            if (mycodedFile.isFile()) {
                // >FM:PI4TUE:PA0R:Jynhgf:m: :496
                //TrString = ">FM:" + a.callsign + ":" + Destination + ":"
                //        + token + ":u:" + myfile
                //        + ":" + Long.toString(mycodedFile.length()) + "\n";
                returnString = ">FM:" + Main.configuration.getPreference("CALLSIGNASSERVER") + ":" + Destination + ":"
                        + token + ":m:" + " "
                        + ":" + Long.toString(mycodedFile.length()) + "\n";
            }

            File Transactions = new File(Main.Transactions);
            FileWriter tr;
            try {
                tr = new FileWriter(Transactions, true);
                tr.write(returnString);
                tr.close();
            } catch (IOException ex) {
                Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
            }
            String dataString = RMsgUtil.readFile(codedFile);
            returnString += dataString + "\n-end-\n";
        }

        return returnString;
    }

    //Read one email from the IMAP server
    public static String deleteMail(int emailNumber) {

        IMAPFolder folder = null;
        Store store = null;
        String returnString = "";

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
            String emailAddress = Main.configuration.getPreference("SERVEREMAILADDRESS");
            String emailPassword = Main.configuration.getPreference("SERVERPASSWORD");
            //store.connect("imap.googlemail.com",emailAddress, emailPassword);
            store.connect(imapHost, emailAddress, emailPassword);
            //folder = (IMAPFolder) store.getFolder("[Gmail]/Spam"); // This doesn't work for other email account
            folder = (IMAPFolder) store.getFolder("inbox");
            if (!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
            }
            //Get the number of messages in the folder and compare
            if (emailNumber > folder.getMessageCount()) {
                //Sorry not enough
                returnString = "Email # " + emailNumber + " does Not exist.\n";
                return returnString;
            } else {
                //Get the selected messages
                Message message = folder.getMessage(emailNumber);
                //Mark it for deletion
                message.setFlag(Flags.Flag.DELETED, true);
                returnString = "Deleted Email # " + emailNumber + "\n";
            }
        } catch (Error err) {
            err.printStackTrace();
            Main.log.writelog("Error accessing Folder: " + err.getMessage() + "\n", true);
            //throw (err);
        } catch (Exception e) {
            Exception e1 = e;
            Main.log.writelog("Error accessing emails: " + e1.getMessage() + "\n", true);
        } finally {
            try {
                if (folder != null && folder.isOpen()) {
                    // expunges the folder to remove messages which are marked deleted
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                //do nothing
            }
        }
        return returnString;
    }
   

    //Read a web page as supplied and optionally trim with the begin: and end: strings
    public static String readWebPage(String webURL, String startStopStr, boolean compressedPage) {
        String webPage = "";
        String begin = "";
        String end = "";
        try {
            Pattern STm;
            Matcher st;
            //Make sure the url is properly formed. Assume www.abc is http://www.abc
            if (webURL.startsWith("www")) {
                webURL = "http://" + webURL;
            }
            Document doc = Jsoup.connect(webURL).get();
            if (doc != null) {
                //Preserve or convert line breaks
                doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
                doc.select("br").append("\\n");
                doc.select("p").prepend("\\n");
                String s = doc.html().replaceAll("\\\\n", "\n");
                //webPage = doc.text();
                webPage = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            }
            //Extract begin and end limits if any
            if (startStopStr.contains("end:")) {
                STm = Pattern.compile("\\s*(begin:.*)?(end:.*)");
                st = STm.matcher(startStopStr);
                if (st.lookingAt()) {
                    if (st.group(1) != null) {
                        begin = st.group(1).replaceFirst("begin:", "").trim();
                    }
                    if (st.group(2) != null) {
                        end = st.group(2).replaceFirst("end:", "").trim();
                    }
                }
            } else {
                //No end: statement, maybe just a begin:
                STm = Pattern.compile("\\s*(begin:.*)?");
                st = STm.matcher(startStopStr);
                if (st.lookingAt()) {
                    if (st.group(1) != null) {
                        begin = st.group(1).replaceFirst("begin:", "").trim();
                    }
                }
            }
            //Trim required? begin: abc
            if (!begin.equals("")) {
                if (webPage.contains(begin)) {
                    //Found, trim then
                    webPage = webPage.substring(webPage.indexOf(begin));
                }
            }
            //Trim required? end: xyz
            if (!end.equals("")) {
                if (webPage.contains(end)) {
                    //Found, trim then
                    webPage = webPage.substring(0, webPage.lastIndexOf(end));
                }
            }
            if (compressedPage) {
                webPage = tgetZip(webPage);
            } else {
                //Pskmail does not deal in unicode (the CRC gets corrupted), replace or strip any non ASCII character
                webPage = webPage.replaceAll("\u2013", "-");
                webPage = webPage.replaceAll("[^a-zA-Z0-9\\n\\s\\<\\>\\!\\[\\]\\{\\}\\:\\;\\\\\'\"\\/\\?\\=\\+\\-\\_\\@\\#\\+\\$\\%\\^\\&\\*,\\.\\(\\)\\|]", "~");
                webPage = "Your wwwpage: " + webPage.length() + "\n"
                        + webPage + "\n-end-\n";
            }
        } catch (Exception e) {
            webPage = "Error getting web page: " + e.getMessage() + "\n";
        }
        return webPage;
    }
    
    //Compresses an email message and provides the exchange initiation string (">FM:") to send to the client
    public static String tgetZip(String webPage) {
        String returnString = "";

        //String downloaddir = Main.HomePath + Main.Dirprefix + "Downloads" + Main.Separator;

        String codedFile = "";
        String token = "";
        FileInputStream in = null;

        if (webPage.length() != 0) {

            String Destination = Main.TTYCaller;

            String mysourcefile = Main.HomePath + Main.Dirprefix + "tmpfile";
            try {
                RMsgUtil.saveDataStringAsFile("", mysourcefile, webPage); //no path as it is included in filename
            } catch (Exception e) {
            }
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
            //token = Long.toString(Math.abs(r.nextLong()), 12);
            token = Long.toString(Math.abs(r.nextLong()), 12);
            token = token.substring(token.length()-6);
            //token = "tmp" + token;

            codedFile = Main.HomePath + Main.Dirprefix + "Outbox" + Main.Separator + Destination + "_-w-_" + token;

            Base64.encodeFileToFile(tmpfile, codedFile);

            File dlfile = new File(tmpfile);
            if (dlfile.exists()) {
                dlfile.delete();
            }

            File mycodedFile = new File(codedFile);
            if (mycodedFile.isFile()) {
                // >FM:PI4TUE:PA0R:Jynhgf:w: :496
                //TrString = ">FM:" + a.callsign + ":" + Destination + ":"
                //        + token + ":u:" + myfile
                //        + ":" + Long.toString(mycodedFile.length()) + "\n";
                returnString = ">FM:" + Main.configuration.getPreference("CALLSIGNASSERVER") + ":" + Destination + ":"
                        + token + ":w:" + " "
                        + ":" + Long.toString(mycodedFile.length()) + "\n";
            }

            File Transactions = new File(Main.Transactions);
            FileWriter tr;
            try {
                tr = new FileWriter(Transactions, true);
                tr.write(returnString);
                tr.close();
            } catch (IOException ex) {
                Logger.getLogger(mainpskmailui.class.getName()).log(Level.SEVERE, null, ex);
            }
            String dataString = RMsgUtil.readFile(codedFile);
            returnString += dataString + "\n-end-\n";
        }

        return returnString;
    }


    public static String getPendingList(String server, String caller) {
        String returnList = "";

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
                        
                if (pendingCaller.equals(caller)) {
                    //Add this file to the list of pending downloads
                    //>FO5:PI4TUE:PA0R:JhyJkk:f:test.txt:496
                    returnList += ">FO5:" + server + ":" + caller + ":" + pendingToken + ":" + pendingType + ": :" + filesOutbox[i].length() + "\n";
                } 
                
            }
        }

   
        return returnList;
    }
    
}
