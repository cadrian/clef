#/usr/bin/env bash

rm -rf pkg
mkdir -p pkg/build pkg/debs
cd pkg

function prepareModule() {
    local module=$1

    (
        echo
        echo Preparing $module
        cp -al ../$module build/$module
    )
}

function buildModule() {
    local module=$1

    (
        echo
        echo Building $module

        cd build/$module

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

        debuild -b -us -uc > build.log || exit 1
        cd ..
        sudo dpkg -i *.deb || exit 1
    ) || exit 1

    mv build/*.deb debs/
}

if [ $# -gt 0 ]; then
    for module in "$@"; do
        prepareModule $module
    done
    for module in "$@"; do
        buildModule $module
    done
else
    for module in Root Model Database UserInterface Application; do
        prepareModule $module
    done
    for module in Root Model Database UserInterface Application; do
        buildModule $module
    done
fi

echo
echo Done.
