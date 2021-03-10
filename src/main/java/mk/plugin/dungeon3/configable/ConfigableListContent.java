package mk.plugin.dungeon3.configable;

public abstract class ConfigableListContent {
	
	public ConfigableListContent() {}
	public ConfigableListContent(String s) {
		load(s);
	};
	public abstract void load(String s);
	public abstract String toString();
	
}
