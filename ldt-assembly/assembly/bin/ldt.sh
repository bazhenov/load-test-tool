#!/bin/sh

canonical_readlink () { 
    cd `dirname $1`; 
    __filename=`basename $1`; 
    if [ -h "$__filename" ]; then 
        canonical_readlink `readlink $__filename`; 
    else 
        echo "`pwd -P`/$__filename"; 
    fi 
}

LINK=$0
[ -h $0 ] && LINK=`canonical_readlink $0`
WHOME=`dirname $LINK`
LDT_CLASSPATH=.:./target/classes:$LDT_CLASSPATH

java $JAVA_OPTS -cp "$WHOME/../lib/*:$LDT_CLASSPATH" com.farpost.ldt.Main $@
