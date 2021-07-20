import java.util.List;

class RequestParams{
    // requested Item from buyer
    String item;
    List<Integer> connectedPath;
    int maxHops;
    //int startID;
    String requestID;

    public RequestParams(String requestID, String item, List<Integer> connectedPath, int maxHops){
        this.item=item;
        this.connectedPath=connectedPath;
        this.maxHops=maxHops;
        //this.startID=startID;
        this.requestID = requestID;
    }

    public String getItem() {
        return item;
    }

    public List<Integer> getConnectedPath() {
        return connectedPath;
    }

    public int getMaxHops() {
        return maxHops;
    }

    /*public int getStartID() {
       return startID;*/

    public String getRequestID() {
        return requestID;
    }
}