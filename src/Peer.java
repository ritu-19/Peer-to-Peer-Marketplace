



import java.io.*;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Peer implements Server {

    String IP = InetAddress.getLocalHost().getHostAddress(); // IP Address
    int port = 0; // Port
    int peerID = 0; // Id of the Peer
    String type = ""; // Type of Peer
    int maxHops = 0; // Maximum number of Hops Allowed
    String configFile = ""; // Configuration file for Peer ID and IP address-Port-Type
    String neighFile = ""; // Configuration file for Peer ID ans neighbors
    String product = "";

    HashMap<String, Integer> products; /* Product - Count */
    HashMap<Integer, InetAddress> config; /* PeerID - IP Address */
    HashMap<Integer, Integer> ports; /* PeerID - Port */
    HashMap<Integer, String> typeNode; /* PeerID - Type of Peer */
    HashMap<Integer, Server> neighbors; /* Peer ID - Neigbor Node Registry */
    //private Object lock;
    Lock lock;
    ExecutorService service;

    // map of request id (peerid_buyRequestId) to requested item
    Map <String,String>buyRequestMap;
    // counter of last buy requested
    int buyRequestCounter;
    Writer requestTimeLog;

    String logsFileName = "";
    //PrintWriter getProduct, announce;
    //PrintWriter requestTimeLog;
    String dirPathLogs = "PerfMetricsLogs/";
    String dirPeerLogs = "PeerLogs/";

    BufferedWriter getProduct = null, announce = null, buyProd = null;
    File newDirectoryPerfLogs = new File(dirPathLogs);
    //Create directory for non existed path.
    boolean isCreated = newDirectoryPerfLogs.mkdirs();

    File newDirectoryPeerLogs = new File(dirPeerLogs);
    //Create directory for non existed path.
    boolean isCreated1 = newDirectoryPeerLogs.mkdirs();

    /*if (isCreated) {
        //System.out.printf("1. Successfully created directories, path:%s",
               // newDirectory.getCanonicalPath());
    } else if (newDirectory.exists()) {
        //System.out.printf("1. Directory path already exist, path:%s",
               // newDirectory.getCanonicalPath());
    }*/

    /* Calling the Construtor */
    public Peer(int id, String configFileName, String neighborsFileName, int port, String type, int hops) throws IOException {
        this.peerID = id;
        this.type = type;
        this.port = port;
        this.configFile = configFileName;
        this.neighFile = neighborsFileName;
        this.maxHops = hops;

        this.config = new HashMap<Integer, InetAddress>();
        this.neighbors = new HashMap<Integer, Server>();
        this.ports = new HashMap<Integer, Integer>();
        this.typeNode = new HashMap<Integer, String>();
        this.products = new HashMap<String, Integer>();
        service= Executors.newFixedThreadPool(2);

        buyRequestCounter=0;
        buyRequestMap= new HashMap<>();

        /* Adding Performance Metrics Log Files */

        String fileLogs1 = "getProduct-" + Integer.toString(this.peerID);
        String fileLogs2 = "announce-" + Integer.toString(this.peerID);
        String fileLogs3 = "buyProd-" + Integer.toString(this.peerID);
        /*this.getProduct = new PrintWriter(fileLogs1);
        this.announce = new PrintWriter(fileLogs2);*/

        FileWriter fw1 = new FileWriter(dirPathLogs + fileLogs1);
        this.getProduct = new BufferedWriter(fw1);

        FileWriter fw2 = new FileWriter(dirPathLogs + fileLogs2);
        this.announce = new BufferedWriter(fw2);

        FileWriter fw3 = new FileWriter(dirPathLogs + fileLogs3);
        this.buyProd = new BufferedWriter(fw3);

        PrintStream fileStream = null;
        try {
            fileStream = new PrintStream(dirPeerLogs + "Peer"+Integer.toString(peerID)+".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(fileStream);

        //requestTimeLog = new PrintWriter("Peer"+Integer.toString(peerID)+"RequestTimeLog.log","UTF-8");
        requestTimeLog = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("Peer"+Integer.toString(peerID)+"RequestTimeLog.log"), "utf-8"));

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Performing some shutdown cleanup...");
                service.shutdown();
                while (true) {
                    try {
                        System.out.println("Waiting for the service to terminate...");
                        if (service.awaitTermination(5, TimeUnit.SECONDS)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                    }
                }
                System.out.println("Done cleaning");
            }
        }));
    }

    /* Parsing the Configuration File:   ID - IP - Port - Type and keep the mappings */
    public void configure_IP_Port() {
        Utils.configure_IP_Port(this);
    }

    /* Parsing the Current Peer ID and get the corresponding Neighbors */
    public void configure_Neighbors() {
        Utils.configure_Neighbors(this);
    }

    // Remote api
    public void getProductfromNeigh(String requestID,String item, List<Integer> connectedPath,int maxHop) throws RemoteException,
            NotBoundException {
        RequestParams reqParams = new RequestParams(requestID,item,connectedPath,maxHops);
        service.execute(new RequestTask(reqParams,this));
    }

    // Remote api
    public void make_announcements(String requestID,int peerID, List<Integer> connectedPath) throws RemoteException,
            NotBoundException {

        ReplyParams replyParams = new ReplyParams(requestID,peerID,connectedPath);
        service.execute(new ReplyTask(replyParams,this));
    }

    // Remote api /* Buy Functionality- Updating the Product Counts */
    public boolean buy(String requestedItem) {

        Future<Boolean> success = service.submit(new BuyTask(this,requestedItem));
        try {
            return success.get(); // blocking call to get result of transaction
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }


    // remote api for initiating connection to peers
    public void connectToNeighbors() throws RemoteException {
        // connect to Peers
        configure_Neighbors();

        // start seller or buyer in a separate thread(non-terminating threads) as call need to return to controller
        Thread t = new Thread(new StartPeer());
        t.start();

    }

    public class StartPeer implements Runnable {
        public void run() {
            // call internal run1 method for starting buyer or seller
            startPeer();
        }
    }

    /* Run Method */
    private void startPeer() {

        if (this.type.equals("seller")) {
            this.startSeller();  // If Peer Type is Seller, call Seller Method
        } else {
            try {
                this.startBuyer();   // If Peer Type is Buyer, call Buyer Method
            } catch (Exception e) {
                System.err.println("An Error Occured");
                e.printStackTrace();
            }

        }
    }

    /* Setting a Peer as a Buyer */
    private void startBuyer() {

        while (true) {
            //update buy request ID
            buyRequestCounter++;
            String item = Utils.selectRandItem(); // Selecting a Random Product for Buying

            // save the request details in map for later
            String buyRequestID = Integer.toString(peerID)+"_"+Integer.toString(buyRequestCounter);
            buyRequestMap.put(buyRequestID,item);

            System.out.println(String.format("Peer %d as a Buyer looking for product:%s",this.peerID,item));

            try {
                this.lookupProduct(buyRequestID,item, this.maxHops); // Calling LookUp For Product Functionality
                Thread.sleep(10000);
            } catch (Exception e) {
                System.err.println("An Error Occured");
                e.printStackTrace();
            }
        }
    }


    /* Buyer API to initiate Look up on the Immediate Neighbors for item */
    private void lookupProduct(String requestID, String item, int maxHop)  {
        // iterate over neighbors

        Iterator hmIterator = this.neighbors.entrySet().iterator();
        int startID = this.peerID;
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            Server neighServer = (Server) mapElement.getValue();

            /* Mainting a path for all the ids starting from the current Id */
            List<Integer> connectedPath = new LinkedList<>();
            connectedPath.add(startID);
            try {
                long startTime = System.nanoTime();
                neighServer.getProductfromNeigh(requestID,item, connectedPath, maxHop);
                long estimatedTime = System.nanoTime() - startTime;

                try {
                    //requestTimeLog.write(Long.toString(estimatedTime));
                    //requestTimeLog.println(Long.toString(estimatedTime));
                    this.getProduct.write(String.valueOf(estimatedTime) + "\n");
                    this.getProduct.flush();
                }catch(IOException io)
                {
                    io.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Setting a Peer as a Seller */
    private void startSeller() {
        // Initializing the products - Product-Count
        initialize();
    }


    /* Initializing the Product and Count for Product for Seller */
    /* called when seller role assigned and when inventory becomes 0 */
    public void initialize() {
        int count;
        Random rand = new Random();
        count = rand.nextInt(100) + 1;

        String item = Utils.selectRandItem(); // Selecting a Random Product for Selling

        this.product=item;
        // Keep the Mapping in the HashMap
        this.products.put(this.product, count);

        System.out.println(String.format("Peer %d as a Seller selling for product:%s with count:%d",this.peerID,this.product,count));
    }
}