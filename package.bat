@echo off
setlocal
cd /d "%~dp0"
echo ========================================================
echo      STUDY PLANNER CLI - STANDALONE JAR PACKAGER
echo ========================================================

where jar >nul 2>&1
if errorlevel 1 goto :no_jar

if not exist bin\com\studyplanner\app\Main.class goto :need_compile

:do_package
echo [*] Packaging standalone executable JAR using native JDK jar tool...
jar --create --file study-planner-cli.jar --main-class com.studyplanner.app.Main -C bin .
if errorlevel 1 goto :pkg_failed

echo [OK] Standalone JAR created: study-planner-cli.jar
echo [*] You can run it anytime using: java -jar study-planner-cli.jar
goto :end

:need_compile
echo [*] Compiling sources first...
call "%~dp0compile.bat"
if errorlevel 1 goto :compile_failed
goto :do_package

:no_jar
echo [!] Native Java jar tool not found in PATH.
exit /b 1

:compile_failed
echo [ERROR] Compilation failed.
exit /b 1

:pkg_failed
echo [ERROR] Failed to create JAR.
exit /b 1

:end
endlocal
