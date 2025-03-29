package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

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

    // --- Timelines ---
    private Timeline statDecayTimeline;
    private Timeline stateMonitorTimeline;
    private Timeline sleepIncreaseTimeline;
    private Timeline tempAnimationTimer;    // For temporary states like HAPPY
    private Timeline messageTimeline;       // For hiding messages
    private Timeline evolutionTimeline;     // For evolution sequences
    private Timeline clockTimeline;         // *** NEW: For the real-time clock ***
    private Timeline fruitSpawnTimeline;    // *** NEW: For spawning fruit ***
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

    // --- State Management ---
    private boolean isSleeping = false;
    private State previousState = State.NORMAL;
    private Node gameOverOverlay = null; // Reference to the game over screen
    private Random random = new Random(); //for random starting type. remove once choose chao
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    // --- Item Spawning ---
    private List<String> fruitItemNames;
    private List<String> giftItemNames;

    // --- Initialization ---

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSounds();
        inventory = new Inventory();
        score = new Score(0);
        inventoryUIMap = new HashMap<>();
        inventoryButtonsOrdered = new ArrayList<>();
        initializeItemLists();

        if (fruitImageView != null) {
            fruitAnimation = new FruitAnimation(fruitImageView, FruitType.RED);
            fruitImageView.setVisible(false);
        } else System.err.println("FXML Warning: fruitImageView is null.");

        // --- Load Game Data (or create default) ---
        loadOrCreateChao(); // Now uses random basic type
        initializeInventoryUIMap();
        populateOrderedInventoryButtons();
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }

        if (isNewGameCondition()) { // Replace with your actual new game check
            addDefaultInventory(); // Populate the inventory data model
        } else {
            // TODO: Load inventory data from save file
        }

        updateScoreUI(score.getScore());
        updateNameLabel();
        updateStatusBars();
        updateProfileChaoImage();
        updateInventoryDisplay();

        //Setup Time
        setupTimelines();
        startTimelines();

        Platform.runLater(() -> {
            if (mainContainer != null && mainContainer.getScene() != null) {
                setupKeybindings(mainContainer.getScene());
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
        return inventory.isEmpty(); // Assuming you add an isEmpty() method to Inventory
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
            bonkSoundPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/crying.wav");
            // looping sounds
            sleepingSoundPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/sleeping.wav");
            if (sleepingSoundPlayer != null) {
                sleepingSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Set to loop, but we'll manually stop/start
            }

            angryPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/angry.wav");
            if (angryPlayer != null) {
                angryPlayer.setCycleCount(MediaPlayer.INDEFINITE); // *** Make ANGRY loop ***
            }

            cryingPlayer = createMediaPlayer("/com/example/chaotopia/Assets/Sounds/crying.wav");
            if (cryingPlayer != null) {
                cryingPlayer.setCycleCount(MediaPlayer.INDEFINITE); // *** Make CRYING loop ***
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
                player.seek(Duration.ZERO); // Rewind to start
                player.play();
            });
        } else {
            System.err.println("Attempted to play a null sound effect.");
        }
    }

    /**
     * Attaches the key press event handler (`handleKeyPress`) to the provided Scene.
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
        if (event.getCode() == KeyCode.S) {
            playSoundEffect(buttonClickPlayer);
            saveGame();
            event.consume(); // Indicate the event has been handled
            return;
        }
        if (event.getCode() == KeyCode.M) {
            playSoundEffect(buttonClickPlayer);
            goToMainMenu();
            event.consume();
            return;
        }

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
                // Check if button is not null and is currently enabled (implicitly checks interaction allowed state for items)
                if (targetButton != null && !targetButton.isDisabled()) {
                    targetButton.fire(); // Simulate a button click
                    event.consume();
                } else {
                    System.out.println("Inventory slot " + (buttonIndex + 1) + " action denied (button disabled or null).");
                    displayMessage("None available!",2);
                }
            }
            return; // Handled (or attempted to handle) inventory key
        }

        if (!isInteractionAllowed("KEYPRESS")) { // Use a generic type or check within methods
            System.out.println("Interaction denied by keypress due to Chao state.");
            return;
        }

        switch (event.getCode()) {
            case P:
                playChao();
                event.consume();
                break;
            case Z:
                sleepChao();
                event.consume();
                break;
            case E:
                exerciseChao();
                event.consume();
                break;
            case V:
                vetChao();
                event.consume();
                break;
            case B:
                bonkChao();
                event.consume();
                break;
            case Q: // Changed from 'P' for Pet as 'P' is Play
                petChao();
                event.consume();
                break;
            default:
                break;
        }
    }

    /**
     * Initializes the lists of item names used for random spawning of fruits and gifts.
     */
    private void initializeItemLists() {
        fruitItemNames = Arrays.asList(
                "Red Fruit",
                "Blue Fruit",
                "Green Fruit",
                "Hero Fruit",
                "Dark Fruit"
        );
        giftItemNames = Arrays.asList(
                "Trumpet",
                "Duck",
                "T.V."
        );
    }

    /**
     * Maps inventory item names (Strings) to their corresponding UI elements (Button and Label)
     * stored in the `inventoryUIMap`. This allows easy access to UI components for updates.
     */
    private void initializeInventoryUIMap() {
        if (redFruitButton == null || redFruitCountLabel == null) {
            System.err.println("Error in initializeInventoryUIMap: FXML components not injected yet!");
            return;
        }
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
                if (itemUI.button() == null || itemUI.countLabel() == null) {
                    System.err.println("Warning: UI elements missing for item: " + itemName);
                    return; // Skip if button or label injection failed
                }

                int count = inventory.getItemCount(itemName);

                // Update count label text
                itemUI.countLabel().setText(String.valueOf(count));

                // Enable/disable button based on count & handle hover effect
                boolean available = count > 0;
                itemUI.button().setDisable(!available);

            });
        });
    }
    /**
     * Selects a random basic Chao type (Blue, Red, or Green).
     *
     * @return A randomly chosen basic ChaoType.
     */
    private ChaoType getRandomBasicChaoType() {
        ChaoType[] basicTypes = {ChaoType.BLUE, ChaoType.RED, ChaoType.GREEN};
        return basicTypes[random.nextInt(basicTypes.length)];
    }

    /**
     * Loads Chao data (if available, placeholder) or creates a new default Chao.
     * If restarting via "Play Again", reuses the base Chao type selected at the start of the session.
     * Initializes or updates the main Chao animation display.
     */
    private void loadOrCreateChao() {
        // TODO: Implement actual load logic here. If loading from a save,
        if (this.currentSessionBaseType == null) {
            System.out.println("First time load or new session: Picking random basic Chao type.");
            this.currentSessionBaseType = getRandomBasicChaoType(); // Pick AND store for reuse
        } else {
            System.out.println("Restarting game (Play Again): Reusing base Chao type: " + this.currentSessionBaseType);
        }
        // Create the Chao instance
        System.out.println("Creating Chao with type: " + this.currentSessionBaseType);
        // Always reset stats, name (if desired), state, etc.
        Status initialStatus = new Status(100, 100, 100, 100); // Reset stats
        // Use the determined (and possibly stored) base type
        this.chao = new Chao(0, "Bubbles", this.currentSessionBaseType, State.NORMAL, initialStatus);
        this.isSleeping = false; // Reset state flag

        // --- Initialize or Update Animation ---
        if (chaoImageView != null) {
            if (chaoAnimation == null) { // First time creation
                // Make sure the animation uses the correct type right away
                chaoAnimation = new ChaoAnimation(chaoImageView, chao.getType(), AnimationState.fromState(chao.getState()));
                chaoAnimation.startAnimation();
            } else {
                // Ensure animation matches the (potentially reused) type and reset state
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
                new KeyFrame(Duration.millis(250), e -> monitorChaoState())
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

        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream != null) {
                Image sitImage = new Image(stream);
                Platform.runLater(() -> profileChaoImageView.setImage(sitImage)); // Update on FX thread
            } else {
                System.err.println("Could not load profile image: " + imagePath);
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
            playSoundEffect(happyPlayer);
            Commands.play(chao);
            updateStatusBars();
            score.updateScore(10);
            updateScoreUI(score.getScore());
            showHappyAnimation();

            // Special handling if we're angry
            if (chao.getState() == State.ANGRY && chao.getStatus().getHappiness() >= 50) {
                chao.setState(State.NORMAL);
                syncChaoAnimationToState(State.NORMAL, true);
            }
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
            Commands.sleep(chao);
            if (chao.getState() == State.SLEEPING) {
                if(sleepingSoundPlayer != null) {
                    sleepingSoundPlayer.seek(Duration.ZERO);
                    sleepingSoundPlayer.play();
                }
                updateStatusBars();
                score.updateScore(7);
                updateScoreUI(score.getScore());
                isSleeping = true;
                syncChaoAnimationToState(State.SLEEPING, true); // Force update
                startSleepIncrease();
                enableAllInteractions(false); // Disable buttons during sleep
            }
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
            Commands.exercise(chao);
            playSoundEffect(happyPlayer);
            updateStatusBars();
            score.updateScore(10);
            updateScoreUI(score.getScore());
            showHappyAnimation();
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

            playSoundEffect(happyPlayer);
            // If vet was needed (no message returned)
            updateStatusBars();
            score.updateScore(-20); // Cost for vet visit
            updateScoreUI(score.getScore());
            showHappyAnimation(); // Happy after being healed
        }else {
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
            updateStatusBars();

            // Check for Hero evolution only if it's a basic type
            if (previousAlignment < 7 && chao.getAlignment() >= 7) {
                triggerEvolution(true); // True for Hero
            } else {
                playSoundEffect(happyPlayer);
                score.updateScore(3);
                updateScoreUI(score.getScore());
                showHappyAnimation();
            }
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
            Commands.bonk(chao);
            playSoundEffect(bonkSoundPlayer);
            updateStatusBars();
            score.updateScore(-3);
            updateScoreUI(score.getScore());

            // Check for Dark evolution
            if (previousAlignment > -7 && chao.getAlignment() <= -7) {
                triggerEvolution(false); // False for Dark
            } else {
                showTemporaryStateAnimation(AnimationState.HUNGRY, 2);
            }
        }else {
            handleInteractionDenied("BONK");
        }
    }

    // --- Item/Fruit Actions ---

    /** FXML action method linked to the Trumpet button. Calls {@link #giftItem}. */
    @FXML public void giftTrumpet() { playSoundEffect(buttonClickPlayer);giftItem("Trumpet"); }
    /** FXML action method linked to the Duck button. Calls {@link #giftItem}. */
    @FXML public void giftDuck() { playSoundEffect(buttonClickPlayer);giftItem("Duck"); }
    /** FXML action method linked to the TV button. Calls {@link #giftItem}. */
    @FXML public void giftTV() { playSoundEffect(buttonClickPlayer);giftItem("T.V."); } // Ensure item name matches inventory key

    /** FXML action method linked to the Red Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedRedFruit() { playSoundEffect(buttonClickPlayer);feedFruit("Red Fruit", FruitType.RED, false); }
    /** FXML action method linked to the Blue Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedBlueFruit() { playSoundEffect(buttonClickPlayer);feedFruit("Blue Fruit", FruitType.BLUE, false); }
    /** FXML action method linked to the Green Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedGreenFruit() { playSoundEffect(buttonClickPlayer);feedFruit("Green Fruit", FruitType.GREEN, false); } // Assuming GREEN exists
    /** FXML action method linked to the Hero Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedHeroFruit() { playSoundEffect(buttonClickPlayer);feedFruit("Hero Fruit", FruitType.HERO, true); }
    /** FXML action method linked to the Dark Fruit button. Calls {@link #feedFruit}. */
    @FXML public void feedDarkFruit() {playSoundEffect(buttonClickPlayer); feedFruit("Dark Fruit", FruitType.DARK, true); }

    /**
     * Handles the action of giving a non-fruit item (gift) to the Chao.
     * Checks inventory, applies item effects via Commands, updates UI, score, and plays sounds.
     *
     * @param itemName The name of the gift item being given.
     */
    private void giftItem(String itemName) {
        if (!isInteractionAllowed("GIFT")) {
            handleInteractionDenied("GIFT"); return;
        }
        if (inventory.getItemCount(itemName) <= 0) {
            displayMessage("No " + itemName + " available!", 1.5);
            return;
        }

        // --- Display the item visually BEFORE applying effects ---
        double displayDuration = 2.0; // How long to show the item
        if ("Duck".equals(itemName) || "T.V.".equals(itemName) || "Trumpet".equals(itemName)) {
            displayGiftedItem(itemName, displayDuration);
        }

        playSoundEffect(happyPlayer);
        Commands.give(chao, new Item(itemName));
        inventory.removeItem(itemName);
        updateInventoryDisplay();
        updateStatusBars();
        score.updateScore(10);
        updateScoreUI(score.getScore());
        showHappyAnimation();
        displayMessage(chao.getName() + " liked the " + itemName + "!", 1.5);
    }

    /**
     * Displays the image of a gifted item temporarily near the Chao.
     * Uses the fruitImageView location.
     *
     * @param itemName The name of the item (e.g., "Duck", "T.V.", "Trumpet"). Must match image filename (lowercase).
     * @param durationSeconds How long the item should be displayed.
     */
    private void displayGiftedItem(String itemName, double durationSeconds) {
        if (fruitImageView == null) {
            System.err.println("Cannot display gift item: fruitImageView is null.");
            return;
        }

        // Construct the image path
        String imagePath = String.format("/com/example/chaotopia/Assets/Inventory/%s.png", itemName.toLowerCase().replace(".", ""));

        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream == null) {
                System.err.println("Could not load gift item image: " + imagePath);
                return;
            }

            Image itemImage = new Image(stream);

            // Stop any previous gift display timeline
            if (giftDisplayTimeline != null) {
                giftDisplayTimeline.stop();
            }
            if (fruitAnimation != null) {
                fruitAnimation.stopAnimation();
                Platform.runLater(() -> {
                    if(fruitImageView != null) fruitImageView.setVisible(false);
                });
            }

            // --- Display the Item ---
            Platform.runLater(() -> {
                fruitImageView.setImage(itemImage);
                // Optional: Adjust size
                // fruitImageView.setFitWidth(60);
                // fruitImageView.setFitHeight(60);
                // fruitImageView.setPreserveRatio(true);
                fruitImageView.setVisible(true);

                // --- Set Timer to Hide ---
                giftDisplayTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(durationSeconds), e -> {
                            if (giftDisplayTimeline != null && giftDisplayTimeline.getStatus() != Timeline.Status.STOPPED) {
                                if (fruitImageView != null) {
                                    if(fruitImageView.getImage() == itemImage) {
                                        fruitImageView.setVisible(false);
                                        fruitImageView.setImage(null);
                                    } else {
                                        System.out.println("Gift display timer finished, but imageview has changed.");
                                    }
                                }
                            }
                        })
                );
                giftDisplayTimeline.setCycleCount(1);
                giftDisplayTimeline.play();
            });

        } catch (Exception e) {
            System.err.println("Error loading or displaying gift item image at path: " + imagePath);
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of feeding a fruit item to the Chao.
     * Checks inventory, triggers fruit animation, applies fruit effects via Commands,
     * updates UI, score, checks for alignment-based evolution, and plays sounds.
     *
     * @param fruitName The name of the fruit item being fed.
     * @param fruitType The corresponding FruitType enum for animation purposes.
     * @param isSpecial Indicates if the fruit uses the special feeding logic (e.g., Hero/Dark Fruit).
     */
    private void feedFruit(String fruitName, FruitType fruitType, boolean isSpecial) {
        if (!isInteractionAllowed("FEED")) {
            handleInteractionDenied("FEED"); return;
        }
        if (inventory.getItemCount(fruitName) <= 0) {
            displayMessage("No " + fruitName + " available!", 1.5);
            return;
        }
        if (fruitAnimation == null) {
            System.err.println("Cannot feed fruit: FruitAnimation not initialized.");
            return;
        }

        // --- Stop any active gift display ---
        if (giftDisplayTimeline != null && giftDisplayTimeline.getStatus() == Timeline.Status.RUNNING) {
            System.out.println("Fruit feeding interrupting gift display."); // Optional log
            giftDisplayTimeline.stop();
            // fruitImageView will be immediately reused by fruit animation, no need to hide here
        }

        playSoundEffect(eatingPlayer);
        fruitAnimation.changeFruitType(fruitType);

        Item fruit = new Item(fruitName);
        int previousAlignment = chao.getAlignment();
        ChaoType previousType = chao.getType(); // Check type BEFORE feeding
        boolean evolutionTriggered = false;

        if (isSpecial) {
            Commands.feedSpecialFruit(chao, fruit); // Apply stats FIRST
            // Check evolution condition AFTER stats are applied, using PREVIOUS type
            if (fruitName.equals("Hero Fruit") && previousAlignment < 7 && chao.getAlignment() >= 7) {
                triggerEvolution(true);
                evolutionTriggered = true;
            } else if (fruitName.equals("Dark Fruit") && previousAlignment > -7 && chao.getAlignment() <= -7) {
                triggerEvolution(false);
                evolutionTriggered = true;
            }
        } else {
            Commands.feed(chao, fruit);
        }

        inventory.removeItem(fruitName);
        updateInventoryDisplay();
        if (!evolutionTriggered) {
            score.updateScore(5);
            updateScoreUI(score.getScore());
            showTemporaryStateAnimation(AnimationState.HAPPY, 3);
        }
        updateStatusBars();
    }


    // --- Evolution Logic ---

    /**
     * Initiates and manages the Chao evolution sequence.
     * Disables interactions, plays evolution animation, updates Chao type,
     * displays messages, plays sounds, and returns to normal state after a delay.
     *
     * @param isHeroEvolution True if triggering Hero evolution, false for Dark evolution.
     */
    private void triggerEvolution(boolean isHeroEvolution) {
        ChaoType currentType = chao.getType();
        int currentAlignment = chao.getAlignment();
        int evolutionScore = isHeroEvolution ? 50 : 25;

        boolean canEvolveToHero = isHeroEvolution && currentType != ChaoType.HERO && currentAlignment >= 7;
        boolean canEvolveToDark = !isHeroEvolution && currentType != ChaoType.DARK && currentAlignment <= -7;

        if (!canEvolveToHero && !canEvolveToDark) {
            enableAllInteractions(true); // Ensure buttons are usable
            return;
        }

        AnimationState evolutionAnimation = AnimationState.EVOLVING;
        AnimationState finalStateAnimation = isHeroEvolution ? AnimationState.HAPPY : AnimationState.ANGRY;

        double evolutionDelay = 4.0;
        double finalAnimationDelay = 2.0;

        score.updateScore(evolutionScore);
        chao.setState(State.EVOLVING);
        updateScoreUI(score.getScore());
        enableAllInteractions(false);

        if (chaoAnimation != null) chaoAnimation.changeAnimation(evolutionAnimation);

        if (evolutionTimeline != null) evolutionTimeline.stop();

        evolutionTimeline = new Timeline(
                new KeyFrame(Duration.seconds(evolutionDelay), e -> { // Use declared variable
                    chao.evolve();
                    displayMessage(chao.getName() + " evolved to " + chao.getType() + "!",4);
                    syncChaoAnimationToType(chao.getType());
                    updateProfileChaoImage();

                    if (chaoAnimation != null) chaoAnimation.changeAnimation(finalStateAnimation);

                    Timeline returnTimeline = new Timeline(
                            new KeyFrame(Duration.seconds(finalAnimationDelay), e2 -> { // Use declared variable
                                monitorChaoState();
                                State expectedPostEvoState = StateUtility.fromAnimationState(finalStateAnimation);
                                if (chao.getState() == State.EVOLVING || chao.getState() == expectedPostEvoState) {
                                    chao.setState(State.NORMAL);
                                    syncChaoAnimationToState(State.NORMAL, true);
                                }
                                enableAllInteractions(true);
                            })
                    );
                    returnTimeline.play();
                })
        );
        evolutionTimeline.play();
    }


    // --- UI Update Methods ---

    /**
     * Updates the Chao's name label in the UI.
     */
    private void updateNameLabel() {
        if (nameLabel != null && chao != null) {
            nameLabel.setText(chao.getName());
        }
    }

    /**
     * Updates the score label in the UI.
     *
     * @param newScore The score value to display.
     */
    private void updateScoreUI(int newScore) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + newScore);
        }
    }

    /**
     * Updates all status progress bars based on current Chao stats.
     */
    public void updateStatusBars() {
        if (chao == null || healthBar == null /* || etc... check all bars AND labels */
                || healthLabel == null || fullnessLabel == null || happinessLabel == null || sleepLabel == null ) {
            return;
        }

        final String lowStatusStyleClass = "low-status-bar";

        Platform.runLater(() -> { // Ensure UI updates on FX thread
            Status status = chao.getStatus();
            int health = status.isDead() ? 0 : status.getHealth();
            int fullness = status.getFullness();
            int happiness = status.getHappiness();
            int sleep = status.getSleep();

            // Update ProgressBars (0.0 to 1.0)
            healthBar.setProgress(health / 100.0);
            fullnessBar.setProgress(fullness / 100.0);
            happinessBar.setProgress(happiness / 100.0);
            sleepBar.setProgress(sleep / 100.0);

            // *** UPDATE LABELS ***
            healthLabel.setText(String.valueOf(health));
            fullnessLabel.setText(String.valueOf(fullness));
            happinessLabel.setText(String.valueOf(happiness));
            sleepLabel.setText(String.valueOf(sleep));

            checkAndApplyLowStatus(healthBar, health, 25);
            checkAndApplyLowStatus(fullnessBar, fullness, 25);
            checkAndApplyLowStatus(happinessBar, happiness, 25);
            checkAndApplyLowStatus(sleepBar, sleep, 25);

            // Check for death after updating bars
            if (status.isDead() && chao.getState() != State.DEAD) {
                handleDeath();
            }
        });
    }

    /**
     * Checks if a status value is below a threshold and applies/removes a specific CSS style class
     * to the corresponding ProgressBar for visual feedback (e.g., turning red).
     *
     * @param bar The ProgressBar UI element.
     * @param value The current integer value of the status.
     * @param threshold The threshold below which the style should be applied.
     */
    private void checkAndApplyLowStatus(ProgressBar bar, int value, int threshold) {
        final String lowStatusStyleClass = "low-status-bar";
        if (bar == null) return; // Safety check

        boolean isLow = (value <= threshold);
        boolean hasClass = bar.getStyleClass().contains(lowStatusStyleClass);

        if (isLow && !hasClass) {
            bar.getStyleClass().add(lowStatusStyleClass);
        } else if (!isLow && hasClass) {
            bar.getStyleClass().remove(lowStatusStyleClass);
        }
    }

    /**
     * Handles the game logic and UI changes when the Chao's health reaches zero.
     * Sets Chao state to DEAD, stops timelines and sounds, updates animation, and shows the game over screen.
     */
    private void handleDeath() {
        chao.setState(State.DEAD);
        stopTimelines();
        stopAllSounds();

        if (sleepingSoundPlayer != null) sleepingSoundPlayer.stop();

        if (chaoAnimation != null) {
            chaoAnimation.changeState(State.DEAD);
        }
        showGameOverScreen(); // This disables interactions
    }


    /**
     * Displays a message temporarily on the screen.
     */
    public void displayMessage(String message, double durationSeconds) {
        if (messageLabel == null) {
            System.out.println("UI Message (Label not found): " + message);
            return;
        }
        System.out.println("Displaying Message: " + message); // Console log

        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setVisible(true);

            if (messageTimeline != null) messageTimeline.stop();

            messageTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(durationSeconds), e -> messageLabel.setVisible(false))
            );
            messageTimeline.play();
        });
    }

    // --- Animation and State Management ---

    /**
     * Shows a temporary happy animation then reverts to the previous state.
     */
    public boolean showHappyAnimation() {
        // Store the previous state before showing happy animation
        State preHappyState = chao.getState();

        // Show the happy animation temporarily
        boolean shown = showTemporaryStateAnimation(AnimationState.HAPPY, 1.5);

        // If we were angry before, make sure we return to angry after
        if (shown && preHappyState == State.ANGRY) {
            Timeline returnToAngry = new Timeline(
                    new KeyFrame(Duration.seconds(1.6), e -> {
                        if (chao.getStatus().getHappiness() < 50) {
                            chao.setState(State.ANGRY);
                            syncChaoAnimationToState(State.ANGRY, true);
                        }
                    }) // Missing parenthesis added here
            );

            returnToAngry.play();
        }
        return shown;
    }


    /**
     * Shows a temporary animation for a specific state, then reverts.
     * @param tempAnimState The AnimationState to show temporarily.
     * @param duration Seconds to show the temporary state.
     * @return true if animation was shown, false otherwise.
     */

    public boolean showTemporaryStateAnimation(AnimationState tempAnimState, double duration) {
        State tempState = StateUtility.fromAnimationState(tempAnimState);

        if (chao == null || chao.getState() == State.DEAD || isSleeping || chao.getState() == State.EVOLVING) {
            return false;
        }

        if (tempAnimationTimer == null || tempAnimationTimer.getStatus() != Timeline.Status.RUNNING) {
            // Only update previousState if we are transitioning *from* a stable state
            if (chao.getState() != State.HAPPY && chao.getState() != State.HUNGRY && chao.getState() != State.ANGRY) { // Add other temp states if necessary
                this.previousState = chao.getState();
            } else {
                // If already in a temp state, keep the original previousState
                System.out.println("Already in temp state " + chao.getState() + ", keeping previousState: " + this.previousState); // Debug log
            }

        } else {
            tempAnimationTimer.stop();
            // Keep the originally stored previousState
        }

        chao.setState(tempState); // Set the temporary logical state
        if(chaoAnimation != null) {
            // Directly use AnimationState, changeAnimation handles starting/stopping
            chaoAnimation.changeAnimation(tempAnimState);
        }

        tempAnimationTimer = new Timeline(
                new KeyFrame(Duration.seconds(duration), e -> {
                    if (chao != null && chao.getState() == tempState) {
                        monitorChaoState();
                    } else {
                        System.out.println("Temp timer finished, but state was already different: " + (chao != null ? chao.getState() : "null chao")); // Debug log
                    }
                })
        );
        tempAnimationTimer.setCycleCount(1);
        tempAnimationTimer.play();
        return true;
    }



    /**
     * Updates the Chao's animation based on its logical state.
     * @param newState The logical state (e.g., State.SLEEPING)
     * @param forceRestart If true, stops and restarts the animation timeline.
     */
    private void syncChaoAnimationToState(State newState, boolean forceRestart) {
        if (chaoAnimation != null && chao != null) {
            AnimationState targetAnimState = AnimationState.fromState(newState);
            // Check if animation needs change OR if forcing restart
            if (chaoAnimation.getAnimationState() != targetAnimState || forceRestart) {
                // Use changeState which handles the conversion internally now
                chaoAnimation.changeState(newState);
            }
        }
    }
    /**
     * Changes the Chao type used by the main Chao animation instance.
     * Stops the current animation and starts the new one for the specified type.
     *
     * @param newType The new ChaoType to display.
     */
    private void syncChaoAnimationToType(ChaoType newType) {
        if (chaoAnimation != null) {
            chaoAnimation.changeChaoType(newType); // This stops/starts animation already
        }
    }

    /**
     * Starts the timeline to gradually increase sleep stat.
     */
    public void startSleepIncrease() {
        if (sleepIncreaseTimeline != null) sleepIncreaseTimeline.stop();
        System.out.println(chao.getName() + " is increasing sleep..."); // Debug log

        sleepIncreaseTimeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {
                    if (chao != null && isSleeping && chao.getStatus().getSleep() < 100) {
                        chao.getStatus().adjustSleep(2);
                        updateStatusBars();
                    } else {
                        // Stop condition
                        if (sleepIncreaseTimeline != null) {
                            sleepIncreaseTimeline.stop();
                        }

                        // *** WAKE UP LOGIC FIX ***
                        if (chao != null && isSleeping && chao.getStatus().getSleep() >= 100) {
                            if (sleepingSoundPlayer != null) sleepingSoundPlayer.stop();
                            displayMessage(chao.getName() + " woke up!",4);
                            isSleeping = false; // *** SET isSleeping TO FALSE ***
                            // Determine new state *after* waking up
                            monitorChaoState(); // This should set state to NORMAL/HUNGRY etc.
                            enableAllInteractions(true); // *** ENABLE INTERACTIONS ***
                            System.out.println("Interactions enabled after waking up."); // Debug log
                        } else if (!isSleeping) {
                            if (sleepingSoundPlayer != null) sleepingSoundPlayer.stop();
                            System.out.println("Sleep increase stopped because isSleeping is false."); // Debug log
                            enableAllInteractions(true); // Ensure enabled if stopped externally
                        }
                    }
                })
        );
        sleepIncreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        sleepIncreaseTimeline.play();
    }

    /**
     * Periodically checks the Chao's stats and updates its logical and visual state.
     */
    private void monitorChaoState() {
        if (chao == null || chao.getState() == State.DEAD || chao.getState() == State.EVOLVING){
            if (cryingPlayer != null) cryingPlayer.stop();
            if (angryPlayer != null) angryPlayer.stop();
            return;
        }

        Status status = chao.getStatus();
        State currentState = chao.getState();
        State previousMonitorState = currentState;

        boolean isTempStateRunning = (tempAnimationTimer != null &&
                tempAnimationTimer.getStatus() == Timeline.Status.RUNNING);

        // Allow monitoring if we're angry, even during temp animations
        if (isTempStateRunning && chao.getState() != State.ANGRY &&
                (currentState == State.HAPPY || currentState == State.HUNGRY)) {
            if (cryingPlayer != null) cryingPlayer.stop();
            if (angryPlayer != null) angryPlayer.stop();
            return; // Exit the monitor early
        }

        // --- If not in a temporary animation, determine correct state ---
        State determinedState = currentState; // Start with current

        if (isSleeping) {
            determinedState = State.SLEEPING;
            if (sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                System.out.println("Restarting sleeping sound from monitor...");
                sleepingSoundPlayer.seek(Duration.ZERO);
                sleepingSoundPlayer.play();
            }
        } else if (currentState == State.SLEEPING) {
            // Just woke up logic...
            if (status.getHappiness() <= 0) determinedState = State.ANGRY;
            else if (status.getFullness() <= 0) determinedState = State.HUNGRY;
            else determinedState = State.NORMAL;
        } else if (status.getSleep() <= 0) {
            if(sleepingSoundPlayer != null) {
                sleepingSoundPlayer.seek(Duration.ZERO);
                sleepingSoundPlayer.play();
            }
            displayMessage(chao.getName() + " fell asleep from exhaustion!",4);
            status.adjustHealth(-15);
            determinedState = State.SLEEPING;
            chao.setState(State.SLEEPING);    // Set logical state immediately
            isSleeping = true;
            syncChaoAnimationToState(State.SLEEPING, true);
            enableAllInteractions(false);
            startSleepIncrease();
            updateStatusBars();
            if (cryingPlayer != null) cryingPlayer.stop();
            if (angryPlayer != null) angryPlayer.stop();
            return; // Exit early
        } else if (status.getHappiness() <= 0 || (currentState == State.ANGRY && status.getHappiness() < 50)) {
            determinedState = State.ANGRY;
        } else if (status.getFullness() <= 0) {
            determinedState = State.HUNGRY;
        } else {
            determinedState = State.NORMAL;
        }

        // --- Manage Sound Loops based on determinedState ---

        // Stop loops that SHOULDN'T be playing based on the *determined* state
        if (determinedState != State.HUNGRY && cryingPlayer != null && cryingPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            System.out.println("Monitor stopping crying loop."); // Debug
            cryingPlayer.stop();
        }
        if (determinedState != State.ANGRY && angryPlayer != null && angryPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            System.out.println("Monitor stopping angry loop."); // Debug
            angryPlayer.stop();
        }
        if (determinedState != State.SLEEPING && sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            // Only stop sleeping sound if *we* determined state isn't sleeping
            // (Waking up via timeline handles its own stop)
            System.out.println("Monitor stopping sleeping loop."); // Debug
            sleepingSoundPlayer.stop();
        }

        // Start/Ensure loops that SHOULD be playing are playing
        if (determinedState == State.HUNGRY && cryingPlayer != null && cryingPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            System.out.println("Monitor starting/restarting crying loop."); // Debug
            cryingPlayer.seek(Duration.ZERO);
            cryingPlayer.play();
        } else if (determinedState == State.ANGRY && angryPlayer != null && angryPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            System.out.println("Monitor starting/restarting angry loop."); // Debug
            angryPlayer.seek(Duration.ZERO);
            angryPlayer.play();
        } else if (determinedState == State.SLEEPING && sleepingSoundPlayer != null && sleepingSoundPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            // Ensure sleeping sound restarts if monitor finds it should be playing but isn't
            System.out.println("Monitor starting/restarting sleeping loop."); // Debug
            sleepingSoundPlayer.seek(Duration.ZERO);
            sleepingSoundPlayer.play();
        }

        // --- Update Logical State and Animation (Only if actually changed) ---
        if (determinedState != previousMonitorState) {
            System.out.println("State changed: " + previousMonitorState + " -> " + determinedState);
            chao.setState(determinedState);
            syncChaoAnimationToState(determinedState, true); // Update animation
            // No need for one-shot sounds here now, loops handle it
        }
    }



