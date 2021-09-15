/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Connectwindow.java
 *
 * Created on Dec 6, 2011, 9:56:46 AM
 */
package javapskmail;

/**
 *
 * @author rein
 */
public class Connectwindow extends javax.swing.JDialog {
    
    String TXmodemstr = Main.LastTxModem;
    String RXmodemstr = Main.LastRxModem;
    String[] Modemstrings = {"default","THOR8","MFSK16","THOR22",
                                            "MFSK32",  "PSK250R", 
                                            "PSK500R", "PSK500", 
                                            "PSK250","PSK125R","THOR11","DOMINOEX5","CTSTIA", "PSK1000",
                                            "PSK63RC5", "PSK63RC10", "PSK250RC3", "PSK125RC4", "DOMINOEX22", "DOMINOEX11"};
    String[] modemindicators = {"0","1","2","3","4","5","6","7","8","b","d","n","f", "g",  "h",  "i",  "j",  "k", "l", "m"};
    modemmodeenum TXm;
    modemmodeenum RXm;
    
 

    /** Creates new form Connectwindow */
    public Connectwindow(java.awt.Frame parent, boolean modal) {
     
        super(parent, modal);
        initComponents();
//     System.out.println("Modem=" + Main.LastRxModem);      
        this.cboServerConnect.removeAllItems();
//        Rigctl.Loadfreqs(myServer);

        // Add servers from main
        for (int i = 0; i < Main.Servers.length; i++){
            if (!Main.Servers[i].equals("")) cboServerConnect.addItem(Main.Servers[i]);
        }
        this.cboServerConnect.setSelectedItem(Main.mainui.cboServer.getSelectedItem());
        
        //set modems
            this.jComboBoxTXModem.removeAllItems();
            this.jComboBoxRXModem.removeAllItems();       

            int i = 0;
            int k = 0;
            int l = 0;
            for (i = 0; i < Main.modes.length(); i++) {
                    if (Main.modes.substring(i, i+1).equals("g")){
                        jComboBoxTXModem.addItem("PSK1000");
                        jComboBoxRXModem.addItem("PSK1000"); 
                        if (Main.LastRxModem.equals("PSK1000")){
                            l = k;
                        }
                        k++;
                    } else if (Main.modes.substring(i, i+1).equals("7")) {
                        jComboBoxTXModem.addItem("PSK500");
                        jComboBoxRXModem.addItem("PSK500");
                        if (Main.LastRxModem.equals("PSK500")){
                            l = k;
                        }
                        k++;                        
                     } else if (Main.modes.substring(i, i+1).equals("6")){
                        jComboBoxTXModem.addItem("PSK500R");
                        jComboBoxRXModem.addItem("PSK500R");
                        if (Main.LastRxModem.equals("PSK500R")){
                            l = k;
                        }
                        k++;
                     } else if (Main.modes.substring(i, i+1).equals("5")){
                        jComboBoxTXModem.addItem("PSK250R");
                        jComboBoxRXModem.addItem("PSK250R");
                        if (Main.LastRxModem.equals("PSK250R")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("l")){
                        jComboBoxTXModem.addItem("DOMINOEX22");
                        jComboBoxRXModem.addItem("DOMINOEX22");
                        if (Main.LastRxModem.equals("DOMINOEX22")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("m")){
                        jComboBoxTXModem.addItem("DOMINOEX11");
                        jComboBoxRXModem.addItem("DOMINOEX11");
                        if (Main.LastRxModem.equals("DOMINOEX11")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("4")){
                        jComboBoxTXModem.addItem("MFSK32");
                        jComboBoxRXModem.addItem("MFSK32");
                        if (Main.LastRxModem.equals("MFSK32")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("3")){
                        jComboBoxTXModem.addItem("THOR22");
                        jComboBoxRXModem.addItem("THOR22");
                        if (Main.LastRxModem.equals("THOR22")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("2")){
                        jComboBoxTXModem.addItem("MFSK16");
                        jComboBoxRXModem.addItem("MFSK16");
                        if (Main.LastRxModem.equals("PSK1000")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("1")){
                        jComboBoxTXModem.addItem("THOR8");
                        jComboBoxRXModem.addItem("THOR8");
                        if (Main.LastRxModem.equals("THOR8")){
                            l = k;
                        }
                        k++;
                     }else if (Main.modes.substring(i, i+1).equals("8")){
                        jComboBoxTXModem.addItem("PSK250");
                        jComboBoxRXModem.addItem("PSK250");
                         if (Main.LastRxModem.equals("PSK250")){
                            l = k;
                        }
                        k++;                   }else if (Main.modes.substring(i, i+1).equals("b")){
                        jComboBoxTXModem.addItem("PSK125R");
                        jComboBoxRXModem.addItem("PSK125R");
                        if (Main.LastRxModem.equals("PSK125R")){
                            l = k;
                        }
                        k++;
                    }else if (Main.modes.substring(i, i+1).equals("n")){
                        jComboBoxTXModem.addItem("DOMINOEX5");
                        jComboBoxRXModem.addItem("DOMINOEX5");
                        if (Main.LastRxModem.equals("DOMINOEX5")){
                            l = k;
                        }
                        k++;                       
                    }
                }
//            System.out.println("lastRXmodem=" + Main.LastRxModem);

                        jComboBoxTXModem.setSelectedIndex(l);
                        jComboBoxRXModem.setSelectedIndex(l);     
    }
    
    private String getrxmodemindex(){
        String index = "8";
        for (int i = 0; i < Modemstrings.length; i++){
            if (Modemstrings[i].equals(jComboBoxRXModem.getSelectedItem().toString())) {
                index = modemindicators[i];
                break;
            }
        }
        
        return index;
    }
     private String gettxmodemindex(){
        String index = "8";
        for (int i = 0; i < Modemstrings.length; i++){
            if (Modemstrings[i].equals(jComboBoxTXModem.getSelectedItem().toString())) {
                index = modemindicators[i];
                break;
            }
        }
        
        return index;
    }   


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBoxTXModem = new javax.swing.JComboBox();
        jComboBoxRXModem = new javax.swing.JComboBox();
        cboServerConnect = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ConnectServerButton = new javax.swing.JButton();
        ServerConnectCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connect");

        jLabel1.setText("Server");

        jComboBoxTXModem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTXModemActionPerformed(evt);
            }
        });

        jComboBoxRXModem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRXModemActionPerformed(evt);
            }
        });

