PREFIX="PREFIX"

echo dir total orig_fail com_fail diff ratio

for d in `find test262/test -type d -mindepth 1`; do
  dd=${d:13}

  if [[ $dd == $PREFIX* ]]; then 
    continue
  fi
  
  # total
  total=`grep $dd result/test262.txt | wc -l`

  # number of fails in original test
  origFail=`grep $dd result/test262.txt | grep FAIL | wc -l`

  # number of fails in compiled test
  compileFail=`grep $dd result/compiled-test262.txt | grep FAIL | wc -l`

  (( diff = compileFail - origFail ))
  (( ratio = diff * 100 / total ))

  if [ $diff -eq 0 ]; then
    PREFIX=$dd
  else
    echo $dd $total $origFail $compileFail $diff $ratio"%"
    PREFIX="PREFIX"
  fi
done
