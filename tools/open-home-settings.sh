#!/bin/sh
# local helper: go Home, scroll to the Settings icon and hold OK to open Home settings
cd "$(dirname "$0")/.."
tools/tvsh input keyevent KEYCODE_HOME; sleep 2
tools/tvsh "input keyevent $(printf 'KEYCODE_DPAD_RIGHT %.0s' $(seq 1 30))"; sleep 1
tools/tvsh input keyevent --longpress KEYCODE_DPAD_CENTER; sleep 2
