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

//import android.graphics.Bitmap;
//import android.location.Location;

import java.util.LinkedList;

/**
 * Created by jdouyere on 31/12/16.
 *
 * Message objects list for gps position updates (when available) and TXing
 */

public class RMsgTxList {

    public static LinkedList messageList = new LinkedList();


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



    //Iterate through the list and update the locations with currently
    // passed location if the boolean msgHasPosition is true AND the location is null
    synchronized public static void updateLocation(RMsgLocation msgLocation) {
        RMsgObject messageObject = null;
        int listLength = messageList.size();

        //Iterate through the list and update the locations with currently
        // passed location if the boolean msgHasPosition is true AND the location is null
        for (int ii=0; ii < listLength; ii++) {
            messageObject = (RMsgObject) messageList.get(ii);
            if (messageObject.msgHasPosition && messageObject.position == null) {
                messageObject.position = msgLocation;
                messageObject.positionAge = (int)((System.currentTimeMillis() - messageObject.positionRequestTime) / 1000);
                messageList.set(ii, messageObject);
            }
        }
    }



    //Without image data
    synchronized public static void addMessageToList(String msgTo, String via, String msgSms,
                                                          Boolean msgHasPosition, RMsgLocation msgLocation, long positionRequestTime,
                                                          Short[] msgVoiceMessage) {

        RMsgObject messageObject = new RMsgObject(msgTo, via, msgSms, null, 0, false, 0, msgHasPosition,
                msgLocation, positionRequestTime, msgVoiceMessage);
        messageObject.from = RMsgProcessor.getCall();
        messageList.addFirst(messageObject);
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



    //Add pre-created message object (e.g when forwarding)
    synchronized public static void addMessageToList(RMsgObject mMessage) {

        messageList.addFirst(mMessage);
    }

}
