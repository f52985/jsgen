cp -r generated script

while read file; do
  rm script/$file*
done < filter.txt
