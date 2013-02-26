/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: To interface (asyrmato, me oles tis me8odous tou asyrmatou
 *                    apo klhronomikothta) pou kanei parsing kai sygrish.
 */


package mainthread;
import java.util.ArrayList;

public class InterfaceWireless extends Interface {
    
    private String baseStationMAC = "Not Found";
    private String baseStationESSID = "Not Found";
    private String channel = "Not Found";
    private String accessPointSituation = "Not Found";
    private String signalLevel = "Not Found";
    private String linkQuality = "Not Found";
    private String txPower = "Not Found";
    private String noiseLevel = "Not Found";
    private String missedBeacon = "Not Found";
    
    private ParserIwconfig iwconfigParser;
    private ParserIwgetid iwgetidParser;
    private ParserProcNetWireless pnwParser;
    private ParserIwlist iwlistParser;
    private InterfaceWireless interfaceClone;
    
    private volatile boolean checkAP = true;
    private volatile boolean checkSelf = true;
    private ArrayList<AccessPoint> apList;
    
     
    public InterfaceWireless(String name, DataLists lists, WebServiceConnector printer, int T, int k, int X, int c) {
        super(name, lists, printer, T, k, X, c);
        iwconfigParser = new ParserIwconfig();
        iwgetidParser = new ParserIwgetid();
        pnwParser = new ParserProcNetWireless();
        iwlistParser = new ParserIwlist();
        apList = new ArrayList<>();
    }
    
    
    @Override
    public boolean isWireless() {
        return true;
    }
    
    // Setters - Getters
    public String getBaseStationMAC() {
        return baseStationMAC; }
    
    public String getBaseStationESSID() {
        return baseStationESSID; }
    
    public String getChannel() {
        return channel; }
    
    public String getAccessPointMode() {
        return accessPointSituation; }
    
    public String getSignalLevel() {
        return signalLevel; }
    
    public String getLinkQuality() {
        return linkQuality; }
    
    public String getTxPower() {
        return txPower; }
    
    public String getNoiseLevel() {
        return noiseLevel; }
    
    public String getMissedBeacon() {
        return missedBeacon; }
    
    
    public void setBaseStationMAC(String baseStationMAC) {
        this.baseStationMAC = baseStationMAC; }
    
    public void setBaseStationESSID(String baseStationESSID) {
        this.baseStationESSID = baseStationESSID; }
    
    public void setChannel(String channel) {
        this.channel = channel; }
    
    public void setAccessPointMode(String accessPointSituation) {
        this.accessPointSituation = accessPointSituation; }
    
    public void setSignalLevel(String signalLevel) {
        this.signalLevel = signalLevel; }
    
    public void setLinkQuality(String linkQuality) {
        this.linkQuality = linkQuality; }
    
    public void setTxPower(String txPower) {
        this.txPower = txPower; }
    
    public void setNoiseLevel(String noiseLevel) {
        this.noiseLevel = noiseLevel; }
    
    public void setMissedBeacon(String missedBeacon) {
        this.missedBeacon = missedBeacon; }

    
    // Oi shmaies gia to an to wireless 8a kanei elegxo
    // 8a kanei gia Access Points, gia ton eayto tou h kai gia ta 2 (an den exoume allo) 
    public void setFlags(boolean checkAp, boolean checkSelf) {
        this.checkSelf = checkSelf;
        this.checkAP = checkAP;
    }
    
