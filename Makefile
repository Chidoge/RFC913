default: 
	javac SFTP/client/*.java 
	javac SFTP/server/*.java

run_server:
	javac SFTP/server/*.java
	java SFTP.server.Server

run_client: 
	javac SFTP/client/*.java
	java SFTP.client.Client

clean: 
	rm SFTP/client/*.class
	rm SFTP/server/*.class