import java.io.*;
import java.net.*;
import java.util.*;

public class L4_49_59_Client_Stat_TDM {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java L4_49_59_Client_Stat_TDM <IP> <Port> <InputFile1> [<InputFile2> ... <InputFileN>]");
            System.exit(1);
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);
        int numFiles = args.length - 2;

        List<FileInputStream> inputs = new ArrayList<>();

        try {
            // Print each input file content before sending
            for (int i = 2; i < args.length; i++) {
                String filename = args[i];
                String content = readFileAsString(filename);
                System.out.println(filename + ": " + content);
                inputs.add(new FileInputStream(filename));
            }

            Socket socket = new Socket(serverIP, port);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            boolean[] eofFlags = new boolean[numFiles];
            boolean allEOF = false;

            int frameNumber = 1;

            while (!allEOF) {
                allEOF = true;
                ByteArrayOutputStream frameStream = new ByteArrayOutputStream();

                for (int i = 0; i < numFiles; i++) {
                    if (!eofFlags[i]) {
                        int data = inputs.get(i).read();
                        if (data == -1) {
                            eofFlags[i] = true;
                        } else {
                            allEOF = false;
                            frameStream.write(i + 1);
                            frameStream.write(data);
                        }
                    }
                }

                if (!allEOF) {
                    byte[] frame = frameStream.toByteArray();
                    dos.writeShort(frame.length);
                    dos.write(frame);
                    dos.flush();

                    System.out.println("Frame #" + frameNumber);
                    for (int i = 0; i < frame.length; i += 2) {
                        int streamID = frame[i] & 0xFF;
                        char dataChar = (char) frame[i + 1];
                        System.out.println(" Stream ID: " + streamID + " Data: " + dataChar);
                    }
                    System.out.println();
                    frameNumber++;
                }
            }

            for (FileInputStream fis : inputs) fis.close();
            dos.close();
            socket.close();

            System.out.println("All data sent.");

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
