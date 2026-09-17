#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "========================================================"
echo "       STUDY PLANNER CLI - LAUNCHER"
echo "========================================================"

if [ ! -f "bin/com/studyplanner/app/Main.class" ]; then
    echo "[*] Compiled class files not found. Compiling first..."
    ./compile.sh
fi

echo "[*] Launching Study Planner CLI..."
java -cp bin com.studyplanner.app.Main
