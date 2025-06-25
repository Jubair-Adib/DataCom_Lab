import java.io.*;
import java.net.*;

public class L5_49_59_ClientCRC {
    // CRC polynomials (binary, no leading 1)
    private static final String CRC8_POLY = "100000111";      // CRC-8: x^8 + x^2 + x^1 + x^0
    private static final String CRC10_POLY = "11000110011";   // CRC-10: x^10 + x^9 + x^5 + x^4 + x^1 + x^0
    private static final String CRC16_POLY = "11000000000000101"; // CRC-16: x^16 + x^15 + x^2 + x^0
    private static final String CRC32_POLY = "100000100110000010001110110110111"; // CRC-32 

    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String data = readFile(inputFile);
        System.out.println("Client connected to the server on Handshaking port 8000");
        Socket socket = new Socket("localhost", 8000);
        int clientPort = socket.getLocalPort();
        System.out.println("Client’s Communication Port: " + clientPort);
        System.out.println("Client is Connected");
        System.out.println("File Content: " + data);
        String binaryData = toBinaryString(data);
        System.out.println("Converted Binary Data: " + binaryData);

        // For each CRC type
        String[] crcTypes = {"CRC-8", "CRC-10", "CRC-16", "CRC-32"};
        for (String crcType : crcTypes) {
            String poly = getPoly(crcType);
            int polyLen = poly.length();
            System.out.println("\n" + crcType + " Generator Polynomial: " + poly);
            String dataToDivide = binaryData + "0".repeat(polyLen - 1);
            System.out.println("After Appending zeros Data to Divide: " + dataToDivide);
            String remainder = mod2div(dataToDivide, poly);
            System.out.println("CRC Remainder: " + remainder);
            String codeword = binaryData + remainder;
            System.out.println("Transmitted Codeword to Server: " + codeword);

            // Send codeword and CRC type to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(codeword);
            out.println(crcType);
        }
        socket.close();
    }

    private static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static String toBinaryString(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return sb.toString();
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
}
