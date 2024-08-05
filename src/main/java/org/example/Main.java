package org.example;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tunes().setVisible(true);
        });
    }
}