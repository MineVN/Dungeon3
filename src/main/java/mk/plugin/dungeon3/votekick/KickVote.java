package mk.plugin.dungeon3.votekick;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class KickVote {
	
	private String id;
	private UUID target;
	private UUID voter;
	private int max;
	private int votedYes;
	private long start;
	private boolean end;
	private List<String> voted;
	
	public KickVote(String id, UUID target, UUID voter, int max, long start) {
		this.id = id;
		this.target = target;
		this.voter = voter;
		this.max = max;
		this.start = start;
		this.end = false;
		this.voted = Lists.newArrayList();
	}
	
	public UUID getVoter() {
		return this.voter;
	}
	
	public String getDungeonID() {
		return this.id;
	}
	
	public UUID getTarget() {
		return this.target;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public int getVotedYes() {
		return this.votedYes;
	}
	
	public void addVoteYes(String player) {
		this.votedYes += 1;
		this.voted.add(player);
	}
	
	public boolean isVoted(String player) {
		return this.voted.contains(player);
	}
	
	public boolean isYes() {
		return this.votedYes > this.max / 2;
	}
	
	public long getStart() {
		return this.start;
	}
	
	public boolean canEndVote(long voteTime) {
		return System.currentTimeMillis() < this.start + voteTime;
	}
	
	public void setEndVote() {
		this.end = true;
	}
	
	public boolean isEnded() {
		return this.end;
	}
	
}
