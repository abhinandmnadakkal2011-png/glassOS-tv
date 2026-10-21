---
name: install
description: Install the glassOS-tv Liquid Glass launcher on an Android TV or Google TV over Wi-Fi ADB and make it the home screen. Use when the user wants glassOS-tv, a new or ad-free home screen or launcher on their Android TV / Google TV, or to replace the stock Google TV home.
argument-hint: "[TV IP address]"
allowed-tools: Bash, Read, Edit, Write, AskUserQuestion
---

# Install glassOS-tv on the TV

Work on the real TV, one step at a time, and check each step with a screenshot pulled from the TV.
Everything here is reversible; never uninstall a system package.

## 1. Get the checkout

Run `bash "${CLAUDE_PLUGIN_ROOT}/scripts/ensure-repo.sh"`. It reuses the current directory,
`$GLASSOS_TV_DIR` or `~/glassOS-tv` if one is a glassOS-tv checkout, otherwise clones it to
`~/glassOS-tv`, and sets up the Python ADB client in `tools/venv`. Its last output line is the checkout
path: run every later command from there (`cd <path> && ...`).

## 2. Prepare the TV

If the user has not said so already, tell them to do this on the TV and wait for them to confirm:

1. Settings → System (or Device Preferences) → About → press OK on **Build** 7 times.
2. Back → Developer options → turn on **USB debugging** (and Network/Wireless debugging if shown).
3. Keep the TV on. Some boxes cut power to their USB port while debugging is on.

Get the TV's IP: use `$ARGUMENTS` if given, otherwise ask, or look in `arp -a`. Export it as `TV_HOST`
for every command below (`TV_HOST=<ip> ...`).

## 3. Preflight

Run `TV_HOST=<ip> bash "${CLAUDE_PLUGIN_ROOT}/scripts/preflight.sh" <checkout>`. Before the first
connection, tell the user an **"Allow USB debugging?"** prompt will appear on the TV and they should
tick "Always allow" and accept it. For anything marked MISS:

- JDK 17 / Android SDK on macOS: `brew install openjdk@17 && brew install --cask android-commandlinetools`,
  then `sdkmanager "platforms;android-34" "build-tools;34.0.0"`. Ask before installing.
- TV not answering: read `docs/TROUBLESHOOTING.md` → Connecting. Do not debug the stock `adb` binary on
  macOS ("No route to host" is Local Network privacy); `tools/tvsh` avoids it.

Note the model and Android version from the probe, and look for a matching folder in `examples/`
(`grep -ril "<model>" examples/*/debloat.txt`). Its header says whether the TV rejects sideloaded
launchers and what to do about the Google TV setup wizard.

## 4. Build and install

1. Back up first: `mkdir -p backup && tools/tvsh 'pm list packages' > backup/packages.txt` and
   `tools/tvsh 'pm list packages -d' > backup/disabled.txt`.
2. Run `TV_HOST=<ip> launcher/build-and-install.sh` (run it in the background; the first Gradle build
   takes several minutes). It builds the APK, installs it, sets it as Home and disables the stock
   launcher it recognises. If it stops because Home resolves to something unexpected, show the user the
   message and ask which `STOCK_LAUNCHER=<package>` or `STOCK_LAUNCHER=none` to use.
3. If the install fails with `INSTALL_FAILED_VERIFICATION_FAILURE` or the TV refuses any HOME app, say
   plainly that this TV will not accept a replacement launcher, stop retrying, and offer the debloat
   skill instead.
4. On Google TV, `com.google.android.tungsten.setupwraith` can keep owning Home. Read
   `docs/TROUBLESHOOTING.md` about it **before** disabling it, because it can own the saved Wi-Fi network,
   and ask the user before touching it.

## 5. Verify

`tools/shot.sh home KEYCODE_HOME`, then Read `shots/home.png` and look at it. Check Left/Right on the
dock, and that Home returns to glassOS-tv (`tools/shot.sh right KEYCODE_DPAD_RIGHT KEYCODE_DPAD_RIGHT`).
Fix what you see before moving on. If screenshots come out black, the TV is showing protected video:
press Home first.

## 6. Finish

- Offer the photo wallpapers (the `wallpapers` skill) and debloating (the `debloat` skill).
- Tell the user how to use it: hold OK on the Settings icon (last in the dock) for Home settings, hold
  Up on the dock for Inputs and Wi-Fi.
- Remind them to switch USB debugging off when done: port 5555 is plaintext.
- How to undo: `TV_HOST=<ip> tools/restore.sh <debloat list> [stock launcher package]` re-enables the
  stock launcher.
