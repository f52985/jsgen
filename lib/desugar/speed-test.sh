orig_dir="no-harness-script"
comp_dir="no-harness-compiled-script"

date +%"T"

#orig test
filenames=`find $orig_dir -name "*.js"`
for filename in $filenames; do
  code=$(< $filename)
  script="
var start = performance.now();
$code
var end = performance.now();
console.log('$filename ' + (end-start) + ' ms');"
  echo $script | node 2> /dev/null
done

#comp test
filenames=`find $comp_dir -name "*.js"`
for filename in $filenames; do
  code=$(< $filename)
  script="
var start = performance.now();
$code
var end = performance.now();
console.log('$filename ' + (end-start) + ' ms');"
  echo $script | node 2> /dev/null
done

date +%"T"
