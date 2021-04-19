package javapskmail;

/**
 *
 * @author jdouyere
 */

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John Douyere (VK2ETA) on 07/02/17.
 */

//public class RMsgDisplayList extends ArrayAdapter<RMsgDisplayItem> {
public class RMsgDisplayList {

    ArrayList<RMsgDisplayItem> displayList = new ArrayList<RMsgDisplayItem>();

    //public RMsgDisplayList(Context context, int textViewResourceId, ArrayList<RMsgDisplayItem> objects) {
    //    super(context, textViewResourceId, objects);
    //    displayList = objects;
    //}
    
    public RMsgDisplayList(ArrayList<RMsgDisplayItem> objects) {
        //super(context, textViewResourceId, objects);
        displayList = objects;
    }

    //@Override
    //synchronized public int getCount() {
    //    return super.getCount();
    //}

    /*
    @Override
    synchronized public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.smslistview, null);
        TextView textView = (TextView) v.findViewById(R.id.textmsg);
        RMsgDisplayItem mDisplayItem = displayList.get(position);
        String msgText;
        if (mDisplayItem.myOwn) {
            //Sent by me, shift to the right
            TextView paddingView = (TextView) v.findViewById(R.id.padding);
            paddingView.setVisibility(View.VISIBLE);
            //paddingView.setBackgroundColor(RadioMSG.myInstance.getResources().getColor(R.color.BLACK));
            //paddingView.setText("  ");
        }
        if (mDisplayItem.mMessage.position != null && mDisplayItem.inRange) {
            msgText = mDisplayItem.mMessage.formatForList(true);
            float firstWarningDistance = (float) config.getPreferenceD("TRACKINGFIRSTWARNING", 350.0f);
            float secondWarningDistance = (float) config.getPreferenceD("TRACKINGSECONDWARNING", 200.0f);
            float thirdWarningDistance = (float) config.getPreferenceD("TRACKINGTHIRDWARNING", 50.0f);
            if (mDisplayItem.currentDistance <= thirdWarningDistance) {
                textView.setBackgroundColor(ContextCompat.getColor(RadioMSG.myContext, R.color.YELLOW));//getColor(R.color.YELLOW));
                textView.setTextColor(ContextCompat.getColor(RadioMSG.myContext, R.color.BLACK));//getColor(R.color.BLACK));
            } else if (mDisplayItem.currentDistance <= secondWarningDistance) {
                textView.setBackgroundColor(ContextCompat.getColor(RadioMSG.myContext, R.color.RED));//RED));
                textView.setTextColor(ContextCompat.getColor(RadioMSG.myContext, R.color.WHITE));//getColor(R.color.WHITE));
            } else if (mDisplayItem.currentDistance <= firstWarningDistance) {
                textView.setBackgroundColor(ContextCompat.getColor(RadioMSG.myContext, R.color.DARKGREEN));//getColor(R.color.DARKGREEN));
                textView.setTextColor(ContextCompat.getColor(RadioMSG.myContext, R.color.WHITE));//WHITE));
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        } else {
            msgText = mDisplayItem.mMessage.formatForList(false);
            if (!mDisplayItem.mMessage.crcValid && !mDisplayItem.mMessage.crcValidWithPW && !mDisplayItem.myOwn) {
                textView.setBackgroundColor(ContextCompat.getColor(RadioMSG.myContext, R.color.DARKRED));//getColor(R.color.DARKRED));
            }
        }
        textView.setText(msgText);
        //ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        //imageView.setImageResource(displayList.get(position).getAnimalImage());
        return v;

    }
*/


    synchronized public int getsize() {
        return displayList.size();
    }

    synchronized public int getLength() {
        return displayList.size();
    }

    /* FIX ME
    //Update closest message entry index we are converging to (reducing distance)
    synchronized public void updateClosestPois() {

        int closest = -1;
        float minDistance = 999999f; //1000KM max
        //Save value so that it is consistent during the loop processing
        Location mLocation = RadioMSG.currentLocation;
        RMsgDisplayItem mDisplayItem;
        for (int i = 0; i < displayList.size(); i++) {
            mDisplayItem = displayList.get(i);
            //Only for items with a location
            if (mDisplayItem.mMessage.position != null) {
                //Shift current to previous distance
                mDisplayItem.previousDistance = mDisplayItem.currentDistance;
                //Calculate new distance
                mDisplayItem.currentDistance = mDisplayItem.mMessage.position.distanceTo(mLocation);
                if (mDisplayItem.currentDistance < minDistance && mDisplayItem.currentDistance < mDisplayItem.previousDistance) {
                    minDistance = mDisplayItem.currentDistance;
                    closest = i;
                }
                displayList.set(i, mDisplayItem);
            }
        }
        int longestRange = (int) config.getPreferenceD("TRACKINGFIRSTWARNING", 350.0f);
        //Now mark all the POIs which fit within a given radius (50M) and to which we are converging to
        for (int i = 0; i < displayList.size(); i++) {
            mDisplayItem = displayList.get(i);
            //Only for items with a location
            if (mDisplayItem.mMessage.position != null) {
                //Include all items within an extra third warning zone distance to show close sequence of warnings
                if (i == closest || (mDisplayItem.currentDistance <= longestRange && mDisplayItem.currentDistance <
                        mDisplayItem.previousDistance)) {
                    mDisplayItem.inRange = true;
                } else { //reset flag
                    mDisplayItem.inRange = false;
                }
                //update list
                displayList.set(i, mDisplayItem);
            }
        }
        //Request update of listview
        if (RadioMSG.msgArrayAdapter != null) {
            RadioMSG.msgArrayAdapter.notifyDataSetChanged();
        }
        //If not set, set at zero
        //closest = closest < 0 ? 0 : closest;
        RadioMSG.closestPoi = closest;

    }
*/

