#!/bin/sh

LINK=$0
[ -h $0 ] && LINK=`readlink -f $0`
WHOME=`dirname $LINK`
LDT_CLASSPATH=.:./target/classes:$LDT_CLASSPATH

java $JAVA_OPTS -cp "$WHOME/../lib/*:$LDT_CLASSPATH" com.farpost.ldt.Main $@
