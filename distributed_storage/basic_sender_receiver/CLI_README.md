# Distributed Systems Programs - Organization

This directory contains distributed systems programs organized into folders:

## Folder Structure

### 📁 `originals/`
Original console-based programs (7 files):
- `sender.py` - Simple sender
- `receiver.py` - Simple receiver
- `duplsender.py` - Duplex sender (client)
- `duplresciver.py` - Duplex receiver (server)
- `2waysender.py` - Two-way CRUD sender (client)
- `2wayreceiver.py` - Two-way CRUD receiver (server)
- `takeandsend.py` - Standalone name collector

**To run:** `py <filename>` (e.g., `py sender.py`)

### 📁 `nrml_gui/`
Terminal-like GUI wrappers (8 files):
- `gui_base.py` - Shared terminal GUI framework
- `sender_gui.py` - GUI for sender
- `receiver_gui.py` - GUI for receiver
- `duplsender_gui.py` - GUI for duplex sender
- `duplresciver_gui.py` - GUI for duplex receiver
- `2waysender_gui.py` - GUI for two-way sender
- `2wayreceiver_gui.py` - GUI for two-way receiver
- `takeandsend_gui.py` - GUI for name collector

**To run:** `py <filename>` (e.g., `py sender_gui.py`)

**Features:**
- Black terminal window with white text
- Simple input field at bottom
- Same behavior as console versions
- No extra buttons or complexity

## Quick Start

### Console Programs
```bash
cd originals
py receiver.py    # Start server
# In another terminal:
py sender.py      # Start client
```

### GUI Programs
```bash
cd nrml_gui
py receiver_gui.py    # Start server GUI
# In another terminal:
py sender_gui.py      # Start client GUI
```

## Program Pairs

| Server | Client | Description |
|--------|--------|-------------|
| `receiver.py` | `sender.py` | Simple name transfer |
| `duplresciver.py` | `duplsender.py` | Duplex communication |
| `2wayreceiver.py` | `2waysender.py` | Full CRUD operations |
| - | `takeandsend.py` | Standalone (no server) |

*Add `_gui` suffix for GUI versions*
