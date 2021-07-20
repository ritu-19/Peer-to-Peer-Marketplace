import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.io.IOException;

//use concurrent map/synchronization for requestID map as multiple threads can try to remove the request

class ReplyTask implements  Runnable{

    ReplyParams replyParams;
    Peer peer;


    int sellerPeerID;
    List<Integer> connectedPath;
//    int maxHops;
//    int port;
    String requestID;


    public ReplyTask(ReplyParams replyParams, Peer peer) {
        this.replyParams = replyParams;
        this.peer = peer;
        sellerPeerID= replyParams.getPeerID();
        connectedPath = replyParams.getConnectedPath();
//      maxHops=replyParams.getMaxHops();
//      port=replyParams.getPort();
        requestID = replyParams.getRequestID();
    }

    public void run(){

        System.out.println(String.format("Peer %d log:make_announcements() for request %s ",peer.peerID,requestID));

        if (connectedPath.size() == 0)
        {
            System.out.println(String.format("Peer %d log:make_announcements()::message reached buyer for request %s from seller %d",peer.peerID,requestID,sellerPeerID));

            //check if the request was already satisfied (handling scenario when reply from multiple matched sellers come
            // need sychronization for handling multiple matches
            synchronized(peer) {
                if (!peer.buyRequestMap.containsKey(requestID))
                    return; // do nothing as request was satisfied earlier
                else
                {
                    // try to buy from the matched seller
                    try {
                        //int id = sellerPeerID;
                        //  get seller remote IP
                        InetAddress sellerIP = peer.config.get(sellerPeerID);
                        int sellerPort = peer.ports.get(sellerPeerID);
                        //Get registry service of seller
                        Registry sellerRegistry = LocateRegistry.getRegistry(sellerIP.getHostAddress(),sellerPort); //Done-> get ip of host mahcine of the peer , check implementation for remote case

                        Server sellerServer = (Server) sellerRegistry.lookup(Integer.toString(sellerPeerID));

                        //get item from request map
                        String requestedItem = peer.buyRequestMap.get(requestID);

                        long startTime = System.nanoTime();
                        boolean success = sellerServer.buy(requestedItem); // Done check if buy gets successful as mutiple peers can call buy at same time to the  same seller , how to handle failure scenario?
                        long estimatedTime = System.nanoTime() - startTime;

                        try {
                            peer.buyProd.write(String.valueOf(estimatedTime) + "\n");
                            peer.buyProd.flush();
                        }catch(IOException io)
                        {
                            io.printStackTrace();
                        }

                        System.out.println(String.format("Peer %d log:make_announcements()::buy complete for request %s and item %s ",peer.peerID,requestID,requestedItem));
                        if(success)
                        {
                            peer.buyRequestMap.remove(requestID);
                        }

                    } catch (RemoteException e) {
                        System.err.println("An Error Occured");
                        e.printStackTrace();
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                    return;

                }
            }
        }
        else {

            /* BackTrack Until Path Becomes 0 (that is reach to the origin Buyer) */
            int neighPeerID = 0;
            Server neighServer = null;
            if(!connectedPath.isEmpty()) {

                int indexOfLastElement = connectedPath.size() - 1;
                neighPeerID = connectedPath.get(indexOfLastElement);
                neighServer = peer.neighbors.get(neighPeerID);
                if(neighServer==null)
                    return;

                //Remove Id from the current Connected Path from the Origin ID
                connectedPath.remove(indexOfLastElement);

                try {

                    long startTime = System.nanoTime();
                    neighServer.make_announcements(requestID,sellerPeerID, connectedPath);
                    long estimatedTime = System.nanoTime() - startTime;

                    //peer.announce.write(Long.toString(endTime - startTime));
                    try {
                        peer.announce.write(String.valueOf(estimatedTime) + "\n");
                        peer.announce.flush();
                    }catch(IOException io)
                    {
                        io.printStackTrace();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}