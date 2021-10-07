date +%"T"

rm -rf script
rm -rf module

echo "Creating directory"
for d in `find test262/test -type d`; do
  d1="script/${d:13}"
  d2="module/${d:13}"
  
  mkdir $d1
  mkdir $d2
done

echo "Discriminating file"
for f in `find test262/test -name "*.js"`; do
  if grep -q "type: SyntaxError" "$f" ; then
    continue
  fi

  if grep -q "flags: \[.*module.*\]" $f || [[ "$f" == *"_FIXTURE.js" ]] ; then
    cp $f "module/${f:13}"
  else
    cp $f "script/${f:13}"
  fi
done

date +%"T"
