/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

import java.awt.Image;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

/**
 *
 * @author mveyratd
 */
public class SelectionBloc extends javax.swing.JDialog {

    public int blocAPoser = -1; //Entier permettant de savoir quel bloc a été choisi par le joueur
    public int[] objectsToPlace;
    public PObject objectToPlace;
    public Player player;
    public Game game;
    public boolean chosenObject = false;
    public boolean placedObject = false;
    
    /**
     * Creates new form SelectionBloc
     */
    public SelectionBloc(Game game, Player player, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.game = game;
        this.player = player;
        
        // Affichage des images des blocs sur les boutons après redimensionnement
        jButton1.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (15).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
        jButton2.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Saw.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
        jButton3.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/laser2.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
//        jButton4.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/punch0_0.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
//        jButton5.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/portail.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
//        jButton6.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Barrel (1).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
//        jButton7.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Barrel (1).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
//        jButton8.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Barrel (1).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(450, 250));
        getContentPane().setLayout(null);

        jLabel1.setText("Mode édition : choisissez votre bloc ...");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(120, 30, 220, 20);

        buttonGroup1.add(jButton1);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(40, 80, 80, 80);

        buttonGroup1.add(jButton2);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(160, 80, 80, 80);

        buttonGroup1.add(jButton3);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(280, 80, 80, 80);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
//            this.blocAPoser = objectsToPlace[0];
            createObjectToPlace(objectsToPlace[0]);
            this.chosenObject = true;
        } catch (IOException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
//            this.blocAPoser = objectsToPlace[1];
            createObjectToPlace(objectsToPlace[1]);
            this.chosenObject = true;
        } catch (IOException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
//            this.blocAPoser = objectsToPlace[2];
            createObjectToPlace(objectsToPlace[2]);
            this.chosenObject = true;
        } catch (IOException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(SelectionBloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(SelectionBloc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectionBloc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectionBloc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectionBloc.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelectionBloc dialog = new SelectionBloc(null, null, new javax.swing.JFrame(), false);
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    public void setIcons() {
        setIconOnButton(jButton1, objectsToPlace[0]);
        setIconOnButton(jButton2, objectsToPlace[1]);
        setIconOnButton(jButton3, objectsToPlace[2]);
    }

    private void setIconOnButton(JButton jButton, int i) {
        switch(i){
            case 0: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (5).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 1: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (16).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 2: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (13).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 3: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (17).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 4: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Barrel_1_3.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 5: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Barrel_2_4.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 6: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/patterns/Tile (18).png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 7: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Tile_10.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 8: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Tile_11.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 9: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Tile_12.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 10: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/bomb.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 11: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Saw.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 12: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/laser_centre.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 13: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/punch0_0.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 14: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Spikes_2.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
            case 15: jButton.setIcon(new javax.swing.ImageIcon(new javax.swing.ImageIcon("./images/Explosive_0.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT))); break;
        }
    }

    private void createObjectToPlace(int i) throws IOException, SQLException {
        int num_players = game.players.size();
        int new_id = game.next_id + (num_players - game.next_id % num_players) + game.players.indexOf(player);
        switch (i) {
            //Tests pour savoir quel bloc a été choisi dans la fenetre SelectBloc
            case 0:case 1:case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9: //Plateforme
                this.objectToPlace = new Platform(this.game, new Vec2(-50,-50), Platform.standardBoxes[i], i, new_id); break;
            case 10: //Bombe
                this.objectToPlace = new Bomb(this.game, new Vec2(-50,-50), new_id); break;
            case 11: //Scie circulaire
                this.objectToPlace = new Saw(this.game, new Vec2(-50,-50), new_id);break;
            case 12: //Laser                       
                this.objectToPlace = new Laser(this.game, new Vec2(-50,-50), 0, new_id);break;
            case 13: //Punch  
                this.objectToPlace = new Punch(this.game, 0, new Vec2(-50,-50), new_id);break;
            case 14: //Spikes
                this.objectToPlace = new Spikes(this.game, 0, new Vec2(-50,-50), new_id);break;
            case 15: //Mine
                this.objectToPlace = new Explosive(this.game, new Vec2(-50,-50), new_id);break;
        }
        if (this.objectToPlace != null){
            this.objectToPlace.last_sync = game.sync.latest++;
            this.objectToPlace.syncSet(game.sync, true);
        }
    }
}
