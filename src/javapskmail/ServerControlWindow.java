/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javapskmail;

/**
 *
 * @author jdouyere
 */
public class ServerControlWindow extends javax.swing.JDialog {
    private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle");

    /**
     * Creates new form ServerControlWindow
     */
    public ServerControlWindow(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //Add list of items to jcomboboxes
        ActionComboBox.removeAllItems();
        //Stop Scanning, Start Scanning, Mute Auto-Forward, Unmute Auto-Forward, Unregister from Email/SMS
        String[] actionComboboxList = bundle.getString("ServerControl.actionList").split(",");
        for (int i=0; i < actionComboboxList.length; i++) {
            ActionComboBox.addItem(actionComboboxList[i]);
        }
        //Select first item by default
        ActionComboBox.setSelectedIndex(0);
        
        //Add list of items to unitsOfTime Combo box, e.g: Minutes,Hours
        unitsOfTimeComboBox.removeAllItems();
        //Stop Scanning, Start Scanning, Mute Auto-Forward, Unmute Auto-Forward, Unregister from Email/SMS
        String[] unitsOfTimeList = bundle.getString("ServerControl.unitsOfTimeList").split(",");
        for (int i=0; i < unitsOfTimeList.length; i++) {
            unitsOfTimeComboBox.addItem(unitsOfTimeList[i]);
        }
        //Select first item by default
        unitsOfTimeComboBox.setSelectedIndex(0);
        
        //Add list of items to amountOfTime Combo box, e.g: 10,20 etc. Start with minutes for now.
        amountComboBox.removeAllItems();
        //Time Sync, Stop Scanning, Start Scanning, Mute Auto-Forward, Unmute Auto-Forward, Unregister from Email/SMS
        String[] amountOfTimeList = {"5","10","15","20","30","45"};
        for (int i=0; i < amountOfTimeList.length; i++) {
            amountComboBox.addItem(amountOfTimeList[i]);
        }
        //Select first item by default
        amountComboBox.setSelectedIndex(0);
        //Preload call sign with this station's call sign
        forCallsignTextField.setText(Main.q.callsignAsServer);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        ActionComboBox = new javax.swing.JComboBox<>();
        forLabel = new javax.swing.JLabel();
        amountComboBox = new javax.swing.JComboBox<>();
        unitsOfTimeComboBox = new javax.swing.JComboBox<>();
        forCallsignLabel = new javax.swing.JLabel();
        forCallsignTextField = new javax.swing.JTextField();
        forAddressLabel = new javax.swing.JLabel();
        forAddressTextField = new javax.swing.JTextField();
        CancelButton = new javax.swing.JButton();
        OkButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(65536, 65536));
        setMinimumSize(new java.awt.Dimension(250, 200));
        setPreferredSize(new java.awt.Dimension(350, 300));

