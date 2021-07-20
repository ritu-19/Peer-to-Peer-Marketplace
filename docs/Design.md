**1. Design Considerations –** <br /><br />
    _a. Peer Structure:_ <br /><br />
       Each peer will have a peer-id, host address and role which states whether
       it is buyer or seller. The details about each peer are stored in the config
       file (FileName: config). We use a fully connected/ring/mesh topology for
       our peers. The number of peers is equal to the size of the config file. <br /><br />
    _b. Choice of Communication:_ <br /><br />
       We are using RMI for communications in Java. RMI is Remote Method
       Invocation. It allows an object residing in one JVM to access/invoke an
       object in another JVM. We used RMIs registry interface to map names to
       remote objects. In this we use a server that registers its remote objects
       that can be looked up by others using its name. <br />
       If the default port for RMI(1099) is busy then we provide alternate ports for
       the registry creation. <br /><br />
    _c. Neighbors File:_<br /><br />
       The details of peers being neighbors of each other is stored in this file.<br />
       This file is parsed each time we try to configure neighboring peers and
       establish a connection amongst each other.<br /><br />
**2. Implementation Details –**<br /><br />
    The broad idea of the implementation revolves around using data
    structures to store buyer and seller configurations like products they are
    selling/buying with their quantities.<br />
    For each seller, the product he is selling and its quantity is decided
    randomly. Also for each buyer the product he is looking up for is done
    using random assignment.<br />
    We start with randomly initialising peers as buyers and sellers and starting
    lookup using query flooding. In this setting peers are connected over an
    overlay network. ​It means if a connection/path exists from one peer to
    another, it is a part of this overlay network. In this network the peers act as
    nodes and the connection between these nodes are the edges, thus
    resulting in a graph-like structure as shown below.<br /><br />


a. _Lookup_product ​ -<br /><br />
We implement our lookup functionality in Peer class. In this we start by
creating an iterator over the neighbors of a currentPeer. Then we create a
LinkedList to maintain a path from buyer ID to the seller ID. This list helps
in tracing back to the identify which buyer initiated the lookup. If we don’t
find the seller in the immediate neighbourhood, then this list helps us in
locating the seller in the neighbours of neighbours. The current hop count
is the size of this LinkedList.<br /><br />
_b. Reply -_ <br /><br />
The reply from the seller is represented in the ReplyTask class in our
implementation. In this we first collect the request from the requestItem
map and make sure buy is called. Also we have a boolean success that
makes sure that buy is successful because there are multiple buyers
buying at the same time. This entire block is synchronized using thread
pool so that if there are multiple buyer threads they have to wait until the
critical section is free. This will make sure that response time is improved.<br /><br />
_c. Buy -_<br /><br />
The buy function is implemented in the BuyTask class in our
implementation. In this synchronized block we first check if the inventory is
same as the item that buyer is requesting, if this is not the case it returns
false. If the inventory is available then its value is reduced to issue the
selling. If the product is over then we initialise the inventory again. If the
seller is found, we backtrack over the LinkedList by popping out the ID’s
pushed so that we get the ID of the buyer. This case can also happen if
the size of the linkedList is equal to the maxHopCount. <br /><br />
_d. Thread Pool for multiple buyers and sellers:_<br /><br />
We use Thread Pool in order to obtain multiple concurrent buying and
selling. A thread pool reuses previously created threads to execute current
tasks and offers a solution to the problem of thread cycle overhead and
resource thrashing. In our implementation we initialise the thread pool to
5, but it can be changed to any number in order to maintain multiple
buyers and sellers.<br /><br />

**3. Design Advantages -**<br /><br />
The code supports a wide variety of concurrency -<br /><br />
    1. The design provides synchronization, that is, multiple buyers can be served parallely by a server. Also, the
        design supports multiple-server, multiple-buyer parallely. So, we there are multiple threads available, then if 
        the response time of the buyers can be reduced. For instance,  if multiple buyers are requesting from a single server, then
        if only one thread is present, then the response time will be high. So, we have taken care of this, using ThreadPools (in which 
        number of threads are configurable).<br />
    2. The design is developed in such a way that it frees the developer from the thread management stuff. It is handled automatically.Threadpool size can be configured to scale system for requests and replies depending on the available rersources on the system.<br />
    3. Any peer in the design can be made a buyer or seller. Each of them
       support both the functionalities.<br />
    4. The design works for any kind of combination of buyers and sellers
       and is fully tested for all patterns (upto 8 peers, in total) like: 1 seller 1 buyer, 1 seller 2 buyer, 2 buyer 1 seller, all buyers and all sellers, only buyer and only seller.<br />
    5. The design developed works perfectly in both centralized and distributed environment. <br />
    6. The design supports fully connected, ring and mesh etc. topologies.<br />
    7. The design is very easy to operate because most of the work is
       automated and can be configured very easily.<br />
    8. Since service is an interface , the design supports different implementations of remote calls can be provided meaning different logic for sending request /reply / buy transaction. <br />
    9. Since service is an interface , the design provides flexibility to have  different implementations of remote calls  meaning different logic for sending request /reply / buy transaction . <br />
    10. Code is easily maintainable as separate class for request , reply and buy exists . Change specific to them will not affect other classes and need to be done at one place only. </br>
    11. A cleanup hook is added which shuts down all threads in thread pool when jvm is interrupted externally(eg sending kill command). <br />
    


