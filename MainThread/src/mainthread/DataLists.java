/* Anaptyksh Logismikou: Ergasia 1h
 * Omada: Koutsakis Ilias,      A.M. 1115200800026
 *        Siamouris Anastasios, A.M. 1115200800175
 * package mainthread
 * Class Description: Mia class pou periexei tis listes twn interfaces, twn asyrmatwn
 *                    interfaces kai twn access points. thn dinoume san orisma
 *                    opou xreiazetai gia na ginontai oi aparaithtes allages.
 */


package mainthread;
import java.util.ArrayList;

public class DataLists {
    
    private ArrayList<Interface> myInterfaces;
    private ArrayList<InterfaceWireless> myWirelessInterfaces;
    private ArrayList<AccessPoint> myAccessPoints;
    
    Object lock = new Object();
    
    public DataLists() {
        myInterfaces = new ArrayList<>();
        myWirelessInterfaces = new ArrayList<>();
        myAccessPoints = new ArrayList<>();    
    }
    
    public ArrayList getAccessPoints() {
        return myAccessPoints;
    }
    
    public ArrayList getInterfaces() {
        return myInterfaces;
    }
    
    public Interface getMyInterfaces(int i) {
        return myInterfaces.get(i); }
    
    public InterfaceWireless getMyWirelessInterfaces(int i) {
        return myWirelessInterfaces.get(i); }
    
    
    public int getMyInterfacesSize() {
        synchronized (lock) {
            return myInterfaces.size();
    }}
    
    public int getMyWirelessInterfacesSize() {
        return myWirelessInterfaces.size(); }

    public int getMyAccessPointsSize() {
        return myAccessPoints.size(); }
    
    
    public void addMyInterfaces(Interface myInterface) {
        myInterfaces.add(myInterface); }
    
    public void addMyWirelessInterfaces(InterfaceWireless myInterface) {
        myWirelessInterfaces.add(myInterface); }
    
    
    public void removeMyInterfaces(int i) {
        myInterfaces.remove(i); }
    
    public void removeMyWirelessInterfaces(int i) {
        myWirelessInterfaces.remove(i); }
}