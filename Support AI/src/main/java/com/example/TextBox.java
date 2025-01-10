package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class TextBox extends TextEngine {
    static JFrame frame;
    static JPanel firstPanel, secondPanel;
    final static int WIDTH = 800, HEIGHT = 800;
    static ArrayList<String> inputArray = new ArrayList<>(); // user questions
    static ArrayList<String> outputArry = new ArrayList<>(); // API

    public static void run() {
        frame = new JFrame("Support AI");
        firstPanel = new JPanel();
        secondPanel = new JPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        frame.setLayout(new BorderLayout());
        firstPanel.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea("Support AI");
        textArea.setFocusable(false);
        JButton enter = new JButton("Enter");
        JTextArea userText = new JTextArea();
        JPanel temp = new JPanel();
        userText.setPreferredSize(new Dimension(300, 100));
        enter.setPreferredSize(new Dimension(100, 100));
        temp.add(userText);
        temp.add(enter);

        firstPanel.add(temp, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        firstPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));
        scrollPane.getViewport().setBackground(Color.decode("#F2F2F2"));
        // so that the box is auto fill to make it look better
        for (int i = 0; i < 12; i++) {
            input.add(anonTextMessege(null, 3));
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            });
        }
        String auto = "What can I help you with?";
        input.add(anonTextMessege(auto, 1));// 1 == AI, 2 == User

        enter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                String userInput = userText.getText();
                inputArray.add(userInput);
                userText.setText("");
                input.add(anonTextMessege(userInput, 2));
                frame.repaint();
                frame.revalidate();
                try {
                    outputArry.add(aiOutput(userInput));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                input.add(anonTextMessege(outputArry.get(outputArry.size() - 1), 1));
                frame.repaint();
                frame.revalidate();
                System.out.println("User inputs: " + inputArray);
                System.out.println("API output: " + outputArry);

                SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                });
            }

        });

        scrollPane.setViewportView(input);
        frame.add(textArea, BorderLayout.NORTH);
        frame.add(firstPanel, BorderLayout.CENTER);
        frame.setVisible(true);

    }
}
