#!/bin/sh

LINK=$0
[ -h $0 ] && LINK=`readlink $0`
WHOME=`dirname $LINK`
LDT_CLASSPATH=.:./target/classes

java -cp "$WHOME/../lib/*:$LDT_CLASSPATH" com.farpost.ldt.Main $@