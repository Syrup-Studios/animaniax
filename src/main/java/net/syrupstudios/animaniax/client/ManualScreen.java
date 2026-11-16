package net.syrupstudios.animaniax.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ManualScreen extends Screen {
    private static final int PAGE_COUNT = 9;
    private int page;

    public ManualScreen() {
        super(Component.translatable("item.animaniax.manual"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.translatable("gui.animaniax.manual.previous"), button ->
                page = Math.max(0, page - 1))
                .pos(width / 2 - 84, Math.max(0, height - 28)).size(80, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.animaniax.manual.next"), button ->
                page = Math.min(PAGE_COUNT - 1, page + 1))
                .pos(width / 2 + 4, Math.max(0, height - 28)).size(80, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, Component.translatable("gui.animaniax.manual.page." + page + ".title"),
                width / 2, 24, 0xFFFFFF);
        int textWidth = Math.min(width - 40, 360);
        int x = (width - textWidth) / 2;
        int y = 48;
        var lines = font.split(Component.translatable("gui.animaniax.manual.page." + page + ".text"), textWidth);
        for (var line : lines) {
            if (y > height - 48) break;
            graphics.drawString(font, line, x, y, 0xE0E0E0);
            y += 12;
        }
        graphics.drawCenteredString(font, Component.translatable("gui.animaniax.manual.page_count", page + 1, PAGE_COUNT),
                width / 2, Math.max(0, height - 46), 0xA0A0A0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
