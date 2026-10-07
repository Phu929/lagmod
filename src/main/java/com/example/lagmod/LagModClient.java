package com.example.lagmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class LagModClient implements ClientModInitializer {
    static boolean on = false;
    static int level = 1;
    static final Random RNG = new Random();

    @Override
    public void onInitializeClient() {
        KeyBinding toggle = reg("toggle", GLFW.GLFW_KEY_L);
        KeyBinding up = reg("up", GLFW.GLFW_KEY_RIGHT_BRACKET);
        KeyBinding down = reg("down", GLFW.GLFW_KEY_LEFT_BRACKET);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggle.wasPressed()) on = !on;
            while (up.wasPressed()) level = Math.min(10, level + 1);
            while (down.wasPressed()) level = Math.max(1, level - 1);
            if (client.player == null || client.world == null) return;
            if (!on) return;
            client.player.sendMessage(Text.literal("LAG ON  level " + level), true);

            long end = System.nanoTime() + level * 8_000_000L;
            double x = 1;
            while (System.nanoTime() < end) x += Math.sin(x) * Math.sqrt(x + 1);

            double px = client.player.getX(), py = client.player.getY(), pz = client.player.getZ();
            for (int i = 0; i < level * 1500; i++) {
                client.world.addParticle(ParticleTypes.EXPLOSION,
                        px + (RNG.nextDouble() - .5) * 24,
                        py + RNG.nextDouble() * 8,
                        pz + (RNG.nextDouble() - .5) * 24, 0, 0, 0);
            }
        });
    }

    private static KeyBinding reg(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.lagmod." + name, InputUtil.Type.KEYSYM, key, "category.lagmod"));
    }
}
