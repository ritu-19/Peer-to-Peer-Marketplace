#export _JAVA_OPTIONS="-XX:ParallelGCThreads=2"

javac *.java
echo -e
lsof -ti :1099 | xargs  kill -9
lsof -ti :1901 | xargs  kill -9
lsof -ti :1902 | xargs  kill -9

sleep 3
echo "killed all processes"
rmiregistry 1099 &
echo -e
sleep 3
echo "running registery"
lsof -ti tcp:1099
echo -e

java Main 1 1901 1099 4 seller config neighbors -Djava.rmi.server.codebase=file:/Users/ritu/Desktop/Lab-1/src/ &
echo -e
sleep 3
java Main 2 1902 1099 4 buyer config neighbors -Djava.rmi.server.codebase=file:/Users/ritu/Desktop/Lab-1/src/ &
echo -e
sleep 3
java Controller config