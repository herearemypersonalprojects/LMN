@echo off
cls 


echo.
echo   )))---------- Set up
echo.

set VERSION_QA=0.0.1-SNAPSHOT

set INSTANCE=artifactID_hoctap

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
del /q /f   %JBOSS_SERVER%\deploy\*.jar
del /q /f   %JBOSS_SERVER%\deploy\*.ear
del /q /f   %JBOSS_SERVER%\deploy\*.war

echo.
echo   )))---------- Replacing ear files
echo.


:QA
if not defined VERSION_QA goto :START
set QA=com\mkyong\common\Struts2Example\com.mkyong.common\Struts2Example-com.mkyong.common.war
if     exist %M2_LOCAL%\%QA% copy %M2_LOCAL%\%QA% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%QA% copy %M2_REMOTE%\%QA% %JBOSS_SERVER%\deploy

:START
echo.
echo   )))---------- Starting JBOSS
echo.
echo  INSTANCE        = %INSTANCE%
echo  JAVA_HOME       = %JAVA_HOME%
if defined VERSION_QA echo  VERSION_QA      = %VERSION_QA%
echo.

%JBOSS_ROOT%\bin\run_remote.bat -c %INSTANCE% -b 0.0.0.0
