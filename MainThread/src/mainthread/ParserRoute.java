/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: O parser pou ektelei route -n.
 */


package mainthread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ParserRoute {
    
    Process proc;
    BufferedReader routeInput;
    
    
    // Me ayton ton parser vriskoume default gateway mesw ths entolhs route -n.
    public void getRouteProperties(Interface myInterface) {
        String line;
        int indexGateway = 0;
        int indexIface = 0;
        int indexGenmask = 0;
        String gateway = "Not Found";
        String name = null;
        
        try { 
            proc = Runtime.getRuntime().exec("sudo route -n");
            routeInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            
            while ((line = routeInput.readLine()) != null) {
                if (line.indexOf("Gateway") != -1 && line.indexOf("Iface") != -1) {
                    indexGateway = line.indexOf("Gateway");
                    indexGenmask = line.indexOf("Genmask");
                    indexIface = line.indexOf("Iface");
                }
                
                if (line.startsWith("0.0.0.0")) {
                    gateway = line.substring(indexGateway, indexGenmask - 1);
                    gateway = gateway.trim();
                    name = line.substring(indexIface);
                    name = name.trim();
                }
                
                if (myInterface.getName().equals(name))
                    myInterface.setDefaultGateway(gateway);
            }
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try {
                routeInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
}}