# BimBam 2014 reference screenshot audit

Source: `IMG_2897.zip` supplied in the repository and re-uploaded in this conversation. The archive contains 50 PNG screenshots (IMG_2897.PNG–IMG_2945.PNG, with gaps) plus `ImportFilesFromOtherAPP.pdf` and `UnzipReadme.zip`.

## Primary dashboard reference

- `IMG_2897.PNG`: Home dashboard. Blue city/building background; translucent left account panel with avatar, username, Robux/Tix balances, search field, and Logout; four large two-column tiles: Games, Catalog, Inventory, Builders Club. The upper-left Roblox/menu controls and lower-right information control are visible.
- `IMG_2898.PNG`: second dashboard tile set: Profile, Messages, Groups, Leaderboards.
- The screenshots show example account `1Ezra`, Robux 22, Tix 100. BimBam must retain the requested test account display name `1234`, Robux 0, Tix 0 instead of copying those example account values.

## Page references

- `IMG_2899.PNG`–`IMG_2902.PNG`: Games discovery, game tiles, and game details.
- `IMG_2903.PNG`–`IMG_2910.PNG`: Catalog featured items, category navigation, and item browsing.
- `IMG_2911.PNG`–`IMG_2915.PNG`: Character/Avatar customization and currently-wearing/outfit views.
- `IMG_2916.PNG`–`IMG_2921.PNG`: Profile, statistics, groups, badges, and related profile sections.
- `IMG_2922.PNG`–`IMG_2924.PNG`: Inventory and inventory categories/items.
- `IMG_2925.PNG`–`IMG_2930.PNG`: Community/group-related pages, group discovery, group details, and item ownership.
- `IMG_2931.PNG`–`IMG_2938.PNG`: Friends/player lists and additional avatar/inventory views.
- `IMG_2939.PNG`–`IMG_2941.PNG`: In-game view and mobile gameplay controls.
- `IMG_2942.PNG`–`IMG_2945.PNG`: In-game pause, reset-character confirmation, settings, and report-abuse menus.

## Implementation requirements from the references

1. Use the supplied screenshots as the visual source of truth for page composition, tile imagery, navigation, typography, colors, and control placement.
2. Do not substitute generic placeholder cards for pages shown in the reference set.
3. Keep the BimBam test account values (`1234`, Robux `0`, Tix `0`) rather than copying the source account's balances.
4. Keep dashboard navigation separate from the native in-game pause/settings/report menus.
5. Treat the Quest 3 environment surrounding the floating dashboard in the separately supplied VR reference as the headset environment, not as Roblox dashboard artwork.

## Current implementation gap

The current `MainActivity` is a simplified Android view prototype. Its non-Home pages are placeholder text sections, and its Home composition does not yet reproduce the full-screen blue dashboard and account/avatar panel in `IMG_2897.PNG`/`IMG_2898.PNG`. The screenshots are references, not evidence that those pages or server-backed functions are already implemented.
