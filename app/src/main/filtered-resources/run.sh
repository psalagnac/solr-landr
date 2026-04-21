#!/bin/bash

#
# Simple bash script to start Solr Landr in the terminal
#

if [ "$JAVA_HOME" ]; then
  java_cmd="$JAVA_HOME/bin/java"
else
  java_cmd="java"
fi

if [[ "$1" = 'debug' ]]; then
  shift

  suspend="n"
  if [[ "$1" = 'suspend' ]]; then
    suspend="y"
    shift
  fi

  java_opts="-agentlib:jdwp=transport=dt_socket,server=y,suspend=$suspend,address=*:5005"
fi

$java_cmd $java_opts -jar @jarfile@ "$@"
