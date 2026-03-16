@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo 🛠️  Compiling Distributed Systems...

cd ..

echo [1/3] Collaborative Editing...
javac collaborative_editing/CollaborativeEditingServer.java collaborative_editing/CollaborativeEditingHandler.java collaborative_editing/CollaborativeEditingClient.java

echo [2/3] Chat Systems...
javac distributed_chat/hybrid_chat/OneToOneGroupChatServer.java distributed_chat/hybrid_chat/OneToOneGroupChatClient.java distributed_chat/hybrid_chat/GroupChatClientHandler.java
javac distributed_chat/one_to_one/oneToOneServer.java distributed_chat/one_to_one/oneToOneClient.java
javac distributed_chat/group_chat/GroupChatServer.java distributed_chat/group_chat/GroupChatClient.java

echo [3/3] RMI Systems...
javac rmi_systems/arithmetic_core/*.java

echo.
echo ✅ Compilation complete.
pause
