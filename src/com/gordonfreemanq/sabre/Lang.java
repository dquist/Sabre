package com.gordonfreemanq.sabre;

public class Lang 
{	
	// PLAYER
	public static String playerSetBed = "<g>You set your bed location.";
	public static String playerSetBedFreeWorld = "<b>You can only set your bed in the overworld.";
	public static String playerBedMissing = "<b>Your bed was missing or obstructed.";
	
	
	// GROUPS
	public static String groupCreated = "<g>You created the group <c>%s<g>.";
	public static String groupAlreadyExists = "<b>The group <c>%s <b>already exists.";
	public static String groupAlreadyOwn = "<b>You already own <c>%s<b>.";
	public static String groupAlreadyMember = "<b>You are already a member <c>%s<b>.";
	public static String groupPlayerAlreadyMember = "<c>%s<b> is already a member of <c>%s<b>.";
	public static String groupPlayerAlreadyInvited = "<c>%s<b> is already invited to <c>%s<b>.";
	public static String groupNotExist = "<b>The group <c>%s <b>doesn't exist.";
	public static String groupNotOwner = "<b>You are not owner of <c>%s<b>.";
	public static String groupNotMember = "<b>You are not a member of <c>%s<b>.";
	public static String groupPlayerNotMember = "<c>%s <b>is not a member of <c>%s<b>.";
	public static String groupRenamed = "<g>You renamed <c>%s<g> to <c>%s<g>.";
	public static String groupPlayerInvited = "<g>You invited <c>%s <g>to <c>%s<g>.";
	public static String groupPlayerNotInvited = "<c>%s <b>is not invited to <c>%s<b>.";
	public static String groupPlayerUninvited = "<g>You uninvited <c>%s <g>from <c>%s<g>.";
	public static String groupNotInviteSelf = "<b>You are already a member of <c>%s<b>.";
	public static String groupNotTransferSelf = "<b>You cannot transfer a group to yourself.";
	public static String groupNotKickSelf = "<b>You cannot kick yourself.";
	public static String groupNotRankSelf = "<b>You cannot change your own rank.";
	public static String unknownPlayer = "<b>Unknown player <c>%s<b>.";
	public static String groupInvited = "<g>You were invited to <c>%s<g>.";
	public static String groupNotInvited = "<b>You are not invited to <c>%s<b>.";
	public static String groupYouJoined = "<g>You joined <c>%s<g>.";
	public static String groupPlayerJoined = "<c>%s <i>joined <c>%s<i>.";
	public static String groupYouLeft = "<g>You left <c>%s<g>.";
	public static String groupPlayerLeft = "<c>%s <i>left <c>%s<i>.";
	public static String groupYouKicked = "<g>You kicked <c>%s <g>from <c>%s<g>.";
	public static String groupPlayerKicked = "<c>%s <g>kicked <c>%s <g>from <c>%s<g>.";
	public static String groupYouWereKicked = "<i>You were kicked from <c>%s<i>.";
	public static String groupCheckRank = "<i>Your rank in <c>%s <i>is <n>%s<i>.";
	public static String groupBadRank= "<b>Unknown rank, use:<i> OWNER, ADMIN, OFFICER, BUILDER, MEMBER";
	public static String groupSetRank= "<g>You set the rank of <c>%s <g>to <i>%s<g>";
	public static String groupTriedJoin = "<c>%s <i>tried to join <c>%s<i>.";
	public static String groupUseTransfer = "<b>You can't set another owner.";
	public static String groupNotLeaveOwner = "<b>The group owner cannot leave.";
	public static String groupTransferred = "<g>You transferred <c>%s <g>to <c>%s<g>.";
	public static String groupPlayerTransferred = "<c>%s <i>transferred <c>%s <i>to you.";
	public static String groupAutoJoinEnabled = "<i>Set auto-join status to <g>enabled<i>.";
	public static String groupAutoJoinDisabled = "<i>Set auto-join status to <b>disabled<i>.";
	public static String groupJoinHelp = "<i>Use '<c>/f join <c>%s<i>' to join.";
	public static String groupLeaveHelp = "<i>Use '<c>/f leave <c>%s<i>' to leave.";
	public static String groupTransferHelp = "<i>Use '<c>/f transfer <c>%s<i>' to transfer the group.";
	
	
	// CHAT
	public static String chatPlayerNowOffline = "<i>You were chatting with <c>%s <i>who is now offline.";
	public static String chatMovedGlobal = "<i>Moved to global chat.";
	public static String chatMovedServerBcast = "<h>Moved to server broadcast chat.";
	public static String chatMovedGroup = "<i>Moved to <c>%s <i>group chat.\n";
	public static String chatMovedGroupHelp = "<i>Use '<c>/f c' <i>to return to global chat.";
	public static String chatNoOneHears = "<silver>No one hears you.";
	public static String chatChattingWith = "<lp>You are now chatting with <c>%s<lp>.";
	public static String chatNoReply = "<lp>You have no one to reply to.";
	public static String chatWillReplyTo = "<lp>You will reply to <c>%s<lp>.";
	public static String chatPlayerOffline = "<c>%s <b>is offline.";
	public static String chatOfflineActivity = "<i>There was some activity while you were offline:";
	
	
	// BUILD
	public static String blockInfoEnable = "<i>INFO mode is now <g>enabled<i>.";
	public static String blockInfoDisable = "<i>INFO mode is now <b>disabled<i>.";
	public static String blockBypassEnable = "<i>BYPASS mode is now <g>enabled<i>.";
	public static String blockBypassDisable = "<i>BYPASS mode is now <b>disabled<i>.";
	public static String blockBuildMode = "<i>Build mode set to <n>%s<i>.";
	public static String blockBuildModeGroup = "<g>Build mode set to <n>%s <g>for <c>%s<g>.";
	public static String blockNotMaterial = "<b>That is not a valid reinforcement material.";
	public static String blockMaterialDepleted = "<n>%s <i>depleted, left fortification mode.";
	public static String blockNotReinforceable = "<b>That block cannot be reinforced.";
	public static String blockShowType = "<i>That's a <n>%s<i>.";
	public static String blockShowInfo = "<b>%s with <n>%s <b>at <n>%s%% <b>health.";
	public static String blockShowInfoAccess = "<g>%s to <c>%s <g>with <n>%s <g>at <n>%s%% <g>health.";
	public static String blockShowInfoSpecial = "<n>%s <b>is %s with <n>%s <b>at <n>%s%% <b>health.";
	public static String blockShowInfoAccessSpecial = "<n>%s <g>is %s to <c>%s <g>with <n>%s <g>at <n>%s%% <g>health.";
	public static String blockMismatched = "<b>You can't do that, mismatched reinforcement.";
	public static String blockIsLocked = "<b>%s is locked.";
	public static String blockIsLockedSpecial = "<n>%s <b>is locked.";
	public static String blockAdminBypassEnable = "<h>ADMIN BYPASS <i>mode is now <g>enabled<i>.";
	public static String blockAdminBypassDisable = "<h>ADMIN BYPASS <i>mode is now <b>disabled<i>.";
	public static String blockNoReinforcement = "<i>No reinforcement.";
	public static String blockFailedToReinforce = "<b>Failed to create reinforcement.";
	public static String blockNoChange = "<i>No change was made.";
	public static String blockChanged = "<i>Changed to <n>%s <i>with <n>%s<i>.";
	public static String blockCantPlace = "<i>You cannot place this block.";
	public static String blockMaterialHasLore = "<b>You cannot reinforce with lore items.";
	public static String blockNoAccess = "<i>You don't have access to do that.";
	
	
	// ADMIN
	public static String adminPlayerModifyBan = "Your account is being modified. \nPlease wait a minute before logging back in.";
	public static String adminNameExists = "<b>A player with the name <c>%s <b>already exists.";
	public static String adminChangedPlayerName = "<g>The player <c>%s <g>is now known as <c>%s<g>.";
	public static String adminRemovedPlayer = "<g>You deleted the player <c>%s<g>.";
	public static String adminBannedPlayer = "<g>You banned the player <c>%s<g> with the reason \"<i>%s<g>\".";
	public static String adminUnbannedPlayer = "<g>You unbanned the player <c>%s<g>.";
	public static String adminPlayerNotBanned = "<b>The player <c>%s <b>is not banned.";
	public static String adminYourNameIsNow = "<i>Your name has been set to <c>%s<i>.";
	public static String adminYouBypassed = "<h>You bypassed this block on <c>%s<h>.";
	public static String adminSetSpawn = "<g>You set the spawn location.";
	public static String adminInvalidItem = "<c>That item does not exist.";
	public static String adminVanished = "<teal>You have vanished. Poof.";
	public static String adminUnvanished = "<teal>You have become visible.";
	
	
	
