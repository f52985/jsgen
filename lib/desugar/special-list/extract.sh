grep "type: SyntaxError" -r test262/test -l > special-list/syntax-error.txt
grep "flags: \[.*module.*\]" -r test262/test -l > special-list/module.txt
find test262/test -name "*_FIXTURE.js" > special-list/fixture.txt
