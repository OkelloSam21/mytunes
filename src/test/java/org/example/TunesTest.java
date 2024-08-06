package org.example;

import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

public class TunesTest {
    private Tunes tunes;

    @BeforeEach
    public void setUp() {
        tunes = new Tunes();
    }

    @AfterEach
    public void tearDown() {
        tunes = null;
    }

    @Test
    void actionPerformed_shouldPlaySongWhenPlayCommand() {
        ActionEvent playEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Play");
        tunes.actionPerformed(playEvent);
        Assertions.assertNotNull(tunes.mediaPlayer);
    }

    @Test
    void actionPerformed_shouldStopSongWhenStopCommand() {
        ActionEvent stopEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Stop");
        tunes.actionPerformed(stopEvent);
        assertNull(tunes.mediaPlayer);
    }

    @Test
    void actionPerformed_shouldPauseSongWhenPauseCommand() {
        ActionEvent pauseEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Pause");
        tunes.actionPerformed(pauseEvent);
        assertSame(tunes.mediaPlayer.getStatus(), MediaPlayer.Status.PAUSED);
    }

    @Test
    void actionPerformed_shouldUnpauseSongWhenUnpauseCommand() {
        ActionEvent unpauseEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Unpause");
        tunes.actionPerformed(unpauseEvent);
        assertSame(tunes.mediaPlayer.getStatus(), MediaPlayer.Status.PLAYING);
    }

    @Test
    void actionPerformed_shouldPlayNextSongWhenNextCommand() {
        ActionEvent nextEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Next");
        tunes.actionPerformed(nextEvent);
        assertEquals(1, tunes.songLibrary.getSelectedRow());
    }

    @Test
    void actionPerformed_shouldPlayPreviousSongWhenPreviousCommand() {
        ActionEvent previousEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Previous");
        tunes.actionPerformed(previousEvent);
        assertEquals(0, tunes.songLibrary.getSelectedRow());
    }

    @Test
    void actionPerformed_shouldAddSongWhenAddSongCommand() {
        ActionEvent addSongEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Add Song");
        tunes.actionPerformed(addSongEvent);
        assertEquals(1, tunes.songLibrary.getRowCount());
    }

    @Test
    void actionPerformed_shouldDeleteSongWhenDeleteSongCommand() {
        ActionEvent deleteSongEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Delete Song");
        tunes.actionPerformed(deleteSongEvent);
        assertEquals(0, tunes.songLibrary.getRowCount());
    }

    @Test
    void actionPerformed_shouldOpenSongWhenOpenCommand() {
        ActionEvent openEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Open");
        tunes.actionPerformed(openEvent);
        assertEquals(1, tunes.songLibrary.getRowCount());
    }

    @Test
    void actionPerformed_shouldExitApplicationWhenExitCommand() {
        ActionEvent exitEvent = new ActionEvent(tunes, ActionEvent.ACTION_PERFORMED, "Exit");
        tunes.actionPerformed(exitEvent);
        assertFalse(tunes.isVisible());
    }
}