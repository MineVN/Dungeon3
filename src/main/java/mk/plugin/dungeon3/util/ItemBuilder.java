package mk.plugin.dungeon3.util;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
	
	// .?
	public static ItemStack buildItem(ConfigurationSection config) {
		Material material = Material.valueOf(config.getString(".type"));
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		String name = config.getString(".name").replace("&", "§");
		if (name.equals("") || name == null) name = "§r";
		meta.setDisplayName(name);
		List<String> lore = Lists.newArrayList();
		config.getStringList(".lore").forEach(s -> lore.add(s.replace("&", "§")));;
		Map<Enchantment, Integer> e = new HashMap<Enchantment, Integer> ();
		for (int i = 0 ; i < config.getStringList(".enchant").size(); i ++) {
			String s = config.getStringList(".enchant").get(i);
			String eString = s.substring(0, s.indexOf(":"));
			Enchantment enchant = Enchantment.getByName(eString);
			int level = Integer.parseInt(s.substring(s.indexOf(":") + 1));
			e.put(enchant, level);
		}
		if (config.contains("flag")) {
			config.getStringList("flag").forEach(s -> {
				meta.addItemFlags(ItemFlag.valueOf(s));
			});
		}
		
		meta.setLore(lore);
		for (Enchantment en : e.keySet()) {
			meta.addEnchant(en, e.get(en), true);
		}
		
		int data = config.getInt(".data");
		item.setDurability((short) data);
		item.setItemMeta(meta);
		
		return item;
	}
	
}
