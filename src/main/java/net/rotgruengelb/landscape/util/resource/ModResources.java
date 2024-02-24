package net.rotgruengelb.landscape.util.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.rotgruengelb.landscape.Landscape;
import net.rotgruengelb.landscape.feature.zones.rule.AvailableRuleSets;
import net.rotgruengelb.landscape.feature.zones.rule.RuleSet;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModResources {

	public static void registerModResourceReloadListeners() {

		Landscape.LOGGER.debug("Registering ModResourceReloadListeners for " + Landscape.MOD_ID);

		completeRuleSetResourceRegistration(ResourceManagerHelper.get(ResourceType.SERVER_DATA));

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			completeRuleSetResourceRegistration(ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES));
		}
	}

	private static void completeRuleSetResourceRegistration(ResourceManagerHelper helper) {
		helper.registerReloadListener(new SimpleResourceReloadListener<Map<Identifier, RuleSet>>() {
			@Override
			public Identifier getFabricId() { return new Identifier(Landscape.MOD_ID, "rulesets"); }

			@Override
			public CompletableFuture<Map<Identifier, RuleSet>> load(ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.supplyAsync(() -> AvailableRuleSets.load(manager), executor);
			}

			@Override
			public CompletableFuture<Void> apply(Map<Identifier, RuleSet> resource, ResourceManager manager, Profiler profiler, Executor executor) {
				return CompletableFuture.runAsync(() -> AvailableRuleSets.apply(resource), executor);
			}
		});
	}
}
