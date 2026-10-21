---
name: debloat
description: Safely debloat an Android TV or Google TV over ADB - back up, disable telemetry, ad rows and unused preloaded apps in small batches with pm disable-user, and restore with one script. Use when the user wants to remove ads, bloatware or telemetry from their TV, or undo a debloat.
argument-hint: "[TV IP address]"
allowed-tools: Bash, Read, Edit, Write, AskUserQuestion
---

# Debloat the TV

Disable, never uninstall. Every change must be undoable with `tools/restore.sh`.

## Setup

1. `bash "${CLAUDE_PLUGIN_ROOT}/scripts/ensure-repo.sh"` → checkout path (last line). Work from there.
2. `TV_HOST` from `$ARGUMENTS`, the conversation, or ask. `TV_HOST=<ip> tools/tvsh --probe` to connect;
   warn the user first that the "Allow USB debugging?" prompt may appear on the TV.
3. Ask what is plugged into the TV's USB ports: some boxes cut USB power while debugging is on.

## Back up

```bash
mkdir -p backup
tools/tvsh 'pm list packages' > backup/packages.txt
tools/tvsh 'pm list packages -d' > backup/disabled.txt
tools/tvsh getprop > backup/getprop.txt
for ns in global secure system; do tools/tvsh "settings list $ns" > backup/settings-$ns.txt; done
```

`backup/` is git-ignored; it holds the TV's serial and IDs, so never commit or share it.

## Pick the list

Look for the model in `examples/` (`grep -ril "<model>" examples/*/debloat.txt`, and see
`examples/README.md`). If one matches, show the user its groups and comments. If none does, build a list
from `pm list packages`, sorted into: manufacturer telemetry/analytics; manufacturer content and ad
services (recommendation rows, content providers); background services they probably don't need;
preloaded apps; streaming apps. Check unclear packages with `tools/tvsh dumpsys package <pkg>` first.

Never disable: the TV / Live TV app and the input services for HDMI and the tuner, the input/source menu,
remote and Bluetooth pairing, the on-screen keyboard, Google Play Store and Play Services.

Before touching streaming apps, the voice assistant, Chromecast, Miracast or the update service, ask the
user with a multi-select checklist which ones they use, and keep those.

Save the final list as `backup/my-debloat.txt` in the `examples/` format (one package
per line, `#` comments).

## Disable in small batches

Use `tools/batch.sh <tag> <pkg> <pkg> ...` with 3-6 packages per batch. It disables one per shell call
(chained `pm` commands can silently stop after the first), presses Home and saves `shots/<tag>.png`.
Read each screenshot before the next batch. If something broke, re-enable that batch with
`tools/tvsh pm enable <pkg>` one at a time to find the culprit.

## Optional: ad-blocking DNS

Offer it, Android 9+ only, and say it does not block YouTube ads:
`tools/tvsh settings put global private_dns_specifier dns.adguard-dns.com` then
`tools/tvsh settings put global private_dns_mode hostname`. Check an app still plays.
Undo: `tools/tvsh settings put global private_dns_mode opportunistic`.

## Finish

- Undo everything: `TV_HOST=<ip> tools/restore.sh backup/my-debloat.txt [stock launcher package]`.
- Offer to contribute the list as `examples/<brand>-<model>/debloat.txt` (see `CONTRIBUTING.md`), after
  they have lived with it a few days.
- Remind them to switch USB debugging off.
