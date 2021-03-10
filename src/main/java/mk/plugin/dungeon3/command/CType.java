package mk.plugin.dungeon3.command;

import com.destroystokyo.paper.Title;
import com.google.common.collect.Lists;
import io.lumine.xikage.mythicmobs.MythicMobs;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.location.DLocation;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.rank.Rank;
import mk.plugin.dungeon3.rank.RankUtils;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.List;

public enum CType {
	
	OPPLAYERCMD {
		@Override
		public void execute(String cmd, Player player) {
			player.setOp(true);
			try {
				Bukkit.dispatchCommand(player, cmd);
			}
			catch (Exception e) {
				player.setOp(false);
				e.printStackTrace();
			}
			player.setOp(false);
		}
	},
	PLAYERCMD {
		@Override
		public void execute(String cmd, Player player) {
			Bukkit.dispatchCommand(player, cmd);
		}
	},
	CONSOLECMD {
		@Override
		public void execute(String cmd, Player player) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	},
	TELE {
		@Override
		public void execute(String cmd, Player player) {
			String d = DPlayerUtils.getCurrentDungeon(player);
			Dungeon dg = DDataUtils.getDungeon(d);
			DLocation dl = dg.getLocations().getOrDefault(cmd, null);
			if (dl == null) {
				player.sendMessage(Utils.c("&cLỗi location, thông báo quản trị viên để fix"));
			}
			DGameUtils.teleport(player, dl.getLocation());
		}
	},
	MESS {
		@Override
		public void execute(String cmd, Player player) {
			player.sendMessage(Utils.c(cmd));
		}
	},
	BROADCAST {
		@Override
		public void execute(String cmd, Player player) {
			Bukkit.getOnlinePlayers().forEach(p -> {
				p.sendMessage(Utils.c(cmd));
			});
		}
	},
	TITLE {
		@Override
		public void execute(String cmd, Player player) {
			String t = cmd.split(";")[0];
			String s = cmd.split(";")[1];
			int i1 = 10;
			int i2 = 40;
			int i3 = 10;
			if (cmd.split(";").length > 2) {
				i1 = Integer.valueOf(cmd.split(";")[2]);
				i2 = Integer.valueOf(cmd.split(";")[3]);
				i3 = Integer.valueOf(cmd.split(";")[4]);
			}
			player.sendTitle(new Title(Utils.c(t), Utils.c(s), i1, i2, i3));
		}
	},
	SOUND {
		@Override
		public void execute(String cmd, Player player) {
			Sound s = Sound.valueOf(cmd.split(";")[0]);
			float f1 = Float.valueOf(cmd.split(";")[1]);
			float f2 = Float.valueOf(cmd.split(";")[2]);
			player.playSound(player.getLocation(), s, f1, f2);
		}
	},
	SHOWRANK {
		@Override
		public void execute(String cmd, Player player) {
			String id = DPlayerUtils.getCurrentDungeon(player);
			DStatus status = DGameUtils.getStatus(id);
			Rank r = RankUtils.getRank(id, status.getStatistic(player));
			RankUtils.showRank(player, r);
		}
	},
	KILL {
		@Override
		public void execute(String cmd, Player player) {
			List<String> kills = cmd.equals("*") ? null : Lists.newArrayList(cmd.split(";"));
			DStatus s = DPlayerUtils.getStatus(player);
			if (kills == null || (kills != null && kills.contains("slave"))) {
				s.getTurnStatus().getSlaveToSaves().forEach(le -> {
					le.remove();
				});
			}
			s.getTurnStatus().getMobToKills().forEach((le, id) -> {
				if (kills != null) {
					if (!kills.contains(id)) return;
				}
				le.setKiller(null);
				le.remove();
			});
			player.getWorld().getEntities().forEach(entity -> {
				if (entity instanceof Item) return;
				if (MythicMobs.inst().getMobManager().getAllMythicEntities().contains(entity))  {
					entity.remove();
				}
			});
		}
	};
	
	private CType() {}
	
	public abstract void execute(String cmd, Player player);
	
}
