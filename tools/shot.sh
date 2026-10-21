#!/bin/sh
# local helper: optional key presses, then screenshot to shots/<name>.png
cd "$(dirname "$0")/.."; name=$1; shift
for k in "$@"; do tools/tvsh input keyevent "$k" >/dev/null; sleep 0.8; done; sleep 1.5
tools/tvsh screencap -p /sdcard/s.png && tools/tvsh --pull /sdcard/s.png "shots/$name.png" >/dev/null && echo "shots/$name.png"
