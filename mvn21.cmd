@echo off
REM 本机 Maven 默认是 JDK17，项目需要 21；用此脚本代替直接 mvn
set "JAVA_HOME=C:\Users\yzd\.jdks\ms-21.0.11"
set "PATH=%JAVA_HOME%\bin;%PATH%"
mvn %*
