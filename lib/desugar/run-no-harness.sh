if [ $# -eq 1 ]; then
  cat helper.js no-harness-${1%.injected} $1
  cat helper.js no-harness-${1%.injected} $1 | node
  exit 0
fi

date +%"T"

for file in `find script -name "*.injected"`; do
  msg=`cat helper.js no-harness-${file%.injected} $file | node 2>&1`
  if [ -z "$msg" ]; then
    echo PASS $file
  else
    echo FAIL $file
    echo $msg
  fi
done

date +%"T"
