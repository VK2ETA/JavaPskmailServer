/*
 * socketmanager.java
 *
 * Copyright (C) 2008 Pär Crusefalk (SM0RWO)
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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Observable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author per
 */
public class socketmanager extends Observable{
    /* Socket handling to modem */
    int port = 3122;
    InetAddress address;
    Socket socket;
    OutputStream os;
    OutputStreamWriter osw;
    PrintWriter pw;
    // fldigi command related
    char SOC = (char) 26; // Start of command
    char EOC = (char) 27; // End of command
    char NUL = (char) 0; // Null character

    /** /
     * Constructor
     */
    public socketmanager(){

    }

    
    /** /
     * Open the socket and connect to fldigi
     */
    public void connectmodem(){
        String pskmailon = SOC+"MULTIPSK-OFF"+EOC;
        // Set localhost as destination for socket
        try {
            address = InetAddress.getByName("127.0.0.1");
            socket = new Socket(address, port);
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            pw = new PrintWriter(osw);
            // Time to send stuff to fldigi
            pw.println(pskmailon);
            pw.flush();
            Thread.sleep(1000);
            pw.println("<cmd>server</cmd>");
            pw.flush();
            Thread.sleep(1000);
        } catch (Exception ex) {
            Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     /** /
     * Crap, remove later.
     * @param testtext
     */
    public void sendframe(String testtext){
        String sendtext;
        try{
        sendtext = ""+(char) 0;
        sendtext += SOC;
        sendtext += "TX";
        sendtext += EOC;
        sendtext += testtext;
        sendtext += SOC;
        sendtext += "RX_AFTER_TX";
        sendtext += EOC;
        pw.println(sendtext);
        pw.flush();
       }
       catch (Exception ex){
        Logger.getLogger(arq.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    /**
     * 
     * @param cmd
     */
    public void sendcommand(String cmd){
        
    }

}
