@echo off

set DIR=%~dp0
rmdir /s/q %DIR%obj
rmdir /s/q %DIR%objs
rmdir /s/q %DIR%libs\armeabi
rmdir /s/q %DIR%libs\armeabi-v7a
rmdir /s/q %DIR%libs\x86

rmdir /s/q %DIR%..\..\objs
::rmdir /s/q %DIR%assets

pause
exit