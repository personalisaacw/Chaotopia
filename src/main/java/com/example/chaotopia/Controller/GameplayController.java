package com.example.chaotopia.Controller;

// Added Model import assuming it contains necessary classes like Chao, State, etc.
import com.example.chaotopia.Model.*;
import com.example.chaotopia.Model.GameFile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Controller for the main gameplay screen (Gameplay.fxml).
 * Manages game logic, UI updates, animations, and Chao state.
 * Merges responsibilities previously handled by GameplayAnimationController.
 */
public class GameplayController extends BaseController implements Initializable {

    // --- FXML Injections from Gameplay.fxml ---
    @FXML private BorderPane mainContainer;
    @FXML private StackPane centerStackPane; // Container for fruit, messages, game over

    // Top Bar
    @FXML private Button backButton;
    @FXML private Label timeLabel;
    @FXML private Button saveButton;

    // Left Bar Buttons
    @FXML private Button playButton;
    @FXML private Button sleepButton;
    @FXML private Button exerciseButton;
    @FXML private Button vetButton;
    @FXML private Button petButton;
    @FXML private Button bonkButton;

    // Bottom Bar (Profile)
    @FXML private Label nameLabel;
    @FXML private ImageView chaoImageView; // Chao sprite display
    @FXML private ProgressBar healthBar;
    @FXML private ProgressBar fullnessBar;
    @FXML private ProgressBar happinessBar;
    @FXML private ProgressBar sleepBar;
    @FXML private Label scoreLabel;

    // Right Bar (Inventory) - Assuming buttons for direct use
    @FXML private VBox inventoryButtonContainer; // Container for easier enable/disable
    @FXML private Button redFruitButton;
    @FXML private Button blueFruitButton;
    @FXML private Button greenFruitButton;
    @FXML private Button heroFruitButton;
    @FXML private Button darkFruitButton;
    @FXML private Button trumpetButton;
    @FXML private Button duckButton;
    @FXML private Button tvButton;

    //Count inventory items
    @FXML private Label redFruitCountLabel;
    @FXML private Label blueFruitCountLabel;
    @FXML private Label greenFruitCountLabel;
    @FXML private Label heroFruitCountLabel;
    @FXML private Label darkFruitCountLabel;
    @FXML private Label trumpetCountLabel;
    @FXML private Label duckCountLabel;
    @FXML private Label tvCountLabel;

    // Center Elements
    @FXML private ImageView fruitImageView; // For fruit feeding animation
    @FXML private ImageView profileChaoImageView; //For chao profile picture
    @FXML private Label messageLabel; // For status messages
    @FXML private Label healthLabel; // Stat number labels
    @FXML private Label fullnessLabel;
    @FXML private Label happinessLabel;
    @FXML private Label sleepLabel;

    // --- Game Logic Instance Variables ---
    private Chao chao;
    private Inventory inventory;
    private Score score;
    private ChaoAnimation chaoAnimation;      // Handles Chao sprite animation
    private FruitAnimation fruitAnimation;    // Handles fruit sprite animation
    private record InventoryItemUI(Button button, Label countLabel) {} // Helper record
    private Map<String, InventoryItemUI> inventoryUIMap;
    private List<Button> inventoryButtonsOrdered;
    private ChaoType currentSessionBaseType = null;
    private GameFile game = null;

    // --- Timelines ---
    private Timeline statDecayTimeline;
    private Timeline stateMonitorTimeline;
    private Timeline sleepIncreaseTimeline;
    private Timeline tempAnimationTimer;    // For temporary states like HAPPY
    private Timeline messageTimeline;       // For hiding messages
    private Timeline evolutionTimeline;     // For evolution sequences
    private Timeline clockTimeline;
    private Timeline fruitSpawnTimeline;
    private Timeline giftSpawnTimeline;
    private Timeline giftDisplayTimeline;

    // --- Media Players ---
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer buttonClickPlayer;
    private MediaPlayer eatingPlayer;
    private MediaPlayer happyPlayer;
    private MediaPlayer angryPlayer;
    private MediaPlayer cryingPlayer;
    private MediaPlayer sleepingSoundPlayer;
    private MediaPlayer bonkSoundPlayer;
    private MediaPlayer evolutionSoundPlayer;

    // --- State Management ---
    private boolean isSleeping = false;
    private State previousState = State.NORMAL;
    private Node gameOverOverlay = null; // Reference to the game over screen
    private final Random random = new Random(); // Final because initialized once
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private long lastUserActionTime = 0;
    private static final long SOUND_LOOP_COOLDOWN_MS = 2000;
    private Time time;

    // --- Item Spawning ---
    private List<String> fruitItemNames;
    private List<String> giftItemNames;

    // --- Initialization ---
    private final IntegerProperty slotIndex = new SimpleIntegerProperty(-1);

