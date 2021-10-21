runner="./node_modules/test262-harness/bin/run.js"
tests="script/**/*.js"

date +%"T"
$runner "$tests" --test262Dir ../../tests/test262 -t 8
date +%"T"