/**
 * Applies natural stat decay over time.
 */
private void decreaseStats() {
    if (chao == null || chao.getState() == State.DEAD) {
        return; // No decay when dead or sleeping
    }

    Status status = chao.getStatus();
    int prevHealth = status.getHealth(); // Store for death check

    Commands.applyNaturalDecay(chao); // Apply decay logic

    updateStatusBars(); // Update UI

    // Check if stats hitting zero triggered a state change
    monitorChaoState(); // Re-evaluate state after decay

    // Explicitly check for death caused by decay *after* updating bars/state
    if (prevHealth > 0 && status.isDead()) {
        handleDeath();
    }
}

// --- Game Over Logic ---

    /**
     * Creates and displays the Game Over overlay screen.
     * Disables background interactions, shows the final score, and provides buttons
     * to play again or return to the main menu.
     */
private void showGameOverScreen() {
    if (gameOverOverlay != null) return; // Already showing

    Platform.runLater(() -> {
        try {
            enableAllInteractions(false); // Disable background buttons

            VBox gameOverBox = new VBox(10);
            gameOverBox.setAlignment(Pos.CENTER);
            // Apply CSS or inline styles matching the game's theme
            gameOverBox.setStyle("-fx-background-color: rgba(40, 20, 10, 0.85); -fx-padding: 30px; " +
                    "-fx-border-color: #8B4513; -fx-border-width: 4px; -fx-border-radius: 15px; " +
                    "-fx-background-radius: 15px;");
            gameOverBox.setMaxSize(350, 200);

            Label gameOverLabel = new Label("GAME OVER");
            gameOverLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-';"); // Use game font

            Label finalScoreLabel = new Label("Final Score: " + score.getScore());
            finalScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-';");

            HBox buttonBox = new HBox(20);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new javafx.geometry.Insets(15, 0, 0, 0));

            Button newGameButton = new Button("Play Again?");
            // Apply button styling from CSS if possible, or inline
            newGameButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 12px; " +
                    "-fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #DEB887; -fx-border-radius: 8;");
            newGameButton.setOnAction(e -> startNewGame());

            Button mainMenuButton = new Button("Main Menu");
            mainMenuButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 12px; " +
                    "-fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #DEB887; -fx-border-radius: 8;");
            mainMenuButton.setOnAction(e -> goToMainMenu());

            buttonBox.getChildren().addAll(newGameButton, mainMenuButton);
            gameOverBox.getChildren().addAll(gameOverLabel, finalScoreLabel, buttonBox);

            // Add to the center stack pane
            if (centerStackPane != null) {
                StackPane overlayContainer = new StackPane();
                overlayContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // Dim background
                overlayContainer.setAlignment(Pos.CENTER);
                overlayContainer.getChildren().add(gameOverBox);
                overlayContainer.setTranslateX(-75);
                overlayContainer.setTranslateY(100);

                this.gameOverOverlay = overlayContainer; // Store reference
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
     * Removes the Game Over overlay screen from the UI if it is currently displayed.
     */
private void removeGameOverScreen() {
    if (gameOverOverlay != null && centerStackPane != null) {
        centerStackPane.getChildren().remove(gameOverOverlay);
        gameOverOverlay = null;
    }
}
    /**
     * Resets the game state to start a new game session after a Game Over.
     * Clears the Game Over screen, resets score/inventory, creates a new Chao (reusing the initial base type),
     * restarts background music and timelines, updates all UI elements, and enables interactions.
     */
private void startNewGame() {
    System.out.println("Starting new game...");
    removeGameOverScreen();
    stopAllSounds();

    // Reset score, inventory, create new Chao
    score = new Score(0);
    inventory = new Inventory();
    addDefaultInventory(); // Add default items again
    loadOrCreateChao();    // Create a fresh tasty Chao

    if (backgroundMusicPlayer != null) {
        backgroundMusicPlayer.seek(Duration.ZERO); // Ensure it starts from beginning
        backgroundMusicPlayer.play();
    } else {
        loadSounds(); // Try to reload sounds if they failed initially? Or handle error better.
        if(backgroundMusicPlayer != null) backgroundMusicPlayer.play();
    }

    // Update UI immediately
    updateScoreUI(score.getScore());
    updateNameLabel();
    updateStatusBars();
    updateInventoryDisplay();
    updateProfileChaoImage();

    // Reset state flags
    isSleeping = false;
    previousState = State.NORMAL;

    // Restart timelines and enable interactions
    stopTimelines(); // Ensure old ones are stopped
    setupTimelines();
    startTimelines();
    enableAllInteractions(true);

    // Ensure Chao animation is correct
    if(chaoAnimation != null) {
        syncChaoAnimationToType(chao.getType());
        syncChaoAnimationToState(chao.getState(), true);
    }

    System.out.println("New game started.");
}

// --- System Actions ---

    /** FXML action method linked to the Save button. Placeholder for save game functionality. */
@FXML
public void saveGame() {
    playSoundEffect(buttonClickPlayer);
    // TODO: Implement actual saving mechanism (e.g., to JSON)
    System.out.println("Attempting to save game...");
    // Example: SaveManager.save(chao, inventory, score);
    displayMessage("Game Saved! (Placeholder)", 2.0);
}

    /**
     * FXML action method linked to the Back/Main Menu button.
     * Initiates the shutdown process and should trigger navigation back to the main menu scene (placeholder).
     */
@FXML
public void goToMainMenu() {
    // TODO: Implement navigation back to the main menu scene
    System.out.println("Returning to Main Menu... (Implement Navigation)");
    displayMessage("Returning to Menu...", 1.5);
    // Example: SceneManager.loadScene("MainMenu.fxml", mainContainer.getScene());
    // Make sure to call shutdown() before switching scenes if necessary
    shutdown();
}

/**
 * Stops all running timelines and animations. Call before closing the stage or navigating away.
 */
public void shutdown() {
    stopTimelines();
    stopAllSounds();
    if (chaoAnimation != null) {
        chaoAnimation.stopAnimation();
    }
    if (fruitAnimation != null) fruitAnimation.stopAnimation();
}

    /**
     * Stops all currently running MediaPlayer instances.
     * Ensures loops (background, sleeping, angry, crying) and one-shot sounds are halted.
     */
    private void stopAllSounds() {
        System.out.println("Stopping all sounds...");
        if (bonkSoundPlayer != null) bonkSoundPlayer.stop();
        if (backgroundMusicPlayer != null) backgroundMusicPlayer.stop();
        if (buttonClickPlayer != null) buttonClickPlayer.stop();
        if (eatingPlayer != null) eatingPlayer.stop();
        if (happyPlayer != null) happyPlayer.stop();
        if (angryPlayer != null) angryPlayer.stop(); // Will stop angry loop
        if (cryingPlayer != null) cryingPlayer.stop(); // Will stop crying loop
        if (sleepingSoundPlayer != null) sleepingSoundPlayer.stop(); // Will stop sleeping loop
    }

    /**
     * Stops all currently running Timeline instances used for game progression and events.
     */
private void stopTimelines() {
    if (statDecayTimeline != null) statDecayTimeline.stop();
    if (stateMonitorTimeline != null) stateMonitorTimeline.stop();
    if (sleepIncreaseTimeline != null) sleepIncreaseTimeline.stop();
    if (tempAnimationTimer != null) tempAnimationTimer.stop();
    if (messageTimeline != null) messageTimeline.stop();
    if (evolutionTimeline != null) evolutionTimeline.stop();
    if (giftDisplayTimeline != null) giftDisplayTimeline.stop();
}

// --- Interaction Checks and Helpers ---

/**
 * Enables or disables interaction buttons.
 * @param enable True to enable, false to disable.
 */
public void enableAllInteractions(boolean enable) {
    // List all interaction buttons/containers that need disabling
    List<Node> controlsToToggle = Arrays.asList(
            playButton, sleepButton, exerciseButton, vetButton, petButton, bonkButton,
            inventoryButtonContainer // Disable the whole VBox for items
            // Add specific item buttons here if they are not in the container
    );

    Platform.runLater(() -> {
        controlsToToggle.forEach(node -> {
            if (node != null) {
                node.setDisable(!enable);
            }
        });
        // Always keep save/back enabled (or handle separately)
        if (saveButton != null) saveButton.setDisable(false);
        if (backButton != null) backButton.setDisable(false);
    });
}

    /**
     * Checks if a specific type of interaction is currently allowed based on the Chao's state.
     * Denies interactions if Chao is DEAD, EVOLVING, or SLEEPING.
     * Restricts interactions if Chao is ANGRY (allowing only specific types).
     *
     * @param commandType A string representing the type of action being attempted (e.g., "PLAY", "FEED", "KEYPRESS").
     * @return true if the interaction is allowed, false otherwise.
     */
private boolean isInteractionAllowed(String commandType) {
    if (chao == null) return false;
    State currentState = chao.getState();

    // Deny based on state (DEAD, EVOLVING always block)
    if (currentState == State.DEAD || currentState == State.EVOLVING) {
        return false;
    }

    if (currentState == State.SLEEPING) {
        return false;
    }

    if (currentState == State.ANGRY) {
        return commandType.equalsIgnoreCase("PLAY") || commandType.equalsIgnoreCase("GIFT") || commandType.equalsIgnoreCase("KEYPRESS"); // Allow specific actions
    }

    if (commandType.equalsIgnoreCase("KEYPRESS")) {
        return true;
    }

    return true;
}

    /**
     * Displays a message indicating why an interaction was denied based on the Chao's current state.
     * Called by action methods when `isInteractionAllowed` returns false.
     *
     * @param commandType A string representing the type of action that was denied (used in the message).
     */
private void handleInteractionDenied(String commandType) {
    if (chao == null) return;
    State currentState = chao.getState();
    String reason = "";
    switch (currentState) {
        case DEAD: reason = chao.getName() + " is no longer with us..."; break;
        case SLEEPING: reason = chao.getName() + " is sleeping!"; break;
        case ANGRY: reason = chao.getName() + " is too angry to " + commandType.toLowerCase() + "!"; break;
        case EVOLVING: reason = chao.getName() + " is evolving!"; break;
        default: reason = "Interaction not allowed right now."; break;
    }
    displayMessage(reason, 1.5);
    // Optional: Play a "denied" sound effect
}
}

