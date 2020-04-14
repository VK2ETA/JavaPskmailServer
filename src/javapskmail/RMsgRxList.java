
package javapskmail;

/**
 *
 * @author jdouyere
 */

/**
 *
 * @author jdouyere
 */

//import android.graphics.Bitmap;
//import android.location.Location;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jdouyere on 31/12/16.
 *
 * Message objects list for received messages
 */

public class RMsgRxList {

    public static LinkedList<RMsgObject> messageList = new LinkedList();

   
    //Get length of message list
    synchronized public static int getLength() {

        return messageList.size();
    }



    //Get length of message list
    synchronized public static int getAvailableLength() {
        int availableforTx = 0;
        RMsgObject messageObject = null;
        int listLength = messageList.size();

        //Iterate through the list and count only the locations with updated gps coordinates
        for (int ii=0; ii < listLength; ii++) {
            messageObject = (RMsgObject) messageList.get(ii);
            if (!messageObject.msgHasPosition || (messageObject.msgHasPosition && messageObject.position != null)) {
                availableforTx++;
            }
        }
        return availableforTx;
    }



    //Get last message of list (the oldest in the FIFO queue)
    //Must be consistent in regards to gps location information
    // Therefore ignore objects that are awaiting location updates
    synchronized public static RMsgObject getOldest() {
        RMsgObject messageObject = null;
        int listLength = messageList.size();

        if (listLength > 0) {
            //Iterate through the list and count only the locations with updated gps coordinates
            for (int ii=listLength - 1; ii >= 0; ii--) {
                messageObject = (RMsgObject) messageList.get(ii);
                if (!messageObject.msgHasPosition || (messageObject.msgHasPosition && messageObject.position != null)) {
                    return (RMsgObject) messageList.remove(ii);
                }
            }
        }
        return null;
    }



    //Get (and remove) the earliest message in the list (the youngest in the FIFO queue)
    // Includes objects that are awaiting location updates
    synchronized public static RMsgObject getLatest() {
        RMsgObject messageObject = null;
        int listLength = messageList.size();

        if (listLength > 0) {
            return (RMsgObject) messageList.remove(0);
        }
        return null;
    }
    
    
    
    //To prevent sending full details over the air every time for brievety and protection, we
    // allow the To address destination to be aliased. The first transmission is expected to
    // contain the full information in the format "alias=destination". Subsequent transmissions
    // may only contain "alias=" as the To address destination. A backward (lastest to earliest)
    // scan of the received message list allows to find the previous match to convert alias only to 
    // alias=destination format which is then stored in that message (for potentially being used
    // as a match later on). Each "From" callsign have their own alias=destination combinations.
    synchronized public static String getAliasAndDestination(String toAlias, String fromStr) {

        Pattern psc = Pattern.compile("^\\s*(.+)\\s*=(.*)\\s*$");
        Pattern pscf = Pattern.compile("^\\s*(.+)\\s*=(.+)\\s*$");
        Matcher msc = psc.matcher(toAlias);
        if (msc.lookingAt()) {
            String group2 = msc.group(2);
            if (group2.equals("")) {
                //We have an alias alone, try to find a previous message with "alias=destination"
                int listLength = messageList.size();
                RMsgObject messageObject = null;
                if (listLength > 0) {
                    //Iterate through the list and count only the locations with updated gps coordinates
                    for (int ii = listLength - 1; ii >= 0; ii--) {
                        messageObject = (RMsgObject) messageList.get(ii);
                        msc = pscf.matcher(messageObject.to);
                        if (msc.lookingAt()) {
                            if (toAlias.equals(msc.group(1) + "=") 
                                    && !msc.group(2).equals("") 
                                    && !msc.group(2).equals("**unknown**") 
                                    && messageObject.from.equals(fromStr)) {
                                //We have a match
                                return toAlias + msc.group(2);
                            }
                        }
                    }
                }
                //No records or No match, then return an error message for further processing
                return toAlias + "**unknown**";
            }
        } 
        //We have a (new) full alias and destination combination
        //  OR  we have a strait callsign address, return as-is
        return toAlias;
    }
    
