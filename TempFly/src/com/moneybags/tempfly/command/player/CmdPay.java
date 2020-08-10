package com.moneybags.tempfly.command.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.moneybags.tempfly.TempFly;
import com.moneybags.tempfly.command.TimeCommand;
import com.moneybags.tempfly.time.TimeManager;
import com.moneybags.tempfly.util.U;
import com.moneybags.tempfly.util.V;

public class CmdPay extends TimeCommand {

	@SuppressWarnings("deprecation")
	public CmdPay(TempFly tempfly, CommandSender s, String[] args) {
		if (!V.payable) {
			U.m(s, V.invalidCommand);
			return;
		}
		if (!U.isPlayer(s)) {
			U.m(s, V.invalidSender);
			return;
		}
		if (!U.hasPermission(s, "tempfly.pay")) {
			U.m(s, V.invalidPermission);
			return;
		}
		if (args.length < 3) {
			U.m(s, U.cc("&c/tf pay [player] [amount-> {args=[-s][-m][-h][-d]}]"));
			return;
		}
		OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
		if (p == null || (p != null && !p.isOnline() && !p.hasPlayedBefore())) {
			U.m(s, V.invalidPlayer.replaceAll("\\{PLAYER}", args[1]));
			return;
		}
		
		if ((Player)s == p) {
			U.m(s, V.invalidReciever);
			return;
		}
		double amount = 0;
		amount = quantifyArguments(s, args, 2);
		if (amount <= 0) {
			return;
		}
		amount = Math.floor(amount);
		TimeManager manager = tempfly.getTimeManager();
		Player sender = (Player)s;
		double bal = manager.getTime(sender.getUniqueId());
		if (bal < amount) {
			U.m(s, V.invalidTimeSelf);
			return;
		}
		if ((V.maxTime > -1) && (manager.getTime(p.getUniqueId()) + amount >= V.maxTime)) {
			U.m(s, manager.regexString(V.timeMaxOther, amount)
					.replaceAll("\\{PLAYER}", p.getName()));
			return;
		}
		
		manager.removeTime(sender.getUniqueId(), amount);
		manager.addTime(p.getUniqueId(), amount);
		U.m(s, manager.regexString(V.timeSentOther, amount)
				.replaceAll("\\{PLAYER}", p.getName()));
		if (p.isOnline()) {
			U.m((Player)p, manager.regexString(V.timeSentSelf, amount)
					.replaceAll("\\{PLAYER}", s.getName()));	
		}
	}

}
