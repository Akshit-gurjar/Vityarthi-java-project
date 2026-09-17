#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "========================================================"
echo "      STUDY PLANNER CLI - NATIVE TEST SUITE"
echo "========================================================"

if [ ! -f "bin/com/studyplanner/test/TestRunner.class" ]; then
    echo "[*] Compiling test and source files..."
    ./compile.sh
fi

echo "[*] Executing Native Java Automated Tests..."
java -cp bin com.studyplanner.test.TestRunner
