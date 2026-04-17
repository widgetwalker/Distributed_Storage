$ErrorActionPreference = "Stop"

function Test-Api($Action, $PayloadJson) {
    # Since we are using Java Serialization, it's hard to test directly from PowerShell via JSON.
    # So I will just write a short pure Java client test instead.
}
