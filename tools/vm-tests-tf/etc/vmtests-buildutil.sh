#!/bin/bash
#
# Copyright (C) 2018 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Execute the vmtests-buildutil program
prog="$0"
while [ -h "${prog}" ]; do
    newProg=`/bin/ls -ld "${prog}"`
    newProg=`expr "${newProg}" : ".* -> \(.*\)$"`
    if expr "x${newProg}" : 'x/' >/dev/null; then
        prog="${newProg}"
    else
        progdir=`dirname "${prog}"`
        prog="${progdir}/${newProg}"
    fi
done
oldwd=`pwd`
progdir=`dirname "${prog}"`
cd "${progdir}"
progdir=`pwd`
prog="${progdir}"/`basename "${prog}"`
cd "${oldwd}"

jarfile=vmtests-buildutil.jar
libdir="$progdir"

if [ ! -r "$libdir/$jarfile" ]; then
    # set vmtests-buildutil.jar location for the Android tree case
    libdir=`dirname "$progdir"`/framework
fi

if [ ! -r "$libdir/$jarfile" ]; then
    echo `basename "$prog"`": can't find $jarfile"
    exit 1
fi

# By default, give vmtests-buildutil a max heap size of 1 gig. This can be overridden
# by using a "-J" option (see below).
defaultMx="-Xmx1024M"

# The following will extract any initial parameters of the form "-J<stuff>" from
# the command line and pass them to the Java invocation (instead of to vmtests-buildutil).
# This makes it possible for you to add a command-line parameter such as
# "-JXmx256M" in your scripts, for example. "java" (with no args) and "java -X"
# give a summary of available options.

javaOpts=""

while expr "x$1" : 'x-J' >/dev/null; do
    opt=`expr "x$1" : 'x-J\(.*\)'`
    javaOpts="${javaOpts} -${opt}"
    if expr "x${opt}" : "xXmx[0-9]" >/dev/null; then
        defaultMx="no"
    fi
    shift
done

if [ "${defaultMx}" != "no" ]; then
    javaOpts="${javaOpts} ${defaultMx}"
fi

if [ "$OSTYPE" = "cygwin" ]; then
    # For Cygwin, convert the jarfile path into native Windows style.
    jarpath=`cygpath -w "$libdir/$jarfile"`
else
    jarpath="$libdir/$jarfile"
fi

exec java $javaOpts -cp "$jarpath" "$@"
