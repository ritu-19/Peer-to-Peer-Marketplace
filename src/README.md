# 677 Lab 1

# Follow the following instructions to run the code:

# Configuration Files:

We are maintaining 2 configuration files:
1.	Config.txt: In this, I have put all peers id, ip address, port. Registry port and the type of the peer (each entity separated by a ‘-‘) in the following format: 
    a.	PeerID-IP Address-Registry Port-Type
        i.	Example: 1-127.0.01-1099-Seller

Registry by default runs on 1099 port, but sometimes, if incase this port is not free, then we can change it to another port like 2001 etc.

2.	Neighbors.txt: In this, I have defined the topology (network) in the following format:
    a.	PeerID-Comma Separated Immediate Neighbors
        i.	Example: For 3 peers:
            1.	1-2,3
            2.	2-1,3
            3.	3-1,2
            
# Output Files: 

The program will generate:
1.	log files of all the peers (For instance Peer1.log, Peer2.log , where 1,2 are the peer ids)
2.	Response Time of the remote calls (announce, buy, look_up) for all the Peers.
3.	All Peer log Files will be saved in PeerLogs/ Folder

# Performance Metrics:

For Performance Metrics, average response time of all the remote calls for all the peers have been calculated and then the corresponding graphs have been plotted. All perfLogs will be saved in PerfMetricLogs/ folder.
Run plotGraphs.py python script for plotting the graphs.

Steps to plot the Perf Metrics graphs:
  1.	Navigate to the src folder in the lab-1-yadav folder
  2.	Run, python plotScripts.py 
  
# Test Results: 

# TestCase 1: 2 Peers (Both Running on Local Machine):

PeerID 1 – Acting as Seller
PeerID 2 – Acting as Buyer
Registry running on 1099(default) Port Number
Thread Pool Size: 2
To Run this testCase will be generated: 
1.	Go to test/TestCase-1 directory
2.	Run ./script.sh
3.	Output files: 
a.	Peer Logs -> /src/PeerLogs
b.	PerfMetric Logs -> /src/PerfMetricsLogs
c.	Graphs -> Perf-Metrics

# TestCase 2: 6 Peers (All Running on Local Machine)

PeerID 1 – Acting as Seller
PeerID 2 – Acting as Seller
PeerID 3 – Acting as Seller
PeerID 4 – Acting as Buyer
PeerID 5 – Acting as Buyer
PeerID 6 – Acting as Buyer
Thread Pool Size: 2
Registry running on 1099(default) Port Number
To Run this testCase: 
1.	Go to test/TestCase-2 directory
2.	Run ./script.sh
3.	Output files will be generated: 
a.	Peer Logs -> /src/PeerLogs
b.	PerfMetric Logs -> /src/PerfMetricsLogs
c.	Graphs -> Perf-Metrics

# TestCase 3: 2 Peers (One on Edlab 7 and the other on Edlab 1)

Edlab 7: IP Address: 128.119.243.175
	PeerID 1: Seller
Edlab 1: IP Address: 28.119.243.147
	PeerID 2: Buyer
Thread Pool Size: 2
Registry running on 2001 Port Number
To Run this testCase:
1.	Go to test/TestCase-3 directory
2.	Run ./script_1.sh on Edlab-7
3.	Once script_1 is fully run, navigate to the Edlab-1 and run
a.	./script_2.sh

# TestCase 4: 3 Peers ( Two on Edlab 7 and the other one on Edlab 1)

Edlab 7: IP Address: 128.119.243.175
	PeerID 1: Seller
	PeerID 2: Buyer
Edlab 1: IP Address: 28.119.243.147
	PeerID 3: Buyer
Thread Pool Size: 2
Registry running on 2001 Port Number
To Run this testCase:
1.	Go to test/TestCase-4 directory
2.	Run ./script_1.sh on Edlab-7
3.	Once script_1 is fully run, navigate to the Edlab-1 and run
a.	./script_2.sh

# TestCase 5: 6 Peers (Three on Edlab 7 and the other three on Edlab 1)

Edlab 7: IP Address: 128.119.243.175
	PeerID 1: Seller
	PeerID 2: Seller
PeerID 3: Seller
Edlab 1: IP Address: 28.119.243.147
	PeerID 4: Buyer
PeerID 5: Buyer
PeerID 6: Buyer
Thread Pool Size: 2
Registry running on 2001 Port Number
To Run this testCase:
1.	Go to test/TestCase-5 directory
2.	Run ./script_1.sh on Edlab-7
3.	Once script_1 is fully run, navigate to the Edlab-1 and run
a.	./script_2.sh

# TestCase 6: 6 Peers (Two on Edlab 7, Two on Edlab 1 and the other Two on Edlab 2)

Edlab 7: IP Address: 128.119.243.175
	PeerID 1: Seller
PeerID 2: Buyer
Edlab 1: IP Address: 28.119.243.147
	PeerID 3: Buyer
PeerID 4: Buyer
Edlab 2: IP Address: 128.119.243.164
	PeerID 5: Seller
