#!/bin/bash -e

if [ "x$WOODSTOCK_SECRET" == "x" ]; then
	echo "export WOODSTOCK_SECRET to decrypt files";
	exit 1;
fi

find  . -name "*.crypt" | while read k; do
	echo "decrypt: $k";
	openssl enc -aes-256-cbc -salt -in "$k" -out "$(echo "$k" | sed "s/\.crypt$//g")" -d -pass "pass:$WOODSTOCK_SECRET" -md md5;
done
