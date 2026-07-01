#!/bin/bash
# one time setup for the vibematch server on a fresh Ubuntu cloud machine.
# run it ON the server (not your laptop) as a user with sudo:
#
#   sudo bash deploy/setup.sh
#
# it installs java, builds the server, and wires it up so it runs 24/7 and
# comes back on its own after a reboot or a crash.
set -e

APP_DIR=/opt/vibematch
PORT=5050

echo "==> installing java"
apt-get update -y
# default-jdk grabs whatever recent OpenJDK the distro ships (works on both
# Debian and Ubuntu); we only need javac + java, any version 17+ is fine
apt-get install -y default-jdk

echo "==> creating the service user"
id vibematch >/dev/null 2>&1 || useradd --system --home "$APP_DIR" --shell /usr/sbin/nologin vibematch

echo "==> placing the app in $APP_DIR"
mkdir -p "$APP_DIR"
# copy everything from wherever this script is being run from
cp -r "$(dirname "$0")/.." "$APP_DIR.tmp"
rm -rf "$APP_DIR"
mv "$APP_DIR.tmp" "$APP_DIR"

echo "==> building"
cd "$APP_DIR"
rm -rf build && mkdir -p build
javac -cp "desktop/lib/*" -d build $(find desktop -name "*.java")

echo "==> setting ownership"
chown -R vibematch:vibematch "$APP_DIR"

echo "==> installing the service"
cp "$APP_DIR/deploy/vibematch.service" /etc/systemd/system/vibematch.service
systemctl daemon-reload
systemctl enable vibematch
systemctl restart vibematch

echo "==> opening the firewall port"
if command -v ufw >/dev/null 2>&1; then
    ufw allow "$PORT"/tcp || true
fi

echo ""
echo "done. server should be listening on port $PORT."
echo "check it with:   systemctl status vibematch"
echo "see the logs:    journalctl -u vibematch -f"
echo ""
echo "IMPORTANT: also open TCP port $PORT in the cloud provider's firewall"
echo "(GCP: VPC network > Firewall > allow tcp:$PORT), or clients cant reach it."
