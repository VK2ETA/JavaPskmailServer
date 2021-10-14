/*
 * GetWebPageDialog.java
 *
 * Created on March 17, 2009, 3:40 PM
 */
package javapskmail;

/**
 *
 * @author rein
 */
public class GetWebPageDialog extends javax.swing.JDialog {

    /**
     * Creates new form GetWebPageDialog
     */
    public GetWebPageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.WebPage1.setText(Main.configuration.getPreference("URL1"));
        this.WebPage2.setText(Main.configuration.getPreference("URL2"));
        this.WebPage3.setText(Main.configuration.getPreference("URL3"));
        this.WebPage4.setText(Main.configuration.getPreference("URL4"));
        this.WebPage5.setText(Main.configuration.getPreference("URL5"));
        this.WebPage6.setText(Main.configuration.getPreference("URL6"));
        this.URL1b.setText(Main.configuration.getPreference("URL1B"));
        this.URL2b.setText(Main.configuration.getPreference("URL2B"));
        this.URL3b.setText(Main.configuration.getPreference("URL3B"));
        this.URL4b.setText(Main.configuration.getPreference("URL4B"));
        this.URL5b.setText(Main.configuration.getPreference("URL5B"));
        this.URL6b.setText(Main.configuration.getPreference("URL6B"));
        this.URL1e.setText(Main.configuration.getPreference("URL1E"));
        this.URL2e.setText(Main.configuration.getPreference("URL2E"));
        this.URL3e.setText(Main.configuration.getPreference("URL3E"));
        this.URL4e.setText(Main.configuration.getPreference("URL4E"));
        this.URL5e.setText(Main.configuration.getPreference("URL5E"));
        this.URL6e.setText(Main.configuration.getPreference("URL6E"));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setVisible(true);
    }

    
    private void saveAllData() {
        String path = Main.homePath + Main.dirPrefix;
        Config c = new Config(path);
        c.SetWebPages(WebPage1.getText(), WebPage2.getText(), WebPage3.getText(), WebPage4.getText(), WebPage5.getText(), WebPage6.getText());
        c.SetWebPagesB(URL1b.getText(), URL2b.getText(), URL3b.getText(), URL4b.getText(), URL5b.getText(), URL6b.getText());
        c.SetWebPagesE(URL1e.getText(), URL2e.getText(), URL3e.getText(), URL4e.getText(), URL5e.getText(), URL6e.getText());
        c.saveURLs();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        WebPage1 = new javax.swing.JTextField();
        WebPage2 = new javax.swing.JTextField();
        WebPage3 = new javax.swing.JTextField();
        WebPage4 = new javax.swing.JTextField();
        WebPage5 = new javax.swing.JTextField();
        WebPage6 = new javax.swing.JTextField();
        WebButton1 = new javax.swing.JButton();
        WebButton2 = new javax.swing.JButton();
        WebButton3 = new javax.swing.JButton();
        WebButton4 = new javax.swing.JButton();
        WebButton5 = new javax.swing.JButton();
        WebButton6 = new javax.swing.JButton();
        Weblabel = new javax.swing.JLabel();
        URL1b = new javax.swing.JTextField();
        URL1e = new javax.swing.JTextField();
        URL2b = new javax.swing.JTextField();
        URL3b = new javax.swing.JTextField();
        URL4b = new javax.swing.JTextField();
        URL5b = new javax.swing.JTextField();
        URL6b = new javax.swing.JTextField();
        URL2e = new javax.swing.JTextField();
        URL3e = new javax.swing.JTextField();
        URL4e = new javax.swing.JTextField();
        URL5e = new javax.swing.JTextField();
        URL6e = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        WebTestURL1Button = new javax.swing.JButton();
        WebOKButton = new javax.swing.JButton();
        GetWebCancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(590, 310));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        WebPage1.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage1.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 4);
        jPanel1.add(WebPage1, gridBagConstraints);

        WebPage2.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage2.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 4);
        jPanel1.add(WebPage2, gridBagConstraints);

        WebPage3.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage3.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 4);
        jPanel1.add(WebPage3, gridBagConstraints);

        WebPage4.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage4.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 4);
        jPanel1.add(WebPage4, gridBagConstraints);

        WebPage5.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage5.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 4);
        jPanel1.add(WebPage5, gridBagConstraints);

        WebPage6.setMinimumSize(new java.awt.Dimension(250, 27));
        WebPage6.setPreferredSize(new java.awt.Dimension(250, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 5, 4);
        jPanel1.add(WebPage6, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("javapskmail/Bundle"); // NOI18N
        WebButton1.setText(bundle.getString("GetWebPageDialog.WebButton1.text")); // NOI18N
        WebButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
        jPanel1.add(WebButton1, gridBagConstraints);

        WebButton2.setText(bundle.getString("GetWebPageDialog.WebButton2.text")); // NOI18N
        WebButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
        jPanel1.add(WebButton2, gridBagConstraints);

        WebButton3.setText(bundle.getString("GetWebPageDialog.WebButton3.text")); // NOI18N
        WebButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
        jPanel1.add(WebButton3, gridBagConstraints);

        WebButton4.setText(bundle.getString("GetWebPageDialog.WebButton4.text")); // NOI18N
        WebButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
        jPanel1.add(WebButton4, gridBagConstraints);

        WebButton5.setText(bundle.getString("GetWebPageDialog.WebButton5.text")); // NOI18N
        WebButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
        jPanel1.add(WebButton5, gridBagConstraints);

        WebButton6.setText(bundle.getString("GetWebPageDialog.WebButton6.text")); // NOI18N
        WebButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 5, 0);
        jPanel1.add(WebButton6, gridBagConstraints);

        Weblabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        Weblabel.setText(bundle.getString("GetWebPageDialog.Weblabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 0, 0);
        jPanel1.add(Weblabel, gridBagConstraints);

        URL1b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL1b.setName(""); // NOI18N
        URL1b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(URL1b, gridBagConstraints);

        URL1e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL1e.setName(""); // NOI18N
        URL1e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(URL1e, gridBagConstraints);

        URL2b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL2b.setName(""); // NOI18N
        URL2b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(URL2b, gridBagConstraints);

        URL3b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL3b.setName(""); // NOI18N
        URL3b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(URL3b, gridBagConstraints);

        URL4b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL4b.setName(""); // NOI18N
        URL4b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(URL4b, gridBagConstraints);

        URL5b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL5b.setName(""); // NOI18N
        URL5b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(URL5b, gridBagConstraints);

        URL6b.setMinimumSize(new java.awt.Dimension(4, 25));
        URL6b.setName(""); // NOI18N
        URL6b.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 5, 3);
        jPanel1.add(URL6b, gridBagConstraints);

        URL2e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL2e.setName(""); // NOI18N
        URL2e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(URL2e, gridBagConstraints);

        URL3e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL3e.setName(""); // NOI18N
        URL3e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(URL3e, gridBagConstraints);

        URL4e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL4e.setName(""); // NOI18N
        URL4e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(URL4e, gridBagConstraints);

        URL5e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL5e.setName(""); // NOI18N
        URL5e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(URL5e, gridBagConstraints);

        URL6e.setMinimumSize(new java.awt.Dimension(4, 25));
        URL6e.setName(""); // NOI18N
        URL6e.setPreferredSize(new java.awt.Dimension(4, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 5, 7);
        jPanel1.add(URL6e, gridBagConstraints);

        jLabel2.setText(bundle.getString("GetWebPageDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 46;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 3);
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel3.setText(bundle.getString("GetWebPageDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 75;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 8, 0, 7);
        jPanel1.add(jLabel3, gridBagConstraints);

        pnlButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        WebTestURL1Button.setText(bundle.getString("GetWebPageDialog.text")); // NOI18N
        WebTestURL1Button.setMaximumSize(new java.awt.Dimension(97, 30));
        WebTestURL1Button.setMinimumSize(new java.awt.Dimension(97, 30));
        WebTestURL1Button.setName(""); // NOI18N
        WebTestURL1Button.setPreferredSize(new java.awt.Dimension(97, 30));
        WebTestURL1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebTestURL1ButtonActionPerformed(evt);
            }
        });
        pnlButtons.add(WebTestURL1Button);

        WebOKButton.setText(bundle.getString("GetWebPageDialog.WebOKButton.text")); // NOI18N
        WebOKButton.setMinimumSize(new java.awt.Dimension(80, 30));
        WebOKButton.setPreferredSize(new java.awt.Dimension(80, 30));
        WebOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WebOKButtonActionPerformed(evt);
            }
        });
        pnlButtons.add(WebOKButton);

        GetWebCancelButton.setText(bundle.getString("GetWebPageDialog.GetWebCancelButton.text")); // NOI18N
        GetWebCancelButton.setMinimumSize(new java.awt.Dimension(80, 30));
        GetWebCancelButton.setPreferredSize(new java.awt.Dimension(80, 30));
        GetWebCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GetWebCancelButtonActionPerformed(evt);
            }
        });
        pnlButtons.add(GetWebCancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 43;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 10, 0);
        jPanel1.add(pnlButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GetWebCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GetWebCancelButtonActionPerformed

        this.setVisible(false);
    }//GEN-LAST:event_GetWebCancelButtonActionPerformed

    private void WebOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebOKButtonActionPerformed

        saveAllData();
        this.setVisible(false);
    }//GEN-LAST:event_WebOKButtonActionPerformed

    private void WebButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton1ActionPerformed

        if (Main.connected) {
            if (this.WebPage1.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL1b.getText().equals("") | URL1e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage1.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage1.getText() + " begin:" + URL1b.getText() + " end:" + URL1e.getText() + "\n";
                    }
                } else {
                    if (URL1b.getText().equals("") | URL1e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage1.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage1.getText() + " begin:" + URL1b.getText() + " end:" + URL1e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton1ActionPerformed

    private void WebButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton2ActionPerformed

        if (Main.connected) {
            if (this.WebPage2.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL2b.getText().equals("") | URL2e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage2.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage2.getText() + " begin:" + URL2b.getText() + " end:" + URL2e.getText() + "\n";
                    }
                } else {
                    if (URL2b.getText().equals("") | URL2e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage2.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage2.getText() + " begin:" + URL2b.getText() + " end:" + URL2e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton2ActionPerformed

    private void WebButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton3ActionPerformed

        if (Main.connected) {
            if (this.WebPage3.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL3b.getText().equals("") | URL3e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage3.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage3.getText() + " begin:" + URL3b.getText() + " end:" + URL3e.getText() + "\n";
                    }
                } else {
                    if (URL3b.getText().equals("") | URL3e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage3.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage3.getText() + " begin:" + URL3b.getText() + " end:" + URL3e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton3ActionPerformed

    private void WebButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton4ActionPerformed

        if (Main.connected) {
            if (this.WebPage4.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL4b.getText().equals("") | URL4e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage4.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage4.getText() + " begin:" + URL4b.getText() + " end:" + URL4e.getText() + "\n";
                    }
                } else {
                    if (URL4b.getText().equals("") | URL4e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage4.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage4.getText() + " begin:" + URL4b.getText() + " end:" + URL4e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton4ActionPerformed

    private void WebButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton5ActionPerformed

        if (Main.connected) {
            if (this.WebPage5.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL5b.getText().equals("") | URL5e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage5.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage5.getText() + " begin:" + URL5b.getText() + " end:" + URL5e.getText() + "\n";
                    }
                } else {
                    if (URL5b.getText().equals("") | URL5e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage5.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage5.getText() + " begin:" + URL5b.getText() + " end:" + URL5e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton5ActionPerformed

    private void WebButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebButton6ActionPerformed

        if (Main.connected) {
            if (this.WebPage6.getText().length() > 0) {
                if (Main.compressedmail) {
                    if (URL6b.getText().equals("") | URL6e.getText().equals("")) {
                        Main.txText += "~TGETZIP " + this.WebPage6.getText() + "\n";
                    } else {
                        Main.txText += "~TGETZIP " + this.WebPage6.getText() + " begin:" + URL6b.getText() + " end:" + URL6e.getText() + "\n";
                    }
                } else {
                    if (URL6b.getText().equals("") | URL6e.getText().equals("")) {
                        Main.txText += "~TGET " + this.WebPage6.getText() + "\n";
                    } else {
                        Main.txText += "~TGET " + this.WebPage6.getText() + " begin:" + URL6b.getText() + " end:" + URL6e.getText() + "\n";
                    }
                }
                saveAllData();
            }
        } else {
            Main.q.Message("You need to connect first...", 5);
        }
        this.setVisible(false);
    }//GEN-LAST:event_WebButton6ActionPerformed

    private void WebTestURL1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WebTestURL1ButtonActionPerformed
        //e.g.: "~TGET www.bom.gov.au/nsw/forecasts/centralwestslopes.shtml begin:Forecast issued at end:The next routine forecast"
        String url1 = this.WebPage1.getText();
        if (url1.length() > 0) {
            String webText = "";
            if (URL1b.getText().equals("") | URL1e.getText().equals("")) {
                webText = ServerMail.readWebPage(url1, "", false);
            } else {
                webText = ServerMail.readWebPage(url1, " begin:" + URL1b.getText() + " end:" + URL1e.getText(), false);
                //test: webText = serverMail.readRawWebPage(url1, " begin:" + URL1b.getText() + " end:" + URL1e.getText(), false);
            }
            if (webText.length() > 0) {
                Main.mainui.appendMainWindow("\n-----------------------------\n" + webText + "\n-----------------------------\n");
            }
        }
    }//GEN-LAST:event_WebTestURL1ButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GetWebPageDialog dialog = new GetWebPageDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton GetWebCancelButton;
    private javax.swing.JTextField URL1b;
    private javax.swing.JTextField URL1e;
    private javax.swing.JTextField URL2b;
    private javax.swing.JTextField URL2e;
    private javax.swing.JTextField URL3b;
    private javax.swing.JTextField URL3e;
    private javax.swing.JTextField URL4b;
    private javax.swing.JTextField URL4e;
    private javax.swing.JTextField URL5b;
    private javax.swing.JTextField URL5e;
    private javax.swing.JTextField URL6b;
    private javax.swing.JTextField URL6e;
    private javax.swing.JButton WebButton1;
    private javax.swing.JButton WebButton2;
    private javax.swing.JButton WebButton3;
    private javax.swing.JButton WebButton4;
    private javax.swing.JButton WebButton5;
    private javax.swing.JButton WebButton6;
    private javax.swing.JButton WebOKButton;
    private javax.swing.JTextField WebPage1;
    private javax.swing.JTextField WebPage2;
    private javax.swing.JTextField WebPage3;
    private javax.swing.JTextField WebPage4;
    private javax.swing.JTextField WebPage5;
    private javax.swing.JTextField WebPage6;
    private javax.swing.JButton WebTestURL1Button;
    private javax.swing.JLabel Weblabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlButtons;
    // End of variables declaration//GEN-END:variables

}
