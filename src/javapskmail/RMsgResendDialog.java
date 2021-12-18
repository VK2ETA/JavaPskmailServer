/*
 * RMsgAddToDialog.java
 *
 * Copyright (C) 2018-2021 John Douyere (VK2ETA) 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package javapskmail;

public class RMsgResendDialog extends javax.swing.JDialog {

      java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle"); 
      /**
     * Creates new form RMsgAddToDialog
     */
    public RMsgResendDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //Associate the action command with each button for retrieval on pressing OK
        jRadioButtonAllSinceLast.setActionCommand("l");
        jRadioButtonHM1.setActionCommand("1");
        jRadioButtonHM10.setActionCommand("10");
        jRadioButtonHM2.setActionCommand("2");
        jRadioButtonHM20.setActionCommand("20");
        jRadioButtonHM3.setActionCommand("3");
        jRadioButtonHM5.setActionCommand("5");
        jRadioButtonHM7.setActionCommand("7");
        jRadioButtonWhatAnyType.setActionCommand("a");
        jRadioButtonWhatLastDays.setActionCommand("d");
        jRadioButtonWhatLastHours.setActionCommand("h");
        jRadioButtonWhatLastMinutes.setActionCommand("m");
        jRadioButtonWhatLongEmails.setActionCommand("f");
        jRadioButtonWhatPositions.setActionCommand("p");
        jRadioButtonWhatRadioOnly.setActionCommand("w");
        jRadioButtonWhatShortEmails.setActionCommand("e");

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupHowMany = new javax.swing.ButtonGroup();
        buttonGroupWhatType = new javax.swing.ButtonGroup();
        jPanel4 = new javax.swing.JPanel();
        jRadioButtonWhatShortEmails = new javax.swing.JRadioButton();
        jRadioButtonWhatLongEmails = new javax.swing.JRadioButton();
        jRadioButtonWhatAnyType = new javax.swing.JRadioButton();
        jRadioButtonWhatPositions = new javax.swing.JRadioButton();
        jRadioButtonWhatRadioOnly = new javax.swing.JRadioButton();
        jRadioButtonWhatLastMinutes = new javax.swing.JRadioButton();
        jRadioButtonWhatLastHours = new javax.swing.JRadioButton();
        jRadioButtonWhatLastDays = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jRadioButtonHM1 = new javax.swing.JRadioButton();
        jRadioButtonHM3 = new javax.swing.JRadioButton();
        jRadioButtonAllSinceLast = new javax.swing.JRadioButton();
        jRadioButtonHM2 = new javax.swing.JRadioButton();
        jRadioButtonHM5 = new javax.swing.JRadioButton();
        jRadioButtonHM10 = new javax.swing.JRadioButton();
        jRadioButtonHM20 = new javax.swing.JRadioButton();
        jRadioButtonHM7 = new javax.swing.JRadioButton();
        jCheckBoxForceRelaying = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RMsgResendDialog.WhatType Border"))); // NOI18N
        jPanel4.setMaximumSize(new java.awt.Dimension(300, 35));
        jPanel4.setMinimumSize(new java.awt.Dimension(300, 35));

        buttonGroupWhatType.add(jRadioButtonWhatShortEmails);
        jRadioButtonWhatShortEmails.setText(bundle.getString("RMsgResendDialog.WhatShortEmails")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatLongEmails);
        jRadioButtonWhatLongEmails.setText(bundle.getString("RMsgResendDialog.WhatLongEmails")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatAnyType);
        jRadioButtonWhatAnyType.setSelected(true);
        jRadioButtonWhatAnyType.setText(bundle.getString("RMsgResendDialog.WhatAnyType")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatPositions);
        jRadioButtonWhatPositions.setText(bundle.getString("RMsgResendDialog.WhatPositions")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatRadioOnly);
        jRadioButtonWhatRadioOnly.setText(bundle.getString("RMsgResendDialog.WhatRadio")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatLastMinutes);
        jRadioButtonWhatLastMinutes.setText(bundle.getString("RMsgResendDialog.WhatLastMinutes")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatLastHours);
        jRadioButtonWhatLastHours.setText(bundle.getString("RMsgResendDialog.WhatLastHours")); // NOI18N

        buttonGroupWhatType.add(jRadioButtonWhatLastDays);
        jRadioButtonWhatLastDays.setText(bundle.getString("RMsgResendDialog.WhatLastDays")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonWhatLongEmails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatShortEmails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatPositions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatRadioOnly, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatLastMinutes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatLastHours, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatLastDays, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonWhatAnyType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jRadioButtonWhatAnyType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatShortEmails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatLongEmails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatRadioOnly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatPositions)
                .addGap(4, 4, 4)
                .addComponent(jRadioButtonWhatLastMinutes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatLastHours)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonWhatLastDays)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setMaximumSize(new java.awt.Dimension(300, 35));
        jPanel1.setMinimumSize(new java.awt.Dimension(300, 35));

        jButtonOk.setText(bundle.getString("RMsgaddToDialog.okBtn")); // NOI18N
        jButtonOk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText(bundle.getString("RMsgaddToDialog.cancelBtn")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonCancel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOk)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RMsgResendDialog.HowManyBorder"))); // NOI18N
        jPanel2.setMaximumSize(new java.awt.Dimension(300, 35));
        jPanel2.setMinimumSize(new java.awt.Dimension(300, 35));

        buttonGroupHowMany.add(jRadioButtonHM1);
        jRadioButtonHM1.setText(bundle.getString("RMsgResendDialog.HowManyOne")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM3);
        jRadioButtonHM3.setText(bundle.getString("RMsgResendDialog.HowManyThree")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonAllSinceLast);
        jRadioButtonAllSinceLast.setSelected(true);
        jRadioButtonAllSinceLast.setText(bundle.getString("RMsgResendDialog.All_since_last_request")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM2);
        jRadioButtonHM2.setText(bundle.getString("RMsgResendDialog.HowManyTwo")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM5);
        jRadioButtonHM5.setText(bundle.getString("RMsgResendDialog.HowManyFive")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM10);
        jRadioButtonHM10.setText(bundle.getString("RMsgResendDialog.HowManyTen")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM20);
        jRadioButtonHM20.setText(bundle.getString("RMsgResendDialog.HowManyTwenty")); // NOI18N

        buttonGroupHowMany.add(jRadioButtonHM7);
        jRadioButtonHM7.setText(bundle.getString("RMsgResendDialog.HowManySeven")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonHM1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButtonHM7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jRadioButtonAllSinceLast, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jRadioButtonHM1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHM20)
                .addContainerGap(44, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(jRadioButtonAllSinceLast)
                    .addContainerGap(238, Short.MAX_VALUE)))
        );

        jCheckBoxForceRelaying.setText(bundle.getString("RMsgResendDialog.jCheckboxForceRelaying")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jCheckBoxForceRelaying)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxForceRelaying)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed

        if (Main.mainui.selectedTo.equals("*") && Main.mainui.selectedVia.equals("")) {
            Main.q.Message(bundle.getString("you_must_select_to_or_via"), 5);
            //middleToastText("CAN'T Request Positions from \"ALL\"\n\nSelect a single TO destination above");
            //} else if (RMsgProcessor.matchMyCallWith(selectedTo, false)) {
            //middleToastText("CAN'T Request Positions from \"YOURSELF\"\n\nSelect another TO destination above");
        } else {
            String howManyToResend = this.buttonGroupHowMany.getSelection().getActionCommand();
            String whatToResend = this.buttonGroupWhatType.getSelection().getActionCommand();
            //System.out.println("howMany: " + howManyToResend);
            //System.out.println("whatType: " + whatToResend);            
            boolean forceRelayingQTC = jCheckBoxForceRelaying.isSelected();
            String resendString = " " + (forceRelayingQTC ? "r" : "") + howManyToResend + (whatToResend.length() > 0 ? " " + whatToResend : "");
            //Remove To if we use a via data as we never relay *qtc? messages
            // RMsgTxList.addMessageToList("*", selectedVia, "*qtc?" + resendString, false, null, 0, null);
            String toStr = (Main.mainui.selectedVia.equals("") || forceRelayingQTC) ? Main.mainui.selectedTo : "*";
            RMsgTxList.addMessageToList(toStr, Main.mainui.selectedVia, "*qtc?" + resendString, false, null, 0, null);
        }
        //Done
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        //Exit
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    
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
            java.util.logging.Logger.getLogger(RMsgResendDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RMsgResendDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RMsgResendDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RMsgResendDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RMsgResendDialog dialog = new RMsgResendDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroupHowMany;
    private javax.swing.ButtonGroup buttonGroupWhatType;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JCheckBox jCheckBoxForceRelaying;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButtonAllSinceLast;
    private javax.swing.JRadioButton jRadioButtonHM1;
    private javax.swing.JRadioButton jRadioButtonHM10;
    private javax.swing.JRadioButton jRadioButtonHM2;
    private javax.swing.JRadioButton jRadioButtonHM20;
    private javax.swing.JRadioButton jRadioButtonHM3;
    private javax.swing.JRadioButton jRadioButtonHM5;
    private javax.swing.JRadioButton jRadioButtonHM7;
    private javax.swing.JRadioButton jRadioButtonWhatAnyType;
    private javax.swing.JRadioButton jRadioButtonWhatLastDays;
    private javax.swing.JRadioButton jRadioButtonWhatLastHours;
    private javax.swing.JRadioButton jRadioButtonWhatLastMinutes;
    private javax.swing.JRadioButton jRadioButtonWhatLongEmails;
    private javax.swing.JRadioButton jRadioButtonWhatPositions;
    private javax.swing.JRadioButton jRadioButtonWhatRadioOnly;
    private javax.swing.JRadioButton jRadioButtonWhatShortEmails;
    // End of variables declaration//GEN-END:variables
}
