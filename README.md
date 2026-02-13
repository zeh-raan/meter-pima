# meter-pima
A small fast food shop java application software.

# Fork your own Fork
git clone https://github.com/yourname/meter-pima.git

# Makes personal changes on branches
git checkout -b <branch-name>

# After you have made your changes
git add .
git commit -m "branch-name message"
git push origin <branch-name>

# Update your main repo
git checkout <branch-name>
git pull origin main

## Basically

1. Download [SQLite JDBC](https://github.com/xerial/sqlite-jdbc/releases/tag/3.51.2.0) here!

2. Run the system
    ```bash
    java -classpath ".:sqlite-jdbc-3.51.2.0.jar" Main.java
    ```