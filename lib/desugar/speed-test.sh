#!/bin/zsh

orig_dir="script"
comp_dir="compiled-script"

date +%"T"

# warm up
echo "for(var x=0;x<1000000000;x++);" | node

# orig test
filenames=( `find $orig_dir -name "*.js"` )
for filename in ${filenames[@]}; do
  code=`cat $filename`
  script="\"use strict\";
var start = performance.now();
$code
var end = performance.now();
console.log('$filename ' + (end-start) + ' ms');"
  echo $script | timeout 1s node 2> /dev/null
done

# comp test
filenames=( `find $comp_dir -name "*.js"` )
for filename in ${filenames[@]}; do
  code=`cat $filename`
  require=`echo $code | grep require`
  code=`echo $code | grep -v require`
  script="\"use strict\";
$require
var start = performance.now();
$code
var end = performance.now();
console.log('$filename ' + (end-start) + ' ms');"
  echo $script | timeout 1s node 2> /dev/null
done

date +%"T"
