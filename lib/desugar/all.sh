set -e

# TODO: install submodules

# install babel
npm install
./patch.sh # change babel so that skip on compile failure

# filter runnable tests
echo "filtering.. (~4 min.)"
./filter.sh

# run test262
echo "running test 262.. (~20 min.)"
./run.sh > result/test262.txt

# compile test262
echo "compiling test 262 with babel.. (~3 min.)"
./compile.sh > result/babel.txt

# run compiled test262
echo "running compiled test 262.. (~20 min.)"
./run-compiled.sh > result/compiled-test262.txt
