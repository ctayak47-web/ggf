package com.crolclient.client.gui;

import com.crolclient.client.config.ConfigManager;
import com.crolclient.client.gui.component.CategoryPanel;
import com.crolclient.client.gui.component.SearchBar;
import com.crolclient.client.gui.component.SettingsPanel;
import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.theme.Theme;
import com.crolclient.client.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Главный экран меню CrolClient — ClickGUI. Открывается по Right Shift
 * (см. регистрацию хоткея в CrolClient) и по команде /crol.
 * <p>
 * Структура:
 *   - Слева: строка поиска + панели категорий (по одной на ModuleCategory),
 *     каждая со списком своих модулей — клик по строке модуля включает/
 *     выключает его.
 *   - Справа: панель настроек модуля, на который навели/кликнули последним
 *     (слайдеры/чекбоксы/цвета/режимы).
 *   - Внизу: переключатель темы оформления (циклический список тем из
 *     ThemeManager).
 * <p>
 * Экран не ставит игру на паузу (см. isPauseScreen), чтобы соответствовать
 * поведению типичных клиентских ClickGUI.
 */
public class ClickGuiScreen extends Screen {

    private final List<CategoryPanel> categoryPanels = new ArrayList<>();
    private SettingsPanel settingsPanel;
    private SearchBar searchBar;

    private String activeSearchQuery = "";

    public ClickGuiScreen() {
        super(Text.literal("CrolClient"));
    }

    @Override
    protected void init() {
        categoryPanels.clear();

        int startX = 20;
        int startY = 30;
        int gapX = 150;

        searchBar = new SearchBar(startX, 8, 300);

        int i = 0;
        for (ModuleCategory category : ModuleCategory.values()) {
            CategoryPanel panel = new CategoryPanel(category, startX + i * gapX, startY);
            panel.setModules(ModuleManager.getInstance().getModulesByCategory(category));
            categoryPanels.add(panel);
            i++;
        }

        settingsPanel = new SettingsPanel(startX + categoryPanels.size() * gapX + 10, startY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Theme theme = ThemeManager.getInstance().getCurrentTheme();

        // затемнение игрового мира позади меню
        context.fill(0, 0, width, height, 0x88000000);

        context.drawText(textRenderer, Text.literal("CrolClient"), 20, -14 + 30, theme.accentColorArgb(), true);

        searchBar.render(context, theme);

        for (CategoryPanel panel : categoryPanels) {
            panel.render(context, theme, mouseX, mouseY);
        }

        settingsPanel.render(context, theme, mouseX, mouseY);

        renderThemeSwitcher(context, theme);

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderThemeSwitcher(DrawContext context, Theme theme) {
        int x = 20;
        int y = height - 24;
        String label = "Тема: " + ThemeManager.getInstance().getCurrentThemeId() + " (клик — сменить)";
        com.crolclient.client.util.RenderUtil.drawTextWithBackground(context, label, x, y, theme.textColorArgb(), theme.backgroundColorArgb());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        if (searchBar.isMouseOver((int) mouseX, (int) mouseY)) {
            searchBar.setFocused(true);
            return true;
        } else {
            searchBar.setFocused(false);
        }

        for (CategoryPanel panel : categoryPanels) {
            Module clicked = panel.getModuleAt((int) mouseX, (int) mouseY);
            if (clicked != null) {
                clicked.toggle();
                settingsPanel.setTargetModule(clicked);
                return true;
            }
        }

        if (isThemeSwitcherClicked((int) mouseX, (int) mouseY)) {
            cycleTheme();
            return true;
        }

        settingsPanel.handleClick((int) mouseX, (int) mouseY);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            settingsPanel.handleDrag((int) mouseX, (int) mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private boolean isThemeSwitcherClicked(int mouseX, int mouseY) {
        int x = 20;
        int y = height - 24;
        return mouseX >= x && mouseX <= x + 220 && mouseY >= y && mouseY <= y + 12;
    }

    private void cycleTheme() {
        Map<String, Theme> themes = ThemeManager.getInstance().getThemes();
        List<String> ids = new ArrayList<>(themes.keySet());
        String current = ThemeManager.getInstance().getCurrentThemeId();
        int idx = ids.indexOf(current);
        String next = ids.get((idx + 1) % ids.size());
        ThemeManager.getInstance().setCurrentTheme(next);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchBar.isFocused()) {
            searchBar.appendChar(chr);
            applySearch();
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBar.isFocused() && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
            searchBar.backspace();
            applySearch();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void applySearch() {
        activeSearchQuery = searchBar.getQuery();
        List<Module> results = ModuleManager.getInstance().search(activeSearchQuery);
        for (CategoryPanel panel : categoryPanels) {
            List<Module> filtered = results.stream()
                    .filter(m -> m.getCategory() == panel.category)
                    .toList();
            panel.setModules(filtered);
        }
    }

    @Override
    public void close() {
        // Автосохранение конфигурации при закрытии меню, как указано в архитектуре.
        ConfigManager.getInstance().save();
        super.close();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