        jScrollPane1.setBorder(null);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(65536, 65536));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(250, 200));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 300));

        jPanel1.setMaximumSize(new java.awt.Dimension(65536, 65536));
        jPanel1.setMinimumSize(new java.awt.Dimension(300, 250));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 250));

        ActionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionComboBoxActionPerformed(evt);
            }
        });

        forLabel.setText(bundle.getString("ServerControl.forLabel")); // NOI18N

        amountComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        unitsOfTimeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        unitsOfTimeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitsOfTimeComboBoxActionPerformed(evt);
            }
        });

        forCallsignLabel.setText(bundle.getString("ServerControl.forCallsignLabel")); // NOI18N

        forCallsignTextField.setText("jTextField1");

        forAddressLabel.setText(bundle.getString("ServerControl.forAddress")); // NOI18N

        CancelButton.setText(bundle.getString("Cancel")); // NOI18N
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        OkButton.setText(bundle.getString("OK")); // NOI18N
        OkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(forLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(amountComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(unitsOfTimeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(forAddressLabel))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(forCallsignTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ActionComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(forAddressTextField)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(forCallsignLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(CancelButton)
                                .addGap(18, 18, 18)
                                .addComponent(OkButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ActionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(forLabel)
                    .addComponent(amountComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitsOfTimeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(forCallsignLabel)
                .addGap(7, 7, 7)
                .addComponent(forCallsignTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(forAddressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(forAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelButton)
                    .addComponent(OkButton))
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ActionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActionComboBoxActionPerformed
        //Enable/disable relevant sections after this selection
        int index = ActionComboBox.getSelectedIndex();
        switch (index) {
            case 0: //Time Sync
            case 2: //Scan On (for ever)
            case 4: //Unmute auto forwarding of Emails/SMSs for ever
                //Disable time value and units
                forLabel.setEnabled(false);
                amountComboBox.setEnabled(false);
                unitsOfTimeComboBox.setEnabled(false);
                //Disable callsign and alias fields
                forCallsignLabel.setEnabled(false);
                forCallsignTextField.setEnabled(false);
                forAddressLabel.setEnabled(false);
                forAddressTextField.setEnabled(false);
                break;
            case 1: //Scan Off for x amount of time
            case 3: //Mute auto forwarding of Emails/SMSs for x amount of time
                //Enable time value and units
                forLabel.setEnabled(true);
                amountComboBox.setEnabled(true);
                unitsOfTimeComboBox.setEnabled(true);
                //Disable callsign and alias fields
                forCallsignLabel.setEnabled(false);
                forCallsignTextField.setEnabled(false);
                forAddressLabel.setEnabled(false);
                forAddressTextField.setEnabled(false);
                break;
            case 5:
                //Clear links between this station's call sign and the emails and SMS numbers. This will
                //   prevent future email and SMS replies to be sent to this callsign, until a 
                //   new email or SMS is sent from this station. If "For address" is blank, clear all links
                //   for this station.
                //Disable time value and units
                forLabel.setEnabled(false);
                amountComboBox.setEnabled(false);
                unitsOfTimeComboBox.setEnabled(false);
                //Enable callsign and alias fields
                forCallsignLabel.setEnabled(true);
                forCallsignTextField.setEnabled(true);
                forAddressLabel.setEnabled(true);
                forAddressTextField.setEnabled(true);
                break;  
        }   
    }//GEN-LAST:event_ActionComboBoxActionPerformed

    private void unitsOfTimeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitsOfTimeComboBoxActionPerformed

        int index = unitsOfTimeComboBox.getSelectedIndex();
        switch (index) {
            case 0:
                //Prefill with Minutes values: 10,20 etc. Start with minutes for now.
                amountComboBox.removeAllItems();
                //Stop Scanning, Start Scanning, Mute Auto-Forward, Unmute Auto-Forward, Unregister from Email/SMS
                String[] amountOfMinutesList = {"5", "10", "15", "20", "30", "45"};
                for (int i = 0; i < amountOfMinutesList.length; i++) {
                    amountComboBox.addItem(amountOfMinutesList[i]);
                }
                //Select first item by default
                amountComboBox.setSelectedIndex(0);
                break;
            case 1:
                //Prefill with Hours values: 1,2,3,4,5,6,10,15,24
                amountComboBox.removeAllItems();
                //Stop Scanning, Start Scanning, Mute Auto-Forward, Unmute Auto-Forward, Unregister from Email/SMS
                String[] amountOfHoursList = {"1", "2", "3", "4", "5", "6", "10", "15", "24"};
                for (int i = 0; i < amountOfHoursList.length; i++) {
                    amountComboBox.addItem(amountOfHoursList[i]);
                }
                //Select first item by default
                amountComboBox.setSelectedIndex(0);
                break;
        }
    }//GEN-LAST:event_unitsOfTimeComboBoxActionPerformed

    private void OkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkButtonActionPerformed
        int index = ActionComboBox.getSelectedIndex();

        if ((!Main.mainui.selectedTo.equals("*") && Main.mainui.selectedVia.equals(""))
                || (Main.mainui.selectedTo.equals("*") && !Main.mainui.selectedVia.equals(""))) {
            switch (index) {
                case 0:
                    //Send time request to remote station
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*tim?", //Via always blank
                            false, null, 0, null);
                    break;
                case 1:
                    int amount = Integer.parseInt(amountComboBox.getSelectedItem().toString());
                    int unitIndex = unitsOfTimeComboBox.getSelectedIndex();
                    String suffix = unitIndex == 0 ? "m" : "h";
                    //Send scan off command (we do not allow for infinite scan off for the moment)
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*cmd s off " + amount + " " + suffix,
                            false, null, 0L, null);
                    break;
                case 2:
                    //Send scan on command
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*cmd s on",
                            false, null, 0L, null);
                    break;
                case 3:
                    //Send mute command to stop auto forwarding of emails and SMSs on their reception
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*cmd mute",
                            false, null, 0L, null);
                    break;
                case 4:
                    //Send unmute command to allow auto forwarding of emails and SMSs on their 
                    //  reception PROVIDED the setting allows auto forwarding at the server (the 
                    //  "Send Immediately" options in 
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*cmd unmute",
                            false, null, 0L, null);
                    break;
                case 5:
                    //Clear links between this station's call sign and the emails and SMS numbers. This will
                    //   prevent future email and SMS replies to be sent to this callsign, until a 
                    //   new email or SMS is sent from this station. If "For address" is blank, clear all links
                    //   for this station.
                    String client = forCallsignTextField.getText();
                    String address = forAddressTextField.getText();
                    RMsgTxList.addMessageToList(Main.mainui.selectedTo, Main.mainui.selectedVia, "*cmd unlink " + client.trim()
                            + " " + address.trim(), false, null, 0L, null);
                    break;
            }
            this.dispose();
        }
    }//GEN-LAST:event_OkButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

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
            java.util.logging.Logger.getLogger(ServerControlWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerControlWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerControlWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerControlWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ServerControlWindow dialog = new ServerControlWindow(new javax.swing.JFrame(), true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ActionComboBox;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton OkButton;
    private javax.swing.JComboBox<String> amountComboBox;
    private javax.swing.JLabel forAddressLabel;
    private javax.swing.JTextField forAddressTextField;
    private javax.swing.JLabel forCallsignLabel;
    private javax.swing.JTextField forCallsignTextField;
    private javax.swing.JLabel forLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> unitsOfTimeComboBox;
    // End of variables declaration//GEN-END:variables
}
