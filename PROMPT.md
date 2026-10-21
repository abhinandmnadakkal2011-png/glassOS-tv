# The prompt

**Using Claude Code?** Install the plugin instead: it runs this repo's tested scripts for you.

```
/plugin marketplace add jmarknavarro/glassOS-tv
/plugin install glassos-tv@glassos-tv
```

Then `/glassos-tv:install <TV IP>`. See the [README](README.md#install-with-claude-code).

Otherwise, paste the prompt below into any coding assistant with shell access (Cursor, Codex, Claude
Code without the plugin, …) on a computer on the same Wi-Fi as the TV. It works on Android TV / Google
TV sets (Xiaomi, TCL, Hisense, Sony, OnePlus, Realme, Nvidia Shield and most others), not on Samsung
Tizen, LG webOS or Fire TV.

The assistant installs glassOS-tv from this repo: it uses your checkout if you run it inside one, or
clones the repo, then builds and installs `launcher/` with the repo's scripts. It does not write a
launcher of its own.

Before you start, on the TV: Settings → System (or Device Preferences) → About → press OK on "Build"
7 times, then go back → Developer options → turn on USB debugging (and Network/Wireless debugging if
your TV shows it). Keep the TV on. Some boxes cut power to their USB port while USB debugging is on,
so a USB speaker or drive plugged into it goes dead until you switch debugging off again.

---

I want to debloat my Android TV and replace its home screen with glassOS-tv, a Liquid Glass style
launcher. My TV model is: <MODEL NAME HERE>. Work on the real device, step by step, and verify
everything you do with screenshots pulled from the TV. Ask me before anything that depends on my
preferences.

If the current directory is a glassOS-tv checkout (it has `launcher/build-and-install.sh` and
`tools/tv.py`), use it: follow its README, `docs/TROUBLESHOOTING.md` and the matching `examples/`
folder, and use its scripts rather than writing your own. Otherwise, clone
https://github.com/jmarknavarro/glassOS-tv and use that. Install glassOS-tv from `launcher/` only:
don't write a launcher of your own, and don't install a different third-party launcher. If the build or
install fails, fix the cause or tell me; don't replace it with something else.

Connect
- Find the TV on my network (hostname, ARP table, or ask me for the IP) and connect over ADB on port
  5555: `python3 -m venv tools/venv && tools/venv/bin/pip install -r tools/requirements.txt`,
  then `TV_HOST=<ip> tools/tvsh --probe`.
- If the stock adb binary fails with "No route to host" on macOS even though the port is open, that is
  macOS Local Network privacy blocking adb. Don't debug it: use the Python `adb-shell` package in a venv
  (that is what `tools/tv.py` does). If its file push hangs on larger files, transfer files as base64
  chunks through the shell and verify the md5 (`tools/b64push.py`).
- Tell me when the "Allow USB debugging?" prompt will appear on the TV so I can accept it.
- If `adb install` hangs on Android 14 it is waiting for an on-screen confirmation; push the APK and run
  `pm install -r` over the shell instead.

Back up first
- Save the full package list, disabled-package list, getprop output and all settings to `backup/`
  before changing anything. Save the list of what you disable as `backup/my-debloat.txt`;
  `tools/restore.sh backup/my-debloat.txt` re-enables all of it.
- Ask me what is plugged into the TV's USB ports. Some devices cut their power while USB debugging
  is on, so warn me, and remind me to switch debugging off once you're done.

Debloat
- If `examples/` has my model, start from its `debloat.txt` and show me its groups. Otherwise list
  installed packages and sort them into: manufacturer telemetry/analytics, manufacturer content and ad
  services (PatchWall, recommendation rows, content providers), background services I probably don't
  need (share/cast duplicates, cleaners), preloaded apps, and streaming apps.
- Disable the first categories with `pm disable-user --user 0`, never uninstall system packages.
- Never disable what the TV needs to work: the TV app and input services that play HDMI and the
  tuner, the input/source menu, remote control and Bluetooth pairing services, the on-screen
  keyboard, Google Play Store and Play Services. Manufacturer package names often don't say what a
  package does: check its activities and services with `dumpsys package` first, and ask me if it's
  still unclear.
- Before touching streaming apps, voice assistant, Chromecast, Miracast or the update service, ask me
  which ones I use with a checklist question and disable only what I don't tick.
- Run `pm` commands one per shell call; chained commands may silently stop after the first.
- Disable in small batches and screenshot between them (`tools/batch.sh <tag> <pkg>...`): a batch can
  take something load-bearing with it and you cannot tell which.
- Offer me ad-blocking DNS as an optional extra: on Android 9 and newer, Private DNS set to an
  ad-blocking resolver (`settings put global private_dns_specifier dns.adguard-dns.com`, then
  `settings put global private_dns_mode hostname`). Tell me it doesn't block YouTube ads, check an app
  still plays afterwards, and tell me the undo (`private_dns_mode opportunistic`).

Launcher
- Check that the TV accepts APKs before building anything. Some China-market sets answer every
  sideload with `INSTALL_FAILED_VERIFICATION_FAILURE` — `adb install`, `pm install`, a hand-started
  installer and even the manufacturer's own APKs all fail — and others refuse any package that
  declares `android.intent.category.HOME` unless it carries the manufacturer's signature. If you
  hit either, do the debloat half, say plainly that the launcher half is impossible here, and stop
  retrying installs. The matching `examples/` header says which case your TV is.
- Install JDK 17 and Android SDK platform 34 / build-tools 34.0.0 if missing (ask me
  first), then run `TV_HOST=<ip> launcher/build-and-install.sh`. It builds, installs, sets glassOS-tv as
  Home and disables the stock launcher it recognises. If it stops because Home resolves to something
  unexpected, show me the message and ask what to pass as `STOCK_LAUNCHER`.
- Before disabling the stock launcher, check which other apps claim HOME (`cmd package query-activities
  -a android.intent.action.MAIN -c android.intent.category.HOME`): on Google TV the setup wizard
  (`com.google.android.tungsten.setupwraith`) takes over even after the stock launcher is disabled, and
  it can own the saved Wi-Fi network. Read `docs/TROUBLESHOOTING.md` and ask me before touching it.
- Offer the photo wallpapers afterwards (`tools/push-wallpapers.sh`) and the themes in `themes/`
  (`tools/theme.sh themes/<name>`).

Verify
- After each change, inject remote key presses over ADB, take a screenshot from the TV and look at it
  (`tools/shot.sh <name> KEYCODE_...`). Fix what you see before moving on. A black screenshot means
  protected video is on screen: press Home first.
- If a remote button relies on an accessibility helper, check that the service is enabled and bound
  for the current user, then ask me to press the real button; an injected key is not proof (see
  `docs/TROUBLESHOOTING.md`).
- When done, give me the restore command or script, the build-and-install command, and tell me how to
  open Home settings, change the dock order and the wallpaper, and how to re-enable any app.

---

Expect to answer three or four checklist questions (which apps you use, whether to keep the voice
assistant, Chromecast and firmware updates, which wallpaper). Everything is reversible.
