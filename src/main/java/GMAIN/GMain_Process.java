package GMAIN;

import com.mycenter.gobject.GPacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.YES_NO_OPTION;

/**
 *
 * @author Gic
 */
public class GMain_Process implements Runnable {

    Thread THREAD_TO_RUN_THIS_PROCESS = null;
    ServerSocket MAIN_SERVER_SOCKET = null;
    GSub_Process CLIENTS_PROCESS[] = null;
    public GGUI GUI = null;
    int TOTAL_CLIENT = 0;
    int PORT = 0;
    String IP = "localhost";
    int MAXIMUM_SIZE = 10;
//*********************************************

    public GMain_Process(GGUI ui, int port) {
        try {
            IP = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException ex) {
            GUI.tbMainPn.append("{1900}\n" + ex.getMessage());
        }
        PORT = port;
        GUI = ui;
        try {
            MAIN_SERVER_SOCKET = new ServerSocket(PORT);// mo socket voi port chi dinh
        } catch (IOException ex) {
            GUI.tbMainPn.append(">>> LỖI KHI KHỞI ĐỘNG MÁY CHỦ [localhost:" + MAIN_SERVER_SOCKET.getLocalPort() + "]\n" + ex.getMessage());
            if (JOptionPane.showConfirmDialog(null, "{1901} LỖI KHI KHỞI ĐỘNG MÁY CHỦ [localhost:" + MAIN_SERVER_SOCKET.getLocalPort() + "]\n" + ex.getMessage() + "\nThử lại với port ngẫu nhiên ?", "CHÚ Ý", YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (MAIN_SERVER_SOCKET != null) {
                    GUI.Restart(0);// neu ok thi khoi dong lai voi port ngau nhien (0)
                }
            } else {
                stop();
                GUI.tbPort.requestFocus();
                GUI.tbPort.selectAll();
                return;
            }
        }
        if (THREAD_TO_RUN_THIS_PROCESS == null) {
            THREAD_TO_RUN_THIS_PROCESS = new Thread(this);// chay thread voi tien trinh chua socket (this)
            THREAD_TO_RUN_THIS_PROCESS.start();// bat dau chay thread
        }
        GUI.tbMainPn.append("MÁY CHỦ KHỞI ĐỘNG THÀNH CÔNG.\n>>>" + IP + ":" + MAIN_SERVER_SOCKET.getLocalPort() + "<<<\n");
        GUI.btStart.setEnabled(false);
        GUI.tbPort.setEditable(false);
        GUI.lbIP.setText(IP + ":");
        GUI.btStart.setText("SERVER IS RUNNING...");
        GUI.btStart.setToolTipText("Server đang hoạt động");
        CLIENTS_PROCESS = new GSub_Process[MAXIMUM_SIZE];
    }

    @Override
    public void run() {
        while (THREAD_TO_RUN_THIS_PROCESS != null) {
            Socket sck = null;
            try {
                sck = MAIN_SERVER_SOCKET.accept();//*****************************************************
            } catch (IOException ex) {
                GUI.tbMainPn.append("\n{1902} LỖI KHI CHẤP NHẬN TRUY CẬP:\n" + ex.getMessage());
                continue;//*******************************************************************************
            }
            //**********************************************************************************
            //xuong toi day nghia la mot socket moi da dc chap nhan thanh cong...
            //...
            if (TOTAL_CLIENT < CLIENTS_PROCESS.length) {// neu tong so client da ket noi van con nho hon suc chua toi da  
                GUI.tbMainPn.append("--->Client[" + TOTAL_CLIENT + "][" + sck.getPort() + "] :" + sck + " đã kết nối.\n");
                CLIENTS_PROCESS[TOTAL_CLIENT] = new GSub_Process(sck, this);
                CLIENTS_PROCESS[TOTAL_CLIENT].start();
                TOTAL_CLIENT++;
            } else {
                GUI.tbMainPn.append("\nQuá số lượng Client tối đa !!! " + TOTAL_CLIENT + "/" + MAXIMUM_SIZE + " (client)\n---> Đã từ chối truy cập [" + sck.getPort() + "]");
                try {
                    sck.close();
                } catch (IOException ex) {
                    GUI.tbMainPn.append("\n{1903} LỖI KHI ĐÓNG SOCKET:\n" + ex.getMessage());
                }
            }
        }
    }

    public void stop() {
        if (THREAD_TO_RUN_THIS_PROCESS != null) {
            THREAD_TO_RUN_THIS_PROCESS.stop();
            THREAD_TO_RUN_THIS_PROCESS = null;
            MAIN_SERVER_SOCKET = null;
        }
    }

    //******************************************************************************************************************************************************************
    synchronized void processor(int ID, GPacket PACKET) {//*************************************************************************************************************
        System.out.println(PACKET.toString());
        int index = indexClient(ID);
        switch (PACKET.getAction()) {
            case "HI THERE, I WANT CONNECT WITH U !":
                CLIENTS_PROCESS[index].send(new GPacket("OKE BABE! YOUR CONNECTION ACCEPTED :)))"));
                break;
            case "I WANT TO LOG IN !":
                if (getSubThreadByUsername(PACKET.getFirst()) == null) {
                    if (checkLogin_temp(PACKET.getFirst(), PACKET.getLast())) {//======== dung tk, mk******
                        CLIENTS_PROCESS[index].USERNAME = PACKET.getFirst();
                        CLIENTS_PROCESS[index].send(new GPacket("LOGIN REPONSE", "OKE BABY!"));
                        Announce(new GPacket("ACTIVE USERs", CLIENTS_PROCESS[index].USERNAME));
                        sendActiveUsersListTo(CLIENTS_PROCESS[index].USERNAME);
                        GUI.tbMainPn.append("--->[" + CLIENTS_PROCESS[index].ID + "]: [" + CLIENTS_PROCESS[index].USERNAME + "] đã đăng nhập\n");
                    } else {//                                                    ======= sai tk/mk********
                        CLIENTS_PROCESS[index].send(new GPacket("LOGIN REPONSE", "NO"));
                    }
                } else {
                    CLIENTS_PROCESS[index].send(new GPacket("LOGIN REPONSE", "EXISTS"));
                }
                break;
            case "I WANT TO REGISTER A NEW ACCOUNT !":
                if (!userExists(PACKET.getFirst())) {
//                    DB.addUser(packet.sender, packet.content);
                    CLIENTS_PROCESS[index].USERNAME = PACKET.getFirst();
                    CLIENTS_PROCESS[index].send(new GPacket("REGISTER REPONSE", "OKE!"));
                    CLIENTS_PROCESS[index].send(new GPacket("LOGIN REPONSE", "AHIHI =)))"));
                    Announce(new GPacket("ACTIVE USERs", CLIENTS_PROCESS[index].USERNAME));
                    sendActiveUsersListTo(CLIENTS_PROCESS[index].USERNAME);
                } else {
                    CLIENTS_PROCESS[index].send(new GPacket("LOGIN REPONSE", "EXISTS"));
                }
                break;
            case "BYE BYE !":
                GUI.tbMainPn.append("--->[" + CLIENTS_PROCESS[index].ID + "]:[" + CLIENTS_PROCESS[index].USERNAME + "] đã thoát\n");
                Announce(new GPacket("LOGOUT USERs", CLIENTS_PROCESS[index].USERNAME));
                remove(ID);
                break;
            case "THIS IS FILE":
//                CLIENTS_PROCESS[index].send(new GPacket("",PACKET.toString()));
//                getClientThread(packet.recipient).send(new Message("THISISFILE", packet.sender, packet.content, packet.recipient));
                break;
            case "THIS IS MESSAGE":
                if (PACKET.getFirst().equals("<Tất cả>")) {
                    Announce(new GPacket("THIS IS MESSAGE FOR ALL", CLIENTS_PROCESS[index].USERNAME, PACKET.getLast()));
                } else {
                    getSubThreadByUsername(PACKET.getFirst()).send(new GPacket("THIS IS MESSAGE", CLIENTS_PROCESS[index].USERNAME, PACKET.getLast()));
//                    clients[findClient(ID)].send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
                }
                break;
            default:
                System.out.println("\nGoi chua xac dinh~~~~~~~~~~~~~~\n");
        }
    }

    int indexClient(int ID) {
        for (int i = 0; i < TOTAL_CLIENT; i++) {
            if (CLIENTS_PROCESS[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    boolean checkLogin_temp(String content, String password) {
        //sau day la code tam thoi de login
        //sau khi co database, noi dung trong ham nay se duoc thay the hoan toan
        //...
        if (content.equals("tao") || content.equals("mai") || content.equals("linh") && password.equals("password")) {
            return true;
        } else {
            return false;
        }
    }

    boolean userExists(String name) {
        //sau day la code tam thoi
        //sau khi co database, noi dung trong ham nay se duoc thay the hoan toan
        //...
        if (name.equals("tao") || name.equals("mai") || name.equals("linh")) {
            return true;
        }
        return false;
    }

    void Announce(GPacket gPacket) {
//        for (GSub_Process gSub_Process : CLIENTS_PROCESS) {
//            gSub_Process.send(gPacket);
//        }
        for (int i = 0; i < TOTAL_CLIENT; i++) {
            System.out.println("\n" + CLIENTS_PROCESS[i].ID + ":" + CLIENTS_PROCESS[i].USERNAME + "\n");
            if (!CLIENTS_PROCESS[i].USERNAME.isBlank() && !CLIENTS_PROCESS[i].USERNAME.isEmpty()) {
                CLIENTS_PROCESS[i].send(gPacket);
            }
        }
    }

    GSub_Process getSubThreadByUsername(String usr) {
        for (int i = 0; i < TOTAL_CLIENT; i++) {
            if (CLIENTS_PROCESS[i].USERNAME.equals(usr)) {
                return CLIENTS_PROCESS[i];
            }
        }
        return null;
    }

    private void sendActiveUsersListTo(String NAME) {
        for (int i = 0; i < TOTAL_CLIENT; i++) {
            if (!CLIENTS_PROCESS[i].USERNAME.isBlank() && !CLIENTS_PROCESS[i].USERNAME.isEmpty()) {
                getSubThreadByUsername(NAME).send(new GPacket("ACTIVE USERs", CLIENTS_PROCESS[i].USERNAME));
            }
        }
    }

    public synchronized void remove(int ID) {
        int pos = indexClient(ID);
        if (pos >= 0) {
            GSub_Process client_exit_thread = CLIENTS_PROCESS[pos];
            if (pos < TOTAL_CLIENT - 1) {// thuc hien doi cac phan tu de xoa CLIENT_PROCESS[pos]
                for (int i = pos + 1; i < TOTAL_CLIENT; i++) {
                    CLIENTS_PROCESS[i - 1] = CLIENTS_PROCESS[i];
                }
            }
            TOTAL_CLIENT--;
            try {
                client_exit_thread.close();// dong tat cac luong in, out, socket truoc...
            } catch (IOException ioe) {
                GUI.tbMainPn.append("\n {1987} Lỗi khi đóng client:[" + ID + "]\n" + ioe.getMessage());
            }
            GUI.tbMainPn.append("--->[" + ID + "]: Client[" + pos + "]:[" + client_exit_thread.USERNAME + "] đã thoát\n");
            client_exit_thread.stop();//... sau do dong thread nay
        }
    }
}
