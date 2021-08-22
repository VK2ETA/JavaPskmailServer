/*
 * contact.java
 *
 * Copyright (C) 2011 Pär Crusefalk (SM0RWO)
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

/**
 * This is the class that holds a contact and its used by the addressbook.
 * These contacts are stored as one line each in a csv file.. Perhaps also as xml?
 * Feed it a line of csv data and it will populate its fields.
 * @author Pär Crusefalk <per at crusefalk.se>
 *
 */
public class contact {
    private String FirstName;
    private String LastName;
    private String Nickname;
    private String HamCallsign;
    private String OtherCallsign;
    private String Phone;
    private String MobilePhone;
    private String Email;
    private String MMSI;
    private String Notes;
    //VK2ETA: add Radio Msg fields
    private Boolean showInVIA;
    private Boolean showInTO;
    private Boolean showMobileInTO;
    private Boolean showEmailInTO;
    private String MobilePhoneAlias;
    private String EmailAlias;    

    public contact(){
        super();
        InitVars();
    }

    private void InitVars(){
        FirstName="";
        LastName="";
        Nickname="";
        HamCallsign="";
        OtherCallsign="";
        Phone="";
        MobilePhone="";
        Email="";
        MMSI="";
        Notes="";
        showInVIA = false;
        showInTO = false;
        showMobileInTO = false;
        showEmailInTO = false;
        MobilePhoneAlias = "";
        EmailAlias = "";
    }

    /**
     * Feed a whole csv line to the class and have it populate itself
     * @param line
     * @return
     */
    public Boolean LoadCSV(String line){
        Boolean myreturn=true;
        // Holds the array
        String[] temp;
        String delimiter = ",";

        temp = line.split(delimiter);
        
        // Trim extra "" from the csv file
        for (int i = 0; i < temp.length; i++){
            temp[i]= temp[i].replace("\"", ""); 
        }

        // Save all the fields, more fields could follow later
        if (temp.length > 15){
            setFirstName(temp[0]);
            setLastName(temp[1]);
            this.setPhone(temp[2]);
            this.setMobilePhone(temp[3]);
            this.setHamCallsign(temp[4]);
            this.setOtherCallsign(temp[5]);
            this.setMMSI(temp[6]);
            this.setEmail(temp[7]);
            this.setNotes(temp[8]);
            this.setNickname(temp[9]);
            this.setShowInVIA(temp[11].equals("Y"));
            this.setShowInTO(temp[10].equals("Y"));
            this.setMobilePhoneAlias(temp[12]);
            this.setEmailAlias(temp[13]);
            this.setShowMobileInTO(temp[14].equals("Y"));
            this.setShowEmailInTO(temp[15].equals("Y"));
        } else if (temp.length > 8){ //Make it compatible with old contact files
            setFirstName(temp[0]);
            setLastName(temp[1]);
            this.setPhone(temp[2]);
            this.setMobilePhone(temp[3]);
            this.setHamCallsign(temp[4]);
            this.setOtherCallsign(temp[5]);
            this.setMMSI(temp[6]);
            this.setEmail(temp[7]);
            this.setNotes(temp[8]);
            this.setNickname(temp[9]);
        } else
            myreturn = false;

        return myreturn;
    }

    /**
     * Get the object data as one row csv, just push this to a file to save
     * @return
     */
    public String GetDataAsCSV(){
        String output="";
        output =  "\""+this.getFirstName()+"\",";
        output += "\"" + this.getLastName() + "\",";
        output += "\"" + this.getPhone()+ "\",";
        output += "\"" + this.getMobilePhone()+ "\",";
        output += "\"" + this.getHamCallsign()+ "\",";
        output += "\"" + this.getOtherCallsign()+ "\",";
        output += "\"" + this.getMMSI()+ "\",";
        output += "\"" + this.getEmail()+ "\",";
        output += "\"" + this.getNotes()+ "\",";
        output += "\"" + this.getNickname()+ "\",";
        output += "\"" + this.getShowInTO()+ "\",";
        output += "\"" + this.getShowInVIA()+ "\",";
        output += "\"" + this.getEmailAlias()+ "\",";
        output += "\"" + this.getMobilePhoneAlias()+ "\"";
        return output;
    }

    /**
     * This is used to enable this object in lists
     * @return
     */
    @Override
    public String toString(){
        return FirstName+" "+LastName+"  <"+Email+">";
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getHamCallsign() {
        return HamCallsign;
    }

    public void setHamCallsign(String HamCallsign) {
        this.HamCallsign = HamCallsign;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getMMSI() {
        return MMSI;
    }

    public void setMMSI(String MMSI) {
        this.MMSI = MMSI;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }
    
    public String getShowInVIA() {
        return (showInVIA ? "Y": "N");
    }

    public String getShowInTO() {
        return (showInTO ? "Y": "N");
    }
   
    public String getShowMobileInTO() {
        return (showMobileInTO ? "Y": "N");
    }
   
    public String getShowEmailInTO() {
        return (showEmailInTO ? "Y": "N");
    }
   
    public String getMobilePhoneAlias() {
        return MobilePhoneAlias;
    }

    public String getEmailAlias() {
        return EmailAlias;
    }

    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String Notes) {
        this.Notes = Notes;
    }

    public String getOtherCallsign() {
        return OtherCallsign;
    }

    public void setOtherCallsign(String OtherCallsign) {
        this.OtherCallsign = OtherCallsign;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getNickname(){
        return Nickname;
    }
    
    public void setNickname(String myNickname){
        this.Nickname = myNickname;
    }
    
    public void setShowInVIA(Boolean showInVIA) {
        this.showInVIA = showInVIA;
    }

    public void setShowInTO(Boolean showInTO) {
        this.showInTO = showInTO;
    }
   
    public void setShowMobileInTO(Boolean showMobileInTO) {
        this.showMobileInTO = showMobileInTO;
    }
   
    public void setShowEmailInTO(Boolean showEmailInTO) {
        this.showEmailInTO = showEmailInTO;
    }
   
    public void setMobilePhoneAlias(String MobilePhoneAlias) {
        this.MobilePhoneAlias = MobilePhoneAlias;
    }

    public void setEmailAlias(String EmailAlias) {
        this.EmailAlias = EmailAlias;
    }     
}
