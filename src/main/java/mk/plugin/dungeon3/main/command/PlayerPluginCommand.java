package mk.plugin.dungeon3.main.command;

import mk.plugin.dungeon3.dungeon.manager.DGamePlays;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.queue.DQueueGUI;
import mk.plugin.dungeon3.votekick.KickVotes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerPluginCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command var2, String var3, String[] args) {
		
		Player player = (Player) sender;
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("thoat")) {
				DGamePlays.kick(player, true);
			}
			else if (args[0].equalsIgnoreCase("votekick")) {
				if (args.length <= 1) {
					sender.sendMessage("§aDùng: §f/phoban votekick <player>");
					return false;
				}
				String tn = args[1];
				Player target = Bukkit.getPlayer(tn);
				if (target == null) {
					sender.sendMessage("§cNgười chơi không tồn tại");
					return false;
				}
				if (target == player) {
					sender.sendMessage("§c?");
					sender.sendMessage("§cXài §f/phoban thoat §cđi bạn");
					return false;
				}
				if (!DPlayerUtils.isInDungeon(player)) {
					sender.sendMessage("§cBạn có ở trong phó bản đâu?");
					return false;
				}
				String id = DPlayerUtils.getCurrentDungeon(player);
				if (!DPlayerUtils.isInDungeon(target, id)) {
					sender.sendMessage("§cĐối phương không cùng phó bản với bạn");
					return false;
				}
				UUID uuid = target.getUniqueId();
				KickVotes.voteKick(id, uuid, player.getUniqueId());
			}
		}
		
		else DQueueGUI.openGUI(player);
		
		return false;
	}
	
}
