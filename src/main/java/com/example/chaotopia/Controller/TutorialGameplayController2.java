package com.example.chaotopia.Controller;

import com.example.chaotopia.Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Controller for the main gameplay screen during Tutorial.
 * Manages game logic, UI updates, animations, and Chao state.
 * For full details on methods, please see GameplayController.
 */
public class TutorialGameplayController2 extends BaseController implements Initializable {

    // --- FXML Injections from Gameplay.fxml ---
    @FXML private BorderPane mainContainer;
    @FXML private StackPane centerStackPane; // Container for fruit, messages, game over

    // Top Bar
    @FXML private Button backButton;
    @FXML private Label timeLabel; // TODO: Implement timer logic if needed
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

    // --- Timelines ---
    private Timeline statDecayTimeline;
    private Timeline stateMonitorTimeline;
    private Timeline sleepIncreaseTimeline;
    private Timeline tempAnimationTimer;    // For temporary states like HAPPY
    private Timeline messageTimeline;       // For hiding messages
    private Timeline evolutionTimeline;     // For evolution sequences

    // --- State Management ---
    private boolean isSleeping = false;
    private State previousState = State.NORMAL;
    private Node gameOverOverlay = null; // Reference to the game over screen
    private Random random = new Random(); //for random starting type. remove once choose chao

    private int tutorialStep;
    @FXML private StackPane step1;
    @FXML private StackPane step2;
    @FXML private StackPane step3;
    @FXML private StackPane step4;
    @FXML private StackPane step5;
    @FXML private StackPane step6;
    @FXML private StackPane step7;
    @FXML private StackPane step8;
    @FXML private StackPane step9;
    @FXML private StackPane extraStep;
    @FXML private StackPane extraStep2;
    @FXML private StackPane step10;
    @FXML private StackPane step11;
    @FXML private StackPane step12;
    @FXML private StackPane step13;
    @FXML private StackPane step14;
    @FXML private StackPane step15;
    private EventHandler<MouseEvent> tutorialClickHandler;
    // --- Initialization ---

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tutorialClickHandler = event -> step();
        mainContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
        inventory = new Inventory();
        score = new Score(0);
        inventoryUIMap = new HashMap<>();
        inventoryButtonsOrdered = new ArrayList<>();

        saveButton.setDisable(true);
        playButton.setDisable(true);
        sleepButton.setDisable(true);
        exerciseButton.setDisable(true);
        vetButton.setDisable(true);
        petButton.setDisable(true);
        bonkButton.setDisable(true);



        if (fruitImageView != null) {
            fruitAnimation = new FruitAnimation(fruitImageView, FruitType.RED);
            fruitImageView.setVisible(false);
        } else System.err.println("FXML Warning: fruitImageView is null.");

        // --- Load Game Data (or create default) ---
        loadOrCreateChao(); // Now uses random basic type
        initializeInventoryUIMap();
        populateOrderedInventoryButtons();

        if (isNewGameCondition()) {
            addDefaultInventory(); // Populate the inventory data model
        } else {
        }

        updateScoreUI(score.getScore());
        updateNameLabel();
        updateStatusBars();
        setupTimelines();
        startTimelines();
        updateProfileChaoImage();
        updateInventoryDisplay();

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

    private boolean isNewGameCondition() {
        return inventory.isEmpty();
    }

    private void populateOrderedInventoryButtons() {

        inventoryButtonsOrdered.clear(); // Ensure list is empty before adding
        inventoryButtonsOrdered.add(redFruitButton);   // 1
        inventoryButtonsOrdered.add(blueFruitButton);  // 2
        inventoryButtonsOrdered.add(greenFruitButton); // 3
        inventoryButtonsOrdered.add(heroFruitButton);  // 4
        inventoryButtonsOrdered.add(darkFruitButton);  // 5
        inventoryButtonsOrdered.add(trumpetButton);    // 6
        inventoryButtonsOrdered.add(duckButton);       // 7
        inventoryButtonsOrdered.add(tvButton);         // 8

    }

