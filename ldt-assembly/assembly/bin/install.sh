#!/bin/sh

LINK=$0
[ -h $0 ] && LINK=`readlink $0`
WHOME=`dirname $LINK`

ln -sf $WHOME/ldt.sh /usr/bin/ldt
chmod +x /usr/bin/ldt