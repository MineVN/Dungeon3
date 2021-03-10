package mk.plugin.dungeon3.task;

import com.google.common.collect.Lists;
import mk.plugin.dungeon3.dungeon.moneycoin.DMoneyCoin;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.moneyapi.MoneyAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DMoneyCoinTask extends BukkitRunnable {
	
	private List<Item> checked;
	
	private DMoneyCoinTask() {
		this.checked = Lists.newArrayList();
	}
	
	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.getWorld().getNearbyEntitiesByType(Item.class, player.getLocation(), 0.5).forEach(i -> {
				if (checked.contains(i)) return;
				DMoneyCoin coin = DMoneyCoin.from(i);
				if (coin == null) return;
				if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
					MoneyAPI.giveMoney(player, coin.getValue());
					player.sendActionBar("§f§l+" + coin.getValue() + "$");
				}
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				Bukkit.getScheduler().runTask(MainDungeon3.get(), () -> {
					checked.remove(i);
					i.remove();
				});
			});
		});	
	}
	
	public static void start() {
		new DMoneyCoinTask().runTaskTimer(MainDungeon3.get(), 0, 5);
	}

}
