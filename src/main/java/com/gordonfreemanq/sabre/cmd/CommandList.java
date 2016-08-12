package com.gordonfreemanq.sabre.cmd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.gordonfreemanq.sabre.cmd.factory.CmdFactory;
import com.gordonfreemanq.sabre.cmd.pearl.CmdPearl;
import com.gordonfreemanq.sabre.cmd.snitch.CmdSnitch;

public class CommandList implements Set<SabreCommand> {

	private final HashSet<SabreCommand> commands = new HashSet<SabreCommand>();
	private CmdAutoHelp cmdAutoHelp;
	
	public CommandList() {
	}
	
	public void registerCommands() {
		
		// Help command
		cmdAutoHelp = new CmdAutoHelp();
		
		// Register Commands
		commands.add(new CmdRoot());
		commands.add(new CmdPearl());
		commands.add(new CmdFactory());
		commands.add(new CmdSnitch());
		commands.add(new CmdChat());
		commands.add(new CmdChatMe());
		commands.add(new CmdChatMsg());
		commands.add(new CmdChatReply());
		commands.add(new CmdChatServer());
		commands.add(new CmdChatSay());
		commands.add(new CmdChatIgnore());
		commands.add(new CmdBuildBypass());
		commands.add(new CmdBuildFortify());
		commands.add(new CmdBuildInfo());
		commands.add(new CmdBuildReinforce());
		commands.add(new CmdBuildOff());
		commands.add(new CmdBuildAcid());
		commands.add(new CmdHelp());
		
		// Admin commands
		commands.add(new CmdAdminRoot());
		commands.add(new CmdSpeed());
		commands.add(new CmdTest());
		commands.add(new CmdTeleport());
		commands.add(new CmdTeleportHere());
		commands.add(new CmdAdminFly());
		commands.add(new CmdAdminVanish());
		commands.add(new CmdAdminGamemode());
		commands.add(new CmdAdminRespawn());
		commands.add(new CmdAdminBan());
		commands.add(new CmdAdminUnban());
		commands.add(new CmdAdminMore());
		commands.add(new CmdAdminGive());
	}
	
	public CmdAutoHelp getAutoHelp() {
		return this.cmdAutoHelp;
	}
	
	@Override
	public int size() {
		return commands.size();
	}

	@Override
	public boolean isEmpty() {
		return commands.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return commands.contains(o);
	}

	@Override
	public Iterator<SabreCommand> iterator() {
		return commands.iterator();
	}

	@Override
	public Object[] toArray() {
		return commands.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return commands.toArray(a);
	}

	@Override
	public boolean add(SabreCommand e) {
		return commands.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return commands.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return commands.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends SabreCommand> c) {
		return commands.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return commands.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return commands.removeAll(c);
	}

	@Override
	public void clear() {
		commands.clear();
	}
}
