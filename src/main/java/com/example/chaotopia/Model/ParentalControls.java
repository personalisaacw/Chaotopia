package com.example.chaotopia.Model;

/**
 * Entity that allows the parent or admin to set restrictions on
 * the game files.
 * <br><br>
 * Entry into the parental controls menu is secured via a password with
 * {@link #authenticate}. The parent can revive a Chao here with
 * {@link #reviveChao}.
 *
 * @version 1.0.0
 * @author Justin Rowbotham
 */
public final class ParentalControls {
    /** The password securing entry into the parental controls menu. */
    private static final String PASSWORD = "abc123";

    /**
     * Constructor method for parental controls.
     * Cannot be called due to the class being static.
     */
    private ParentalControls() {}

    /**
     * Method determining if an input password matches the one for the menu.
     * @param input the password to be checked
     * @return True if the password strings match, false otherwise.
     */
    public static boolean authenticate(String input) {
        return PASSWORD.equals(input);
    }

    /**
     * Method that revives the Chao associated with the passed game file.
     * <p>
     * If the Chao is still alive, the method just maxes out its stats.
     *
     * @param gameFile the game file containing the Chao to be revived
     */
    public static void reviveChao(GameFile gameFile) {
        Chao chao = gameFile.getChao();
        chao.getStatus().updateStats(100,100,100,100);
        gameFile.setChao(chao);
    }
}