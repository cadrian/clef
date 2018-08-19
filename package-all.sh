#/usr/bin/env bash

rm -rf pkg
mkdir pkg
cd pkg

function buildModule() {
    local module=$1

    (
        echo
        echo Building $module
        mkdir -p build/$module
        cp -al ../$module build/$module/src
        cd build/$module/src

        case $module in
            Application)
                # Special treatment for Application: need to fill the right classpath in the main script
                (
                    mvn dependency:build-classpath -Dmdep.outputFile=src/scripts/cp.txt > ../getdep.log
                    cd src/scripts
                    mv clef clef.orig
                    sed "s!%CLASSPATH%!$(<cp.txt)!" clef.orig > clef
                )
                ;;
            *)
                :
                ;;
        esac

        debuild -b -us -uc > ../build.log || exit 1
        cd ..
        sudo dpkg -i *.deb || exit 1
    ) || exit 1
}

if [ $# -gt 0 ]; then
    for module in "$@"; do
        buildModule $module
    done
else
    for module in Root Model Database UserInterface Application; do
        buildModule $module
    done
fi

echo

ln build/*/*.deb .

echo Done.
