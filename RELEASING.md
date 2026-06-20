# Releasing akichi — cadenced release process

Goal: the **stable** app on the TV never breaks. New work is proven on a separate
**dev** app first, then promoted to stable in curated, versioned releases.

## Two channels (installed side-by-side)

| Channel | applicationId | Label | Who uses it |
|---------|---------------|-------|-------------|
| stable  | `com.akichi.tv`     | akichi      | daily driver — only proven releases |
| dev     | `com.akichi.tv.dev` | akichi dev (amber icon) | testing lab — may crash, never touches stable |

Different `applicationId` ⇒ separate installs and separate data. A crash in dev can
never affect the stable app. Each channel keeps its own console registration.

## Flow

1. **Build (continuous).** Every push to `main` runs CI and produces both flavor
   debug APKs (`akichi-apks` artifact: `app-stable-debug.apk`, `app-dev-debug.apk`).
2. **Test on dev.** Install only the **dev** APK on the TV and verify the change.
   Crashes here are harmless to the daily app.
3. **Review gate.** Show the change (screens/behaviour) for approval before promoting.
4. **Promote to stable.** When a batch is approved:
   - bump `versionCode` and set `versionName` (e.g. `v0.2.0`),
   - tag the commit `vX.Y.Z`,
   - install that build's **stable** APK over the existing stable app
     (in-place upgrade — same applicationId + signature, so data is preserved).
5. The user always runs **stable**; **dev** is the preview that runs ahead.

So stable intentionally lags dev: dev is the bleeding edge, stable is the curated
release line. Batch changes into versioned releases — don't promote every commit.

## Versioning

- Pre-1.0 while shaping the product: `v0.x.y`. Patch = fixes, minor = features.
- `versionCode` is a single incrementing integer, bumped on every stable release.

## Data safety on upgrade

- Same-channel upgrade (`install -r`) preserves app data (incl. console registration).
- Channel/applicationId change needs a one-time data migration (the registered-console
  DB can be transplanted via adb — see the deploy notes).

## Quality gate (before promoting to stable)

- App launches and reaches the console list without crashing.
- A full session connects and the controller behaves (the Fire TV DualSense fix holds).
- Critical-path tests pass (unit + integration) — we test only what must not regress,
  not for coverage.
