    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
workdir="$basedir"/Paper/work
minecraftversion=$(cat "$basedir"/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
nms="net/minecraft"
	if [ -f "$basedir/Paper/Paper-Server/src/main/java/$nms/$1.java" ]; then
		mkdir -p "$(dirname "$target")"
function importLibrary {
    group=$1
    lib=$2
    prefix=$3
    shift 3
    for file in "$@"; do
        file="$prefix/$file"
        target="$basedir/Paper/Paper-Server/src/main/java/$file"
        targetdir=$(dirname "$target")
        mkdir -p "${targetdir}"
        base="$workdir/Minecraft/$minecraftversion/libraries/${group}/${lib}/$file"
        if [ ! -f "$base" ]; then
            echo "Missing $base"
            exit 1
        fi
        export MODLOG="$MODLOG  Imported $file from $lib\n";
        sed 's/\r$//' "$base" > "$target" || exit 1
    done
}

files=$(cat patches/server/* | grep "+++ b/src/main/java/net/minecraft/" | sort | uniq | sed 's/\+\+\+ b\/src\/main\/java\/net\/minecraft\///g')
nonnms=$(cat patches/server/* | grep -R "new file mode" | grep -v "new file mode" | grep -oE --color=none "net\/minecraft\/.*.java" | sed 's/.*\/net\/minecraft\///g')
    containsElement "$f" "${nonnms[@]}"
    if [ "$?" == "1" ]; then
        if [ ! -f "$basedir/Paper/Paper-Server/src/main/java/net/minecraft/$f" ]; then
            f="$(echo "$f" | sed 's/.java//g')"
            if [ ! -f "$decompiledir/$nms/$f.java" ]; then
                echo "$(bashColor 1 31) ERROR!!! Missing NMS$(bashColor 1 34) $f $(bashColorReset)";
                error=true
            else
                import $f
            fi
        fi
    fi
if [ -n "$error" ]; then
  exit 1
fi
########################################################
########################################################
########################################################
#              LIBRARY IMPORTS
# These must always be mapped manually, no automatic stuff
#
#             # group    # lib          # prefix               # many files

#importLibrary com.mojang datafixerupper com/mojang/datafixers/util Either.java
importLibrary com.mojang brigadier  com/mojang/brigadier CommandDispatcher.java
importLibrary com.mojang brigadier  com/mojang/brigadier/tree LiteralCommandNode.java
importLibrary com.mojang brigadier  com/mojang/brigadier/suggestion SuggestionsBuilder.java
importLibrary com.mojang brigadier  com/mojang/brigadier/arguments BoolArgumentType.java