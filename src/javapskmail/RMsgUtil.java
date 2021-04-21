/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

/**
 *
 * @author jdouyere
 */
/*
 * Message.java  
 *
 * Copyright (C) 2014 John Douyere (VK2ETA) 
 *
 * This program is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU General Public License for more details.  
 *
 * You should have received a copy of the GNU General Public License  
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//tbf import android.graphics.Bitmap;


public class RMsgUtil {




    public static void addEntryToLog(String entry) {
        String logFileName = Main.HomePath + Main.Dirprefix + "RadioMsg.log";
        File logFile = new File(logFileName);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                //loggingclass.writelog("IO Exception Error in Create file in 'addEntryToLog' " + e.getMessage(), null, true);
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(entry);
            buf.newLine();
            buf.flush();
            buf.close();
        }
        catch (IOException e)
        {
            //loggingclass.writelog("IO Exception Error in add line in 'addEntryToLog' " + e.getMessage(), null, true);
        }
    }


/*
    public static boolean saveMessageAsFile(String filePath, String fileName, String dataString) {
        FileWriter out = null;
        File fileToSave = new File(filePath + fileName);
        try
        {
            if (fileToSave.exists()) {
                fileToSave.delete();
            }
            out = new FileWriter(fileToSave, true);
            out.write(dataString);
            out.close();
            //Update the serial number used for the file name
            if (config.getPreferenceB("SERNBR_FNAME", true)) {
                int serNbr = config.getPreferenceI("SERNBR", 1);
                config.setPreferenceS("SERNBR", Integer.toString(++serNbr));
            }
            RadioMSG.topToastText("\nSaved file: " + fileName + "\n");
            //addEntryToLog(Messaging.dateTimeStamp() + ": Saved Messaging file " + fileName);
        }
        catch (IOException e)
        {
            loggingclass.writelog("Error creating file", e, true);
            return false;
        }
        return true;
    }
*/


    //Save a datastring into a file as specified
    public static boolean saveDataStringAsFile(String filePath, String fileName, String dataString) {
        FileWriter out = null;
        File fileToSave = new File(filePath + fileName);
        try
        {
            if (fileToSave.exists()) {
                fileToSave.delete();
            }
            out = new FileWriter(fileToSave, true);
            out.write(dataString);
            out.close();
            //RadioMSG.topToastText("\nSaved file: " + fileName + "\n");
            //addEntryToLog(Messaging.dateTimeStamp() + ": Saved file " + fileName);
        }
        catch (IOException e)
        {
            //loggingclass.writelog("Error creating file", e, true);
            return false;
        }
        return true;
    }



    //Delete file
    public static boolean deleteFile(String mFolder, String fileName, boolean adviseDeletion) {

        String fullFileName = Main.HomePath + Main.Dirprefix + mFolder + Main.Separator + fileName;
        File n = new File(fullFileName);
        if (!n.isFile()) {
            //RadioMSG.middleToastText("Message file " + fileName + " Not Found. Deleted?");
            return false;
        } else {
            n.delete();
            if (adviseDeletion) {
                //RadioMSG.middleToastText("Message Deleted...");
                //addEntryToLog(Messaging.dateTimeStamp() + ": Deleted file " + fileName);
            }
            return true;
        }
    }



    //Copy binary or text files from one folder to another CONSERVING THE NAME and CONDITIONALLY LOGGING THE ACTION
    public static boolean copyAnyFile(String originFolder, String fileName, String destinationFolder, boolean adviseCopy) {

        File dir = new File(Main.HomePath + Main.Dirprefix + destinationFolder);
        if (dir.exists()) {
            String fullFileName = Main.HomePath + Main.Dirprefix + originFolder + Main.Separator + fileName;
            File mFile = new File(fullFileName);
            if (!mFile.isFile()) {
                //Main.PostToTerminal("File " + fileName + " not found in " + destinationFolder + "\n");
                return false;
            } else {
                FileOutputStream fileOutputStrm = null;
                FileInputStream fileInputStrm = null;
                try {
                    fileInputStrm = new FileInputStream(fullFileName);
                    String fullDestinationFileName = Main.HomePath + Main.Dirprefix + destinationFolder + Main.Separator + fileName;
                    fileOutputStrm = new FileOutputStream(fullDestinationFileName);
                    byte[] mBytebuffer = new byte[256];
                    int byteCount = 0;
                    while ((byteCount = fileInputStrm.read(mBytebuffer)) != -1) {
                        fileOutputStrm.write(mBytebuffer, 0, byteCount);
                    }
                }
                catch (FileNotFoundException e) {
                    //Main.PostToTerminal("File not found: " + fullFileName + "\n");
                }
                catch (IOException e) {
                    //Main.PostToTerminal("Error copying: " + fileName + " " + e + "\n");
                }
                finally {
                    try {
                        if (fileInputStrm != null) {
                            fileInputStrm.close();
                        }
                        if (fileOutputStrm != null) {
                            fileOutputStrm.close();
                        }
                    }
                    catch (IOException e) {
                        //Main.PostToTerminal("File close error: " + e + "\n");
                    }
                }
                //if (adviseCopy)
                //    addEntryToLog(Messaging.dateTimeStamp() + ": Copied file " + fileName + " to " + destinationFolder);
                return true;
            }
        } else {
            //Main.PostToTerminal("Directory not found: " + destinationFolder + "\n");
            return false;
        }
    }


    //Copy binary or text files from one folder to another WHILE CHANGING THE NAME
    public static boolean copyAnyFile(String originFolder, String fileName,
                                      String destinationFolder, String newFileName) {
        boolean result = true;

        File dir = new File(Main.HomePath + Main.Dirprefix + destinationFolder);
        if (dir.exists()) {
            String fullFileName = Main.HomePath + Main.Dirprefix + originFolder + Main.Separator + fileName;
            File mFile = new File(fullFileName);
            if (!mFile.isFile()) {
                result = false;
            } else {
                FileOutputStream fileOutputStrm = null;
                FileInputStream fileInputStrm = null;
                try {
                    fileInputStrm = new FileInputStream(fullFileName);
                    String fullDestinationFileName = Main.HomePath + Main.Dirprefix + destinationFolder + Main.Separator + newFileName;
                    fileOutputStrm = new FileOutputStream(fullDestinationFileName);
                    byte[] mBytebuffer = new byte[256];
                    int byteCount = 0;
                    while ((byteCount = fileInputStrm.read(mBytebuffer)) != -1) {
                        fileOutputStrm.write(mBytebuffer, 0, byteCount);
                    }
                }
                catch (FileNotFoundException e) {
                    //Main.PostToTerminal("File not found: " + fullFileName + "\n");
                    result = false;
                }
                catch (IOException e) {
                    //Main.PostToTerminal("Error copying: " + fileName + " " + e + "\n");
                    result = false;
                }
                finally {
                    try {
                        if (fileInputStrm != null) {
                            fileInputStrm.close();
                        }
                        if (fileOutputStrm != null) {
                            fileOutputStrm.close();
                        }
                    }
                    catch (IOException e) {
                        //Main.PostToTerminal("File close error: " + e + "\n");
                        result = false;
                    }
                }
            }
        } else {
            //Main.PostToTerminal("Directory not found: " + destinationFolder + "\n");
            result = false;
        }
        //if (result) addEntryToLog(Messaging.dateTimeStamp() + ": Copied file " + newFileName + " to " + destinationFolder);
        return result;
    }



    public static double Round(double Rval, int Rpl) {
        double p = Math.pow(10,Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return tmp/p;
    }

    
    //Takes the GPS location in priority from a connected GPS (NMEA via 
    //  serial port OR GPSD) or, if not present, from the preferences
    private static RMsgLocation getBestPosition() {
        double myLatitude = 0.0f;
        double myLongitude = 0.0f;
        try {
            String latstring;
            String lonstring;
            // Get the GPS position data or the preset data
            if (Main.gpsdata.getFixStatus()) {
                if (!Main.HaveGPSD) {
                    latstring = Main.gpsdata.getLatitude();
                    lonstring = Main.gpsdata.getLongitude();
                } else {
                    latstring = Main.GPSD_latitude;
                    lonstring = Main.GPSD_longitude;
                }
                //course = Main.gpsdata.getCourse();
                //speed = Main.gpsdata.getSpeed();
            } else {
                // Preset data
                latstring = Main.configuration.getPreference("LATITUDE", "0.0f");
                lonstring = Main.configuration.getPreference("LONGITUDE", "0.0f");
            }
            myLatitude = Double.parseDouble(latstring);
            myLongitude = Double.parseDouble(lonstring);
            RMsgLocation myPosition = new RMsgLocation("");
            myPosition.setLatitude(myLatitude);
            myPosition.setLongitude(myLongitude);
            return myPosition;
        } catch (Exception e) {
            return null;
        }
    }

    //Queues a GPS position fix and sends position
    public static void sendPosition(String smsMessage) {
        long positionRequestTime = System.currentTimeMillis();

        RMsgLocation myPosition = getBestPosition();
        //tbf txMessageList.addMessageToList (RadioMSG.selectedTo, RadioMSG.selectedVia, smsMessage,
        RMsgTxList.addMessageToList ("*", "", smsMessage,
                true, myPosition, positionRequestTime,
                null);
        //Now request fast periodic updates
        //The cancellation of these requests is done on first receipt of a location fix
        //tbf RadioMSG.myInstance.requestQuickGpsUpdate();
    }

    //Queues a GPS position fix and sends position
    public static void replyWithPosition(String to, String via, String rxMode) {

        long positionRequestTime = System.currentTimeMillis();
        RMsgObject replyMessage = new RMsgObject();
        //Reply in the same mode as the request
        replyMessage.rxMode = rxMode;
        replyMessage.from = Main.callsignAsServer.trim();
        replyMessage.to = to;
        replyMessage.via = via;
        replyMessage.msgHasPosition = true;
        replyMessage.positionRequestTime = positionRequestTime;
        replyMessage.positionAge = 0; //no time delay here
        replyMessage.position = getBestPosition();

        //Queue regardless
        RMsgTxList.addMessageToList(replyMessage);
    }


    
    //Queues a message with a reply text
    public static void replyWithText(RMsgObject mMessage, String replyText) {

        RMsgObject replyMessage = new RMsgObject();
        replyMessage.sms = replyText;
        //Reply in the same mode as the request
        replyMessage.rxMode = mMessage.rxMode;
        replyMessage.from = Main.callsignAsServer.trim();
        replyMessage.to = mMessage.from;
        replyMessage.via = mMessage.via;
        RMsgTxList.addMessageToList(replyMessage);
    }

    //Extract the destination from an "alias=destination" To address field
    public static String extractDestination(String toAliasAndDestination) {
        
        Pattern psc = Pattern.compile("^\\s*(.+)\\s*=(.*)\\s*$");
        Matcher msc = psc.matcher(toAliasAndDestination);
        if (msc.lookingAt()) {
            if (!msc.group(2).equals("")) {
                return msc.group(2);
            }
        } 
        //We must have a strait callsign address, return as-is
        return toAliasAndDestination;
    }

    //Extract the alias from an "alias=destination" address field, return string in "alias=" format
    public static String extractAliasOnly(String toAliasAndDestination) {
        
        Pattern psc = Pattern.compile("^\\s*(.+)\\s*=(.*)\\s*$");
        Matcher msc = psc.matcher(toAliasAndDestination);
        if (msc.lookingAt()) {
            if (!msc.group(1).equals("")) {
                //Found, return "alias="
                return msc.group(1) + "=";
            }
        } 
        //No alias, return blank
        return "";
    }



    //Takes the temp bitmap and creates a byte array ready for TX by MFSK
    public static byte[] extractPictureArray(Bitmap myBitmap) {
        String attachmentBuffer;

        try {
            //Propose GC at this point as the ByteBuffer allocation is memory intensive
            System.gc();
            //For resizing
            //Bitmap.createScaledBitmap(yourBitmap, 50, 50, true); // Width and Height in pixel e.g. 50
            //Size of buffer containing the Bitmap
            int attachedPictureWidth = myBitmap.getWidth();
            int attachedPictureHeight = myBitmap.getHeight();
            int pictureArraySize =  attachedPictureWidth *
                    attachedPictureHeight * 4;
            //Extract RGB array from Bitmap
            ByteBuffer byteBuffer = ByteBuffer.allocate(pictureArraySize);
            myBitmap.copyPixelsToBuffer(byteBuffer);
            //Blank it out as we are finished with it
            //NO, Keep it in case we press send again
            // RadioMSG.tempImageBitmap = null;
            //Possible future action includes savings as-is ready for re-send
            System.gc();
            byte[] attachedPicture = new byte[pictureArraySize];
            byteBuffer.rewind();
            byteBuffer.get(attachedPicture);
            byteBuffer = null;
            System.gc();
            return attachedPicture;
        }
        catch (Exception e)
        {
            //Invalid bitmap data
            //loggingclass.writelog("General Exception Error in 'extractPictureArray' " + e.getMessage(), null, true);
            return null;
        }
    }



    //Takes a directory for the data file, a data file name
    //Returns the text content of the file
    public static String readFile(String mDir, String mFileName) {
        String dataString = new String();
        String readString = new String();

        try
        {
            //First separate the header from the data fields (Headers are never compressed)
            //For this read the data file until we find the form information
            File fi = new File(Main.HomePath + Main.Dirprefix
                    + mDir + Main.Separator + mFileName);
            FileInputStream fileISi = new FileInputStream(fi);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fileISi));
            dataString = "";
            //Handles both hard-coded forms and custom forms
            while ((readString = buf.readLine()) != null)
            {
                dataString += readString + "\n";
            }
        }
        catch (FileNotFoundException e)
        {
            //RadioMSG.middleToastText("Message file " + mFileName + " Not Found. Deleted?");
        }
        catch (IOException e)
        {
            //loggingclass.writelog("IO Exception Error in 'readFile' " + e.getMessage(), null, true);
        }
        return dataString;
    }

    
    //Takes a data file name (including path)
    //Returns the text content of the file
    public static String readFile(String mFileName) {
        String fileAsString = "";

        try {
            FileInputStream is = new FileInputStream(mFileName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = buf.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            fileAsString = sb.toString();
        } catch (FileNotFoundException e) {
            //RadioMSG.middleToastText("Message file " + mFileName + " Not Found. Deleted?");
        } catch (IOException e) {
            //loggingclass.writelog("IO Exception Error in 'readFile' " + e.getMessage(), null, true);
        }
        return fileAsString;
    }

    // Creates an array of flmsg smsview file names
    public static String[] createFileListFromFolder(String mFolder)
    {
        String[] fileNamesArray = null;
        try
        {
            // Get the list of files in the designated folder
            File dir = new File(Main.HomePath + Main.Dirprefix
                    + mFolder);
            File[] files = dir.listFiles();
            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file)
                {
                    return file.isFile();
                }
            };

            // We should now have an array of strings containing the file names
            files = dir.listFiles(fileFilter);
            if (files.length > 0) {
                fileNamesArray = new String[files.length] ;
                for (int i = 0; i < files.length; i++)
                {
                    fileNamesArray[i] = files[i].getName();
                }
            }
        } catch (Exception e) {
            //loggingclass.writelog("Error when listing Folder: " + mFolder + "\nDetails: ", e, true);
        }
        return fileNamesArray;
    }

    //Send a single beep or double beep by sending a Tune command to the modem for short periods of time
    public static void sendBeeps(final boolean positiveAck) {

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    //int ackPosition = config.getPreferenceI("ACKPOSITION", 0);
                    int ackPosition = 1; //for now
                    //boolean onlyForAlerts = config.getPreferenceB("ACKONLYALERTS", false);
                    if (ackPosition > 0) {
                        //Wait for any TXing to complete
                        int waitCount = 0;
                        while (Main.TXActive && waitCount < 100) { //Max 5 seconds
                            Thread.sleep(50);
                            waitCount++;
                        }
                        //Only send if we waiting less than 5 seconds otherwise it is pointless
                        if (waitCount < 100) {
                            //Mark as TXing
                            Main.TXActive = true;
                            //Time for reception to finish trailing tones
                            //Wait 3 seconds for the Rx to be fully completed
                            int remainingSleep = 3000 - 100 * waitCount;
                            remainingSleep = remainingSleep < 0 ? 0 : remainingSleep;
                            Thread.sleep(remainingSleep);
                            //int oldAudioFreq = 0;
                            //If CRC is wrong send double tone
                            if (positiveAck) {
                                Rigctl.Tune();
                                Thread.sleep(150);
                                Rigctl.Abort();
                            } else {
                                //oldAudioFreq = Rigctl.SetAudiofreq(2200); //Does not work if either TX lock or reset to carrier is on
                                for (int x = 0; x < 5; x++) {
                                    Rigctl.Tune();
                                    Thread.sleep(100);
                                    Rigctl.Abort();
                                    //Restore modem to last audio frequency
                                    Thread.sleep(300);
                                }
                                //if (oldAudioFreq > 0 && !mMessage.crcValid && !mMessage.crcValidWithPW) {
                                //    Rigctl.SetAudiofreq(oldAudioFreq);
                                //}
                            }
                            Main.TXActive = false;
                        }
                    }
                } catch (Exception e) {
                    //loggingclass.writelog("Exception Error in 'sendBeeps' " + e.getMessage(), null, true);
                }
            }
        };
        myThread.start();
    }
}
