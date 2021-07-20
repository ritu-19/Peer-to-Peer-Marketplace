import java.util.List;

class ReplyParams{

    //matched seller peerID
    int peerID;
    List<Integer> connectedPath;
    String requestID;

    public ReplyParams(String requestID,int peerID, List<Integer> connectedPath) {
        this.peerID = peerID;
        this.connectedPath = connectedPath;

        this.requestID =  requestID;
    }

    public int getPeerID() {
        return peerID;
    }

    public List<Integer> getConnectedPath() {
        return connectedPath;
    }

    public String getRequestID() {
        return requestID;
    }

}