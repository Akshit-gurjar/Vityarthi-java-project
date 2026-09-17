#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "========================================================"
echo "       STUDY PLANNER CLI - NATIVE COMPILER"
echo "========================================================"

mkdir -p bin
echo "[*] Compiling all Java sources to bin/..."
javac -encoding UTF-8 -d bin $(find src -name "*.java")
echo "[✓] Compilation successful. Class files generated in 'bin/'."
