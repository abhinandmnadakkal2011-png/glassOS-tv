#!/usr/bin/env bash
# Find a glassOS-tv checkout (the current directory, $GLASSOS_TV_DIR, or ~/glassOS-tv), clone it if
# there is none, and set up the ADB client venv. Prints the checkout's absolute path on the last line.
set -euo pipefail
REPO_URL="${GLASSOS_TV_REPO:-https://github.com/jmarknavarro/glassOS-tv.git}"
is_checkout() { [ -f "$1/launcher/build-and-install.sh" ] && [ -f "$1/tools/tv.py" ]; }

DIR=""
for d in "$PWD" "${GLASSOS_TV_DIR:-}" "$HOME/glassOS-tv"; do
  [ -n "$d" ] && is_checkout "$d" && { DIR="$(cd "$d" && pwd)"; break; }
done
if [ -z "$DIR" ]; then
  DIR="${GLASSOS_TV_DIR:-$HOME/glassOS-tv}"
  [ -e "$DIR" ] && { echo "ERROR: $DIR exists but is not a glassOS-tv checkout; set GLASSOS_TV_DIR" >&2; exit 1; }
  echo "Cloning $REPO_URL into $DIR" >&2
  git clone --depth 1 "$REPO_URL" "$DIR" >&2
fi

command -v python3 >/dev/null || { echo "ERROR: python3 is required" >&2; exit 1; }
if [ ! -x "$DIR/tools/venv/bin/python" ] && [ ! -x "$DIR/tools/venv/Scripts/python.exe" ]; then
  echo "Setting up the ADB client in tools/venv" >&2
  python3 -m venv "$DIR/tools/venv" >&2
  "$DIR/tools/venv/bin/pip" install -q -r "$DIR/tools/requirements.txt" >&2
fi
mkdir -p "$DIR/shots"
echo "$DIR"
