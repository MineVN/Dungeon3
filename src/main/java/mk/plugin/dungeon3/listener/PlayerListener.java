package mk.plugin.dungeon3.listener;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.dungeon.util.DSlaveUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.util.Utils;
import mk.plugin.dungeon3.votekick.KickVote;
import mk.plugin.dungeon3.votekick.KickVotes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Map.Entry;

public class PlayerListener implements Listener {

	/*
	 * Vote kick
	 */
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (e.getMessage().equalsIgnoreCase("yes")) {
			if (!DPlayerUtils.isInDungeon(player)) return;
			String id = DPlayerUtils.getCurrentDungeon(player);
			if (!KickVotes.hasVote(id)) return;
			KickVote vote = KickVotes.get(id);
			
			if (vote.isVoted(player.getName())) {
				Lang.DUNGEON_VOTE_KICK_VOTED.send(player);
				return;
			}
			e.setCancelled(true);
			
			Lang.DUNGEON_VOTE_KICK_VOTED.send(player);
			KickVotes.addVote(id, player.getName());
			DStatus status = DGameUtils.getStatus(id);
			status.getPlayers().forEach(p -> {
				Player pVoter = Bukkit.getPlayer(vote.getVoter());
				Player pTarget = Bukkit.getPlayer(vote.getTarget());
				
				Map<String, String> plh = Maps.newHashMap();
				plh.put("%voter%", pVoter.getName());
				plh.put("%target%", pTarget.getName());
				plh.put("%vote%", vote.getVotedYes() + "");
				plh.put("%max%", vote.getMax() + "");
				
				Lang.DUNGEON_VOTE_KICK_RESULT.send(Bukkit.getPlayer(p), plh);
			});
			
			if (vote.canEndVote(KickVotes.VOTE_TIME)) {
				KickVotes.endVote(id, true);
			}
		}
	}
	
	
	/*
	 * Save slaves
	 */
	
	@EventHandler
	public void onSlaveSlave(PlayerToggleSneakEvent e) {
		if (!e.isSneaking()) return;
		Player player = e.getPlayer();
		
		Location locationn = null;
		LivingEntity lee = null;
		
		for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
			if (DSlaveUtils.isSlave(entity)) {
				if (DSlaveUtils.isSaved(entity)) continue;
				lee = (LivingEntity) entity;
				locationn = lee.getLocation();
				break;
			}
		}
		
		if (lee == null) return;
		
		Location location = locationn;
		LivingEntity le = lee;
		new BukkitRunnable() {
			
			int i = 0;
			final int seconds = 5;
			final int count = seconds * 4;
			
			@Override
			public void run() {
				i++;
				if (DSlaveUtils.isSaved(le)) {
					this.cancel();
					return;
				}
				
				if (!player.isSneaking()) {
					this.cancel();
					return;
				}
				
				if (player.getLocation().distance(location) > 2) {
					this.cancel();
					return;
				} 
				
				if (i >= count) {
					player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, le.getLocation().add(0, 1, 0), 20, 0.5f, 0.5f, 0.5f, 0);
					this.cancel();
					DSlaveUtils.save(player, le);
					return;
				}
				
				int percent = i * 100 / count;
				String s = "§a§o" + percent + "%";
				
				player.sendTitle(s, Lang.DUNGEON_SLAVE_SAVING.get(), 0, 10, 0);
				player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_PLACE, 1, 1);
				player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 20, 0.4f, 0.2f, 0.4f, 0);
			}
		}.runTaskTimerAsynchronously(MainDungeon3.get(), 0, 5);
	}
	
	/*
	 * Cancel teleport
	 */
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		if (player.hasPermission("dungeon.teleport")) return;

		DDataUtils.getDungeons().values().forEach(d -> {
			Location to = e.getTo();
			if (d.getInfo().getWorlds().contains(to.getWorld().getName())) {
				if (!DGameUtils.checkTeleport(player)) {
					e.setCancelled(true);
					Lang.DUNGEON_TELEPORT_FAIL.send(player);
					return;
				}
				// Check mob nearby
				else {
					String id = DPlayerUtils.getCurrentDungeon(player);
					DStatus status = DGameUtils.getStatus(id);
					
					// Check if nearby player = 0
					if (Utils.countNearBy(player.getLocation(), 20, EntityType.PLAYER) > 1) return;
					
					// Check if distance > 100
					for (Entry<LivingEntity, String> entry : status.getTurnStatus().getMobToKills().entrySet()) {
						LivingEntity le = entry.getKey();
						if (le.getLocation().distance(to) > 100) {
							le.teleport(to);
						}
					}
				}
			}
		});
		
	}
	
	/*
	 * Cancel gliding
	 */
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (player.hasPermission("dungeon.teleport")) return;
		if (DPlayerUtils.getCurrentDungeon(player) == null) return;
		
		Dungeon d = DDataUtils.getDungeon(DPlayerUtils.getCurrentDungeon(player));
		Location l = player.getLocation();
		
		if (d.getInfo().getWorlds().contains(l.getWorld().getName())) {
			player.sendMessage("§cKhông thể bay trong dungeon");
			e.setCancelled(true);
		}
		
	}
	
	/*
	 * Cancel fly
	 */
	@EventHandler
	public void onGlide(PlayerToggleFlightEvent e) {
		Player player = e.getPlayer();
		if (player.hasPermission("dungeon.teleport")) return;
		if (DPlayerUtils.getCurrentDungeon(player) == null) return;
		
		Dungeon d = DDataUtils.getDungeon(DPlayerUtils.getCurrentDungeon(player));
		Location l = player.getLocation();
		
		if (d.getInfo().getWorlds().contains(l.getWorld().getName())) {
			player.sendMessage("§cKhông thể bay trong dungeon");
			e.setCancelled(true);
		}
		
	}
	
	/*
	 * Check velocity
	 */
	@EventHandler
	public void onVelocity(PlayerVelocityEvent e) {
		Player player = e.getPlayer();
		if (player.hasPermission("dungeon.teleport")) return;
		if (DPlayerUtils.getCurrentDungeon(player) == null) return;
		
		Dungeon d = DDataUtils.getDungeon(DPlayerUtils.getCurrentDungeon(player));
		if (d.getOption().isVelocityAllowed()) return;
		Location l = player.getLocation();
		
		if (d.getInfo().getWorlds().contains(l.getWorld().getName())) {
			player.sendMessage("§cHành động đã bị chặn trong dungeon này");
			e.setCancelled(true);
		}
		
	}
	
	/*
	 * Player god
	 */
	@EventHandler
	public void onDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (DPlayerUtils.isGod(player)) e.setCancelled(true);
		}
	}
	
	
	
}
