for module in Root Model Database UserInterface Application; do
    (
        cd $module
        exec mvn "$@"
    )
done
