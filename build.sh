NAME=square-android-api
VERSION=0.9

NAME=$NAME-$VERSION

# Clear and recreate build directory.
rm -r build
mkdir -p build/classes
mkdir -p build/dist

# Compile classes.
javac -g -classpath external/android.jar -d build/classes \
    -target 5 `find src -name *.java`

# Generate Javadocs.
TITLE="Square for Android API v${VERSION}"

FOOTER="<font size='-1'>Copyright (C) 2010 <a href='https://squareup.com/'>\
Square, Inc.</a> \
Licensed under the <a href='http://www.apache.org/licenses/LICENSE-2.0'>Apache \
License</a>, Version 2.0.</font>"

javadoc -protected -bottom "$FOOTER" \
	-header "$TITLE" \
    	-doctitle "$TITLE" \
	-classpath external/android.jar \
        -sourcepath src -d build/javadoc com.squareup.android

# Generate jars.

jar cfM build/dist/$NAME-src.zip -C src .

jar cfM build/dist/$NAME-javadoc.zip -C build/javadoc .
jar cfM build/dist/$NAME.jar -C build/classes .

jar cfM build/$NAME.zip -C build/dist .

rm examples/twocents/libs/*
cp build/dist/$NAME.jar examples/twocents/libs/
