#!/bin/bash

VERSION=$1

if [ -z "$VERSION" ]; then
  echo "Użycie: ./release.sh v1.0.0"
  exit 1
fi

git add .
git commit -m "🔖 Release $VERSION"
git push origin main

git tag $VERSION
git push origin $VERSION

echo "✅ Wydano $VERSION i wypchnięto na GitHub!"