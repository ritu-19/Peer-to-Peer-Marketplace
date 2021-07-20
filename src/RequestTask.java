import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

class RequestTask implements  Runnable{

    RequestParams requestParams;
    Peer peer;
    String item; List<Integer> connectedPath; int maxHops;
    String requestID;

    public RequestTask(RequestParams requestParams, Peer peer) {
        this.requestParams = requestParams;
        this.peer = peer;

        item=requestParams.getItem();
        connectedPath=requestParams.getConnectedPath();
        maxHops=requestParams.getMaxHops();
        this.requestID = requestParams.getRequestID();

    }


    public void run(){

        System.out.println(String.format("Peer %d log:getProductfromNeigh() for item %s and request %s",peer.peerID,item,requestID));

        int hops = connectedPath.size()-1;

        if(hops == maxHops)
        {
            System.out.println(String.format("Peer %d log:getProductfromNeigh()::Max Hop Count Reached %d for request %s and item %s",peer.peerID,hops,requestID,item));
            return;
        }

        // if peer is a seller and has requested item and product count >0
        if ((peer.type.equals("seller") || peer.type.equals("Seller")) && peer.product.equals(item) && (int) peer.products.get(item) > 0) {

            System.out.println(String.format("Peer %d log:getProductfromNeigh()::matched product %s for request %s",peer.peerID,item,requestID));


            int neighPeerID = 0;
            Server neighServer = null;

        /* If the Seller id Found and is selling the same product which the buyer wants to buy then
        the seller makes the announcement and we have to backtrack (Remove from the connected Path) */
            int indexOfLastElement = connectedPath.size() - 1;
            neighPeerID = connectedPath.get(indexOfLastElement);
            neighServer = peer.neighbors.get(neighPeerID);

            //Remove Id from the current Connected Path from the Origin ID
            connectedPath.remove(indexOfLastElement);

            if (neighServer != null) {
                // Seller will make the announcement
                try {

                    long startTime = System.nanoTime();
                    neighServer.make_announcements(requestID,peer.peerID, connectedPath);
                    long estimatedTime = System.nanoTime() - startTime;

                    try {
                        //peer.announce.write(Long.toString(endTime - startTime));
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
        else {


            // Else if above three conditions donot match
            /* Keep Hoping (Looking for the peers)  if the seller is not available */

            // Adding the PeerId to the Path
            connectedPath.add(peer.peerID);

            // Again Iterator on the corresponding Neighbors
            Set entrySet = peer.neighbors.entrySet();
            Iterator hmIterator = peer.neighbors.entrySet().iterator();

            boolean propagated = false;  // boolean to track if we are no longer forwarding buyer request

            while (hmIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) hmIterator.next();

                int neighPeerID = (int) mapElement.getKey();
                Server neighServer = (Server) mapElement.getValue();

                if (connectedPath.contains(neighPeerID) == false) {

                    propagated = true;

                    try {
                        long startTime = System.nanoTime();
                        neighServer.getProductfromNeigh(requestID,item, connectedPath, maxHops);
                        long estimatedTime = System.nanoTime() - startTime;

                        try {
                            //requestTimeLog.write(Long.toString(estimatedTime));
                            //requestTimeLog.println(Long.toString(estimatedTime));
                            peer.getProduct.write(String.valueOf(estimatedTime) + "\n");
                            peer.getProduct.flush();
                        }catch(IOException io)
                        {
                            io.printStackTrace();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (NotBoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }

            if (!propagated) {
                System.out.println(String.format("Peer %d log:getProductfromNeigh():No neighbor to propagate for request %s and item %s", peer.peerID,requestID,item));

            }
        }
    }
}