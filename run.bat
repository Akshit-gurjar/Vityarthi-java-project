@echo off
setlocal
cd /d "%~dp0"
echo ========================================================
echo        STUDY PLANNER CLI - LAUNCHER
echo ========================================================

where java >nul 2>&1
if errorlevel 1 goto :no_java

if not exist bin\com\studyplanner\app\Main.class goto :need_compile

:run_app
echo [*] Launching Study Planner CLI...
java -cp bin com.studyplanner.app.Main
goto :end

:need_compile
echo [*] Compiled classes not found. Compiling first...
call "%~dp0compile.bat"
if errorlevel 1 goto :compile_failed
goto :run_app

:no_java
echo [!] Java runtime (java) not found in PATH.
exit /b 1

:compile_failed
echo [ERROR] Compilation failed. Cannot launch application.
exit /b 1

:end
endlocal
