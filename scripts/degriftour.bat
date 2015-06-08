@echo off
cls 


echo.
echo   )))---------- Set up
echo.

set VERSION_CL=10.4.7
set VERSION_CO=10.4.7
set VERSION_CATALOGWAR=2.0.6
set VERSION_WEBTOOLKIT=10.4.7
set VERSION_BP=10.4.7

set INSTANCE=degriftour

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


:CL
if not defined VERSION_CL goto :CO
set CL=cameleo\cameleo-libs-ear\%VERSION_CL%\cameleo-libs-ear-%VERSION_CL%.jar
if     exist %M2_LOCAL%\%CL% copy %M2_LOCAL%\%CL% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CL% copy %M2_REMOTE%\%CL% %JBOSS_SERVER%\deploy

:CO
if not defined VERSION_CO goto :CATALOGWAR
set CO=connectivity\connectivity-ear\%VERSION_CO%\connectivity-ear-%VERSION_CO%.ear
if     exist %M2_LOCAL%\%CO% copy %M2_LOCAL%\%CO% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CO% copy %M2_REMOTE%\%CO% %JBOSS_SERVER%\deploy

:CATALOGWAR
if not defined VERSION_CATALOGWAR goto :WEBTOOLKIT
set CATALOGWAR=specific\degriftour\degriftour-catalog-war\%VERSION_CATALOGWAR%\degriftour-catalog-war-%VERSION_CATALOGWAR%.war
if     exist %M2_LOCAL%\%CATALOGWAR% copy %M2_LOCAL%\%CATALOGWAR% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%CATALOGWAR% copy %M2_REMOTE%\%CATALOGWAR% %JBOSS_SERVER%\deploy

:WEBTOOLKIT
if not defined VERSION_WEBTOOLKIT goto :BP
set WEBTOOLKIT=cameleo\catalog\webapp\cameleo-webtoolkit\%VERSION_WEBTOOLKIT%\cameleo-webtoolkit-%VERSION_WEBTOOLKIT%.jar
if     exist %M2_LOCAL%\%WEBTOOLKIT% copy %M2_LOCAL%\%WEBTOOLKIT% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%WEBTOOLKIT% copy %M2_REMOTE%\%WEBTOOLKIT% %JBOSS_SERVER%\deploy

:BP
if not defined VERSION_BP goto :START
set BP=cameleo\bookingprocess-ear\%VERSION_BP%\bookingprocess-ear-%VERSION_BP%.ear
if     exist %M2_LOCAL%\%BP% copy %M2_LOCAL%\%BP% %JBOSS_SERVER%\deploy
if not exist %M2_LOCAL%\%BP% copy %M2_REMOTE%\%BP% %JBOSS_SERVER%\deploy

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
