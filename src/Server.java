import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface Server extends Remote {

    /* Remote RMI Calls */
    // Buyer buys the product 
    boolean buy(String requestedItem) throws RemoteException;
    // Hop on the Neighbors and Check
    void getProductfromNeigh(String requestID,String item, List<Integer> connectedPath, int hops) throws RemoteException, NotBoundException;
    // Seller Announcements
    void make_announcements(String requestID,int peerID, List<Integer> connectedPath) throws RemoteException, NotBoundException;

    void connectToNeighbors() throws RemoteException; // command issued by controller to connect after all peers are set up on machine

}
