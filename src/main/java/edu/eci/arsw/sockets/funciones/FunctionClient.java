package edu.eci.arsw.sockets.funciones;

import java.io.*;
import java.net.Socket;

public class FunctionClient {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 35000;
        for (String a : args) {
            if (a.startsWith("--host=")) host = a.substring(7);
            if (a.startsWith("--port=")) port = Integer.parseInt(a.substring(7));
        }
        try (Socket s = new Socket(host, port);
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)) {
            System.out.println("Conectado a " + host + ":" + port + " (comandos: fun:sin|cos|tan, números, quit)");
            String line;
            while ((line = stdin.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) break;
                System.out.println(resp);
                if ("bye".equalsIgnoreCase(resp)) break;
            }
        }
    }
}
