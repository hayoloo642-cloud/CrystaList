#!/usr/bin/env python3
"""
Generate installer icon files (logo.png, logo.ico, logo.icns) from the
CrystaList master icon.

Usage:
  python3 scripts/gen_icons.py

Input:
  assets/app-icons/crystalist/icon-1024.png - single 1024x1024 master

Outputs (written to desktopApp/src/desktopMain/resources/):
  logo.png  - 512x512          (Linux .deb / .rpm / AppImage, Windows PNG icon)
  logo.ico  - multi-size ICO   (Windows)
  logo.icns - macOS icon bundle via iconutil (macOS only)
"""

import platform
import subprocess
import sys
import tempfile
from pathlib import Path

REPO_ROOT = Path(__file__).parent.parent
RESOURCES = REPO_ROOT / "desktopApp" / "src" / "desktopMain" / "resources"
MASTER = REPO_ROOT / "assets" / "app-icons" / "crystalist" / "icon-1024.png"

ICO_SIZES = [16, 32, 48, 64, 128, 256]


def load_master(size: int):
    from PIL import Image

    if not MASTER.is_file():
        raise SystemExit(f"Missing master icon: {MASTER}")
    img = Image.open(MASTER).convert("RGBA")
    if img.size != (size, size):
        img = img.resize((size, size), Image.LANCZOS)
    return img


def write_linux_png():
    out = RESOURCES / "logo.png"
    load_master(512).save(str(out), "PNG")
    print(f"  logo.png  -> {out}")


def write_windows_ico():
    out = RESOURCES / "logo.ico"
    base = load_master(256)
    base.save(str(out), format="ICO", sizes=[(s, s) for s in ICO_SIZES])
    print(f"  logo.ico  -> {out}")


def write_macos_icns():
    """Requires macOS + iconutil (ships with Xcode Command Line Tools)."""
    with tempfile.TemporaryDirectory() as td:
        iconset = Path(td) / "icon.iconset"
        iconset.mkdir()

        for base_size in [16, 32, 128, 256, 512]:
            load_master(base_size).save(iconset / f"icon_{base_size}x{base_size}.png")
            load_master(base_size * 2).save(iconset / f"icon_{base_size}x{base_size}@2x.png")

        out = RESOURCES / "logo.icns"
        result = subprocess.run(
            ["iconutil", "-c", "icns", str(iconset), "-o", str(out)],
            capture_output=True,
        )
        if result.returncode != 0:
            print(
                f"  iconutil failed: {result.stderr.decode().strip()}",
                file=sys.stderr,
            )
            sys.exit(1)
        print(f"  logo.icns -> {out}")


def main():
    try:
        from PIL import Image  # noqa: F401
    except ImportError as error:
        raise SystemExit("Pillow is required; install the pinned icon-generation dependency first") from error

    os_name = platform.system()
    print(f"Generating installer icons (platform={os_name})...")

    write_linux_png()

    if os_name == "Windows":
        write_windows_ico()
    elif os_name == "Darwin":
        write_macos_icns()

    print("Done.")


if __name__ == "__main__":
    main()
