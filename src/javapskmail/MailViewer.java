/*
 * MailViewer.java
 *
 * Copyright (C) 2010 Pär Crusefalk (SM0RWO)
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package javapskmail;

import java.awt.HeadlessException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author per
 */
public class MailViewer extends javax.swing.JFrame {
    
    email myobject; // The email that this window will display

    /** Creates new form MailViewer */
    public MailViewer(email inmail) {
        initComponents();
        myobject = inmail;
        if (myobject != null){
            LoadEmail();
        }
    }

    private void LoadEmail(){
        try {
            String[] attached;
            // Clear the form
            this.lblDate.setText("");
            this.lblFrom.setText("");
            this.lblTo.setText("");
            this.lblSubject.setText("");
            this.txtContent.setText("");
            this.lblReplyTo.setText("");
            this.lblAttachmentFilename.setText("");

            // First check the object!!!!!
            if (myobject != null) {
                this.lblDate.setText(myobject.getDatestr());
                this.lblFrom.setText(myobject.getFrom());
                this.lblTo.setText(myobject.getTo());
                this.lblSubject.setText(myobject.getSubject());
                this.txtContent.setText(myobject.getMessageTextPart());
                // Check for an attachment
                if (myobject.HasAttachment){                
                    this.lblAttachmentFilename.setText(myobject.getAttfilename().toString());
                    this.bSaveAttachment.setEnabled(true);
                }
            }

            // resize the textarea for scrollbars
            this.txtContent.setRows(calculaterows());
            // Move to the top
            this.txtContent.setCaretPosition(0);
        } catch (Exception e) {
            Main.log.writelog("Email window error, faulty message object?", e, true);
        }
    }

