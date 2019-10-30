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
  find "$ROOT/tests/dataflow/input/" -type f -name "*.dcf"
}

function clean {
  sed -E 's/^.+dataflow\///' <<< "$1"
}

# benchmark with hyperfine
function fine {
  "$ROOT/tests/hyperfine" --warmup 4 "$@"
}

# THIS FUNCTION ASSUMES THAT EVERYTHING WORKS
# $1 -> dcf input file
# &1,&2 -> everything from hyperfine
function bench {
  declare -r DCF_FILE="$1"

  # unoptimized binary and optimized binary
  declare -r TEMP_BIN="$TMPDIR/$(basename "$DCF_FILE").exec"
  declare -r TEMP_OPT_BIN="$TMPDIR/$(basename "$DCF_FILE").opt.exec"

  # compile unoptimized
  "$RUNNER" --target=assembly "$DCF_FILE" 2> /dev/null |
    gcc -no-pie -O0 -x assembler - -o "$TEMP_BIN"

  # compile optimized
  "$RUNNER" --target=assembly --opt=all "$DCF_FILE" 2> /dev/null |
    gcc -no-pie -O0 -x assembler - -o "$TEMP_OPT_BIN"

  green "\n-------------------------------- $(clean "$DCF_FILE") --------------------------------"

  red "\nUNOPTIMIZED benchmark"
  fine "$TEMP_BIN"

  green "\nOPTIMIZED benchmark"
  fine "$TEMP_OPT_BIN"
}

# directory to hold all temporary values
declare -r TMPDIR="$ROOT/.dcf-tmp"

export TMPDIR

# create fresh for every run
mkdir -p "$TMPDIR"

build  # calls build.sh

for INPUT_FILE in "$@"; do
  bench "$INPUT_FILE"
done
