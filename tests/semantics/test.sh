#!/usr/bin/env bash
# USAGE
#   `./semantics/test.sh`

source "$(git rev-parse --show-toplevel)/tests/source.sh"

build  # calls build.sh

count_all=0   # number of all tests
count_pass=0  # number of passed tests

function run-semantics {
  "$RUNNER" --target=inter "$1"
}

for INPUT_FILE in $(dirname $0)/illegal/*; do
  if run-semantics "$INPUT_FILE" &> /dev/null; then
    red "ILLEGAL FILE '$INPUT_FILE' PASSED"
  else
    green "SUCCESSFULLY FAILED '$INPUT_FILE'"
    count_pass=$(( count_pass + 1 ))
  fi
  count_all=$(( count_all + 1 ))
done

for INPUT_FILE in $(dirname $0)/legal/*; do
  if run-semantics "$INPUT_FILE" &> /dev/null; then
    green "PASSED '$INPUT_FILE'"
    count_pass=$(( count_pass + 1 ))
  else
    red "LEGAL FILE '$INPUT_FILE' FAILED"
  fi
  count_all=$(( count_all + 1 ))
done

echo -e "\nSemantics: PASSED $count_pass / $count_all"
