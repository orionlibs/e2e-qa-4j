#!/usr/bin/env bash
set -e
PROJECT_JAR="target/e2eqa4j.jar"
TARGET_DIR="/opt/e2eqa4j"
WRAPPER="/usr/local/bin/e2eqa4j"

if [ ! -f "$PROJECT_JAR" ]; then
echo "Build first: mvn package -DskipTests"
exit 1
fi

sudo mkdir -p "$TARGET_DIR"
sudo cp "$PROJECT_JAR" "$TARGET_DIR/e2eqa4j.jar"

sudo tee "$WRAPPER" > /dev/null <<'EOF'
#!/usr/bin/env bash
java -jar /opt/e2eqa4j/e2eqa4j.jar "$@"
EOF

sudo chmod +x "$WRAPPER"
echo "Installed E2EQA4J -> $WRAPPER"