package mk.plugin.dungeon3.queue;

import com.google.common.collect.Lists;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.ticket.Tickets;
import mk.plugin.dungeon3.util.ItemStackUtils;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class DQueueGUI {

	public static final String TITLE = "§0§lSORASKY | PHÓ BẢN - DUNGEON";
	
	public static final int TUT_SLOT = 4;
	
	public static void openGUI(Player player) {
		int amount = 0;
		for (Dungeon d : DDataUtils.getDungeons().values()) {
			if (d.getOption().getGUISlot() > amount) amount = d.getOption().getGUISlot();
		}
		amount++;
		
		int size = 45;
//		if (amount % 9 == 0) size = amount;
//		else size = (amount / 9 + 1) * 9;
		Inventory inv = Bukkit.createInventory(null, size, TITLE);
		player.openInventory(inv);
		
		Bukkit.getScheduler().runTaskAsynchronously(MainDungeon3.get(), () -> {
			for (int i = 0 ; i < inv.getSize() ; i++) {
				if (!getEmptySlot().contains(i)) inv.setItem(i, getBlackSlot());
			}
			inv.setItem(TUT_SLOT, getTut());
		});
		
		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				if (player.getOpenInventory() == null) {
					this.cancel();
					return;
				}
				if (!player.getOpenInventory().getTitle().equals(TITLE)) {
					this.cancel();
					return;
				}
				DDataUtils.getDungeons().forEach((id, dungeon) -> {
					DQueueStatus sts = DQueues.getStatus(id, player);
					inv.setItem(dungeon.getOption().getGUISlot(), sts.getIcon(id, player));
				});
			}
		};
		br.runTaskTimerAsynchronously(MainDungeon3.get(), 0, 10);
	}
	
	public static void onClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equalsIgnoreCase(TITLE)) return;
		e.setCancelled(true);
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
		
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		
		ItemStack item = e.getInventory().getItem(slot);
		if (ItemStackUtils.hasTag(item, "dungeonClick")) {
			String id = ItemStackUtils.getTag(item, "dungeonID");
			if (ItemStackUtils.getTag(item, "dungeonClick").equals("join")) {
				if (DPlayerUtils.isInDungeon(player)) {
					player.sendMessage("§cHoàn thành hoặc thoát dungeon hiện tại để vào hàng chờ");
					return;
				}
				if (DQueues.isInQueue(player) && !DQueues.isInQueue(id, player)) {
					player.sendMessage("§cRời hàng chờ rồi chọn");
					return;
				}
				
				// Check ticket
				Dungeon d = DDataUtils.getDungeon(id);
				if (d.getOption().isTicketRequired()) {
					if (!Tickets.takeOne(player)) {
						player.sendMessage("§cBạn cần có vẻ để vào hàng chờ của " + d.getInfo().getName());
						return;
					}
				} else player.sendMessage("§aĐã lấy 1 vé phó bản từ kho đồ của bạn");
				
				DQueues.add(id, player);
				player.sendMessage("§aĐã tham gia phòng chờ");
				return;
			}
			else if (ItemStackUtils.getTag(item, "dungeonClick").equals("leave")) {
				DQueues.remove(id, player);
				player.sendMessage("§aĐã rời phòng chờ");
				return;
			}
		}
	}
	
	public static ItemStack getTut() {
		ItemStack is = new ItemStack(Material.BOOK, 1);
		ItemStackUtils.setDisplayName(is, "§a§lPhó bản (Dungeon) là gì ?");
		List<String> lore = Lists.newArrayList();
		lore.add("");
		lore.add("§f§oLà nơi bạn chiến đấu với quái vật, ");
		lore.add("§f§otừ đó nhận được §a§oKinh nghiệm, Tiền,");
		lore.add("§a§oNguyên liệu chế tác, Đá cường hóa, ");
		lore.add("§a§oBùa may mắn, Đá nâng bậc,...");
		lore.add("");
		lore.add("§f§oĐể có thể tham gia phó bản, bạn phải ");
		lore.add("§f§ođợi người tham gia trước kết thúc ");
		lore.add("§f§orồi bạn mới có thể vào");
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getBlackSlot() {
		ItemStack other = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		other.setDurability(Utils.getColorDurability(DyeColor.BLACK, Material.STAINED_GLASS_PANE));
		ItemMeta meta = other.getItemMeta();
		meta.setDisplayName(" ");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		other.setItemMeta(meta);
		return other;
	}
	
	public static List<Integer> getEmptySlot() {
		return Lists.newArrayList(29, 30, 31, 32, 33, 38, 39, 40, 41, 42).stream().map(i -> i - 9).collect(Collectors.toList());
	}
	
}
