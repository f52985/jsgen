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

for file in `find script -name "*.injected"`; do
  if [[ `cat $file` != "// Error" ]]; then
    # Normal
    msg=`cat helper.js $prefix${file%.injected} $file | timeout 1s node 2>&1`
    if [ $? -eq 0 ] && [ -z "$msg" ]; then 
      echo PASS $file
    else
      echo FAIL $file
      if [ -z "$msg" ]; then
        msg="TIMEOUT"
      fi
      echo $msg
    fi
  else
    #Error
    msg=`cat $prefix${file%.injected} | timeout 1s node 2>&1`
    if [ $? -eq 0 ] && [ -z "$msg" ]; then
      echo FAIL $file
      echo "Error Expected but got no Error"
    else
      echo PASS $file
    fi
  fi
done

date +%"T"
