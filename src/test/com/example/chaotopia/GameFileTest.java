package com.example.chaotopia;

import com.example.chaotopia.Model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GameFile} class functionality including save/load operations,
 * slot management, and edge case handling.
 *
 * <p>This test class verifies the following functionality:
 * <ul>
 *   <li>Round-trip saving and loading of game data</li>
 *   <li>Handling of partial game data</li>
 *   <li>Slot management operations (checking empty slots, deleting files)</li>
 *   <li>Edge cases like empty data and corrupted files</li>
 * </ul>
 *
 * <p>The tests use JUnit 5 features including:
 * <ul>
 *   <li>Temporary directories for file operations</li>
 *   <li>Parameterized test methods</li>
 *   <li>Setup/cleanup methods</li>
 * </ul>
 *
 * @see GameFile
 * @see Chao
 * @see Inventory
 * @see Score
 */
class GameFileTest {
    /**
     * Temporary directory provided by JUnit for file operations during tests.
     * All files created in this directory are automatically cleaned up after tests.
     */
    @TempDir
    static Path tempDir;

    /** Test Chao instance used across multiple test cases */
    private Chao testChao;
    /** Test Inventory instance used across multiple test cases */
    private Inventory testInventory;
    /** Test Score instance used across multiple test cases */
    private Score testScore;
    /** Maximum number of save slots to test with */
    private int MAX_SLOTS = 2;

    /**
     * Sets up test fixtures before each test method.
     * Initializes:
     * <ul>
     *   <li>A test Chao with sample attributes</li>
     *   <li>A test Inventory with sample items</li>
     *   <li>A test Score with sample value</li>
     * </ul>
     */
    @BeforeEach
    void setup() {
        Status chaoStatus = new Status(80, 90, 70, 60);
        testChao = new Chao(50, "TestChao", ChaoType.RED, State.NORMAL, chaoStatus);

        Map<String, Integer> items = new HashMap<>();
        items.put("Fruit", 5);
        items.put("Toy", 2);
        testInventory = new Inventory(items);

        testScore = new Score(1000);
    }

    /**
     * Cleans up after all tests in the class have completed.
     * Currently performs no operations as temporary directory cleanup is automatic.
     */
    @AfterAll
    static void cleanupAll() {
        // No cleanup needed
    }

