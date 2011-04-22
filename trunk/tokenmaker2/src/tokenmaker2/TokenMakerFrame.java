package tokenmaker2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * TokenMakerFrame provides a dialog to allow a user to pick if they want to
 * make a PC or an NPC token and starts up the relevant GUI frame to do that
 * task.
 * 
 * @author Blakey, Summer 2010.
 * 
 */
public class TokenMakerFrame extends JFrame
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton      button            = new JButton("Launch");
    private JRadioButton npcMakerButton    = new JRadioButton("An NPC Token",
                                                   true);
    private JRadioButton playerMakerButton = new JRadioButton("A PC Token",
                                                   false);

    public TokenMakerFrame()
    {
        super("TokenMaker");
        setSize(200, 200);
        setLayout(new BorderLayout());
        setVisible(true);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("What do you want to make today?");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(npcMakerButton);
        buttonGroup.add(playerMakerButton);

        panel.add(label);
        panel.add(button);
        panel.add(npcMakerButton);
        panel.add(playerMakerButton);
        add(panel, BorderLayout.CENTER);

        ButtonListener listener = new ButtonListener();
        button.addActionListener(listener);
    }

    class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            if (npcMakerButton.isSelected())
            {
                @SuppressWarnings("unused")
                NPCMaker frame = new NPCMaker();
            }
            else
            {
                @SuppressWarnings("unused")
                PCMaker frame = new PCMaker();
            }
            setVisible(false);
        }
    }

}