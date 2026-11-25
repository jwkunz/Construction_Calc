#!/usr/bin/env bash
# Builds web/construction_calc_vX_Y_Z.html: the app as ONE self-contained HTML file (logo inlined as a data URI).
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT_DIR}"

VERSION="$(tr -d '[:space:]' < ../VERSION)"
OUT="construction_calc_v${VERSION//./_}.html"

rm -f construction_calc_v*.html
python3 - "${OUT}" <<'PY'
import base64, sys
logo = base64.b64encode(open("Construction_Calc.png", "rb").read()).decode()
html = open("Construction_Calc.html", encoding="utf-8").read()
assert 'src="Construction_Calc.png"' in html
open(sys.argv[1], "w", encoding="utf-8").write(html.replace('src="Construction_Calc.png"', 'src="data:image/png;base64,' + logo + '"'))
PY

if grep -E '<script[^>]+src=|<link[^>]+href=|src="[^d"]' "${OUT}" >/dev/null; then
  echo "error: ${OUT} still has external references" >&2
  exit 1
fi
echo "Built ${ROOT_DIR}/${OUT}"
