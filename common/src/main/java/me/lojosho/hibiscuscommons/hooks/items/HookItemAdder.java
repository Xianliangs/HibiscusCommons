package me.lojosho.hibiscuscommons.hooks.items;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.Events.PlayerEmoteEndEvent;
import dev.lone.itemsadder.api.Events.PlayerEmotePlayEvent;
import me.lojosho.hibiscuscommons.api.events.HibiscusHookReload;
import me.lojosho.hibiscuscommons.api.events.HibiscusPlayerEmoteEndEvent;
import me.lojosho.hibiscuscommons.api.events.HibiscusPlayerEmotePlayEvent;
import me.lojosho.hibiscuscommons.hooks.Hook;
import me.lojosho.hibiscuscommons.hooks.HookFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that integrates the plugin {@link dev.lone.itemsadder.api.ItemsAdder ItemsAdder} to provide custom items
 */
@SuppressWarnings("SpellCheckingInspection")
public class HookItemAdder extends Hook {
    private boolean enabled = false;

    public HookItemAdder() {
        super("itemsadder", HookFlag.ITEM_SUPPORT);
    }

    /**
     * Gets a cosmetic {@link ItemStack} that is associated with the provided id from the plugin {@link dev.lone.itemsadder.api.ItemsAdder ItemsAdder}
     */
    @Override
    public ItemStack getItem(@NotNull String itemId) {
        if (enabled) {
            CustomStack stack = CustomStack.getInstance(itemId);
            if (stack == null) return null;
            return stack.getItemStack().clone();
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemAdderDataLoad(ItemsAdderLoadDataEvent event) {
        HibiscusHookReload.ReloadType reloadType = enabled ? HibiscusHookReload.ReloadType.RELOAD : HibiscusHookReload.ReloadType.INITIAL;
        this.enabled = true;
        HibiscusHookReload newEvent = new HibiscusHookReload(this, reloadType);
        Bukkit.getPluginManager().callEvent(newEvent);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEmotePlay(PlayerEmotePlayEvent event) {
        HibiscusPlayerEmotePlayEvent newEvent = new HibiscusPlayerEmotePlayEvent(this, event.getPlayer(), event.getEmoteName());
        Bukkit.getPluginManager().callEvent(newEvent);
        if (newEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerEmoteEnd(PlayerEmoteEndEvent event) {
        HibiscusPlayerEmoteEndEvent newEvent = new HibiscusPlayerEmoteEndEvent(this, event.getPlayer(), event.getEmoteName());
        Bukkit.getPluginManager().callEvent(newEvent);
    }

    public String getItemString(ItemStack itemStack) {
        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (CustomStack.byItemStack(itemStack) == null) return null;
        return CustomStack.byItemStack(itemStack).getId();
    }

    public boolean getIAEnabled() {
        return enabled;
    }
}
