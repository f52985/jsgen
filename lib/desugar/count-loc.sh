sum=0
cnt=0

echo dir original compiled diff

for f in `find compiled-script -name "*.js"`; do
  orig=${f:9}
  orig_cnt=`cat $orig | sed '/^\s*$/d' | wc -l`
  comp_cnt=`cat $f | sed '/^\s*$/d' | wc -l`

  (( diff = comp_cnt - orig_cnt ))
  (( sum += diff ))
  (( cnt += 1 ))
  echo $orig $orig_cnt $comp_cnt $diff
done

(( avg = sum / cnt ))

echo "sum: $sum"
echo "avg: $avg"
