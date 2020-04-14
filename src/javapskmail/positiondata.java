/*
 *  *
 *  * Copyright (C) 2008 Pär Crusefalk (SM0RWO)
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package javapskmail;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * This class will take an aprs position message and parse the data, hold it.
 * @author per
 */
public class positiondata {
    private float latitude=0;
    private float longitude=0;
    private float altitude=0;
    private Integer course=0;
    private float speed=0;
    private String icon="";
    private Date updated;
    private String status="";
    private String rawmessage="";
    private String compressedmessage="";

    /**
     * Constructor for position class
     * @param posmessage raw aprs position message
     */
    public void positiondata(String posmessage, Boolean compressed){
        if (compressed)
            compressedmessage = posmessage;
        else
            rawmessage = posmessage;
        // Now parse that
        parsemessage();
    }

    /**
     * Call the parsing methods for raw or compressed
     */
    private void parsemessage(){
            if (rawmessage.length()>1) parserawmessage();
            if (compressedmessage.length()>1) parsecompressed();
    }

    /**
     * Handle a plain text aprs message, updated internal fields.
     * Format should be: number, callsign, lat, long, icon, date time, status msg
     */
    private void parserawmessage(){

    }

    /**
     * Handle a compressed aprs pos message, update internal fields
     */
    private void parsecompressed(){
    
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }


}
