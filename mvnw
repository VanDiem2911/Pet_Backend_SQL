#!/bin/sh
exec "$(dirname "$0")/.mvn/wrapper/maven-wrapper.properties" >/dev/null 2>&1
./mvnw.cmd "$@"