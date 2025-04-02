package com.example.chaotopia.Application;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;

/**Background music that plays during all screens except Gameplay.
 * @author Rosaline Scully
 * */

public class BackgroundMusic {

    /** Initialize a media play to hold the music */
    private static MediaPlayer menuMusicPlayer;
    /**A string path to where the background music is*/
    private static final String MENU_MUSIC_PATH = "/com/example/chaotopia/Assets/Sounds/background.mp3"; // <-- ADJUST PATH AND FILENAME

    /**
     * Starts the looping main menu background music if it's not already playing.
     */
    public static void startMenuMusic() {
        // Only start if it's not already playing
        if (menuMusicPlayer == null || menuMusicPlayer.getStatus() == MediaPlayer.Status.STOPPED || menuMusicPlayer.getStatus() == MediaPlayer.Status.UNKNOWN) {
            try {
                URL resourceUrl = BackgroundMusic.class.getResource(MENU_MUSIC_PATH);
                if (resourceUrl != null) {
                    Media media = new Media(resourceUrl.toExternalForm());
                    menuMusicPlayer = new MediaPlayer(media);
                    menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
                    menuMusicPlayer.setVolume(0.3); // Adjust volume as needed (0.0 to 1.0)

                    // Add error handling for the player itself
                    menuMusicPlayer.setOnError(() -> {
                        System.err.println("Menu Music Player Error: " + menuMusicPlayer.getError());
                        stopMenuMusic(); // Stop on error
                    });

                    menuMusicPlayer.play();
                    System.out.println("Menu background music started.");
                } else {
                    System.err.println("Could not find menu music resource: " + MENU_MUSIC_PATH);
                }
            } catch (Exception e) {
                System.err.println("Error creating MediaPlayer for menu music: " + e.getMessage());
                e.printStackTrace();
                menuMusicPlayer = null; // Ensure player is null on error
            }
        } else {
            System.out.println("Menu background music already playing or in a non-stopped state.");
        }
    }

    /**
     * Stops the main menu background music if it is currently playing.
     */
    public static void stopMenuMusic() {
        if (menuMusicPlayer != null && (menuMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING || menuMusicPlayer.getStatus() == MediaPlayer.Status.PAUSED)) {
            try {
                menuMusicPlayer.stop();
                System.out.println("Menu background music stopped.");
                menuMusicPlayer.dispose();
                menuMusicPlayer = null;
            } catch (Exception e) {
                System.err.println("Error stopping menu music player: " + e.getMessage());
            }

        } else {
            System.out.println("Menu music player was not playing or was null.");
        }
    }

    /**
     * Checks if the menu music is currently playing.
     * @return true if the menu music player exists and is playing, false otherwise.
     */
    public static boolean isMenuMusicPlaying() {
        return menuMusicPlayer != null && menuMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}