grep -vxFf special-list/syntax-error.txt result/babel.txt > __tmp__
grep -vxFf special-list/module.txt __tmp__ > __tmp2__
grep -vxFf special-list/fixture.txt __tmp2__ > result/valid-babel.txt

rm __tmp__
rm __tmp2__
