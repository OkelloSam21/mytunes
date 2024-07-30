# MyTunes

## Description
MyTunes is a desktop application for managing and playing your music library. It allows you to add, delete, and play songs, as well as organize your music collection.

## Features
- Add songs to the library
- Delete songs from the library
- Play, pause, stop, and navigate through songs
- Drag and drop files to add them to the library
- SQLite database integration for storing song information

## Installation
1. **Clone the repository**:
    ```sh
    git clone https://github.com/OkelloSam21/mytunes.git
    cd mytunes
    ```

2. **Set up the database**:
    - Ensure you have SQLite installed.
    - The database will be automatically created and set up when you run the application for the first time.

3. **Add Libraries**:
    - **IntelliJ IDEA**:
        - Open the project in IntelliJ IDEA.
        - Go to `File > Project Structure > Modules > Dependencies`.
        - Click the `+` icon to add the necessary libraries (e.g., `sqlite-jdbc`, `jlayer`).

    - **Maven**:
        - Create a `pom.xml` file in the root directory of your project.
        - Add the following dependencies:
        ```xml
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.example</groupId>
            <artifactId>mytunes</artifactId>
            <version>1.0-SNAPSHOT</version>
            <dependencies>
                <!-- SQLite JDBC -->
                <dependency>
                    <groupId>org.xerial</groupId>
                    <artifactId>sqlite-jdbc</artifactId>
                    <version>3.36.0.3</version>
                </dependency>
                <!-- JLayer (MP3 Player) -->
                <dependency>
                    <groupId>javazoom</groupId>
                    <artifactId>jlayer</artifactId>
                    <version>1.0.1</version>
                </dependency>
            </dependencies>
        </project>
        ```

    - **Gradle**:
        - Create a `build.gradle` file in the root directory of your project.
        - Add the following dependencies:
        ```groovy
        plugins {
            id 'java'
        }

        group 'com.example'
        version '1.0-SNAPSHOT'

        repositories {
            mavenCentral()
        }

        dependencies {
            implementation 'org.xerial:sqlite-jdbc:3.36.0.3'
            implementation 'javazoom:jlayer:1.0.1'
        }
        ```

4. **Build and run the application**:
    - **IntelliJ IDEA**:
        - Build the project and run the `Main` class.
    - **Maven**:
        - Open the terminal and run `mvn clean install`.
    - **Gradle**:
        - Open the terminal and run `./gradlew build`.

## Usage
- **Add Song**: Use the "Add Song" option in the menu or right-click on the library pane to add a song.
- **Delete Song**: Select a song in the library and use the "Delete Song" option in the menu or right-click on the library pane.
- **Play Song**: Select a song in the library and click the "Play" button.
- **Pause/Unpause Song**: Click the "Pause" or "Unpause" button.
- **Stop Song**: Click the "Stop" button.
- **Next/Previous Song**: Use the "Next" or "Previous" buttons to navigate through the songs.

## Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

## License
This project is licensed under the MIT License.
