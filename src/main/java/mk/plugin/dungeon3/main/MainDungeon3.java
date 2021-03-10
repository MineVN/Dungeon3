package mk.plugin.dungeon3.main;

import mk.plugin.dungeon3.buff.Buff;
import mk.plugin.dungeon3.dungeon.manager.DGameEnds;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.listener.DungeonListener;
import mk.plugin.dungeon3.listener.EntityListener;
import mk.plugin.dungeon3.listener.GUIListener;
import mk.plugin.dungeon3.listener.PlayerListener;
import mk.plugin.dungeon3.main.command.AdminPluginCommand;
import mk.plugin.dungeon3.main.command.PlayerPluginCommand;
import mk.plugin.dungeon3.queue.DQueueTask;
import mk.plugin.dungeon3.queue.DQueues;
import mk.plugin.dungeon3.rank.Rank;
import mk.plugin.dungeon3.slave.Slaves;
import mk.plugin.dungeon3.task.DMoneyCoinTask;
import mk.plugin.dungeon3.ticket.Tickets;
import mk.plugin.dungeon3.yaml.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MainDungeon3 extends JavaPlugin {
	
	public boolean loaded = false;
	
	@Override
	public void onEnable() {
		this.registerCommands();
		this.registerListeners();
		this.hookPlugins();
		this.runTasks();
		if (Bukkit.getOnlinePlayers().size() > 0) this.reloadConfig();
	}
	
	@Override
	public void onDisable() {
		DDataUtils.getDungeons().keySet().forEach(id -> {
			if (DGameUtils.isPlaying(id)) DGameEnds.loseDungeon(id);
		});
	}
	
	@Override
	public void reloadConfig() {
		this.saveDefaultConfig();
		YamlFile.reloadAll(this);
		Lang.init(this, YamlFile.MESSAGE);
		DDataUtils.loadAll(YamlFile.CONFIG.get());
		DDataUtils.saveAll();
		Rank.loadAll(YamlFile.CONFIG.get());
		Buff.init(YamlFile.CONFIG.get());
		Tickets.init(YamlFile.CONFIG.get());
		Slaves.reload(YamlFile.CONFIG.get());
		this.createQueues();
		this.registerTasks();
	}
	
	public void registerCommands() {
		this.getCommand("dungeon").setExecutor(new PlayerPluginCommand());
		this.getCommand("mk/plugin/dungeon3").setExecutor(new AdminPluginCommand());
	}
	
	public void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new DungeonListener(), this);
		Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
	}
	
	public void registerTasks() {
		new DQueueTask().runTaskTimerAsynchronously(this, 0, 100);
	}
	
	public void createQueues() {
		DDataUtils.getDungeons().forEach((id, dungeon) -> {
			if (!DGameUtils.isPlaying(id) && !DQueues.hasQueue(id)) {
				DQueues.newQueue(id);
			}
		});
	}
	
	public void hookPlugins() {
		
	}
	
	public void runTasks() {
		DMoneyCoinTask.start();
	}
	
	public static mk.plugin.dungeon3.main.MainDungeon3 getPlugin() {
		return (mk.plugin.dungeon3.main.MainDungeon3) Bukkit.getPluginManager().getPlugin("Dungeon3");
	} 
	
	public static mk.plugin.dungeon3.main.MainDungeon3 get() {
		return (mk.plugin.dungeon3.main.MainDungeon3) Bukkit.getPluginManager().getPlugin("Dungeon3");
	} 
	
}
