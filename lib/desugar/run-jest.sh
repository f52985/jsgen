#!/bin/zsh
#bash has a bug

if [[ "$1" == *.injected ]]; then
  cat helper.js ${1%.injected} $1
  cat helper.js ${1%.injected} $1 | node
  exit 0
fi

date +%"T"

if [[ "$1" == "--compiled" ]]; then
  prefix="compiled-"
fi

for assert in `find script -name "*.injected"`; do
  script=$prefix${assert%.injected}
  if [[ `cat $assert` != "// Error" ]]; then
    # Normal
    msg=`cat helper.js $script $assert | timeout 1s node 2>&1`
    if [ $? -eq 0 ] && [ -z "$msg" ]; then 
      echo PASS $script
    else
      echo FAIL $script
      if [ -z "$msg" ]; then
        msg="TIMEOUT"
      fi
      echo $msg
    fi
  else
    #Error
    msg=`(echo \"use strict\"\; ; cat $script) | timeout 1s node 2>&1`
    if [ $? -eq 0 ] && [ -z "$msg" ]; then
      echo FAIL $script
      echo "Error Expected but got no Error"
    else
      echo PASS $script
    fi
  fi
done

date +%"T"
