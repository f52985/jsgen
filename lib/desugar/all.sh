# TODO: install submodules

# install babel
npm install
./patch.sh # change babel so that skip on compile failure

# run test262
echo "running test 262.. (~20 min.)"
./run.sh > result/test262.txt

# compile test262
echo "compiling test 262 with babel.. (~1 min.)"
./compile.sh > result/babel.txt

# run compiled test262
echo "running compield test 262.. (~?? min.)"
./run-compiled.sh > result/compiled-test262.txt
