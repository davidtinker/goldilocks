#!/bin/bash
# Start Goldilocks server running in the background

GOLDILOCKS_LOG=goldilocks.log
GOLDILOCKS_PIDFILE=goldilocks.pid

[ -f /etc/default/goldilocks ] && . /etc/default/goldilocks

set -e

cd /var/lib/goldilocks
nohup java -jar goldilocks.jar >> $GOLDILOCKS_LOG 2>&1 &
PID=$!

echo $PID > $GOLDILOCKS_PIDFILE

echo "Goldilocks PID ${PID}, tail -f $GOLDILOCKS_LOG to check for successful startup"
