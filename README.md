# CodeForge-IDE

A Java Swing desktop code editor built on the Nukepad framework, with embedded toolchain support for Arduino, Android, and ESP32 development.

## Requirements

### Core
- **Java 25+** (OpenJDK recommended)
- **Maven 3.x** — build tool
- **Git** — version control integration

### Arduino Support
- **[arduino-cli](https://arduino.github.io/arduino-cli/)** — must be installed and on PATH, or set its path in Settings > Toolchain Settings
- Board cores installed via Board Manager (e.g., `arduino:avr`, `esp32:esp32`)
- Serial connection via USB (jSerialComm for Serial Monitor)

### Android Support
- **Android SDK** — set path in Settings > Toolchain Settings
  - Includes: `adb` (platform-tools), build-tools, platform APIs
- **Gradle wrapper** (`gradlew`) — must be present in the project root
- **JDK 17+** — required by modern Android Gradle Plugin
- Connected device or emulator for deployment

### ESP32 / ESP8266 Support

**Arduino-framework route:**
- **[arduino-cli](https://arduino.github.io/arduino-cli/)** with ESP32/ESP8266 core installed (`arduino-cli core install esp32:esp32`)
- Serial connection via USB

**ESP-IDF route:**
- **[ESP-IDF](https://docs.espressif.com/projects/esp-idf/)** — set `IDF_PATH` in Settings > Toolchain Settings
- Python 3.8+ required by ESP-IDF
- Serial connection via USB

### Language Server Protocol (LSP)

Language servers provide code intelligence (completions, diagnostics, go-to-definition, hover) when editing files. Install only the servers for languages you use. Servers are started on-demand when a matching file is opened.

| Language | Server | Install |
|----------|--------|---------|
| Java | `jdtls` | Bundled via Eclipse JDT LS — requires a JDK 17+ on PATH |
| C/C++ | `clangd` | `sudo apt install clangd` (Linux) / `brew install llvm` (macOS) / [LLVM releases](https://releases.llvm.org/) (Windows) |
| Python | `pylsp` | `pip install python-lsp-server` |
| JavaScript/TypeScript | `typescript-language-server` | `npm install -g typescript-language-server typescript` |
| Go | `gopls` | `go install golang.org/x/tools/gopls@latest` |
| C# | `OmniSharp` | [omnisharp-roslyn releases](https://github.com/OmniSharp/omnisharp-roslyn/releases) — extract and add to PATH |
| HTML | `vscode-html-language-server` | `npm install -g vscode-langservers-extracted` |
| CSS | `vscode-css-language-server` | `npm install -g vscode-langservers-extracted` |
| JSON | `vscode-json-language-server` | `npm install -g vscode-langservers-extracted` |
| SQL | `sqls` | `go install github.com/sqls-server/sqls@latest` |

> **Tip:** A single `npm install -g vscode-langservers-extracted` covers HTML, CSS, and JSON servers.

### Linux
- **X11 display server** — required for GUI (Wayland may work with XWayland)
- Run with: `DISPLAY=:0 java -jar target/Nukepad.jar`

## Building

```bash
mvn package
```

Produces `target/Nukepad.jar` (uber-jar via maven-shade-plugin).

## Running

```bash
java -jar target/Nukepad.jar
```

On Linux without a display server session:
```bash
DISPLAY=:0 java -jar target/Nukepad.jar
```

## Features

- Multi-tab code editor with syntax highlighting (Java, C/C++, Python, JavaScript, TypeScript, Kotlin, Groovy, YAML, Go, Rust, and more)
- **Language Server Protocol (LSP)** — real-time completions, diagnostics, hover, and go-to-definition for 10 languages (Java, C/C++, Python, JS/TS, Go, C#, HTML, CSS, JSON, SQL)
- Git integration (init, status, commit, push, pull, log, diff)
- Terminal and Shell tabs
- Serial Monitor (jSerialComm)
- Logcat viewer (Android)
- Plugin system
- Dark/Light themes
- Arduino compile/upload, Board Manager, Library Manager
- Android Gradle build, deploy, and launch
