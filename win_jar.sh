.\build.sh

jar cmf manifest.txt Logger.jar *.class

java -jar Logger.jar

.\cleanup.sh
