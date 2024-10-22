import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class daGui {

    public static void main(String[] args) {
        // Create frame
        JFrame frame = new JFrame("PACC");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a panel for the top section (profile picture and name)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align left

        // Load the profile picture
        ImageIcon profilePicture = new ImageIcon("path/to/your/profile_picture.png"); // image pathing
        JLabel profileLabel = new JLabel(profilePicture);
        profileLabel.setPreferredSize(new Dimension(40, 40)); // Set size for profile picture

        // Create a label for the name
        JLabel nameLabel = new JLabel("Angelo");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Set font style and size
        nameLabel.setForeground(new Color(2, 2, 185)); // text colour

        // Add components to the top panel
        topPanel.add(profileLabel);
        topPanel.add(nameLabel);

        // Add the top panel to the frame
        frame.add(topPanel, BorderLayout.NORTH);

        // Create a text area (for displaying messages)
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false); // Don't let the user type here, display only
        chatArea.setBackground(new Color(30, 30, 30)); // chat background
        chatArea.setForeground(new Color(255, 0, 255)); //
        JScrollPane chatScroll = new JScrollPane(chatArea); // Scrolling through messages
        frame.add(chatScroll, BorderLayout.CENTER);

        // Create a panel for the input field and send button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // Create a text field (for user input)
        JTextField inputField = new JTextField();
        inputField.setBackground(new Color(50, 50, 50)); 
        inputField.setForeground(new Color(255, 255, 255)); 
        inputPanel.add(inputField, BorderLayout.CENTER); // Add input field to panel

        // Create a button to send the message
        ImageIcon sendIcon = new ImageIcon("sendbutton.jpg");
        JButton sendButton = new JButton(sendIcon);
        sendButton.setBackground(new Color(76, 175, 80)); // 
        sendButton.setForeground(new Color(5, 2, 185)); // White text
        sendButton.setPreferredSize(new Dimension(65, 50)); // Set button size 
        inputPanel.add(sendButton, BorderLayout.EAST); // Add button to panel

        // Create a button for file attachment
        ImageIcon attachIcon = new ImageIcon("paperclip.png"); // Insert image here
        JButton attachButton = new JButton(attachIcon); // Use the ImageIcon as the button 
        attachButton.setBackground(new Color(76, 175, 80)); 
        attachButton.setBorderPainted(false); // To remove/add button border
        attachButton.setContentAreaFilled(false); // To remove/add background filling
        attachButton.setPreferredSize(new Dimension(40, 40)); // Set button size 
        inputPanel.add(attachButton, BorderLayout.WEST); // Add attach button to the left of input 

        // Add input panel to the bottom 
        frame.add(inputPanel, BorderLayout.SOUTH);

        // background colour
        frame.getContentPane().setBackground(new Color(20, 20, 20)); 

        // Action when the send button is clicked
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                if (!message.isEmpty()) {
                    chatArea.append("You: " + message + "\n");
                    inputField.setText(""); // Clear the input after sending
                }
            }
        });
        // The file gets chose in the action
        attachButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    chatArea.append("Attached: " + selectedFile.getName() + "\n");
        }
    }
});
        // Show frame
        frame.setVisible(true);
    }
}
