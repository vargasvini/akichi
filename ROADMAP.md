# akichi roadmap

Approved design + behaviour decisions, and what is implemented vs pending.
Everything is built and tested on the **dev** channel first, then promoted to **stable**
(see RELEASING.md). UI is implemented incrementally, screen by screen.

## Done (logic)
- **Controller fix auto-activation.** The Fire OS DualSense remap now applies only when it
  makes sense: DualSense (054c:0ce6) **and** a likely-broken environment (Amazon device or
  Android <= 12). On Android 13+/USB it stays off so a correct controller is never re-scrambled.
  The settings toggle is still a manual override; learn-mode is the final fallback.
- **Bitrate cap raised** 50 → 100 Mbps (clamp in `Preferences.validateBitrate`).

## Pending (UI — incremental on dev)
1. **Home** — compact single-console hero; state-aware action: Connect when ready, Wake + a
   "zzz" bubble when in standby; quick start presets (resolution / fps).
2. **In-stream overlay** — performance HUD (latency / fps / bitrate / codec), live quality
   controls, controller-fix indicator, resume, disconnect.
3. **Add console wizard** — discover → link (PSN account id with a "where to find" helper +
   on-console instructions) → ready.
4. **Settings** — TV category rail; controller fix as a first-class item; remapping entry.
5. **Remapper** — interactive learn mode (press-the-button calibration) + shortcut to the
   auto Fire TV fix; per-device profiles.
6. **Bitrate control UI** — Auto (default, adaptive) or Custom slider up to 100 Mbps, with a
   note that the PS5 caps its encoder so very high values rarely improve quality.

## Approach
- Keep the current architecture (Views/XML + activities); evolve the screens incrementally
  for low risk and easy review, rather than a big-bang rewrite. Compose-for-TV can come later
  if D-pad navigation needs it.
- Simplicity first; dev-first; promote to stable only after verifying on the TV.

## Bigger bets (later)
- Rebase the JNI onto chiaki-ng's modern libchiaki (HDR/HEVC Main10, DualSense haptics,
  adaptive bitrate/congestion).
- Paid relay/SaaS for out-of-home Remote Play (recurring revenue).

## Distribution & in-app updates (both feasible)
- **In-app updater ("new version → update here", no manual reinstall).** Feasible. The app
  checks a version manifest (GitHub Releases API or a small hosted JSON) for a higher
  versionCode; if newer, it downloads the APK and installs it via Android `PackageInstaller`
  (needs the `REQUEST_INSTALL_PACKAGES` permission; the user taps one system confirm). Because
  every build shares the committed signing key, it is an **in-place update — no uninstall, data
  preserved**. The consistent-signing work already done is the prerequisite.
- **Appearing in the Downloader app (Fire TV).** Verified on-device: Downloader is a browser
  and its "Search Term" runs a Google web search (go.aftvnews.com/googlesearch?q=...). So chiaki
  shows up purely because of its **web presence** — there is no Downloader app registry. For
  akichi to appear the same way: make the repo + Releases **public** and add a small landing
  page (e.g. GitHub Pages) so search engines index "akichi" + its APK link. Plus register a
  short **Downloader Code** (aftv.news) pointing at the APK URL for one-step installs. Same
  hosted APK feeds the in-app updater — one release pipeline. (Repo is private today; going
  public is the prerequisite for discoverability.)
