#!/usr/bin/env bash
# USAGE:
#   execute this script without any args

source "$(git rev-parse --show-toplevel)/tests/source.sh"

function finish {
  rm -rf "$TMPDIR"
}
trap finish INT TERM HUP EXIT

# &1 -> all input decaf files that run without an error
function get-input-files-no-error {
  find "$ROOT/tests/codegen/input/" -type f -name "*.dcf"
}

# &1 -> all input decaf files that are expected to throw an error
function get-input-files-error {
  find "$ROOT/tests/codegen/error/" -type f -name "*.dcf"
}

# $1 -> decaf file to compile
# $2 -> assembly file to create
function dcf-to-asm {
  "$RUNNER" --target=assembly "$1" -o "$2"
}

# $1 -> assembly file to assemble
# $2 -> executable file to create
function asm-to-exec {
  gcc -no-pie "$1" -o "$2"
}

# $1 -> path
# &2 -> shorter versino of path
function clean {
  sed -E 's/^.+codegen\///' <<< "$1"
}

# $1 -> dcf input file
# &2 -> error messages related to exit code of 3
# $? -> 0 if test passed,
#       1 if the executable threw a runtime error
#       2 if output did not match
#       3 otherwise
function test-runner {
  declare -r DCF_FILE="$1"
  declare -r EXPECTED_OUTPUT_FILE="$(dirname $(dirname $DCF_FILE))/output/$(basename $DCF_FILE).out"

  # temporary files to hold intermediate results
  declare -r TEMP_ASM="$TMPDIR/$(basename $DCF_FILE).s"
  declare -r TEMP_BIN="$TMPDIR/$(basename $DCF_FILE).bin"
  declare -r TEMP_OUT="$TMPDIR/$(basename $DCF_FILE).out"

  # compile to asm
  if dcf-to-asm "$DCF_FILE" "$TEMP_ASM" &> /dev/null; then
    # compile to executable
    if asm-to-exec "$TEMP_ASM" "$TEMP_BIN" &> /dev/null; then

      # execute binary, save output and exit code
      "$TEMP_BIN" > "$TEMP_OUT" 2> /dev/null
      declare -r CODE=$?

      if [[ $CODE -eq 0 ]]; then
        if diff "$EXPECTED_OUTPUT_FILE" "$TEMP_OUT" &> /dev/null; then
          return 0  # everything is well
        else
          return 2  # output mismatch probably
        fi
      else
        return 1  # executable threw a runtime error
      fi

    else
      red "assembly of '$(clean $DCF_FILE)' could not be assembled"
    fi
  else
    red "failed to compile '$(clean $DCF_FILE)' to assembly"
  fi
  return 3  # compiler or assembler failed
}

# $1 -> dcf input file
# &1 -> 'TESTCASE-PASS' if test passed, nothing otherwise
function test-should-pass {
  declare -r DCF_FILE="$1"

  test-runner "$DCF_FILE"
  declare -r CODE=$?

  case $CODE in
    0) green "passed -- '$(clean $DCF_FILE)'";
       echo 'TESTCASE-PASS';;
    1) red "threw a runtime error -- '$(clean $DCF_FILE)'";;
    2) red "output doesn't match -- '$(clean $DCF_FILE)";;
    3) red "your compiler threw an error -- '$(clean $DCF_FILE)'";;
  esac
}

# $1 -> dcf input file
# &1 -> 'TESTCASE-PASS' if a runtime error is thrown, nothing otherwise
function test-should-fail {
  declare -r DCF_FILE="$1"

  test-runner "$DCF_FILE"
  declare -r CODE=$?

  case $CODE in
    0) red "failed to throw a runtime error -- '$(clean $DCF_FILE)'";;
    3) red "your compiler threw an error -- '$(clean $DCF_FILE)'";;
    *) green "successfully threw a runtime error -- '$(clean $DCF_FILE)'";
       echo 'TESTCASE-PASS';;
  esac
}

function par {
  "$ROOT/parallel" --eta --max-procs 4 $@
}

# directory to hold all temporary values
declare -r TMPDIR="$ROOT/.dcf-tmp/"

# functions and globals to use in functions
export TMPDIR
export -f dcf-to-asm asm-to-exec test-runner test-should-pass test-should-fail clean

# download gnu parallel
if ! [[ -f "$ROOT/parallel" ]]; then
  wget -O - 'http://git.savannah.gnu.org/cgit/parallel.git/plain/src/parallel' > "$ROOT/parallel"
  chmod +x "$ROOT/parallel"
fi
"$ROOT/parallel" --version

# create fresh for every run
mkdir -p "$TMPDIR"

build  # calls build.sh

# number of tests that shouldn't throw an error and passed
# kind of making a naive assumption that --max-procs 4 will not cause OOM
declare -r COUNT_PASS_NO_ERROR=$(
  get-input-files-no-error |
    par 'test-should-pass {}' |
    grep 'TESTCASE-PASS' |
    wc -l
)
# number of tests that are supposed to throw an error and passed
declare -r COUNT_PASS_ERROR=$(
  get-input-files-error |
    par 'test-should-fail {}' |
    grep 'TESTCASE-PASS' |
    wc -l
)

# number of all tests
declare -r COUNT_ALL=$(
 { get-input-files-no-error; get-input-files-error; } | wc -l
)

green "\nPASSED $(( COUNT_PASS_NO_ERROR + COUNT_PASS_ERROR )) / $COUNT_ALL"
