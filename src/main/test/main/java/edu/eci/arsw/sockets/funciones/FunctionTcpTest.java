package edu.eci.arsw.sockets.funciones;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTcpTest {

    @Test
    void defaultIsCosAndCommandsSwitchFunction() throws Exception {
        int port;
        try (var ss = new java.net.ServerSocket(0)) { port = ss.getLocalPort(); }
        FunctionServer server = new FunctionServer(port);
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(server);

        try (Socket c = new Socket("127.0.0.1", port);
             BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()), true)) {

            // cos(0) = 1
            out.println("0");
            double cos0 = Double.parseDouble(in.readLine());
            assertEquals(1.0, cos0, 1e-12);

            // switch to sin
            out.println("fun:sin");
            assertEquals("ok", in.readLine());

            out.println("0");
            double sin0 = Double.parseDouble(in.readLine());
            assertEquals(0.0, sin0, 1e-12);

            // cos(pi/2) ≈ 0
            out.println("fun:cos"); assertEquals("ok", in.readLine());
            out.println(Double.toString(Math.PI/2));
            double cos90 = Double.parseDouble(in.readLine());
            assertEquals(0.0, cos90, 1e-9);

            out.println("quit"); assertEquals("bye", in.readLine());
        } finally {
            server.shutdown();
            es.shutdownNow();
        }
    }
}
