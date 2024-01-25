package net.rotgruengelb.landscape.feature.zones.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.rotgruengelb.landscape.util.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.rotgruengelb.landscape.Landscape.LOGGER;

public class AvailableRuleSets {

	private static Map<String, RuleSet> RULE_SETS = new HashMap<>();

	public static void apply(Map<String, RuleSet> resource) { RULE_SETS = resource; }

	public static Map<String, RuleSet> getRuleSets() { return RULE_SETS; }

	public static Map<String, RuleSet> load(ResourceManager manager) {
		Map<String, RuleSet> ruleSets = new HashMap<>();
		for (Identifier id : manager.findAllResources("rulesets", path -> path.getPath()
				.endsWith(".json")).keySet()) {
			if (manager.getResource(id).isEmpty()) {
				continue;
			}
			try (InputStream stream = manager.getResource(id).get().getInputStream()) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(stream);

				if (rootNode.has("name") && rootNode.has("rules")) {
					String name = rootNode.get("name").asText();
					JsonNode rulesNode = rootNode.get("rules");

					String cleanId = StringUtils.removeFileExtension(id.toString());

					RuleSet ruleSet = new RuleSet(name, new Identifier(cleanId));

					Iterator<Map.Entry<String, JsonNode>> fields = rulesNode.fields();
					while (fields.hasNext()) {
						Map.Entry<String, JsonNode> entry = fields.next();
						String ruleName = entry.getKey();
						boolean ruleValue = entry.getValue().asBoolean();
						ruleSet.add(ruleName, ruleValue);
					}
					ruleSets.put(cleanId, ruleSet);
				}
			} catch (Exception e) {
				LOGGER.error("Error occurred while loading resource json" + id.toString(), e);
			}
		}
		return ruleSets;
	}

	public static RuleSet getRuleSetByIdentifier(String name) { return RULE_SETS.get(name); }
}
