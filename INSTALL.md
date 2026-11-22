# Installing CrystaList

Download the file for your system from the
[latest release](https://github.com/hayoloo642-cloud/CrystaList/releases/latest).

## macOS (15+)

1. Download `CrystaList-macos-arm64-<version>.dmg` (Apple silicon) or
   `CrystaList-macos-x64-<version>.dmg` (Intel).
2. Open the DMG and drag **CrystaList** into Applications.
3. The build is unsigned, so the first launch needs
   **System Settings → Privacy & Security → Open Anyway**, or:
   ```bash
   xattr -dr com.apple.quarantine /Applications/CrystaList.app
   ```

Apple silicon builds need macOS 15 or newer.

## Windows (10+)

1. Download `CrystaList-windows-x64-<version>.zip`.
2. Extract it somewhere permanent, e.g. `C:\Users\<you>\Apps\CrystaList`.
3. Run `CrystaList.exe` from the extracted folder.
4. SmartScreen may warn about an unsigned app: **More info → Run anyway**.

Keep the whole extracted folder together; the launcher needs the bundled
runtime next to it.

## Linux (glibc, x86_64)

1. Download `CrystaList-linux-x64-<version>.AppImage`.
2. Make it executable and run it:
   ```bash
   chmod +x CrystaList-linux-x64-*.AppImage
   ./CrystaList-linux-x64-*.AppImage
   ```
3. If FUSE is missing, run with `--appimage-extract-and-run`.
   On Debian/Ubuntu: `sudo apt install libfuse2`.

## Signing

None of the builds are code-signed, so macOS and Windows will show a warning
the first time. Signing needs an Apple Developer ID and a Windows code-signing
certificate; see `REBRAND.md` for the remaining upstream services
(Discord app id, Listen Together server, update channel).