    private void setupKeybindings(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
        System.out.println("Keybindings setup on scene."); // Debug message
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.S) {
            saveGame();
            event.consume(); // Indicate the event has been handled
            return;
        }
        if (event.getCode() == KeyCode.M) {
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
                    // Optionally display a message or play a sound
                }
            }
            return; // Handled (or attempted to handle) inventory key
        }

        if (!isInteractionAllowed("KEYPRESS")) { // Use a generic type or check within methods
            System.out.println("Interaction denied by keypress due to Chao state.");
            // Optionally display a message using handleInteractionDenied

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
    //random chao picker
    private ChaoType getRandomBasicChaoType() {
        ChaoType[] basicTypes = {ChaoType.BLUE, ChaoType.RED, ChaoType.GREEN};
        return basicTypes[random.nextInt(basicTypes.length)];
    }



    private void loadOrCreateChao() {
        // TODO: Replace with actual load logic
        System.out.println("Creating default Chao...");
        Status initialStatus = new Status(100, 100, 100, 100);
        ChaoType startingType = getRandomBasicChaoType();
        this.chao = new Chao(0, "Bubbles", ChaoType.RED, State.NORMAL, initialStatus);
        this.isSleeping = false;

        // Initialize or update main Chao Animation
        if (chaoImageView != null) {
            if (chaoAnimation == null) { // First time creation
                chaoAnimation = new ChaoAnimation(chaoImageView, chao.getType(), AnimationState.fromState(chao.getState()));
                chaoAnimation.startAnimation();
            } else { // Update existing animation instance
                syncChaoAnimationToType(chao.getType());
                syncChaoAnimationToState(chao.getState(), true);
            }
        } else {
            System.err.println("CRITICAL: chaoImageView is null during Chao creation.");
        }
    }

    private void addDefaultInventory() {
        System.out.println("Adding default inventory items...");
        inventory.addItem("Red Fruit", 0);
        inventory.addItem("Blue Fruit", 0);
        inventory.addItem("Green Fruit", 0);
        inventory.addItem("Hero Fruit", 0);
        inventory.addItem("Dark Fruit", 0);
        inventory.addItem("Trumpet", 0);
        inventory.addItem("Duck", 0);
        inventory.addItem("T.V.", 0);
    }

    private void setupTimelines() {
        // Stat Decay (e.g., every 10 seconds)
//        statDecayTimeline = new Timeline(
//                new KeyFrame(Duration.seconds(2), e -> decreaseStats())
//        );
//        statDecayTimeline.setCycleCount(Timeline.INDEFINITE);

        // Frequent State Monitor (e.g., every 250ms)
        stateMonitorTimeline = new Timeline(
                new KeyFrame(Duration.millis(250), e -> monitorChaoState())
        );
        stateMonitorTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimelines() {
        if (stateMonitorTimeline != null) stateMonitorTimeline.play();
    }

    private void updateProfileChaoImage() {
        if (profileChaoImageView == null || chao == null) {
            return; // Cannot update if view or chao is missing
        }

        // Load the SIT1.png for the current Chao type
        // Uses the AnimationState.SIT resource name logic
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

    @FXML
    public void playChao() {
        if (isInteractionAllowed("PLAY")) {
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
        tutorialStep++;
        if(tutorialStep == 10){

            handlingTutorialSteps(tutorialStep);
        }
    }

    @FXML
    public void sleepChao() {
        if (isInteractionAllowed("SLEEP")) {
            Commands.sleep(chao);
            if (chao.getState() == State.SLEEPING) {
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

    @FXML
    public void exerciseChao() {
        if (isInteractionAllowed("EXERCISE")) {
            Commands.exercise(chao);
            updateStatusBars();
            score.updateScore(10);
            updateScoreUI(score.getScore());
            showHappyAnimation();
        }
    }

    @FXML
    public void vetChao() {
        if (isInteractionAllowed("VET")) {
            String commandResult = Commands.vet(chao);
            if (commandResult != null) { // Vet returns message if already healthy
                displayMessage(commandResult, 2.0);
                return; // Don't proceed if message shown
            }
            // If vet was needed (no message returned)
            updateStatusBars();
            score.updateScore(-20); // Cost for vet visit
            updateScoreUI(score.getScore());
            showHappyAnimation(); // Happy after being healed
        }
    }

    @FXML
    public void petChao() {
        if (isInteractionAllowed("PET")) {
            int previousAlignment = chao.getAlignment();
            Commands.pet(chao);
            updateStatusBars();

            // Check for Hero evolution only if it's a basic type
            if (previousAlignment < 7 && chao.getAlignment() >= 7) {
                triggerEvolution(true); // True for Hero
            } else {
                score.updateScore(3);
                updateScoreUI(score.getScore());
                showHappyAnimation();
            }
        }
    }

    @FXML
    public void bonkChao() {
        if (isInteractionAllowed("BONK")) {
            int previousAlignment = chao.getAlignment();
            Commands.bonk(chao);
            updateStatusBars();
            score.updateScore(-3);
            updateScoreUI(score.getScore());

            // Check for Dark evolution
            if (previousAlignment > -7 && chao.getAlignment() <= -7) {
                triggerEvolution(false); // False for Dark
            } else {
                showTemporaryStateAnimation(AnimationState.HUNGRY, 1.0);
            }
        }
    }

    // --- Item/Fruit Actions ---

    @FXML public void giftTrumpet() { giftItem("Trumpet"); tutorialStep++; if(tutorialStep == 9) {handlingTutorialSteps(tutorialStep);}}
    @FXML public void giftDuck() { giftItem("Duck"); tutorialStep++; if(tutorialStep == 9) {handlingTutorialSteps(tutorialStep);}}
    @FXML public void giftTV() { giftItem("T.V."); tutorialStep++; if(tutorialStep == 9) {handlingTutorialSteps(tutorialStep);}} // Ensure item name matches inventory key

    @FXML public void feedRedFruit() { feedFruit("Red Fruit", FruitType.RED, false); tutorialStep++; handlingTutorialSteps(tutorialStep); }
    @FXML public void feedBlueFruit() { feedFruit("Blue Fruit", FruitType.BLUE, false); }
    @FXML public void feedGreenFruit() { feedFruit("Green Fruit", FruitType.GREEN, false); } // Assuming GREEN exists
    @FXML public void feedHeroFruit() { feedFruit("Hero Fruit", FruitType.HERO, true); }
    @FXML public void feedDarkFruit() { feedFruit("Dark Fruit", FruitType.DARK, true); }

    private void giftItem(String itemName) {
        if (!isInteractionAllowed("GIFT")) {
            handleInteractionDenied("GIFT"); return;
        }
        if (inventory.getItemCount(itemName) <= 0) {
            displayMessage("No " + itemName + " available!", 1.5);
            // TODO: Play sound effect
            return;
        }

        Commands.give(chao, new Item(itemName));
        inventory.removeItem(itemName);
        updateInventoryDisplay();

        updateStatusBars();
        score.updateScore(10);
        updateScoreUI(score.getScore());
        showHappyAnimation();
        displayMessage(chao.getName() + " liked the " + itemName + "!", 1.5);
    }


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
            showHappyAnimation();
        }
        updateStatusBars();
    }


    // --- Evolution Logic ---

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

    private void updateNameLabel() {
        if (nameLabel != null && chao != null) {
            nameLabel.setText(chao.getName());
        }
    }

    private void updateScoreUI(int newScore) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + newScore);
        }
    }

    private void handlingTutorialSteps(int tutorialStep) {
        switch(tutorialStep){
            case 1:
                step1.setOpacity(0);
                step2.setOpacity(1);
                break;
            case 2:
                step2.setOpacity(0);
                step3.setOpacity(1);
                break;
            case 3:
                step3.setOpacity(0);
                step4.setOpacity(1);
                break;
            case 4:
                step4.setOpacity(0);
                step5.setOpacity(1);
                break;
            case 5:
                mainContainer.removeEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
                step5.setOpacity(0);
                step6.setOpacity(1);
                inventory.addItem("Red Fruit", 1);
                updateInventoryDisplay();
                fullnessBar.setProgress(50 / 100.0);
                fullnessLabel.setText("50");
                chao.getStatus().setStats(100, 100, 50, 100);
                monitorChaoState();
                break;
            case 6:
                step6.setOpacity(0);
                step7.setOpacity(1);
                inventory.addItem("Trumpet", 1);
                inventory.addItem("Duck", 1);
                inventory.addItem("T.V.", 1);
                updateInventoryDisplay();

                happinessBar.setProgress(0 / 100.0);
                happinessLabel.setText("0");
                chao.getStatus().setStats(0, 100, 100, 100);
                monitorChaoState();
                break;
            case 9:
                playButton.setDisable(false);
                step7.setOpacity(0);
                extraStep.setOpacity(1);
                break;
            case 10:

                extraStep.setOpacity(0);
                playButton.setDisable(true);
                step8.setOpacity(1);
                mainContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
                break;
            case 11:
                step8.setOpacity(0);
                step9.setOpacity(1);
                break;
            case 12:
                mainContainer.removeEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
                step9.setOpacity(0);
                step10.setOpacity(1);
                sleepBar.setProgress(0 / 100.0);
                sleepLabel.setText("0");
                chao.getStatus().setStats(85, 100, 100, 0);
                monitorChaoState();
                saveButton.setDisable(true);
                break;
            case 13:
                mainContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
                step10.setOpacity(0);
                extraStep2.setOpacity(1);
                break;
            case 14:

                extraStep2.setOpacity(0);
                step11.setOpacity(1);
                break;
            case 15:
                step11.setOpacity(0);
                step12.setOpacity(1);
                break;
            case 16:
                step12.setOpacity(0);
                step13.setOpacity(1);
                break;
            case 17:
                step13.setOpacity(0);
                step14.setOpacity(1);
                break;
            case 18:
                step14.setOpacity(0);
                step15.setOpacity(1);
                mainContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, tutorialClickHandler);
                break;



        }
    }


    public void goBack3Steps(ActionEvent e) {
        popStack();
        popStack();
        goBack(e);
    }

    @FXML
    public void step(){
        tutorialStep++;
        handlingTutorialSteps(tutorialStep);
    }

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
    private void handleDeath() {
        chao.setState(State.DEAD);
        stopTimelines();

        if (chaoAnimation != null) {
            chaoAnimation.changeState(State.DEAD);
        }
        showGameOverScreen(); // This disables interactions
    }

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

    private void syncChaoAnimationToType(ChaoType newType) {
        if (chaoAnimation != null) {
            chaoAnimation.changeChaoType(newType); // This stops/starts animation already
        }
    }

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
                            displayMessage(chao.getName() + " woke up!",4);
                            isSleeping = false; // *** SET isSleeping TO FALSE ***
                            // Determine new state *after* waking up
                            monitorChaoState(); // This should set state to NORMAL/HUNGRY etc.
                            //enableAllInteractions(true); // *** ENABLE INTERACTIONS ***
                            tutorialStep++;
                            handlingTutorialSteps(tutorialStep);
                            System.out.println("Interactions enabled after waking up."); // Debug log
                        } else if (!isSleeping) {
                            System.out.println("Sleep increase stopped because isSleeping is false."); // Debug log
                            enableAllInteractions(true); // Ensure enabled if stopped externally
                        }
                    }
                })
        );
        sleepIncreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        sleepIncreaseTimeline.play();
    }

    private void monitorChaoState() {
        if (chao == null || chao.getState() == State.DEAD || chao.getState() == State.EVOLVING) return;

        Status status = chao.getStatus();
        State currentState = chao.getState();

        boolean isTempStateRunning = (tempAnimationTimer != null &&
                tempAnimationTimer.getStatus() == Timeline.Status.RUNNING);

        // Allow monitoring if we're angry, even during temp animations
        if (isTempStateRunning && chao.getState() != State.ANGRY &&
                (currentState == State.HAPPY || currentState == State.HUNGRY)) {
            return; // Exit the monitor early
        }

        // --- If not in a temporary animation, determine correct state ---
        State determinedState = currentState; // Start with current

        if (isSleeping) {
            determinedState = State.SLEEPING;
            // Wake up handled by sleepIncreaseTimeline
        } else if (currentState == State.SLEEPING) {
            // Just woke up logic...
            if (status.getHappiness() <= 0) determinedState = State.ANGRY;
            else if (status.getFullness() <= 0) determinedState = State.HUNGRY;
            else determinedState = State.NORMAL;
        } else if (status.getSleep() <= 0) {
            displayMessage(chao.getName() + " fell asleep from exhaustion!",4);
            status.adjustHealth(-15);
            determinedState = State.SLEEPING;
            chao.setState(State.SLEEPING);    // Set logical state immediately
            isSleeping = true;
            syncChaoAnimationToState(State.SLEEPING, true);
            enableAllInteractions(false);
            startSleepIncrease();
            updateStatusBars();
            return; // Exit early
        } else if (status.getHappiness() <= 0 || (currentState == State.ANGRY && status.getHappiness() < 50)) {
            determinedState = State.ANGRY;
        } else if (status.getFullness() <= 0) {
            determinedState = State.HUNGRY;
        } else {
            determinedState = State.NORMAL;
        }

        if (determinedState != currentState) {
            chao.setState(determinedState);
            syncChaoAnimationToState(determinedState, true);
        }
    }

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
                buttonBox.setPadding(new Insets(15, 0, 0, 0));

                Button newGameButton = new Button("Play Again?");
                // Apply button styling from CSS if possible, or inline
                newGameButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 16px; " +
                        "-fx-padding: 10 20; -fx-background-radius: 8; -fx-border-color: #DEB887; -fx-border-radius: 8;");
                newGameButton.setOnAction(e -> startNewGame());

                Button mainMenuButton = new Button("Main Menu");
                mainMenuButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-family: 'Upheaval TT -BRK-'; -fx-font-size: 16px; " +
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

    private void removeGameOverScreen() {
        if (gameOverOverlay != null && centerStackPane != null) {
            centerStackPane.getChildren().remove(gameOverOverlay);
            gameOverOverlay = null;
        }
    }

    private void startNewGame() {
        System.out.println("Starting new game...");
        removeGameOverScreen();

        // Reset score, inventory, create new Chao
        score = new Score(0);
        inventory = new Inventory();
        addDefaultInventory(); // Add default items again
        loadOrCreateChao();    // Create a fresh Chao

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

    @FXML
    public void saveGame() {
        // TODO: Implement actual saving mechanism (e.g., to JSON)
        System.out.println("Attempting to save game...");
        // Example: SaveManager.save(chao, inventory, score);
        displayMessage("Game Saved! (Placeholder)", 2.0);
    }

    @FXML
    public void goToMainMenu() {
        // TODO: Implement navigation back to the main menu scene
        System.out.println("Returning to Main Menu... (Implement Navigation)");
        displayMessage("Returning to Menu...", 1.5);
        // Example: SceneManager.loadScene("MainMenu.fxml", mainContainer.getScene());
        // Make sure to call shutdown() before switching scenes if necessary
        shutdown();
    }

    public void shutdown() {
        stopTimelines();
        if (chaoAnimation != null) {
            chaoAnimation.stopAnimation();
        }
        if (fruitAnimation != null) fruitAnimation.stopAnimation();
    }

    private void stopTimelines() {
        if (statDecayTimeline != null) statDecayTimeline.stop();
        if (stateMonitorTimeline != null) stateMonitorTimeline.stop();
        if (sleepIncreaseTimeline != null) sleepIncreaseTimeline.stop();
        if (tempAnimationTimer != null) tempAnimationTimer.stop();
        if (messageTimeline != null) messageTimeline.stop();
        if (evolutionTimeline != null) evolutionTimeline.stop();
    }

    // --- Interaction Checks and Helpers ---

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
            //if (saveButton != null) saveButton.setDisable(false);
            //if (backButton != null) backButton.setDisable(false);
        });
    }

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