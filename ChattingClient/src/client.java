import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class client extends JFrame {
	private JTextArea logArea = new JTextArea();
	private JTextField textField = new JTextField();
	private JButton sendBtn = new JButton();
	private Socket socket = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private String nickname = null;
	private String msg = null;
	
	public client() {
		setTitle("채팅");
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		
		logArea.setBounds(0, 0, 300, 330);
		logArea.setEditable(false);
		textField.setBounds(0, 330, 240, 36);
		textField.addActionListener(new sendMessage());
		sendBtn.setBounds(240, 330, 54, 35);
		sendBtn.setFocusPainted(false);
		sendBtn.setBackground(Color.WHITE);
		sendBtn.setFont(new Font("맑은고딕", Font.PLAIN, 10));
		sendBtn.setText("전송");
		sendBtn.addActionListener(new sendMessage());
		
		add(logArea);
		add(textField);
		add(sendBtn);
		
		setVisible(true);
		
		nickname = JOptionPane.showInputDialog(null, "닉네임을 입력해주세요.", "닉네임 입력", JOptionPane.INFORMATION_MESSAGE);
		if(nickname == null) {
			System.exit(1);
		}
		
		try {
			socket = new Socket("127.0.0.1", 3000);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(nickname);
			
			while(true) {
				msg = in.readUTF();
				logArea.append(msg+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class sendMessage implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String msg = textField.getText();
			try {
				out.writeUTF(msg);
				textField.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new client();
	}

}
