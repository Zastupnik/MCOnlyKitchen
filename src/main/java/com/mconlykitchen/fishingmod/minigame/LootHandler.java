package com.mconlykitchen.fishingmod.minigame;

import com.mconlykitchen.fishingmod.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.List;
import java.util.Random;

public class LootHandler {

    private static final Random rand = new Random();

    /** Выдать рыбу игроку */
    public static void giveFish(EntityPlayer player, boolean lava) {
        Item fish;

        if (lava) {
            // адские рыбы (жёстко прописаны в коде)
            Item[] netherFish = {
                    GameRegistry.findItem("minecraft", "magma_cream"),
                    GameRegistry.findItem("minecraft", "ghast_tear"),
                    GameRegistry.findItem("minecraft", "blaze_powder")
            };
            fish = netherFish[rand.nextInt(netherFish.length)];
        } else {
            // обычные рыбы (жёстко прописаны в коде)
            Item[] normalFish = {
                    GameRegistry.findItem("minecraft", "fish"),       // треска
                    GameRegistry.findItem("minecraft", "salmon"),
                    GameRegistry.findItem("minecraft", "clownfish"),
                    GameRegistry.findItem("minecraft", "pufferfish")
            };
            fish = normalFish[rand.nextInt(normalFish.length)];
        }

        if (fish != null) {
            player.inventory.addItemStackToInventory(new ItemStack(fish));
            player.addChatMessage(new ChatComponentText("Ты поймал рыбу!"));
        }
    }

    /** Выдать лут из сундука */
    public static void giveChestLoot(EntityPlayer player, boolean lava) {
        List<String> lootList = lava ? ConfigHandler.netherChestLoot : ConfigHandler.chestLoot;
        float goldenChance = ConfigHandler.goldenChestChance;

        if (lootList == null || lootList.isEmpty()) {
            player.addChatMessage(new ChatComponentText("Сундук пуст..."));
            return;
        }

        // шанс золотого сундука
        boolean golden = rand.nextFloat() < goldenChance;

        // выбираем случайный предмет
        String itemName = lootList.get(rand.nextInt(lootList.size()));
        Item item = GameRegistry.findItem("minecraft", itemName);

        if (item != null) {
            ItemStack stack = new ItemStack(item, golden ? 2 : 1); // золотой сундук даёт двойной лут
            player.inventory.addItemStackToInventory(stack);

            if (golden) {
                player.addChatMessage(new ChatComponentText("Ты открыл ЗОЛОТОЙ сундук!"));
            } else {
                player.addChatMessage(new ChatComponentText("Ты открыл сундук!"));
            }
        } else {
            player.addChatMessage(new ChatComponentText("Ошибка: предмет " + itemName + " не найден."));
        }
    }
}
