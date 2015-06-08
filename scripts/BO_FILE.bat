@echo off
cls 


echo.
echo   )))---------- Set up
echo.

REM set VERSION_BO=11.3-SNAPSHOT
set VERSION_B2B=11.3-SNAPSHOT
set VERSION_MO=11.3-SNAPSHOT
set VERSION_CL=11.3-SNAPSHOT
set VERSION_CO=11.3-SNAPSHOT

set INSTANCE=bo-b2b-mo

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

:BO
if not defined VERSION_BO goto :B2B
set BO=orchestra\orchestra-bo-ear\%VERSION_BO%\orchestra-bo-ear-%VERSION_BO%.ear
if     exist %M2_LOCAL%\%BO% copy %M2_LOCAL%\%BO% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%BO% copy %M2_REMOTE%\%BO% %JBOSS_SERVER%\deploy

:B2B
if not defined VERSION_B2B goto :MO
set B2B=orchestra\orchestra-b2b-war\%VERSION_B2B%\orchestra-b2b-war-%VERSION_B2B%.war
if     exist %M2_LOCAL%\%B2B% copy %M2_LOCAL%\%B2B% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%B2B% copy %M2_REMOTE%\%B2B% %JBOSS_SERVER%\deploy

:MO
if not defined VERSION_MO goto :CL
REM set MO=orchestra\orchestra-ear\%VERSION_MO%\orchestra-ear-%VERSION_MO%.ear
REM if     exist %M2_LOCAL%\%MO% copy %M2_LOCAL%\%MO% %JBOSS_SERVER%\deploy
REM if not exist %M2_LOCAL%\%MO% copy %M2_REMOTE%\%MO% %JBOSS_SERVER%\deploy

:CL
if not defined VERSION_CL goto :CO
set CL=cameleo\cameleo-libs-ear\%VERSION_CL%\cameleo-libs-ear-%VERSION_CL%.jar
if     exist %M2_LOCAL%\%CL% copy %M2_LOCAL%\%CL% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CL% copy %M2_REMOTE%\%CL% %JBOSS_SERVER%\deploy

:CO
if not defined VERSION_CO goto :XMCTO
set CO=connectivity\connectivity-ear\%VERSION_CO%\connectivity-ear-%VERSION_CO%.ear
if     exist %M2_LOCAL%\%CO% copy %M2_LOCAL%\%CO% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CO% copy %M2_REMOTE%\%CO% %JBOSS_SERVER%\deploy

:XMCTO
if not defined VERSION_XMCTO goto :START
set XMCTO=mcto.external.order\ears\xmcto.external.order-%VERSION_XMCTO%.ear
if     exist %M1_LOCAL%\%XMCTO% copy %M1_LOCAL%\%XMCTO% %JBOSS_SERVER%\deploy
if not exist %M1_LOCAL%\%XMCTO% copy %M1_REMOTE%\%XMCTO% %JBOSS_SERVER%\deploy


:START
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
