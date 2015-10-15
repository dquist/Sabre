package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.cmd.SabreCommand;

public class CmdPearl extends SabreCommand {

	public final SabreCommand cmdFree = new CmdPearlFree();
	public final SabreCommand cmdKill = new CmdPearlKill();
	public final SabreCommand cmdLocate = new CmdPearlLocate();
	public final SabreCommand cmdReturn = new CmdPearlReturn();
	public final SabreCommand cmdSummon = new CmdPearlSummon();
	public final SabreCommand cmdMokshaBind = new CmdMokshaBind();
	public final SabreCommand cmdMokshaJailbreak = new CmdMokshaJailbreak();
	
	private static CmdPearl instance;
	public static CmdPearl getInstance() {
		return instance;
	}
	
	public CmdPearl()
	{
		super();
		this.aliases.add("pp");

		this.senderMustBePlayer = true;
		
		this.setHelpShort("The PrisonPearl base command");

		this.addSubCommand(cmdFree);
		this.addSubCommand(cmdKill);
		this.addSubCommand(cmdLocate);
		this.addSubCommand(cmdReturn);
		this.addSubCommand(cmdSummon);
		this.addSubCommand(cmdMokshaBind);
		this.addSubCommand(cmdMokshaJailbreak);
		
		instance = this;
	}

	@Override
	public void perform() 
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
