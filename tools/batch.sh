#!/bin/sh
# local helper: disable the given packages one per shell call, then screenshot
cd "$(dirname "$0")/.."; tag=$1; shift
for p in "$@"; do printf '%s: ' "$p"; tools/tvsh pm disable-user --user 0 "$p"; done
sleep 4; tools/tvsh input keyevent KEYCODE_HOME >/dev/null; sleep 3
tools/tvsh screencap -p /sdcard/s.png && tools/tvsh --pull /sdcard/s.png "shots/$tag.png" >/dev/null && echo "shot shots/$tag.png"
