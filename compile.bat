@echo off
setlocal
cd /d "%~dp0"
echo ========================================================
echo        STUDY PLANNER CLI - NATIVE COMPILER
echo ========================================================

where javac >nul 2>&1
if errorlevel 1 goto :no_javac

if not exist bin mkdir bin

echo [*] Compiling Java source and test files to bin/...
javac -encoding UTF-8 -d bin -sourcepath "src\main\java;src\test\java" src\main\java\com\studyplanner\app\Main.java src\test\java\com\studyplanner\test\TestRunner.java src\test\java\com\studyplanner\*.java
if errorlevel 1 goto :compile_failed

echo [OK] Compilation successful. Class files generated in 'bin/'.
goto :end

:no_javac
echo [!] Java compiler (javac) not found in PATH.
echo [*] Please install JDK 21+ and ensure javac is in your PATH.
exit /b 1

:compile_failed
echo [ERROR] Compilation failed.
exit /b 1

:end
endlocal
