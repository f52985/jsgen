rm -rf no-harness-script

for d in `find script -type d`; do
  d1="no-harness-$d"
  
  mkdir $d1
done

for file in `find script -name "*.no-harness"`; do
  cp $file no-harness-${file%.no-harness}
done
