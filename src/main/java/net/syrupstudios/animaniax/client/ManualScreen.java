package net.syrupstudios.animaniax.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ManualScreen extends Screen {
    private static final int PAGE_COUNT = 9;
    private static final String[] TOPIC_KEYS = {
            "overview", "care", "farm", "poultry", "companions", "small_animals", "housing", "tools", "products"
    };
    private int page;
    private int scrollLine;

    public ManualScreen() {
        super(Component.translatable("item.animaniax.manual"));
    }

    @Override
    protected void init() {
        if (height < 170) {
            addRenderableWidget(Button.builder(Component.translatable("gui.animaniax.manual.previous"), button -> {
                page = Math.max(0, page - 1);
                scrollLine = 0;
                narratePage();
            }).pos(width / 2 - 84, Math.max(0, height - 24)).size(80, 20).build());
            addRenderableWidget(Button.builder(Component.translatable("gui.animaniax.manual.next"), button -> {
                page = Math.min(PAGE_COUNT - 1, page + 1);
                scrollLine = 0;
                narratePage();
            }).pos(width / 2 + 4, Math.max(0, height - 24)).size(80, 20).build());
            narratePage();
            return;
        }
        int buttonWidth = Math.min(92, (width - 28) / 3);
        int gap = 4;
        int left = (width - (buttonWidth * 3 + gap * 2)) / 2;
        int top = height - 74;
        for (int i = 0; i < PAGE_COUNT; i++) {
            int index = i;
            addRenderableWidget(Button.builder(Component.translatable("gui.animaniax.manual.topic." + TOPIC_KEYS[i]),
                    button -> { page = index; scrollLine = 0; narratePage(); })
                    .pos(left + i % 3 * (buttonWidth + gap), top + i / 3 * 23)
                    .size(buttonWidth, 20).build());
        }
        narratePage();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        boolean compact = height < 170;
        boolean tiny = height < 110;
        graphics.drawCenteredString(font, Component.translatable("gui.animaniax.manual.page." + page + ".title"), width / 2,
                tiny ? 2 : compact ? 8 : 28, 0xFFFFFF);

        int textWidth = Math.min(width - 40, 360);
        int x = (width - textWidth) / 2;
        int y = tiny ? 20 : compact ? 30 : 54;
        var lines = font.split(Component.translatable("gui.animaniax.manual.page." + page + ".text"), textWidth);
        int infoY = compact ? height - 44 : height - 83;
        int visibleLines = Math.max(1, ((tiny ? height - 24 : infoY) - y - 8) / 12);
        scrollLine = Math.max(0, Math.min(scrollLine, Math.max(0, lines.size() - visibleLines)));
        for (int i = scrollLine; i < Math.min(lines.size(), scrollLine + visibleLines); i++) {
            graphics.drawString(font, lines.get(i), x, y, 0xE0E0E0);
            y += 12;
        }
        if (!tiny) graphics.drawCenteredString(font, Component.translatable("gui.animaniax.manual.page_count", page + 1, PAGE_COUNT),
                width / 2, infoY, 0xA0A0A0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 263 || keyCode == 266) {
            page = Math.max(0, page - 1);
            scrollLine = 0;
            narratePage();
            return true;
        }
        if (keyCode == 262 || keyCode == 267) {
            page = Math.min(PAGE_COUNT - 1, page + 1);
            scrollLine = 0;
            narratePage();
            return true;
        }
        if (keyCode == 265 || keyCode == 264) {
            scrollLine = Math.max(0, scrollLine + (keyCode == 265 ? -1 : 1));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void narratePage() {
        triggerImmediateNarration(true);
    }

    @Override
    protected void updateNarrationState(NarrationElementOutput output) {
        super.updateNarrationState(output);
        NarrationElementOutput pageNarration = output.nest();
        pageNarration.add(NarratedElementType.TITLE,
                Component.translatable("gui.animaniax.manual.page." + page + ".title"));
        pageNarration.add(NarratedElementType.POSITION,
                Component.translatable("gui.animaniax.manual.page_count", page + 1, PAGE_COUNT));
        pageNarration.add(NarratedElementType.HINT,
                Component.translatable("gui.animaniax.manual.page." + page + ".text"));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scrollLine = Math.max(0, scrollLine - (int) Math.signum(scrollY) * 2);
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
