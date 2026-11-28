# Construction Calc

![Construction Calc Logo](web/Construction_Calc.png)

An imperial measurement calculator for construction work: decimals, whole numbers and mixed fractions. It runs entirely on your device as a single HTML page and works offline.

## Get it

| Where | What |
|-------|------|
| **[jwkunz.github.io/Construction_Calc](https://jwkunz.github.io/Construction_Calc/)** | The web app, always the latest `main` (deployed by GitHub Pages) |
| **[Releases](https://github.com/jwkunz/Construction_Calc/releases)** | `construction-calc-vX.Y.Z.html`, a single self-contained file you can save and open locally, and `construction-calc-vX.Y.Z.apk`, the Android app |

## Repository layout

```
VERSION                  the one place the project version lives
web/                     the calculator (plain HTML)
  Construction_Calc.html   the UI source (edit this)
  Construction_Calc.png    logo
  scripts/                 single_file_package.sh
Android/                 Gradle project: a WebView app that bundles the web build
scripts/version.mjs      keeps VERSION and the Android build in agreement
docs/                    build notes per version, privacy policy
.github/workflows/       ci.yml (checks, builds, releases) and pages.yml (site deploy)
```

The web app is edited in exactly one place, `web/`. Both the GitHub Pages site and the Android app are built from it.

## How it fits together

`web/scripts/single_file_package.sh` inlines the logo into the HTML to produce `web/construction_calc_vX_Y_Z.html` (git-ignored). That file is:

- copied to the site by `pages.yml`,
- bundled by Gradle into the Android app as `assets/index.html`, which `MainActivity` loads in a WebView,
- attached to each GitHub release.

## Building locally

```bash
./web/scripts/single_file_package.sh        # builds web/construction_calc_vX_Y_Z.html; open it in a browser
cd Android && ./gradlew :app:assembleDebug  # needs JDK 21 and the Android SDK
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Pass `-PskipWebBuild` to Gradle to reuse an existing `web/construction_calc_v*.html`. CI signs the release APK with the key in the repository's `RELEASE_*` secrets. A local `assembleRelease` without them produces an unsigned APK.

## Continuous integration

- **`ci.yml`** runs on pushes to `main`, tags and pull requests: checks the version files agree, builds the single HTML file (verifying it has no external references), and builds the release APK.
- **On a `v*` tag** it also publishes a GitHub release containing the HTML file and the APK.
- **`pages.yml`** deploys the single HTML file to GitHub Pages on every push to `main`.

## Versioning and releasing

```bash
node scripts/version.mjs                # print the version
node scripts/version.mjs set 1.2.0      # update VERSION
node scripts/version.mjs check          # fail if things disagree
```

Add notes under `docs/build_notes/`, then commit and tag:

```bash
git tag v1.2.0 && git push origin v1.2.0
```

CI refuses to release if the tag doesn't match `VERSION`. The Android `versionName` and `versionCode` (`major*10000 + minor*100 + patch`) are derived from `VERSION`.

## Privacy

No ads, analytics, accounts or network access. See [docs/Construction_Calc_privacy_policy.md](docs/Construction_Calc_privacy_policy.md).

## License

MIT. See [LICENSE](LICENSE).
