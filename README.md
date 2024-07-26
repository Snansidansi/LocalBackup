# LocalBackup

LocalBackup is a small backup program with a graphical user interface (GUI) for creating local backups of files and directories.

## Features
- Create/Delete backups from a source file or directory to a target directory
- Custom tags with custom colors for backups
- Logging system for backups (error and info logs)
- Manual backup initiation
- Customizable settings:
  - Backup execution during auto start (if the program gets executed through a Windows .exe file)
  - Light and dark mode
  - ...

## General Information
1. A file or directory is only copied to the backup location if it does not already exist there or if it has been modified since the last backup.
2. Tags are disabled by default but can be activated in the settings.
3. The program will create several directories in the directory of the executable file or the IDE working directory. These directories store the program data and should not be deleted, as this will result in data loss. These directories must be in the same location as the program executable file.
4. The program is only tested for Windows.
5. To remove a tag from a backup, select the tag in the "Apply tags" section and click on the "Apply tag" button. Then, unselect the backup rows that should no longer have the tag.

## Demo
### Dark Mode
<img src="../media/images/main-scene-dark.png?raw=true" width="500">
<img src="../media/images/log-dark.png?raw=true" width="500">
<img src="../media/images/settings-dark.png?raw=true" width="300">

### Light Mode Example
<img src="../media/images/main-scene-light.png?raw=true" width="500">