    /**
     * Cleans up test files after each test method execution.
     * Deletes any test save files created in the default directory.
     *
     * @throws IOException if there's an error deleting test files
     */
    @AfterEach
    void cleanup() throws IOException {
        // Clean up test files in the default directory
        for (int i = 1; i <= MAX_SLOTS; i++) {
            Path path = Paths.get(GameFile.SAVES_DIR + "slot_" + i + ".json");
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    /**
     * Helper method to create a test GameFile instance with predefined values.
     *
     * @param slotId the save slot ID to use
     * @return a configured GameFile instance
     * @throws IOException if there's an error creating directories
     */
    private GameFile createTestGameFile(int slotId) throws IOException {
        Files.createDirectories(Paths.get(GameFile.SAVES_DIR));

        return new GameFile(slotId, testChao, testInventory, testScore,
                3600000L, 5, 720000L);
    }

    // Save/Load Tests --------------------------------------------------------

    /**
     * Tests the complete round-trip of saving and loading game data.
     * Verifies that all saved data matches the loaded data including:
     * <ul>
     *   <li>Playtime and session count</li>
     *   <li>Chao attributes (name, type, alignment)</li>
     *   <li>Inventory items</li>
     *   <li>Score value</li>
     * </ul>
     *
     * @throws Exception if any save/load operation fails
     */
    @Test
    void saveAndLoad_RoundTrip_DataMatches() throws Exception {
        // Create and save game
        GameFile original = createTestGameFile(1);
        original.save();

        // Load game
        GameFile loaded = new GameFile(1);

        // Verify all data matches
        assertEquals(original.getPlaytime(), loaded.getPlaytime());
        assertEquals(original.getNumSessions(), loaded.getNumSessions());

        Chao loadedChao = loaded.getChao();
        assertNotNull(loadedChao);
        assertEquals(testChao.getName(), loadedChao.getName());
        assertEquals(testChao.getType(), loadedChao.getType());
        assertEquals(testChao.getAlignment(), loadedChao.getAlignment());

        Inventory loadedInventory = loaded.getInventory();
        assertNotNull(loadedInventory);
        assertEquals(testInventory.getItems(), loadedInventory.getItems());

        assertEquals(testScore.getScore(), loaded.getScore().getScore());
    }

    /**
     * Tests saving and loading with partial game data.
     * Verifies that null values for inventory and score are handled correctly.
     *
     * @throws Exception if any save/load operation fails
     */
    @Test
    void save_PartialData_SavesCorrectly() throws Exception {
        // Save with only some data
        GameFile partialGame = new GameFile(2, testChao, null, null, 1800000L, 3, 600000L);
        partialGame.save();

        // Load and verify
        GameFile loaded = new GameFile(2);

        assertNotNull(loaded.getChao());
        assertNull(loaded.getInventory());
        assertNull(loaded.getScore());
        assertEquals(1800000L, loaded.getPlaytime());
        assertEquals(3, loaded.getNumSessions());
    }

    // Slot Management Tests --------------------------------------------------

    /**
     * Tests that non-existent slots are correctly identified as empty.
     *
     * @throws Exception if the slot check fails
     */
    @Test
    void isEmptySlot_NonExistentSlot_ReturnsTrue() throws Exception {
        assertTrue(GameFile.isEmptySlot(1));
    }

    /**
     * Tests that existing slots are correctly identified as not empty.
     *
     * @throws Exception if the slot check fails
     */
    @Test
    void isEmptySlot_ExistingSlot_ReturnsFalse() throws Exception {
        new GameFile(1, testChao, null, null, 0L, 0, 0L).save();
        assertFalse(GameFile.isEmptySlot(1));
    }

    /**
     * Tests that corrupted save files are treated as empty slots.
     *
     * @throws Exception if the slot check fails
     */
    @Test
    void isEmptySlot_CorruptedFile_ReturnsTrue() throws Exception {
        Path path = Paths.get(GameFile.SAVES_DIR + "slot_1.json");
        Files.writeString(path, "invalid json data");
        assertTrue(GameFile.isEmptySlot(1));
    }

    /**
     * Tests that existing save files can be successfully deleted.
     *
     * @throws Exception if the delete operation fails
     */
    @Test
    void deleteFile_ExistingFile_DeletesFile() throws Exception {
        new GameFile(1, testChao, null, null, 0L, 0, 0L).save();
        Path path = Paths.get(GameFile.SAVES_DIR + "slot_1.json");
        assertTrue(Files.exists(path));

        GameFile.deleteFile(1);
        assertFalse(Files.exists(path));
    }

    /**
     * Tests that attempting to delete non-existent files doesn't throw exceptions.
     */
    @Test
    void deleteFile_NonExistentFile_DoesNothing() throws Exception {
        Path path = Paths.get(GameFile.SAVES_DIR + "slot_1.json");
        assertFalse(Files.exists(path));

        assertDoesNotThrow(() -> GameFile.deleteFile(1));
    }

    // Edge Case Tests --------------------------------------------------------

    /**
     * Tests that game files with all null data can be saved and loaded.
     *
     * @throws Exception if any save/load operation fails
     */
    @Test
    void save_EmptyData_SavesSuccessfully() throws Exception {
        GameFile emptyGame = new GameFile(1, null, null, null, 0L, 0, 0L);
        assertDoesNotThrow(emptyGame::save);

        GameFile loaded = new GameFile(1);
        assertNull(loaded.getChao());
        assertNull(loaded.getInventory());
        assertNull(loaded.getScore());
    }

    /**
     * Tests that loading corrupted JSON data throws the expected exception.
     *
     * @throws Exception if file creation fails
     */
    @Test
    void load_CorruptedFile_ThrowsJSONException() throws Exception {
        Path path = Paths.get(GameFile.SAVES_DIR + "slot_1.json");
        Files.writeString(path, "invalid json data");

        assertThrows(JSONException.class, () -> new GameFile(1));
    }
}