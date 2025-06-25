//10.33.2.210
import java.io.*;
import java.net.*;

public class 49_59_ClientOneWay {
    public static void main(String[] args) throws IOException {
        // Step 1: Establish socket connection to the server
        Socket socket = new Socket("10.33.2.210", 5000);
        System.out.println("Client Connected at server Handshaking port " + socket.getPort());
        System.out.println("Client's communication port " + socket.getLocalPort());
        System.out.println("Client is Connected");

        // Step 2: Set up DataOutputStream to send data to the server
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        // Step 3: Read binary data from the file
        BufferedReader fileReader = new BufferedReader(new FileReader("input.txt"));
        String message;
        while ((message = fileReader.readLine()) != null) {
            System.out.println("Original message read from file: " + message);

            // Step 4: Apply bit stuffing to the message before sending
            String stuffedMessage = bitStuffing(message);
            System.out.println("Sending stuffed message: " + stuffedMessage);

            // Step 5: Send the stuffed data to the server
            output.writeUTF(stuffedMessage);
        }

        // Close the file reader, output stream, and socket
        fileReader.close();
        output.close();
        socket.close();
    }

    // Bit Stuffing Algorithm
    public static String bitStuffing(String input) {
        int count = 0;
        StringBuilder stuffed = new StringBuilder();
        
        for (char bit : input.toCharArray()) {
            stuffed.append(bit); // Append the bit to the stuffed string
            
            if (bit == '1') {
                count++;
                if (count == 5) {
                    stuffed.append('0'); // Stuff a '0' after 5 consecutive 1's
                    count = 0;  // Reset the count after stuffing
                }
            } else {
                count = 0; // Reset the count on '0'
            }
        }
        
        return stuffed.toString(); // Return the stuffed string
    }
}
