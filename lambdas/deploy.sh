#!/bin/bash

if [ -z "$1" ]; then
	echo "Pass in function name directory"
  exit 1
fi

lambda_name=$1

rm ${lambda_name}.zip
cd $lambda_name
zip -X -r ../${lambda_name}.zip *
cd ..

aws lambda update-function-code --function-name ${lambda_name} --zip-file fileb://${lambda_name}.zip
