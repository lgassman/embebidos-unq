#!/bin/sh
JAR="`dirname $0`/javacc-6.1.2.jar"

case "`uname`" in
     CYGWIN*) JAR="`cygpath --windows -- "$JAR"`" ;;
esac

java -classpath "$JAR" javacc "$@"
