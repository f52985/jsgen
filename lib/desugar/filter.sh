while read file; do
  rm script/$file*
done < filter.txt
