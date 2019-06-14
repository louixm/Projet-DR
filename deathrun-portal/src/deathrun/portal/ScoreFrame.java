/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 *
 * @author bsoares
 */
public class ScoreFrame extends javax.swing.JDialog {

    /**
     * Creates new form ScoreFrame
     */
    
    //declarations
    private ArrayList<javax.swing.JPanel> progressBars;
    private Game game;
    
    
        
    public ScoreFrame(Game game, java.awt.Frame parent, boolean modal) throws InterruptedException {
        super(parent, modal);
        this.game = game;
        
        setMaximumSize(new java.awt.Dimension(500, 350));
        setMinimumSize(new java.awt.Dimension(500, 350));
        setResizable(false);
        getContentPane().setLayout(null);
    
        initProgressBars(game);
        initComponents();
        
        
        try {
            background.setIcon(new javax.swing.ImageIcon(ImageIO.read(new File("./images/fond_4.png"))));
        } catch (IOException ex) {
            Logger.getLogger(StartMenu.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("pas d'image");
        }
        
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        //TODO: initialise background manually after the progress bars.
//        try {
//            background.setIcon(new javax.swing.ImageIcon(ImageIO.read(new File("./images/fond_4.png"))));
//        } catch (IOException ex) {
//            Logger.getLogger(StartMenu.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("pas d'image");
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        background = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(500, 350));
        setMinimumSize(new java.awt.Dimension(500, 350));
        setResizable(false);
        getContentPane().setLayout(null);

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(207, 270, 60, 23);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(440, 40, 20, 190);
        getContentPane().add(background);
        background.setBounds(0, 0, 500, 350);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initProgressBars(Game game) throws InterruptedException{
        
//        progressBars = new ArrayList<javax.swing.JPanel>();
        Thread.sleep(100);
        if (game != null) {
            int i = 0;
            for (Player player : game.players){
                if (!player.disconnected){
                    i++;
                    ArrayList<JPanel> progress = new ArrayList<JPanel>();
                    int nextLocation = 140;
                    javax.swing.JLabel playerName = new javax.swing.JLabel();
                    playerName.setFont(new java.awt.Font("Trebuchet MS", 1, 16));
                    Color playerColor = player.getPlayerColor();
                    playerName.setForeground(playerColor);
                    playerName.setText(player.name);
                    getContentPane().add(playerName);
                    playerName.setBounds(15, 50*i, 120, 18);
                    
                    if (game.sync != null) {

                        try {
                            int cumulAmount = 0;
                            PreparedStatement req = game.sync.srv.prepareStatement("SELECT * FROM scores WHERE player = ?");
                            req.setInt(1, player.db_id);
                            ResultSet r = req.executeQuery();
                            while(r.next()){
                                int amount = r.getInt("amount");
                                int type = r.getInt("type");
                                JPanel score = new JPanel();
                                int[] rgb1 = {playerColor.getRed(),playerColor.getGreen(), playerColor.getBlue()};
                                for (int j = 0; j < 3; j++){
                                    rgb1[j] += 80;
                                    if (rgb1[j] > 255) rgb1[j] = 255;
                                }
                                Color lighterPlayerColor = new Color(rgb1[0],rgb1[1],rgb1[2]);
                                int[] rgb2 = {playerColor.getRed(),playerColor.getGreen(), playerColor.getBlue()};
                                for (int j = 0; j < 3; j++){
                                    rgb2[j] -= 80;
                                    if (rgb2[j] < 0) rgb2[j] = 0;
                                }
                                Color darkerPlayerColor = new Color(rgb2[0],rgb2[1],rgb2[2]);
                                if (type == 0) score.setBackground(player.getPlayerColor());
//                                else if (type == 1) score.setBackground(player.getPlayerColor().darker().darker());
//                                else score.setBackground(player.getPlayerColor().brighter().brighter());
                                else if (type == 1) score.setBackground(darkerPlayerColor);
                                else score.setBackground(lighterPlayerColor);
                                getContentPane().add(score);
                                score.setBounds(nextLocation, 50*i, amount*3, 20);
                                nextLocation += amount*3;
                                cumulAmount += amount;
                            }
                            if (cumulAmount >= 100){
                                javax.swing.JLabel victory = new javax.swing.JLabel();
                                victory.setFont(new java.awt.Font("Trebuchet MS", 1, 26));
                                victory.setForeground(Color.WHITE);
                                victory.setText(player.name + " won!");
                                getContentPane().add(victory);
                                victory.setBounds(100, 125, 350, 22);
                                
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(ScoreFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
                
        
//                javax.swing.JPanel progressBar = new javax.swing.JPanel();
//                progressBar.setBackground(player.getPlayerColor());
//        //        UIManager.put("progressBar.foreground", Color.YELLOW);
////                progressBar.setValue(100); //score
////                progressBar.setBorderPainted(false);
//                getContentPane().add(progressBar);
//                progressBar.setBounds(140, 50*(1+progressBars.size()), 20, 20);
//
//                progressBars.add(progressBar);
            
        pack();
    }
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
//        game.switchToEditionMode(true);
        game.scoresClosed = true;
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ScoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ScoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ScoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ScoreFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ScoreFrame scoreframe = new ScoreFrame(null, new javax.swing.JFrame(), false);
                    scoreframe.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
                        }
                    });
//                scoreframe.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                    scoreframe.setVisible(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ScoreFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton jButton2;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    
    
}
