@REM
@REM Copyright 2017 the original author or authors.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@REM @echo off
SETLOCAL

@REM Attempt to set APP_HOME
@REM %~dp0 is the expansion of the %0 variable (the current script) to drive-letter and path only.
SET APP_HOME=%~dp0

@REM Add default JVM options here. You may also use JAVA_OPTS and GRADLE_OPTS.
SET DEFAULT_JVM_OPTS=

@REM GRADLE_OPTS is the preferred way to set JVM options for all Gradle commands.
@REM JAVA_OPTS is used by the JVM itself, and may not be honored by all tools that wrap the JVM.

@REM Allow changing the JVM used for Gradle by setting JAVA_HOME.
IF NOT "%JAVA_HOME%" == "" (
    IF EXIST "%JAVA_HOME%\bin\java.exe" (
        SET JAVA_CMD="%JAVA_HOME%\bin\java.exe"
    )
)

IF "%JAVA_CMD%" == "" (
    SET JAVA_CMD=java
)

@REM Determine the Java executable to use for Gradle processes
@REM Check if a custom Java home is provided for Gradle
IF NOT "%GRADLE_JAVA_HOME%" == "" (
    IF EXIST "%GRADLE_JAVA_HOME%\bin\java.exe" (
        SET JAVA_CMD="%GRADLE_JAVA_HOME%\bin\java.exe"
    ) ELSE (
        ECHO WARNING: GRADLE_JAVA_HOME is set to '%GRADLE_JAVA_HOME%' but '%GRADLE_JAVA_HOME%\bin\java.exe' is not an executable. 1>&2
        ECHO          Falling back to system default 'java'. 1>&2
    )
)

@REM Check if Gradle has a wrapper.properties file with a Java home
IF EXIST "%APP_HOME%\gradle\wrapper\gradle-wrapper.properties" (
    FOR /F "tokens=1* delims==" %%A IN ('FINDSTR /R "^java\.home=" "%APP_HOME%\gradle\wrapper\gradle-wrapper.properties"') DO (
        SET JAVA_HOME_PROPERTY=%%B
    )
    IF NOT "%JAVA_HOME_PROPERTY%" == "" (
        IF EXIST "%JAVA_HOME_PROPERTY%\bin\java.exe" (
            SET JAVA_CMD="%JAVA_HOME_PROPERTY%\bin\java.exe"
        ) ELSE IF EXIST "%APP_HOME%\%JAVA_HOME_PROPERTY%\bin\java.exe" (
            SET JAVA_CMD="%APP_HOME%\%JAVA_HOME_PROPERTY%\bin\java.exe"
        ) ELSE (
            ECHO WARNING: Java home '%JAVA_HOME_PROPERTY%' specified in gradle-wrapper.properties could not be found or is not an executable. 1>&2
            ECHO          Falling back to system default 'java'. 1>&2
        )
    )
)

@REM Determine GRADLE_HOME
IF "%GRADLE_HOME%" == "" (
    SET GRADLE_HOME=%APP_HOME%
) ELSE (
    IF NOT EXIST "%GRADLE_HOME%" (
        ECHO ERROR: GRADLE_HOME is set to an invalid directory: %GRADLE_HOME%
        EXIT /B 1
    )
)

@REM Determine GRADLE_USER_HOME
IF "%GRADLE_USER_HOME%" == "" (
    SET GRADLE_USER_HOME=%HOMEDRIVE%%HOMEPATH%\.gradle
) ELSE (
    IF NOT EXIST "%GRADLE_USER_HOME%" (
        ECHO WARNING: GRADLE_USER_HOME is set to a non-existent directory: %GRADLE_USER_HOME% 1>&2
        ECHO          Creating directory: %GRADLE_USER_HOME% 1>&2
        MD "%GRADLE_USER_HOME%"
        IF ERRORLEVEL 1 (
            ECHO ERROR: Failed to create GRADLE_USER_HOME directory: %GRADLE_USER_HOME%
            EXIT /B 1
        )
    )
)

@REM Setup Gradle classpath
SET GRADLE_CLASSPATH=%GRADLE_HOME%\gradle\wrapper\gradle-wrapper.jar

@REM Set up logging for `gradlew` to detect when it's being run as part of a `script-runner` test.
SET GRADLE_OPTS=%GRADLE_OPTS% -Dorg.gradle.daemon.id=%COMPUTERNAME%.%DATE:/=-%.%TIME::=-%.%RANDOM%

@REM Execute Gradle
"%JAVA_CMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% ^
    -classpath "%GRADLE_CLASSPATH%" ^
    org.gradle.wrapper.GradleWrapperMain %*

IF ERRORLEVEL 1 (
    EXIT /B %ERRORLEVEL%
)

ENDLOCAL
EXIT /B 0
        