	// SNITCH
	public static final String snitchNotFound = "<b>No snitch was found.";
	public static final String snitchCleared = "<g>Snitch entries cleared.";
	public static final String snitchEntry = "<c>%s entry at <a>%s <n>[%d %d %d %s]";
	public static final String snitchLoggedIn = "<c>%s login at <a>%s <n>[%d %d %d %s]";
	public static final String snitchLoggedOut = "<c>%s logout at <a>%s <n>[%d %d %d %s]";
	public static final String snitchNotifyEnabled = "<i>Notifications for this snitch are now <g>on<i>.";
	public static final String snitchNotifyDisabled = "<i>Notifications for this snitch are now <b>off<i>.";
	public static final String snitchNotifyWasPlaced = "<i>You broke a snitch placed by <c>%s <i>on <a>%s.";
	
	
	// SIGNS
	public static final String signNotReinforced = "<i>This sign is not reinforced.";
	public static final String signNowVisible = "<i>This sign is now <g>visible<i>.";
	public static final String signNowHidden = "<i>This sign is now <b>hidden<i>.";
	
	// WARP
	public static final String warpHitDrive = "<g>Created a <c>Teleport Linker<g>. Hit a warp drive or teleport pad to link them together.";
	public static final String warpLinkedPadDrive = "<g>Teleport pad linked to warp drive.";
	public static final String warpLinkedPadToPad = "<g>Teleport pads linked.";
	public static final String warpNoPadFound = "<b>No teleport pad was found at that location.";
	public static final String warpTooFar = "<i>Teleport pad must be within %d blocks of the warp drive.";
	public static final String warpMissingDrive = "<i>Teleport pad is not linked to a warp drive.";
	public static final String warpDirectMissingDrive = "<i>That teleport pad is not linked to a direct warp drive.";
	public static final String warpMissingPad = "<i>Teleport pad is not linked to another pad.";
	public static final String warpMissingDestDrive = "<i>Destination teleport pad is not linked to a direct warp drive.";
	public static final String warping = "<i>Teleporting...";
	
	
	// PEARLS
	public static final String pearlCantHold = "<b>Imprisoned players cannot pick up prison pearls.";
	public static final String pearlMotd = "<n>You are trapped in this bleak and endless world \nby a prison pearl.";
	public static final String pearlMotd2 = "<i>Type \"<c>/pp locate<i>\" to locate your pearl.";
	public static final String pearlNotImprisoned = "<i>You are not imprisoned.";
	public static final String pearlPearlIsHeld = "<i>Your pearl is held by <a>%s <n>[%d %d %d %s]";
	public static final String pearlYouBound = "<g>You've bound <c>%s <g>to a prison pearl!";
	public static final String pearlYouWereBound = "<b>You've been bound to a prison pearl by <c>%s<b>!";
	public static final String pearlYouWereFreed = "<g>You've been freed!";
	public static final String pearlYouFreed = "<g>You freed <c>%s<g>.";
	public static final String pearlNotHoldingPearl = "<b>You are not holding a prison pearl.";
	public static final String pearlAlreadySummoned = "<c>%s <b> is already summoned.";
	public static final String pearlNotSummoned = "<c>%s <b> is not summoned.";
	public static final String pearlYouSummoned = "<g>You summoned <c>%s <g>from prison.";
	public static final String pearlYouReturned = "<g>You returned <c>%s <g>to prison.";
	public static final String pearlYouKilled = "<g>You killed <c>%s<g>.";
	public static final String pearlAlreadyPearled = "<c>%s <b>is already held by a prison pearl.";
	public static final String pearlNotHoldingMoksha = "<b>You are not holding a moksha rod.";
	public static final String pearlPlayerNotImprisoned = "<i>That player is not imprisoned.";
	public static final String pearlBoundMoksha = "<g>You bound <c>%s <g>to a moksha rod.";
	public static final String pearlMokshaNotBound = "<i>This rod is not bound to anyone.";
	public static final String pearlMokshaAddStrength = "<i>This rod has no strength.";
	public static final String pearlJailbreakPass = "<g>You broke <c>%s<g> out of prison!";
	public static final String pearlJailbreakFail = "<b>Your jailbreak attempt was too weak and failed.";
	public static final String pearlUpdateStrength = "<i>You updated the pearl strength to <c>%d.";
	public static final String pearlCantDoThat = "<i>You can't do that when imprisoned!";
	public static final String pearlNoPlayer = "<i>There's no online player by that name.";
	public static final String pearlBcastRequestSent = "<i>Broadcast request sent.";
	public static final String pearlBcastRequest = "<c>%s <i>has requested to broadcast their pearl location.\nType <c>/pp confirm <i>to confirm";
	public static final String pearlNoBcastRequest = "<i>You have no broadcast requests.";
	public static final String pearlGettingBcasts = "<g>You have now receiving broadcasts from <c>%s.";
	public static final String pearlBroadcast = "<i>The pearl of <c>%s <i>is held by <a>%s <n>[%d %d %d %s]";
	
	
	// FACTORY
	public static final String factoryMustHoldFurnace = "<b>You must be holding a single furnace to do this.";
	public static final String factoryCreatedBaseFactory = "<g>You created a factory base.";
	public static final String factoryNotFound = "<b>No factory was found.";
	public static final String factoryNoRecipe = "<b>No recipe is selected.";
	public static final String factoryMissingChests = "<b>Factory is missing chests. <i>Use <c>/factory configure";
	public static final String factoryMissingFuel = "<b>Factory is out of fuel.";
	public static final String factoryNeedFollowing = "<b>You are missing the following: <i>%s";
	public static final String factoryActivated = "<g>Started recipe <a>%s<g>";
	public static final String factoryDeactivated = "<i>Factory deactivated.";
	public static final String factoryComplete = "<g>Recipe <a>%s <g>is complete!";
	public static final String factoryError = "<b>Recipe <a>%s <b>had an internal error.";
	public static final String factoryRecipeOutOfFuel = "<b>Recipe <a>%s <b>ran out of fuel!";
	public static final String factoryCantDoWhileRunning = "<b>You can't do that while the factory is running.";
	public static final String factoryUpdateController = "<i>Updated controller.";
	
	
	// MISC
	public static final String permForbidden = "<b>You don't have permission to %s.";
	public static final String permDoThat = "do that";
	public static final String commandSenderMustBePlayer = "<b>This command can only be used by ingame players.";
	public static final String commandToFewArgs = "<b>Too few arguments. <i>Use like this:";
	public static final String commandToManyArgs = "<b>Strange argument \'<p>%s<b>\'. <i>Use the command like this:";
	public static String youAreBanned = "You are banned!";
	public static final String ConfirmCommand = "<i>You must confirm this command with \'<c>/%s confirm<i>\'";
	public static String noPermission = "<b>You don't have permission to do that.";
	public static String mustHoldController = "<i>You must be holding a <c>%s<i>.";
	public static String unknownWorld = "<b>Unknown world <c>%s<b>.";
	public static String noCraftingLore = "<i>You cannot craft with lore items.";
	public static String recipeDisabled = "<i>That crafting recipe is disabled.";
	public static String recipeNeed2Logs = "<i>You need 2 logs to make a plank.";
	public static String exceptionGeneral = "<b>An internal error occurred.";
	public static String exceptionLogin = "An internal server error occurred.";
	public static String exceptionConsoleDuring = "An exception error occurred during \"%s\"";
	public static String exceptionBlockOp = "<b>An internal error occurred during this block operation.";
}
