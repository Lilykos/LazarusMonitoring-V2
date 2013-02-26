/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: To systhma tou "athroisth", gia thn wra apla tou dinoume
 *                    antikeimena gia na kanei tis analoges ektypwseis.
 */


package mainthread;
import com.webservice.main.MonitorData;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.ws.WebServiceException;

public class WebServiceConnector implements Runnable {
    
    ShutDownClass sdHook;
    private volatile boolean checkAlive = true;
    private com.webservice.main.WebServiceHandler port = null;
    private long currentTime;
    private String device;
    private static boolean checkPort;
    
    
    public void setAlive(boolean checkAlive) {
        this.checkAlive = checkAlive; }
    
    public WebServiceConnector(String wsdlURL, String device, ShutDownClass sdHook) {
        this.device = device;
        this.sdHook = sdHook;
        URL url;
        checkPort = true;
        try {
            url = new URL(wsdlURL + "?wsdl");
            com.webservice.main.WebServiceHandler_Service service = new com.webservice.main.WebServiceHandler_Service(url);
            port = service.getWebServiceHandlerPort();
        } catch (MalformedURLException ex) {
            System.out.println("Wrong URL!");
            checkPort = false;
            Thread t = new Thread(sdHook);
            t.start();
            checkAlive = false;
        } catch (WebServiceException ex) {
            System.out.println("Port Error!");
            checkPort = false;
            Thread t = new Thread(sdHook);
            t.start();
            checkAlive = false;
        }
    }
    
    public synchronized void connectToWS(InterfaceWireless iface, ArrayList<AccessPoint> apList, boolean interfaceChanged, boolean apChanged) {
        if (checkPort) {
            MonitorData md = new MonitorData();
            currentTime = System.currentTimeMillis();
            double curTime = (double) (currentTime/1000);
            System.out.println("Wireless Interface sent data!");
            md.setApChanged(apChanged);
            md.setInterfaceChanged(interfaceChanged);
            md.setTimeChanged(curTime);
            md.setIsDeleted(false);

            if (interfaceChanged) {
                md.setIsWireless(true);
                md.setName(iface.getName());
                md.setMac(iface.getMAC());
                md.setIp(iface.getIP());
                md.setMask(iface.getMask());
                md.setNetworkAddress(iface.getNetworkAddress());
                md.setBcastAddr(iface.getBcastAddr());
                md.setDefaultGateway(iface.getDefaultGateway());
                md.setMtu(iface.getMTU());
                md.setPacketErrorRate(iface.getPacketErrorRate());
                md.setBroadcastRate(iface.getBroadcastRate());
                md.setConsumedGauge(iface.getConsumedGauge());

                md.setBaseStationMAC(iface.getBaseStationMAC());
                md.setBaseStationESSID(iface.getBaseStationESSID());
                md.setChannel(iface.getChannel());
                md.setAccessPointSituation(iface.getAccessPointMode());
                md.setSignalLevel(iface.getSignalLevel());
                md.setLinkQuality(iface.getLinkQuality());
                md.setTxPower(iface.getTxPower());
                md.setNoiseLevel(iface.getNoiseLevel());
                md.setMissedBeacon(iface.getMissedBeacon());
            }

            if (apChanged) {
                for (AccessPoint ap: apList) {
                    md.getMyAP().add(ap.toString());
                }
            }
            try{ port.setMonitorData(device, md);
            } catch(WebServiceException ex) {
                System.out.println("Port Error!");
                checkPort = false;
                Thread t = new Thread(sdHook);
                t.start();
                checkAlive = false;
            }
        }
    }
    
    public synchronized void connectToWS(Interface iface, ArrayList<AccessPoint> apList, boolean interfaceChanged, boolean apChanged) {
        if (checkPort) {
            MonitorData md = new MonitorData();
            currentTime = System.currentTimeMillis();
            double curTime = (double) (currentTime/1000);
            System.out.println("Interface sent data!");
            md.setApChanged(apChanged);
            md.setInterfaceChanged(interfaceChanged);
            md.setTimeChanged(curTime);
            md.setIsDeleted(false);

            md.setIsWireless(false);
            md.setName(iface.getName());
            md.setMac(iface.getMAC());
            md.setIp(iface.getIP());
            md.setMask(iface.getMask());
            md.setNetworkAddress(iface.getNetworkAddress());
            md.setBcastAddr(iface.getBcastAddr());
            md.setDefaultGateway(iface.getDefaultGateway());
            md.setMtu(iface.getMTU());
            md.setPacketErrorRate(iface.getPacketErrorRate());
            md.setBroadcastRate(iface.getBroadcastRate());
            md.setConsumedGauge(iface.getConsumedGauge());

            try{ port.setMonitorData(device, md);
            } catch(WebServiceException ex) {
                System.out.println("Port Error!");
                checkPort = false;
                Thread t = new Thread(sdHook);
                t.start();
                checkAlive = false;
            }
        }
    }    
    
    public synchronized void deleteFromDatabase(String mac, boolean isWireless) {
        if (checkPort) {
            MonitorData md = new MonitorData();
            currentTime = System.currentTimeMillis();
            double curTime = (double) (currentTime/1000);

            md.setIsWireless(isWireless);
            md.setTimeChanged(curTime);
            md.setMac(mac);
            md.setIsDeleted(true);

            System.out.println("\tInterface deleted!!!");
            try{ port.setMonitorData(device, md);
            } catch(WebServiceException ex) {
                System.out.println("Port Error!");
                checkPort = false;
                Thread t = new Thread(sdHook);
                t.start();
                checkAlive = false;
            }
        }
    }

    @Override
    public void run() {
        while(this.checkAlive) {
            // Apla menei zwntano to thread.
        }
        System.out.println("Web Connection closed!");
    }
}