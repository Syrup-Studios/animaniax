package net.syrupstudios.animaniax.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Single-page field notebook screen for the Animaniax manual.
 */
public final class ManualScreen extends Screen {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("animaniax", "textures/gui/book.png");
    private static final int TEX_SIZE = 256;

    // Visible notebook cover region inside book.png.
    private static final int BOOK_U = 20;
    private static final int BOOK_V = 1;
    private static final int BOOK_WIDTH = 146;
    private static final int BOOK_HEIGHT = 180;

    // Interior paper margins keeping clear of the spiral rings and curled corner.
    private static final int PAGE_LEFT = 16;
    private static final int TEXT_WIDTH = 112;
    private static final int HEADER_Y = 11;
    private static final int DIVIDER_Y = 24;
    private static final int BODY_TOP = 28;
    private static final int BODY_BOTTOM = 150;
    private static final int LINE_STEP = 10;
    private static final int INDEX_TOP = 30;
    private static final int INDEX_ROW_HEIGHT = 13;
    private static final int FOOTER_Y = 155;

    // Arrow sprite coordinates in the lower sheet of book.png.
    private static final int ARROW_U_CURVED_NORMAL = 3;
    private static final int ARROW_U_CURVED_HOVER = 26;
    private static final int ARROW_V_CURVED_NEXT = 229;
    private static final int ARROW_V_CURVED_PREV = 242;
    private static final int ARROW_CURVED_W = 18;
    private static final int ARROW_CURVED_H = 10;

    private static final int ARROW_U_STRAIGHT_NORMAL = 48;
    private static final int ARROW_U_STRAIGHT_HOVER = 70;
    private static final int ARROW_V_STRAIGHT_BACK = 242;
    private static final int ARROW_STRAIGHT_W = 18;
    private static final int ARROW_STRAIGHT_H = 11;

    private static final int COLOR_TITLE = 0xFF1B2E18;
    private static final int COLOR_BODY = 0xFF273324;
    private static final int COLOR_MUTED = 0xFF566B51;
    private static final int COLOR_LINK = 0xFF234721;
    private static final int COLOR_LINK_HOVER = 0xFF134E10;
    private static final int COLOR_ROW_HOVER_BG = 0x264B7A3A;
    private static final int COLOR_RULE = 0xFFA7C1A2;

    private static final String[] TOPIC_KEYS = {
            "overview", "care", "farm", "poultry", "companions", "small_animals", "housing", "tools", "products"
    };

    private static int lastPage = 0;

    private final List<PageSlice> pages = new ArrayList<>();
    private final List<TopicEntry> topicEntries = new ArrayList<>();

    private int bookX;
    private int bookY;
    private int currentPage = lastPage;

    private PageArrowButton prevButton;
    private PageArrowButton nextButton;
    private PageArrowButton contentsButton;

    public ManualScreen() {
        super(Component.translatable("item.animaniax.manual"));
    }

    @Override
    protected void init() {
        bookX = (this.width - BOOK_WIDTH) / 2;
        bookY = (this.height - BOOK_HEIGHT) / 2;

        buildPages();

        Component prevLabel = Component.translatable("gui.animaniax.manual.previous");
        Component nextLabel = Component.translatable("gui.animaniax.manual.next");
        Component contentsLabel = Component.translatable("gui.animaniax.manual.contents");

        prevButton = addRenderableWidget(new PageArrowButton(
                bookX + 18, bookY + FOOTER_Y,
                ARROW_CURVED_W, ARROW_CURVED_H,
                ARROW_U_CURVED_NORMAL, ARROW_U_CURVED_HOVER, ARROW_V_CURVED_PREV,
                prevLabel,
                () -> setPage(currentPage - 1, false)
        ));
        prevButton.setTooltip(Tooltip.create(prevLabel));

        nextButton = addRenderableWidget(new PageArrowButton(
                bookX + 104, bookY + FOOTER_Y,
                ARROW_CURVED_W, ARROW_CURVED_H,
                ARROW_U_CURVED_NORMAL, ARROW_U_CURVED_HOVER, ARROW_V_CURVED_NEXT,
                nextLabel,
                () -> setPage(currentPage + 1, false)
        ));
        nextButton.setTooltip(Tooltip.create(nextLabel));

        contentsButton = addRenderableWidget(new PageArrowButton(
                bookX + PAGE_LEFT, bookY + HEADER_Y,
                ARROW_STRAIGHT_W, ARROW_STRAIGHT_H,
                ARROW_U_STRAIGHT_NORMAL, ARROW_U_STRAIGHT_HOVER, ARROW_V_STRAIGHT_BACK,
                contentsLabel,
                () -> setPage(0, false)
        ));
        contentsButton.setTooltip(Tooltip.create(contentsLabel));

        syncButtons();
        narratePage();
    }

