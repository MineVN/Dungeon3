package mk.plugin.dungeon3.ticket;

import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.util.ItemBuilder;
import mk.plugin.dungeon3.util.ItemStackUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Tickets {
	
	private static ItemStack PATTERN;
	
	public static void init(FileConfiguration config) {
		PATTERN = ItemBuilder.buildItem(config.getConfigurationSection("ticket.item"));
	}
	
	public static ItemStack getTicket(String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		String name = d.getInfo().getName();
		
		ItemStack is = PATTERN.clone();
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(meta.getDisplayName().replace("%dungeon_name%", name));
		is.setItemMeta(meta);
		
		return ItemStackUtils.setTag(is, "dungeon3.ticket", id);
	}
	
	public static boolean isTicket(ItemStack is) {
		return ItemStackUtils.hasTag(is, "dungeon3.ticket");
	}
	
	public static boolean isTicket(ItemStack is, String id) {
		if (!isTicket(is)) return false;
		String tid = getDungeon(is);
		return id.equalsIgnoreCase(tid);
	}
	
	public static String getDungeon(ItemStack ticket) {
		return ItemStackUtils.getTag(ticket, "dungeon3.ticket");
	}
	
	public static boolean takeOne(Player p) {
		PlayerInventory inv = p.getInventory();

		int count = 1;

		for (int slot = 0; slot < inv.getSize(); slot++) {
			ItemStack i = inv.getItem(slot);
			if (count <= 0)
				break;
			if (i == null)
				continue;
			if (isTicket(i)) {
				if (i.getAmount() > count) {
					i.setAmount(i.getAmount() - count);
					count = 0;
				} else if (i.getAmount() <= count) {
					count -= i.getAmount();
					inv.setItem(slot, null);
				}
			}
		}
		
		return true;
	}
	
	public static int count(Player player, String id) {
		int count = 0;
		PlayerInventory inv = player.getInventory();

		for (ItemStack i : inv.getContents()) {
			if (i == null)
				continue;
			if (isTicket(i, id))
				count += i.getAmount();
		}
		return count;
	}
	
	public static boolean contains(Player player, int amount, String id) {
		int count = count(player, id);
		return count >= amount;
	}
	
	
	
}