PeerID 6: Buyer
Thread Pool Size: 2
Registry running on 2001 Port Number
To Run this testCase:
1.	Go to test/TestCase-6 directory
2.	Run ./script_1.sh on Edlab-7
3.	Once script_1 is fully run, navigate to the Edlab-1 and run
a.	./script_2.sh
4.	Once script_2 is fully run, navigate to the Edlab-2 and run
a.	./script_3.sh


# Detailed Steps:
            
  To run the code, follow the below mentioned steps: I am explaining the steps with 3 peers with id 1,2,3 (1 acting as seller and 2,3 are acting as buyers), and the registry is assumed to run on port 2001:
The number of peers, N: is equal to the number of entries in the config.txt file.

# For Config 1: If all the peers are on present the same machine:

Steps: All the java code is present in the src folder: /lab-1-yadav/src

Corresponding Script is in: script.sh
Run it using ./script.sh after navigating to the src folder in the lab-1-yadav folder

1.	Navigate to the src folder in the lab-1-yadav folder
2.	Compile all the java files
      a.	javac *.java      
3.	export _JAVA_OPTIONS="-XX:ParallelGCThreads=2" // Limiting GC Threads to 2
4.	It’s always better to free the port which you want to use in your code, So, suppose you want all your three peers run on 1901, 1902, 1903 ports respectively
      a.	lsof –ti  :2001 | xargs kill -9  // Free the port for registry (port : 2001)
      b.	lsof –ti :1901 | xargs kill -9  // Free the port of other process as well in the similar manner
5.	Start the registry
      a.	If you want to start the registry on the default port (i.e., 1099), run,
          i.	rmiregistry  &
      b.	Else, if you want to run your registry on a different port (let’s say, 2001), run
          i.	rmiregistry 2001 &
      c.	lsof -ti tcp:2001
6.	Run all the peers :  Main Function: Inputs: PeerID, Port Number on which the Peers runs(make sure that the port number is free), Port Number on which you want registry to run(again make sure that the port number is free), number of hops, type of peer (Seller/Buyer)
       a.	java Main 1 1901 2001 4 seller config neighbors -Djava.rmi.server.codebase=file://nfs/elsrv4/users4/grad/ryadav/677/lab-1-yadav/src/ &
       b.	java Main 2 1903 2001 4 seller config neighbors -Djava.rmi.server.codebase=file://nfs/elsrv4/users4/grad/ryadav/677/lab-1-yadav/src/ &
7.	After all the peers are started, run the controller program which input as the config file.
       a.	Java Controller config
       
# Note: Number of entries in the config is equivalent to the number of the peers.

# For Config 2: If some/all peers are present on different machines:
Change the config File with the ip address of the other machines, (as described above)
Now follow the following steps:

Corresponding Script is in: script.sh
⎝	Navigate to the src folder in the lab-1-yadav folder on Machine 1
⎝	Run the script ./script_1.sh on one machine (suppose Machine Number 1)
⎝	Navigate to the src folder in the lab-1-yadav folder on Machine 2
⎝	Run it using ./script_2.sh on the other machine (suppose Machine Number 2)

1.	Navigate to the src folder in the lab-1-yadav folder
2.	Compile all the java files
        a.	javac *.java      
3.	export _JAVA_OPTIONS="-XX:ParallelGCThreads=2" // Limiting GC Threads to 2
4.	It’s always better to free the port which you want to use in your code, So, suppose you want all your three peers run on 1901, 1902, 1903 ports respectively
        a.	lsof –ti  :2001 | xargs kill -9  // Free the port for registry (port : 2001)
        b.	lsof –ti :1901 | xargs kill -9  // Free the port of other process as well in the similar manner
5.	Start the registry
        a.	If you want to start the registry on the default port (i.e., 1099), run,
            i.	rmiregistry  &
        b.  Else, if you want to run your registry on a different port (let’s say, 2001), run
            i.	rmiregistry 2001 &
        c.	lsof -ti tcp:2001
6.	Follow the above 5 steps for both the machines (Machine Number 1 and 2)
7.	Run all the peers:  Main Function: Inputs: PeerID, Port Number on which the Peers runs(make sure that the port number is free), Port Number on which you want registry to run(again make sure that the port number is free), number of hops, type of peer (Seller/Buyer)
         a.	Run the peers on Machine 1 (the peers which you want to run on Machine 1 – IP address set to the corresponding peer id in config)
             i.	java Main 1 1901 2001 4 seller config neighbors -Djava.rmi.server.codebase=file://nfs/elsrv4/users4/grad/ryadav/677/lab-1-yadav/src/ &
          b.	Run the peers on Machine 2 (the peers which you want to run on Machine 2 – IP address set to the corresponding peer id in config)
              i.	java Main 2 1903 2001 4 seller config neighbors -Djava.rmi.server.codebase=file://nfs/elsrv4/users4/grad/ryadav/677/lab-1-yadav/src/ &
8.	After all the peers are started on both the machines, run the controller program on any machine which input as the config file.
          a.	Java Controller config
9.	Also, the number of threads can be set to any number in the Peer.java file. Currently set to 2. The following line in Peer.java: Executors.newFixedThreadPool(2); (2: determines the number of threads )
