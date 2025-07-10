import java.io.*;
import java.net.*;
import java.util.*;

public class DSSSSender {
    public static void main(String[] args) throws IOException {
        int serverPort = 7007;
        if (args.length < 4) {
            System.out.println("Usage: java DSSSSender <chipCode> <noiseProbability> <inputFile1> [<inputFile2> ...]");
            return;
        }
        String chipCodeStr = args[0];
        double noiseProb = Double.parseDouble(args[1]);
        int[] chipCode = Arrays.stream(chipCodeStr.split("")).mapToInt(Integer::parseInt).toArray();
        List<String> inputFiles = new ArrayList<>();
        for (int i = 2; i < args.length; i++) inputFiles.add(args[i]);

        // Prepare data for each file
        List<String> fileNames = new ArrayList<>();
        List<List<Integer>> spreadBitsList = new ArrayList<>();
        List<List<Integer>> noisyBitsList = new ArrayList<>();
        List<String> originalList = new ArrayList<>();
        for (String inputFile : inputFiles) {
            List<Integer> spreadBits = new ArrayList<>();
            StringBuilder original = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                int c;
                while ((c = br.read()) != -1) {
                    original.append((char)c);
                    String bin = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                    for (char bit : bin.toCharArray()) {
                        int b = bit - '0';
                        for (int chip : chipCode) {
                            spreadBits.add(b ^ chip);
                        }
                    }
                }
            }
            // Simulate channel noise
            Random rand = new Random();
            List<Integer> noisyBits = new ArrayList<>();
            for (int bit : spreadBits) {
                if (rand.nextDouble() < noiseProb) {
                    noisyBits.add(bit ^ 1); // flip
                } else {
                    noisyBits.add(bit);
                }
            }
            fileNames.add(inputFile);
            spreadBitsList.add(spreadBits);
            noisyBitsList.add(noisyBits);
            originalList.add(original.toString());
        }
        // Send files via socket
        try (Socket socket = new Socket("localhost", serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            dos.writeInt(fileNames.size()); // number of files
            for (int i = 0; i < fileNames.size(); i++) {
                dos.writeUTF(fileNames.get(i));
                // Send spread bits
                dos.writeInt(spreadBitsList.get(i).size());
                for (int b : spreadBitsList.get(i)) dos.writeInt(b);
                // Send noisy bits
                dos.writeInt(noisyBitsList.get(i).size());
                for (int b : noisyBitsList.get(i)) dos.writeInt(b);
            }
        }
        // Log
        for (int i = 0; i < fileNames.size(); i++) {
            System.out.println("Original (" + fileNames.get(i) + "): " + originalList.get(i));
            System.out.println("Spread bits for " + fileNames.get(i) + ": " + spreadBitsList.get(i));
            System.out.println("Noisy bits for " + fileNames.get(i) + ": " + noisyBitsList.get(i));
        }
        System.out.println("All files sent to receiver at localhost:" + serverPort);
    }
}
