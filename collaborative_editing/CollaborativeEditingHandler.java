import java.io.*;
import java.net.*;
import java.util.*;

public class CollaborativeEditingHandler extends Thread {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final List<CollaborativeEditingHandler> clients;
    private final List<String> document;
    private String username = "Unknown";
    private volatile boolean running = true;
    private boolean isWriter = false;

    public CollaborativeEditingHandler(Socket socket, List<CollaborativeEditingHandler> clients,
            List<String> document) {
        this.socket = socket;
        this.clients = clients;
        this.document = document;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("[HANDLER] Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String message;

            while (running && (message = in.readLine()) != null) {
                message = message.trim();

                if (message.startsWith("USERNAME:")) {
                    username = message.substring(9);

                    sendMessage("[SERVER] Select Mode (READ/WRITE):");
                    String modeSelect = in.readLine();
                    if (modeSelect != null && modeSelect.trim().equalsIgnoreCase("WRITE")) {
                        if (CollaborativeEditingServer.requestWriteAccess(this)) {
                            isWriter = true;
                            sendMessage("[SERVER] You are now the WRITER.");
                        } else {
                            sendMessage("[SERVER] Write lock busy. You are in READ mode.");
                        }
                    } else {
                        sendMessage("[SERVER] You are in READ mode.");
                    }

                    sendMessage("[SERVER] Welcome, " + username + "! Type READ to view the document.");
                    CollaborativeEditingServer.logEdit(username, "connected as " + (isWriter ? "WRITER" : "READER"));
                    continue;
                }

                processCommand(message);
            }

        } catch (SocketException e) {
            System.out.println("[HANDLER] Client " + username + " disconnected abruptly.");
        } catch (IOException e) {
            System.err.println("[HANDLER] Error reading from client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ", 3);
        String cmd = parts[0].toUpperCase();

        try {
            switch (cmd) {
                case "MODE":
                    if (parts.length < 2) {
                        sendMessage("[SERVER] Usage: MODE <READ|WRITE>");
                    } else {
                        handleModeSwitch(parts[1].toUpperCase());
                    }
                    break;

                case "READ":
                    handleRead();
                    break;

                case "APPEND":
                    if (!isWriter) {
                        sendMessage("[ERROR] Read-only mode. Use 'MODE WRITE' to switch.");
                        break;
                    }
                    if (parts.length < 2) {
                        sendMessage("[ERROR] Usage: APPEND <text>");
                    } else {
                        handleAppend(command.substring(7));
                    }
                    break;

                case "REPLACE":
                    if (!isWriter) {
                        sendMessage("[ERROR] Read-only mode. Use 'MODE WRITE' to switch.");
                        break;
                    }
                    if (parts.length < 3) {
                        sendMessage("[ERROR] Usage: REPLACE <lineNo> <text>");
                    } else {
                        int lineNo = Integer.parseInt(parts[1]);
                        String text = command.substring(command.indexOf(parts[1]) + parts[1].length()).trim();
                        handleReplace(lineNo, text);
                    }
                    break;

                case "DELETE":
                    if (!isWriter) {
                        sendMessage("[ERROR] Read-only mode. Use 'MODE WRITE' to switch.");
                        break;
                    }
                    if (parts.length < 2) {
                        sendMessage("[ERROR] Usage: DELETE <lineNo>");
                    } else {
                        int lineNo = Integer.parseInt(parts[1]);
                        handleDelete(lineNo);
                    }
                    break;

                default:
                    sendMessage("[ERROR] Unknown command. Use: READ, APPEND, REPLACE, DELETE");
            }
        } catch (NumberFormatException e) {
            sendMessage("[ERROR] Invalid line number.");
        } catch (Exception e) {
            sendMessage("[ERROR] Command failed: " + e.getMessage());
        }
    }

    private void handleModeSwitch(String newMode) {
        if (newMode.equals("READ")) {
            if (isWriter) {
                CollaborativeEditingServer.releaseWriteAccess(this);
                isWriter = false;
                sendMessage("[SERVER] Switched to READ mode.");
                CollaborativeEditingServer.logEdit(username, "switched to READ mode");
            } else {
                sendMessage("[SERVER] Already in READ mode.");
            }
        } else if (newMode.equals("WRITE")) {
            if (isWriter) {
                sendMessage("[SERVER] Already in WRITE mode.");
            } else {
                if (CollaborativeEditingServer.requestWriteAccess(this)) {
                    isWriter = true;
                    sendMessage("[SERVER] Switched to WRITE mode.");
                    CollaborativeEditingServer.logEdit(username, "switched to WRITE mode");
                } else {
                    sendMessage(
                            "[ERROR] Write lock is currently held by: " + CollaborativeEditingServer.getWriterName());
                }
            }
        } else {
            sendMessage("[ERROR] Invalid mode. Use READ or WRITE.");
        }
    }

    private void handleRead() {
        synchronized (document) {
            sendMessage("\n========== DOCUMENT ==========");
            if (document.isEmpty()) {
                sendMessage("(empty document)");
            } else {
                for (int i = 0; i < document.size(); i++) {
                    sendMessage((i + 1) + ": " + document.get(i));
                }
            }
            sendMessage("==============================\n");
        }
        CollaborativeEditingServer.logEdit(username, "READ");
    }

    private void handleAppend(String text) {
        synchronized (document) {
            document.add(text);
            int lineNo = document.size();
            String notification = "[UPDATE] " + username + " appended line " + lineNo + ": " + text;
            CollaborativeEditingServer.broadcastUpdate(notification, this);
            sendMessage("[SUCCESS] Line " + lineNo + " added.");
            CollaborativeEditingServer.logEdit(username, "APPEND line " + lineNo + ": " + text);
            CollaborativeEditingServer.saveDocument();
        }
    }

    private void handleReplace(int lineNo, String text) {
        synchronized (document) {
            if (lineNo < 1 || lineNo > document.size()) {
                sendMessage("[ERROR] Line number out of range (1-" + document.size() + ")");
                return;
            }

            String oldText = document.get(lineNo - 1);
            document.set(lineNo - 1, text);
            String notification = "[UPDATE] " + username + " replaced line " + lineNo + ": " + text;
            CollaborativeEditingServer.broadcastUpdate(notification, this);
            sendMessage("[SUCCESS] Line " + lineNo + " replaced.");
            CollaborativeEditingServer.logEdit(username,
                    "REPLACE line " + lineNo + " (was: '" + oldText + "', now: '" + text + "')");
            CollaborativeEditingServer.saveDocument();
        }
    }

    private void handleDelete(int lineNo) {
        synchronized (document) {
            if (lineNo < 1 || lineNo > document.size()) {
                sendMessage("[ERROR] Line number out of range (1-" + document.size() + ")");
                return;
            }

            String deletedText = document.remove(lineNo - 1);
            String notification = "[UPDATE] " + username + " deleted line " + lineNo;
            CollaborativeEditingServer.broadcastUpdate(notification, this);
            sendMessage("[SUCCESS] Line " + lineNo + " deleted.");
            CollaborativeEditingServer.logEdit(username, "DELETE line " + lineNo + " (was: '" + deletedText + "')");
            CollaborativeEditingServer.saveDocument();
        }
    }

    public void sendMessage(String message) {
        if (out != null && !socket.isClosed()) {
            out.println(message);
        }
    }

    private void disconnect() {
        running = false;
        CollaborativeEditingServer.logEdit(username, "disconnected");
        CollaborativeEditingServer.removeClient(this);

        if (isWriter) {
            CollaborativeEditingServer.releaseWriteAccess(this);
        }
        closeQuietly(in);
        closeQuietly(out);
        closeQuietly(socket);
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
            }
        }
    }

    private void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public String getClientName() {
        return username;
    }
}
