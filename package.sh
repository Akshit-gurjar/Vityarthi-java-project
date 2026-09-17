#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "========================================================"
echo "     STUDY PLANNER CLI - STANDALONE JAR PACKAGER"
echo "========================================================"

if [ ! -f "bin/com/studyplanner/app/Main.class" ]; then
    ./compile.sh
fi

echo "[*] Packaging standalone executable JAR using native JDK jar tool..."
jar --create --file study-planner-cli.jar --main-class com.studyplanner.app.Main -C bin .
echo "[✓] Standalone JAR created: study-planner-cli.jar"
echo "[*] You can run it anytime using: java -jar study-planner-cli.jar"
