// javac L4_49_59_Server_Synchronous_TDM.java
// java L4_49_59_Server_Synchronous_TDM

import java.io.*;
import java.net.*;

public class L4_49_59_Server_Synchronous_TDM{
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is connected at port no: " + PORT);
            System.out.println("Server is connecting");
            System.out.println("Waiting for the client");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client request is accepted at port no: " + clientSocket.getPort());
            System.out.println("Server’s Communication Port: " + PORT);

            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            // Prepare output files
            FileOutputStream fos1 = new FileOutputStream("output1.txt");
            FileOutputStream fos2 = new FileOutputStream("output2.txt");
            FileOutputStream fos3 = new FileOutputStream("output3.txt");

            int T = 2;  // time slot bytes per file
            int N = 3;  // number of files

            byte[] buffer = new byte[N * T];
            int bytesRead;

            while ((bytesRead = dis.read(buffer)) != -1) {
                // For each byte, write to corresponding output file if not '#'
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < T; j++) {
                        int idx = i * T + j;
                        if (idx >= bytesRead) break;
                        byte b = buffer[idx];
                        if (b != (byte) '#') {
                            if (i == 0) fos1.write(b);
                            else if (i == 1) fos2.write(b);
                            else if (i == 2) fos3.write(b);
                        }
                    }
                }
            }

            // Close all
            fos1.close();
            fos2.close();
            fos3.close();
            dis.close();
            clientSocket.close();
            serverSocket.close();

            System.out.println("Files reconstructed successfully!");

            // Optional: print content of output files
            System.out.print("output1.txt: ");
            printFile("output1.txt");
            System.out.print("output2.txt: ");
            printFile("output2.txt");
            System.out.print("output3.txt: ");
            printFile("output3.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.print(line);
        }
        System.out.println();
        br.close();
    }
}

