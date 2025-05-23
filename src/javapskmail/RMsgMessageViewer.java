/*
 * RMsgMessageViewer.java
 *
 * Copyright (C) 2021-2022 John Douyere (VK2ETA)
 * Based on Pär Crusefalk (SM0RWO) code.
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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class RMsgMessageViewer extends javax.swing.JFrame {
    
    RMsgDisplayItem mDisplayItem; // The Radio Message that this window will display
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle"); 

    /** Creates new form */
    public RMsgMessageViewer(RMsgDisplayItem mDisplayItem) {
        initComponents();
        this.mDisplayItem = mDisplayItem;
        if (mDisplayItem != null){
            LoadRadioMessage();
        }        
        if (mDisplayItem.myOwn) {
            //Disable reply button if my own message
            bReply.setEnabled(false);
        } else {
            //Disable Send again if not my own
            bTxAgain.setEnabled(false);
        }
    }

    private void LoadRadioMessage(){
        try {

            // Clear the form
            this.lblRmsgViewerFn.setText("");
            this.lblRmsgViewerFrom.setText("");
            this.lblRmsgViewerCoord.setText("");
            this.txtRMsgViewerSms.setText("");
            this.lblRmsgViewerTo.setText("");
            this.lblRmsgViewerVia.setText("");
            this.txtRMsgViewerEntry.setText("");

            // First check the object!!!!!
            if (mDisplayItem != null) {
                this.txtRMsgViewerSms.setText(RMsgMisc.unescape(mDisplayItem.mMessage.sms)); //No tracking
                this.lblRmsgViewerFn.setText(mDisplayItem.mMessage.fileName);
                this.lblRmsgViewerFrom.setText(mDisplayItem.mMessage.from);
                this.lblRmsgViewerTo.setText(mDisplayItem.mMessage.to.equals("*") ? 
                        bundle.getString("RMsgMessageViewer.ALL") : mDisplayItem.mMessage.to);
                this.lblRmsgViewerVia.setText(mDisplayItem.myOwn ? mDisplayItem.mMessage.relay : mDisplayItem.mMessage.via);
                if (mDisplayItem.mMessage.msgHasPosition) {
                    RMsgLocation pos = mDisplayItem.mMessage.position;
                    this.lblRmsgViewerCoord.setText(pos.getLatitude() + "," + pos.getLongitude());
                }
            }

            // resize the textarea for scrollbars
            this.txtRMsgViewerSms.setRows(calculaterows());
            // Move to the top
            this.txtRMsgViewerSms.setCaretPosition(0);
        } catch (Exception e) {
            Main.log.writelog("RadioMSg window error, faulty message object?", e, true);
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
            String mytext = mDisplayItem.mMessage.formatForList(false);
            String[] poList = mytext.split("\r\n|\r|\n");
            counter = poList.length;
            return counter;
        } catch (Exception e) {
            return 0;
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
        lblFromLabel = new javax.swing.JLabel();
        lblTo = new javax.swing.JLabel();
        lblTo1 = new javax.swing.JLabel();
        lblDateTime = new javax.swing.JLabel();
        lblCoordinates = new javax.swing.JLabel();
        lblRmsgViewerFrom = new javax.swing.JLabel();
        lblRmsgViewerTo = new javax.swing.JLabel();
        lblRmsgViewerFn = new javax.swing.JLabel();
        lblRmsgViewerCoord = new javax.swing.JLabel();
        lblRmsgViewerVia = new javax.swing.JLabel();
        pnlBottom = new javax.swing.JPanel();
        txtRMsgViewerEntry = new javax.swing.JTextField();
        pnlContent = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRMsgViewerSms = new javax.swing.JTextArea();
        pnlTopButtons = new javax.swing.JPanel();
        bReply = new javax.swing.JButton();
        bForward = new javax.swing.JButton();
        bShowOnMap = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bTxAgain = new javax.swing.JButton();
        mnuEmailViewer = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuPrint = new javax.swing.JMenuItem();
        mnuClose = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuCopy = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle"); // NOI18N
        setTitle(bundle.getString("RMsgMessageViewer.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(401, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlTop.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTop.setMinimumSize(new java.awt.Dimension(400, 100));
        pnlTop.setPreferredSize(new java.awt.Dimension(515, 100));
        pnlTop.setLayout(new java.awt.GridBagLayout());

        lblFromLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lblFromLabel.setText(bundle.getString("RMsgMessageViewer.lblFromLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblFromLabel, gridBagConstraints);

        lblTo.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lblTo.setText(bundle.getString("RMsgMessageViewer.lblTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblTo, gridBagConstraints);

        lblTo1.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lblTo1.setText(bundle.getString("RMsgMessageViewer.lblVia.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblTo1, gridBagConstraints);

        lblDateTime.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lblDateTime.setText(bundle.getString("RMsgMessageViewer.lblDateTime.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblDateTime, gridBagConstraints);

        lblCoordinates.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        lblCoordinates.setText(bundle.getString("RMsgMessageViewer.lblCoordinates.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlTop.add(lblCoordinates, gridBagConstraints);

        lblRmsgViewerFrom.setText(bundle.getString("RMsgMessageViewer.lblRmsgViewerFrom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblRmsgViewerFrom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblRmsgViewerTo, gridBagConstraints);

        lblRmsgViewerFn.setText(bundle.getString("RMsgMessageViewer.lblRmsgViewerFn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblRmsgViewerFn, gridBagConstraints);

        lblRmsgViewerCoord.setText(bundle.getString("RMsgMessageViewer.lblRmsgViewerCoord.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblRmsgViewerCoord, gridBagConstraints);

        lblRmsgViewerVia.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        pnlTop.add(lblRmsgViewerVia, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(pnlTop, gridBagConstraints);

        pnlBottom.setMinimumSize(new java.awt.Dimension(400, 20));
        pnlBottom.setPreferredSize(new java.awt.Dimension(515, 30));
        pnlBottom.setRequestFocusEnabled(false);

        txtRMsgViewerEntry.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 102, 102)));
        txtRMsgViewerEntry.setMaximumSize(new java.awt.Dimension(1400, 27));
        txtRMsgViewerEntry.setMinimumSize(new java.awt.Dimension(400, 27));
        txtRMsgViewerEntry.setPreferredSize(new java.awt.Dimension(510, 27));
        txtRMsgViewerEntry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtRMsgViewerEntryMouseClicked(evt);
            }
        });
        txtRMsgViewerEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRMsgViewerEntryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtRMsgViewerEntry, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(txtRMsgViewerEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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

        txtRMsgViewerSms.setEditable(false);
        txtRMsgViewerSms.setColumns(10);
        txtRMsgViewerSms.setLineWrap(true);
        txtRMsgViewerSms.setRows(20);
        txtRMsgViewerSms.setWrapStyleWord(true);
        txtRMsgViewerSms.setMinimumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setViewportView(txtRMsgViewerSms);

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

        bReply.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        bReply.setForeground(new java.awt.Color(0, 102, 51));
        bReply.setText(bundle.getString("RMsgMessageViewer.bReply.text")); // NOI18N
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

        bForward.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        bForward.setForeground(new java.awt.Color(0, 102, 51));
        bForward.setText(bundle.getString("RMsgMessageViewer.bForward.text")); // NOI18N
        bForward.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bForward.setMaximumSize(new java.awt.Dimension(100, 30));
        bForward.setPreferredSize(new java.awt.Dimension(95, 30));
        bForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bForwardActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bForward);

        bShowOnMap.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        bShowOnMap.setForeground(new java.awt.Color(0, 102, 51));
        bShowOnMap.setText(bundle.getString("RMsgMessageViewer.bShowOnMap.text")); // NOI18N
        bShowOnMap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bShowOnMap.setMaximumSize(new java.awt.Dimension(100, 30));
        bShowOnMap.setPreferredSize(new java.awt.Dimension(95, 30));
        bShowOnMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bShowOnMapActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bShowOnMap);

        bDelete.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        bDelete.setForeground(new java.awt.Color(0, 102, 51));
        bDelete.setText(bundle.getString("RMsgMessageViewer.bDelete.text")); // NOI18N
        bDelete.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bDelete.setMaximumSize(new java.awt.Dimension(100, 30));
        bDelete.setPreferredSize(new java.awt.Dimension(95, 30));
        bDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bDelete);

        bTxAgain.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N
        bTxAgain.setForeground(new java.awt.Color(0, 102, 51));
        bTxAgain.setText(bundle.getString("RMsgMessageViewer.bTxAgain.text")); // NOI18N
        bTxAgain.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bTxAgain.setMaximumSize(new java.awt.Dimension(100, 30));
        bTxAgain.setPreferredSize(new java.awt.Dimension(95, 30));
        bTxAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTxAgainActionPerformed(evt);
            }
        });
        pnlTopButtons.add(bTxAgain);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(pnlTopButtons, gridBagConstraints);

        jMenu1.setText(bundle.getString("RMsgMessageViewer.jMenu1.text_1")); // NOI18N

        mnuPrint.setText(bundle.getString("RMsgMessageViewer.mnuPrint.text")); // NOI18N
        jMenu1.add(mnuPrint);

        mnuClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        mnuClose.setText(bundle.getString("RMsgMessageViewer.mnuClose.text")); // NOI18N
        mnuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCloseActionPerformed(evt);
            }
        });
        jMenu1.add(mnuClose);

        mnuEmailViewer.add(jMenu1);

        jMenu2.setText(bundle.getString("RMsgMessageViewer.jMenu2.text")); // NOI18N

        mnuCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        mnuCopy.setText(bundle.getString("RMsgMessageViewer.mnuCopy.text")); // NOI18N
        jMenu2.add(mnuCopy);

        mnuEmailViewer.add(jMenu2);

        setJMenuBar(mnuEmailViewer);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseActionPerformed

        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_mnuCloseActionPerformed

    // Create a reply email
    private void bReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReplyActionPerformed

        String intext = txtRMsgViewerEntry.getText();
        if (intext.trim().length() > 0) {
            RMsgTxList.addMessageToList(mDisplayItem.mMessage.from, mDisplayItem.mMessage.via, intext,
                    false, null, 0L, null);
        } else {
            Main.q.Message(bundle.getString("RMsgMessageViewer.TypeAReplyFirst"), 5);
        }    
    }//GEN-LAST:event_bReplyActionPerformed

    private void bForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bForwardActionPerformed

        String dir = mDisplayItem.myOwn ? Main.dirSent : Main.dirInbox;
        RMsgObject fwRMsg = RMsgObject.extractMsgObjectFromFile(dir, mDisplayItem.mMessage.fileName, false);
        fwRMsg.to = Main.mainui.selectedTo; //As selected in the Main UI
        //Nope, use the selected via
        //fwRMsg.via = fwRMsg.relay; //Use the same route in reverse
        fwRMsg.via = Main.mainui.selectedVia;
        fwRMsg.msgHasPosition = mDisplayItem.mMessage.msgHasPosition;
        fwRMsg.position = mDisplayItem.mMessage.position;
        fwRMsg.rxMode = mDisplayItem.mMessage.rxMode;
        //Check if I was the original creator of this message, if not add myself as relay
        if (!RMsgProcessor.matchMyCallWith(mDisplayItem.mMessage.from, false)) {
            fwRMsg.relay = Main.callsignAsServer;
        }
        //Build the ro field to allow detection of duplicates
        if (fwRMsg.fileName != null && fwRMsg.receiveDate == null) {
            //Get the receivedDate for the "ro:" information to be sent at TX time
            try {
                //Example = "2017-10-25_113958";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                fwRMsg.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                fwRMsg.receiveDate.setTime(sdf.parse(fwRMsg.fileName.replaceAll(".txt", "")));
            } catch (ParseException e) {
                //Nothing
            }
        }
        //fwRMsg.sent = mDisplayItem.mMessage.sent;
        RMsgTxList.addMessageToList(fwRMsg);

    }//GEN-LAST:event_bForwardActionPerformed

    private void bShowOnMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bShowOnMapActionPerformed
        if (mDisplayItem.mMessage.position != null) {
            String geoFormat = "";
            try {
                geoFormat = RMsgObject.getGeoFormat(mDisplayItem.mMessage);
                URI uri = new URI(geoFormat);
                Desktop.getDesktop().browse(uri);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }//GEN-LAST:event_bShowOnMapActionPerformed

    private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed

        String mFileName = mDisplayItem.mMessage.fileName;
        //Get the message including binary data
        final String msgFolder = mDisplayItem.myOwn ? Main.dirSent : Main.dirInbox;
        RMsgUtil.deleteFile(msgFolder, mFileName, true);// Advise deletion
        if (mDisplayItem.mMessage.picture != null) {
            String pictureFileName = mFileName.replace(".txt", ".png");
            RMsgUtil.deleteFile(Main.dirImages, pictureFileName, true);// Advise deletion
        }
        this.setVisible(false);
        this.dispose();
        Main.mainui.buildDisplayList();
        Main.mainui.loadRadioMsg();
    }//GEN-LAST:event_bDeleteActionPerformed

    private void txtRMsgViewerEntryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtRMsgViewerEntryMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRMsgViewerEntryMouseClicked

    private void txtRMsgViewerEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRMsgViewerEntryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRMsgViewerEntryActionPerformed

    private void bTxAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTxAgainActionPerformed

        if (RMsgProcessor.matchMyCallWith(mDisplayItem.mMessage.from, false)) {
            //From me. Build a copy.
            RMsgObject sendAgain = RMsgObject.extractMsgObjectFromFile(Main.dirSent, mDisplayItem.mMessage.fileName, false);
            if (sendAgain.fileName != null && sendAgain.receiveDate == null) {
                //Get the receivedDate for the "ro:" information to be sent at TX time
                try {
                    //Example = "2017-10-25_113958";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'_'HHmmss", Locale.US);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    sendAgain.receiveDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    sendAgain.receiveDate.setTime(sdf.parse(sendAgain.fileName.replaceAll(".txt", "")));
                } catch (ParseException e) {
                    //Debug
                    //e.printStackTrace();
                }
                RMsgTxList.addMessageToList(sendAgain);
            }
        } else {
            //Not mine, warn to use forward instead
            Main.q.Message(bundle.getString("RMsgMessageViewer.NotSentByYouUseForwardInstead"), 5);
        }
    }//GEN-LAST:event_bTxAgainActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bForward;
    private javax.swing.JButton bReply;
    private javax.swing.JButton bShowOnMap;
    private javax.swing.JButton bTxAgain;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCoordinates;
    private javax.swing.JLabel lblDateTime;
    private javax.swing.JLabel lblFromLabel;
    private javax.swing.JLabel lblRmsgViewerCoord;
    private javax.swing.JLabel lblRmsgViewerFn;
    private javax.swing.JLabel lblRmsgViewerFrom;
    private javax.swing.JLabel lblRmsgViewerTo;
    private javax.swing.JLabel lblRmsgViewerVia;
    private javax.swing.JLabel lblTo;
    private javax.swing.JLabel lblTo1;
    private javax.swing.JMenuItem mnuClose;
    private javax.swing.JMenuItem mnuCopy;
    private javax.swing.JMenuBar mnuEmailViewer;
    private javax.swing.JMenuItem mnuPrint;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTopButtons;
    private javax.swing.JTextField txtRMsgViewerEntry;
    private javax.swing.JTextArea txtRMsgViewerSms;
    // End of variables declaration//GEN-END:variables

}
