import java.io.*;
import java.net.*;
import java.util.*;

public class L4_49_59_Server_Stat_TDM {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

            Map<Integer, FileOutputStream> outputFiles = new HashMap<>();
            boolean running = true;

            int frameNumber = 1;

            while (running) {
                try {
                    int frameLength = dis.readUnsignedShort();
                    if (frameLength <= 0) break;

                    byte[] frame = new byte[frameLength];
                    dis.readFully(frame);

                    System.out.println("Frame #" + frameNumber);
                    for (int i = 0; i < frameLength; i += 2) {
                        int streamID = frame[i] & 0xFF;
                        byte data = frame[i + 1];
                        char dataChar = (char) data;

                        System.out.println(" Stream ID: " + streamID + " Data: " + dataChar);

                        if (!outputFiles.containsKey(streamID)) {
                            String outFileName = "Output" + streamID + ".txt";
                            outputFiles.put(streamID, new FileOutputStream(outFileName));
                            System.out.println("Created output file: " + outFileName);
                        }

                        outputFiles.get(streamID).write(data);
                    }
                    System.out.println();
                    frameNumber++;
                } catch (EOFException eof) {
                    running = false;
                }
            }

            // Close all output streams
            for (FileOutputStream fos : outputFiles.values()) {
                fos.close();
            }

            dis.close();
            clientSocket.close();

            // After closing, print contents of each output file
            for (int streamID : outputFiles.keySet()) {
                String outFileName = "Output" + streamID + ".txt";
                String content = readFileAsString(outFileName);
                System.out.println(outFileName + ": " + content);
            }

            System.out.println("Server terminated.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method to read entire file content as String
    private static String readFileAsString(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int ch;
            while ((ch = br.read()) != -1) {
                sb.append((char) ch);
            }
        }
        return sb.toString();
    }
}
