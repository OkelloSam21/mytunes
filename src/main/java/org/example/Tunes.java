package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.sql.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// Import mp3agic for reading ID3 tags
import com.mpatric.mp3agic.*;

public class Tunes extends JFrame implements ActionListener {
    JTable songLibrary;
    private Connection dbConnection;
    MediaPlayer mediaPlayer; // JavaFX MediaPlayer for better control
    private int currentSongIndex = -1;

    public Tunes() {
        new JFXPanel(); // Initialize JavaFX toolkit
        initializeFrame();
        createControlPanel();
        createSongLibrary();
        createMenuBar();
        addPopupMenuToLibrary();
        setupDatabase();
        refreshSongLibrary(); // Load existing songs when the application starts
    }

    private void initializeFrame() {
        setTitle("MyTunes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton pauseButton = new JButton("Pause");
        JButton unpauseButton = new JButton("Unpause");
        JButton nextButton = new JButton("Next");
        JButton prevButton = new JButton("Previous");

        playButton.addActionListener(this);
        stopButton.addActionListener(this);
        pauseButton.addActionListener(this);
        unpauseButton.addActionListener(this);
        nextButton.addActionListener(this);
        prevButton.addActionListener(this);

        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(pauseButton);
        controlPanel.add(unpauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(prevButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void createSongLibrary() {
        // Define column names for the table
        String[] columnNames = {"ID", "Title", "Artist", "Album", "Year", "Genre", "Comment"};

        // Create a custom table model that only allows editing of the Comment column
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow editing the Comment column (index 6)
            }
        };

        songLibrary = new JTable(model);
        JScrollPane libraryScrollPane = new JScrollPane(songLibrary);
        add(libraryScrollPane, BorderLayout.CENTER);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem addSongMenuItem = new JMenuItem("Add Song");
        JMenuItem deleteSongMenuItem = new JMenuItem("Delete Song");

        openItem.addActionListener(this);
        exitItem.addActionListener(this);
        addSongMenuItem.addActionListener(this);
        deleteSongMenuItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(addSongMenuItem);
        fileMenu.add(deleteSongMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void addPopupMenuToLibrary() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addSongItem = new JMenuItem("Add Song");
        JMenuItem deleteSongItem = new JMenuItem("Delete Song");
        addSongItem.addActionListener(this);
        deleteSongItem.addActionListener(this);
        popupMenu.add(addSongItem);
        popupMenu.add(deleteSongItem);

        songLibrary.setComponentPopupMenu(popupMenu);

        // Enable drag and drop
        songLibrary.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        addSongFromFile(file);
                    }
                    refreshSongLibrary();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addSongFromFile(File file) {
        try {
            // Use mp3agic to read ID3 tags
            Mp3File mp3file = new Mp3File(file.getAbsolutePath());
            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
            } else {
                id3v2Tag = new ID3v24Tag();
                mp3file.setId3v2Tag(id3v2Tag);
            }

            // Extract metadata from ID3 tags
            String title = id3v2Tag.getTitle();
            String artist = id3v2Tag.getArtist();
            String album = id3v2Tag.getAlbum();
            String yearStr = id3v2Tag.getYear();
            int year = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);
            String genre = id3v2Tag.getGenreDescription();
            String comment = id3v2Tag.getComment();
            String filePath = file.getAbsolutePath();

            // Insert song into database
            String sql = "INSERT INTO songs (title, artist, album, year, genre, comment, file_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            pstmt.setString(3, album);
            pstmt.setInt(4, year);
            pstmt.setString(5, genre);
            pstmt.setString(6, comment);
            pstmt.setString(7, filePath);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Song added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding song: " + e.getMessage());
        }
    }

    private void setupDatabase() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Create a connection to the database
            dbConnection = DriverManager.getConnection("jdbc:sqlite:mytunes.db");
            System.out.println("Connected to the database successfully.");

            // Create tables if they don't exist
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createSongsTable = "CREATE TABLE IF NOT EXISTS songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "artist TEXT," +
                "album TEXT," +
                "year INTEGER," +
                "genre TEXT," +
                "comment TEXT," +
                "file_path TEXT NOT NULL)";

        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(createSongsTable);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Play":
                playSong();
                break;
            case "Stop":
                stopSong();
                break;
            case "Pause":
                pauseSong();
                break;
            case "Unpause":
                unpauseSong();
                break;
            case "Next":
                nextSong();
                break;
            case "Previous":
                previousSong();
                break;
            case "Add Song":
                addSong();
                break;
            case "Delete Song":
                deleteSong();
                break;
            case "Open":
                openSong();
                break;
            case "Exit":
                exitApplication();
                break;
        }
    }

    private void playSong() {
        int selectedRow = songLibrary.getSelectedRow();
        if (selectedRow >= 0) {
            int songId = (int) songLibrary.getValueAt(selectedRow, 0);
            try {
                String sql = "SELECT file_path FROM songs WHERE id = ?";
                PreparedStatement pstmt = dbConnection.prepareStatement(sql);
                pstmt.setInt(1, songId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String filePath = rs.getString("file_path");
                    Media media = new Media(new File(filePath).toURI().toString());
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.play();
                    currentSongIndex = selectedRow;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error playing song: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a song to play.");
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void unpauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    private void nextSong() {
        stopSong();
        int rowCount = songLibrary.getRowCount();
        if (rowCount > 0) {
            currentSongIndex = (currentSongIndex + 1) % rowCount;
            songLibrary.setRowSelectionInterval(currentSongIndex, currentSongIndex);
            playSong();
        } else {
            JOptionPane.showMessageDialog(this, "No songs in the library.");
        }
    }

    private void previousSong() {
        stopSong();
        int rowCount = songLibrary.getRowCount();
        if (rowCount > 0) {
            currentSongIndex = (currentSongIndex - 1 + rowCount) % rowCount;
            songLibrary.setRowSelectionInterval(currentSongIndex, currentSongIndex);
            playSong();
        } else {
            JOptionPane.showMessageDialog(this, "No songs in the library.");
        }
    }

    private void addSong() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            addSongFromFile(selectedFile);
            refreshSongLibrary();
        }
    }

    private void deleteSong() {
        int selectedRow = songLibrary.getSelectedRow();
        if (selectedRow >= 0) {
            int songId = (int) songLibrary.getValueAt(selectedRow, 0);
            try {
                String sql = "DELETE FROM songs WHERE id = ?";
                PreparedStatement pstmt = dbConnection.prepareStatement(sql);
                pstmt.setInt(1, songId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Song deleted successfully!");
                refreshSongLibrary();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting song: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a song to delete.");
        }
    }

    private void refreshSongLibrary() {
        try {
            String sql = "SELECT id, title, artist, album, year, genre, comment FROM songs";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Create a new table model
            DefaultTableModel model = (DefaultTableModel) songLibrary.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getInt("year"),
                        rs.getString("genre"),
                        rs.getString("comment")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing song library: " + e.getMessage());
        }
    }

    private void openSong() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            addSongFromFile(selectedFile);
            refreshSongLibrary();
            // Play the newly added song
            currentSongIndex = songLibrary.getRowCount() - 1;
            songLibrary.setRowSelectionInterval(currentSongIndex, currentSongIndex);
            playSong();
        }
    }

    private void exitApplication() {
        System.out.println("Exiting application");
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tunes().setVisible(true));
    }
}