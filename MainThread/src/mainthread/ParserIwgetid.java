/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: O parser pou ektelei oles tis me8odous pou xreiazontai iwgetid.
 */


package mainthread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParserIwgetid {
    
    Process proc;
    BufferedReader iwgetidInput;
    
    
    // Me thn iwgwtid vriskoume amesa to channel tou interface 
    public void setChannelProperties(InterfaceWireless myInterface) {
        String channel;
        
        try {
            proc = Runtime.getRuntime().exec("sudo iwgetid " + myInterface.getName() + " -c");
            iwgetidInput =  new BufferedReader(new InputStreamReader(proc.getInputStream()));;
            
            String line = iwgetidInput.readLine();
            int index = line.indexOf("Channel:");
            if (index != -1) {
                channel = line.substring(index  + "Channel:".length());
                channel = channel.trim();
                myInterface.setChannel(channel);
            }
        
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try {
                iwgetidInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
}}