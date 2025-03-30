package com.example.chaotopia.Model;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a game save file with all associated data including Chao, inventory,
 * scores, and game statistics. Handles loading, saving, and managing game save slots.
 */
public class GameFile {
    /** Directory for save files */
    public static final String SAVES_DIR = "src/main/resources/com/example/chaotopia/Saves/";

    /** Maximum number of save slots available */
    private static final int MAX_SLOTS = 2;

    private int slotId;
    private Chao chao;
    private Inventory inventory;
    private Score score;
    private long playtime;
    private int numSessions;
    private long averagePlaytime;

    // Static methods ----------------------------------------------------------

    /**
     * Deletes the save file for the specified slot.
     *
     * @param slotId The slot number to delete (1-MAX_SLOTS)
     * @throws IOException If there's an error deleting the file
     */
    public static void deleteFile(int slotId) throws IOException {
        Path path = Paths.get(SAVES_DIR + "slot_" + slotId + ".json");
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * Checks if a slot is empty or contains no valid save data.
     *
     * @param slotId The slot number to check (1-MAX_SLOTS)
     * @return true if the slot is empty or contains invalid data, false otherwise
     * @throws IOException If there's an error reading the file
     */
    public static boolean isEmptySlot(int slotId) throws IOException {
        Path path = Paths.get(SAVES_DIR + "slot_" + slotId + ".json");

        // If file doesn't exist, slot is empty
        if (!Files.exists(path)) {
            return true;
        }

        // If file exists but can't be read as JSON, treat as empty
        try {
            String content = Files.readString(path);
            return !new JSONObject(content).optBoolean("ActiveSlot", false);
        } catch (JSONException e) {
            return true; // If corrupted, treat it as empty
        }
    }

    // Constructors ------------------------------------------------------------

    /**
     * Constructs a GameFile by loading data from the specified save slot.
     *
     * @param slotId The save slot to load (1-MAX_SLOTS)
     * @throws IOException If the save file cannot be read
     * @throws JSONException If the save data is corrupted
     * @throws IllegalArgumentException If slotId is invalid
     */
    public GameFile(int slotId) throws IOException, JSONException {
        if (slotId < 0 || slotId > MAX_SLOTS) {
            throw new IllegalArgumentException("Slot ID must be between 0 and " + MAX_SLOTS);
        }
        this.slotId = slotId;
        this.load();
    }

    /**
     * Constructs a new GameFile with the specified data, typically for a new game or updating an existing one.
     *
     * @param slotId The save slot (1-MAX_SLOTS)
     * @param chao The Chao object (can be null)
     * @param inventory The Inventory object (can be null)
     * @param score The Score object (can be null)
     * @param playtime Total playtime in ms (default: 0)
     * @param numSessions Number of play sessions (default: 0)
     * @param averagePlaytime Average playtime per session (default: 0)
     */
    public GameFile(int slotId, Chao chao, Inventory inventory, Score score,
                    Long playtime, Integer numSessions, Long averagePlaytime) {
        this.slotId = slotId;
        this.chao = chao;
        this.inventory = inventory;
        this.score = score;
        this.playtime = (playtime != null) ? playtime : 0L;
        this.numSessions = (numSessions != null) ? numSessions : 0;
        this.averagePlaytime = (averagePlaytime != null) ? averagePlaytime : 0L;
    }

    // Public instance methods -------------------------------------------------

    /**
     * Saves the current game state to the assigned slot.
     *
     * @throws IOException If there's an error writing the file
     */
    public void save() throws IOException {
        JSONObject json = new JSONObject();

        // Game metadata
        json.put("ActiveSlot", true)
                .put("SlotId", slotId)
                .put("playtime", playtime)
                .put("numSessions", numSessions)
                .put("averagePlaytime", averagePlaytime);

        // Chao Information
        if (chao != null) {
            json.put("chao", new JSONObject()
                    .put("name", chao.getName())
                    .put("type", chao.getType().toString())
                    .put("status", new JSONObject()
                            .put("happiness", chao.getStatus().getHappiness())
                            .put("health", chao.getStatus().getHealth())
                            .put("fullness", chao.getStatus().getFullness())
                            .put("sleep", chao.getStatus().getSleep()))
                    .put("state", chao.getState().toString())
                    .put("alignment", chao.getAlignment()));
        }

        // Inventory Information
        if (inventory != null) {
            json.put("inventory", new JSONObject(inventory.getItems()));
        }

        // Score
        if (score != null) {
            json.put("score", score.getScore());
        }

        // Ensure directory exists
        Files.createDirectories(Paths.get(SAVES_DIR));

        // Write to file
        try (FileWriter writer = new FileWriter(SAVES_DIR + "slot_" + slotId + ".json")) {
            writer.write(json.toString(4));
        }
    }

    // Getters and setters -----------------------------------------------------

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long newPlaytime) {
        playtime = newPlaytime;
    }

    public int getNumSessions() {
        return numSessions;
    }

    public void setNumSessions(int newNumSessions) {
        numSessions = newNumSessions;
    }

    public Chao getChao() {
        return this.chao;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Score getScore() {
        return this.score;
    }

    // Private methods ---------------------------------------------------------

    /**
     * Loads game data from the assigned slot's save file.
     *
     * @throws IOException If the file cannot be read
     * @throws JSONException If the data is corrupted
     */
    private void load() throws IOException, JSONException {
        Path path = Paths.get(SAVES_DIR + "slot_" + slotId + ".json");
        if (!Files.exists(path)) {
            throw new IOException("Save file not found for slot " + slotId);
        }

        String content = new String(Files.readAllBytes(path));
        JSONObject json = new JSONObject(content);

        // Load game stats
        this.playtime = json.getLong("playtime");
        this.numSessions = json.getInt("numSessions");
        this.averagePlaytime = json.getLong("averagePlaytime");

        // Load chao
        if (json.has("chao")) {
            JSONObject chaoJson = json.getJSONObject("chao");

            // Load basic properties
            String name = chaoJson.getString("name");
            ChaoType type = ChaoType.valueOf(chaoJson.getString("type"));
            State state = State.valueOf(chaoJson.getString("state"));
            int alignment = chaoJson.getInt("alignment");
            JSONObject statusJson = chaoJson.getJSONObject("status");
            Status status = new Status(
                    statusJson.getInt("happiness"),
                    statusJson.getInt("health"),
                    statusJson.getInt("fullness"),
                    statusJson.getInt("sleep")
            );
            this.chao = new Chao(alignment, name, type, state, status);
        }

        // Load inventory
        if (json.has("inventory")) {
            JSONObject inventoryJson = json.getJSONObject("inventory");
            Map<String, Integer> items = new HashMap<>();
            for (String key : inventoryJson.keySet()) {
                items.put(key, inventoryJson.getInt(key));
            }
            this.inventory = new Inventory(items);
        }

        // Load score
        if (json.has("score")) {
            this.score = new Score(json.getInt("score"));
        }
    }
}