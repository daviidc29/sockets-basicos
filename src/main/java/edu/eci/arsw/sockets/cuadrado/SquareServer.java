package edu.eci.arsw.sockets.cuadrado;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SquareServer implements Runnable {
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public SquareServer(int port) { this.port = port; }

    public static void main(String[] args) {
        int port = parsePort(args, 35000);
        new SquareServer(port).run();
    }

    static int parsePort(String[] args, int def) {
        for (String a : args) if (a.startsWith("--port=")) return Integer.parseInt(a.substring(7));
        return def;
    }

    public void shutdown() { running.set(false); }

    @Override public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("SquareServer en puerto " + serverSocket.getLocalPort());
            while (running.get()) {
                try (Socket s = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if ("quit".equalsIgnoreCase(line.trim())) { out.println("bye"); break; }
                        try {
                            double d = Double.parseDouble(line.trim());
                            double sq = d * d;
                            out.println(Double.toString(sq));
                        } catch (NumberFormatException nfe) {
                            out.println("error: not-a-number");
                        }
                    }
                } catch (IOException ignored) { /* cliente cerró */ }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar SquareServer: " + e.getMessage());
        }
    }
}
