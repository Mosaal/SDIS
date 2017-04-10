JAR = -jar
JAVA = java
PEER = Peer.jar
CLIENT = TestApp.jar

RunPeer1:
	$(JAVA) $(JAR) $(PEER) 1.0 1 1234 224.0.0.5:2345 224.0.0.6:3456 224.0.0.7:4567

RunPeer2:
	$(JAVA) $(JAR) $(PEER) 1.0 2 1235 224.0.0.5:2345 224.0.0.6:3456 224.0.0.7:4567

RunPeer3:
	$(JAVA) $(JAR) $(PEER) 1.0 3 1236 224.0.0.5:2345 224.0.0.6:3456 224.0.0.7:4567

RunBackup:
	$(JAVA) $(JAR) $(CLIENT) localhost:1234 BACKUP img.jpeg 2

RunState:
	$(JAVA) $(JAR) $(CLIENT) localhost:1234 STATE

RunRestore:
	$(JAVA) $(JAR) $(CLIENT) localhost:1234 RESTORE img.jpeg

RunReclaim:
	$(JAVA) $(JAR) $(CLIENT) localhost:1234 RECLAIM 192

RunDelete:
	$(JAVA) $(JAR) $(CLIENT) localhost:1234 DELETE img.jpeg
