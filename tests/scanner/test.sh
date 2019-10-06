#!/usr/bin/env bash
# USAGE
#   `./scanner/test.sh`

source "$(git rev-parse --show-toplevel)/tests/source.sh"

# cleanup ritual
function finish {
  rm -f "$OUTPUT_FILE"
}
trap finish EXIT

# temp file that will hold outputs
# will be auto deleted when this script finishes running
declare -r OUTPUT_FILE="$(mktemp)"
export OUTPUT_FILE

count_all=0   # number of all tests
count_pass=0  # number of passed tests

build  # calls build.sh

# run all files
for INPUT_FILE in "$(dirname $0)"/input/*; do
  count_all=$(( count_all + 1 ))

  # run the scanner and save output to $OUTPUT_FILE
  # remove `2> /dev/null` if you want to see the debug output
  "$RUNNER" "$INPUT_FILE" --target=scan > "$OUTPUT_FILE" 2> /dev/null
  EXIT_CODE=$?  # exit code of the above command

  # name of the file containing the expected output
  EXPECTED="$(dirname $0)/output/$(remove-ext $(basename $INPUT_FILE)).out"

  if grep -Pq 'line \d+:\d+: (unexpected|expecting) .+' < "$EXPECTED"; then
    # we are expecting a failure
    if [[ $EXIT_CODE -eq 0 ]]; then
      red "FAILED TO FAIL $INPUT_FILE"
    else
      green "SUCCESSFULLY FAILED $INPUT_FILE"
      count_pass=$(( count_pass + 1 ))
    fi
  elif ! diff "$EXPECTED" "$OUTPUT_FILE"; then
    red "FAILED $INPUT_FILE"
  else
    green "PASSED $INPUT_FILE"
    count_pass=$(( count_pass + 1 ))
  fi
done

echo -e "\nPASSED $count_pass / $count_all"
