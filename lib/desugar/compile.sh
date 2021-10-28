runner="./node_modules/.bin/babel"

function compile() {
  in=$1
  out=$2
  arg=$3

  rm -rf $out
  
  date +%"T"
  echo "list of fails:"
  $runner $in --out-dir $out $arg
  date +%"T"
}

in="${1:-script}"
out="${2:-compiled-script}"

compile $in $out
#compile "module" "compiled-module" "--source-type module"
