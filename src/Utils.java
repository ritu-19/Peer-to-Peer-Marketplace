import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class Utils{

    /* Parsing the Configuration File:   ID - IP - Port - Type and keep the mappings */
    public static void configure_IP_Port(Peer peer) {

        System.out.println(String.format("Peer %d :Inside configure_IP_Port():initializing peer with config file details" , peer.peerID));

        try {

            File myObj = new File(peer.configFile);
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNext()) {
                String line = myReader.nextLine();
                String[] data = line.split("-");
                int id = Integer.parseInt(data[0]);
                InetAddress ip = InetAddress.getByName(data[1]);
                int port = Integer.parseInt(data[2]);
                String type = data[3];

                /* Keeping the Mappings */
                peer.config.put(id, ip);
                peer.ports.put(id, port);
                peer.typeNode.put(id, type);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("An Error Occured");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.err.println("An Error Occured");
            e.printStackTrace();
        }
    }



    public static void configure_Neighbors(Peer peer) {

        try {
            File myObj = new File(peer.neighFile);
            Scanner myReader = new Scanner(myObj);
            System.out.println(String.format("Peer %d Inside configure_Neighbors()",peer.peerID));

            while (myReader.hasNext()) {
                String line = myReader.nextLine();
//                System.out.println("Line:     " + line);
                String[] data = line.split("-");
                Integer currentPeerID = Integer.parseInt(data[0]);

                if (currentPeerID == (int)peer.peerID) {
                    String neighbors[] = data[1].split(","); // Get the Neigbors of the current PeerID

//                    System.out.println(String.format("log:configure_Neighbors::Displaying the Neighbors"));

                    for (int i = 0; i < neighbors.length; i++) {
                        // Establishing Connection to the Neighbors through RMI
                        try {

                            // Getting the id of the Neighbor
                            int id = Integer.parseInt(neighbors[i]);
                            System.out.println(String.format("Peer %d Connecting to neighbor:%s" ,peer.peerID, neighbors[i]));

                            /* Retrieving the Port and IP of the neighbor */
                            int port =peer.ports.get(id);
                            InetAddress ip =peer.config.get(id);
                            System.out.println(String.format("Peer %d Conecting to ip:%s port:%d",peer.peerID,ip.getHostAddress(),port));

                            //Establishing the connection to the Neighbour
                            Registry registry = LocateRegistry.getRegistry(ip.getHostAddress(),port); // default port 1099 DONE-> pass port
//                            System.out.println("Conecting to registry: " + registry);
                            Server neighStub = (Server) registry.lookup(neighbors[i]);

                            //System.out.println("Data:   " + registry + "   " + peer);
//                             Keeping the mapping of neighbor id <-> Remote Registry
                            peer.neighbors.put(id, neighStub);

                        } catch (RemoteException e) {
                            System.err.println("An Error Occured");
                            e.printStackTrace();
                        }
                    }
                }
            }
            myReader.close();
        } catch (Exception e) {
            System.err.println("An Error Occured");
            e.printStackTrace();
        }
    }


    /* Selecting a Random Product for Buying */
    public static String selectRandItem() {
        Random rand = new Random();
        int itemNum = rand.nextInt(3);
        String item = "";

        if (itemNum == 0) {
            item = "FISH";
        } else if (itemNum == 1) {
            item = "SALT";
        } else {
            item = "BOAR";
        }
        return item;
    }

}