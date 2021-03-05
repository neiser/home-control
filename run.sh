#!/usr/bin/env bash
set -euo pipefail

artifact_name="application-jar"
jar_filename="/tmp/home-control-application.jar"
jar_pid=""

check_for_update_and_download() {
  local artifacts_json
  artifacts_json=$(curl --silent -H "Authorization: token $GITHUB_TOKEN" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/neiser/home-control/actions/artifacts)
  local newest_artifact_json
  newest_artifact_json=$(echo "$artifacts_json" | jq ".artifacts | map(select(.name == \"$artifact_name\")) | sort_by(.updated_at) | .[-1]" || echo "null")
  if [[ "$newest_artifact_json" == "null" ]]; then
    return 0
  fi
  local download_url
  download_url=$(echo "$newest_artifact_json" | jq --raw-output '.archive_download_url')
  local artifact_updated_at
  artifact_updated_at=$(date -d"$(echo "$newest_artifact_json" | jq --raw-output '.updated_at')" +%s)
  local file_updated_at
  file_updated_at=$(stat -c '%Y' "$jar_filename" 2>/dev/null || echo 0)

  if [[ "$file_updated_at" == "$artifact_updated_at" ]]; then
    return 0
  fi
  local downloaded_file="$jar_filename.$file_updated_at"
  local tmpfile
  tmpfile=$(mktemp)
  # shellcheck disable=SC2064
  trap "rm -f \"$tmpfile\"" RETURN
  wget \
    --header='Accept: application/vnd.github.v3+json' \
    --header="Authorization: token $GITHUB_TOKEN" \
    "$download_url" -O "$tmpfile"
  unzip -p "$tmpfile" >"$downloaded_file"
  touch -c -d "@$artifact_updated_at" "$downloaded_file"
  echo "$downloaded_file"
}

kill_jar() {
  if [[ -n "$jar_pid" ]]; then
    echo "Stopping $jar_pid"
    kill "$jar_pid"
    jar_pid=""
    sleep 1
  fi
}
trap kill_jar EXIT

start_jar() {
  if [[ -n "$jar_pid" ]]; then
    if kill -0 "$jar_pid"; then
      return
    fi
  fi
  java -jar $jar_filename &
  jar_pid=$!
  echo "Started $jar_pid"
}

while true; do
  downloaded_file=$(check_for_update_and_download)
  if [[ -n "$downloaded_file" ]]; then
    echo "Updating jar.."
    kill_jar
    mv "$downloaded_file" "$jar_filename"
  fi
  start_jar
  sleep 300
done
