# CrystaList — rebrand notes

CrystaList is a fork of [Metrolist](https://github.com/MetrolistGroup/Metrolist) (GPL-3.0),
rebranded with an Apple Music inspired design language.

## What changed

| Area | From | To |
| --- | --- | --- |
| App name | Metrolist | CrystaList |
| Kotlin packages | `com.metrolist.*` | `com.crystalist.*` |
| Android applicationId | `com.metrolist.music` | `com.crystalist.music` |
| iOS bundle id | `com.metrolist.MetrolistApp` | `com.crystalist.CrystaListApp` |
| URL scheme | `metrolist://`, `metrolist-auth://` | `crystalist://`, `crystalist-auth://` |
| Web domain | `metrolist.cc` | `crystalist.app` |
| Theme | Material 3 dynamic (green seed) | fixed Apple Music palette |
| Seed color | `#6750A4` | `#FA243C` |
| Typography | Material defaults | SF-style scale, tight tracking |
| Shapes | 16–32dp expressive | 8–26dp calm |
| Motion | `MotionScheme.expressive()` | `MotionScheme.standard()` |
| Icons | 8 brand variants | single CrystaList note icon |

## Design language (Apple Music)

- **Light**: white canvas, `#1C1C1E` type, `#F2F2F7` elevated surfaces.
- **Dark**: true-black canvas, white type, `#1C1C1E` / `#2C2C2E` cards.
- **Accent**: `#FA243C` (Apple Music red), secondary `#FF375F`, link blue `#0071E3`.
- **Type scale**: display 56 → label 11, bold headings, tight tracking.
- **Motion**: standard specs, no spring bounce.

All of it lives in
`shared/src/commonMain/kotlin/com/crystalist/shared/ui/theme/`:
`AppleMusicColorScheme.kt`, `CommonTheme.kt`, `Typography.kt`, `Shapes.kt`, `Motion.kt`.

## Intentional leftovers

These keep pointing at upstream on purpose:

- `com.metrolist.innertubex:*` — external JitPack streaming library, not our code.
- `com.github.MetrolistGroup.*` — Maven coordinates.
- Contributor credits and licence headers — GPL attribution for the original authors.

## Before publishing

1. **Discord Rich Presence** — `DISCORD_APP_ID` in
   `shared/src/commonMain/kotlin/com/crystalist/shared/discord/DiscordRpcManager.kt`
   is still upstream's app id. Create your own Discord application and replace it,
   otherwise presence and login will fail.
2. **Listen Together / device pairing server** — the sync backend still targets
   upstream's `metroserver`. Either self-host or disable the feature.
3. **Updater** — `AppUpdateManager` and `DesktopUpdateManager` read releases from
   `CrystaListGroup/CrystaList` on GitHub. Point them at your own repository.
4. **Signing** — release builds need `KEYSTORE_PATH`, `KEYSTORE_PASSWORD`,
   `KEY_ALIAS`, `KEY_PASSWORD`.
5. **Sentry / Lyprio credentials** — optional; builds work without them.

## Licensing

GPL-3.0. The derived app must stay open source under the same licence, keep the
original attribution, and mark modifications. The "Metrolist" name and logo are
not covered by the licence; this fork uses its own name and artwork.
