package net.rotgruengelb.landscape.feature.zones.rule;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.rotgruengelb.landscape.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static net.rotgruengelb.landscape.Landscape.LOGGER;

public class AvailableRuleSets {

	public static final Identifier EMPTY_RULESET = new Identifier("landscape:rulesets/empty");

	private static Map<Identifier, RuleSet> RULE_SETS = new HashMap<>();

	public static void apply(Map<Identifier, RuleSet> resource) { RULE_SETS = resource; }

	public static Map<Identifier, RuleSet> getRuleSets() { return RULE_SETS; }

	public static Map<Identifier, RuleSet> load(ResourceManager manager) {
		Map<Identifier, RuleSet> ruleSets = new HashMap<>();
		Gson gson = new Gson();

		for (Identifier id : manager.findAllResources("rulesets", path -> path.getPath()
				.endsWith(".json")).keySet()) {
			if (manager.getResource(id).isEmpty()) {
				continue;
			}

			try (InputStream stream = manager.getResource(id).get().getInputStream()) {
				JsonElement jsonElement = gson.fromJson(new InputStreamReader(stream), JsonElement.class);

				if (jsonElement.isJsonObject()) {
					JsonObject rootNode = jsonElement.getAsJsonObject();

					if (rootNode.has("name") && rootNode.has("rules")) {
						String name = rootNode.get("name").getAsString();
						JsonObject rulesNode = rootNode.getAsJsonObject("rules");

						Identifier cleanId = new Identifier(StringUtils.removeFileExtension(id.toString()));

						RuleSet ruleSet = new RuleSet(name, cleanId);

						for (Map.Entry<String, JsonElement> entry : rulesNode.entrySet()) {
							String ruleName = entry.getKey();
							boolean ruleValue = entry.getValue().getAsBoolean();
							ruleSet.add(ruleName, ruleValue);
						}

						ruleSets.put(cleanId, ruleSet);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error occurred while loading resource json" + id.toString(), e);
			}
		}

		return ruleSets;
	}

	public static RuleSet getRuleSet(Identifier name) {
		RuleSet ruleSet = RULE_SETS.get(name);
		return ruleSet != null ? ruleSet : RULE_SETS.get(EMPTY_RULESET);
	}
}
