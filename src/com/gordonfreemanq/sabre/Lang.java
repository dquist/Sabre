package com.gordonfreemanq.sabre;

public class Lang 
{	
	// PLAYER
	public static final String playerSetBed = "<g>You set your bed location.";
	public static final String playerSetBedFreeWorld = "<b>You can only set your bed in the overworld.";
	public static final String playerBedMissing = "<b>Your bed was missing or obstructed.";
	
	
	// GROUPS
	public static final String groupCreated = "<g>You created the group <c>%s<g>.";
	public static final String groupAlreadyExists = "<b>The group <c>%s <b>already exists.";
	public static final String groupAlreadyOwn = "<b>You already own <c>%s<b>.";
	public static final String groupAlreadyMember = "<b>You are already a member <c>%s<b>.";
	public static final String groupPlayerAlreadyMember = "<c>%s<b> is already a member of <c>%s<b>.";
	public static final String groupPlayerAlreadyInvited = "<c>%s<b> is already invited to <c>%s<b>.";
	public static final String groupNotExist = "<b>The group <c>%s <b>doesn't exist.";
	public static final String groupNotOwner = "<b>You are not owner of <c>%s<b>.";
	public static final String groupNotMember = "<b>You are not a member of <c>%s<b>.";
	public static final String groupPlayerNotMember = "<c>%s <b>is not a member of <c>%s<b>.";
	public static final String groupRenamed = "<g>You renamed <c>%s<g> to <c>%s<g>.";
	public static final String groupPlayerInvited = "<g>You invited <c>%s <g>to <c>%s<g>.";
	public static final String groupPlayerNotInvited = "<c>%s <b>is not invited to <c>%s<b>.";
	public static final String groupPlayerUninvited = "<g>You uninvited <c>%s <g>from <c>%s<g>.";
	public static final String groupNotInviteSelf = "<b>You are already a member of <c>%s<b>.";
	public static final String groupNotTransferSelf = "<b>You cannot transfer a group to yourself.";
	public static final String groupNotKickSelf = "<b>You cannot kick yourself.";
	public static final String groupNotRankSelf = "<b>You cannot change your own rank.";
	public static final String unknownPlayer = "<b>Unknown player <c>%s<b>.";
	public static final String groupInvited = "<g>You were invited to <c>%s<g>.";
	public static final String groupNotInvited = "<b>You are not invited to <c>%s<b>.";
	public static final String groupYouJoined = "<g>You joined <c>%s<g>.";
	public static final String groupPlayerJoined = "<c>%s <i>joined <c>%s<i>.";
	public static final String groupYouLeft = "<g>You left <c>%s<g>.";
	public static final String groupPlayerLeft = "<c>%s <i>left <c>%s<i>.";
	public static final String groupYouKicked = "<g>You kicked <c>%s <g>from <c>%s<g>.";
	public static final String groupPlayerKicked = "<c>%s <g>kicked <c>%s <g>from <c>%s<g>.";
	public static final String groupYouWereKicked = "<i>You were kicked from <c>%s<i>.";
	public static final String groupCheckRank = "<i>Your rank in <c>%s <i>is <n>%s<i>.";
	public static final String groupBadRank= "<b>Unknown rank, use:<i> OWNER, ADMIN, OFFICER, BUILDER, MEMBER";
	public static final String groupSetRank= "<g>You set the rank of <c>%s <g>to <i>%s<g>";
	public static final String groupTriedJoin = "<c>%s <i>tried to join <c>%s<i>.";
	public static final String groupUseTransfer = "<b>You can't set another owner.";
	public static final String groupNotLeaveOwner = "<b>The group owner cannot leave.";
	public static final String groupTransferred = "<g>You transferred <c>%s <g>to <c>%s<g>.";
	public static final String groupPlayerTransferred = "<c>%s <i>transferred <c>%s <i>to you.";
	public static final String groupAutoJoinEnabled = "<i>Set auto-join status to <g>enabled<i>.";
	public static final String groupAutoJoinDisabled = "<i>Set auto-join status to <b>disabled<i>.";
	public static final String groupJoinHelp = "<i>Use '<c>/f join <c>%s<i>' to join.";
	public static final String groupLeaveHelp = "<i>Use '<c>/f leave <c>%s<i>' to leave.";
	public static final String groupTransferHelp = "<i>Use '<c>/f transfer <c>%s<i>' to transfer the group.";
	public static final String groupMute = "<i>Chat on group <c>%s <b>muted<i>.";
	public static final String groupUnmute = "<i>Chat on group <c>%s <g>unmuted<i>.";
	
	
	// CHAT
	public static final String chatPlayerNowOffline = "<i>You were chatting with <c>%s <i>who is now offline.";
	public static final String chatMovedGlobal = "<i>Moved to global chat.";
	public static final String chatMovedServerBcast = "<h>Moved to server broadcast chat.";
	public static final String chatMovedGroup = "<i>Moved to <c>%s <i>group chat.\n";
	public static final String chatMovedGroupHelp = "<i>Use '<c>/f c' <i>to return to global chat.";
	public static final String chatNoOneHears = "<silver>No one hears you.";
	public static final String chatChattingWith = "<lp>You are now chatting with <c>%s<lp>.";
	public static final String chatNoReply = "<lp>You have no one to reply to.";
	public static final String chatWillReplyTo = "<lp>You will reply to <c>%s<lp>.";
	public static final String chatPlayerOffline = "<c>%s <b>is offline.";
	public static final String chatOfflineActivity = "<i>There was some activity while you were offline:";
	public static final String chatYouAreIgnored = "<i>The player <c>%s <i>has ignored you.";
	
	
	// BUILD
	public static final String blockInfoEnable = "<i>INFO mode is now <g>enabled<i>.";
	public static final String blockInfoDisable = "<i>INFO mode is now <b>disabled<i>.";
	public static final String blockBypassEnable = "<i>BYPASS mode is now <g>enabled<i>.";
	public static final String blockBypassDisable = "<i>BYPASS mode is now <b>disabled<i>.";
	public static final String blockBuildMode = "<i>Build mode set to <n>%s<i>.";
	public static final String blockBuildModeGroup = "<g>Build mode set to <n>%s <g>for <c>%s<g>.";
	public static final String blockNotMaterial = "<b>That is not a valid reinforcement material.";
	public static final String blockMaterialDepleted = "<n>%s <i>depleted, left fortification mode.";
	public static final String blockNotReinforceable = "<b>That block cannot be reinforced.";
	public static final String blockShowType = "<i>That's a <n>%s<i>.";
	public static final String blockShowInfo = "<b>%s with <n>%s <b>at <n>%s%% <b>health.";
	public static final String blockShowInfoAccess = "<g>%s to <c>%s <g>with <n>%s <g>at <n>%s%% <g>health.";
	public static final String blockShowInfoSpecial = "<n>%s <b>is %s with <n>%s <b>at <n>%s%% <b>health.";
	public static final String blockShowInfoAccessSpecial = "<n>%s <g>is %s to <c>%s <g>with <n>%s <g>at <n>%s%% <g>health.";
	public static final String blockMismatched = "<b>You can't do that, mismatched reinforcement.";
	public static final String blockIsLocked = "<b>%s is locked.";
	public static final String blockIsLockedSpecial = "<n>%s <b>is locked.";
	public static final String blockAdminBypassEnable = "<h>ADMIN BYPASS <i>mode is now <g>enabled<i>.";
	public static final String blockAdminBypassDisable = "<h>ADMIN BYPASS <i>mode is now <b>disabled<i>.";
	public static final String blockNoReinforcement = "<i>No reinforcement.";
	public static final String blockFailedToReinforce = "<b>Failed to create reinforcement.";
	public static final String blockNoChange = "<i>No change was made.";
	public static final String blockChanged = "<i>Changed to <n>%s <i>with <n>%s<i>.";
	public static final String blockCantPlace = "<i>You cannot place this block.";
	public static final String blockMaterialHasLore = "<b>You cannot reinforce with lore items.";
	public static final String blockNoAccess = "<i>You don't have access to do that.";
	
	
	// ADMIN
	public static final String adminPlayerModifyBan = "Your account is being modified. \nPlease wait a minute before logging back in.";
	public static final String adminNameExists = "<b>A player with the name <c>%s <b>already exists.";
	public static final String adminChangedPlayerName = "<g>The player <c>%s <g>is now known as <c>%s<g>.";
	public static final String adminRemovedPlayer = "<g>You deleted the player <c>%s<g>.";
	public static final String adminBannedPlayer = "<g>You banned the player <c>%s<g> with the reason \"<i>%s<g>\".";
	public static final String adminUnbannedPlayer = "<g>You unbanned the player <c>%s<g>.";
	public static final String adminPlayerNotBanned = "<b>The player <c>%s <b>is not banned.";
	public static final String adminYourNameIsNow = "<i>Your name has been set to <c>%s<i>.";
	public static final String adminYouBypassed = "<h>You bypassed this block on <c>%s<h>.";
	public static final String adminSetSpawn = "<g>You set the spawn location.";
	public static final String adminInvalidItem = "<c>That item does not exist.";
	public static final String adminVanished = "<teal>You have vanished. Poof.";
	public static final String adminUnvanished = "<teal>You have become visible.";
	public static final String adminFlyOn = "<i>You are now flying.";
	public static final String adminFlyOff = "<i>You stopped flying.";
	public static final String adminInvalidMode = "<b>Invalid game mode.";
	public static final String adminUpdatedMode = "<g>Updated game mode to <c>%s<g>.";
	
	
	
