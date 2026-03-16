# Tkinter GUI Wrappers - User Guide

## Overview
This directory contains 7 distributed systems programs with tkinter GUI wrappers. All GUIs use a shared `gui_base.py` framework for consistency and code reusability.

## Files Structure

### Core Framework
- **`gui_base.py`** - Reusable base class with common GUI components

### GUI Programs

#### 1. Simple Sender/Receiver
- **`sender_gui.py`** - Client that sends multiple names to server
- **`receiver_gui.py`** - Server that receives and displays names

**How to use:**
1. Run `receiver_gui.py` first, click "Start Server"
2. Run `sender_gui.py`, enter number of names, click "Generate Name Fields"
3. Fill in names and click "Send Names"

#### 2. Duplex Communication
- **`duplsender_gui.py`** - Client with menu-based name entry
- **`duplresciver_gui.py`** - Server with dual operation (client + server menu)

**How to use:**
1. Run `duplresciver_gui.py` first, click "Start Server"
2. Run `duplsender_gui.py`, click "Connect to Server"
3. Both client and server can add names independently

#### 3. Two-Way CRUD Operations
- **`2waysender_gui.py`** - Client with full CRUD operations
- **`2wayreceiver_gui.py`** - Server with full CRUD operations

**How to use:**
1. Run `2wayreceiver_gui.py` first, click "Start Server"
2. Run `2waysender_gui.py`, click "Connect to Server"
3. Both can Create, Edit, Delete, and Show names

#### 4. Standalone Name Collector
- **`takeandsend_gui.py`** - Standalone program to collect and display names

**How to use:**
1. Run `takeandsend_gui.py`
2. Enter number of names, click "Generate Name Fields"
3. Fill in names and click "Collect Names"

## Running the Programs

### Requirements
- Python 3.x
- tkinter (usually included with Python)

### Launch Commands
```bash
# Simple programs
python sender_gui.py
python receiver_gui.py

# Duplex programs
python duplsender_gui.py
python duplresciver_gui.py

# Two-way CRUD programs
python 2waysender_gui.py
python 2wayreceiver_gui.py

# Standalone program
python takeandsend_gui.py
```

## Features

### All GUIs Include:
- ✅ Thread-safe operations (non-blocking GUI)
- ✅ Status indicators with color coding
- ✅ Scrollable output logs
- ✅ Error handling with user-friendly messages
- ✅ Consistent design and layout
- ✅ Real-time updates

### Color Coding:
- 🟢 **Green** - Connected/Active
- 🔴 **Red** - Disconnected/Error
- 🟠 **Orange** - Connecting/Warning
- 🔵 **Blue** - Info/Cleared

## Original Console Programs
The original console-based programs are still available:
- `sender.py` / `receiver.py`
- `duplsender.py` / `duplresciver.py`
- `2waysender.py` / `2wayreceiver.py`
- `takeandsend.py`

## Optimization Features
1. **Reusable Base Class** - `gui_base.py` eliminates code duplication
2. **Threading** - All network operations run in separate threads
3. **Thread-Safe Updates** - GUI updates are safely queued from worker threads
4. **Resource Management** - Proper cleanup of sockets and threads on exit
5. **Consistent Interface** - All programs follow the same design pattern
