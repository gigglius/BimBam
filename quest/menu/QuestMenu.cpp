#include "QuestMenu.h"
namespace bimbam {
bool MenuRect::contains(float px, float py) const {
    return px >= x && px <= x + width && py >= y && py <= y + height;
}
QuestMenu::QuestMenu()
    : screen_(MenuScreen::Main), open_(true),
      previousTriggerDown_(false), hoveredButton_(-1) { rebuildButtons(); }
void QuestMenu::open() { open_ = true; previousTriggerDown_ = false; rebuildButtons(); }
void QuestMenu::close() { open_ = false; hoveredButton_ = -1; previousTriggerDown_ = false; }
bool QuestMenu::isOpen() const { return open_; }
MenuScreen QuestMenu::screen() const { return screen_; }
const std::vector<MenuButton>& QuestMenu::buttons() const { return buttons_; }
int QuestMenu::hoveredButton() const { return hoveredButton_; }
void QuestMenu::showMain() { screen_ = MenuScreen::Main; rebuildButtons(); }
void QuestMenu::showSettings() { screen_ = MenuScreen::Settings; rebuildButtons(); }
void QuestMenu::showAvatar() { screen_ = MenuScreen::Avatar; rebuildButtons(); }
void QuestMenu::rebuildButtons() {
    buttons_.clear(); hoveredButton_ = -1;
    const float left = -0.19f, width = 0.38f, height = 0.075f, gap = 0.018f;
    float y = 0.10f;
    if (screen_ == MenuScreen::Main) {
        buttons_.push_back({"play", "PLAY", {left,y,width,height}, MenuAction::StartGame});
        y -= height + gap;
        buttons_.push_back({"avatar", "AVATAR", {left,y,width,height}, MenuAction::OpenAvatar});
        y -= height + gap;
        buttons_.push_back({"settings", "SETTINGS", {left,y,width,height}, MenuAction::OpenSettings});
        y -= height + gap;
        buttons_.push_back({"quit", "QUIT", {left,y,width,height}, MenuAction::Quit});
    } else {
        if (screen_ == MenuScreen::Settings) {
            buttons_.push_back({"comfort", "COMFORT OPTIONS", {left,y,width,height}, MenuAction::None});
            y -= height + gap;
            buttons_.push_back({"turn", "TURNING OPTIONS", {left,y,width,height}, MenuAction::None});
            y -= height + gap;
        } else {
            buttons_.push_back({"avatar-default", "DEFAULT AVATAR", {left,y,width,height}, MenuAction::None});
            y -= height + gap;
        }
        buttons_.push_back({"back", "BACK", {left,y,width,height}, MenuAction::Back});
    }
}
MenuAction QuestMenu::updatePointer(float panelX, float panelY, bool triggerDown) {
    if (!open_) { previousTriggerDown_ = triggerDown; hoveredButton_ = -1; return MenuAction::None; }
    hoveredButton_ = -1;
    for (std::size_t i = 0; i < buttons_.size(); ++i)
        if (buttons_[i].bounds.contains(panelX, panelY)) { hoveredButton_ = static_cast<int>(i); break; }
    const bool justPressed = triggerDown && !previousTriggerDown_;
    previousTriggerDown_ = triggerDown;
    if (!justPressed || hoveredButton_ < 0) return MenuAction::None;
    const MenuAction action = buttons_[static_cast<std::size_t>(hoveredButton_)].action;
    switch (action) {
        case MenuAction::OpenSettings: showSettings(); break;
        case MenuAction::OpenAvatar: showAvatar(); break;
        case MenuAction::Back: showMain(); break;
        case MenuAction::StartGame: close(); break;
        case MenuAction::Quit: close(); break;
        case MenuAction::None: break;
    }
    return action;
}
} // namespace bimbam
