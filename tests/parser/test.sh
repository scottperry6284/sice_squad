#!/usr/bin/env bash
# USAGE
#   `./parser/test.sh`

source "$(git rev-parse --show-toplevel)/tests/source.sh"

count_all=0
count_pass=0

build

function run_parser {
  "$RUNNER" "$1" --target=parse &> /dev/null
}

# run all files
for INPUT_FILE in "$(dirname $0)"/illegal/*; do
  count_all=$(( count_all + 1 ))
  if run_parser "$INPUT_FILE"; then
    red "FAILED TO FAIL $INPUT_FILE"
  else
    green "SUCCESSFULLY FAILED $INPUT_FILE"
    count_pass=$(( count_pass + 1 ))
  fi
done

for INPUT_FILE in "$(dirname $0)"/legal/*; do
  count_all=$(( count_all + 1 ))
  if run_parser "$INPUT_FILE"; then
    green "PASSED $INPUT_FILE"
    count_pass=$(( count_pass + 1 ))
  else
    red "FAILED $INPUT_FILE"
  fi
done

echo -e "\nPASSED $count_pass / $count_all"
