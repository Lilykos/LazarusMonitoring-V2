/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: H main class pou dhmiourgei ta nhmata, kanei elegxo gia allages
 *                    kai energopoiei to shutdown hook me ctrl-c.
 */


package mainthread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class MainThread implements Runnable {
    
    private ArrayList<Thread> myInterfaceThreads;
    private ArrayList<String> interfaceNewNames;
    private ArrayList<String> interfaceNames;
    private ParserIfconfig parser;
    private ParserIwconfig iwconfigParser;
    private DataLists lists;
    private Random generator;
    private WebServiceConnector printerRunnable;
    private Properties prop;
    
    int T;
    int k;
    int X;
    int c;
    String wsdlURL;
    String deviceName;
    
    
    @Override
    public void run() {
        prop = new Properties();
        
        lists = new DataLists();
        myInterfaceThreads = new ArrayList<>();
        interfaceNewNames = new ArrayList<>();
        interfaceNames = new ArrayList<>();
        parser = new ParserIfconfig();
        iwconfigParser = new ParserIwconfig();
        generator = new Random();
        
        int curtime1 = 0;
        int curtime2 = 0;;
        
        boolean changesFound = false;
        boolean firstCycle = true;
        
        int sleepTime = T;
        int localc = 1;
        
        System.out.println("\\****************************************\\\n");
        System.out.println("\\******* LAZARUS: ***********************\\\n");
        System.out.println("\\******* The WiFi Monitoring Program ****\\\n");
        System.out.println("\\****************************************\\\n\n");
        
        // Apla koimatai ligo gia na fanoun oi arxikes ektypwseis.
        try { Thread.sleep(3000); }
        catch (InterruptedException ex) { ex.printStackTrace(); }
        
        // Diavasma property file kai arxikopoihsh timwn mesw synarthshs
        readPropertyFile(T, k, X, c, wsdlURL, deviceName);
        
        // Dhmiourgia shutdown hook gia to kleisimo
        ShutDownClass sdHook = new ShutDownClass(Thread.currentThread(), lists, myInterfaceThreads);
        Runtime.getRuntime().addShutdownHook(new Thread(sdHook));
        
        printerRunnable = new WebServiceConnector(wsdlURL, deviceName, sdHook);
        Thread printer = new Thread(printerRunnable);
        printer.start();
        
        try {
            while (!Thread.currentThread().isInterrupted()) {
                
                // metavlhth kai to sleeptime, an vrhke opoiadhpote allagh
                changesFound = false;
                
                
            //Parsing twn onomatwn mono kai eisodos tous se lista
            //Xrhsh algorithmou gia elegxo kai eyresh diaforetikwn onomatwn
            //Dhmiourgia kai diagrafh interface -> thread
                  
                interfaceNewNames = parser.findNames();
                
                // Dhmiourgia newn interfaces/threads
                for (String name: interfaceNewNames) {
                    if (interfaceNames.contains(name))
                        interfaceNames.remove(name);
                    else {
                        if(iwconfigParser.iwConfigCheck(name)) {
                            InterfaceWireless iface = new InterfaceWireless(name, lists, printerRunnable, T, k, X, c);
                            lists.addMyInterfaces(iface);
                            lists.addMyWirelessInterfaces(iface);
                            myInterfaceThreads.add(new Thread(iface));
                            myInterfaceThreads.get(myInterfaceThreads.size() - 1).start();
                            changesFound = true;
                        }
                        else {
                            Interface iface = new Interface(name, lists, printerRunnable, T, k, X, c);
                            lists.addMyInterfaces(iface);
                            myInterfaceThreads.add(new Thread(iface));
                            myInterfaceThreads.get(myInterfaceThreads.size() - 1).start();
                            changesFound = true;
                        }
                    }
                }

                // Diagrafh interfaces/threads me flag kai join()
                String mac;
                boolean isWireless;
                for (String name: interfaceNames) {
                    for (int i = 0; i < lists.getMyInterfacesSize(); i++) {
                        if ((lists.getMyInterfaces(i).getName()).equals(name)) {
                            
                            mac = lists.getMyInterfaces(i).getMAC();
                            isWireless = lists.getMyInterfaces(i).isWireless();
                            printerRunnable.deleteFromDatabase(mac, isWireless);
                            
                            lists.getMyInterfaces(i).setAlive(false);
                            myInterfaceThreads.get(i).join(); 
                            myInterfaceThreads.remove(myInterfaceThreads.get(i));
                            lists.removeMyInterfaces(i);
                            changesFound = true;
                            break;
                        } 
                    }
                    for (int i = 0; i < lists.getMyWirelessInterfacesSize(); i++) {
                        if ((lists.getMyWirelessInterfaces(i).getName()).equals(name)) {
                            lists.removeMyWirelessInterfaces(i);
                            break;
                        }
                    }
                }

                // Dinoume thn kainouria lista sthn metavlhth ths palias
                //gia sygrish sthn epomenh run pou 8a kanei h main
                interfaceNames = interfaceNewNames;
                
                
                // Epilogh tyxaioy wireless interface gia elegxo twn Access Points
                // An exoume mono ena interface typwnoume mynhma
                // kai to exoume na kanei olous tous elegxous
                if (lists.getMyWirelessInterfacesSize() > 1) {
                    int index = generator.nextInt(lists.getMyWirelessInterfacesSize());
                    for (int i = 0; i < lists.getMyWirelessInterfacesSize(); i++) {
                        if (lists.getMyWirelessInterfacesSize() == 1) {
                            lists.getMyWirelessInterfaces(i).setFlags(true, true);
                            break;
                        }
                        if (i == index)
                            lists.getMyWirelessInterfaces(i).setFlags(true, false);
                        else 
                            lists.getMyWirelessInterfaces(i).setFlags(false, true);
                    }
                }
                if (lists.getMyWirelessInterfacesSize() == 1) {
                    System.out.println("Only 1 Wireless Interface found. Resuming checks with this interface only.");
                    lists.getMyWirelessInterfaces(0).setFlags(true, true);
                }
                
                System.out.println("Current Thread Number : " + myInterfaceThreads.size());
                System.out.println("Total Interfaces Number : " + lists.getMyInterfacesSize() +
                                   " Wireless Interfaces Number : " + lists.getMyWirelessInterfacesSize()+ "\n");
                
                // Xrhsh Markov gia tis epanalhpseis
                if (changesFound) {
                    localc = 1;
                    sleepTime = T;
                }
                else {  
                    if (localc % c != 0)
                        localc ++;
                    else {
                        if (sleepTime != k*T) {
                            sleepTime = sleepTime + T;
                            localc = 1;
                }}}
                
                
                // Eyresh tou Dt xronou pou exei perasei gia xrhsh sto sleepTime
                // H flag xrhsimeyei gia na mhn ypologizei ton xrono MONO sthn 1h epanalhpsh
                // Gia oles tis epomenes ypologizetai kanonika
                // OMOIA GIA: Interface, InterfaceWireless, MainThread
                curtime2 = (int) System.currentTimeMillis();
                if (!firstCycle)
                    sleepTime = Math.abs(sleepTime - (curtime2 - curtime1));
                firstCycle = false;
                
                System.out.println("\\*** Main thread " + " will sleep for " + sleepTime);
                Thread.sleep(sleepTime);
                curtime1 = (int) System.currentTimeMillis();
            }
        } catch (InterruptedException ex) { System.out.println("\\*** Main Thread Interrupted!\n"); }        
    }
    
    
    // To property file yparxei sto package ths askhshsh
    public void readPropertyFile(int T, int k, int X, int c, String wsdlURL, String deviceName) {
        
        // Se periptwsh pou paei kati strava apo to property file
        // dinoume kapoies default times gia na leitourghsei omala to programma.
        String propT = "3000";
        String propk = "5";
        String propX = "10";
        String propC = "1";
        String propURL = "Not Found!";
        String propDevice = "Not Found!";
        try {
            
            // Xrhsimopoioume aythn edw thn entolh giati se default path
            // den leitourgei apo terminal.
            prop.load(getClass().getResourceAsStream("PropertiesFile.properties"));
            propT = prop.getProperty("T");
            propk = prop.getProperty("k");
            propX = prop.getProperty("X");
            propC = prop.getProperty("c");
            propURL = prop.getProperty("url");
            propDevice = prop.getProperty("device");
            
            System.out.println("\t *** Properties File Found! ***");
        } catch (IOException ex) { System.out.println("Property File NOT FOUND: Using default properties."); }
        this.T = Integer.parseInt(propT);
        this.k = Integer.parseInt(propk);
        this.X = Integer.parseInt(propX);
        this.c = Integer.parseInt(propC);
        this.wsdlURL = propURL;
        this.deviceName = propDevice;
    }

    
    public static void main(String[] args) {
        Thread mainThread = new Thread(new MainThread());
        mainThread.start();
    } 
}
