

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import java.util.HashMap;
import java.util.Map;

public class Controller {

    private static int N = 2; // number of peers

    //private static String hosts[]={"127.0.0.1","127.0.0.1"};
    //private static int ports[]={1099,1099};

    //registery service ips and ports of each client
    static Map<Integer,String> idIpMap = new HashMap<>();
    static Map<Integer,Integer> idPortMap = new HashMap<>();

    public static void main(final String[] args) {

        PrintStream fileStream = null;
        try {
            fileStream = new PrintStream("Controller.log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(fileStream);


        final String configFileName = args[0];

        // parse config file for N and ips and ports of registery service
        parseConfig(configFileName);

        N = idIpMap.size();

        System.out.println("N:     " + N);
        for (int i = 1; i <= N; i++)
        {   int peerID = i;

            try {

                // Establishing Connection to the Neighbors through RMI
                Registry registry = LocateRegistry.getRegistry(idIpMap.get(peerID),idPortMap.get(peerID));
                System.out.println("Controller::Conecting to registry:     " + registry);

                //get peer server stub from remote registery
                Server peer = (Server)registry.lookup(Integer.toString(peerID));
                peer.connectToNeighbors();

            } catch (RemoteException e) {
                System.err.println("An Error Occured");
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }

    }

    private static void parseConfig(String filename) {

        System.out.println(String.format("Controller:Parsing config file for peer details"));

        try {

            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNext()) {
                String line = myReader.nextLine();
                String[] data = line.split("-");
                int id = Integer.parseInt(data[0]);
                InetAddress ip = InetAddress.getByName(data[1]);
                int port = Integer.parseInt(data[2]);
                String type = data[3];

                /* Keeping the Mappings */
                idIpMap.put(id, ip.getHostAddress());
                idPortMap.put(id, port);

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


}
