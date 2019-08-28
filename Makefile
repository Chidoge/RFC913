default: 
	javac client/*.java 
	javac server/*.java

run_server:
	javac server/*.java
	java server.Server

run_client: 
	javac client/*.java
	java client.Client

clean: 
	rm *.class