    public GameplayController() {
        slotIndex.addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() != -1) {
                initializeGame(newVal.intValue());
            }
        });
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex.set(slotIndex);
    }

    private void initializeGame(int slotIndex) {
        try {
            game = new GameFile(slotIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (game.getChao() != null) {
            chao = game.getChao();
            loadOrCreateChao(chao);
            System.out.println("\nChao: " + chao.getName());
            System.out.println("- Type: " + chao.getType());
            System.out.println("- State: " + chao.getState());
            System.out.println("- Alignment: " + chao.getAlignment());
            System.out.println("- Status: " + chao.getStatus().getCurrStats());
        }

        if (game.getInventory() != null) {
            inventory = game.getInventory();
            System.out.println("\nInventory:");
            for (Map.Entry<String, Integer> entry : game.getInventory().getItems().entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
        }

        if (game.getScore() != null) {
            System.out.println("\nScore: " + game.getScore().getScore());
            score = game.getScore();
        }

        loadSounds();
        inventoryUIMap = new HashMap<>();
        inventoryButtonsOrdered = new ArrayList<>();
        initializeItemLists();

        if (fruitImageView != null) {
            fruitAnimation = new FruitAnimation(fruitImageView, FruitType.RED);
            fruitImageView.setVisible(false);
        } else {
            System.err.println("FXML Warning: fruitImageView is null.");
        }

        initializeInventoryUIMap();
        populateOrderedInventoryButtons();
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }

        updateScoreUI(score.getScore());
        updateNameLabel();
        updateStatusBars();
        updateProfileChaoImage();
        updateInventoryDisplay();

        //Setup Time
        setupTimelines();
        startTimelines();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources){

        Platform.runLater(() -> {
            Scene scene = mainContainer.getScene();
            if (scene != null) {
                setupKeybindings(scene);
            } else if (mainContainer != null) {
                // Fallback: Listen for the scene property change
                mainContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setupKeybindings(newScene);
                    }
                });
            } else {
                System.err.println("Cannot set up keybindings: mainContainer is null.");
            }
        });
    }

    /**
     * Checks if the game is being started fresh (e.g., no save loaded).
     * Currently uses whether the inventory is empty as a simple check.
     *
     * @return true if it's considered a new game condition, false otherwise.
     */
    private boolean isNewGameCondition() {
        // Example: Check if a loaded save file exists, or check if 'chao' is newly created vs loaded
        // For now, let's check if inventory is empty as a proxy
        return inventory.isEmpty(); // Assuming Inventory has an isEmpty() method
    }

    /**
     * Populates the ordered list of inventory buttons for keyboard shortcut mapping (1-8).
     * The order in this list determines which button corresponds to which number key.
     */
    private void populateOrderedInventoryButtons() {
        // Add buttons in the exact order you want 1-8 to map to
        inventoryButtonsOrdered.clear(); // Ensure list is empty before adding
        inventoryButtonsOrdered.add(redFruitButton);   // 1
        inventoryButtonsOrdered.add(blueFruitButton);  // 2
        inventoryButtonsOrdered.add(greenFruitButton); // 3
        inventoryButtonsOrdered.add(heroFruitButton);  // 4
        inventoryButtonsOrdered.add(darkFruitButton);  // 5
        inventoryButtonsOrdered.add(trumpetButton);    // 6
        inventoryButtonsOrdered.add(duckButton);       // 7
        inventoryButtonsOrdered.add(tvButton);         // 8
        // Make sure you have exactly 8 buttons here if you map 1-8
    }

    /**
     * Loads all sound effects and background music into MediaPlayer objects.
     * Sets looping properties and initial volume for background music.
     */
    private void loadSounds() {
        try {
            // Background Music (Looping)
            backgroundMusicPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/gamebackground.mp3");
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusicPlayer.setVolume(0.2);
            }

            // One-Shot Sounds
            buttonClickPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/buttonclick.mp3");
            eatingPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/eating.wav");
            happyPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/happy.wav");
            bonkSoundPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/crying.wav"); // Reusing crying sound
            evolutionSoundPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/evolve.mp3");

            // Looping sounds
            sleepingSoundPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/sleeping.wav");
            if (sleepingSoundPlayer != null) {
                sleepingSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            angryPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/angry.wav");
            if (angryPlayer != null) {
                angryPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

            cryingPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/crying.wav"); // Separate instance for hungry loop
            if (cryingPlayer != null) {
                cryingPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }

        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a MediaPlayer instance from a given resource path string.
     * Handles potential errors like missing resources.
     *
     * @param resourcePath The path to the sound file within the application resources (e.g., "/com/example/.../sound.mp3").
     * @return A configured MediaPlayer instance, or null if the resource is not found or an error occurs.
     */
    private MediaPlayer createMediaPlayer(String resourcePath) {
        MediaPlayer player = null;
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            if (resourceUrl != null) {
                Media media = new Media(resourceUrl.toExternalForm());
                player = new MediaPlayer(media);
            } else {
                System.err.println("Could not find sound resource: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error creating MediaPlayer for " + resourcePath + ": " + e.getMessage());
        }
        return player;
    }

    /**
     * Helper method to play a one-shot sound effect from the beginning.
     */
    private void playSoundEffect(MediaPlayer player) {
        if (player != null) {
            Platform.runLater(() -> {
                player.stop();
                player.seek(Duration.ZERO); // Rewind to start
                player.play();
            });
        } else {
            // Changed to a less verbose warning
            // System.err.println("Attempted to play a null sound effect.");
        }
    }

    /**
     * Attaches the key press event handler (handleKeyPress) to the provided Scene.
     * This enables keyboard shortcuts for game actions.
     *
     * @param scene The Scene to listen for key events on.
     */
    private void setupKeybindings(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
        System.out.println("Keybindings setup on scene."); // Debug message
    }

    /**
     * Handles key press events detected on the main game scene.
     * Triggers corresponding game actions (system, inventory, Chao interactions) based on the key code.
     * Consumes the event if an action is triggered.
     *
     * @param event The KeyEvent object containing information about the key press.
     */
    private void handleKeyPress(KeyEvent event) {
        // System actions
        if (event.getCode() == KeyCode.S) {
            playSoundEffect(buttonClickPlayer);
            saveGame();
            event.consume();
            return;
        }
        if (event.getCode() == KeyCode.M) {
            playSoundEffect(buttonClickPlayer);
            ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());
            goToMenu(actionEvent);
            event.consume();
            return;
        }

        // Inventory actions
        int buttonIndex = -1;
        switch (event.getCode()) {
            case DIGIT1: case NUMPAD1: buttonIndex = 0; break;
            case DIGIT2: case NUMPAD2: buttonIndex = 1; break;
            case DIGIT3: case NUMPAD3: buttonIndex = 2; break;
            case DIGIT4: case NUMPAD4: buttonIndex = 3; break;
            case DIGIT5: case NUMPAD5: buttonIndex = 4; break;
            case DIGIT6: case NUMPAD6: buttonIndex = 5; break;
            case DIGIT7: case NUMPAD7: buttonIndex = 6; break;
            case DIGIT8: case NUMPAD8: buttonIndex = 7; break;
        }

        if (buttonIndex != -1) {
            if (buttonIndex < inventoryButtonsOrdered.size()) {
                Button targetButton = inventoryButtonsOrdered.get(buttonIndex);
                // Check if button is valid and enabled
                if (targetButton != null && !targetButton.isDisabled()) {
                    targetButton.fire(); // Simulate click (action method will play sound)
                    event.consume();
                } else {
                    System.out.println("Inventory slot " + (buttonIndex + 1) + " action denied (button disabled or null).");
                    displayMessage("None available!", 1.5); // Shortened duration
                }
            }
            return; // Handled inventory key
        }

        // Check general interaction allowance for other actions
        if (!isInteractionAllowed("KEYPRESS")) {
            System.out.println("Interaction denied by keypress due to Chao state.");
            return;
        }

        // Other gameplay actions
        switch (event.getCode()) {
            case P: playChao(); event.consume(); break;
            case Z: sleepChao(); event.consume(); break;
            case E: exerciseChao(); event.consume(); break;
            case V: vetChao(); event.consume(); break;
            case B: bonkChao(); event.consume(); break;
            case Q: petChao(); event.consume(); break; // Q for Pet
            default: // No action mapped
                break;
        }
    }

    /**
     * Initializes the lists of item names used for random spawning of fruits and gifts.
     */
    private void initializeItemLists() {
        fruitItemNames = Arrays.asList(
                "Red Fruit", "Blue Fruit", "Green Fruit", "Hero Fruit", "Dark Fruit"
        );
        giftItemNames = Arrays.asList(
                "Trumpet", "Duck", "T.V."
        );
    }

    /**
     * Maps inventory item names (Strings) to their corresponding UI elements (Button and Label)
     * stored in the `inventoryUIMap`. This allows easy access to UI components for updates.
     */
    private void initializeInventoryUIMap() {
        if (redFruitButton == null || redFruitCountLabel == null) { // Check one pair for readiness
            System.err.println("Error in initializeInventoryUIMap: FXML components not injected yet!");
            return;
        }
        inventoryUIMap.clear(); // Ensure map is empty before population
        inventoryUIMap.put("Red Fruit", new InventoryItemUI(redFruitButton, redFruitCountLabel));
        inventoryUIMap.put("Blue Fruit", new InventoryItemUI(blueFruitButton, blueFruitCountLabel));
        inventoryUIMap.put("Green Fruit", new InventoryItemUI(greenFruitButton, greenFruitCountLabel));
        inventoryUIMap.put("Hero Fruit", new InventoryItemUI(heroFruitButton, heroFruitCountLabel));
        inventoryUIMap.put("Dark Fruit", new InventoryItemUI(darkFruitButton, darkFruitCountLabel));
        inventoryUIMap.put("Trumpet", new InventoryItemUI(trumpetButton, trumpetCountLabel));
        inventoryUIMap.put("Duck", new InventoryItemUI(duckButton, duckCountLabel));
        inventoryUIMap.put("T.V.", new InventoryItemUI(tvButton, tvCountLabel));
    }

    /**
     * Updates the display of all inventory items in the UI.
     * Sets the count text on labels and enables/disables buttons based on item availability (count > 0).
     * Ensures UI updates happen on the JavaFX Application Thread.
     */
    private void updateInventoryDisplay() {
        if (inventory == null || inventoryUIMap == null || inventoryUIMap.isEmpty()) {
            System.err.println("Cannot update inventory display: Inventory or UI Map not initialized.");
            return;
        }

        Platform.runLater(() -> { // Ensure UI updates on FX thread
            inventoryUIMap.forEach((itemName, itemUI) -> {
                // Add null check for itemUI itself
                if (itemUI == null || itemUI.button() == null || itemUI.countLabel() == null) {
                    System.err.println("Warning: UI elements missing or null for item: " + itemName);
                    return; // Skip if UI components are missing
                }

                int count = inventory.getItemCount(itemName);
                itemUI.countLabel().setText(String.valueOf(count));
                itemUI.button().setDisable(count <= 0); // Disable if count is 0 or less
            });
        });
    }

//    /**
//     * Selects a random basic Chao type (Blue, Red, or Green).
//     *
//     * @return A randomly chosen basic ChaoType.
//     */
//    private ChaoType getRandomBasicChaoType() {
//        ChaoType[] basicTypes = {ChaoType.BLUE, ChaoType.RED, ChaoType.GREEN};
//        return basicTypes[random.nextInt(basicTypes.length)];
//    }

    /**
     * Loads Chao data (placeholder) or creates a new default Chao.
     * If restarting via "Play Again", reuses the base Chao type selected at the start of the session.
     * Initializes or updates the main Chao animation display.
     */
    private void loadOrCreateChao(Chao chao) {
        // Create the Chao instance
        this.chao = chao;
        this.isSleeping = false; // Reset state flag
        this.currentSessionBaseType = chao.getType();

        // --- Initialize or Update Animation ---
        if (chaoImageView != null) {
            if (chaoAnimation == null) { // First time creation
                chaoAnimation = new ChaoAnimation(chaoImageView, chao.getType(), AnimationState.fromState(chao.getState()));
                chaoAnimation.startAnimation();
            } else { // Update existing animation instance
                syncChaoAnimationToType(chao.getType());
                syncChaoAnimationToState(chao.getState(), true); // Reset to NORMAL animation
            }
        } else {
            System.err.println("CRITICAL: chaoImageView is null during Chao creation.");
        }
    }

    /**
     * Adds a default set of items to the inventory. Typically used for new games.
     */
    private void addDefaultInventory() {
        System.out.println("Adding default inventory items...");
        inventory.addItem("Red Fruit", 3);
        inventory.addItem("Blue Fruit", 3);
        inventory.addItem("Green Fruit", 3);
        inventory.addItem("Hero Fruit", 3);
        inventory.addItem("Dark Fruit", 3);
        inventory.addItem("Trumpet", 1);
        inventory.addItem("Duck", 1);
        inventory.addItem("T.V.", 1);
    }

    /**
     * Sets up the periodic timelines for game events like stat decay, state monitoring,
     * clock updates, and item spawning.
     */
    private void setupTimelines() {
        // Stat Decay (e.g., every 2 seconds)
        statDecayTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> decreaseStats())
        );
        statDecayTimeline.setCycleCount(Timeline.INDEFINITE);

        // Frequent State Monitor (e.g., every 250ms)
        stateMonitorTimeline = new Timeline(
                new KeyFrame(Duration.millis(250), e -> {
                    monitorChaoState();
                    time.stepTime();
                    if(!time.canPlay()){
                        //TODO: show popup for game
                        System.out.println("Chao timed out.");
                        time.storeTime(game);
                    };
                })
        );

        stateMonitorTimeline.setCycleCount(Timeline.INDEFINITE);

        // Clock Update Timeline (every second)
        clockTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> updateClock()), // Update immediately once
                new KeyFrame(Duration.seconds(1)) // Then repeat every second
        );
        clockTimeline.setCycleCount(Timeline.INDEFINITE);

        // Fruit Spawning Timeline (every 5 seconds)
        fruitSpawnTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> spawnRandomItem(fruitItemNames, "fruit"))
        );
        fruitSpawnTimeline.setCycleCount(Timeline.INDEFINITE);

        // Gift Spawning Timeline (every 10 seconds)
        giftSpawnTimeline = new Timeline(
                new KeyFrame(Duration.seconds(10), e -> spawnRandomItem(giftItemNames, "gift"))
        );
        giftSpawnTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts all the main game loop timelines (stat decay, state monitor, clock, spawners).
     * Note: Sleep increase timeline is started separately when the Chao sleeps.
     */
    private void startTimelines() {
        if (statDecayTimeline != null) statDecayTimeline.play();
        if (stateMonitorTimeline != null) stateMonitorTimeline.play();
        if (clockTimeline != null) clockTimeline.play();
        if (fruitSpawnTimeline != null) fruitSpawnTimeline.play();
        if (giftSpawnTimeline != null) giftSpawnTimeline.play();
    }

    /**
     * Updates the time label in the UI with the current system time, formatted.
     */
    private void updateClock() {
        if (timeLabel != null) {
            Platform.runLater(() -> timeLabel.setText(LocalTime.now().format(timeFormatter)));
        }
    }

    /**
     * Spawns a random item from the specified item pool (fruits or gifts).
     * Adds the item to the inventory, updates the UI display, and shows a notification message.
     * Does nothing if the Chao is dead.
     *
     * @param itemPool The list of item names to choose from (e.g., `fruitItemNames`).
     * @param itemCategory A string descriptor for the item type ("fruit" or "gift"), used for logging.
     */
    private void spawnRandomItem(List<String> itemPool, String itemCategory) {
        if (chao == null || chao.getState() == State.DEAD) {
            return; // Don't spawn items if game isn't active
        }
        if (itemPool == null || itemPool.isEmpty()) {
            System.err.println("Cannot spawn item: " + itemCategory + " pool is empty.");
            return;
        }
        String itemName = itemPool.get(random.nextInt(itemPool.size()));
        inventory.addItem(itemName, 1);
        updateInventoryDisplay();
        // Display message
        displayMessage("Found a " + itemName + "!", 2.0);
    }

    /**
     * Updates the static image in the profile box to show the Chao sitting.
     */
    private void updateProfileChaoImage() {
        if (profileChaoImageView == null || chao == null) {
            return; // Cannot update if view or chao is missing
        }

        String sitFrameName = AnimationState.SIT.getResourceName() + "1.png";
        String imagePath = String.format("/com/example/chaotopia/sprites/%s/%s",
                chao.getType().getResourceName(),
                sitFrameName);

        try { // Use try-with-resources for InputStream later if needed, but getResource is simpler
            URL resourceUrl = getClass().getResource(imagePath);
            if (resourceUrl != null) {
                Image sitImage = new Image(resourceUrl.toExternalForm());
                Platform.runLater(() -> profileChaoImageView.setImage(sitImage)); // Update on FX thread
            } else {
                System.err.println("Could not load profile image resource: " + imagePath);
                Platform.runLater(() -> profileChaoImageView.setImage(null)); // Clear image on error
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image at path: " + imagePath);
            e.printStackTrace();
            Platform.runLater(() -> profileChaoImageView.setImage(null));
        }
    }

    // --- Chao Actions (Linked from FXML Buttons) ---

    /**
     * Handles the "Play" action for the Chao.
     * Increases happiness and score, shows happy animation, and potentially breaks out of ANGRY state.
     * Plays relevant sound effects.
     */
    @FXML
    public void playChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("PLAY")) {
            lastUserActionTime = System.currentTimeMillis();
            String commandResult = Commands.play(chao);

            if (commandResult == null) {
                stopAllLoops();
                Commands.play(chao);
                updateStatusBars();
                score.updateScore(10);
                updateScoreUI(score.getScore());

                playSoundEffect(happyPlayer);
                showHappyAnimation();

                // Special handling if we're angry
                if (chao.getState() == State.ANGRY && chao.getStatus().getHappiness() >= 50) {
                    chao.setState(State.NORMAL);
                    syncChaoAnimationToState(State.NORMAL, true);
                }
            }else {
                displayMessage(commandResult, 1.5);
            }
        } else {
            handleInteractionDenied("PLAY");
        }
    }

    /**
     * Handles the "Sleep" action for the Chao.
     * If successful, puts the Chao into the SLEEPING state, starts the sleep stat increase,
     * disables interactions, and plays/loops the sleeping sound.
     */
    @FXML
    public void sleepChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("SLEEP")) {
            stopAllLoops();
            Commands.sleep(chao);
            if (chao.getState() == State.SLEEPING) {
                // Start sleeping sound loop
                if (sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    sleepingSoundPlayer.seek(Duration.ZERO);
                    sleepingSoundPlayer.play();
                }
                // Stop other state loops
                if (angryPlayer != null) angryPlayer.stop();
                if (cryingPlayer != null) cryingPlayer.stop();

                updateStatusBars();
                score.updateScore(7);
                updateScoreUI(score.getScore());
                isSleeping = true;
                syncChaoAnimationToState(State.SLEEPING, true); // Force update
                startSleepIncrease();
                enableAllInteractions(false); // Disable buttons during sleep
            }
        } else {
            handleInteractionDenied("SLEEP");
        }
    }

    /**
     * Handles the "Exercise" action for the Chao.
     * Increases health/happiness, decreases fullness/sleep, updates score, and shows happy animation/sound.
     */
    @FXML
    public void exerciseChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("EXERCISE")) {
            lastUserActionTime = System.currentTimeMillis();
            stopAllLoops();
            Commands.exercise(chao);
            updateStatusBars();
            score.updateScore(10);
            updateScoreUI(score.getScore());

            playSoundEffect(happyPlayer);
            showHappyAnimation();
        } else {
            handleInteractionDenied("EXERCISE");
        }
    }

    /**
     * Handles the "Vet" action for the Chao.
     * Fully restores health if needed, deducts score, and shows happy animation/sound.
     * Displays a message if the Chao is already healthy.
     */
    @FXML
    public void vetChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("VET")) {
            String commandResult = Commands.vet(chao);
            if (commandResult != null) { // Vet returns message if already healthy
                displayMessage(commandResult, 2.0);
                return; // Don't proceed if message shown
            }

            stopAllLoops();
            playSoundEffect(happyPlayer);

            updateStatusBars();
            score.updateScore(-20); // Cost for vet visit
            updateScoreUI(score.getScore());
            showHappyAnimation(); // Happy after being healed
            lastUserActionTime = System.currentTimeMillis();

        } else {
            handleInteractionDenied("VET");
        }
    }

    /**
     * Handles the "Pet" action for the Chao.
     * Increases happiness and Hero alignment, updates score, and potentially triggers Hero evolution.
     * Shows happy animation/sound if no evolution occurs.
     */
    @FXML
    public void petChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("PET")) {
            int previousAlignment = chao.getAlignment();
            Commands.pet(chao);
            updateStatusBars(); // Update stats display

            // Check for Hero evolution only if it's a basic type and not already evolved
            if (previousAlignment < 7 && chao.getAlignment() >= 7 && chao.getType() != ChaoType.HERO) {
                lastUserActionTime = System.currentTimeMillis();
                triggerEvolution(true); // True for Hero
            } else {
                lastUserActionTime = System.currentTimeMillis();
                stopAllLoops();
                playSoundEffect(happyPlayer);
                score.updateScore(3);
                updateScoreUI(score.getScore());
                showHappyAnimation();
            }
        } else {
            handleInteractionDenied("PET");
        }
    }

    /**
     * Handles the "Bonk" action for the Chao.
     * Decreases happiness, increases Dark alignment, updates score, and potentially triggers Dark evolution.
     * Plays a bonk/crying sound and shows a temporary animation.
     */
    @FXML
    public void bonkChao() {
        playSoundEffect(buttonClickPlayer);
        if (isInteractionAllowed("BONK")) {
            int previousAlignment = chao.getAlignment();
            Commands.bonk(chao); // Apply model changes
            updateStatusBars(); // Update UI
            score.updateScore(-3);
            updateScoreUI(score.getScore());

            // Check for Dark evolution only if basic type and not already evolved
            if (previousAlignment > -7 && chao.getAlignment() <= -7 && chao.getType() != ChaoType.DARK) {
                triggerEvolution(false); // False for Dark
                lastUserActionTime = System.currentTimeMillis();
            } else {
                lastUserActionTime = System.currentTimeMillis();
                stopAllLoops();
                playSoundEffect(bonkSoundPlayer);
                showTemporaryStateAnimation(AnimationState.HUNGRY, 2);
            }
        } else {
            handleInteractionDenied("BONK");
        }
    }

    // --- Item/Fruit Actions ---

    /** FXML action method linked to the Trumpet button. Calls {@link #giftItem}. */
    @FXML public void giftTrumpet() { playSoundEffect(buttonClickPlayer); giftItem("Trumpet"); }
    /** FXML action method linked to the Duck button. Calls {@link #giftItem}. */
    @FXML public void giftDuck() { playSoundEffect(buttonClickPlayer); giftItem("Duck"); }
    /** FXML action method linked to the TV button. Calls {@link #giftItem}. */
    @FXML public void giftTV() { playSoundEffect(buttonClickPlayer); giftItem("T.V."); }

    /** FXML action method linked to the Red Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedRedFruit() { playSoundEffect(buttonClickPlayer); feedFruit("Red Fruit", FruitType.RED, false); }
    /** FXML action method linked to the Blue Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedBlueFruit() { playSoundEffect(buttonClickPlayer); feedFruit("Blue Fruit", FruitType.BLUE, false); }
    /** FXML action method linked to the Green Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedGreenFruit() { playSoundEffect(buttonClickPlayer); feedFruit("Green Fruit", FruitType.GREEN, false); }
    /** FXML action method linked to the Hero Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedHeroFruit() { playSoundEffect(buttonClickPlayer); feedFruit("Hero Fruit", FruitType.HERO, true); }
    /** FXML action method linked to the Dark Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedDarkFruit() { playSoundEffect(buttonClickPlayer); feedFruit("Dark Fruit", FruitType.DARK, true); }

    /**
     * Handles the action of giving a non-fruit item (gift) to the Chao.
     * Checks inventory, displays item visually, applies effects via Commands, updates UI, score, and plays sounds.
     *
     * @param itemName The name of the gift item being given.
     */
    private void giftItem(String itemName) {
        if (!isInteractionAllowed("GIFT")) { handleInteractionDenied("GIFT"); return; }
        if (inventory.getItemCount(itemName) <= 0) { displayMessage("No " + itemName + " available!", 1.5); return; }

        // Display the item visually BEFORE applying effects
        double displayDuration = 2.0; // How long to show the item
        if ("Duck".equals(itemName) || "T.V.".equals(itemName) || "Trumpet".equals(itemName)) {
            displayGiftedItem(itemName, displayDuration);
        }

        // Apply logic
        stopAllLoops();
        lastUserActionTime = System.currentTimeMillis();
        playSoundEffect(happyPlayer);
        Commands.give(chao, new Item(itemName)); // Apply effect to model
        inventory.removeItem(itemName);          // Update model
        updateInventoryDisplay();                // Update UI
        updateStatusBars();                      // Update UI
        score.updateScore(10);                   // Update model
        updateScoreUI(score.getScore());         // Update UI
        showHappyAnimation();                    // Show visual reaction
        displayMessage(chao.getName() + " liked the " + itemName + "!", 1.5); // Show text feedback
    }

    /**
     * Displays the image of a gifted item temporarily near the Chao.
     * Uses the fruitImageView location. Stops conflicting animations.
     *
     * @param itemName The name of the item (e.g., "Duck", "T.V.", "Trumpet"). Must match image filename.
     * @param durationSeconds How long the item should be displayed.
     */
    private void displayGiftedItem(String itemName, double durationSeconds) {
        if (fruitImageView == null) { return; } // Safety check

        String safeItemName = itemName.toLowerCase().replace(".", ""); // Handle "T.V." -> "tv"
        String imagePath = String.format("/com/example/chaotopia/Assets/Inventory/%s.png", safeItemName);

        try {
            URL resourceUrl = getClass().getResource(imagePath);
            if (resourceUrl == null) {
                System.err.println("Could not load gift item image resource: " + imagePath);
                return;
            }
            Image itemImage = new Image(resourceUrl.toExternalForm());

            // Stop conflicting animations/displays
            if (fruitAnimation != null) fruitAnimation.stopAnimation();
            if (giftDisplayTimeline != null) giftDisplayTimeline.stop();
            fruitImageView.setVisible(false); // Hide current content immediately

            // Display Item & Set Hide Timer
            Platform.runLater(() -> {
                fruitImageView.setImage(itemImage);
                fruitImageView.setVisible(true);

                giftDisplayTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(durationSeconds), e -> {
                            // Check if this timer is still valid and image matches
                            if (giftDisplayTimeline != null && giftDisplayTimeline.getStatus() != Timeline.Status.STOPPED) {
                                if (fruitImageView != null && fruitImageView.getImage() == itemImage) {
                                    fruitImageView.setVisible(false);
                                    fruitImageView.setImage(null);
                                }
                            }
                        })
                );
                giftDisplayTimeline.setCycleCount(1);
                giftDisplayTimeline.play();
            });

        } catch (Exception e) {
            System.err.println("Error processing gift item image: " + imagePath);
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of feeding a fruit item to the Chao.
     * Checks inventory, triggers animation/sound, applies effects via Commands,
     * updates UI, score, checks for alignment-based evolution.
     *
     * @param fruitName The name of the fruit item being fed.
     * @param fruitType The corresponding FruitType enum for animation purposes.
     * @param isSpecial Indicates if the fruit uses the special feeding logic.
     */
    private void feedFruit(String fruitName, FruitType fruitType, boolean isSpecial) {
        if (!isInteractionAllowed("FEED")) { handleInteractionDenied("FEED"); return; }
        if (inventory.getItemCount(fruitName) <= 0) { displayMessage("No " + fruitName + " available!", 1.5); return; }
        if (fruitAnimation == null) { System.err.println("Cannot feed fruit: FruitAnimation not initialized."); return; }

        // Stop any active gift display
        if (giftDisplayTimeline != null && giftDisplayTimeline.getStatus() == Timeline.Status.RUNNING) {
            giftDisplayTimeline.stop();
            fruitImageView.setVisible(false);
        }

        // Play sound & start animation
        stopAllLoops();
        playSoundEffect(eatingPlayer);
        fruitAnimation.changeFruitType(fruitType);

        // Apply logic
        Item fruit = new Item(fruitName);
        int previousAlignment = chao.getAlignment();
        ChaoType previousType = chao.getType();
        boolean evolutionTriggered = false;

        if (isSpecial) {
            Commands.feedSpecialFruit(chao, fruit); // Apply stats FIRST
            // Check evolution condition AFTER stats are applied
            if (fruitName.equals("Hero Fruit") && previousAlignment < 7 && chao.getAlignment() >= 7 && previousType != ChaoType.HERO) {
                triggerEvolution(true); evolutionTriggered = true;
            } else if (fruitName.equals("Dark Fruit") && previousAlignment > -7 && chao.getAlignment() <= -7 && previousType != ChaoType.DARK) {
                triggerEvolution(false); evolutionTriggered = true;
            }
        } else {
            Commands.feed(chao, fruit);
        }

        // Update inventory & UI
        inventory.removeItem(fruitName);
        updateInventoryDisplay();
        updateStatusBars(); // Update stats display

        if (!evolutionTriggered) {
            lastUserActionTime = System.currentTimeMillis();
            score.updateScore(5);
            updateScoreUI(score.getScore());
            showTemporaryStateAnimation(AnimationState.HAPPY,3);
        }

    }

    // --- Evolution Logic ---

    /**
     * Initiates and manages the Chao evolution sequence.
     * Disables interactions, plays sound/animation, updates Chao model,
     * displays messages, and handles post-evolution state transition.
     *
     * @param isHeroEvolution True if triggering Hero evolution, false for Dark evolution.
     */
    private void triggerEvolution(boolean isHeroEvolution) {
        // Re-check conditions right before starting
        if (chao == null || chao.getState() == State.EVOLVING) return;
        ChaoType currentType = chao.getType();
        int currentAlignment = chao.getAlignment();
        boolean canEvolve = (isHeroEvolution && currentType != ChaoType.HERO && currentAlignment >= 7) ||
                (!isHeroEvolution && currentType != ChaoType.DARK && currentAlignment <= -7);
        if (!canEvolve) return; // Exit if conditions somehow changed

        // --- Setup Evolution State ---
        lastUserActionTime = System.currentTimeMillis();
        chao.setState(State.EVOLVING);
        isSleeping = false; // Can't sleep while evolving
        stopAllLoops();
        playSoundEffect(evolutionSoundPlayer); // Play evolution SFX
        enableAllInteractions(false); // Disable UI interactions

        // Update score
        int evolutionScore = isHeroEvolution ? 50 : 25;
        score.updateScore(evolutionScore);
        updateScoreUI(score.getScore());

        // Start visual animation
        if (chaoAnimation != null) chaoAnimation.changeAnimation(AnimationState.EVOLVING);

        // --- Timeline for Evolution Process ---
        if (evolutionTimeline != null) evolutionTimeline.stop(); // Stop previous if any

        double evolutionVisualDuration = 4.0; // Duration of the evolving visual effect
        double postEvolutionPoseDuration = 2.0; // Duration to show the final pose

        evolutionTimeline = new Timeline(
                new KeyFrame(Duration.seconds(evolutionVisualDuration), e -> {
                    // -- Visuals Complete: Apply Model Change --
                    if (chao == null || chao.getState() != State.EVOLVING) return; // Check if still evolving

                    chao.evolve(); // Actual change in the model
                    displayMessage(chao.getName() + " evolved to " + chao.getType() + "!", 4.0);
                    syncChaoAnimationToType(chao.getType()); // Update animation base type
                    updateProfileChaoImage(); // Update profile picture

                    // Show the final pose animation
                    AnimationState finalPose = isHeroEvolution ? AnimationState.HAPPY : AnimationState.ANGRY;
                    if (chaoAnimation != null) chaoAnimation.changeAnimation(finalPose);

                    // -- Schedule Return to Normal State --
                    Timeline returnTimeline = new Timeline(
                            new KeyFrame(Duration.seconds(postEvolutionPoseDuration), e2 -> {
                                if (chao == null) return;
                                // Revert state if still in post-evolution pose
                                if (chao.getState() == State.EVOLVING) {
                                    chao.setState(State.NORMAL);
                                    syncChaoAnimationToState(State.NORMAL, true);
                                }
                                enableAllInteractions(true); // Re-enable UI
                            })
                    );
                    returnTimeline.play();
                })
        );
        evolutionTimeline.play();
    }


    // --- UI Update Methods ---

    /**
     * Updates the Chao's name label in the UI. Runs on FX thread.
     */
    private void updateNameLabel() {
        if (nameLabel != null && chao != null) {
            Platform.runLater(() -> nameLabel.setText(chao.getName()));
        }
    }

    /**
     * Updates the score label in the UI. Runs on FX thread.
     * @param newScore The score value to display.
     */
    private void updateScoreUI(int newScore) {
        if (scoreLabel != null) {
            Platform.runLater(() -> scoreLabel.setText("Score: " + newScore));
        }
    }

    /**
     * Updates all status progress bars and numeric labels based on current Chao stats.
     * Runs on FX thread and handles death check.
     */
    public void updateStatusBars() {
        if (chao == null || healthBar == null || fullnessBar == null || happinessBar == null || sleepBar == null ||
                healthLabel == null || fullnessLabel == null || happinessLabel == null || sleepLabel == null ) {
            return; // Exit if required components are missing
        }

        final Status status = chao.getStatus(); // Get status once

        Platform.runLater(() -> {
            int health = status.isDead() ? 0 : status.getHealth();
            int fullness = status.getFullness();
            int happiness = status.getHappiness();
            int sleep = status.getSleep();

            // Update Bars
            healthBar.setProgress(health / 100.0);
            fullnessBar.setProgress(fullness / 100.0);
            happinessBar.setProgress(happiness / 100.0);
            sleepBar.setProgress(sleep / 100.0);

            // Update Labels
            healthLabel.setText(String.valueOf(health));
            fullnessLabel.setText(String.valueOf(fullness));
            happinessLabel.setText(String.valueOf(happiness));
            sleepLabel.setText(String.valueOf(sleep));

            // Apply low status styling
            checkAndApplyLowStatus(healthBar, health, 25);
            checkAndApplyLowStatus(fullnessBar, fullness, 25);
            checkAndApplyLowStatus(happinessBar, happiness, 25);
            checkAndApplyLowStatus(sleepBar, sleep, 25);

            // Check for death *after* UI updates
            if (status.isDead() && chao.getState() != State.DEAD) {
                handleDeath();
            }
        });
    }

    /**
     * Applies/removes a CSS style class (`low-status-bar`) to a ProgressBar
     * based on whether the value is below a threshold. Runs on FX thread.
     *
     * @param bar The ProgressBar UI element.
     * @param value The current integer value of the status.
     * @param threshold The threshold below which the style should be applied.
     */
    private void checkAndApplyLowStatus(ProgressBar bar, int value, int threshold) {
        final String lowStatusStyleClass = "low-status-bar";
        if (bar == null) return;

        Platform.runLater(() -> { // Ensure style changes on FX thread
            boolean isLow = (value <= threshold);
            // Use StyleClass methods which handle duplicates
            if (isLow) {
                if (!bar.getStyleClass().contains(lowStatusStyleClass)) {
                    bar.getStyleClass().add(lowStatusStyleClass);
                }
            } else {
                bar.getStyleClass().remove(lowStatusStyleClass);
            }
        });
    }

    /**
     * Handles the Chao's death: sets state, stops game loops/sounds,
     * updates animation, shows game over screen, disables interactions.
     */
    private void handleDeath() {
        if (chao == null || chao.getState() == State.DEAD) return; // Prevent multiple calls

        System.out.println(chao.getName() + " has died.");
        chao.setState(State.DEAD);
        isSleeping = false;

        stopTimelines(); // Stop game loop timers
        stopAllSounds(); // Stop all sounds

        if (chaoAnimation != null) {
            chaoAnimation.changeState(State.DEAD);
        }
        showGameOverScreen(); // Display game over UI
        enableAllInteractions(false); // Ensure interactions stay disabled
    }

    /**
     * Displays a message temporarily in the messageLabel UI element.
     * Runs on the FX Application thread.
     *
     * @param message The text message to display.
     * @param durationSeconds How long the message should remain visible.
     */
    public void displayMessage(String message, double durationSeconds) {
        if (messageLabel == null) {
            System.out.println("UI Message (Label not found): " + message);
            return;
        }
        // System.out.println("Displaying Message: " + message); // Optional: Reduce console spam

        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setVisible(true);

            // Stop previous message timer if running
            if (messageTimeline != null) {
                messageTimeline.stop();
            }

            // Start new timer to hide the message
            messageTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(durationSeconds), e -> {
                        if (messageLabel != null) messageLabel.setVisible(false);
                    })
            );
            messageTimeline.play();
        });
    }

    // --- Animation and State Management Helpers ---

    /**
     * Shows a temporary happy visual animation, then reverts visuals based on the current logical state.
     *
     * @return true if the temporary animation was started, false otherwise.
     */
    public boolean showHappyAnimation() {
        return showTemporaryStateAnimation(AnimationState.HAPPY, 1.5);
    }

    /**
     * Shows a temporary visual animation for a specified state, then reverts visuals
     * to match the current logical state after the duration. Does not change logical state.
     *
     * @param tempAnimState The AnimationState to show temporarily.
     * @param duration Seconds to show the temporary state.
     * @return true if animation was started, false otherwise.
     */
    public boolean showTemporaryStateAnimation(AnimationState tempAnimState, double duration) {
        // Don't show temp anims during critical states or if animation system not ready
        if (chao == null || chaoAnimation == null || chao.getState() == State.DEAD || chao.getState() == State.EVOLVING || isSleeping) {
            return false;
        }

        // Stop previous temp timer if running
        if (tempAnimationTimer != null) {
            tempAnimationTimer.stop();
        }

        // Store previous state *before* showing temp anim? No, rely on monitorChaoState to fix.
        // Change visuals immediately
        chaoAnimation.changeAnimation(tempAnimState);

        // Start timer to revert visuals
        tempAnimationTimer = new Timeline(
                new KeyFrame(Duration.seconds(duration), e -> {
                    // When timer ends, sync animation back to the *actual* current logical state
                    if (chao != null && chao.getState() != State.DEAD && chao.getState() != State.EVOLVING) {
                        // Don't revert if Chao fell asleep/etc during the temp anim
                        if (chao.getState() != State.SLEEPING) {
                            syncChaoAnimationToState(chao.getState(), true); // Revert visuals
                        }
                    }
                })
        );
        tempAnimationTimer.setCycleCount(1);
        tempAnimationTimer.play();
        return true;
    }

    /**
     * Updates the Chao's main visual animation to match its logical state.
     * Delegates to the ChaoAnimation object.
     *
     * @param newState The logical state (e.g., State.SLEEPING).
     * @param forceRestart If true, forces the animation to restart.
     */
    private void syncChaoAnimationToState(State newState, boolean forceRestart) {
        if (chaoAnimation != null) {
            // Let ChaoAnimation handle the state->animation mapping and restart logic
            chaoAnimation.changeState(newState);
        }
    }

    /**
     * Changes the Chao type used by the main Chao animation instance.
     * Delegates to the ChaoAnimation object.
     *
     * @param newType The new ChaoType to display.
     */
    private void syncChaoAnimationToType(ChaoType newType) {
        if (chaoAnimation != null) {
            chaoAnimation.changeChaoType(newType); // Assumes this handles starting/stopping
        }
    }

    /**
     * Starts the timeline to gradually increase the sleep stat while the Chao is sleeping.
     * Handles waking the Chao up when sleep is full or interrupted.
     */
    public void startSleepIncrease() {
        if (chao == null) return; // Safety check
        if (sleepIncreaseTimeline != null) sleepIncreaseTimeline.stop();
        System.out.println(chao.getName() + " is increasing sleep...");

        sleepIncreaseTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    if (chao != null && isSleeping && chao.getStatus().getSleep() < 100) {
                        chao.getStatus().adjustSleep(4);
                        updateStatusBars(); // Update UI
                    } else {
                        // Stop condition (sleep full or no longer 'isSleeping')
                        if (sleepIncreaseTimeline != null) {
                            sleepIncreaseTimeline.stop();
                        }
                        // Trigger wake up logic if applicable
                        if (chao != null && isSleeping) { // Check if we *were* sleeping when timeline ended
                            handleWakeUp(chao.getStatus().getSleep() < 100); // Pass true if wake up was forced/interrupted
                        }
                    }
                })
        );
        sleepIncreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        sleepIncreaseTimeline.play();
    }

    /**
     * Handles the logic for when a Chao wakes up. Stops sounds/timelines,
     * updates state via monitor, enables UI.
     * @param forced True if waking up was not due to reaching 100 sleep.
     */
    private void handleWakeUp(boolean forced) {
        if (chao == null || !isSleeping) return; // Already awake or no chao

        System.out.println(chao.getName() + " waking up (forced=" + forced + ")");
        isSleeping = false; // Update internal flag FIRST
        if (sleepingSoundPlayer != null) sleepingSoundPlayer.stop(); // Stop sound loop
        if (sleepIncreaseTimeline != null) sleepIncreaseTimeline.stop(); // Ensure timeline is stopped

        if (!forced) {
            displayMessage(chao.getName() + " woke up!", 4.0);
        }

        // Re-evaluate state *after* setting isSleeping=false
        monitorChaoState();
        // Only enable interactions if the new state allows it
        if (chao.getState() != State.DEAD && chao.getState() != State.EVOLVING) {
            enableAllInteractions(true);
        }
    }

    /**
     * Periodically checks Chao stats, determines the correct logical state,
     * updates the state, syncs the visual animation, and manages looping sounds.
     */
    private void monitorChaoState() {
        // Exit checks
        if (chao == null || chao.getState() == State.DEAD || chao.getState() == State.EVOLVING) {
            stopAllLoops(); return; // Stop loops and exit
        }

        // Don't change logical state during temp visual animations (unless ANGRY)
        boolean tempVisualRunning = tempAnimationTimer != null && tempAnimationTimer.getStatus() == Timeline.Status.RUNNING;
        if (tempVisualRunning && chao.getState() != State.ANGRY) {
            updateSoundLoops(chao.getState()); // Keep sounds synced to logical state
            return;
        }

        State currentState = chao.getState();
        Status status = chao.getStatus();
        State determinedState = currentState;

        // --- State Determination Logic ---
        // Check SLEEPING state first (based on isSleeping flag)
        if (isSleeping) {
            determinedState = State.SLEEPING;
        }
        // Check if just woke up (isSleeping is false, but currentState was SLEEPING)
        else if (currentState == State.SLEEPING) {
            if (status.getHappiness() <= 0) determinedState = State.ANGRY;
            else if (status.getFullness() <= 0) determinedState = State.HUNGRY;
            else determinedState = State.NORMAL;
        }
        // Check if should fall asleep (and not already SLEEPING)
        else if (status.getSleep() <= 0 && currentState != State.SLEEPING) {
            // Apply exhaustion effects and set up sleep state
            System.out.println(chao.getName() + " fell asleep from exhaustion!");
            displayMessage(chao.getName() + " fell asleep from exhaustion!", 4.0);
            status.adjustHealth(-15);
            updateStatusBars(); // Show health drop

            determinedState = State.SLEEPING;
            chao.setState(State.SLEEPING); // Set state NOW
            isSleeping = true;
            syncChaoAnimationToState(State.SLEEPING, true);
            enableAllInteractions(false);
            startSleepIncrease(); // Start gaining sleep
            stopAllLoops(); // Stop angry/crying loops
            updateSoundLoops(State.SLEEPING); // Start sleeping loop
            return; // Exit monitor early after setting up sleep
        }
        // Check for ANGRY state (if not sleeping/evolving/dead)
        else if (status.getHappiness() <= 0 || (currentState == State.ANGRY && status.getHappiness() < 50)) {
            determinedState = State.ANGRY;
        }
        // Check for HUNGRY state (if not sleeping/evolving/dead/angry)
        else if (status.getFullness() <= 0 && currentState != State.ANGRY) { // Don't override ANGRY with HUNGRY
            determinedState = State.HUNGRY;
        }
        // Check for returning to NORMAL from ANGRY/HUNGRY
        else if (currentState == State.ANGRY && status.getHappiness() >= 50) {
            determinedState = State.NORMAL;
        } else if (currentState == State.HUNGRY && status.getFullness() > 0) {
            determinedState = State.NORMAL;
        }
        // Otherwise, if no other state applies, remain NORMAL (or current state if not previously handled)
        else if (currentState == State.NORMAL || determinedState == currentState) { // handles default case or no change needed
            determinedState = State.NORMAL;
        }


        // --- Apply State Change and Sync Visuals/Sounds ---
        if (determinedState != currentState) {
            System.out.println("Monitor State change: " + currentState + " -> " + determinedState);
            chao.setState(determinedState);
            syncChaoAnimationToState(determinedState, true);
        }

        updateSoundLoops(determinedState); // Ensure sounds match final state
    }

    /** Helper method to start/stop looping sounds based on the given state, respecting cooldown. */
    private void updateSoundLoops(State state) {
        long currentTime = System.currentTimeMillis();

        // --- Stop incorrect loops first (always safe) ---
        if (state != State.ANGRY && angryPlayer != null && angryPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            angryPlayer.stop();
        }
        if (state != State.HUNGRY && cryingPlayer != null && cryingPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            cryingPlayer.stop();
        }
        if (state != State.SLEEPING && sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            sleepingSoundPlayer.stop();
        }

        // --- Check Cooldown before STARTING a loop ---
        if (currentTime - lastUserActionTime < SOUND_LOOP_COOLDOWN_MS) {
            return;
        }

        // --- Start the correct loop if not already playing AND not in cooldown ---
        try {
            if (state == State.ANGRY && angryPlayer != null && angryPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                angryPlayer.seek(Duration.ZERO); angryPlayer.play();
            } else if (state == State.HUNGRY && cryingPlayer != null && cryingPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                cryingPlayer.seek(Duration.ZERO); cryingPlayer.play();
            } else if (state == State.SLEEPING && sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                sleepingSoundPlayer.seek(Duration.ZERO); sleepingSoundPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error trying to start sound loop for state " + state + ": " + e.getMessage());
        }
    }

    /** Helper method to explicitly stop all state-based looping sounds. */
    private void stopAllLoops() {
        if (angryPlayer != null) {angryPlayer.stop();
        }
        if (cryingPlayer != null) { cryingPlayer.stop();
        }
        if (sleepingSoundPlayer != null) {sleepingSoundPlayer.stop();
        }
    }

    /**
     * Applies natural stat decay over time, updates UI, and re-evaluates Chao state.
     * Called by the statDecayTimeline.
     */
    private void decreaseStats() {
        if (chao == null || chao.getState() == State.DEAD) {
            return;
        }

        Status status = chao.getStatus();
        int prevHealth = status.getHealth();

        Commands.applyNaturalDecay(chao); // Apply model change
        updateStatusBars(); // Update UI

        monitorChaoState(); // Re-evaluate state immediately

        // Check for death caused by decay AFTER potentially changing state
        if (prevHealth > 0 && chao.getStatus().isDead()) { // Re-check status
            handleDeath();
        }
    }

    // --- Game Over Logic ---

    /**
     * Creates and displays the Game Over overlay screen with score and options.
     * Runs on FX thread.
     */
    private void showGameOverScreen() {
        if (gameOverOverlay != null) return; // Prevent duplicate

        Platform.runLater(() -> {
            try {
                enableAllInteractions(false);

                // UI Setup (Consider CSS)
                VBox gameOverBox = new VBox(10);
                gameOverBox.setAlignment(Pos.CENTER);
                gameOverBox.setStyle("-fx-background-color: rgba(40, 20, 10, 0.85); -fx-padding: 30px; -fx-border-color: #8B4513; -fx-border-width: 4px; -fx-border-radius: 15px; -fx-background-radius: 15px;");
                gameOverBox.setMaxSize(350, 200);

                Label gameOverLabel = new Label("GAME OVER");
                gameOverLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-';");

                Label finalScoreLabel = new Label("Final Score: " + score.getScore());
                finalScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-';");

                HBox buttonBox = new HBox(20);
                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.setPadding(new Insets(15, 0, 0, 0));

                Button newGameButton = new Button("Play Again?");
                newGameButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 12px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #DEB887; -fx-border-radius: 8;");
                newGameButton.setOnAction(e -> startNewGame());

                Button mainMenuButton = new Button("Main Menu");
                mainMenuButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 12px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #DEB887; -fx-border-radius: 8;");
                mainMenuButton.setOnAction(e -> goToMenu(e));

                buttonBox.getChildren().addAll(newGameButton, mainMenuButton);
                gameOverBox.getChildren().addAll(gameOverLabel, finalScoreLabel, buttonBox);

                // Add to center pane
                if (centerStackPane != null) {
                    StackPane overlayContainer = new StackPane(gameOverBox);
                    overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
                    overlayContainer.setAlignment(Pos.CENTER);
                    // Optional positioning:
                    // overlayContainer.setTranslateX(-75);
                    // overlayContainer.setTranslateY(100);
                    this.gameOverOverlay = overlayContainer;
                    centerStackPane.getChildren().add(overlayContainer);
                } else {
                    System.err.println("Cannot show Game Over: centerStackPane is null.");
                }
            } catch (Exception e) {
                System.err.println("Error showing game over screen: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Removes the Game Over overlay screen from the UI. Runs on FX thread.
     */
    private void removeGameOverScreen() {
        if (gameOverOverlay != null && centerStackPane != null) {
            Platform.runLater(() -> { // Ensure removal on FX thread
                if (centerStackPane.getChildren().contains(gameOverOverlay)) {
                    centerStackPane.getChildren().remove(gameOverOverlay);
                }
                gameOverOverlay = null; // Clear reference
            });
        }
    }

    /**
     * Resets game state for a new session: removes overlay, resets models,
     * restarts sounds/timelines, updates UI, enables interactions.
     */
    private void startNewGame() {
        System.out.println("Starting new game...");
        removeGameOverScreen();
        stopAllSounds(); // Stop sounds from previous session

        // Reset Models
        score = new Score(0);
        inventory = new Inventory();
        addDefaultInventory();

        //loadOrCreateChao(); // Create new Chao (reuses base type if set)

        // Restart Background Music
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.seek(Duration.ZERO);
            backgroundMusicPlayer.play();
        } else {
            loadSounds(); // Attempt to reload sounds
            if(backgroundMusicPlayer != null) backgroundMusicPlayer.play();
        }

        // Update UI
        updateScoreUI(score.getScore());
        updateNameLabel();
        updateStatusBars();
        updateInventoryDisplay();
        updateProfileChaoImage();

        // Reset State Flags (handled by loadOrCreateChao)
        isSleeping = false;
        previousState = State.NORMAL;

        // Restart Timelines & Interactions
        stopTimelines(); // Stop old before setup
        setupTimelines();
        startTimelines();
        enableAllInteractions(true);

        // Ensure Animation is Correct (handled by loadOrCreateChao->sync)

        System.out.println("New game started.");
    }

    // --- System Actions ---

    /** FXML action method linked to the Save button. Placeholder. */
    @FXML
    public void saveGame() {
        playSoundEffect(buttonClickPlayer);
        try {
            game.save();
            System.out.println("Game saved successfully!");
        } catch (IOException exp) {
            System.err.println("Failed to save game: " + exp.getMessage());
        }
        System.out.println("Attempting to save game...");
        displayMessage("Game Saved!", 2.0);
    }

    /**
     * FXML action method linked to the Back/Main Menu button. Shuts down game state
     * and should trigger navigation (placeholder).
     */
    @FXML
    public void goToMenu(ActionEvent event) {
        System.out.println("Returning to Main Menu... (Implement Navigation)");
        displayMessage("Returning to Menu...", 1.5);
        shutdown(); // Clean up current game
        try{
            goToMainMenu(event);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public void goToM(KeyEvent event) {
//        System.out.println("Returning to Main Menu... (Implement Navigation)");
//        displayMessage("Returning to Menu...", 1.5);
//        shutdown(); // Clean up current game
//        try{
//            goToMainMenu(event);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * Stops all running timelines, sounds, and animations. Called before closing stage or navigating away.
     */
    public void shutdown() {
        System.out.println("Gameplay Controller Shutting Down...");
        stopTimelines();
        stopAllSounds();
        if (chaoAnimation != null) chaoAnimation.stopAnimation();
        if (fruitAnimation != null) fruitAnimation.stopAnimation();
    }

    /**
     * Stops all currently running MediaPlayer instances.
     */
    private void stopAllSounds() {
        System.out.println("Stopping all sounds...");
        MediaPlayer[] players = {
                bonkSoundPlayer, backgroundMusicPlayer, buttonClickPlayer, eatingPlayer,
                happyPlayer, angryPlayer, cryingPlayer, sleepingSoundPlayer, evolutionSoundPlayer
        };
        for (MediaPlayer player : players) {
            if (player != null) {
                // Use Platform.runLater if encountering issues stopping sounds quickly
                // Platform.runLater(() -> player.stop());
                player.stop();
            }
        }
    }

    /**
     * Stops all timelines related to game progression and temporary effects.
     */
    private void stopTimelines() {
        System.out.println("Stopping timelines...");
        Timeline[] timelines = {
                statDecayTimeline, stateMonitorTimeline, sleepIncreaseTimeline, tempAnimationTimer,
                messageTimeline, evolutionTimeline, giftDisplayTimeline, clockTimeline,
                fruitSpawnTimeline, giftSpawnTimeline
        };
        for (Timeline tl : timelines) {
            if (tl != null) {
                tl.stop();
            }
        }
    }

    // --- Interaction Checks and Helpers ---

    /**
     * Enables or disables interaction buttons (actions, inventory). Runs on FX thread.
     * @param enable True to enable, false to disable.
     */
    public void enableAllInteractions(boolean enable) {
        // Include all user-interactable buttons/areas here
        List<Node> controlsToToggle = new ArrayList<>(Arrays.asList(
                playButton, sleepButton, exerciseButton, vetButton, petButton, bonkButton
        ));
        // Add inventory buttons if not handled by disabling the container
        if(inventoryButtonContainer != null) {
            inventoryButtonContainer.setDisable(!enable);
        } else { // Fallback if container isn't used/injected
            controlsToToggle.addAll(Arrays.asList(redFruitButton, blueFruitButton, greenFruitButton, heroFruitButton, darkFruitButton, trumpetButton, duckButton, tvButton));
        }


        Platform.runLater(() -> {
            controlsToToggle.forEach(node -> {
                if (node != null) node.setDisable(!enable);
            });
            // Ensure system buttons have desired state
            if (saveButton != null) saveButton.setDisable(false); // Example: Always enabled
            if (backButton != null) backButton.setDisable(false); // Example: Always enabled
        });
    }

    /**
     * Checks if a specific interaction type is allowed based on the Chao's current state.
     *
     * @param commandType String representing the action type (e.g., "PLAY", "FEED", "KEYPRESS").
     * @return true if the interaction is allowed, false otherwise.
     */
    private boolean isInteractionAllowed(String commandType) {
        if (chao == null) return false;
        State currentState = chao.getState();

        // Block during critical states
        if (currentState == State.DEAD || currentState == State.EVOLVING || currentState == State.SLEEPING) {
            return false;
        }

        // Block most actions when ANGRY
        if (currentState == State.ANGRY) {
            return commandType.equalsIgnoreCase("PLAY")
                    || commandType.equalsIgnoreCase("GIFT")
                    || commandType.equalsIgnoreCase("KEYPRESS"); // Allow keypress if mapped to allowed action
        }

        // Otherwise, allow
        return true;
    }

    /**
     * Displays a message indicating why an interaction was denied based on the Chao's state.
     *
     * @param commandType String representing the action denied (used in message).
     */
    private void handleInteractionDenied(String commandType) {
        if (chao == null) return;
        State currentState = chao.getState();
        String reason = switch (currentState) {
            case DEAD -> chao.getName() + " is no longer with us...";
            case SLEEPING -> chao.getName() + " is sleeping!";
            case ANGRY -> chao.getName() + " is too angry to " + commandType.toLowerCase() + "!";
            case EVOLVING -> chao.getName() + " is evolving!";
            default -> "Interaction not allowed right now.";
        };
        displayMessage(reason, 1.5);
        // Optional: Play a "denied" sound effect
    }
}