    synchronized public RMsgDisplayItem getItem(int pos) {
        return displayList.get(pos);
    }
    

    synchronized public RMsgObject getItemMessage(int pos) {
        return displayList.get(pos).mMessage;
    }
    
    
    //Get one message object from the list at the given position
    synchronized public RMsgDisplayItem getDisplayListItem(int atPosition) {
        if (atPosition >= 0 && atPosition < displayList.size()) {
            return displayList.get(atPosition);
        }
        return null;
    }

    //Add new entry in list
    synchronized public void addNewItem(RMsgObject mMessage, boolean myOwn) {
        //mDisplayItem.currentDistance = 999999f; //1000KM
        //mDisplayItem.previousDistance = 990000f; //990KM, more than current so not highlighted
        //mDisplayItem.inRange = false;
        //mDisplayItem.mMessage = mMessage;
        RMsgDisplayItem mDisplayItem = new RMsgDisplayItem(mMessage, 999999f, 0.0f, false, myOwn);
        displayList.add(mDisplayItem);
//FIX me        this.notifyDataSetChanged(); //Scroll to latest item
    }


    //To prevent sending full details over the air every time for brievety and protection, we
    // allow the To address destination to be aliased. The first transmission is expected to
    // contain the full information in the format "alias=destination". Subsequent transmissions
    // may only contain "alias=" as the To address destination. A backward (lastest to earliest)
    // scan of the received message list allows to find the previous match to convert an alias only to
    // an alias=destination format which is then stored in that message (for potentially being used
    // as a match later on). Each From callsign have their own alias=destination combination.
    //In addition, when a full alias=phonenumber is passed, it is returned with the phone number converted
    //  to international format for consistency
    synchronized public String getReceivedAliasAndDestination(String toAlias, String fromStr) {
        String mToAlias = toAlias;
        Pattern psc = Pattern.compile("^\\s*(.+)\\s*=(.*)\\s*$");
        Pattern pscf = Pattern.compile("^\\s*(.+)\\s*=(.+)\\s*$");
        Matcher msc = psc.matcher(mToAlias);
        if (msc.lookingAt()) {
            String group2 = msc.group(2);
            //if the 2nd part of the address is a cellular number, convert to international number so that we can store it properly
            if (RMsgProcessor.isCellular(group2)) {
                mToAlias =  msc.group(1) + "=" + RMsgProcessor.convertNumberToE164(group2);
            }
            if (group2.equals("")) {
                //We have an alias alone, try to find a previously received message with "alias=destination"
                int listLength = displayList.size();
                RMsgDisplayItem mDisplayItem = null;
                if (listLength > 0) {
                    //Iterate through the list and search for a previous full alias=destination entry (for that sending callsign)
                    for (int ii = listLength - 1; ii >= 0; ii--) {
                        mDisplayItem = displayList.get(ii);
                        if (!mDisplayItem.myOwn) {
                            msc = pscf.matcher(mDisplayItem.mMessage.to);
                            if (msc.lookingAt()) {
                                if (toAlias.equals(msc.group(1) + "=")
                                        && !msc.group(2).equals("")
                                        && !msc.group(2).equals("**unknown**")
                                        && mDisplayItem.mMessage.from.equals(fromStr)) {
                                    //We have a match
                                    return toAlias + msc.group(2);
                                }
                            }
                        }
                    }
                }
                //No records or No match, then return an error message for further processing
                return toAlias + "**unknown**";
            }
        }
        //We have a (new) full alias and destination combination
        //  OR  we have a strait callsign/phone number/email address, return as-is
        return mToAlias;
    }


