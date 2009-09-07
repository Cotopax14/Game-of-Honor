#!/bin/bash
#
# 12.03.2008
# SN, sd&m AG
# 
# Creates md5 and sha1 checksums for all files in the given directory.
#

if [ ! $1 ]; then
	echo "Usage: create-chksums.sh {directory}"
	exit 1
fi

for i in `ls $1`; do
	md5sum $1/$i > $1/$i.md5
	echo "Created: $1/$i.md5"
	sha1sum $1/$i > $1/$i.sha1
	echo "Created: $1/$i.sha1"
done

echo "Done."

exit 0
