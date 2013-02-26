/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: O parser pou ektelei oles tis me8odous pou xreiazontai ifconfig.
 */


package mainthread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParserIfconfig {
    
    Process proc;
    BufferedReader ifconfigInput;
    
    
    // Me xrhsh ths ifconfig pairnei ola ta aparaithta attributes gia ta interfaces
    // To parsing ginetai xeirokinhta, me diavasma grammh grammh kai xeirismo strings.
    public void getIfconfigProperties(Interface myInterface){
        String mac;
        String ip;
        String bcast;
        String mask;
        String mtu = null;
        double bcastRateDouble = 0;
        String bcastRate = null;
        
        String packetsErrorsRate;
        int packErrRate;
        String rxPackets;
        String rxErrors;
        String txPackets;
        String txErrors;
        String rxBytes;
        String txBytes;
        
        int rxPack = 0;
        int rxErr = 0;
        int txPack = 0;
        int txErr = 0;
        int rxBytesInt = 0;
        int txBytesInt = 0;
        
        
        try {
            proc = Runtime.getRuntime().exec("sudo ifconfig " + myInterface.getName());
            ifconfigInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            while ((line = ifconfigInput.readLine()) != null) {
                
                ///// MAC ADDRESS /////
                int macIndex = line.indexOf("HWaddr ");
                if (macIndex != -1) {
                    mac = line.substring(macIndex  + "HWaddr ".length());
                    mac = mac.trim();
                    myInterface.setMAC(mac);
                }


                ///// IP ADDRESS /////
                int bcastIndex = line.indexOf("Bcast:");
                int maskIndex = line.indexOf("Mask:");
                int ipIndex = line.indexOf("inet addr:");
                if (ipIndex != -1) {
                    if (bcastIndex != -1) {
                        ip = line.substring(ipIndex  + "inet addr:".length(), bcastIndex);
                        ip = ip.trim();
                        myInterface.setIP(ip);
                    }
                    else {
                        ip = line.substring(ipIndex  + "inet addr:".length(), maskIndex);
                        ip = ip.trim();
                        myInterface.setIP(ip);
                }}


                ///// BCAST ADDRESS /////
                if (bcastIndex != -1) {
                    bcast = line.substring(bcastIndex  + "Bcast:".length(), maskIndex);
                    bcast = bcast.trim();
                    myInterface.setBcastAddr(bcast);
                }


                ///// MASK /////
                if (maskIndex != -1) {
                    mask = line.substring(maskIndex  + "Mask:".length());
                    mask = mask.trim();
                    myInterface.setMask(mask);
                }
                
                
                ///// MTU /////
                int mtuIndex = line.indexOf("MTU:");
                int metricIndex = line.indexOf("Metric:");
                if (mtuIndex != -1) {
                    mtu = line.substring(mtuIndex + "MTU:".length(), metricIndex - 1);
                    mtu = mtu.trim();
                    myInterface.setMTU(mtu);
                }
                
                
                ///// PACKET ERROR RATE (Rx - Tx Packets) /////
                int rxPacketsIndex = line.indexOf("RX packets:");
                int rxErrorsIndex = line.indexOf("errors:");
                int rxDroppedIndex = line.indexOf("dropped:");
                if (rxPacketsIndex != -1) {
                    if (rxErrorsIndex != -1) { 
                        rxPackets = line.substring(rxPacketsIndex  + "RX packets:".length(), rxErrorsIndex - 1);
                        rxPackets = rxPackets.trim();
                        rxPack = Integer.parseInt(rxPackets);
                        if (rxDroppedIndex != -1) {
                            rxErrors = line.substring(rxErrorsIndex  + "errors:".length(), rxDroppedIndex - 1);
                            rxErrors = rxErrors.trim();
                            rxErr = Integer.parseInt(rxErrors);
                }}}
                
                int txPacketsIndex = line.indexOf("TX packets:");
                int txErrorsIndex = line.indexOf("errors:");
                int txDroppedIndex = line.indexOf("dropped:");
                if (txPacketsIndex != -1) {
                    if (txErrorsIndex != -1) { 
                        txPackets = line.substring(txPacketsIndex  + "RX packets:".length(), txErrorsIndex - 1);
                        txPackets = txPackets.trim();
                        txPack = Integer.parseInt(txPackets);
                        if (txDroppedIndex != -1) {
                            txErrors = line.substring(txErrorsIndex  + "errors:".length(), txDroppedIndex - 1);
                            txErrors = txErrors.trim();
                            txErr = Integer.parseInt(txErrors);
                            if (txPack != 0 && rxPack != 0) {
                                packErrRate = (txErr + rxErr) / (txPack + rxPack);
                                packetsErrorsRate = Integer.toString(packErrRate);
                                myInterface.setPacketErrorRate(packetsErrorsRate);
                            }
                            else myInterface.setPacketErrorRate("Impossible to calculate.");
                }}}
                
                
                ///// BROADCAST RATE (Rx - Tx Bytes) /////
                int rxBytesIndex = line.indexOf("RX bytes:");
                int txBytesIndex = line.indexOf("TX bytes:");
                if (rxBytesIndex != -1) {
                    rxBytes = line.substring(rxBytesIndex + "RX bytes:".length(), line.indexOf("("));
                    rxBytes = rxBytes.trim();
                    rxBytesInt = Integer.parseInt(rxBytes);
                }
                
                if (txBytesIndex != -1) {
                    line = line.substring(txBytesIndex);
                    txBytesIndex = line.indexOf("TX bytes:");
                    txBytes = line.substring(txBytesIndex + "TX bytes:".length(), line.indexOf("("));
                    txBytes = txBytes.trim();
                    txBytesInt = Integer.parseInt(txBytes);
                    int curTime = (int) System.currentTimeMillis();
                    bcastRateDouble = findBroadcastRate(rxBytesInt, txBytesInt, curTime, myInterface.getName());
                    
                    bcastRate = Double.toString(bcastRateDouble);
                    myInterface.setBroadcastRate(bcastRate);
                }
                
                
                ///// CONSUMED GAUGE  /////
                if (mtu != null && bcastRate != null) {
                    double mtuDouble = Double.parseDouble(mtu);
                    double gauge = (Double.parseDouble(myInterface.getBroadcastRate()) / mtuDouble) * 100;
                }
            }
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try { 
                ifconfigInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }
    
    
    // Eidikos xeirismos twn plhroforiwn ths ifconfig gia eyresh Network Address
    public void setNetAddr(Interface myInterface) {
        String ip = myInterface.getIP();
        String mask = myInterface.getMask();
        int[] ipBinary = new int[32];
        int[] maskBinary = new int[32];
        int[] networkAddr = new int[32];
        int[] tempgNetworkAddr = new int[4];
        String[] networkAddrFinal = new String[4];
        String ipFull = "";
        String maskFull = "";
        String ch;
        int length;
        
        if (ip.equals("Not Found") || mask.equals("Not Found"))
            return;
        
        String[] tempIPString = ip.split("\\.");
        String[] tempMaskString = mask.split("\\.");
        
        int[] tempIPInt = new int[4];
        int[] tempMaskInt = new int[4];
        
        
        // Algorithos gia parsing twn 2 strings kai meta apo polles allages kai
        // kopsimo - rapsimo exoume thn network address.
        for (int i = 0; i < tempIPString.length; i++) {
            tempIPInt[i] = Integer.parseInt(tempIPString[i]);
            tempMaskInt[i] = Integer.parseInt(tempMaskString[i]);
            
            tempIPString[i] = Integer.toBinaryString(tempIPInt[i]);
            tempMaskString[i] = Integer.toBinaryString(tempMaskInt[i]);
            
            length = tempIPString[i].length();
            for(int j = 0; j < 8 - length; j++){
                tempIPString[i] = "0" + tempIPString[i];
            }
            length = tempMaskString[i].length();
            for(int j = 0; j < 8 - length; j++){
                tempMaskString[i] = "0" + tempMaskString[i];
            }
            ipFull += tempIPString[i];
            maskFull += tempMaskString[i];
        }
        
        for (int i = 0; i < 32; i++) { 
           ch = ipFull.substring(i, i+1);
           ipBinary[i] = Integer.parseInt(ch);
           
           ch = maskFull.substring(i, i+1);
           maskBinary[i] = Integer.parseInt(ch);
           
           networkAddr[i] = ipBinary[i] & maskBinary[i];
           tempgNetworkAddr[i/8] += networkAddr[i] * Math.pow(2,(31-i)%8);
        }
        
        for (int i = 0; i < 4; i++)
            networkAddrFinal[i] = Integer.toString(tempgNetworkAddr[i]);
        
        String finalnetAddr = networkAddrFinal[0]+"."+networkAddrFinal[1]+"."
                              +networkAddrFinal[2]+"."+networkAddrFinal[3];
        myInterface.setNetworkAddress(finalnetAddr);
    }
    
    
    // Ayth h synarthsh kaleitai mono apo th main gia na diavasei ta onomata kai
    // kat epektash na dei an yparxoun kainouria interfaces h an exoun fygei palia
    public ArrayList<String> findNames() {
        ArrayList<String> names = new ArrayList();
        boolean nameflag = true;
        String ifconfigLine;
        String name;
        
        try {
            proc = Runtime.getRuntime().exec("sudo ifconfig");
            ifconfigInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            
            while ((ifconfigLine = ifconfigInput.readLine()) != null) {
                if (nameflag) {
                    name = ifconfigLine.substring(0, ifconfigLine.indexOf(" "));
                    names.add(name);
                    nameflag = false;
                }
                if (ifconfigLine.equals(""))
                    nameflag = true;
            }
        } catch (IOException ex) { ex.printStackTrace();
        } finally {
            try {
                ifconfigInput.close();
                proc.destroy();
            } catch (IOException ex) { ex.printStackTrace(); }
        }
        return names;
    }

    
    // Me8odos gia eyresh Broadcast Rate - kaleitai apo thn getIfconfigProperties(Interface myInterface)
    private Double findBroadcastRate(int rxBytesInt, int txBytesInt, int curTime, String name) {
        double rate = 0;
        String line;
        Process p;
         
        try {
            
            // Epeidh o elegxos ginetai grhgora, to afhnoume na koimh8ei ligo gia
            // na aykshsoume thn pi8anothta na vrei allages (synh8ws den vriskei pantws).
            try { Thread.sleep(1500); }
            catch (InterruptedException ex) {}
            
            int rxBytesIntNew = 0;
            int txBytesIntNew = 0; 
            p = Runtime.getRuntime().exec("sudo ifconfig " + name);
            ifconfigInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            while ((line = ifconfigInput.readLine()) != null) {
                int rxBytesIndex = line.indexOf("RX bytes:");
                int txBytesIndex = line.indexOf("TX bytes:");
                if (rxBytesIndex != -1) {
                    String rxBytesNew = line.substring(rxBytesIndex + "RX bytes:".length(), line.indexOf("("));
                    rxBytesNew = rxBytesNew.trim();
                    rxBytesIntNew = Integer.parseInt(rxBytesNew);
                }
                
                if (txBytesIndex != -1) {
                    line = line.substring(txBytesIndex);
                    txBytesIndex = line.indexOf("TX bytes:");
                    String txBytesNew = line.substring(txBytesIndex + "TX bytes:".length(), line.indexOf("("));
                    txBytesNew = txBytesNew.trim();
                    txBytesIntNew = Integer.parseInt(txBytesNew);
                    int curTimeNew = (int) System.currentTimeMillis();
                    
                    int time = curTimeNew - curTime;
                    rate = (((rxBytesIntNew + txBytesIntNew) - (rxBytesInt + txBytesInt)) * (8000)) / time;
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
        // den mporoume na kleisoume ta processes giati ta exoume dhlwsei ws class
        // members kai kleinoun sth synarthsh pou kalei thn parousa.
        return rate; 
    }
}