---
name: wallpapers
description: Push photo wallpapers or a theme to the glassOS-tv launcher on the TV, or reset the theme. Use when the user wants the glassOS-tv photo wallpapers, their own picture as the TV wallpaper, or to apply a glassOS-tv theme.
argument-hint: "[image file | theme folder | --reset]"
allowed-tools: Bash, Read
---

# Wallpapers and themes

1. `bash "${CLAUDE_PLUGIN_ROOT}/scripts/ensure-repo.sh"` → checkout path (last line). Work from there.
2. `TV_HOST` from the conversation or ask. The launcher must already be installed
   (`tools/tvsh pm path com.gathin.tvhome` prints a path); otherwise offer the `install` skill.

Depending on `$ARGUMENTS`:

- Nothing: push all the built-in photos with `TV_HOST=<ip> tools/push-wallpapers.sh`.
- An image file: it should be a 1920×1080 JPEG. Copy it into `wallpapers/` with a descriptive snake_case
  name (the file name becomes its label: `tahoe_day.jpg` → "Tahoe Day"), then
  `TV_HOST=<ip> tools/push-wallpapers.sh wallpapers/<name>.jpg`. Resize larger pictures first
  (`sips -z 1080 1920` on macOS) to keep memory low on 1 GB TVs.
- A theme folder (`themes/aurora`, `themes/six-columns`, `themes/default`, or the user's own with a
  `config.json`, see `themes/README.md`): `TV_HOST=<ip> tools/theme.sh <folder>`.
- `--reset`: `TV_HOST=<ip> tools/theme.sh --reset`.

Each push prints `MATCH` when the checksum on the TV matches. Then tell the user to pick it under Home
settings (hold OK on the Settings icon) → Wallpaper, or open the panel with
`tools/open-home-settings.sh`, take `tools/shot.sh panel` and Read `shots/panel.png` to confirm.
