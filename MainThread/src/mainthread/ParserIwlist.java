/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: O parser pou ektelei oles tis me8odous pou xreiazontai iwlist.
 */


package mainthread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParserIwlist {
    
    Process proc;
    BufferedReader iwlistInput;
    String line;
    AccessPoint accessPoint;
    
    // Me thn iwlist vriskoume tis properties twn access points.
    // Kai edw to parsing ginetai xeirokinhta me xrhsh strings.
    public void getAccessPointProperties(String name, ArrayList list) {
        String mac;
        String essid;
        String mode;
        String signal;
        String channel;
        
        try {
            proc = Runtime.getRuntime().exec("sudo iwlist " + name + " scan" );
            iwlistInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            
            while ((line = iwlistInput.readLine()) != null) {
                
                ///// ESSID /////
                int essidIndex = line.indexOf("ESSID:");
                if (essidIndex != -1) {
                    essid = line.substring(essidIndex  + "ESSID:".length());
                    essid = essid.trim();
                    accessPoint.setESSID(essid);
                }
                else {
                    
                    ///// NEW ACCESS POINT CREATION /////
                    int cellIndex = line.indexOf("Cell");
                    if (cellIndex != -1) {
                        accessPoint = new AccessPoint();
                        list.add(accessPoint);
                    }

                    
                    ///// MAC /////
                    int macIndex = line.indexOf("Address: ");
                    if (macIndex != -1) {
                        mac = line.substring(macIndex  + "Address: ".length());
                        mac = mac.trim();
                        accessPoint.setMAC(mac);
                    }


                    ///// ACCESS POINT MODE /////
                    int modeIndex = line.indexOf("Mode:");
                    if (modeIndex != -1) {
                        mode = line.substring(modeIndex  + "Mode:".length());
                        mode = mode.trim();
                        accessPoint.setMode(mode);
                    }


                    ///// SIGNAL LEVEL /////
                    int signalIndex = line.indexOf("Signal level=");
                    if (signalIndex != -1) {
                        signal = line.substring(signalIndex  + "Signal level=".length());
                        signal = signal.trim();
                        accessPoint.setSignalLevel(signal);
                    }
                    
                    
                    ///// CHANNEL /////
                    int channelIndex = line.indexOf("Channel:");
                    if (channelIndex != -1) {
                        channel = line.substring(channelIndex  + "Channel:".length());
                        channel = channel.trim();
                        accessPoint.setChannel(channel);
                    }
                }
            }
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try {
                iwlistInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
}}