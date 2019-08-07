import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MulticastClient extends Thread {
    static ArrayList<String> PeerCheck = new ArrayList<String>();
    static ArrayList<Boolean> PeerLive = new ArrayList<Boolean>();
    static ArrayList<String> JsonList = new ArrayList<String>();
    static private int MUTLICAST_PORT = 4447;

    public MulticastClient() throws UnknownHostException {
    }

    public static void listfiles() throws IOException {
        System.out.println("Peer size:  " + PeerCheck.size());
        for (int i = 0; i < PeerCheck.size(); i++) {
            if (PeerLive.get(i) == true) {
                System.out.println("Peer found: " + PeerCheck.get(i));
                ArrayList<String> files = new ArrayList<String>();
                FileList f = new FileList();
                files = f.getFileArray(JsonList.get(i));
                for (int j = 0; j < files.size(); j++)
                    System.out.println(files.get(j));
            }
        }

    }


    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(MUTLICAST_PORT);
            DatagramPacket packet;
            while (true) {
                byte[] buf = new byte[4096];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.equals("exiting")) {
                    for (int i = 0; i < PeerCheck.size(); i++) {
                        if (PeerCheck.get(i).equals(packet.getAddress().toString())) {
                            PeerLive.set(i, false);
                        }
                    }
                    continue;
                }
                int flag = 0;
                for (int i = 0; i < PeerCheck.size(); i++) {
                    if (PeerCheck.get(i).equals(packet.getAddress().toString())) {
                        if (JsonList.get(i) != received) {
                            JsonList.set(i, received);
                        }
                        PeerLive.set(i, true);
                        flag = 1;
                    }
                }
                if (flag == 0 ) {
                    PeerCheck.add(packet.getAddress().toString());
                    PeerLive.add(true);
                    JsonList.add(received);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



