/*
 * aboutform.java
 *
 * Created on den 26 november 2008, 15:23
 */

package javapskmail;

/**
 *
 * @author  per
 */
public class AboutForm extends javax.swing.JFrame {

    /** Creates new form aboutform */
    public AboutForm() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(470, 200));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("About JPSKmail");
        jPanel1.add(jLabel1);

        getContentPane().add(jPanel1, new java.awt.GridBagConstraints());

        jPanel2.setMinimumSize(new java.awt.Dimension(80, 39));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setText(" OK ");
        jButton1.setMaximumSize(new java.awt.Dimension(80, 29));
        jButton1.setMinimumSize(new java.awt.Dimension(60, 29));
        jButton1.setPreferredSize(new java.awt.Dimension(70, 29));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        getContentPane().add(jPanel2, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(460, 170));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(480, 230));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Welcome to JPSKmail Server and Client application, \n\nJava PSKmail Server  V3.0,0,0\n\nThis is the cross-platforms Client and Server for PSKmail developed in java.\nTo find out more about PSKmail please visit http://pskmail.wikidot.com/.\n\n(c) Copyright 2018-2021 John Douyere (VK2ETA), based \non jPskmail client 1.7 by Pär Crusefalk and Rein Couperus\n\nDistributed under the GNU General Public License version 3 or later.\nThis is free software: you are free to change and redistribute it.\nThere is NO WARRANTY, to the extent permitted by law.\n");
        jTextArea1.setMaximumSize(new java.awt.Dimension(1024, 768));
        jTextArea1.setMinimumSize(new java.awt.Dimension(450, 100));
        jTextArea1.setPreferredSize(new java.awt.Dimension(450, 150));
        jScrollPane1.setViewportView(jTextArea1);

        jTabbedPane1.addTab("About", jScrollPane1);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(460, 170));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(480, 230));

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        jTextArea2.setRows(5);
        jTextArea2.setText("Developers:\nPär Crusefalk, SM0RWO\nRein Couperus, PA0R\nKlaus Lohmann, DL8OAH\nJohn Douyere, VK2ETA\n\nContributors:\nRoberto Abis, IS0GRB\nFranco Spinelli, IW2DHW\nFrederic Bouchet, F4EED\n\n");
        jTextArea2.setMaximumSize(new java.awt.Dimension(1024, 768));
        jTextArea2.setMinimumSize(new java.awt.Dimension(450, 100));
        jTextArea2.setPreferredSize(new java.awt.Dimension(450, 150));
        jScrollPane2.setViewportView(jTextArea2);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Written by", jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    this.setVisible(false);
}//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables

}