    @Override
    public void run() {
        int sleepTime = this.T;
        int localc = 1;
        
        int curtime2 = 0;
        int curtime1 = 0;
        boolean firstCycle = true;
        
        // Exoume tis arxikes boolean gia to interface
        // kai 2 epipleon gia na ektypw8oun ta swsta mynhmata
        // akoma kai an h main prolavei na allaksei tis metavlhtes true - false
        boolean changesFoundAP;
        boolean changesFoundSelf;
        boolean checkSelfPrint = false;
        boolean checkAPPrint = false;
        
        while (this.checkAlive) {
            changesFoundAP = false;
            changesFoundSelf = false;
             
            // Tou dinoume epilogh gia na kanei elegxo analoga me ta orismata
            // pou stelnei h main
            if (this.checkAP) {
                checkAPPrint = true;
                apList.clear();
                int accessPointSize = lists.getMyAccessPointsSize();
                iwlistParser.getAccessPointProperties(this.name, apList);
                if (apList.size() != accessPointSize)
                    changesFoundAP = true;
                else
                    changesFoundAP = false;
            }
            
            if (this.checkSelf) {
                checkSelfPrint = true;
                interfaceClone = new InterfaceWireless(this.name, null, this.printer, 0, 0, 0, 0);
                interfaceClone.copyConstructor(this);
                
                // Ka8e interface dhmiourgei kai diaxeirizetai tous dikous tou parsers
                // gia parsing twn stoixeiwn ( OMOIA kai apo panw gia Access Points)
                parser.getIfconfigProperties(this);
                parser.setNetAddr(this);
                pnwParser.setNoiseAndMissedBeacon(this);
                
                //wireless - specific parsers
                routeParser.getRouteProperties(this);
                iwconfigParser.setIwconfigProperties(this);
                iwgetidParser.setChannelProperties(this);
                if (compareTo(this, interfaceClone))
                    changesFoundSelf = true;
                else
                    changesFoundSelf = false;
            }
            
            if (changesFoundAP || changesFoundSelf) {
                localc = 1;
                sleepTime = T;
                if (changesFoundAP)
                    printer.connectToWS(this, apList, false, true);
                if (changesFoundSelf)
                    printer.connectToWS(this, apList, true, false);
            }
            else {  
                if (localc % c != 0)
                    localc ++;
                else {
                    if (sleepTime != k*T) {
                        sleepTime = sleepTime + T;
                        localc = 1;
                    }}
            }
            
            
            // Eyresh tou Dt xronou pou exei perasei gia xrhsh sto sleepTime
            // H flag xrhsimeyei gia na mhn ypologizei ton xrono MONO sthn 1h epanalhpsh
            // Gia oles tis epomenes ypologizetai kanonika
            // OMOIA GIA: Interface, InterfaceWireless, MainThread
            curtime2 = (int) System.currentTimeMillis();
            if (!firstCycle)
                sleepTime = Math.abs(sleepTime - (curtime2 - curtime1));
            firstCycle = false;
            
            try {
                System.out.println("\\*** " + this.getName() + " will sleep for " + sleepTime);
                Thread.sleep(sleepTime); }
            catch (InterruptedException ex) { System.out.println("\\*** " + this.getName() + " is interrupted!\n"); } 
            curtime1 = (int) System.currentTimeMillis();
        }
    }
    
    
    // O copy constructor gia thn dhmiourgia tou klwnou sygrishs
    public void copyConstructor(InterfaceWireless oldInterface) {
        this.setDefaultGateway(oldInterface.getDefaultGateway());
        this.setIP(oldInterface.getIP());
        this.setMAC(oldInterface.getMAC());
        this.setMTU(oldInterface.getMTU());
        this.setMask(oldInterface.getMask());
        this.setNetworkAddress(oldInterface.getNetworkAddress());
        
        this.setBaseStationMAC(oldInterface.getBaseStationMAC());
        this.setBaseStationESSID(oldInterface.getBaseStationESSID());
        this.setChannel(oldInterface.getChannel());
        this.setAccessPointMode(oldInterface.getAccessPointMode());
        this.setLinkQuality(oldInterface.getLinkQuality());
        this.setSignalLevel(oldInterface.getSignalLevel());
        this.setTxPower(oldInterface.getTxPower());
        this.setNoiseLevel(oldInterface.getNoiseLevel());
        this.setMissedBeacon(oldInterface.getMissedBeacon());
    }
    
    
    // Ta attributes pou 8a elegxoume symfwna me thn ekfwnhsh
    // An vrei OPOIADHPOTE allagh stamatame thn me8odo kai epistrefoume true
    private boolean compareTo(InterfaceWireless newInterface, InterfaceWireless oldInterface) {
        if (!oldInterface.getIP().equals(newInterface.getIP()) ||
            !oldInterface.getMAC().equals(newInterface.getMAC()) ||
            !oldInterface.getMask().equals(newInterface.getMask()) ||
            !oldInterface.getDefaultGateway().equals(newInterface.getDefaultGateway()) ||
            !oldInterface.getLinkQuality().equals(newInterface.getLinkQuality()) ||
            !oldInterface.getSignalLevel().equals(newInterface.getSignalLevel()) ||
            !oldInterface.getNoiseLevel().equals(newInterface.getNoiseLevel())) {
            return true;
        }
        
        if (!oldInterface.getConsumedGauge().equals("Not Found")) {
            double gaugeOld = Double.parseDouble(oldInterface.getConsumedGauge());
            double gaugeNew = Double.parseDouble(newInterface.getConsumedGauge());
            if (((gaugeNew / gaugeOld) * 100) > X)
                return true;
        }
        
        if (!oldInterface.getPacketErrorRate().equals("Not Found") &&
            !oldInterface.getPacketErrorRate().equals("Impossible to calculate.") &&
            !newInterface.getPacketErrorRate().equals("Not Found") &&
            !newInterface.getPacketErrorRate().equals("Impossible to calculate.")) {
            
            double errorsOld = Double.parseDouble(oldInterface.getPacketErrorRate());
            double errorsNew = Double.parseDouble(newInterface.getPacketErrorRate());
            if (errorsOld == 0 && errorsNew == 0)
                return false;
            else if (errorsOld == 0 && errorsNew != 0)
                return true;
            else if (((errorsNew / errorsOld) * 100) > X)
                return true;
        }
        return false;
    }
}