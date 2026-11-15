#pragma once
#include <vector>
namespace bimbam {
enum class MenuScreen { Main, Settings, Avatar };
enum class MenuAction { None, StartGame, OpenSettings, OpenAvatar, Back, Quit };
struct MenuRect {
    float x, y, width, height;
    bool contains(float px, float py) const;
};
struct MenuButton {
    const char* id;
    const char* label;
    MenuRect bounds;
    MenuAction action;
};
// Panel-local normalized coordinates: x/y in [-0.5, 0.5].
class QuestMenu {
public:
    QuestMenu();
    void open();
    void close();
    bool isOpen() const;
    MenuScreen screen() const;
    const std::vector<MenuButton>& buttons() const;
    int hoveredButton() const;
    // Call once per frame with the controller ray intersection and trigger state.
    MenuAction updatePointer(float panelX, float panelY, bool triggerDown);
    void showMain();
    void showSettings();
    void showAvatar();
private:
    void rebuildButtons();
    MenuScreen screen_;
    bool open_;
    bool previousTriggerDown_;
    int hoveredButton_;
    std::vector<MenuButton> buttons_;
};
} // namespace bimbam
