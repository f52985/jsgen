runner="./node_modules/test262-harness/bin/run.js"

function run_compiled () {
  tests=$1

  date +%"T"
  $runner "$tests" --test262Dir ../../tests/test262 --includesDir my-harness -t 16
  date +%"T"
}

run_compiled "compiled-script/**/*.js"
#run_compiled "compiled-module/**/*.js"
