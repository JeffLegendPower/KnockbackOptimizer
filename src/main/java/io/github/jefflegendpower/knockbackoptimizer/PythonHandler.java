package io.github.jefflegendpower.knockbackoptimizer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class PythonHandler {

    private KnockbackOptimizer plugin;
    private AtomicReference<TCPServer> tcpServer = new AtomicReference<>(null);

    public PythonHandler(KnockbackOptimizer plugin) throws IOException {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    tcpServer.set(new TCPServer());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void sendMessage(String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tcpServer.get() != null)
                    tcpServer.get().sendMessage(message);
            }
        }.runTaskAsynchronously(plugin);
    }

    public String getMessage() {
//        AtomicReference<String> message = new AtomicReference<>();
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (tcpServer.get() != null) {
//                    try {
//                        String msg = tcpServer.get().receiveMessage();
//                        message.set(msg);
//                        System.out.println(new Gson().fromJson(msg, JsonObject.class));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }.runTaskAsynchronously(plugin);
        String message = null;

        if (tcpServer.get() != null) {
            try {
                String msg = tcpServer.get().receiveMessage();
                message = msg;
                System.out.println(new Gson().fromJson(msg, JsonObject.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (message == null) return "";
        else return message;
    }

    public static class TCPServer {
        // create tcp server port 271
        private static final int PORT = 271;
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private InputStream dataIn;

        public TCPServer() throws IOException {
            serverSocket = new ServerSocket(PORT);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            dataIn = new DataInputStream(clientSocket.getInputStream());
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public String receiveMessage() throws IOException {
            byte[] buffer = new byte[1024];
            return new String(buffer, 0, dataIn.read(buffer));
        }
    }
}
