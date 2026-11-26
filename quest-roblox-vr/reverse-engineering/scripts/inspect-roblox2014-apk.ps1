param(
  [Parameter(Mandatory=$true)][string]$ApkPath,
  [string]$OutputDir = ".\roblox2014-analysis",
  [string]$Jadx = "jadx",
  [string]$LlvmReadelf = "llvm-readelf",
  [string]$LlvmObjdump = "llvm-objdump",
  [string]$LlvmStrings = "llvm-strings"
)

$ErrorActionPreference = "Stop"
$apk = (Resolve-Path $ApkPath).Path
$out = [System.IO.Path]::GetFullPath($OutputDir)
New-Item -ItemType Directory -Force -Path $out | Out-Null
$extract = Join-Path $out "apk-extracted"
New-Item -ItemType Directory -Force -Path $extract | Out-Null

# APK is ZIP-format; copy to a .zip name so Expand-Archive accepts it.
$zipPath = Join-Path $out "input-apk.zip"
Copy-Item -Force $apk $zipPath
Expand-Archive -LiteralPath $zipPath -DestinationPath $extract -Force
Remove-Item $zipPath -Force

$so = Join-Path $extract "lib\armeabi\libroblox.so"
if (!(Test-Path $so)) {
  Write-Warning "Expected ARM32 libroblox.so was not found at $so. Check APK ABI folders."
  Get-ChildItem (Join-Path $extract "lib") -Recurse -ErrorAction SilentlyContinue | Select-Object FullName
  exit 2
}

# Preserve command output in files for analysis; the script itself does not modify APK contents.
& $LlvmReadelf -h $so | Out-File (Join-Path $out "libroblox-elf-header.txt") -Encoding utf8
& $LlvmReadelf -Ws $so | Out-File (Join-Path $out "libroblox-symbols.txt") -Encoding utf8
& $LlvmReadelf -d $so | Out-File (Join-Path $out "libroblox-dynamic.txt") -Encoding utf8
& $LlvmObjdump -d --demangle $so | Out-File (Join-Path $out "libroblox-disassembly.txt") -Encoding utf8
& $LlvmStrings $so | Out-File (Join-Path $out "libroblox-strings.txt") -Encoding utf8

# Decompile DEX to Java-like source and resources for Activity/lifecycle tracing.
& $Jadx -d (Join-Path $out "jadx-output") $apk

Write-Host ""
Write-Host "Analysis output: $out"
Write-Host "Priority files:"
Write-Host "  libroblox-symbols.txt"
Write-Host "  libroblox-disassembly.txt"
Write-Host "  jadx-output"
Write-Host ""
Write-Host "Search for JNI_OnLoad, nativeStartUpGraphics, nativeStartGame, ActivityGlView,"
Write-Host "surfaceCreated, surfaceChanged, surfaceDestroyed, and native method declarations."
