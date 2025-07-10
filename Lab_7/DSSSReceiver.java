import java.io.*;
import java.net.*;
import java.util.*;

public class DSSSReceiver {
    public static void main(String[] args) throws IOException {
        int listenPort = 7007;
        if (args.length < 2) {
            System.out.println("Usage: java DSSSReceiver <chipCode> <numFiles>");
            return;
        }
        String chipCodeStr = args[0];
        int numFiles = Integer.parseInt(args[1]);
        int[] chipCode = Arrays.stream(chipCodeStr.split("")).mapToInt(Integer::parseInt).toArray();
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            System.out.println("Receiver listening on port " + listenPort);
            try (Socket socket = serverSocket.accept();
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                int filesReceived = dis.readInt();
                for (int f = 0; f < filesReceived; f++) {
                    String fileName = dis.readUTF();
                    // Receive spread bits
                    int spreadLen = dis.readInt();
                    List<Integer> spreadBits = new ArrayList<>();
                    for (int i = 0; i < spreadLen; i++) spreadBits.add(dis.readInt());
                    // Receive noisy bits
                    int noisyLen = dis.readInt();
                    List<Integer> noisyBits = new ArrayList<>();
                    for (int i = 0; i < noisyLen; i++) noisyBits.add(dis.readInt());
                    // Save spread and noisy files
                    String spreadFile = "spread_" + fileName;
                    String noisyFile = "noisy_" + fileName;
                    try (PrintWriter pw = new PrintWriter(spreadFile)) {
                        for (int b : spreadBits) pw.print(b + " ");
                    }
                    try (PrintWriter pw = new PrintWriter(noisyFile)) {
                        for (int b : noisyBits) pw.print(b + " ");
                    }
                    // Despread noisy bits
                    List<Integer> recoveredBits = new ArrayList<>();
                    for (int i = 0; i < noisyBits.size(); i += chipCode.length) {
                        int ones = 0;
                        for (int j = 0; j < chipCode.length && i + j < noisyBits.size(); j++) {
                            int xor = noisyBits.get(i + j) ^ chipCode[j];
                            if (xor == 1) ones++;
                        }
                        recoveredBits.add(ones > chipCode.length / 2 ? 1 : 0);
                    }
                    StringBuilder recoveredText = new StringBuilder();
                    for (int i = 0; i < recoveredBits.size(); i += 8) {
                        int val = 0;
                        for (int j = 0; j < 8 && i + j < recoveredBits.size(); j++) {
                            val = (val << 1) | recoveredBits.get(i + j);
                        }
                        recoveredText.append((char)val);
                    }
                    String outFile = "recovered_" + fileName;
                    try (PrintWriter pw = new PrintWriter(outFile)) {
                        pw.print(recoveredText);
                    }
                    System.out.println("Recovered (" + fileName + "): " + recoveredText);
                    System.out.println("Recovered output written to: " + outFile);
                }
            }
        }
    }
}
