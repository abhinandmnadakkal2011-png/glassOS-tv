#!/usr/bin/env bash
# Copy every picture in wallpapers/ to the launcher's wallpapers folder on the TV. They then show up in
# Home settings > Wallpaper, named after the file (tahoe_day.jpg -> "Tahoe Day").
# Usage: TV_HOST=192.168.1.50 tools/push-wallpapers.sh [file...]
set -euo pipefail
: "${TV_HOST:?set TV_HOST to your TV IP, e.g. TV_HOST=192.168.1.50}"
cd "$(dirname "$0")/.."
DEST=/sdcard/Android/data/com.gathin.tvhome/files/wallpapers
tools/tvsh mkdir -p "$DEST"
FILES=("$@"); [ ${#FILES[@]} -gt 0 ] || FILES=(wallpapers/*.jpg wallpapers/*.jpeg wallpapers/*.png)
for f in "${FILES[@]}"; do
  [ -f "$f" ] || continue
  tools/venv/bin/python -I tools/b64push.py "$f" "$DEST/$(basename "$f")" | grep -E "MATCH|rror"
done
