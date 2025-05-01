package com.novanation.akumanoshima;


import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SceneLoader extends JFrame {
    
    // Scene Panels
    private static final ArrayList<String> scenes = new ArrayList<>();
    private static String currentScene;

    // Card Setup
    private final static CardLayout cardLayout = new CardLayout();
    private final static JPanel cardPanel = new JPanel(cardLayout);

    public static void addScene(JPanel newPanel)
    {
        String sceneName;

        if ("Game".equals(newPanel.getName()))
            sceneName = "Game";
        else
            sceneName = newPanel.getClass().getSimpleName();
            
        
        for (Object elem : scenes)
            if (elem == sceneName)
            {
                System.err.println("Scene Insertion Failed! Scene already exists.");
                return;
            }
        
        cardPanel.add(newPanel, sceneName);
        scenes.add(sceneName);
    }

    public static boolean alreadyInScene(String newScene)
    {
        return !scenes.contains(newScene);
    }

    public static void switchScene(String newScene)
    {
        if (!scenes.contains(newScene))
        {
            System.err.println("Scene Switch Failed!. Scene does not exist.");
            return;
        }
        
        if (currentScene == null ? newScene == null : currentScene.equals(newScene))
        {
            System.err.println("Scene Switch Failed!. The scene is already visible.");
            return;
        }

        cardLayout.show(cardPanel, newScene);
        currentScene = newScene;
    }

    public static JPanel getCardPanel() { return cardPanel; }
    public List<String> getAllScenes() { return scenes; }
}
