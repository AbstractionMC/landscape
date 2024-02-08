package net.rotgruengelb.landscape.feature.zones.rule;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RuleSet {

	private final Map<String, Boolean> rules = new HashMap<>();
	private final String name;
	private final Identifier identifier;

	public Map<String, Boolean> getRules() { return rules; }

	public RuleSet(String name, Identifier identifier) {
		this.name = name;
		this.identifier = identifier;
	}

	public RuleSet add(String rule, boolean value) {
		rules.put(rule, value);
		return this;
	}

	public String getName() { return name; }

	public Identifier getIdentifier() { return identifier; }

	public String getIdentifierString() { return identifier.toString(); }

	public boolean containsRule(String rule) { return rules.containsKey(rule); }

	public boolean getRuleValue(String rule) { return rules.get(rule); }

	public static RuleSet of(Identifier name) {
		return AvailableRuleSets.getRuleSet(Objects.requireNonNullElse(name, AvailableRuleSets.EMPTY_RULESET));
	}

	public static RuleSet of(String name) {
		return of(Identifier.tryParse(name));
	}
}
