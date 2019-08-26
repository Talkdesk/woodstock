#!/bin/bash -e

if [ "x$WOODSTOCK_SECRET" == "x" ]; then
	echo "export WOODSTOCK_SECRET to encrypt files";
	exit 1;
fi

openssl enc -aes-256-cbc -salt -in "$1" -out "$1.crypt" -pass "pass:$WOODSTOCK_SECRET" -md md5;
