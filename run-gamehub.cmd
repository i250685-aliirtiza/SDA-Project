@echo off
setlocal
pushd "%~dp0" || exit /b 1
set "GAMEHUB_JDK="

if defined JAVA_HOME call :checkJdk "%JAVA_HOME%"
for /d %%J in ("%ProgramFiles%\Eclipse Adoptium\jdk-21*" "%ProgramFiles%\Java\jdk-21*" "%ProgramFiles%\Microsoft\jdk-21*" "%ProgramFiles%\Amazon Corretto\jdk21*" "%USERPROFILE%\.jdks\*21*") do (
    if not defined GAMEHUB_JDK call :checkJdk "%%~fJ"
)
if not defined GAMEHUB_JDK (
    for /f "delims=" %%J in ('where javac.exe 2^>nul') do (
        if not defined GAMEHUB_JDK call :checkJdk "%%~dpJ.."
    )
)
if not defined GAMEHUB_JDK (
    echo GameHub requires JDK 21 or newer.
    echo Install a JDK and set JAVA_HOME to the JDK directory, then run this command again.
    popd
    exit /b 1
)

set "JAVA_HOME=%GAMEHUB_JDK%"
if /i "%~1"=="verify" (
    call mvnw.cmd clean verify
) else (
    call mvnw.cmd javafx:run
)
set "GAMEHUB_EXIT=%ERRORLEVEL%"
popd
exit /b %GAMEHUB_EXIT%

:checkJdk
if not exist "%~1\bin\javac.exe" exit /b 0
if not exist "%~1\release" exit /b 0
set "GAMEHUB_JDK_MAJOR="
for /f "tokens=2 delims==." %%V in ('findstr /b "JAVA_VERSION=" "%~1\release" 2^>nul') do set "GAMEHUB_JDK_MAJOR=%%V"
if not defined GAMEHUB_JDK_MAJOR exit /b 0
set "GAMEHUB_JDK_MAJOR=%GAMEHUB_JDK_MAJOR:"=%"
if %GAMEHUB_JDK_MAJOR% LSS 21 exit /b 0
set "GAMEHUB_JDK=%~f1"
exit /b 0
