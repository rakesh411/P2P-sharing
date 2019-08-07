import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AcceptConnection extends Thread {
    public String filenamer;
    public int rport;

    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    public void run() {
        MulticastSocket socketx = null;
        try {
            socketx = new MulticastSocket(4451);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExecutorService executor = Executors.newCachedThreadPool();

        while (true) {

            try {
                byte[] buf = new byte[256];
                DatagramPacket packetx = new DatagramPacket(buf, buf.length);
                socketx.receive(packetx);
                filenamer = new String(packetx.getData(), 0, packetx.getLength());
                int i;
                for (i = 0; i < 10; i++) {
                    if (available(9000 + i)) {
                        break;
                    }
                }
                if (i < 10) rport = 9000 + i;
                else rport = 5000; // check at the client, if port is less than 9000 request again
                String dString = Integer.toString(rport);
                buf = dString.getBytes();
                InetAddress address = packetx.getAddress();
                int port = packetx.getPort();
                packetx = new DatagramPacket(buf, buf.length, address, port);
                executor.execute(new Runnable() {
                    public void run() {
                        try {
                            sendfile(filenamer, rport);
                        } catch (Exception e) {
                            System.out.println("Error occurred " + e.getLocalizedMessage());
                        } finally {
                            // ports[i-9000] = false;
                        }
                    }


                    private void sendfile(String filename, int pa) throws IOException {
                        FileInputStream fis = null;
                        // filename;
                        BufferedInputStream bis = null;
                        OutputStream os = null;
                        //	System.out.println(filename);
                        ServerSocket servsock = null;
                        Socket sock = null;
                        try {
                            servsock = new ServerSocket(pa);
                            while (true) {
                                // System.out.println("Waiting...");
                                try {
                                    sock = servsock.accept();
                                    // System.out.println("Accepted connection : " + sock);
                                    servsock.close();// cse walon ka suggestion
                                    // send file
                                    File myFile = new File(filename);
                                    byte[] mybytearray = new byte[52428800];
                                    fis = new FileInputStream(myFile);
                                    bis = new BufferedInputStream(fis);
                                    os = sock.getOutputStream();
                                    int temp;
                                    int cnt = 0;


                                    while ((temp = bis.read(mybytearray)) != -1) {
                                        os.write(mybytearray, 0, temp);
                                        cnt += temp;
                                    }
                                    // System.out.println("Sending " + filename + "(" + cnt + " bytes)");
                                    os.flush();
                                    // System.out.println("Done.");
                                } finally {
                                    if (bis != null) bis.close();
                                    if (os != null) os.close();
                                    if (sock != null) sock.close();
                                }
                            }
                        } finally {
                            if (servsock != null) servsock.close();
                        }


                    }


                });
                socketx.send(packetx);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }


    }

}







