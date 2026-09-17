@echo off
setlocal
cd /d "%~dp0"
echo ========================================================
echo       STUDY PLANNER CLI - NATIVE TEST SUITE
echo ========================================================

where java >nul 2>&1
if errorlevel 1 goto :no_java

if not exist bin\com\studyplanner\test\TestRunner.class goto :need_compile

:run_tests
echo [*] Executing Native Java Automated Tests...
java -cp bin com.studyplanner.test.TestRunner
goto :end

:need_compile
echo [*] Compiling test and source files...
call "%~dp0compile.bat"
if errorlevel 1 goto :compile_failed
goto :run_tests

:no_java
echo [!] Java runtime (java) not found in PATH.
exit /b 1

:compile_failed
echo [ERROR] Compilation failed.
exit /b 1

:end
endlocal
