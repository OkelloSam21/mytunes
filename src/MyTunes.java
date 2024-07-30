import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javazoom.jl.player.Player;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.sql.*;

public class MyTunes extends JFrame implements ActionListener {
    private JTable songLibrary;
    private Connection dbConnection;

    public MyTunes() {
        initializeFrame();
        createControlPanel();
        createSongLibrary();
        createMenuBar();
        addPopupMenuToLibrary();
        setupDatabase();
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
        String[] columnNames = {"Title", "Artist", "Album", "Year", "Genre", "Comment"};
        Object[][] data = {}; // This will be populated from the database later
        songLibrary = new JTable(data, columnNames);
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

        JScrollPane libraryScrollPane = new JScrollPane(songLibrary);
        libraryScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(java.awt.event.MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // Enable drag and drop
        songLibrary.setDropTarget(new DropTarget() {
//            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List droppedFiles = (List) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (Object file : droppedFiles) {
                        addSongFromFile((File) file);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(libraryScrollPane, BorderLayout.CENTER);
    }

    private void addSongFromFile(File file) {
        String filePath = file.getAbsolutePath();
        String title = JOptionPane.showInputDialog(this, "Enter song title:");
        String artist = JOptionPane.showInputDialog(this, "Enter artist name:");
        String album = JOptionPane.showInputDialog(this, "Enter album name:");
        String yearStr = JOptionPane.showInputDialog(this, "Enter year:");
        int year = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);
        String genre = JOptionPane.showInputDialog(this, "Enter genre:");
        String comment = JOptionPane.showInputDialog(this, "Enter comment:");

        try {
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
            refreshSongLibrary();
        } catch (SQLException e) {
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

    private Player player;
    private String currentSongPath;

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
                    currentSongPath = rs.getString("file_path");
                    new Thread(() -> {
                        try {
                            FileInputStream fis = new FileInputStream(currentSongPath);
                            player = new Player(fis);
                            player.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
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
        if (player != null) {
            player.close();
        }
    }

    private void pauseSong() {
        // The basic Player class doesn't support pausing. For pausing functionality,
        // you'd need to use a more advanced audio library or implement it yourself.
        JOptionPane.showMessageDialog(this, "Pause functionality not implemented.");
    }

    private void unpauseSong() {
        // See comment in pauseSong()
        JOptionPane.showMessageDialog(this, "Unpause functionality not implemented.");
    }

    private int currentSongIndex = -1; // Track the index of the current song

    private void nextSong() {
        int rowCount = songLibrary.getRowCount();
        if (rowCount > 0) {
            currentSongIndex = (currentSongIndex + 1) % rowCount; // Move to the next song, wrap around if at the end
            songLibrary.setRowSelectionInterval(currentSongIndex, currentSongIndex); // Select the next song in the table
            playSong(); // Play the next song
        } else {
            JOptionPane.showMessageDialog(this, "No songs in the library.");
        }
    }

    private void previousSong() {
        int rowCount = songLibrary.getRowCount();
        if (rowCount > 0) {
            currentSongIndex = (currentSongIndex - 1 + rowCount) % rowCount; // Move to the previous song, wrap around if at the beginning
            songLibrary.setRowSelectionInterval(currentSongIndex, currentSongIndex); // Select the previous song in the table
            playSong(); // Play the previous song
        } else {
            JOptionPane.showMessageDialog(this, "No songs in the library.");
        }
    }

    private void addSong() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String title = JOptionPane.showInputDialog(this, "Enter song title:");
            String artist = JOptionPane.showInputDialog(this, "Enter artist name:");
            String album = JOptionPane.showInputDialog(this, "Enter album name:");
            String yearStr = JOptionPane.showInputDialog(this, "Enter year:");
            int year = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);
            String genre = JOptionPane.showInputDialog(this, "Enter genre:");
            String comment = JOptionPane.showInputDialog(this, "Enter comment:");

            try {
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
                refreshSongLibrary();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding song: " + e.getMessage());
            }
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
            DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Artist", "Album", "Year", "Genre", "Comment"}, 0);

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

            songLibrary.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error refreshing song library: " + e.getMessage());
        }
    }

    private void openSong() {
        System.out.println("Open song");
        // TODO: Implement open song functionality
    }

    private void exitApplication() {
        System.out.println("Exit application");
        System.exit(0);
    }

}