@echo off
REM ============================================================
REM  Project 2: Java + JDBC Campus Job System - one-click run
REM  Double-click this file: compile + run
REM ============================================================
cd /d "%~dp0"

set JAR=lib\mysql-connector-j-8.0.33.jar

echo [1/2] Compiling...
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "%JAR%" -d out @sources.txt
if errorlevel 1 (
    echo COMPILE FAILED. Check the error messages above.
    del sources.txt
    pause
    exit /b 1
)
del sources.txt
echo Compile OK.
echo.

echo [2/2] Running...
java -Dfile.encoding=UTF-8 -cp "out;%JAR%" com.campus.Main
echo.

echo To run the JDBC step-by-step trace demo, use:
echo   java -Dfile.encoding=UTF-8 -cp "out;%JAR%" com.campus.trace.JdbcTraceDemo
echo.
pause