	// SNITCH
	public static final String snitchNotFound = "<b>No snitch was found.";
	public static final String snitchCleared = "<g>Snitch entries cleared.";
	public static final String snitchEntry = "<c>%s entry at <a>%s <n>[%d %d %d %s]";
	public static final String snitchLoggedIn = "<c>%s login at <a>%s <n>[%d %d %d %s]";
	public static final String snitchLoggedOut = "<c>%s logout at <a>%s <n>[%d %d %d %s]";
	public static final String snitchNotifyEnabled = "<i>Notifications for this snitch are now <g>on<i>.";
	public static final String snitchNotifyDisabled = "<i>Notifications for this snitch are now <b>off<i>.";
	public static final String snitchNotifyWasPlaced = "<i>You broke a snitch placed by <c>%s <i>on <a>%s.";
	public static final String snitchMute = "<i>Snitches on group <c>%s <b>muted<i>.";
	public static final String snitchUnmute = "<i>Snitches on group <c>%s <g>unmuted<i>.";
	
	
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
	public static final String factoryMustHoldStick = "<b>You must be holding a single stick to do this.";
	public static final String factoryCreatedBaseFactory = "<g>You created a factory base.";
	public static final String factoryCreatedProspector = "<g>You created a farm prospector.";
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
	public static final String factoryFertility = "<i>Chunk fertility is <c>%d%%";
	
	
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
	public static String unknownCommand = "Unknown command. Type \"/help\" for help.";
}
