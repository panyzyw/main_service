@echo off

::rem %DIR% has "\" this singal

set DIR=%~dp0
set APP_WORKSPACE=%DIR%..\
set APP_PROJECT=%DIR%
set ANDROID_NDK_ROOT=D:\Android\android-ndk-r12b
set QUICK_COCOS2DX_ROOT=%DIR%quick-2.2.6
set COCOS2DX_ROOT=%QUICK_COCOS2DX_ROOT%\lib\cocos2d-x
rem set COCOS2DX_ROOT=F:\document\Android\cocos2d-x-2.2.6

::%ANDROID_NDK_ROOT%
echo - config:
echo   ANDROID_NDK_ROOT = %ANDROID_NDK_ROOT% 
echo   APP_WORKSPACE    = %APP_WORKSPACE%
echo   APP_PROJECT 		= %APP_PROJECT%
echo   QUICK_COCOS2DX_ROOT = %QUICK_COCOS2DX_ROOT%
echo   COCOS2DX_ROOT       = %COCOS2DX_ROOT%

echo -------------- set_ndk_project:
rem if use Normal_NDK, set NDK_PROJECT=0
rem if use COCOS2D_NDK, set NDK_PROJECT=1
set NDK_PROJECT=0

echo -------------- set_ndk_debug:
rem if use DEBUG, set NDK_DEBUG=1, otherwise set NDK_DEBUG=0
set NDK_DEBUG=0

if %NDK_DEBUG%==1 (
echo -------------- DEBUG!!!!
set COCOS_NDK_CPPFLAGS=CPPFLAGS:="-DCOCOS2D_DEBUG=1"
set ZCCL_NDK_CFLAGS=CFLAGS:="-DZCCL_DEBUG=1"
) else (
echo -------------- RELEASE!!!!	
set COCOS_NDK_CPPFLAGS=
set ZCCL_NDK_CFLAGS=
)

echo - cleanup:
::if exist "%APP_PROJECT%bin" rmdir /s /q "%APP_PROJECT%bin"
::mkdir "%APP_PROJECT%bin"
::if exist "%APP_PROJECT%assets" rmdir /s /q "%APP_PROJECT%assets"
::mkdir "%APP_PROJECT%assets"

echo - copy scripts:
::mkdir "%APP_PROJECT%assets\scripts"
::xcopy /s /q /y "%APP_WORKSPACE%scripts\*.*" "%APP_PROJECT%assets\scripts\"

echo - copy resources:
::mkdir "%APP_PROJECT%assets\res"
::xcopy /s /q /y "%APP_WORKSPACE%res\*.*" "%APP_PROJECT%assets\res\"


echo Using prebuilt externals:
if %NDK_PROJECT%==0 (

"%ANDROID_NDK_ROOT%\ndk-build.cmd" ^
%COCOS_NDK_CPPFLAGS% ^
%ZCCL_NDK_CFLAGS% ^
NDK_DEBUG=%NDK_DEBUG% ^
-C %APP_PROJECT% 

pause
exit
) else if %NDK_PROJECT%==1 (

"%ANDROID_NDK_ROOT%\ndk-build.cmd" ^
%COCOS_NDK_CPPFLAGS% ^
%ZCCL_NDK_CFLAGS% ^
NDK_DEBUG=%NDK_DEBUG% ^
-C %APP_PROJECT% ^
NDK_MODULE_PATH=%QUICK_COCOS2DX_ROOT%;%COCOS2DX_ROOT%;%COCOS2DX_ROOT%\cocos2dx\platform\third_party\android\prebuilt

pause
exit
)



