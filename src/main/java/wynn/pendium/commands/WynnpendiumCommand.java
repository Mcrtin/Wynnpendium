package wynn.pendium.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import wynn.pendium.Ref;
import wynn.pendium.hud.HudEvents;
import wynn.pendium.hud.components.locationui.LocationSettingButton;
import wynn.pendium.hud.screens.ScreenDisplaySettings;
import wynn.pendium.hud.screens.ScreenEditLocations;

import javax.annotation.ParametersAreNonnullByDefault;

public class WynnpendiumCommand extends CommandBase  {

    @ParametersAreNonnullByDefault
    @Override
    public String getName() {
        return "wynnpendium";
    }

    @Override
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "command.wynnpendium.usage";
    }

    @ParametersAreNonnullByDefault
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {


        HudEvents.screen = new ScreenEditLocations();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
       return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
