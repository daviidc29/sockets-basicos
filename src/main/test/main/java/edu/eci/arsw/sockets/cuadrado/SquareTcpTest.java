package edu.eci.arsw.sockets.cuadrado;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class SquareTcpTest {

    @Test
    void squareServerShouldSquareNumbers() throws Exception {
        // Puerto efímero: abrimos con 0 desde el server
        var serverSockField = java.net.ServerSocket.class.getDeclaredConstructor(int.class);
        // Arranque controlado con constructor normal de SquareServer:
        // Hacemos un truco simple: iniciamos server en puerto efímero escogido mediante ServerSocket temporal
        int port;
        try (var ss = new java.net.ServerSocket(0)) { port = ss.getLocalPort(); }
        SquareServer server = new SquareServer(port);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> f = es.submit(server);

        try (Socket c = new Socket("127.0.0.1", port);
             BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()), true)) {
            out.println("5");
            String r = in.readLine();
            assertEquals(Double.toString(25.0), r);
            out.println("quit");
            assertEquals("bye", in.readLine());
        } finally {
            server.shutdown();
            es.shutdownNow();
        }
    }
}
