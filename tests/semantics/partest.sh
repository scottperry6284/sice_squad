#!/usr/bin/env bash
# run tests in parallel!
#
# USAGE
#   `./semantics/partest.sh`

source "$(git rev-parse --show-toplevel)/tests/source.sh"

function run-semantics {
  "$RUNNER" --target=inter "$1"
}

function test-illegal {
  declare -r INPUT_FILE="$1"
  if run-semantics "$INPUT_FILE" &> /dev/null; then
    red "ILLEGAL FILE '$INPUT_FILE' PASSED"
  else
    green "PASSED '$INPUT_FILE'"
    echo 'TESTPASS'
  fi
}

function test-legal {
  declare -r INPUT_FILE="$1"
  if run-semantics "$INPUT_FILE" &> /dev/null; then
    green "PASSED '$INPUT_FILE'"
    echo 'TESTPASS'
  else
    red "LEGAL FILE '$INPUT_FILE' FAILED"
  fi
}

export -f run-semantics test-legal test-illegal

build  # calls build.sh

# download gnu parallel
if ! [[ -f "$ROOT/parallel" ]]; then
  wget -O - 'http://git.savannah.gnu.org/cgit/parallel.git/plain/src/parallel' > "$ROOT/parallel"
  chmod +x "$ROOT/parallel"
fi

# number of legal tests that passed, recall that this does not filter stderr
declare -r LEGAL_PASS=$(
  find "$(dirname $0)/legal/" -type f |
    "$ROOT/parallel" 'test-legal {}' |
    grep 'TESTPASS' |
    wc -l
)
# same but illegal tests
declare -r ILLEGAL_PASS=$(
  find "$(dirname $0)/illegal/" -type f |
    "$ROOT/parallel" 'test-illegal {}' |
    grep 'TESTPASS' |
    wc -l
)

declare -r COUNT_PASS=$(( LEGAL_PASS + ILLEGAL_PASS ))
declare -r COUNT_ALL=$(find "$ROOT/tests/semantics" -type f -name '*.dcf' | wc -l)
echo -e "\nPASSED $COUNT_PASS / $COUNT_ALL"
