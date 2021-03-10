package mk.plugin.dungeon3.lang;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.yaml.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public enum Lang {
	
	DUNGEON_LOSE_NOPLAYER("&f[&2Dungeon3&f] &aDungeon %dungeon% kết thúc vì không còn ai trụ lại"),
	DUNGEON_LOSE_OVERTIME("&f[&2Dungeon3&f] &aDungeon %dungeon% kết thúc vì vượt quá thời gian"),
	DUNGEON_WIN("&f[&2Dungeon3&f] &aDungeon %dungeon% đã được chinh phục"),
	
	DUNGEON_PLAYER_KICK("&f[&2Dungeon3&f] &cBạn đã rời khỏi dungeon"),
	DUNGEON_PLAYER_DEAD_KICK("&f[&2Dungeon3&f] &cBạn bị đá khỏi dungeon vì chết quá số lần"),
	DUNGEON_PLAYER_DEAD_KICK_OTHER("&f[&2Dungeon3&f] &6Người chơi %player% đã rời khởi phó bản"),
	DUNGEON_PLAYER_DEAD_RESPAWN("&f[&2Dungeon3&f] &6Bạn chết và được hồi sinh về nơi an toàn, lưu ý dungeon giới hạn số lần hồi sinh"),
	DUNGEON_PLAYER_GOD("&f[&2Dungeon3&f] &aBạn được miễn nhiễm sát thương trong %second% giây"),
	
	DUNGEON_SLAVE_SAVING("&a&lGiải cứu..."),
	DUNGEON_SLAVE_SAVED("&f[&2Dungeon3&f] &aGiải cứu thành công!"),
	
	DUNGEON_TELEPORT_FAIL("&f[&2Dungeon3&f] &cKhông thể dịch chuyển đến đây (dungeon.teleport)"),
	
	DUNGEON_NEW_CHECKPOINT("&f[&2Dungeon3&f] &aBạn vừa đạt được một checkpoint mới"),
	
	DUNGEON_SLAVE_SCREAM("&7&oTiếng hét: Cứu, ai đó cứu tôi!"),
	
	DUNGEON_CHEST_OPENED("&cBạn đã mở rương này rồi"),
	
	DUNGEON_30_SECOUNDS_OUT("&2§lTự động rời khỏi phó bản sau &c&l%seconds% &2&lgiây..."),
	
	DUNGEON_VOTE_KICK_SUCCESS("&2[Dungeon] &aMở vote kick thành công, chờ 15s để có kết quả vote!"),
	DUNGEON_VOTE_KICK_VOTED("&2[Dungeon] &aVote thành công"),
	DUNGEON_VOTE_KICK_RESULT("&2[Dungeon] &cKết quả vote kick %target%: &6%vote%/%max%"),
	DUNGEON_VOTE_KICK_YES("&2[Dungeon] &cVote kết thúc, kick %target% khỏi phó bản"),
	DUNGEON_VOTE_KICK_NO("&2[Dungeon] &aVote kết thúc, %target% vẫn tiếp tục đi phó bản"),
	DUNGEON_VOTE_KICK_1("&2[Dungeon] &c%voter% &fmuốn vote kick &c%target% &fkhỏi phó bản"),
	DUNGEON_VOTE_KICK_2("&2[Dungeon] &fChat &cYES &fđể đồng ý, tự động bỏ qua sau 15 giây")
	;

	
	private String value;
	
	private Lang(String value) {
		this.value = value;
	}
	
	public String get() {
		return this.value.replace("&", "§");
	}
	
	public void set(String value) {
		this.value = value;
	}
	
	public String getName() {
		return this.name().toLowerCase().replace("_", "-").replace("&", "§");
	}
	
	public void send(Player player, Map<String, String> placeholders) {
		String s = this.get();
		for (Entry<String, String> e : placeholders.entrySet()) {
			s = s.replace(e.getKey(), e.getValue());
		}
		player.sendMessage(s);
	}
	
	public void send(Player player) {
		String s = this.get();
		player.sendMessage(s);
	}
	
	public void broadcast() {
		Bukkit.getOnlinePlayers().forEach(p -> {
			this.send(p, DPlayerUtils.getPlaceholders(p));
		});
	}
	
	public void broadcast(List<UUID> uuids) {
		uuids.forEach(id -> {
			Player p = Bukkit.getPlayer(id);
			this.send(p, DPlayerUtils.getPlaceholders(p));
		});
	}
	
	public void broadcast(String key, String value) {
		Map<String, String> m = Maps.newHashMap();
		m.put(key, value);
		Bukkit.getOnlinePlayers().forEach(p -> {
			this.send(p, m);
		});
	}
	
	public void send(Player player, String key, String value) {
		Map<String, String> placeholders = Maps.newHashMap();
		placeholders.put(key, value);
		send(player, placeholders);
	}
	
	public static void init(JavaPlugin plugin, YamlFile lang) {
		FileConfiguration config = lang.get();
		for (Lang l : Lang.values()) {
			if (config.contains(l.getName())) {
				l.set(config.getString(l.getName()).replace("&", "§"));
			}
			else {
				config.set(l.getName(), l.get().replace("§", "&"));
				lang.save(plugin);
			}
		}
	}
	
}
