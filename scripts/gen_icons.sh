#!/usr/bin/env bash
# Generate desktop installer icons from the single CrystaList master icon.
set -euo pipefail

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root_dir"

if ! python3 -c 'import PIL' >/dev/null 2>&1; then
  python3 -m pip install --disable-pip-version-check --no-cache-dir \
    --only-binary=:all: --user Pillow==11.3.0 2>/dev/null \
    || python3 -m pip install --disable-pip-version-check --no-cache-dir \
    --only-binary=:all: --break-system-packages Pillow==11.3.0
fi

python3 scripts/gen_icons.py