    //Reverse look-up of origin to alias. Used when sending an email or SMS from this relay to others over the air.
    //Returns origin is no match (the email address or cellular number will be transmitted as-is).
    synchronized public static String getAliasFromOrigin(String origin, String toStr) {

        Pattern psc = Pattern.compile("(^[\\w.-]+@\\w+\\.[\\w.-]+)|(^\\+?\\d{8,16})");
        Pattern pscf = Pattern.compile("^\\s*(.+)\\s*=(.+)\\s*$");
        Matcher msc = psc.matcher(origin);
        if (msc.lookingAt()) {
            //Try to find a previous message with "alias=origin" from this callsign
            int listLength = messageList.size();
            RMsgObject messageObject = null;
            if (listLength > 0) {
                //Iterate through the list and search for a previous full alias=destination entry (for that destination callsign)
                for (int ii = listLength - 1; ii >= 0; ii--) {
                    messageObject = messageList.get(ii);
                    if (!messageObject.myOwn) {
                        msc = pscf.matcher(messageObject.to);
                        if (msc.lookingAt()) {
                            if (origin.equals(msc.group(2))
                                    && !msc.group(1).equals("")
                                    && messageObject.from.equals(toStr)) {
                                //We have a match, return "alias=" only
                                return msc.group(1) + "=";
                            }
                        }
                    }
                }
            }
            //We have no match, return email address or cellular number as is
            return origin;

        }
        //This does not look like a cellular number or email address, return as-is
        return origin;
    }


    //With image data
    synchronized public static void addMessageToList(String msgTo, String via, String msgSms,
                                                          Bitmap msgPicture, int pictureTxSPP, boolean pictureColour, int imageTxModemIndex,
                                                          Boolean msgHasPosition, RMsgLocation msgLocation, long positionRequestTime) {
        RMsgObject messageObject = null;

        messageObject = new RMsgObject(msgTo, via, msgSms,
                msgPicture, pictureTxSPP, pictureColour, imageTxModemIndex,
                msgHasPosition, msgLocation, positionRequestTime, null);
        messageObject.from = RMsgProcessor.getCall();
        messageList.addFirst(messageObject);
    }

    synchronized public static RMsgObject getItem(int pos) {
        return messageList.get(pos);
    }

    //Add pre-created message object (e.g when forwarding)
    synchronized public static void addMessageToList(RMsgObject mMessage) {

        //messageList.addFirst(mMessage); //Issue on desktop java where the order is not right, try this
        messageList.add(mMessage);
    }

    synchronized public static boolean isDuplicate(RMsgObject mMessage) {
        boolean isDuplicate = false;
        //We have an alias alone, try to find a previous message with "alias=destination"
        int listLength = messageList.size();
        RMsgObject recMessage = null;
        if (listLength > 0) {
            //Iterate through the list and count only the locations with updated gps coordinates
            int trialCount = 0;
            for (int ii = listLength - 1; ii >= 0; ii--) {
                //No need to check past the two previous messages
                if (++trialCount > 2) break;
                recMessage = (RMsgObject) messageList.get(ii);
                //Looks like the same message was received direct and via a relay?
                if (recMessage.from.equals(mMessage.from)
                        && recMessage.via.equals(mMessage.relay)
                        && recMessage.sms.equals(mMessage.sms)) {
                    //Check the time difference
                    if (mMessage.timeId.trim().length() == 3) {
                        try {
                            int thisTimeIdMin = Integer.parseInt(mMessage.timeId.substring(0, 1));
                            int thisTimeIdSec = Integer.parseInt(mMessage.timeId.substring(1));
                            int thisTimeId = thisTimeIdMin * 60 + thisTimeIdSec;
                            int strLen = recMessage.fileName.length();
                            //Extract the value of the last 3 digits in the filename = units of minutes and seconds
                            int recTimeIdMin = Integer.parseInt(recMessage.fileName.substring(strLen - 7, strLen - 6));
                            int recTimeIdSec = Integer.parseInt(recMessage.fileName.substring(strLen - 6, strLen - 4));
                            int recTimeId = recTimeIdMin * 60 + recTimeIdSec;
                            int deltaTime = 0;
                            if (recTimeId > thisTimeId) {
                                deltaTime = recTimeId - thisTimeId;
                            } else {
                                deltaTime = thisTimeId - recTimeId;
                            }
                            //Wrap around when getting past 9 Minutes 59 Seconds
                            if (deltaTime > 59) {
                                deltaTime = 600 - deltaTime;
                            }
                            if (deltaTime < 11) {
                                //10 seconds leeway
                                isDuplicate = true;
                            }
                        } catch (Exception e) {
                            //nothing we can do as we have bad information
                        }
                    }
                    
                }
            }
        }
        return isDuplicate;
    }
  
    
}

