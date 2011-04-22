package tokenmaker2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.html.*;

/**
 * PowerFrame provides a GUI to display a power in a pop up frame. It isn't
 * really designed to do anything other than display a power in a pretty way.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class PowerFrame extends JFrame
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton exitButton;

    public PowerFrame(Power power)
    {
        super(power.getName());
        try
        {
            setLayout(new BorderLayout());
            setSize(400, 500);

            // Create the listeners we're going to use
            MyButtonListener buttonListener = new MyButtonListener();
            KeyboardListener keyListener = new KeyboardListener();

            // Create an editor pane in the middle
            JPanel editorPanel = new JPanel();
            JEditorPane editorPane = new JEditorPane(
                    new HTMLEditorKit().getContentType(), power.getHTML());

            // add a CSS rule to force body tags to use the default label font
            // instead of the value in javax.swing.text.html.default.csss
            Font font = UIManager.getFont("Label.font");
            String bodyRule = "body { font-family: " + font.getFamily() + "; "
                    + "font-size: " + font.getSize() + "pt; }";
            ((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule(
                    bodyRule);

            editorPane.setEditable(false);
            JScrollPane editorScrollPane = new JScrollPane(editorPane);
            editorScrollPane
                    .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setPreferredSize(new Dimension(350, 400));
            editorScrollPane.setMinimumSize(new Dimension(10, 10));
            editorPanel.add(editorScrollPane);
            add(editorPanel, BorderLayout.CENTER);
            // force the scroll pane to start at the top.
            editorPane.setCaretPosition(0);

            // Create a panel to deal with exit
            JPanel southPanel = new JPanel();
            exitButton = new JButton("Close");
            exitButton.addActionListener(buttonListener);
            exitButton.addKeyListener(keyListener);
            southPanel.add(exitButton);
            add(southPanel, BorderLayout.SOUTH);

        }
        catch (Exception e)
        {
            System.err.println("Error creating the Power Frame dialog: " + e);
        }

    }

    private void closePowerFrame()
    {
        setVisible(false);
    }

    class MyButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == exitButton)
            {
                closePowerFrame();
            }
        }
    }

    class KeyboardListener extends KeyAdapter
    {
        public void keyTyped(KeyEvent e)
        {
            if (e.getSource() == exitButton)
            {
                closePowerFrame();
            }
        }
    }
}
