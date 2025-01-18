package com.boydti.fawe.util;
import com.boydti.fawe.Fawe;
import com.boydti.fawe.FaweCache;
import com.boydti.fawe.object.brush.BrushSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
public final class BrushCache {
    private static final WeakHashMap<Object, BrushTool> brushCache = new WeakHashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final CompoundTag getNBT(BaseItem item) {
        return item.hasNbtData() ? item.getNbtData() : null;
    }
    private static final Object getKey(BaseItem item) {
        return item.getNativeItem();
    }
    private static final ThreadLocal<Boolean> RECURSION = new ThreadLocal<>();
    public static final BrushTool getTool(Player player, LocalSession session, BaseItem item) {
        if (!item.hasNbtData()) return null;
        Object key = getKey(item);
        if (key == null) return null;
        BrushTool cached = brushCache.get(key);
        if (cached != null) return cached;
        CompoundTag nbt = item.getNbtData();
        if (nbt == null) return null;
        StringTag json = (StringTag) nbt.getValue().get("weBrushJson");
        if (json != null) {
            try {
                if (RECURSION.get() != null) return null;
                RECURSION.set(true);
                BrushTool tool = BrushTool.fromString(player, session, json.getValue());
                tool.setHolder(item);
                brushCache.put(key, tool);
                return tool;
            } catch (Throwable ignore) {
                ignore.printStackTrace();
                Fawe.debug("Invalid brush for " + player + " holding " + item.getType() + ": " + json.getValue());
                if (item != null) {
                    item.setNbtData(null);
                    brushCache.remove(key);
                }
            } finally {
                RECURSION.remove();
            }
        }
        return null;
    }
    public static BrushTool getCachedTool(BaseItem item) {
        Object key = getKey(item);
        if (key != null) return brushCache.get(key);
        return null;
    }
    public static final BrushTool setTool(BaseItem item, BrushTool tool) {
        if (item.getNativeItem() == null) return null;
        CompoundTag nbt = item.getNbtData();
        Map<String, Tag> map;
        if (nbt == null) {
            if (tool == null) {
                Map<String, Object> raw = nbt.getRaw();
                if (raw.remove("weBrushJson") != null && raw.isEmpty()) {
                    item.setNbtData(null);
                }
                return null;
                item.setNbtData(null);
                return tool;
            }
            nbt = new CompoundTag(map = new HashMap<>());
        } else {
}
