# logcat-filter

Interactive terminal UI for filtering and tagging Android logcat output in real time.

---

## Installation

```bash
git clone https://github.com/yourusername/logcat-filter.git
cd logcat-filter && ./gradlew installDist
```

---

## Usage

Pipe `adb logcat` output directly into `logcat-filter`:

```bash
adb logcat | ./build/install/logcat-filter/bin/logcat-filter
```

**Key bindings inside the TUI:**

| Key | Action |
|-----|--------|
| `f` | Add a filter by tag or keyword |
| `t` | Assign a color tag to a pattern |
| `c` | Clear all active filters |
| `q` | Quit |

**Example — filter by a specific tag:**

```bash
adb logcat | logcat-filter --tag MyApp --level W
```

Only log lines matching the tag `MyApp` at warning level or above will be shown, highlighted in the terminal.

**Example — read from a saved logcat file:**

```bash
logcat-filter --file app.log --tag MyApp
```

---

## Requirements

- Java 11+
- Android SDK / `adb` available on your `PATH`

---

## Building

```bash
./gradlew build
```

---

## License

This project is licensed under the [MIT License](LICENSE).
