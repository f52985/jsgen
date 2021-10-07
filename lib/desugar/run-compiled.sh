runner="./test262-harness/bin/run.js"

function run_compiled () {
  tests=$1

  date +%"T"
  $runner "$tests" --test262Dir test262 -t 8
  date +%"T"
}

run_compiled "compiled-script/**/*.js"
run_compiled "compiled-module/**/*.js"
