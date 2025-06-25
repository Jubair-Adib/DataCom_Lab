import java.io.*;
import java.net.*;

public class 49_59_ServerOneWay {
    public static void main(String[] args) throws IOException {
        // Step 1: Establish server socket to listen on port 5000
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server is connected at port no: " + serverSocket.getLocalPort());

        System.out.println("Waiting for the client...\n");

        // Step 2: Accept client connection
        Socket socket = serverSocket.accept();
        System.out.println("Client request is accepted at port no: " + socket.getPort());
        System.out.println("Server's Communication Port: " + socket.getLocalPort());

        // Step 3: Set up DataInputStream to read the data from the client
        DataInputStream input = new DataInputStream(socket.getInputStream());

        String receivedMessage = "";
        while (!receivedMessage.equals("stop")) {
            receivedMessage = input.readUTF(); // Read the stuffed message

            // Step 4: Apply bit de-stuffing to the received message
            String destuffedMessage = bitDeStuffing(receivedMessage);
            System.out.println("Received (destuffed) message: " + destuffedMessage);
        }

        // Close the streams and socket
        input.close();
        socket.close();
        serverSocket.close();
    }

    // Bit De-Stuffing Algorithm
    public static String bitDeStuffing(String stuffed) {
        int count = 0;
        StringBuilder destuffed = new StringBuilder();
        
        for (int i = 0; i < stuffed.length(); i++) {
            char bit = stuffed.charAt(i);
            destuffed.append(bit); // Append the bit to the destuffed string
            
            if (bit == '1') {
                count++;
                if (count == 5) {
                    i++; // Skip the next bit (the stuffed '0')
                    count = 0;  // Reset count after skipping
                }
            } else {
                count = 0; // Reset count on '0'
            }
        }
        
        return destuffed.toString(); // Return the destuffed string
    }
}
