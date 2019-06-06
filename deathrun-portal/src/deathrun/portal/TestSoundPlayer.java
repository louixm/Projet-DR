/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deathrun.portal;

/**
 *
 * @author guillaume.laurent
 */
public class TestSoundPlayer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {

        // La classe SoundPlayer utilise la bibliotheque JLayer. 
        //
        // Pour utiliser cette classe dans un nouveau projet, il faut :
        //   1. Recopier jlayer.jar dans le dossier de votre projet
        //   2. Cliquer droit sur "Librairies", choisir "add JAR/folder" 
        //      et selectionner la copie 
        
        SoundPlayer sound = new SoundPlayer("victorySound.mp3", false);
        sound.play();

    }

}
