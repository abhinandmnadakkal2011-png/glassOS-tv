---
name: check
description: Look at the Android TV's screen - press remote keys over ADB and pull a screenshot to see what the TV shows. Use when the user asks what is on the TV, whether glassOS-tv looks right, to test D-pad navigation, or to debug the launcher on the TV.
argument-hint: "[KEYCODE_... keys to press first]"
allowed-tools: Bash, Read
---

# Check the TV screen

1. `bash "${CLAUDE_PLUGIN_ROOT}/scripts/ensure-repo.sh"` → checkout path (last line). Work from there.
2. `TV_HOST` from the conversation or ask.
3. `TV_HOST=<ip> tools/shot.sh <name> $ARGUMENTS` presses each key (for example `KEYCODE_HOME`,
   `KEYCODE_DPAD_RIGHT`, `KEYCODE_DPAD_UP`, `KEYCODE_DPAD_CENTER`, `KEYCODE_BACK`) and saves
   `shots/<name>.png`. Read the PNG and describe what is on screen.

Useful extras:

- Run any shell command on the TV: `tools/tvsh '<command>'`.
- Who owns Home: `tools/tvsh 'cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME'`.
- Open Home settings: `tools/open-home-settings.sh`.
- A long press: `tools/tvsh input keyevent --longpress KEYCODE_DPAD_CENTER`.

A black screenshot means protected video (Live TV, HDMI, Netflix) is on screen: press Home first. An
injected key does not prove a real remote button works; if one relies on an accessibility helper, ask
the user to press it on the real remote (see `docs/TROUBLESHOOTING.md`).
