# 🌶️🫪 Snack POS System
A Java application to manage **food and drinks** in inventory, edit **menu items**, take **orders**, and process **payments**.

## 📦 Prerequisites
The application requires the following:

1. Java JDK 25.0.2+
    * Download [Java JDK 25](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html) here!  

2. SQLite JDBC Driver **JAR**
    * Download [SQLite JDBC](https://github.com/xerial/sqlite-jdbc/releases/tag/3.51.2.0) here!
    * Copy driver to working directory.
        ```bash
        cp Downloads/sqlite-jdbc-3.51.2.0.jar path/meter-pima/
        ```

## 💻 Setup & Usage
Follow these steps to be able to run the application properly.

1. **Clone the repository.**
    ```bash
    git clone https://github.com/zeh-raan/meter-pima.git
    ```

2. **Navigate to the project directory.**
    ```bash
    cd meter-pima/
    ```

3. **Run the application.**
    ```bash
    java -classpath ".:sqlite-jdbc-3.51.2.0.jar" --enable-native-access=ALL-UNNAMED Main.java
    ```
## **System Flow**
| Layer     | Role                                                         |
| --------- | ------------------------------------------------------------ |
| **Model** | Represents application data and business logic               |
| **GUI**   | Collects input, displays data, and interacts with the Model. |
| **DAO**   | Performs CRUD operations on database entities.               |
| **DB**    | Manages database connection, schema, and stores data.        | 