import java.net.*;
import java.io.InputStream;
import java.io.OutputStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Client {
	private static Socket clientSocket;
	private static InputStream inFromServer;
	private static DataInputStream in;
	private static OutputStream outToServer;
	private static DataOutputStream out;
	
	private static String server = "127.0.0.1";
	private static int port = 21234;
	
	private static JFrame frame;
	private static JScrollPane scrollForSendMessageText;
	private static JScrollPane scrollForMessages;
	private static JLabel errorLabel;
	private static JButton sendB;
	private static JTextArea sendMessageText;
	private static JTextArea allMessagesArea;
	
	private static Thread sendMsg = null;
	private static Thread getMsg = null;

	public static void main(String args[]){
		JPanel panel;
		
		//Call the function createFUI to set up the panel
		panel = createGUI();

		// Call the function to connect the socket and open the stream
		//If success return true
		if ( createConnectionAndStream() ) {

			// Start the threads of getting and sending a messages
			getMessageThread();
			sendMessageThread();
			
			panel.add(scrollForSendMessageText);
			panel.add(sendB);
			panel.add(scrollForMessages);

		}
		
		//If the connection failed
		//set an error message 
		else {
			String erro = "<html><font style='color:red; font-size:15;'>" + "Socket error"
					+ "<br/>Could not able to connect to the server" + "</font></html>";

			errorLabel = new JLabel(erro, SwingConstants.CENTER);
			errorLabel.setBounds(50, 150, 350, 150);

			errorLabel.setVisible(true);

			panel.add(errorLabel);
		}

		//Insert the 'panel' into the frame
		frame.add(panel);
		frame.revalidate();
		frame.repaint();
	}
	
	//The function create new Thread for the receiving messages
	private static void getMessageThread() {

		getMsg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/*
					 * Infinite while loop for the receiving messages
					 * Every time the input stream get date, the date has been read
					 * and insert into the JTestArea= allMessagesArea
					 */
					while (true)
						allMessagesArea.replaceSelection("him\\her: " + in.readUTF() + '\n');
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
		getMsg.start();

	}

	//The function create new Thread for the sending messages
	private static void sendMessageThread() {

		sendMsg = new Thread(new Runnable() {
			@Override
			public void run() {

				//Set listener to the send button
				//every time it get hit, the text send through the output stream
				sendB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							//Write the data to the output stream
							out.writeUTF(sendMessageText.getText());
							
							//insert the message into the the JTextArea= allMessagesArea
							allMessagesArea.replaceSelection("you: " + sendMessageText.getText() + '\n');
						} catch (IOException e1) {
							//If error occurred when writing in the stream
							//exit the program
							e1.printStackTrace();
							System.exit(1);
						}
						//clear the JTextArea from the previous message
						sendMessageText.setText("");

					}
				});
			}
		});

		sendMsg.start();
	}

	//Create the GUI
	private static JPanel createGUI() {
		JPanel panel = new JPanel(null);

		frame = new JFrame("Client chat");
		frame.setSize(500, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		allMessagesArea = new JTextArea();
		allMessagesArea.setEditable(false);
		allMessagesArea.setVisible(true);
		//create Vertical scroller for the JTextArea 'allMessagesArea'
		scrollForMessages = new JScrollPane(allMessagesArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//set the position and the size of the the TextArea
		scrollForMessages.setBounds(40, 40, 400, 400);

		//cread the 'send' Button and located it in the frame
		sendB = new JButton("Send");
		sendB.setBounds(20, 480, 80, 40);
		//disable the button until text has entered
		sendB.setEnabled(false);

		sendMessageText = new JTextArea();
		sendMessageText.setEditable(true);
		sendMessageText.setLineWrap(true);
		sendMessageText.setWrapStyleWord(true);

		//create Vertical scroller for the JTextArea 'sendMessageText'
		scrollForSendMessageText = new JScrollPane(sendMessageText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollForSendMessageText.setVisible(true);
		//set the position and the size of the the TextArea
		scrollForSendMessageText.setBounds(140, 460, 300, 60);

		//create listener for the JTextArea 'sendMessageText'
		sendMessageText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				checkToEnable();
			}

			public void removeUpdate(DocumentEvent e) {
				checkToEnable();
			}

			public void insertUpdate(DocumentEvent e) {
				checkToEnable();
			}

			/*
			 * Check if text has typed
			 * If text typed in the text field
			 * set the send button enable
			 * otherwise disable the button
			*/
			private void checkToEnable() {
				if (!sendMessageText.getText().isEmpty())
					sendB.setEnabled(true);
				else
					sendB.setEnabled(false);
			}
		});
		
		return panel;
	}

	private static Boolean createConnectionAndStream() {

		try {
			//connect to the socket
			clientSocket = new Socket(server, port);
			
			//create input and output streams
			inFromServer = clientSocket.getInputStream();
			in = new DataInputStream(inFromServer);
			outToServer = clientSocket.getOutputStream();
			out = new DataOutputStream(outToServer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
}
