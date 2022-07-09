#!/bin/bash
set -eo pipefail
./gradlew -q packageLibs
mv build/distributions/covid-data-loader-lambda.zip build/covid-data-loader-lambda.zip
