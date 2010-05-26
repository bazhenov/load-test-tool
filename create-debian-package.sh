#!/bin/sh

TMP=/tmp/load-test-tool-1.0-1

# Prepare build environment
rm -rf $TMP
mkdir -p $TMP/usr/share

# Copying program files
tar -C $TMP/usr/share -xf ./ldt-assembly/target/ldt-assembly-1.0-SNAPSHOT.tar.gz
mv $TMP/usr/share/ldt-assembly-1.0-SNAPSHOT $TMP/usr/share/load-test-tool

# Copying debian manifest
mkdir -p $TMP/DEBIAN
cp debian/* $TMP/DEBIAN

# Copying man pages
mkdir -p $TMP/usr/share/man/man1
gzip -c ldt.1 > $TMP/usr/share/man/man1/ldt.1.gz

# Building package
dpkg-deb -b $TMP
mv $TMP.deb .
