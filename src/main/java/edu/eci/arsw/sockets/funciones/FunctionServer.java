package edu.eci.arsw.sockets.funciones;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.DoubleUnaryOperator;

public class FunctionServer implements Runnable {
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private DoubleUnaryOperator op = Math::cos;

    public FunctionServer(int port) { this.port = port; }

    public static void main(String[] args) {
        int port = parsePort(args, 35000);
        new FunctionServer(port).run();
    }

    static int parsePort(String[] args, int def) {
        for (String a : args) if (a.startsWith("--port=")) return Integer.parseInt(a.substring(7));
        return def;
    }

    public void shutdown() { running.set(false); }

    @Override public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("FunctionServer en puerto " + serverSocket.getLocalPort());
            while (running.get()) {
                try (Socket s = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String t = line.trim().toLowerCase(Locale.ROOT);
                        if (t.startsWith("fun:")) {
                            switch (t) {
                                case "fun:sin" -> op = Math::sin;
                                case "fun:cos" -> op = Math::cos;
                                case "fun:tan" -> op = Math::tan;
                                default -> { out.println("error: unknown-fun"); continue; }
                            }
                            out.println("ok");
                        } else if ("quit".equals(t)) {
                            out.println("bye"); break;
                        } else {
                            try {
                                double d = Double.parseDouble(t);
                                double r = op.applyAsDouble(d);
                                out.println(Double.toString(r));
                            } catch (NumberFormatException nfe) {
                                out.println("error: not-a-number");
                            }
                        }
                    }
                } catch (IOException ignored) { }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar FunctionServer: " + e.getMessage());
        }
    }
}
