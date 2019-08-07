import com.sun.org.apache.xml.internal.utils.MutableAttrListImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastServerThread extends Thread {

    private long FIVE_SECONDS = 5000;
    private int MULTICAST_PORT = 4447;
    protected static DatagramSocket socket = null;
    public MulticastServerThread() throws IOException {
        super("MulticastServerThread");
        socket = new DatagramSocket();
    }

    public void exit1() {
        try {
            byte[] buf = new byte[1024];
            String dString = "exiting";
            buf = dString.getBytes();
            InetAddress group = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        while (true) {
            try {
                FileList f = new FileList();
                byte[] buf = new byte[1024];

                String dString = f.getList();
                buf = dString.getBytes();
                // send it
                InetAddress group = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
                socket.send(packet);
                try {
                    sleep((long) (Math.random() * FIVE_SECONDS));
                } catch (InterruptedException e) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
