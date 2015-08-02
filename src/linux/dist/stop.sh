#!/bin/bash
# Stops the Goldilocks server

GOLDILOCKS_PIDFILE=goldilocks.pid

[ -f /etc/default/goldilocks ] && . /etc/default/goldilocks

if [ ! -f $GOLDILOCKS_PIDFILE ] ; then
    echo "$GOLDILOCKS_PIDFILE not found"
    exit 0
fi

PID=`cat $GOLDILOCKS_PIDFILE`
echo "Killing ${PID}"
kill $PID
