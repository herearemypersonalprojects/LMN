@echo off
cls 


echo.
echo   )))---------- Run my first Struts
echo.

set INSTANCE=default

set JBOSS_VERSION=jboss-4.2.1.GA
set JBOSS_ROOT=c:\ts\bin\%JBOSS_VERSION%


set JBOSS_SERVER=%JBOSS_ROOT%\server\%INSTANCE%


echo.
echo   )))---------- Replacing ear files
echo.


echo.
echo   )))---------- Starting JBOSS
echo.
echo  INSTANCE        = %INSTANCE%
echo  JAVA_HOME       = %JAVA_HOME%
if defined VERSION_BO echo  VERSION_BO      = %VERSION_BO%
if defined VERSION_CL echo  VERSION_CL      = %VERSION_CL%
if defined VERSION_CO echo  VERSION_CO      = %VERSION_CO%
if defined VERSION_BP echo  VERSION_BP      = %VERSION_BP%
if defined VERSION_WS echo  VERSION_WS      = %VERSION_WS%
if defined VERSION_DB echo  VERSION_DB      = %VERSION_DB%
echo.

%JBOSS_ROOT%\bin\run_remote.bat -c %INSTANCE% -b 0.0.0.0
