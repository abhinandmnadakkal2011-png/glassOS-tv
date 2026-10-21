#!/usr/bin/env bash
# Report what the launcher build needs (JDK 17, Android SDK 34) and whether the TV answers over ADB.
# Usage: TV_HOST=192.168.1.50 preflight.sh <checkout>
DIR="${1:?usage: preflight.sh <glassOS-tv checkout>}"
ok() { printf '  ok    %s\n' "$1"; }; bad() { printf '  MISS  %s\n' "$1"; MISSING=1; }
MISSING=0
echo "Build tools:"
J=$(/usr/libexec/java_home -v 17 2>/dev/null || { [ -d /opt/homebrew/opt/openjdk@17 ] && echo /opt/homebrew/opt/openjdk@17; } || true)
if [ -n "${JAVA_HOME:-}" ] || [ -n "$J" ]; then ok "JDK 17 (${JAVA_HOME:-$J})"; else bad "JDK 17  -> brew install openjdk@17"; fi
SDK="${ANDROID_HOME:-}"
[ -z "$SDK" ] && for d in ~/Library/Android/sdk ~/Android/Sdk /opt/homebrew/share/android-commandlinetools; do [ -d "$d" ] && SDK=$d && break; done
if [ -n "$SDK" ] && [ -d "$SDK/platforms/android-34" ]; then ok "Android SDK platform 34 ($SDK)"
else bad "Android SDK platform 34 -> brew install --cask android-commandlinetools && sdkmanager \"platforms;android-34\" \"build-tools;34.0.0\""; fi
if [ -n "$SDK" ] && [ -d "$SDK/build-tools/34.0.0" ]; then ok "build-tools 34.0.0"; else bad "build-tools 34.0.0"; fi
echo "TV:"
if [ -z "${TV_HOST:-}" ]; then bad "TV_HOST not set (TV IP: Settings -> Network, or arp -a)"
elif out=$(cd "$DIR" && tools/tvsh --probe 2>&1); then ok "TV at $TV_HOST: $(echo "$out" | tr '\n' ' ')"
else bad "TV at $TV_HOST did not answer: $(echo "$out" | tail -n 1). Is USB debugging on, and was the prompt on the TV accepted?"; fi
exit $MISSING
