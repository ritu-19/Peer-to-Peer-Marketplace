export _JAVA_OPTIONS="-XX:ParallelGCThreads=2"

cwd1="$PWD"
echo "$cwd1"
cd ..
cd ..
cd src
export _JAVA_OPTIONS="-XX:ParallelGCThreads=2"
javac *.java
echo -e
lsof -ti :2001 | xargs  kill -9
lsof -ti :1901 | xargs  kill -9
lsof -ti :1902 | xargs  kill -9
lsof -ti :1903 | xargs  kill -9
lsof -ti :1904 | xargs  kill -9
lsof -ti :1905 | xargs  kill -9
lsof -ti :1906 | xargs  kill -9

sleep 3
echo "killed all processes"
rmiregistry 2001 &
echo -e
sleep 3
echo "running registery"
lsof -ti tcp:2001
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
java Main 3 1903 2001 4 buyer ${path1} ${path2} -Djava.rmi.server.codebase=file:${path3} &
echo -e
sleep 3
java Main 4 1904 2001 4 buyer ${path1} ${path2} -Djava.rmi.server.codebase=file:${path3} &
#echo -e "Run the other script on the next Machine....!"
echo -e
sleep 3
echo "Run the Other Script on the next machine...."

