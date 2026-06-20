# akichi

**Remote Play client for PS4 & PS5 — TV-first.**

akichi lets you stream your PlayStation 4 or PlayStation 5 to your TV box
(Fire TV / Android TV) and play it with a controller, over your local network
or the internet.

> akichi is an independent open-source project and is **not affiliated with,
> endorsed by, or certified by Sony Interactive Entertainment**. "PlayStation",
> "PS4" and "PS5" are trademarks of Sony Interactive Entertainment.

## Why akichi

Built specifically for the living-room / TV use case, with fixes the desktop
forks don't handle:

* **Controller mapping fix for Fire TV** — when a DualSense is paired to a
  Fire TV (Fire OS / Android 11–12), the system delivers a scrambled button and
  trigger layout. akichi detects this and corrects it automatically (see
  `CONTROLLER_DIAGNOSIS.md`). Toggle: *Settings → Fix DualSense mapping (Fire TV)*.
* Working touchpad, controller vibration, and on-screen control toggles.
* TV-first UI focus (in progress).

## Status

Early. The controller-mapping fix and the TV install path work today. The
roadmap (TV-native navigation, HDR/HEVC, DualSense haptics, adaptive bitrate
via the modern libchiaki core, an input remapper with a visual "learn" mode) is
tracked in the project.

## Credits & license

akichi is a fork/derivative of [Chiaki](https://git.sr.ht/~thestr4ng3r/chiaki)
by Florian Märkl and of [chiaki-ng](https://github.com/streetpea/chiaki-ng) by
Street Pea and contributors. Huge thanks to them — without their work on the
Remote Play protocol this would not exist.

Licensed under the **GNU Affero General Public License v3.0** with an OpenSSL
linking exception (`LicenseRef-AGPL-3.0-only-OpenSSL`). See `LICENSE` /
`COPYING` and `NOTICE`. The complete corresponding source is in this repository.
