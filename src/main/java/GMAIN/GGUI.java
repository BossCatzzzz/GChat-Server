package GMAIN;

import com.formdev.flatlaf.FlatDarkLaf;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Gic
 */
public class GGUI extends javax.swing.JFrame {

    protected GMain_Process MAIN_PROCESS = null;

    public GGUI() {
        initComponents();
        this.setLocationRelativeTo(null);
        tbPort.selectAll();
    }

    public void Restart(int port) {// de khoi dong lai server
        if (MAIN_PROCESS != null) {// neu server con dag chay (bi loi)
            MAIN_PROCESS.stop();//thi chay ham de stop server, gan server=null
        }
        MAIN_PROCESS = new GMain_Process(this, 0);//sau cung la khoi tao lai server
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbIP = new javax.swing.JLabel();
        tbPort = new javax.swing.JTextField();
        btStart = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMainPn = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GCenter - By Gic");
        setMinimumSize(new java.awt.Dimension(500, 600));
        setPreferredSize(new java.awt.Dimension(500, 600));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        lbIP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbIP.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbIP.setText("localhost : ");
        lbIP.setToolTipText("Địa chỉ IP");
        jPanel1.add(lbIP);

        tbPort.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbPort.setText("9876");
        tbPort.setToolTipText("Số cổng");
        jPanel1.add(tbPort);

        btStart.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btStart.setText("Start");
        btStart.setToolTipText("Khởi động Server");
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });
        jPanel1.add(btStart);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        tbMainPn.setEditable(false);
        tbMainPn.setColumns(20);
        tbMainPn.setRows(5);
        tbMainPn.setMinimumSize(new java.awt.Dimension(100, 200));
        jScrollPane1.setViewportView(tbMainPn);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        int port = 0;
        try {
            port = Integer.parseInt(tbPort.getText());
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Port không hợp lệ !!\n" + numberFormatException.getMessage());
            tbPort.requestFocus();
            tbPort.selectAll();
            return;
        }
        MAIN_PROCESS = new GMain_Process(this, port);

    }//GEN-LAST:event_btStartActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (JOptionPane.showConfirmDialog(this, "Bạn có muốn dừng máy chủ?", "CHÚ Ý", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            MAIN_PROCESS.stop();
        } else {
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_formWindowClosing

//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }


        try {
            javax.swing.UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btStart;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lbIP;
    public javax.swing.JTextArea tbMainPn;
    public javax.swing.JTextField tbPort;
    // End of variables declaration//GEN-END:variables
}
//</editor-fold>
