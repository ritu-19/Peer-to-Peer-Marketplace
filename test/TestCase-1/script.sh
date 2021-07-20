export _JAVA_OPTIONS="-XX:ParallelGCThreads=2"

cwd1="$PWD"
echo "$cwd1"
cd ..
cd ..
cd src
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

cwd="$PWD"
#echo "$cwd"
str1="/config"
str2="/neighbors"

str3="/"
path1="$cwd1$str1"
path2="${cwd1}${str2}"
path3="${cwd}${str3}"
#echo "${path1}"
#echo "${path2}"
#echo "${path3}"

#echo "Running......"
java Main 1 1901 1099 4 seller ${path1} ${path2} -Djava.rmi.server.codebase=file:${path3} &
echo -e
sleep 3
java Main 2 1902 1099 4 buyer ${path1} ${path2} -Djava.rmi.server.codebase=file:${path3} &
echo -e
sleep 3
java Controller ${path1}