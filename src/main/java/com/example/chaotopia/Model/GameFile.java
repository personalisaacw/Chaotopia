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

public class GameFile {
    // Directory for save files
    public static final String SAVES_DIR = "src/main/resources/com/example/chaotopia/Saves/";

    // Save slot properties
    private int slotId;
    private int MAX_SLOTS = 2;
    private Chao chao;
    private Inventory inventory;
    private Score score;

    // Game statistics
    private long playtime;
    private int numSessions;
    private long averagePlaytime;

    /**
     * Constructor for loading an existing game from a save slot
     * @param slotId The save slot to load (1-3)
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
     * Constructor for initializing a new game or updating an existing one.
     * @param slotId The save slot (1-3)
     * @param chao The Chao object (can be null)
     * @param inventory The Inventory object (can be null)
     * @param score The Score object (can be null)
     * @param playtime (Optional) Total playtime in ms (default: 0)
     * @param numSessions (Optional) Number of play sessions (default: 1)
     * @param averagePlaytime (Optional) Average playtime per session (default: 0)
     */
    public GameFile(int slotId, Chao chao, Inventory inventory, Score score,
                    Long playtime, Integer numSessions, Long averagePlaytime) {
        this.slotId = slotId;
        this.chao = chao;
        this.inventory = inventory;
        this.score = score;
        this.playtime = (playtime != null) ? playtime : 0L;
        this.numSessions = (numSessions != null) ? numSessions : 1;
        this.averagePlaytime = (averagePlaytime != null) ? averagePlaytime : 0L;
    }

    // Save game to file (COMPLETE)
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

        // Write to file if it exists and creates a new one if it doesn't
        try (FileWriter writer = new FileWriter(SAVES_DIR + "slot_" + slotId + ".json")) {
            writer.write(json.toString(4));
        }
    }

    // Load game from file
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


    // Delete a save file (COMPLETE)
    public static void deleteFile(int slotId) throws IOException {
        Path path = Paths.get(SAVES_DIR + "slot_" + slotId + ".json");
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    // Check if slot is empty (COMPLETE)
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
            return true; // If corrupted, treat it as true
        }
    }


    // Getters and setters
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

}