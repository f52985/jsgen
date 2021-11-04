files=(`ls script/*.vec`)
  
echo script origResult compResult origLen compLen origTime compTime

for vec in ${files[@]}; do
  script=${vec%.vec}

  origResult=`grep $script result/jest.txt | cut -d " " -f 1`
  compResult=`grep $script result/compiled-jest.txt | cut -d " " -f 1`
  
  origLen=`wc -c < min-$script 2> /dev/null`
  if [ -z $origLen ]; then
    origLen=N/A
  fi
  
  compLen=`wc -c < min-compiled-$script 2> /dev/null`
  if [ -z $compLen ]; then
    compLen=N/A
  fi
  
  origTime=`grep "^$script " result/speed.txt | cut -d " " -f 2`
  if [ -z $origTime ]; then
    origTime=N/A
  fi
  
  compTime=`grep "^compiled-$script " result/speed.txt | cut -d " " -f 2`
  if [ -z $compTime ]; then
    compTime=N/A
  fi

  echo $script $origResult $compResult $origLen $compLen $origTime $compTime
done
