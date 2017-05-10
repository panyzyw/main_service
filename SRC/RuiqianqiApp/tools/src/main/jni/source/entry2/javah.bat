@echo off
::rem %DIR% has "\" this singal
set DIR=%~dp0
set JDK="C:\Program Files\Java\jdk1.8.0_92"
%JDK%\bin\javah -d %DIR%header -classpath %DIR%..\..\..\java com.zccl.ruiqianqi.tools.jni.NdkTools
pause
exit