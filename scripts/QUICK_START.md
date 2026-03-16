# Quick Start Guide - Collaborative Editing

## For Server (Your Computer)

1. **Double-click:** `start_server.bat`
2. **Enter port:** Press Enter (default: 5001)
3. **Enter file path:** Type path to your .txt file
   - Example: `my_document.txt`
   - Example: `D:\notes\team_notes.txt`
   - Or press Enter for default
4. **Share your IP:** Run `ipconfig` and share IPv4 address with clients

## For Client (Other Computer)

1. **Double-click:** `start_client.bat`
2. **Enter server IP:** Type the server's IP address (e.g., `192.168.1.100`)
3. **Enter port:** Type `5001`
4. **Enter username:** Type your name
5. **Start editing:**
   - `READ` - View document
   - `APPEND <text>` - Add line
   - `REPLACE <lineNo> <text>` - Change line
   - `DELETE <lineNo>` - Remove line
   - `EXIT` - Quit

## Example Session

```
> READ
========== DOCUMENT ==========
1: Meeting Notes
2: Date: January 27, 2026
3: Attendees: Alice, Bob
==============================

> APPEND Action: Review code by Friday
[SUCCESS] Line 4 added.

> REPLACE 3 Attendees: Alice, Bob, Charlie
[SUCCESS] Line 3 replaced.
```

## Network Requirements

- Both computers on same WiFi/LAN
- Firewall allows port 5001
- Server IP accessible from client

## Files

- **Server sees:** Original .txt file updates in real-time
- **Logs:** All edits saved to `edit_log.txt`
