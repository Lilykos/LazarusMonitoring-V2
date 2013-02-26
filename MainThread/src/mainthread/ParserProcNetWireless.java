/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: O parser pou ektelei /proc/net/wireless.
 */


package mainthread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ParserProcNetWireless {
    
    Process proc;
    BufferedReader pnwInput;
    
    
    // Me thn /proc/net/wireless vriskoume ton 8oryvo kai ta missed beacons
    public void setNoiseAndMissedBeacon(InterfaceWireless myInterface) {
        String line;
        int noiseIndex = 0;
        int beaconIndex = 0;
        String noiselevel = "Not Found";
        String missedbeacon = "Not Found";
        
        try { 
            proc = Runtime.getRuntime().exec("sudo cat /proc/net/wireless");
            pnwInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            
            while ((line = pnwInput.readLine()) != null) {
                
                if (line.indexOf("noise") != -1 && line.indexOf("beacon") != -1) {
                    noiseIndex = line.indexOf("noise");
                    beaconIndex = line.indexOf("beacon");
                }
                if (line.indexOf(myInterface.getName()) != -1) {
                    noiselevel = line.substring(noiseIndex);
                    noiselevel = noiselevel.trim();
                    noiselevel = noiselevel.substring(0, noiselevel.indexOf(" "));
                    
                    missedbeacon = line.substring(beaconIndex);
                    missedbeacon = missedbeacon.trim();
                }
            }
            
            myInterface.setMissedBeacon(missedbeacon);
            myInterface.setNoiseLevel(noiselevel);
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try {
                pnwInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
}}