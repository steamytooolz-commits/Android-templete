#!/usr/bin/env bash
# ==============================================================================
# Android Release Upload Keystore Generator
# Generates a standard PKCS12 / JKS upload keystore and converts it to Base64
# for GitHub Actions Repository Secrets.
# ==============================================================================

set -euo pipefail

KEYSTORE_NAME="my-upload-key.jks"
KEY_ALIAS="upload"
VALIDITY_DAYS=10000

echo "--------------------------------------------------------"
echo " Android Release Keystore Generator for GitHub Actions  "
echo "--------------------------------------------------------"

if [ -f "$KEYSTORE_NAME" ]; then
  read -rp "Warning: '$KEYSTORE_NAME' already exists. Overwrite? (y/N): " CONFIRM
  if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
  fi
  rm -f "$KEYSTORE_NAME"
fi

# Generate strong password or prompt user
RANDOM_PASS=$(openssl rand -base64 16 | tr -dc 'a-zA-Z0-9' | head -c 16)
read -rp "Enter keystore password (press enter to use generated: $RANDOM_PASS): " USER_PASS
PASS="${USER_PASS:-$RANDOM_PASS}"

read -rp "Enter Key Alias (default: $KEY_ALIAS): " USER_ALIAS
ALIAS="${USER_ALIAS:-$KEY_ALIAS}"

read -rp "Enter Common Name (CN, default: Android Developer): " USER_CN
CN="${USER_CN:-Android Developer}"

read -rp "Enter Organization (O, default: AI Studio): " USER_O
ORG="${USER_O:-AI Studio}"

echo ""
echo "Generating keystore with keytool..."
keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE_NAME" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity "$VALIDITY_DAYS" \
  -storepass "$PASS" \
  -keypass "$PASS" \
  -dname "CN=$CN, OU=Mobile, O=$ORG, L=Mountain View, ST=CA, C=US"

# Encode to Base64
if command -v base64 >/dev/null 2>&1; then
  # Support Linux (GNU base64 -w 0) and macOS (base64)
  B64_CONTENT=$(base64 -w 0 "$KEYSTORE_NAME" 2>/dev/null || base64 "$KEYSTORE_NAME" | tr -d '\n')
  echo "$B64_CONTENT" > "${KEYSTORE_NAME}.base64.txt"
fi

echo ""
echo "========================================================"
echo " ✅ Keystore generated successfully!"
echo " Keystore file: $KEYSTORE_NAME"
echo " Base64 file:   ${KEYSTORE_NAME}.base64.txt"
echo " Alias:         $ALIAS"
echo " Password:      $PASS"
echo "========================================================"
echo ""
echo "👉 Add these to GitHub (Repository Settings -> Secrets and variables -> Actions):"
echo ""
echo "Secret Name: KEYSTORE_BASE64"
echo "Value:       (Copy content of ${KEYSTORE_NAME}.base64.txt)"
echo ""
echo "Secret Name: STORE_PASSWORD"
echo "Value:       $PASS"
echo ""
echo "Secret Name: KEY_PASSWORD"
echo "Value:       $PASS"
echo ""
echo "Secret Name: KEY_ALIAS"
echo "Value:       $ALIAS"
echo "========================================================"
echo "⚠️  CRITICAL: Never commit '$KEYSTORE_NAME' or '${KEYSTORE_NAME}.base64.txt' to Git!"
