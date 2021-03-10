package mk.plugin.dungeon3.votekick;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class KickVotes {
	
	public static final long VOTE_TIME = 15000;
	
	private static Map<String, KickVote> votes = Maps.newHashMap();
	
	public static void endVote(String id, boolean yes) {
		KickVote vote = votes.get(id);
		votes.remove(id);
		DStatus status = DGameUtils.getStatus(id);
		Player target = Bukkit.getPlayer(vote.getTarget());
		if (yes) {
			status.getPlayers().stream().map(Bukkit::getPlayer).collect(Collectors.toList()).forEach(p -> {
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 1, 1);
				Lang.DUNGEON_VOTE_KICK_YES.send(p, "%target%", target.getName());
			});;
			Bukkit.getScheduler().runTask(MainDungeon3.get(), () -> {
				target.teleport(Utils.getPlayerSpawn());
			});
		}
		else {
			status.getPlayers().stream().map(Bukkit::getPlayer).collect(Collectors.toList()).forEach(p -> {
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 1, 1);
				Lang.DUNGEON_VOTE_KICK_NO.send(p, "%target%", target.getName());
			});;
		}
	}
	
	public static KickVote get(String id) {
		return votes.getOrDefault(id, null);
	}
	
	public static void addVote(String id, String player) {
		KickVote vote = votes.get(id);
		vote.addVoteYes(player);
	}
	
	public static boolean hasVote(String id) {
		return votes.containsKey(id);
	}
	
	public static void voteKick(String id, UUID target, UUID voter) {
		DStatus status = DGameUtils.getStatus(id);
		if (status == null) return;
		long start = System.currentTimeMillis();
		KickVote vote = new KickVote(id, target, voter, status.getPlayers().size(), start);
		votes.put(id, vote);
		
		Player pVoter = Bukkit.getPlayer(voter);
		Player pTarget = Bukkit.getPlayer(target);
		
		vote.addVoteYes(pVoter.getName());
		
		Map<String, String> plh = Maps.newHashMap();
		plh.put("%voter%", pVoter.getName());
		plh.put("%target%", pTarget.getName());
		
		Lang.DUNGEON_VOTE_KICK_SUCCESS.send(pVoter);
		
		status.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 1, 1);
			if (uuid.equals(voter)) return;
			p.sendMessage("");
			Lang.DUNGEON_VOTE_KICK_1.send(p, plh);
			Lang.DUNGEON_VOTE_KICK_2.send(p, plh);
			p.sendMessage("");
		});
		
		Bukkit.getScheduler().runTaskLater(MainDungeon3.get(), () -> {
			if (hasVote(id)) {
				KickVote kv = get(id);
				if (kv.getStart() == start) {
					endVote(id, false);
				}
			}
		}, VOTE_TIME / 50);
	}
	
}
