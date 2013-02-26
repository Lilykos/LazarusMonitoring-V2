/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: To shutdown hook kleinei thn main kai ola ta threads kai
 *                    ektypwnei ta katallhla mynhmata.
 */


package mainthread;
import java.util.ArrayList;

public class ShutDownClass implements Runnable {
    Thread main;
    DataLists lists;
    ArrayList<Thread> threads;
    private static boolean checkHook;
    
    // Dinoume san orismata tis klaseis pou prepei na kleisoun
    public ShutDownClass (Thread main, DataLists lists, ArrayList<Thread> threads) {
        this.main = main;
        this.lists = lists;
        this.threads = threads;
        checkHook = true;
    }
    
    
    @Override
    public void run() {
        if (checkHook){
            checkHook = false;
            System.out.println("\t *** Executing ShutDown Hook. ***");

            main.interrupt();
            try { main.join(); }
            catch (InterruptedException ex) { ex.printStackTrace(); }


            // Svhnei ola ta threadas ena ena kai vgazei ta interfaces apo tis listes
            // pou einai apo8hkeymena. O termatismos ginetai mesw boolean pou stelnoume.
            int size = threads.size();
            for (int i = size - 1; i >= 0; i--) {
                String name = lists.getMyInterfaces(i).getName();
                lists.getMyInterfaces(i).setAlive(false);

                try { threads.get(i).join(); }
                catch (InterruptedException ex) { ex.printStackTrace(); }
                threads.remove(threads.get(i));


                // An einai wireless to exoume kai se allh lista, to vgazoume kai apo ekei
                // Epeidh to size einai metavlhto vgazoume apo to telos gia na mhn petaksei exception.
                if (lists.getMyInterfaces(i).isWireless()) {
                    for (int j = 0; j < lists.getMyWirelessInterfacesSize(); j++) {
                        if ((lists.getMyWirelessInterfaces(j).getName()).equals(lists.getMyInterfaces(i).getName())) {
                            lists.removeMyWirelessInterfaces(j);
                            break;
                }}} 
                lists.removeMyInterfaces(i);
                System.out.println(name + " is finished!\n");
            }


            // Epalh8eysh oti ola exoun paei kala.
            System.out.println("Current Thread Number : " + threads.size());
            System.out.println("Total Interfaces Number : " + lists.getMyInterfacesSize() +
                               " Wireless Interfaces Number : " + lists.getMyWirelessInterfacesSize()+ "\n");
        }
    }
}