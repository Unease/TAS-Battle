package com.minecrafttas.tasbattle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

/**
 * Manages keybinds and their categories.
 * @author Pancake
 */
@Environment(EnvType.CLIENT)
public class KeybindSystem {

	private static Map<KeyMapping, Boolean> keys = new HashMap<>();
	private static Keybind[] keybinds = {
		new Keybind("Start/Stop spectating", "TAS Battle", GLFW.GLFW_KEY_R, true, () -> { // Spectate previous player
			TASBattle.getInstance().getSpectatorManager().cycleSpectate();
		}),
		new Keybind("Spectate next player", "TAS Battle", GLFW.GLFW_KEY_E, true, () -> { //Spectate next player
			TASBattle.getInstance().getSpectatorManager().spectateNextPlayer();
		}),
		new Keybind("Spectate previous player", "TAS Battle", GLFW.GLFW_KEY_Q, true, () -> { //Spectate previous player
			TASBattle.getInstance().getSpectatorManager().spectatePreviousPlayer();
		})
	};

	@Getter
	private static class Keybind {

		private KeyMapping keyMapping;
		private String category;
		private boolean isInGame;
		private Runnable onKeyDown;

		public Keybind(String name, String category, int defaultKey, boolean isInGame, Runnable onKeyDown) {
			this.keyMapping = new KeyMapping(name, defaultKey, category);
			this.category = category;
			this.isInGame = isInGame;
			this.onKeyDown = onKeyDown;
		}
		
	}

	/**
	 * Initialize keybind Manager, register categories and kebinds.
	 * @param keyMappings KeyMappings array
	 */
	public static KeyMapping[] onKeybindInitialize(KeyMapping[] keyMappings) {
		// initialize categories
		Map<String, Integer> categories = KeyMapping.CATEGORY_SORT_ORDER;
		for (int i = 0; i < keybinds.length; i++)
			if (!categories.containsKey(keybinds[i].category))
				categories.put(keybinds[i].category, i + 8);
		
		// add keybinds
		return ArrayUtils.addAll(keyMappings, Arrays.asList(keybinds).stream().map(Keybind::getKeyMapping).toArray(KeyMapping[]::new)); // convert Keybind array to KeyMapping on the fly
	}

	/**
	 * Watch for key presses and trigger keybinds
	 * @param mc Instance of Minecraft
	 */
	public static void onGameLoop(Minecraft mc) {
		for (Keybind keybind : keybinds) {
			if (keybind.isInGame && mc.level == null || !isKeyDown(mc, keybind.getKeyMapping()))
				continue;
			
			keybind.onKeyDown.run();
		}
	}
	

	/**
	 * Check whether key has been pressed recently.
	 * @param mc Instance of minecraft
	 * @param map Key mappings to check
	 * @return Key has been pressed recently
	 */
	private static boolean isKeyDown(Minecraft mc, KeyMapping map) {
		// check if in a text field
		Screen screen = mc.screen;
		if (screen != null && ((screen.getFocused() instanceof EditBox && ((EditBox) screen.getFocused()).canConsumeInput()) || screen.getFocused() instanceof RecipeBookComponent))
			return false;

		boolean wasPressed = keys.containsKey(map) ? keys.get(map) : false;
		boolean isPressed = GLFW.glfwGetKey(mc.getWindow().getWindow(), map.key.getValue()) == GLFW.GLFW_PRESS;
		keys.put(map, isPressed);
		return !wasPressed && isPressed;
	}

}