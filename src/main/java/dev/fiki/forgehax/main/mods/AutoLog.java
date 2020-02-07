package dev.fiki.forgehax.main.mods;

import dev.fiki.forgehax.common.events.packet.PacketInboundEvent;
import dev.fiki.forgehax.main.Common;
import dev.fiki.forgehax.main.events.LocalPlayerUpdateEvent;
import dev.fiki.forgehax.main.util.cmd.settings.BooleanSetting;
import dev.fiki.forgehax.main.util.cmd.settings.IntegerSetting;
import dev.fiki.forgehax.main.util.mod.Category;
import dev.fiki.forgehax.main.util.mod.ToggleMod;
import dev.fiki.forgehax.main.util.mod.loader.RegisterMod;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

import static dev.fiki.forgehax.main.Common.*;

@RegisterMod
public class AutoLog extends ToggleMod {
  
  public AutoLog() {
    super(Category.COMBAT, "AutoLog", false, "automatically disconnect");
  }
  
  public final IntegerSetting threshold = newIntegerSetting()
          .name("threshold")
          .description("health to go down to to disconnect\"")
          .defaultTo(0)
          .build();
  
  public final BooleanSetting noTotem = newBooleanSetting()
          .name("no-totem")
          .description("disconnect if not holding a totem")
          .defaultTo(false)
          .build();
  
  public final BooleanSetting disconnectOnNewPlayer = newBooleanSetting()
          .name("new-player")
          .description("Disconnect if a player enters render distance")
          .defaultTo(false)
          .build();
  
  @SubscribeEvent
  public void onLocalPlayerUpdate(LocalPlayerUpdateEvent event) {
    if (getLocalPlayer() != null) {
      int health = (int) (getLocalPlayer().getHealth() + getLocalPlayer().getAbsorptionAmount());
      if (health <= threshold.getValue()
          || (noTotem.getValue()
          && !((getLocalPlayer().getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
          || getLocalPlayer().getHeldItemMainhand().getItem() == Items.TOTEM_OF_UNDYING))) {
        AutoReconnectMod.hasAutoLogged = true;
        getNetworkManager().closeChannel(new StringTextComponent("Health too low (" + health + ")"));
        disable();
      }
    }
  }
  
  @SubscribeEvent
  public void onPacketRecieved(PacketInboundEvent event) {
    if (event.getPacket() instanceof SSpawnPlayerPacket) {
      if (disconnectOnNewPlayer.getValue()) {
        AutoReconnectMod.hasAutoLogged = true; // dont automatically reconnect
        UUID id = ((SSpawnPlayerPacket) event.getPacket()).getUniqueId();
        
        NetworkPlayerInfo info = MC.getConnection().getPlayerInfo(id);
        String name = info != null ? info.getGameProfile().getName() : "(Failed) " + id.toString();
        
        getNetworkManager().closeChannel(new StringTextComponent(name + " entered render distance"));
        disable();
      }
    }
  }
}
