@echo off
cls 


echo.
echo   )))---------- Set up
echo.

set VERSION_CL=10.3-M1
set VERSION_CO=10.3-M1
set VERSION_CATALOGWAR=2.3.2
set VERSION_WEBTOOLKIT=10.3-M1
set VERSION_BP=10.3-M1

set INSTANCE=lastminute-responsive

set JBOSS_VERSION=jboss-4.2.1.GA
set JBOSS_ROOT=c:\ts\bin\%JBOSS_VERSION%

set M2_LOCAL=c:\ts\m2.repository
set M2_REMOTE=\\dole\maven2repository

set M1_LOCAL=c:\ts\maven.repository
set M1_REMOTE=\\dole\mavenrepository

set JBOSS_SERVER=%JBOSS_ROOT%\server\%INSTANCE%


echo.
echo   )))---------- Deleting old files
echo.


rmdir /S /Q %JBOSS_SERVER%\log
rmdir /S /Q %JBOSS_SERVER%\tmp
rmdir /S /Q %JBOSS_SERVER%\data
rmdir /S /Q %JBOSS_SERVER%\work




if not defined VERSION_CATALOGWAR goto :START
set CATALOGWAR=specific\lastminute\lastminute-catalog-war\%VERSION_CATALOGWAR%\lastminute-catalog-war-%VERSION_CATALOGWAR%.war
rmdir /s /q destination = %JBOSS_SERVER%\deploy\lastminute-catalog-war-%VERSION_CATALOGWAR%.war   
del /q /f   %JBOSS_SERVER%\deploy\lastminute-catalog-war-%VERSION_CATALOGWAR%.war
if     exist %M2_LOCAL%\%CATALOGWAR% copy %M2_LOCAL%\%CATALOGWAR% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CATALOGWAR% copy %M2_REMOTE%\%CATALOGWAR% %JBOSS_SERVER%\deploy



:START
echo.
echo   )))---------- Starting JBOSS
echo.
echo  INSTANCE        = %INSTANCE%
echo  JAVA_HOME       = %JAVA_HOME%
if defined VERSION_CL echo  VERSION_CL      = %VERSION_CL%
if defined VERSION_CO echo  VERSION_CO      = %VERSION_CO%
if defined VERSION_CATALOGWAR echo  VERSION_CATALOGWAR      = %VERSION_CATALOGWAR%
if defined VERSION_WEBTOOLKIT echo  VERSION_WEBTOOLKIT      = %VERSION_WEBTOOLKIT%
echo.

%JBOSS_ROOT%\bin\run_remote.bat -c %INSTANCE% -b 0.0.0.0
