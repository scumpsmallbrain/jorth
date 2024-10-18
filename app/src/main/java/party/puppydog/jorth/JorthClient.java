/*
 *  === party/puppydog/JorthClient.java ===========
 *  creates a swing window with which to interact with Jorth
 */
package party.puppydog.jorth;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JorthClient {
    /* GUI elements */
    public static final JFrame frame = new JFrame("Jorth Client");
    public static final JTextArea output = new JTextArea();
    public static final JScrollPane outputScroll = new JScrollPane(output);
    public static final JTextField input = new JTextField();

    /* application object */
    public static final Jorth jorth = new Jorth();

    public static void main(String[] args) {
        /* frame */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        /* output text area */
        output.setEditable(false);
        output.setFocusable(false);
        output.setFont(new Font("Monospaced", Font.BOLD, 12));
        /* input field */
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* get command */
                String command = input.getText();
                if (command.isEmpty())
                    return;
                input.setText("");
                output.append("> " + command + "\n");
                output.append( jorth.processCommand(command) + "\n" );
            }
        });
        input.setFont(new Font("Monospaced", Font.BOLD, 12));

        /* add to frame */
        frame.add(outputScroll, BorderLayout.CENTER);
        frame.add(input, BorderLayout.SOUTH);
        
        /* launch */
        frame.setVisible(true);
        input.requestFocus();
    }
}
