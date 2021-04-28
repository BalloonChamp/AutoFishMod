package top.gardel.mc.autofish;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import top.gardel.mc.autofish.event.CaughtFishCallBack;

import java.util.Timer;
import java.util.TimerTask;

public class AutoFishMod implements ModInitializer {
    private Timer timer;

    int count = 0;

    double ms = 2000;

    @Override
    public void onInitialize() {

        timer = new Timer(false);
        CaughtFishCallBack.EVENT.register((player) ->
        {
            if (player instanceof ClientPlayerEntity) {
                //player.sendMessage(new LiteralText("鱼上钩了"), false);
                ItemStack mainHandItem = player.getMainHandStack();
                ItemStack offHandItem = player.getOffHandStack();
                ItemStack fishingRod = null;
                Hand hand = null;
                if (mainHandItem.getItem() == Items.FISHING_ROD) {
                    fishingRod = mainHandItem;
                    hand = Hand.MAIN_HAND;
                } else if (offHandItem.getItem() == Items.FISHING_ROD) {
                    fishingRod = offHandItem;
                    hand = Hand.OFF_HAND;
                }
                if (fishingRod != null) {
                    assert MinecraftClient.getInstance().interactionManager != null;
                    MinecraftClient.getInstance().interactionManager.interactItem(player, player.world, hand);
                    if (fishingRod.getMaxDamage() - fishingRod.getDamage() > 3) {
                        Hand finalHand = hand;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MinecraftClient.getInstance().interactionManager.interactItem(player, player.world, finalHand);
                                count++;
                                String countStr = String.valueOf(count);
                                player.sendMessage(new LiteralText(countStr), false);
                                Vec3d headYaw = new Vec3d(player.getHorizontalFacing().getUnitVector());
                                Vec3d headYaw2 = headYaw.multiply(8.7 / (double)ms);
                                double headX = headYaw2.z;
                                double headZ = -headYaw2.x;
                                if (count % 5 == 0) {
                                    if (count % 2 == 0) {
                                        headX *= -1.0;
                                        headZ *= -1.0;
                                    }

                                    Vec3d headFinal = new Vec3d(headX, 0.0, headZ);

                                    for(int i = 0; i < ms; ++i) {
                                        player.move(MovementType.PLAYER, headFinal);

                                        try {
                                            Thread.sleep(1);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }, 500);
                    }
                }
            }
            return ActionResult.SUCCESS;
        });
    }
}