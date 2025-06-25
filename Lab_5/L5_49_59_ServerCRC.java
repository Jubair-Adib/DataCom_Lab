import java.io.*;
import java.net.*;
import java.util.*;

public class L5_49_59_ServerCRC {
    // CRC polynomials (binary, no leading 1)
    private static final String CRC8_POLY = "100000111";      // CRC-8: x^8 + x^2 + x^1 + x^0
    private static final String CRC10_POLY = "11000110011";   // CRC-10: x^10 + x^9 + x^5 + x^4 + x^1 + x^0
    private static final String CRC16_POLY = "11000000000000101"; // CRC-16: x^16 + x^15 + x^2 + x^0
    private static final String CRC32_POLY = "100000100110000010001110110110111"; // CRC-32 IEEE 802.3

    public static void main(String[] args) throws IOException {
        int handshakePort = 8000; // Handshake port
        ServerSocket serverSocket = new ServerSocket(handshakePort);
        System.out.println("Server is connecting");
        System.out.println("Waiting for the client");
        Socket clientSocket = serverSocket.accept();
        int clientPort = clientSocket.getPort();
        System.out.println("Client request is accepted at port no: " + clientPort);
        System.out.println("Server’s Communication Port: " + handshakePort);

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // For each CRC type
        String[] crcTypes = {"CRC-8", "CRC-10", "CRC-16", "CRC-32"};
        for (String crcType : crcTypes) {
            String codeword = in.readLine();
            String receivedType = in.readLine();
            if (!crcType.equals(receivedType)) {
                System.out.println("Expected CRC type " + crcType + " but got " + receivedType);
                continue;
            }
            String poly = getPoly(crcType);
            System.out.println("\n--- " + crcType + " ---");
            System.out.println("Generator Polynomial: " + poly);
            System.out.println("Received Codeword: " + codeword);
            System.out.println("CRC name: " + crcType);
            String remainder = mod2div(codeword, poly);
            System.out.println("Calculated Remainder: " + remainder);
            if (isZero(remainder)) {
                System.out.println("No error detected in transmission.\n");
            } else {
                System.out.println("Error detected in transmission!\n");
            }

            // Simulate single-bit error
            int flipPos = new Random().nextInt(codeword.length());
            String singleBitError = flipBit(codeword, flipPos);
            System.out.println("Server Output with Error Simulation (Bit flipped)");
            System.out.println("Received Codeword: " + singleBitError);
            String remErr = mod2div(singleBitError, poly);
            System.out.println("Calculated Remainder: " + remErr);
            if (!isZero(remErr)) {
                System.out.println("Error detected in transmission!");
                // Find error position by flipping each bit and checking CRC
                int foundPos = -1;
                for (int i = 0; i < singleBitError.length(); i++) {
                    String test = flipBit(singleBitError, i);
                    String rem = mod2div(test, poly);
                    if (isZero(rem)) {
                        foundPos = i + 1; // 1-based position
                        break;
                    }
                }
                if (foundPos != -1) {
                    System.out.println("Error in position: " + foundPos);
                } else {
                    System.out.println("Error position could not be determined.");
                }
            }

            // Simulate burst error
            String burstError = flipMultipleBits(codeword, 3); // flip 3 random bits
            System.out.println("Server Output with Burst Error Simulation (Multiple bits flipped)");
            System.out.println("Received Codeword: " + burstError);
            String remBurst = mod2div(burstError, poly);
            System.out.println("Calculated Remainder: " + remBurst);
            if (!isZero(remBurst)) {
                System.out.println("Error detected in transmission!");
            }
        }

        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    private static String getPoly(String type) {
        switch (type) {
            case "CRC-8": return CRC8_POLY;
            case "CRC-10": return CRC10_POLY;
            case "CRC-16": return CRC16_POLY;
            case "CRC-32": return CRC32_POLY;
            default: throw new IllegalArgumentException("Unknown CRC type");
        }
    }

    // Modulo-2 division
    private static String mod2div(String dividend, String divisor) {
        int pick = divisor.length();
        String tmp = dividend.substring(0, pick);
        int n = dividend.length();
        while (pick < n) {
            if (tmp.charAt(0) == '1')
                tmp = xor(divisor, tmp) + dividend.charAt(pick);
            else
                tmp = xor("0".repeat(pick), tmp) + dividend.charAt(pick);
            tmp = tmp.substring(1);
            pick++;
        }
        if (tmp.charAt(0) == '1')
            tmp = xor(divisor, tmp);
        else
            tmp = xor("0".repeat(pick), tmp);
        return tmp.substring(1);
    }

    private static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < b.length(); i++)
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        return result.toString();
    }

    private static boolean isZero(String s) {
        for (char c : s.toCharArray()) if (c != '0') return false;
        return true;
    }

    // Flip a single bit at position pos
    private static String flipBit(String s, int pos) {
        char[] arr = s.toCharArray();
        arr[pos] = (arr[pos] == '0') ? '1' : '0';
        return new String(arr);
    }

    // Flip n random bits
    private static String flipMultipleBits(String s, int n) {
        char[] arr = s.toCharArray();
        Random rand = new Random();
        Set<Integer> flipped = new HashSet<>();
        while (flipped.size() < n) {
            int pos = rand.nextInt(s.length());
            if (!flipped.contains(pos)) {
                arr[pos] = (arr[pos] == '0') ? '1' : '0';
                flipped.add(pos);
            }
        }
        return new String(arr);
    }

    // Try to find single-bit error position using Hamming distance
    private static int findSingleBitError(String codeword, String poly, String receivedRemainder) {
        for (int i = 0; i < codeword.length(); i++) {
            String test = flipBit(codeword, i);
            String rem = mod2div(test, poly);
            if (isZero(rem)) return i;
        }
        return -1;
    }
}
