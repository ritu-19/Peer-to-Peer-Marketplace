import java.util.concurrent.Callable;

class BuyTask implements Callable<Boolean>{

    Peer peer;
    String requestedItem;

    public BuyTask(Peer peer,String requestedItem) {
        this.peer = peer;
        this.requestedItem = requestedItem;

    }

    public Boolean call() throws Exception {

        System.out.println(String.format("Peer %d log:buy() for item %s ",peer.peerID,requestedItem));

        // Adding Syncronization
        synchronized(peer) {

            // to handle scenario when inventory changes but buyer is still requesting for old item
            if(!peer.product.equals(requestedItem))
                return false;

            peer.products.put(peer.product, (int) peer.products.get(peer.product) - 1);
            System.out.println(String.format("Peer %d log:buy():: inventory updated for %s with count %d", peer.peerID, peer.product, peer.products.get(peer.product)));

            if ((int) peer.products.get(peer.product) == 0) {
                peer.initialize(); // reset inventory again as sold all items
            }
            return true;
        }
    }
}

