# MyTunes

## Description
MyTunes is a desktop application for managing and playing your music library. It allows you to add, delete, and play songs, as well as organize your music collection. The application now leverages ID3 tags to automatically retrieve song metadata, improving the user experience.

## Features
- Add songs to the library using ID3 tags
- Delete songs from the library
- Play, pause, unpause, stop, and navigate through songs
- Drag and drop files to add them to the library
- SQLite database integration for storing song information
- Songs from previous sessions are loaded and displayed on startup

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
        - Click the `+` icon to add the necessary libraries (e.g., `sqlite-jdbc`, `mp3agic`).

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
                <!-- mp3agic (ID3 Tag Library) -->
                <dependency>
                    <groupId>com.mpatric</groupId>
                    <artifactId>mp3agic</artifactId>
                    <version>0.9.1</version>
                </dependency>
                <!-- JavaFX (Media Player) -->
                <dependency>
                    <groupId>org.openjfx</groupId>
                    <artifactId>javafx-controls</artifactId>
                    <version>15.0.1</version>
                </dependency>
                <dependency>
                    <groupId>org.openjfx</groupId>
                    <artifactId>javafx-media</artifactId>
                    <version>15.0.1</version>
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
            implementation 'com.mpatric:mp3agic:0.9.1'
            implementation 'org.openjfx:javafx-controls:15.0.1'
            implementation 'org.openjfx:javafx-media:15.0.1'
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
- **Add Song**: Use the "Add Song" option in the menu or right-click on the library pane to add a song. The application will automatically extract metadata from the ID3 tags.
- **Delete Song**: Select a song in the library and use the "Delete Song" option in the menu or right-click on the library pane.
- **Play Song**: Select a song in the library and click the "Play" button.
- **Pause/Unpause Song**: Click the "Pause" or "Unpause" button.
- **Stop Song**: Click the "Stop" button.
- **Next/Previous Song**: Use the "Next" or "Previous" buttons to navigate through the songs.
- **Drag and Drop**: Drag and drop files directly onto the library pane to add them.

## Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.


