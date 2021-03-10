package mk.plugin.dungeon3.queue;

import org.bukkit.scheduler.BukkitRunnable;

public class DQueueTask extends BukkitRunnable {
	
	@Override
	public void run() {	
		DQueues.checkAll();
	}

	
}