    /**
     *
     * Calculate the text size, used to indicate size to jscrollbar 
     * @return
     */
    private Integer calculaterows(){
        try {
            Integer counter = 0;
            String mytext = myobject.getContent();
            String[] poList = mytext.split("\r\n|\r|\n");
            counter = poList.length;
            return counter;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Save the attachment
     */
    private void SaveAttachment(){
        try {
            File selectedFile;
            // A file chooser for the save dialogue
            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save attachment to...");
            fc.setSelectedFile(new File(myobject.getAttfilename().toString()));
            fc.showSaveDialog(this);
            selectedFile = fc.getSelectedFile();
            if (selectedFile.exists()){
                //Ask if it should be overwritten
                int n = JOptionPane.showConfirmDialog(this,
                "Would you like to overwrite the existing file?",
                "File already exists!",
                JOptionPane.YES_NO_OPTION);
                // No, dont do anything else then
                if (n==1) return;
            }
            String myatt = myobject.getAttachment();
            if (selectedFile != null){
                boolean success = Base64.decodeToFile(myatt, selectedFile.getAbsolutePath().toString());
            }
            // Save to a file, plaintext attachment
            //FileWriter fstream = new FileWriter(selectedFile);
            //BufferedWriter out = new BufferedWriter(fstream);
            //out.write(myobject.getAttachment());
            //Close the output stream
            //out.close();
        } catch (Exception ex) {
            Main.log.writelog("Had trouble saving attachment.", ex, true);
        
        }
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

        pnlTop = new javax.swing.JPanel();
        lblSubjectLabel = new javax.swing.JLabel();
        lblFromLabel = new javax.swing.JLabel();
        lblReplytoLabel = new javax.swing.JLabel();
        lblDateLabel = new javax.swing.JLabel();
        lblToLabel = new javax.swing.JLabel();
        lblSubject = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        lblReplyTo = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblTo = new javax.swing.JLabel();
        pnlBottom = new javax.swing.JPanel();
        lAttach = new javax.swing.JLabel();
        bSaveAttachment = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblAttachmentFilename = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();
        pnlTopButtons = new javax.swing.JPanel();
        bReply = new javax.swing.JButton();
        bForward = new javax.swing.JButton();
        mnuEmailViewer = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuPrint = new javax.swing.JMenuItem();
        mnuClose = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuCut = new javax.swing.JMenuItem();
        mnuCopy = new javax.swing.JMenuItem();
        mnuPaste = new javax.swing.JMenuItem();
        mnuMessage = new javax.swing.JMenu();
        mnuReply = new javax.swing.JMenuItem();
        mnuForward = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle"); // NOI18N
        setTitle(bundle.getString("MailViewer.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(401, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlTop.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTop.setMinimumSize(new java.awt.Dimension(400, 100));
        pnlTop.setPreferredSize(new java.awt.Dimension(515, 100));
        pnlTop.setLayout(new java.awt.GridBagLayout());

        lblSubjectLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        lblSubjectLabel.setText(bundle.getString("MailViewer.lblSubjectLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblSubjectLabel, gridBagConstraints);

        lblFromLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        lblFromLabel.setText(bundle.getString("MailViewer.lblFromLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblFromLabel, gridBagConstraints);

        lblReplytoLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        lblReplytoLabel.setText(bundle.getString("MailViewer.lblReplytoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblReplytoLabel, gridBagConstraints);

        lblDateLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        lblDateLabel.setText(bundle.getString("MailViewer.lblDateLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblDateLabel, gridBagConstraints);

        lblToLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        lblToLabel.setText(bundle.getString("MailViewer.lblToLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblToLabel, gridBagConstraints);

        lblSubject.setText(bundle.getString("MailViewer.lblSubject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblSubject, gridBagConstraints);

        lblFrom.setText(bundle.getString("MailViewer.lblFrom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblFrom, gridBagConstraints);

        lblReplyTo.setText(bundle.getString("MailViewer.lblReplyTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblReplyTo, gridBagConstraints);

        lblDate.setText(bundle.getString("MailViewer.lblDate.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblDate, gridBagConstraints);

        lblTo.setText(bundle.getString("MailViewer.lblTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblTo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(pnlTop, gridBagConstraints);

        pnlBottom.setMinimumSize(new java.awt.Dimension(400, 20));
        pnlBottom.setPreferredSize(new java.awt.Dimension(515, 30));
        pnlBottom.setRequestFocusEnabled(false);
        pnlBottom.setLayout(new java.awt.GridBagLayout());

        lAttach.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lAttach.setText(bundle.getString("MailViewer.lAttach.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnlBottom.add(lAttach, gridBagConstraints);

        bSaveAttachment.setText(bundle.getString("MailViewer.bSaveAttachment.text_1")); // NOI18N
        bSaveAttachment.setEnabled(false);
        bSaveAttachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveAttachmentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        pnlBottom.add(bSaveAttachment, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setMinimumSize(new java.awt.Dimension(14, 25));
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 25));

        lblAttachmentFilename.setText(bundle.getString("MailViewer.lblAttachmentFilename.text")); // NOI18N
        jPanel1.add(lblAttachmentFilename);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlBottom.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(pnlBottom, gridBagConstraints);

        pnlContent.setPreferredSize(new java.awt.Dimension(515, 120));
        pnlContent.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(515, 120));

        txtContent.setColumns(10);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setRows(20);
        txtContent.setWrapStyleWord(true);
        txtContent.setMinimumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setViewportView(txtContent);

        pnlContent.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlContent, gridBagConstraints);

        pnlTopButtons.setMinimumSize(new java.awt.Dimension(400, 40));
        pnlTopButtons.setPreferredSize(new java.awt.Dimension(515, 40));
        pnlTopButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        bReply.setFont(new java.awt.Font("Ubuntu", 1, 12));
        bReply.setForeground(new java.awt.Color(0, 102, 51));
        bReply.setText(bundle.getString("MailViewer.bReply.text")); // NOI18N
        bReply.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bReply.setMaximumSize(new java.awt.Dimension(100, 30));
        bReply.setMinimumSize(new java.awt.Dimension(60, 30));
        bReply.setPreferredSize(new java.awt.Dimension(95, 30));
        bReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bReplyActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bReply);

        bForward.setFont(new java.awt.Font("Ubuntu", 1, 12));
        bForward.setForeground(new java.awt.Color(0, 102, 51));
        bForward.setText(bundle.getString("MailViewer.bForward.text")); // NOI18N
        bForward.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bForward.setMaximumSize(new java.awt.Dimension(100, 30));
        bForward.setPreferredSize(new java.awt.Dimension(95, 30));
        bForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bForwardActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bForward);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(pnlTopButtons, gridBagConstraints);

        jMenu1.setText(bundle.getString("MailViewer.jMenu1.text_1")); // NOI18N

        mnuPrint.setText(bundle.getString("MailViewer.mnuPrint.text")); // NOI18N
        jMenu1.add(mnuPrint);

        mnuClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        mnuClose.setText(bundle.getString("MailViewer.mnuClose.text")); // NOI18N
        mnuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCloseActionPerformed(evt);
            }
        });
        jMenu1.add(mnuClose);

        mnuEmailViewer.add(jMenu1);

        jMenu2.setText(bundle.getString("MailViewer.jMenu2.text")); // NOI18N

        mnuCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuCut.setText(bundle.getString("MailViewer.mnuCut.text")); // NOI18N
        jMenu2.add(mnuCut);

        mnuCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        mnuCopy.setText(bundle.getString("MailViewer.mnuCopy.text")); // NOI18N
        jMenu2.add(mnuCopy);

        mnuPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuPaste.setText(bundle.getString("MailViewer.mnuPaste.text")); // NOI18N
        jMenu2.add(mnuPaste);

        mnuEmailViewer.add(jMenu2);

        mnuMessage.setText(bundle.getString("MailViewer.mnuMessage.text_2")); // NOI18N

        mnuReply.setText(bundle.getString("MailViewer.mnuReply.text")); // NOI18N
        mnuReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuReplyActionPerformed(evt);
            }
        });
        mnuMessage.add(mnuReply);

        mnuForward.setText(bundle.getString("MailViewer.mnuForward.text_3")); // NOI18N
        mnuForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuForwardActionPerformed(evt);
            }
        });
        mnuMessage.add(mnuForward);

        mnuEmailViewer.add(mnuMessage);

        setJMenuBar(mnuEmailViewer);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseActionPerformed

        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_mnuCloseActionPerformed

    /**
     * Create a new message based upon this one
     * @param evt
     */
    private void mnuReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuReplyActionPerformed
        Main.mainui.ReplyMail(myobject.getFrom(), myobject.getSubject());
    }//GEN-LAST:event_mnuReplyActionPerformed

    private void mnuForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuForwardActionPerformed
        Main.mainui.ForwardMail(myobject.getSubject(), myobject.getContent());
    }//GEN-LAST:event_mnuForwardActionPerformed

    // Create a reply email
    private void bReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReplyActionPerformed
        Main.mainui.ReplyMail(myobject.getFrom(), myobject.getSubject());
    }//GEN-LAST:event_bReplyActionPerformed

    private void bForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bForwardActionPerformed
        Main.mainui.ForwardMail(myobject.getSubject(), myobject.getContent());
    }//GEN-LAST:event_bForwardActionPerformed

    private void bSaveAttachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveAttachmentActionPerformed
        SaveAttachment();
    }//GEN-LAST:event_bSaveAttachmentActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bForward;
    private javax.swing.JButton bReply;
    private javax.swing.JButton bSaveAttachment;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lAttach;
    private javax.swing.JLabel lblAttachmentFilename;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDateLabel;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblFromLabel;
    private javax.swing.JLabel lblReplyTo;
    private javax.swing.JLabel lblReplytoLabel;
    private javax.swing.JLabel lblSubject;
    private javax.swing.JLabel lblSubjectLabel;
    private javax.swing.JLabel lblTo;
    private javax.swing.JLabel lblToLabel;
    private javax.swing.JMenuItem mnuClose;
    private javax.swing.JMenuItem mnuCopy;
    private javax.swing.JMenuItem mnuCut;
    private javax.swing.JMenuBar mnuEmailViewer;
    private javax.swing.JMenuItem mnuForward;
    private javax.swing.JMenu mnuMessage;
    private javax.swing.JMenuItem mnuPaste;
    private javax.swing.JMenuItem mnuPrint;
    private javax.swing.JMenuItem mnuReply;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopButtons;
    private javax.swing.JTextArea txtContent;
    // End of variables declaration//GEN-END:variables

}
