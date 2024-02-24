package net.rotgruengelb.landscape.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.LandscapeClient;
import net.rotgruengelb.landscape.block.ModBlocks;
import net.rotgruengelb.landscape.block.ZoneBlock;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.block.enums.ZoneBlockMode;
import net.rotgruengelb.landscape.network.UpdateZoneBlockC2SPacket;
import net.rotgruengelb.nixienaut.ClampedNum;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

import static net.rotgruengelb.landscape.network.constant.PacketIds.C2S_UPDATE_ZONE_BLOCK_PACKET_ID;

@Environment(value = EnvType.CLIENT)
public class ZoneBlockScreen extends Screen {
	private static final ImmutableList<ZoneBlockMode> MODES = ImmutableList.copyOf(ZoneBlockMode.values());
	private static final Text SHOW_ZONES_TEXT = Text.translatable("text.landscape.zone_block.screen.show_zones");
	private static final Text SET_PRIORITY_TEXT = Text.translatable("text.landscape.zone_block.screen.set_priority");
	private static final Text title = Text.translatable("text.landscape.zone_block.screen.title");
	private static final Text SET_RULESET_TEXT = Text.translatable("text.landscape.zone_block.screen.set_ruleset");
	private final BlockPos pos;
	private final ZoneBlockMode originalMode;
	private final boolean originalShowZones;
	private final String newRuleSet;
	private final int originalPriority;
	private final ClampedNum<Integer> newPriority = new ClampedNum<>(0, 99);
	private NbtCompound originalZones;
	private String newZones;
	private ZoneBlockMode newMode;
	private boolean newShowZones;
	private CyclingButtonWidget<Boolean> buttonShowZones;
	private CyclingButtonWidget<ZoneBlockMode> buttonMode;
	private TextFieldWidget inputZones;
	private TextFieldWidget inputPriority;
	private TextFieldWidget inputRuleSet;

	public ZoneBlockScreen(ZoneBlockBlockEntity zoneBlock) {
		super(title);
		this.pos = zoneBlock.getPos();
		this.originalMode = zoneBlock.getMode();
		this.originalShowZones = zoneBlock.shouldShowZones();
		this.originalPriority = zoneBlock.getZoneManagerContext().priority();
		this.newPriority.adjustAndSetValue(this.originalPriority);
		this.newRuleSet = zoneBlock.getRuleSet().getIdentifierString();
		this.newMode = this.originalMode;
		this.newShowZones = this.originalShowZones;
	}

	private void done() {
		this.clientBlockState(this.newMode);
		LandscapeClient.C_LOGGER.debug("Sending UpdateZoneBlockC2SPacket for " + this.pos + " with mode " + this.newMode + " and showZones " + this.newShowZones + " and priority " + this.newPriority + " and ruleSet " + this.newRuleSet + " and zones " + this.newZones);
		ClientPlayNetworking.send(C2S_UPDATE_ZONE_BLOCK_PACKET_ID, new UpdateZoneBlockC2SPacket(this.pos, this.newMode, this.newShowZones, this.processZones(), this.processPriority(), this.processRuleSet()).create());
		this.client.setScreen(null);
	}

	private String processRuleSet() { return this.inputRuleSet.getText(); }

	private NbtCompound processZones() {
		try {
			return StringNbtReader.parse(this.inputZones.getText());
		} catch (CommandSyntaxException e) {
			return this.originalZones;
		}
	}

	private int processPriority() {
		try {
			newPriority.adjustAndSetValue(Integer.parseInt(this.inputPriority.getText()));
		} catch (NumberFormatException e) {
			newPriority.adjustAndSetValue(this.originalPriority);
		}
		return newPriority.getValue();
	}