    private void buildPages() {
        pages.clear();
        topicEntries.clear();

        pages.add(new PageSlice(-1, Component.empty(), List.of(), List.of(), true));

        for (int t = 0; t < TOPIC_KEYS.length; t++) {
            int startPage = pages.size();
            Component topicLabel = Component.translatable("gui.animaniax.manual.topic." + TOPIC_KEYS[t]);
            topicEntries.add(new TopicEntry(t, topicLabel, startPage));

            Component heading = Component.translatable("gui.animaniax.manual.page." + t + ".title")
                    .withStyle(ChatFormatting.BOLD);
            List<FormattedCharSequence> headingLines = font.split(heading, TEXT_WIDTH);
            List<FormattedCharSequence> bodyLines = font.split(
                    Component.translatable("gui.animaniax.manual.page." + t + ".text"), TEXT_WIDTH
            );

            int firstBodyY = BODY_TOP + headingLines.size() * LINE_STEP + 4;
            int firstPageCapacity = Math.max(1, (BODY_BOTTOM - firstBodyY) / LINE_STEP);
            int nextPageCapacity = Math.max(1, (BODY_BOTTOM - BODY_TOP) / LINE_STEP);

            if (bodyLines.isEmpty()) {
                pages.add(new PageSlice(t, topicLabel, headingLines, List.of(), false));
                continue;
            }

            int cursor = 0;
            boolean first = true;
            while (cursor < bodyLines.size()) {
                int capacity = first ? firstPageCapacity : nextPageCapacity;
                int end = Math.min(bodyLines.size(), cursor + capacity);
                List<FormattedCharSequence> sliceHeading = first ? headingLines : List.of();
                List<FormattedCharSequence> sliceBody = bodyLines.subList(cursor, end);
                pages.add(new PageSlice(t, topicLabel, sliceHeading, sliceBody, false));
                cursor = end;
                first = false;
            }
        }

        currentPage = Mth.clamp(currentPage, 0, Math.max(0, pages.size() - 1));
        lastPage = currentPage;
    }

    private void syncButtons() {
        boolean hasPages = !pages.isEmpty();
        prevButton.visible = hasPages && currentPage > 0;
        prevButton.active = prevButton.visible;
        nextButton.visible = hasPages && currentPage < pages.size() - 1;
        nextButton.active = nextButton.visible;
        contentsButton.visible = hasPages && currentPage > 0;
        contentsButton.active = contentsButton.visible;
    }

