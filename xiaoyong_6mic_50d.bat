@echo off

set NAME=xiaoyong_6mic_50d
set DIR=%~dp0
xcopy /f /y /s %DIR%zcbrain\build\outputs\apk\*Mtk_testkey.apk  %DIR%APK
move %DIR%APK\*Mtk_testkey.apk %DIR%APK\%NAME%.apk
pause
exit