	@Override
	protected void init() {
		this.originalZones = Objects.requireNonNull(Objects.requireNonNull(this.client.world.getBlockEntity(this.pos))
				.createNbt().getCompound("zones"));
		this.newZones = this.originalZones.toString();
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			try {
				this.done();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
				.dimensions(this.width / 2 + 4, 210, 150, 20).build());
		this.buttonShowZones = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.newShowZones)
				.omitKeyText()
				.build(this.width / 2 + 4 + 100, 80, 50, 20, Text.of("Show Zones"), (button, showBoundingBox) -> {
					this.newShowZones = button.getValue();
				}));
		this.buttonMode = this.addDrawableChild(CyclingButtonWidget.builder((ZoneBlockMode value) -> Text.translatable("text.landscape.zone_block.mode." + value.asString()))
				.values(MODES).omitKeyText().initially(this.newMode)
				.build(this.width / 2 - 4 - 150, 185, 100, 20, Text.literal("MODE"), (button, blockMode) -> {
					this.newMode = button.getValue();
					this.clientBlockState(this.newMode);
					this.updateWidgets(this.newMode);
				}));
		this.inputZones = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 40, 300, 20, Text.translatable("text.landscape.zone_block.input_zones")) {
		};
		this.inputZones.setMaxLength(5000);
		this.inputZones.setText(this.newZones);
		this.addSelectableChild(this.inputZones);
		this.inputPriority = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 20, 20, Text.translatable("text.landscape.zone_block.input_priority")) {
			@Override
			public boolean charTyped(char chr, int modifiers) {
				if (!Character.isDigit(chr)) {
					return false;
				}
				return super.charTyped(chr, modifiers);
			}
		};
		this.inputPriority.setMaxLength(2);
		this.inputPriority.setText(this.newPriority.getValue().toString());
		this.addSelectableChild(this.inputPriority);
		this.inputRuleSet = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 300, 20, Text.translatable("text.landscape.zone_block.input_ruleset")) {
		};
		this.inputRuleSet.setMaxLength(200);
		this.inputRuleSet.setText(this.newRuleSet);
		this.addSelectableChild(this.inputRuleSet);
		this.updateWidgets(this.newMode);
		this.clientBlockState(this.newMode);
	}

	private void clientBlockState(ZoneBlockMode mode) {
		if (client != null) {
			client.execute(() -> {
				BlockState blockState = client.world.getBlockState(this.pos);
				if (blockState.isOf(ModBlocks.ZONE_BLOCK)) {
					client.world.setBlockState(this.pos, blockState.with(ZoneBlock.MODE, mode), 2);
				}
			});
		}
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String inputZonesContent = this.inputZones.getText();
		String inputPriorityContent = this.inputPriority.getText();
		String inputRuleSetContent = this.inputRuleSet.getText();
		this.init(client, width, height);
		this.inputRuleSet.setText(inputRuleSetContent);
		this.inputZones.setText(inputZonesContent);
		this.inputPriority.setText(inputPriorityContent);
	}

	private void updateWidgets(ZoneBlockMode mode) {
		// TODO: Will maybe do something for the TRIGGER mode...
		//		switch (mode) {
		//			case TRIGGER: {
		//				Dummy._void();
		//				break;
		//			}
		//			default: {
		//				break;
		//			}
		//		}
	}

	@Override
	public void close() {
		this.client.setScreen(null);
		this.clientBlockState(this.originalMode);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			this.done();
			return true;
		}
		return false;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.inputZones.render(context, mouseX, mouseY, delta);
		this.inputPriority.render(context, mouseX, mouseY, delta);
		this.inputRuleSet.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, SET_RULESET_TEXT, this.width / 2 - 153, 110, 0xA0A0A0);
		context.drawTextWithShadow(this.textRenderer, SET_PRIORITY_TEXT, this.width / 2 - 153, 70, 0xA0A0A0);
		context.drawTextWithShadow(this.textRenderer, SHOW_ZONES_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth(SHOW_ZONES_TEXT), 70, 0xA0A0A0);
		context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 10, 0xFFFFFF);
		context.drawTextWithShadow(this.textRenderer, this.newMode.asText(), this.width / 2 - 153, 174, 0xA0A0A0);
	}

	@Override
	public boolean shouldPause() { return false; }
}