    //As above but for messages we receive from an email or sms relay. The from was aliased to if a record exist.
    //  E.g. I send an sms to joe, I send the alias only ("joe=", which translates at the relay to joebloggs@someemail.com).
    //  When joe responds, I get as the sender: "joe=". I need to workout the full address by looking at the previously sent messages
    synchronized public String getSentDestinationFromAlias(String fromAlias) {

        Pattern psc = Pattern.compile("^\\s*(.+)\\s*=(.*)\\s*$");
        Pattern pscf = Pattern.compile("^\\s*(.+)\\s*=(.+)\\s*$");
        Matcher msc = psc.matcher(fromAlias);
        if (msc.lookingAt()) {
            String group2 = msc.group(2);
            //First look into the To preferences to see if any exist
            String keyStr = fromAlias.replaceAll("=", "");
            for (int i=0; i < mainpskmailui.toArray.length; i++) {
                if (mainpskmailui.toArray[i].equals(keyStr)) {
                    return mainpskmailui.toAliasArray[i];
                }
            }
            //Otherwise might have been deleted, look into message history
            if (group2.equals("")) {
                //We have an alias alone, try to find a previously sent message with "alias=destination"
                int listLength = displayList.size();
                RMsgDisplayItem mDisplayItem = null;
                if (listLength > 0) {
                    //Iterate through the sent list and search for a previous full alias=destination entry
                    for (int ii = listLength - 1; ii >= 0; ii--) {
                        mDisplayItem = displayList.get(ii);
                        if (mDisplayItem.myOwn) { //Sent message
                            msc = pscf.matcher(mDisplayItem.mMessage.to);
                            if (msc.lookingAt()) {
                                if (fromAlias.equals(msc.group(1) + "=")
                                        && !msc.group(2).equals("")
                                        && !msc.group(2).equals("**unknown**")) {
                                    //We have a match
                                    return fromAlias + msc.group(2);
                                }
                            }
                        }
                    }
                }
                //No records or No match, then return an error message for further processing
                return fromAlias + "**unknown**";
            }
        }
        //We have a strait callsign address, return as-is
        return fromAlias;
    }


    //Reverse look-up of origin to alias. Used when sending an email or SMS from this relay to others over the air.
    //Returns origin if no match (the email address or cellular number will be transmitted as-is).
    synchronized public String getAliasFromOrigin(String origin, String toStr) {

        Pattern psc = Pattern.compile("(^[\\w.-]+@\\w+\\.[\\w.-]+)|(^\\+?\\d{8,16})");
        Pattern pscf = Pattern.compile("^\\s*(.+)\\s*=(.+)\\s*$");
        Matcher msc = psc.matcher(origin);
        if (msc.lookingAt()) {
            //Try to find a previous message with "alias=origin" from this callsign
            int listLength = displayList.size();
            RMsgDisplayItem mDisplayItem = null;
            if (listLength > 0) {
                //Iterate through the list and search for a previous full alias=destination entry (for that destination callsign)
                for (int ii = listLength - 1; ii >= 0; ii--) {
                    mDisplayItem = displayList.get(ii);
                    if (!mDisplayItem.myOwn) {
                        msc = pscf.matcher(mDisplayItem.mMessage.to);
                        if (msc.lookingAt()) {
                            if (origin.equals(msc.group(2))
                                    && !msc.group(1).equals("")
                                    && mDisplayItem.mMessage.from.equals(toStr)) {
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


    //Detects duplicate messages when a message is ent via a relay, the receiving end
    // can receive the message directly and via the relay moments later.
    // Uses the message id information send with the relayed message which consist of the three
    // least significant digits of the minutes and seconds (E.g: 752 for minute 7 and 52 seconds)
    // of the end of RX time at the relay. Since both the relay and receiving station
    // would have received the original message at the same time, the allowed time difference
    // is 10 seconds to account for time drifts and different CPU speeds at both stations
    synchronized public boolean isDuplicate(RMsgObject mMessage) {
        boolean isDuplicate = false;
        int listLength = displayList.size();
        RMsgObject recMessage = null;
        RMsgDisplayItem recDisplayItem = null;
        if (listLength > 0) {
            //Iterate through the last 3 received items in the list
            int trialCount = 0;
            for (int ii = listLength - 1; ii >= 0; ii--) {
                recDisplayItem = displayList.get(ii);
                //Only received messages
                if (!recDisplayItem.myOwn) {
                    //No need to check past the three previous messages
                    if (++trialCount > 3) break;
                    recMessage = recDisplayItem.mMessage;
                    //Looks like the same message was received direct and via a relay?
                    if (recMessage.from.equals(mMessage.from)
                            && recMessage.via.equals(mMessage.relay)
                            && recMessage.sms.equals(mMessage.sms)
                            && recMessage.msgHasPosition == mMessage.msgHasPosition) {
                        if (!recMessage.msgHasPosition ||
                                (recMessage.position != null && mMessage.position != null
                                        && recMessage.position == mMessage.position)) {
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
            }
        }
        return isDuplicate;
    }


}
