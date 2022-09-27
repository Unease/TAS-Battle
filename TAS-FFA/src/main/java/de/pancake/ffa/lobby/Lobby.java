package de.pancake.ffa.lobby;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.pancake.common.Events;
import de.pancake.ffa.FFA;
import net.kyori.adventure.text.Component;

/**
 * Event listener during lobby phase
 * @author Pancake
 */
public class Lobby implements Events {

	/**
	 * Lobby timer instance
	 */
	private LobbyTimer timer = new LobbyTimer(90, 2, 3, FFA::startGame);

	/**
	 * Lobby scenario manager
	 */
	private LobbyScenarioManager scenarios = new LobbyScenarioManager();

	/**
	 * Update the lobby countdown when a player joins the server and edit the players inventory
	 */
	@Override
	public void playerJoin(Player player) {
		this.timer.addPlayer(player);

		// update inventory
		var inv = player.getInventory();
		inv.clear();

		var chestItem = new ItemStack(Material.CHEST);
		chestItem.editMeta(m -> {
			m.displayName(Component.text(ChatColor.WHITE + "Scenarios"));
			m.lore(Arrays.asList(Component.text(ChatColor.DARK_PURPLE + "Every FFA game can be customized with scenarios."), Component.text(ChatColor.DARK_PURPLE + "These are small additions to the rules that"), Component.text(ChatColor.DARK_PURPLE + "allow for unique and fun gameplay.")));
		});
		inv.setItem(0, chestItem);

		player.updateInventory(); // not taking any risks ._.
	}

	/**
	 * Update the lobby countdown when a player leaves the server
	 */
	@Override
	public void playerLeave(Player player) {
		this.timer.removePlayer(player);
	}

	/**
	 * Opens the scenarios menu on interaction
	 * @see #playerInteract(Player, Action, Block, Material, ItemStack)
	 */
	private void playerInteract2(Player player, Action action, Block clickedBlock, Material material, ItemStack item) {
		if (item != null && item.getType() == Material.CHEST)
			this.scenarios.openInventory(player);
	}

	/**
	 * Interacts with the scenarios menu in a ui
	 * @see #playerClick(Player, ClickType, int, ItemStack, ItemStack, Inventory)
	 */
	private void playerClick2(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) {
		this.scenarios.interact(p, clickedItem);
	}

	// @formatter:off

	// Restrict basic player events
	@Override public boolean playerBreak(Player player, Block block) { return true; }
	@Override public boolean playerPlace(Player player, Block block, ItemStack itemInHand, Block blockAgainst) { return true; }
	@Override public boolean playerDrop(Player player, Item item) { return true; }
	@Override public boolean entityDamage(Entity entity, double damage, DamageCause cause) { return true; }
	@Override public boolean playerConsume(Player player, ItemStack item) { return true; }
	@Override public boolean entityPickup(LivingEntity entity, Item item) { return true; }
	@Override public boolean playerInteract(Player player, Action action, Block clickedBlock, Material material, ItemStack item) { this.playerInteract2(player, action, clickedBlock, material, item); return true; }
	@Override public boolean playerClick(Player p, ClickType click, int slot, ItemStack clickedItem, ItemStack cursor, Inventory inventory) { this.playerClick2(p, click, slot, clickedItem, cursor, inventory); return true; }

	// Non-required events
	@Override public boolean entityExplosion(Entity entity, Location loc) { return false; }
	@Override public List<ItemStack> playerDeath(Player player, List<ItemStack> drops) { return drops; }


}
