// import java.io.*;
// import java.net.*;

// public class L4_49_59_Client_Synchronous_TDM{
//     public static void main(String[] args) {
//         if (args.length < 4) {
//             System.out.println("Argument vhul hoyeche.");
//             return;
//         }

//         String serverIp = args[0];
//         int port = Integer.parseInt(args[1]);
//         int T = Integer.parseInt(args[2]);
//         int N = args.length - 3;

//         FileInputStream[] inputs = new FileInputStream[N];
//         boolean[] eofFlags = new boolean[N];

//         try {
//             // Open files
//             for (int i = 0; i < N; i++) {
//                 inputs[i] = new FileInputStream(args[3 + i]);
//             }

//             Socket socket = new Socket(serverIp, port);
//             System.out.println("Client connected to server at " + serverIp + ":" + port);

//             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

//             boolean allEOF = false;

//             while (!allEOF) {
//                 allEOF=true;
//                 byte[] packet=new byte[N * T];

//                 for (int i = 0; i < N; i++) {
//                     for (int j = 0; j < T; j++) {
//                         int data;
            
//                         do{
//                             data = inputs[i].read();
//                             if (data == -1) break;
//                         } 
//                         while (data == '\r' || data == '\n');

//                         if(data == -1){
//                             packet[i * T + j] = (byte) '#'; 
//                             eofFlags[i] = true;
//                         } 
//                         else{
//                             packet[i * T + j] = (byte) data;
//                             eofFlags[i] = false;
//                             allEOF = false; 
//                         }
//                     }
//                 }

//                 if (!allEOF) {
//                     dos.write(packet);
//                     dos.flush();
//                     System.out.print("Sent packet: ");
//                     for (byte b:packet) {
//                         System.out.print((char) b);
//                     }
//                     System.out.println();
//                 }
//             }
//             for(FileInputStream fis:inputs) {
//                 fis.close();
//             }
//             dos.close();
//             socket.close();
//         } 
//         catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class L4_49_59_Client_Synchronous_TDM {
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 5000;

        try (Socket socket = new Socket(serverHost, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter number of files to send: ");
            int fileCount = scanner.nextInt();
            scanner.nextLine(); // consume newline

            // Send the number of files first
            dos.writeInt(fileCount);

            for (int i = 1; i <= fileCount; i++) {
                System.out.print("Enter path for file " + i + ": ");
                String filePath = scanner.nextLine();
                File file = new File(filePath);

                if (!file.exists() || !file.isFile()) {
                    System.out.println("File does not exist or is not a regular file. Skipping...");
                    // Send empty filename and zero length to indicate skipped file
                    dos.writeInt(0);
                    dos.writeLong(0);
                    continue;
                }

                String fileName = file.getName();
                long fileLength = file.length();

                // Send file name length and name
                dos.writeInt(fileName.length());
                dos.writeUTF(fileName);

                // Send file length
                dos.writeLong(fileLength);

                // Send file content
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, read);
                    }
                }

                System.out.println("Sent file " + i + ": " + fileName + " (" + fileLength + " bytes)");
            }

            System.out.println("All files sent successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
