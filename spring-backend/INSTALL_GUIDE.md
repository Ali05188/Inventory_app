# ============================================
# Installation Guide - Java & Maven for Windows
# ============================================

## OPTION 1: Manual Installation (Recommended)

### Step 1: Install Java 17 (JDK)
1. Go to: https://adoptium.net/temurin/releases/?version=17
2. Download: Windows x64 .msi installer
3. Run installer and CHECK "Set JAVA_HOME variable"

### Step 2: Install Maven
1. Go to: https://maven.apache.org/download.cgi
2. Download: apache-maven-3.9.6-bin.zip
3. Extract to: C:\Program Files\Apache\maven
4. Add to PATH:
   - Open System Properties > Environment Variables
   - Edit "Path" variable
   - Add: C:\Program Files\Apache\maven\bin

### Step 3: Verify Installation
Open NEW PowerShell window and run:
```
java -version
mvn -version
```

---

## OPTION 2: Using Chocolatey (if installed)

```powershell
# Run PowerShell as Administrator
choco install temurin17 -y
choco install maven -y
```

---

## OPTION 3: Using winget (Windows Package Manager)

```powershell
# Run PowerShell as Administrator
winget install EclipseAdoptium.Temurin.17.JDK
winget install Apache.Maven
```

---

## After Installation

1. Close and reopen PowerShell/Terminal
2. Run:
   ```
   cd C:\Users\ali.mellouk.ext\inventory_system\spring-backend
   mvn clean install
   ```

## Troubleshooting

If "mvn" is still not recognized:
1. Check MAVEN_HOME environment variable
2. Check PATH includes %MAVEN_HOME%\bin
3. Restart your computer

