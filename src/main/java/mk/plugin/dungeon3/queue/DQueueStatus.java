package mk.plugin.dungeon3.queue;

import com.google.common.collect.Lists;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.util.ItemStackUtils;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum DQueueStatus {

	CAN_JOIN {
		@Override
		public ItemStack getIcon(String id, Player player) {
			Dungeon d = DDataUtils.getDungeon(id);
			DQueue q = DQueues.getQueue(id);

			ItemStack item = new ItemStack(d.getOption().getGUIIcon());
			item.setDurability(Utils.getColorDurability(DyeColor.GREEN, d.getOption().getGUIIcon()));
			ItemStackUtils.setDisplayName(item, "§3§lPhó bản §b§l" + d.getInfo().getName());

			List<String> lore = Lists.newArrayList();
			lore.addAll(Utils.toList(d.getInfo().getDesc(), 25, "§7§o"));
			lore.add("");
			lore.add("§2Người chơi: §f" + q.getPlayers().size() + "/" + q.getMax());
			lore.add("§2Trạng thái: §aCó thể vào");

			ItemStackUtils.setLore(item, lore);

			item = ItemStackUtils.setTag(item, "dungeonClick", "join");
			item = ItemStackUtils.setTag(item, "dungeonID", id);

			return item;
		}
	},
	CANT_JOIN {
		@Override
		public ItemStack getIcon(String id, Player player) {
			Dungeon d = DDataUtils.getDungeon(id);

			ItemStack item = new ItemStack(d.getOption().getGUIIcon());
			item.setDurability(Utils.getColorDurability(DyeColor.RED, d.getOption().getGUIIcon()));
			ItemStackUtils.setDisplayName(item, "§3§lPhó bản §b§l" + d.getInfo().getName());

			List<String> lore = Lists.newArrayList();
			lore.addAll(Utils.toList(d.getInfo().getDesc(), 25, "§7§o"));
			lore.add("");
			lore.add("§2Trạng thái: §cKhông thể vào");
			lore.add("§2Yêu cầu: ");
			lore.add("§6  Cấp độ: §e" + d.getOption().getLevel().getMin() + " >> " + d.getOption().getLevel().getMax());
			lore.add("§6  Vé vào: §e" + (d.getOption().isTicketRequired() ? "x1 Vé" : "Không"));

			ItemStackUtils.setLore(item, lore);

			return item;
		}
	},
	WAITING {
		@Override
		public ItemStack getIcon(String id, Player player) {
			Dungeon d = DDataUtils.getDungeon(id);
			DQueue q = DQueues.getQueue(id);

			ItemStack item = new ItemStack(d.getOption().getGUIIcon());
			item.setDurability(Utils.getColorDurability(DyeColor.YELLOW, d.getOption().getGUIIcon()));
			ItemStackUtils.setDisplayName(item, "§3§lPhó bản §b§l" + d.getInfo().getName());

			List<String> lore = Lists.newArrayList();
			lore.addAll(Utils.toList(d.getInfo().getDesc(), 25, "§7§o"));
			lore.add("");
			lore.add("§2Người chơi: §f" + q.getPlayers().size() + "/" + q.getMax() + "§7 (Tối thiểu: " + d.getOption().getPlayer().getMin() + ")");
			lore.add("§2Trạng thái: §6Đang chờ, có thể thoát");
			lore.add("§2Đếm ngược:  §f" + DQueues.getSecondsRemain(id) + "s");

			ItemStackUtils.setLore(item, lore);

			item = ItemStackUtils.setTag(item, "dungeonClick", "leave");
			item = ItemStackUtils.setTag(item, "dungeonID", id);

			return item;
		}
	},
	DELAY {
		@Override
		public ItemStack getIcon(String id, Player player) {
			Dungeon d = DDataUtils.getDungeon(id);

			ItemStack item = new ItemStack(d.getOption().getGUIIcon());
			item.setDurability(Utils.getColorDurability(DyeColor.BLUE, d.getOption().getGUIIcon()));
			ItemStackUtils.setDisplayName(item, "§3§lPhó bản §b§l" + d.getInfo().getName());

			List<String> lore = Lists.newArrayList();
			lore.addAll(Utils.toList(d.getInfo().getDesc(), 25, "§7§o"));
			lore.add("");
			lore.add("§2Trạng thái: §9Bị trì hoãn");
			lore.add("§2Đếm ngược:  §f" + Utils.format(DQueues.getRemainDelaySeconds(id, player) * 1000));

			ItemStackUtils.setLore(item, lore);

			return item;
		}
	},
	PLAYING {
		@Override
		public ItemStack getIcon(String id, Player player) {
			Dungeon d = DDataUtils.getDungeon(id);

			ItemStack item = new ItemStack(d.getOption().getGUIIcon());
			item.setDurability(Utils.getColorDurability(DyeColor.PINK, d.getOption().getGUIIcon()));
			ItemStackUtils.setDisplayName(item, "§3§lPhó bản §b§l" + d.getInfo().getName());

			List<String> lore = Lists.newArrayList();
			lore.addAll(Utils.toList(d.getInfo().getDesc(), 25, "§7§o"));
			lore.add("");
			lore.add("§2Trạng thái: §dĐang chiến đấu");
			lore.add("§2Thời gian:  §f" + Utils.formatMinute(DGameUtils.getStatus(id).getAllStatistic().getTimeSurvived() * 1000));

			ItemStackUtils.setLore(item, lore);

			return item;
		}
	};

	public abstract ItemStack getIcon(String id, Player player);

}
