/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.rotgruengelb.landscape.block.enums;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum ZoneBlockMode implements StringIdentifiable {
	TRIGGER("trigger"),
	CUSTOM_RULESET("custom_ruleset"),
	DENY_BREAK("deny_break"),
	DENY_PLACE("deny_place"),
	ALLOW_BREAK("allow_break"),
	ALLOW_PLACE("allow_place");

	private static final String TRANSLATABLE_PREFIX = "text.landscape.zone_block.mode_info.";
	private final String name;

	ZoneBlockMode(String name) { this.name = name; }

	@Override
	public String asString() { return this.name; }

	public Text asText() { return Text.translatable(TRANSLATABLE_PREFIX + this.name); }
}

