javac -cp . -cp .\commons-io-2.4\commons-io-2.4.jar *.java

jar cmf manifest.txt Logger.jar *.class

java -jar Logger.jar

del *.class
del *.jar

