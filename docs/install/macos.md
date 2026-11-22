# Install on macOS

## Download

Choose the ARM64 DMG for Apple Silicon or x64 for Intel from [the download channels](README.md#get-the-package). Packaging runs on macOS 15; compatibility with older systems needs platform verification. The package includes Java.

## Install and verify

1. [Verify the checksum](README.md#verify-a-download).
2. Open the DMG and drag CrystaList to Applications.
3. Eject the image and open CrystaList.
4. If macOS blocks the unsigned app, use its per-app Open Anyway option only after verifying the download. Follow [Apple's instructions](https://support.apple.com/en-us/102445).
5. Check playback, login and credential restoration after restart.

Packages are unsigned and unnotarized. Do not disable Gatekeeper globally.

## Homebrew

The [CrystaList tap](https://github.com/MetrolistGroup/homebrew-tap) installs the matching DMG for your Mac and follows stable releases:

```sh
brew trust crystalistgroup/tap
brew install --cask crystalistgroup/tap/crystalist
```

Homebrew only installs from trusted taps, so `brew trust` is needed once.

Update with `brew upgrade --cask crystalist`. The first launch still needs Open Anyway as in step 4. `brew uninstall --zap --cask crystalist` also deletes settings, the library database and downloads, so export a backup first.

## Nix

The repository flake packages the release DMG for Apple Silicon and Intel Macs:

```sh
nix run github:CrystaListGroup/CrystaList
nix profile install github:CrystaListGroup/CrystaList
```

The profile gets `Applications/CrystaList.app` and a `crystalist` command. To use it from a nix-darwin or Home Manager configuration, add `github:CrystaListGroup/CrystaList` as an input and include `inputs.crystalist.packages.${pkgs.stdenv.hostPlatform.system}.default` in your packages. The flake follows the latest stable release; run `nix flake update crystalist` or `nix profile upgrade` to update.

## Update and uninstall

Back up and quit before replacing the app in Applications. Deleting the app leaves profile data, Keychain credentials and music outside it. Sign out before removing credentials and preserve backups before clearing data.

## Common problems

For architecture or launch failure, confirm the DMG matches the processor and collect the [desktop log](../desktop.md#logs-and-profiling). For credential problems, check [Keychain recovery](../desktop.md#credentials-and-settings-files) before deleting files.
