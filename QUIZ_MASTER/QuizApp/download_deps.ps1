$ErrorActionPreference = "Stop"
$libDir = "lib"

If (!(Test-Path $libDir)) {
    New-Item -ItemType Directory -Force -Path $libDir | Out-Null
}

$deps = @(
    "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/10.1.13/tomcat-embed-core-10.1.13.jar",
    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.44.0.0/sqlite-jdbc-3.44.0.0.jar",
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar",
    "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar"
)

foreach ($url in $deps) {
    $fileName = Split-Path $url -Leaf
    $dest = Join-Path $libDir $fileName
    if (!(Test-Path $dest)) {
        Write-Host "Downloading $fileName..."
        Invoke-WebRequest -Uri $url -OutFile $dest
    } else {
        Write-Host "$fileName already exists."
    }
}
Write-Host "All dependencies downloaded."
