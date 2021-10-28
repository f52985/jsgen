runner="./node_modules/.bin/babel"

function compile() {
  in=$1
  out=$2
  arg="$3"

  rm -rf $out
  
  date +%"T"
  echo "list of fails:"
  $runner $in --out-dir $out $arg
  date +%"T"
}

if [[ "$1" == "--no-harness" ]]; then
  compile "no-harness-script" "no-harness-min-script" "--config-file ./min.config.json --no-comments --minified"
  compile "no-harness-script" "no-harness-min-compiled-script" "--config-file ./min-compile.config.json --no-comments --minified"
else
  compile "script" "min-script" "--config-file ./min.config.json --no-comments --minified"
  compile "script" "min-compiled-script" "--config-file ./min-compile.config.json --no-comments --minified"
fi
