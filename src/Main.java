//package Peers

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
// this file will be run at remote machine
public class Main {

    public static void main(final String[] args) {

        final int id = Integer.parseInt(args[0]);
        final int peerPort = Integer.parseInt(args[1]);
        final int registeryPort = Integer.parseInt(args[2]);
        final int hops = Integer.parseInt(args[3]);
        final String type = args[4];
        final String configFileName = args[5];
        final String neighborsFileName = args[6];

        try {
            //System.out.println(id + "   " + peerPort + "   "  +registeryPort+" " + hops + "   " +  type + "   " + configFileName + "   " + neighborsFileName);

            /* Local Host */
            String localhost = InetAddress.getLocalHost().getHostAddress();
            //System.out.println(localhost);
            System.setProperty("java.rmi.server.hostname", localhost);
            // creare registery serive
            //final Registry registry = LocateRegistry.createRegistry();
            Registry registry = LocateRegistry.getRegistry(localhost,registeryPort);

            /* Initializing the Configurations , create and init initial state of peer on the machine */
            final Peer peer = new Peer(id, configFileName, neighborsFileName, peerPort, type, hops);
            peer.configure_IP_Port();  // IP, Port, Type

            // create stub of peer
            final Server stub = (Server) UnicastRemoteObject.exportObject(peer, peerPort);
            // register stub to runtime for it to be available for remote calls
            registry.bind(String.valueOf(id), stub);

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }
}
