
import java.net.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientStart extends MulticastClient {
    static MulticastServerThread newthread;
    public static String fileToBeReceived;
    public static String server;

    public ClientStart() throws UnknownHostException {
    }

    public static void main(String[] args) throws IOException {
        new MulticastClient().start();
        newthread = new MulticastServerThread();
        newthread.start();
        new AcceptConnection().start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                newthread.exit1();
            }
        });

        while (true) {
            System.out.println("L for listfile ,G for getfile ,Q for exit");
            Scanner sc1 = new Scanner(System.in);
            char c = sc1.next().charAt(0);
            if (c == 'L' || c == 'l') {
                new MulticastClient().listfiles();
                continue;
            } else if (c == 'G' || c == 'g') {
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter server address");
                server = sc.nextLine();
                System.out.println("Enter file to be received");
                fileToBeReceived = sc.nextLine();
                System.out.println("requesting " + fileToBeReceived + " from " + server);
                // return;
                MulticastSocket socket = new MulticastSocket(4450);
                // send request
                byte[] buf = new byte[256];
                buf = fileToBeReceived.getBytes();
                InetAddress address = InetAddress.getByName(server);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4451);
                socket.send(packet);


                DatagramPacket auth;
                byte[] buf1 = new byte[4096];
                auth = new DatagramPacket(buf1, buf1.length);

                socket.receive(auth);

                String data = new String(auth.getData(), 0, auth.getLength());
                int rport = Integer.parseInt(data);
                System.out.println(rport);
                if (rport < 9000) {
                    System.out.println("no port available to access, sending request again");
                    continue;

                }
                System.out.println("Preparing to receive... " + fileToBeReceived);
                FileOutputStream fos = null;
                BufferedOutputStream bos = null;
                InputStream is;

                Socket sock = null;
                try {
                    System.out.println("Preparing to receive... " + fileToBeReceived);
                    try {
                        sock = new Socket(server, rport);

                    } catch (ConnectException e) {
                        e.printStackTrace();
                        sock = new Socket(server, rport);

                    }
                    System.out.println("Connecting...");
                    System.out.println(sock);
                    // receive file
                    byte[] mybytearray = new byte[52428800];
                    is = sock.getInputStream();
                    fos = new FileOutputStream(fileToBeReceived+"1");
                    bos = new BufferedOutputStream(fos);

                    int temp;
                    int cnt = 0;
                    int counter = 0;
                    while ((temp = is.read(mybytearray)) != -1) {
                        bos.write(mybytearray, 0, temp);
                        cnt += temp;
                        counter++;
                        if (counter % 10000 == 0)
                            System.out.println(cnt / (1024 * 1024) + "MB is downloaded");
                    }
                    bos.flush();
                    System.out.println("File " + fileToBeReceived
                            + " downloaded (" + cnt + " bytes read)");
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (fos != null) fos.close();
                    if (bos != null) bos.close();
                    if (sock != null) sock.close();
                    continue;
                }
            } else {
                newthread.exit1();
                System.exit(0);
            }
        }
    }
}