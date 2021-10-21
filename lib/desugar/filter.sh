date +%"T"

rm -rf script
rm -rf module

test262TestDir="../../tests/test262/test"
prefixLen=25

echo "Creating directory"
for d in `find $test262TestDir -type d`; do
  d1="script/${d:$prefixLen}"
  d2="module/${d:$prefixLen}"
  
  mkdir $d1
  mkdir $d2
done

echo "Discriminating file"
for f in `find $test262TestDir -name "*.js"`; do
  if grep -q "type: SyntaxError" "$f" ; then
    continue
  fi

  if grep -q "flags: \[.*module.*\]" $f || [[ "$f" == *"_FIXTURE.js" ]] ; then
    cp $f "module/${f:$prefixLen}"
  else
    cp $f "script/${f:$prefixLen}"
  fi
done

date +%"T"
