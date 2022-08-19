package GMAIN;

import com.mycenter.gobject.GPacket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Gic
 */
public class GSub_Process extends Thread {

    Socket THIS_SOCKET = null;
    GMain_Process MAIN_PROCESS = null;
    GGUI GUI = null;
    private int PORT = -1;// day la port cua client
    String IP="";
    String USERNAME = "";
    ObjectInputStream STREAM_IN = null;
    ObjectOutputStream STREAM_OUT = null;

    //************************************
    public GSub_Process(Socket sub_socket, GMain_Process main) {
        super();
        THIS_SOCKET = sub_socket;
        MAIN_PROCESS = main;
        GUI = main.GUI;
        PORT = sub_socket.getPort();
        IP=sub_socket.getInetAddress().getHostAddress();
        try {
            STREAM_OUT = new ObjectOutputStream(THIS_SOCKET.getOutputStream());
            STREAM_OUT.flush();
            STREAM_IN = new ObjectInputStream(THIS_SOCKET.getInputStream());
        } catch (IOException ex) {
            GUI.tbMainPn.append("\n[" + getID() + "]" + "Error: create(): " + ex.getMessage());
        }
    }

    public void send(GPacket pkt) {
        try {
            STREAM_OUT.writeObject(pkt);
            STREAM_OUT.flush();
            System.out.println(pkt.toString());
        } catch (IOException ex) {
            GUI.tbMainPn.append("\n[" + getID() + "]" + "Error: send(): " + ex.getMessage());
        }
    }

    public String getID() {
        return IP+":"+PORT;
    }

    @Override
    public void run() {
        GPacket pkg = null;
        while (true) {
            try {
                pkg = (GPacket) STREAM_IN.readObject();
            } catch (Exception ex) {// khi ma ex o day quang ra tuc la streamin bi disconnect -> socket disconnect -> client da thoat
                GUI.tbMainPn.append("\n---> [" + getID() + "] đã ngắt kết nối:" + ex.getMessage());
                MAIN_PROCESS.remove(getID());// xoa client nay
                stop();//sau do stop thread nay
            }
            MAIN_PROCESS.processor(getID(), pkg);// goi ham xu ly chinh cua server *********************************************************************************

        }
    }

    public void close() throws IOException {
        if (STREAM_IN != null) {
            STREAM_IN.close();
        }
        if (STREAM_OUT != null) {
            STREAM_OUT.close();
        }
        if (THIS_SOCKET != null) {
            THIS_SOCKET.close();
        }

    }
}
