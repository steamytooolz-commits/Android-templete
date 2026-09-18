# ==============================================================================
# Android Release Upload Keystore Generator (PowerShell for Windows)
# ==============================================================================

$KeyStoreName = "my-upload-key.jks"
$KeyAlias = "upload"
$ValidityDays = 10000

Write-Host "--------------------------------------------------------" -ForegroundColor Cyan
Write-Host " Android Release Keystore Generator (PowerShell)        " -ForegroundColor Cyan
Write-Host "--------------------------------------------------------" -ForegroundColor Cyan

$Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
$RandomPass = -join ((1..16) | ForEach-Object { $Chars[(Get-Random -Maximum $Chars.Length)] })

$Pass = Read-Host "Enter keystore password (press enter to use generated: $RandomPass)"
if ([string]::IsNullOrWhiteSpace($Pass)) { $Pass = $RandomPass }

$Alias = Read-Host "Enter Key Alias (default: $KeyAlias)"
if ([string]::IsNullOrWhiteSpace($Alias)) { $Alias = $KeyAlias }

& keytool -genkeypair -v `
  -keystore $KeyStoreName `
  -alias $Alias `
  -keyalg RSA `
  -keysize 2048 `
  -validity $ValidityDays `
  -storepass $Pass `
  -keypass $Pass `
  -dname "CN=Android Developer, OU=Mobile, O=AI Studio, L=Mountain View, ST=CA, C=US"

$Bytes = [System.IO.File]::ReadAllBytes($KeyStoreName)
$Base64 = [System.Convert]::ToBase64String($Bytes)
[System.IO.File]::WriteAllText("${KeyStoreName}.base64.txt", $Base64)

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host " Keystore generated successfully!" -ForegroundColor Green
Write-Host " Keystore file: $KeyStoreName"
Write-Host " Base64 file:   ${KeyStoreName}.base64.txt"
Write-Host " Password:      $Pass"
Write-Host "========================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Add to GitHub (Settings -> Secrets and variables -> Actions):"
Write-Host "KEYSTORE_BASE64 : (Content from ${KeyStoreName}.base64.txt)"
Write-Host "STORE_PASSWORD  : $Pass"
Write-Host "KEY_PASSWORD    : $Pass"
Write-Host "KEY_ALIAS       : $Alias"
