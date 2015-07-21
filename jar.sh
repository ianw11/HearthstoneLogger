./build.sh

jar cmf manifest.txt Logger.jar *.class

java -jar Logger.jar $1

./cleanup.sh
