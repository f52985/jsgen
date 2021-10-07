runner="./test262-harness/bin/run.js"
tests="test262/test/**/*.js"

date +%"T"
$runner "$tests" -t 8
date +%"T"
