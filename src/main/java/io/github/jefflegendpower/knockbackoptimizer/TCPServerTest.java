package io.github.jefflegendpower.knockbackoptimizer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;


public class TCPServerTest {

    @Test
    public void testTCPServer() throws IOException, InterruptedException {
        System.out.println("1");
        PythonHandler.TCPServer tcpServer = new PythonHandler.TCPServer();
        System.out.println(new Gson().fromJson(tcpServer.receiveMessage(), JsonObject.class));
        System.out.println("2");
        JsonObject message = new JsonObject();
        message.addProperty("feedback", "5.8");
        Thread.sleep(2000);
        tcpServer.sendMessage(message.toString());
    }
}
