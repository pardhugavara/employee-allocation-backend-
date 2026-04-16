#!/bin/sh
# Maven Wrapper script
set -e
MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" "$@"
