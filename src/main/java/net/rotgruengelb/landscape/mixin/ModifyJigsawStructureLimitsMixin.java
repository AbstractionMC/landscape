package net.rotgruengelb.landscape.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.DataResult;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.rotgruengelb.landscape.Landscape;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(JigsawStructure.class)
public class ModifyJigsawStructureLimitsMixin {

	/* method_41662: instance lambda in CODEC assignment */
	@ModifyArg(
			method = "method_41662", at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"
	), index = 1
	)
	private static int codec__expand_size(int i) { return i * 99; }

	/* method_41662: instance lambda in CODEC assignment */
	@ModifyArg(
			method = "method_41662", at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"
	), index = 1
	)
	private static int codec__expand_maxDistanceFromCenter(int i) { return i * 99; }

	@ModifyExpressionValue(
			method = "validate", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/gen/structure/JigsawStructure;maxDistanceFromCenter:I",
			opcode = Opcodes.GETFIELD
	)
	)
	private static int validate__bypass_maxDistanceFromCenter_checks(int i) { return 0; }

	@Inject(method = "validate", at = @At(value = "RETURN"))
	private static void validate__return(JigsawStructure structure, CallbackInfoReturnable<DataResult<JigsawStructure>> cir) {
		if (structure.maxDistanceFromCenter > 128 || structure.maxDistanceFromCenter + 12 > 128) {
			Landscape.LOGGER.warn("Found Structure with maxDistanceFromCenter of " + structure.maxDistanceFromCenter + " which is greater than the vanilla limit of 128.\n" + "\tFound in: " + parse_object_string(structure) + "(startPool:" + parse_startPool_string(structure.startPool) + ")");
		}
	}

	@Unique
	private static String parse_startPool_string(Object input) {
		String regex = "ResourceKey\\[([^\\]]+)\\]";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input.toString());
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}

	@Unique
	private static String parse_object_string(Object input) {
		String regex = "([a-zA-Z]+)\\.(\\w+@\\w+)";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input.toString());
		if (matcher.find()) {
			String first = matcher.group(1);
			String second = matcher.group(2);
			return "minecraft[...]" + first + "." + second;
		} else {
			return "";
		}
	}
}