    private void setPage(int targetPage, boolean playSound) {
        if (pages.isEmpty()) {
            return;
        }
        int clamped = Mth.clamp(targetPage, 0, pages.size() - 1);
        if (clamped == currentPage) {
            return;
        }
        currentPage = clamped;
        lastPage = currentPage;
        syncButtons();
        if (playSound && minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
        narratePage();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        graphics.blit(TEXTURE, bookX, bookY, BOOK_U, BOOK_V, BOOK_WIDTH, BOOK_HEIGHT, TEX_SIZE, TEX_SIZE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (pages.isEmpty()) {
            return;
        }

        PageSlice page = pages.get(currentPage);
        graphics.fill(
                bookX + PAGE_LEFT,
                bookY + DIVIDER_Y,
                bookX + PAGE_LEFT + TEXT_WIDTH,
                bookY + DIVIDER_Y + 1,
                COLOR_RULE
        );

        if (page.contents()) {
            renderContentsPage(graphics, mouseX, mouseY);
        } else {
            renderTopicPage(graphics, page);
        }

        String pageText = (currentPage + 1) + " / " + pages.size();
        int pageX = bookX + 70 - font.width(pageText) / 2;
        graphics.drawString(font, pageText, pageX, bookY + FOOTER_Y + 1, COLOR_MUTED, false);
    }

    private void renderContentsPage(GuiGraphics graphics, int mouseX, int mouseY) {
        Component title = Component.translatable("gui.animaniax.manual.contents").withStyle(ChatFormatting.BOLD);
        int titleX = bookX + PAGE_LEFT + (TEXT_WIDTH - font.width(title)) / 2;
        graphics.drawString(font, title, titleX, bookY + 13, COLOR_TITLE, false);

        for (int i = 0; i < topicEntries.size(); i++) {
            TopicEntry entry = topicEntries.get(i);
            int rowY = bookY + INDEX_TOP + i * INDEX_ROW_HEIGHT;
            boolean hovered = isOverIndexRow(mouseX, mouseY, i);

            if (hovered) {
                graphics.fill(
                        bookX + PAGE_LEFT - 2,
                        rowY - 1,
                        bookX + PAGE_LEFT + TEXT_WIDTH + 2,
                        rowY + INDEX_ROW_HEIGHT - 2,
                        COLOR_ROW_HOVER_BG
                );
            }

            String pageNumber = String.valueOf(entry.targetPage() + 1);
            int numWidth = font.width(pageNumber);
            graphics.drawString(
                    font,
                    pageNumber,
                    bookX + PAGE_LEFT + TEXT_WIDTH - numWidth,
                    rowY + 1,
                    COLOR_MUTED,
                    false
            );

            FormattedCharSequence label = ellipsize(entry.label(), TEXT_WIDTH - numWidth - 6);
            graphics.drawString(
                    font,
                    label,
                    bookX + PAGE_LEFT + 2,
                    rowY + 1,
                    hovered ? COLOR_LINK_HOVER : COLOR_LINK,
                    false
            );
        }
    }

    private void renderTopicPage(GuiGraphics graphics, PageSlice page) {
        FormattedCharSequence runningHeader = ellipsize(page.runningHeader(), TEXT_WIDTH - 22);
        int headerWidth = font.width(runningHeader);
        graphics.drawString(
                font,
                runningHeader,
                bookX + PAGE_LEFT + TEXT_WIDTH - headerWidth,
                bookY + 13,
                COLOR_MUTED,
                false
        );

        int y = bookY + BODY_TOP;
        for (FormattedCharSequence line : page.headingLines()) {
            graphics.drawString(font, line, bookX + PAGE_LEFT, y, COLOR_TITLE, false);
            y += LINE_STEP;
        }
        if (!page.headingLines().isEmpty()) {
            y += 4;
        }
        for (FormattedCharSequence line : page.bodyLines()) {
            graphics.drawString(font, line, bookX + PAGE_LEFT, y, COLOR_BODY, false);
            y += LINE_STEP;
        }
    }

    private FormattedCharSequence ellipsize(Component text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text.getVisualOrderText();
        }
        int ellipsisWidth = font.width("...");
        if (maxWidth <= ellipsisWidth) {
            return Language.getInstance().getVisualOrder(font.substrByWidth(text, Math.max(0, maxWidth)));
        }
        FormattedText prefix = font.substrByWidth(text, maxWidth - ellipsisWidth);
        return Language.getInstance().getVisualOrder(
                FormattedText.composite(prefix, FormattedText.of("..."))
        );
    }

    private boolean isOverIndexRow(double mouseX, double mouseY, int rowIndex) {
        int left = bookX + PAGE_LEFT - 2;
        int right = bookX + PAGE_LEFT + TEXT_WIDTH + 2;
        int top = bookY + INDEX_TOP + rowIndex * INDEX_ROW_HEIGHT - 1;
        int bottom = top + INDEX_ROW_HEIGHT - 1;
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && !pages.isEmpty() && pages.get(currentPage).contents()) {
            for (int i = 0; i < topicEntries.size(); i++) {
                if (isOverIndexRow(mouseX, mouseY, i)) {
                    setPage(topicEntries.get(i).targetPage(), true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            setPage(currentPage - 1, true);
            return true;
        }
        if (scrollY < 0) {
            setPage(currentPage + 1, true);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            setPage(currentPage - 1, true);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            setPage(currentPage + 1, true);
            return true;
        }
        if ((keyCode == GLFW.GLFW_KEY_HOME || keyCode == GLFW.GLFW_KEY_BACKSPACE) && currentPage > 0) {
            setPage(0, true);
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
        if (pages.isEmpty()) {
            return;
        }
        PageSlice page = pages.get(currentPage);
        NarrationElementOutput pageNarration = output.nest();
        Component title = page.contents()
                ? Component.translatable("gui.animaniax.manual.contents")
                : Component.translatable("gui.animaniax.manual.page." + page.topicIndex() + ".title");
        pageNarration.add(NarratedElementType.TITLE, title);
        pageNarration.add(NarratedElementType.POSITION,
                Component.translatable("gui.animaniax.manual.page_count", currentPage + 1, pages.size()));
        if (!page.contents()) {
            pageNarration.add(NarratedElementType.HINT,
                    Component.translatable("gui.animaniax.manual.page." + page.topicIndex() + ".text"));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record TopicEntry(int topicIndex, Component label, int targetPage) {}

    private record PageSlice(
            int topicIndex,
            Component runningHeader,
            List<FormattedCharSequence> headingLines,
            List<FormattedCharSequence> bodyLines,
            boolean contents
    ) {}

    private static final class PageArrowButton extends AbstractButton {
        private final int uNormal;
        private final int uHovered;
        private final int v;
        private final Runnable action;

        private PageArrowButton(
                int x, int y, int width, int height,
                int uNormal, int uHovered, int v,
                Component title, Runnable action
        ) {
            super(x, y, width, height, title);
            this.uNormal = uNormal;
            this.uHovered = uHovered;
            this.v = v;
            this.action = action;
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int u = isHoveredOrFocused() ? uHovered : uNormal;
            graphics.blit(TEXTURE, getX(), getY(), u, v, width, height, TEX_SIZE, TEX_SIZE);
        }

        @Override
        public void playDownSound(SoundManager soundManager) {
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
