if [ $# -eq 1 ]; then
  cat helper.js no-harness-compiled-${1%.injected} $1
  cat helper.js no-harness-compiled-${1%.injected} $1 | node
  exit 0
fi

date +%"T"

for file in `find script -name "*.injected"`; do
  cat helper.js no-harness-compiled-${file%.injected} $file | node >/dev/null 2>/dev/null
  if [ $? -eq 0 ]; then
    echo PASS $file
  else
    echo FAIL $file
  fi
done

date +%"T"
