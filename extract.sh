#!/usr/bin/env sh

set -e

WEBM_DIR="webm"
WEBP_DIR="webp"
PNG_DIR="png"

mkdir -p "$WEBP_DIR" "$PNG_DIR"

for f in "$WEBM_DIR"/*.webm; do
  name="$(basename "$f" .webm)"

  echo "Processing $name.webm"

  # webm -> animated webp
  ffmpeg -y -i "$f" \
    -vf "fps=15,scale=512:-1:flags=lanczos" \
    -c:v libwebp \
    -loop 0 \
    -lossless 0 \
    -quality 70 \
    -compression_level 6 \
    "$WEBP_DIR/$name.webp"

  # verify animated webp
  if ffprobe "$WEBP_DIR/$name.webp" 2>/dev/null | grep -q "Animation: yes"; then
    echo "Animated WebP verified"
  else
    echo "Warning: WebP is not animated"
  fi

  # extract first frame as png
  ffmpeg -y -i "$f" \
    -vf "select=eq(n\,0),scale=512:-1" \
    -vframes 1 \
    "$PNG_DIR/$name.png"

done

echo "All conversions completed"
