#!/usr/bin/env sh

#
# Copyright 2017 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  LS=`ls -ld "$PRG"`
  LINK=`expr "$LS" : '.*-> \(.*\)$'`
  if expr "$LINK" : '/.*' > /dev/null; then
    PRG="$LINK"
  else
    PRG=`dirname "$PRG"`/"$LINK"
  fi
done

APP_HOME=`dirname "$PRG"`
APP_HOME=`cd "$APP_HOME" && pwd`

# Add default JVM options here. You may also use JAVA_OPTS and GRADLE_OPTS.
# The Java System properties '-Dgraal.CompilerOptions=...' and '-Dgraal.debug.CompilationBuffer='
# can be used to set GraalVM specific compiler options or control the size of the compilation buffer.
DEFAULT_JVM_OPTS=""

# GRADLE_OPTS is the preferred way to set JVM options for all Gradle commands.
# JAVA_OPTS is used by the JVM itself, and may not be honored by all tools that wrap the JVM.

# Allow changing the JVM used for Gradle by setting JAVA_HOME.
if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/bin/java" ]; then
        JAVA_CMD="$JAVA_HOME/bin/java"
    fi
fi

if [ -z "$JAVA_CMD" ]; then
    JAVA_CMD="java"
fi

# Determine the Java executable to use for Gradle processes
# Check if a custom Java home is provided for Gradle
if [ -n "$GRADLE_JAVA_HOME" ]; then
    if [ -x "$GRADLE_JAVA_HOME/bin/java" ]; then
        JAVA_CMD="$GRADLE_JAVA_HOME/bin/java"
    else
        echo "WARNING: GRADLE_JAVA_HOME is set to '$GRADLE_JAVA_HOME' but '$GRADLE_JAVA_HOME/bin/java' is not an executable." >&2
        echo "         Falling back to system default 'java'." >&2
    fi
fi

# Check if Gradle has a wrapper.properties file with a Java home
if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.properties" ]; then
    JAVA_HOME_PROPERTY=`grep "java.home" "$APP_HOME/gradle/wrapper/gradle-wrapper.properties" | cut -d'=' -f2-`
    if [ -n "$JAVA_HOME_PROPERTY" ]; then
        if [ -x "$JAVA_HOME_PROPERTY/bin/java" ]; then
            JAVA_CMD="$JAVA_HOME_PROPERTY/bin/java"
        elif [ -x "$APP_HOME/$JAVA_HOME_PROPERTY/bin/java" ]; then
            JAVA_CMD="$APP_HOME/$JAVA_HOME_PROPERTY/bin/java"
        else
            echo "WARNING: Java home '${JAVA_HOME_PROPERTY}' specified in gradle-wrapper.properties could not be found or is not an executable." >&2
            echo "         Falling back to system default 'java'." >&2
        fi
    fi
fi

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
mingw=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true ;;
  MINGW*)  mingw=true ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything else
if $cygwin; then
  APP_HOME=`cygpath --unix "$APP_HOME"`
fi

# Determine GRADLE_HOME
if [ -z "$GRADLE_HOME" ]; then
  GRADLE_HOME="$APP_HOME"
else
  if [ ! -d "$GRADLE_HOME" ]; then
    echo "ERROR: GRADLE_HOME is set to an invalid directory: $GRADLE_HOME"
    exit 1
  fi
fi

# Determine GRADLE_USER_HOME
if [ -z "$GRADLE_USER_HOME" ]; then
  GRADLE_USER_HOME="$HOME/.gradle"
else
  if [ ! -d "$GRADLE_USER_HOME" ]; then
    echo "WARNING: GRADLE_USER_HOME is set to a non-existent directory: $GRADLE_USER_HOME"
    echo "         Creating directory: $GRADLE_USER_HOME"
    mkdir -p "$GRADLE_USER_HOME"
    if [ $? -ne 0 ]; then
      echo "ERROR: Failed to create GRADLE_USER_HOME directory: $GRADLE_USER_HOME"
      exit 1
    fi
  fi
fi

# Setup Gradle classpath
GRADLE_CLASSPATH="$GRADLE_HOME/gradle/wrapper/gradle-wrapper.jar"

# Set up logging for `gradlew` to detect when it's being run as part of a `script-runner` test.
GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.daemon.id=`hostname`.`date +%s`"

# If GRADLE_EXIT_CAPTURE is set, we need to capture the exit code of Java and pass it back.
if [ -n "$GRADLE_EXIT_CAPTURE" ] ; then
  "$JAVA_CMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$GRADLE_CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
  exit $?
else
  exec "$JAVA_CMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$GRADLE_CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
fi
        