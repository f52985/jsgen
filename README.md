# JSGen: JavaScript Program Generator

**JSGen** is a **J**ava**S**cript Program **Gen**erator. It generalizes program
generation techniques used in another tool **JEST** which is a _N+1-version
Differential Testing_ of both JavaScript engines and specifications.

## Installation Guide

We explain how to install JSGen with necessary environment settings from the
scratch.  Before installation, please download JDK 8 and
[`sbt`](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html).

### Download JSGen
```bash
$ git clone https://github.com/f52985/jsgen.git
```

### Environment Setting
Insert the following commands to `~/.bashrc` (or `~/.zshrc`):
```bash
# for JSGen
export JSGEN_HOME="<path to JSGen>" # IMPORTANT!!!
export PATH="$JSGEN_HOME/bin:$PATH" # for executables `jsgen` and etc.
source $JSGEN_HOME/jsgen-auto-completion # for auto completion
```
The `<path to JSGen>` should be the absolute path of JSGen repository.

### Installation of JSGen using `sbt`
```bash
$ cd jsgen && git submodule init && git submodule update && sbt assembly
```

## Simple Examples
Extract and generate a model from ECMAScript 2021 (ES12 / es2021):
```bash
$ jsgen gen-model -extract:version=es2021
# ========================================
#  extract phase
# ----------------------------------------
# version: es2021
# parsing spec.html... (10,476 ms)
# ========================================
#  gen-model phase
# ----------------------------------------
# generating models... (240 ms)
```
Create the following JavaScript file `sample.js`:
```js
// sample.js
var x = 1 + 2;
print(x);
```
Parse `sample.js`:
```bash
$ jsgen parse sample.js
# ========================================
#  parse phase
# ----------------------------------------
# var x = 1 + 2 ; print ( x ) ;
```
Evaluate `sample.js`:
```bash
$ jsgen eval sample.js -silent
# 3.0
```
Show detail path and final results during evaluation of `sample.js`:
```bash
$ jsgen eval sample.js -debug
```

## Basic Commands

You can run the artifact with the following command:
```bash
$ jsgen <sub-command> <option>*
```
with the following sub-commands:
- `help` shows the help message.
- `extract` extracts ECMAScript model from `ecma262/spec.html`.
- `gen-model` generates ECMAScript models.
- `compile-repl` performs REPL for printing compile result of particular step.
- `gen-test` generates tests with the current implementation as the oracle.
- `parse` parses a JavaScript file using the generated parser.
- `load` loads a JavaScript AST to the initial IR states.
- `eval` evaluates a JavaScript file using generated interpreter.
- `filter-meta` extracts and filters out metadata of test262 tests.
- `parse-ir` parses an IR file.
- `load-ir` loads an IR AST to the initial IR states.
- `eval-ir` evaluates an IR file.
- `repl-ir` performs REPL for IR instructions.
- `build-cfg` builds control flow graph (CFG).

and global options:
- `-silent` does not show final results.
- `-debug` turns on the debug mode.
- `-interactive` turns on the interactive mode.
- `-no-bugfix` uses semantics including specification bugs.
- `-time` displays the duration time.
