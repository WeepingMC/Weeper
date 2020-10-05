#!/usr/bin/env bash

(
set -e
basedir="$(cd "$1" && pwd -P)"
FORK_NAME=$2
DOWN_FORK_NAME="$(echo "${FORK_NAME}" | tr '[:upper:]' '[:lower:]')"
workdir="$basedir/Paper/work"
mcver=$(cat "$workdir/BuildData/info.json" | grep minecraftVersion | cut -d '"' -f 4)
paperjar="$basedir/${FORK_NAME}-Server/target/${DOWN_FORK_NAME}-server-$mcver.jar"
vanillajar="$workdir/Minecraft/$mcver/$mcver.jar"
echo "$paperjar"

clipName="$(echo "${FORK_NAME}" | tr '[:upper:]' '[:lower:]')clip.jar"

#sed -i '/.*clip.jar$/d' .gitignore
printf '%s' "$clipName" >> .gitignore
(
    cd "$workdir/Paperclip"
    mvn clean package "-Dmcver=$mcver" "-Dpaperjar=$paperjar" "-Dvanillajar=$vanillajar"
)
#cp "$workdir/Paperclip/assembly/target/paperclip-${mcver}.jar" "$basedir/$clipName"

echo ""
echo ""
echo ""
echo "Build success!"
echo "Copied final jar to $basedir/$clipName"
) || exit 1
