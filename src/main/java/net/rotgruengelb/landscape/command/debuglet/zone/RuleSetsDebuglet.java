package net.rotgruengelb.landscape.command.debuglet.zone;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.rotgruengelb.landscape.feature.zones.rule.AvailableRuleSets;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;

import java.util.Map;

public class RuleSetsDebuglet {
	public static int showAvailableRuleSets(CommandContext<ServerCommandSource> ctx) {
		Map<String, RuleSet> rulesets = AvailableRuleSets.getRuleSets();
		for (String ruleSet : rulesets.keySet()) {
			ctx.getSource()
					.sendMessage(Text.literal("ruleset: " + ruleSet + " -> " + AvailableRuleSets.getRuleSetByIdentifier(ruleSet)
							.getName()));
		}
		return 1;
	}

	public static int showRuleSetRules(CommandContext<ServerCommandSource> ctx) {
		String ruleSetName = StringArgumentType.getString(ctx, "ruleset_name");
		RuleSet ruleSet = AvailableRuleSets.getRuleSetByIdentifier(ruleSetName);
		if (ruleSet == null) {
			ctx.getSource()
					.sendError(Text.literal("No RuleSet with name " + ruleSetName + " found"));
			return 0;
		}
		Map<String, Boolean> rules = ruleSet.getRules();
		for (String rule : rules.keySet()) {
			ctx.getSource().sendMessage(Text.literal("rule: " + rule + " -> " + rules.get(rule)));
		}
		return 1;
	}
}
