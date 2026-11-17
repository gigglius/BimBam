#!/usr/bin/env python3
"""Import the original 2014 Roblox drawable images from the supplied APK ZIP.

Usage:
  python tools/import_2014_apk_assets.py /path/to/com.roblox.client.zip

The source archive is not committed to this repository. This script only copies
image resources; compiled Android XML layouts are not copied because they must
be reconstructed as source XML for the Quest app.
"""
import pathlib
import re
import shutil
import sys
import zipfile

if len(sys.argv) != 2:
    raise SystemExit("Usage: python tools/import_2014_apk_assets.py <roblox-apk-or-resource-zip>")

archive = pathlib.Path(sys.argv[1])
if not archive.is_file():
    raise SystemExit(f"File not found: {archive}")

repo = pathlib.Path(__file__).resolve().parents[1]
target = repo / "quest-roblox-vr/app/src/main/res/drawable-nodpi"
target.mkdir(parents=True, exist_ok=True)
allowed = {".png", ".jpg", ".jpeg", ".webp"}
copied = []

with zipfile.ZipFile(archive) as apk:
    for info in apk.infolist():
        match = re.fullmatch(r"res/drawable/([a-z0-9_]+)(\.png|\.jpg|\.jpeg|\.webp)", info.filename)
        if not match:
            continue
        name = pathlib.PurePosixPath(info.filename).name
        if pathlib.Path(name).suffix.lower() not in allowed:
            continue
        output = target / name
        output.write_bytes(apk.read(info))
        copied.append(name)

required = {
    "games2x.png", "catalog2x.png", "inventory2x.png", "builders_club2x.png",
    "profile2x.png", "messages2x.png", "groups2x.png", "leaderboards2x.png",
    "roblox_logo.png", "sb_games.png", "sb_catalog.png", "sb_community.png",
    "sb_my_roblox.png", "sb_develop.png"
}
missing = sorted(required - set(copied))
if missing:
    raise SystemExit("Archive is missing required dashboard artwork: " + ", ".join(missing))

print(f"Imported {len(copied)} original drawable images into {target}")
