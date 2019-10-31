#!/usr/bin/env bash
# USAGE:
#   `./test.sh` to run tests sequentially
#   `./test.sh --parallel` to run tests in parallel

source "$(git rev-parse --show-toplevel)/tests/source.sh"

if ! gcc -v 2>&1 | grep -q '^Target: x86_64-linux-gnu'; then
  red 'ERROR: architecture is not x86_64-linux-gnu'
  exit 1
fi

# remove temps created during this run
function finish {
  rm -rf "$TMPDIR"
}
trap finish INT TERM HUP EXIT

# &1 -> all input decaf files that should run without an error
function get-input-files-should-pass {
  find "$ROOT/tests/codegen/input/" -type f -name "*.dcf"
}

# &1 -> all input decaf files that are expected to throw an error
function get-input-files-should-fail {
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
  gcc -no-pie -O0 "$1" -o "$2"
}

# $1 -> path
# &1 -> basename along with the immediate parent directory name
function clean {
  sed -E 's/^.+codegen\///' <<< "$1"
}

# run the downloaded parallel executable
function par {
  "$ROOT/parallel" --eta --max-procs 8 $@
}

# $1 -> dcf input file
# &2 -> error messages related to exit code of 3
# $? -> 0 if test passed,
#       1 if the executable threw a runtime error
#       2 if output did not match
#       3 otherwise
function test-runner {
  declare -r DCF_FILE="$1"
  declare -r EXPECTED_OUTPUT_FILE="$(dirname $(dirname "$DCF_FILE"))/output/$(basename "$DCF_FILE").out"

  #  declare -r TEMP_ASM="$TMPDIR/$(basename "$DCF_FILE")/main.s"
  declare -r TEMP_ASM="$TMPDIR/$(basename "$DCF_FILE").s"
  declare -r TEMP_BIN="$TMPDIR/$(basename "$DCF_FILE").exec"
  declare -r TEMP_OUT="$TMPDIR/$(basename "$DCF_FILE").out"

  if dcf-to-asm "$DCF_FILE" "$TEMP_ASM" &> /dev/null; then     # dcf -> asm
    if asm-to-exec "$TEMP_ASM" "$TEMP_BIN" &> /dev/null; then  # asm -> bin
      if "$TEMP_BIN" > "$TEMP_OUT" 2> /dev/null; then          # ./bin
        diff "$EXPECTED_OUTPUT_FILE" "$TEMP_OUT" &> /dev/null && 
          return 0 ||  # everything is well
          return 2     # output mismatch
      else
        return 1  # executable threw a runtime error
      fi

    else
      red "assembly of '$(clean "$DCF_FILE")' could not be assembled"
    fi

  else
    red "failed to compile '$(clean "$DCF_FILE")' to assembly"
  fi
  return 3  # compiler or assembler failed
}

# $1 -> dcf input file
# &1 -> 'TESTCASE-PASS' if test passed, nothing otherwise
function test-should-pass {
  declare -r DCF_FILE="$1"
  declare -r CLEANED="$(clean "$DCF_FILE")"

  test-runner "$DCF_FILE"
  declare -r CODE=$?

  case $CODE in
    0) green "passed -- '$CLEANED'"                ;
       echo 'TESTCASE-PASS'                        ;;
    1) red "threw a runtime error -- '$CLEANED'"   ;;
    2) red "output doesn't match -- '$CLEANED'"    ;;
    3) red "compiler threw an error -- '$CLEANED'" ;;
    *) red "unexpected exit code -- '$CLEANED'"    ;;
  esac
}

# $1 -> dcf input file
# &1 -> 'TESTCASE-PASS' if a runtime error is thrown, nothing otherwise
function test-should-fail {
  declare -r DCF_FILE="$1"
  declare -r CLEANED="$(clean "$DCF_FILE")"

  test-runner "$DCF_FILE"
  declare -r CODE=$?

  case $CODE in
    1) green "successfully threw a runtime error -- '$CLEANED'" ;
       echo 'TESTCASE-PASS'                                     ;;
    3) red "your compiler threw an error -- '$CLEANED'"         ;;
    *) red "failed to throw a runtime error -- '$CLEANED'"      ;;
  esac
}

# directory to hold all temporary values
declare -r TMPDIR="$ROOT/.dcf-tmp"
# 0 if we should run parallel 1 if sequential
declare -r RUN_PARALLEL=$( grep -Pq '^--parallel$' <<< "$1" && echo 0 || echo 1 )

# functions and globals to use in functions
export TMPDIR
export -f dcf-to-asm asm-to-exec test-runner test-should-pass test-should-fail clean

if [[ "$RUN_PARALLEL" -eq 0 ]]; then
  # download gnu parallel
  if ! [[ -f "$ROOT/parallel" ]]; then
    wget -O - 'http://git.savannah.gnu.org/cgit/parallel.git/plain/src/parallel' > "$ROOT/parallel"
    chmod +x "$ROOT/parallel"
  fi
  "$ROOT/parallel" --version
fi

# create fresh for every run
mkdir -p "$TMPDIR"

build  # calls build.sh

# number of tests that shouldn't throw an error and passed
declare -r COUNT_PASS_NO_ERROR=$(
  get-input-files-should-pass |
    if [[ "$RUN_PARALLEL" -eq 0 ]]; then
      par 'test-should-pass {}' <&0
    else 
      while read INPUT_FILE; do
        test-should-pass "$INPUT_FILE"
      done <&0
    fi |
    grep -c 'TESTCASE-PASS'
)
# number of tests that are supposed to throw an error and passed
declare -r COUNT_PASS_ERROR=$(
  get-input-files-should-fail |
    if [[ "$RUN_PARALLEL" -eq 0 ]]; then
      par 'test-should-fail {}' <&0
    else 
      while read INPUT_FILE; do
        test-should-fail "$INPUT_FILE"
      done <&0
    fi |
    grep -c 'TESTCASE-PASS'
)

# number of all tests
declare -r COUNT_ALL=$(
 { get-input-files-should-pass; get-input-files-should-fail; } | wc -l
)

green "\nCodeGen: PASSED $(( COUNT_PASS_NO_ERROR + COUNT_PASS_ERROR )) / $COUNT_ALL"

