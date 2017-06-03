@echo off

set NAME=xiaoyong
set DIR=%~dp0
xcopy /f /y /s %DIR%zcbrain\build\outputs\apk\*-release.apk  %DIR%APK
move %DIR%APK\*-release.apk %DIR%APK\%NAME%.apk
pause
exit