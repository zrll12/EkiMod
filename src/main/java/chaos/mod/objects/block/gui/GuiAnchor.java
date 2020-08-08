package chaos.mod.objects.block.gui;

import java.io.IOException;

import chaos.mod.init.BlockInit;
import chaos.mod.tileentity.TileEntityAnchor;
import chaos.mod.util.Reference;
import chaos.mod.util.data.station.Station;
import chaos.mod.util.handlers.PacketHandler;
import chaos.mod.util.handlers.StationHandler;
import chaos.mod.util.network.PacketAddStationWorker;
import chaos.mod.util.utils.UtilTranslatable;
import chaos.mod.util.utils.UtilTranslatable.TranslateType;
import chaos.mod.util.utils.UtilTranslatable.UtilTCString;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiAnchor extends GuiScreen {
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MODID, "textures/gui/anchor.png");
	private TileEntityAnchor te;
	private GuiTextField nameText;
	private GuiTextField operatorText;
	private GuiButton buttonClear;
	private GuiButton buttonConfirm;
	private GuiButton buttonSetOp;
	private int x;
	private int y;

	public GuiAnchor(TileEntityAnchor te) {
		this.te = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		x = width / 2 - 90;
		y = height / 2 - 100;
		nameText = new GuiTextField(0, fontRenderer, x + 70, y + 50, 100, 15);
		operatorText = new GuiTextField(1, fontRenderer, x + 177, y, 100, 15);
		buttonClear = new GuiButton(0, x + 70, y + 70, 45, 20, new UtilTranslatable(TranslateType.CONTAINER, "button.clear").getFormattedText());
		buttonConfirm = new GuiButton(1, x + 125, y + 70, 45, 20, new UtilTranslatable(TranslateType.CONTAINER, "anchor.button.confirm").getFormattedText());
		buttonSetOp = new GuiButton(2, x + 177, y + 18, 100, 20, new UtilTranslatable(TranslateType.CONTAINER, "anchor.button.setOP").getFormattedText());
		if (te.isValidStation()) {
			buttonClear.enabled = false;
			buttonConfirm.enabled = false;
			nameText.setText(StationHandler.INSTANCE.getStation(te.getPos()).getName());
			nameText.setFocused(false);
			nameText.setEnabled(false);
		} else {
			buttonSetOp.enabled = false;
			nameText.setFocused(true);
			nameText.setCanLoseFocus(true);
			nameText.setText(new UtilTranslatable(TranslateType.CONTAINER, "anchor.text").getUnformattedText());
			nameText.setMaxStringLength(100);
		}
		buttonList.add(buttonClear);
		buttonList.add(buttonConfirm);
		buttonList.add(buttonSetOp);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(TEXTURES);
		drawTexturedModalRect(x, y, 0, 0, 176, 166);
		// draw anchor
		RenderHelper.disableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		float size = 3.0f;
		GlStateManager.scale(size, size, size);
		float msize = (float) Math.pow(size, -1);
		itemRender.renderItemAndEffectIntoGUI(new ItemStack(BlockInit.ANCHOR), Math.round(x / size) + 5, Math.round(y / size) + 2);
		GlStateManager.scale(msize, msize, msize);
		RenderHelper.enableStandardItemLighting();
		// draw element
		nameText.drawTextBox();
		operatorText.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		// draw string
		fontRenderer.drawString(BlockInit.ANCHOR.getLocalizedName(), (x + 176 / 2) - (fontRenderer.getStringWidth(BlockInit.ANCHOR.getLocalizedName()) / 2), y + 8, 4210752);
		fontRenderer.drawSplitString(new UtilTCString(TranslateType.CONTAINER, "anchor.station.name").getFormattedText() + " " + (te.isValidStation() ? te.getStation().getName() : ""), x + 70, y
				+ 20, 106, 4210752);
		fontRenderer.drawSplitString(new UtilTranslatable(TranslateType.CONTAINER, "anchor.desc").getFormattedText(), x + 5, y + 90, 166, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 0:
			resetText(nameText);
			break;
		case 1:
			if (nameText.getText().isEmpty() || nameText.getText().equalsIgnoreCase(new UtilTranslatable(TranslateType.CONTAINER, "anchor.text").getFormattedText())) {
				return;
			}
			PacketHandler.INSTANCE.sendToServer(new PacketAddStationWorker(new Station(nameText.getText(), te.getPos())));
			buttonClear.enabled = false;
			buttonConfirm.enabled = false;
			nameText.setEnabled(false);
			nameText.setFocused(false);
			break;
		case 2:
			if (operatorText.getText().isEmpty())
				return;
			// PacketHandler
			operatorText.setFocused(false);
		default:
			break;
		}

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		nameText.mouseClicked(mouseX, mouseY, mouseButton);
		operatorText.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!nameText.isFocused() && (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))) {
			mc.player.closeScreen();
		}
		super.keyTyped(typedChar, keyCode);
		nameText.textboxKeyTyped(typedChar, keyCode);
		operatorText.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		nameText.updateCursorCounter();
		operatorText.updateCursorCounter();
		super.updateScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void resetText(GuiTextField text) {
		text.setText("");
		text.setFocused(true);
		updateScreen();
	}
}
