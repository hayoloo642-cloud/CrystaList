# Installing CrystaList

Download the file for your system from the
[latest release](https://github.com/hayoloo642-cloud/CrystaList/releases/latest).

| System | File | What it is |
| --- | --- | --- |
| Windows 10/11 | `CrystaList-windows-x64-<version>.exe` | Installer wizard |
| Linux (x86_64) | `CrystaList-linux-x64-<version>.rpm` | RPM package |
| Linux (any distro) | `CrystaList-linux-x64-<version>.AppImage` | Portable AppImage |
| macOS 15+ | `CrystaList-macos-arm64-<version>.dmg` | Apple silicon |
| macOS 15+ | `CrystaList-macos-x64-<version>.dmg` | Intel |

## Windows (.exe)

1. Run `CrystaList-windows-x64-<version>.exe`.
2. Follow the wizard; it installs per-user by default.
3. Because the build is unsigned, SmartScreen may warn:
   **More info → Run anyway**.

## Linux (.rpm)

Fedora/RHEL/openSUSE:

```bash
sudo rpm -i CrystaList-linux-x64-<version>.rpm
# or, to let the package manager resolve anything missing:
sudo dnf install ./CrystaList-linux-x64-<version>.rpm
```

Debian/Ubuntu users: use the AppImage instead, or convert with `alien`.

## Linux (AppImage)

```bash
chmod +x CrystaList-linux-x64-<version>.AppImage
./CrystaList-linux-x64-<version>.AppImage
```

If FUSE is missing, run `--appimage-extract-and-run`
(Debian/Ubuntu: `sudo apt install libfuse2t64`).

## macOS (.dmg)

1. Open `CrystaList-macos-<arch>-<version>.dmg` and drag **CrystaList** into
   Applications.
2. Unsigned build: use **System Settings → Privacy & Security → Open Anyway**,
   or clear the quarantine flag once:
   ```bash
   xattr -dr com.apple.quarantine /Applications/CrystaList.app
   ```

## Signing

None of these installers are code-signed yet, so Windows and macOS show a
one-time warning. Proper signing needs an Apple Developer ID and a Windows
code-signing certificate. See `REBRAND.md` for the upstream services that
still need your own credentials (Discord app id, Listen Together server,
update channel).
