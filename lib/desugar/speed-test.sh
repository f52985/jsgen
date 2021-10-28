test_dir="../../tests/js"
filenames=`ls $test_dir/*.js`

touch "temp-test.js"
touch "speed-test.txt"

date +%"T" > speed-test.txt

for filename in $filenames; do
  code=$(< $filename)
  echo "function test() {
$code
}
var start = performance.now();
test();
var end = performance.now();
console.log('$filename, '+(end-start)+'ms');\n" > temp-test.js
  node "temp-test.js" >> speed-test.txt
done

date +%"T" >> speed-test.txt
rm "temp-test.js"