        cboServerConnect.setEditable(true);
        cboServerConnect.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 12));
        cboServerConnect.setMinimumSize(new java.awt.Dimension(150, 27));
        cboServerConnect.setPreferredSize(new java.awt.Dimension(150, 27));

        jLabel2.setText("TX");

        jLabel3.setText("RX");

        jLabel4.setFont(new java.awt.Font("DejaVu Sans", 1, 14));
        jLabel4.setText("Connect to server");

        ConnectServerButton.setText("Connect");
        ConnectServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectServerButtonActionPerformed(evt);
            }
        });

        ServerConnectCancel.setText("Cancel");
        ServerConnectCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ServerConnectCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(ServerConnectCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ConnectServerButton, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxRXModem, 0, 146, Short.MAX_VALUE)
                            .addComponent(jComboBoxTXModem, 0, 146, Short.MAX_VALUE)
                            .addComponent(cboServerConnect, 0, 146, Short.MAX_VALUE))))
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboServerConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxTXModem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxRXModem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConnectServerButton)
                    .addComponent(ServerConnectCancel))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jComboBoxTXModemActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:        
        TXmodemstr = this.jComboBoxTXModem.getSelectedItem().toString();
        Main.LastTxModem = TXmodemstr;
//        System.out.println("Setting TX:" + Main.LastTxModem);
    }                                                

    private void jComboBoxRXModemActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        // TODO add your handling code here:
        RXmodemstr = this.jComboBoxRXModem.getSelectedItem().toString();
//        System.out.println("rxm:" + RXmodemstr);
        Main.RXmodemindex = getrxmodemindex();
        Main.LastRxModem = RXmodemstr;
        Main.defaultmode = Main.convmodem(RXmodemstr);
        //System.out.println("Setting RX:" + Main.LastRxModem);
    }                                                

    private void ServerConnectCancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }                                                   

    private void ConnectServerButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //System.out.println("TXmodemstr=" + TXmodemstr);
        TXm = Main.convmodem(TXmodemstr);
        RXm = Main.convmodem(RXmodemstr);
        Main.TxModem = TXm;
        Main.RxModem = RXm;
        Main.RxModemString = RXmodemstr;
        String myServer = cboServerConnect.getSelectedItem().toString();
        Main.mainui.myarq.setServer(myServer);
        //VK2ETA add main.q
        Main.q.setServer(myServer);
        //Not on the fly
        //Main.configuration.setServer(myServer);
        Main.mainui.ConnectButtonAction();
        this.dispose();
    }                                                   
       
 
     /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Connectwindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Connectwindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Connectwindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Connectwindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Connectwindow dialog = new Connectwindow(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });

                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify                     
    private javax.swing.JButton ConnectServerButton;
    private javax.swing.JButton ServerConnectCancel;
    private javax.swing.JComboBox cboServerConnect;
    private javax.swing.JComboBox jComboBoxRXModem;
    private javax.swing.JComboBox jComboBoxTXModem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration                   
}
