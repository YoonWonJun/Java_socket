import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class server extends JFrame {
	private JButton srvBtn = new JButton();
	private JTextArea logArea = new JTextArea();
	private ServerSocket serversocket = null;
	private Socket socket = null;
	private Map<String, DataOutputStream> ClientsMap = new HashMap<String, DataOutputStream>();
	private boolean SERVER_CHECK = false;

	public server() {
		setTitle("채팅 서버");
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);

		logArea.setBounds(0, 0, 300, 320);
		logArea.setEditable(false);
		srvBtn.setBounds(0, 320, 294, 45);
		srvBtn.setFocusPainted(false);
		srvBtn.setBackground(Color.WHITE);
		srvBtn.setFont(new Font("맑은고딕", Font.PLAIN, 11));
		srvBtn.setText("서버 ON");
		add(logArea);
		add(srvBtn);
		
		srvBtn.addActionListener(new MyservBtnListener());

		setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new server();
	}

	class serverStart extends Thread {
		public void run() {
			try {
				serversocket = new ServerSocket(3000);
				while (true) {
					socket = serversocket.accept();
					Receiver receiver = new Receiver(socket);
					receiver.start();
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	class Receiver extends Thread {
		private DataInputStream in = null;
		private DataOutputStream out = null;
		private String nickname;
		private String msg;

		public Receiver(Socket socket) {
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				nickname = in.readUTF();
				logArea.append(nickname + "님이 채팅에 입장했습니다.\r\n");
				addClient(nickname, out);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		public void run() {
			try {
				while (true) {
					msg = in.readUTF();
					logArea.append(nickname+" : "+msg+"\r\n");
					sendMessage(nickname, msg);
				}
			} catch (IOException e) {
				removeClient(nickname);
				try {
					in.close();
					out.close();
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	class MyservBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Thread server = new serverStart();
			if (e.getSource() == srvBtn) {
				if (SERVER_CHECK == false) {
					SERVER_CHECK = true;
					logArea.append("SERVER ON\r\n");

					srvBtn.setText("서버 OFF");
					server.start();
				} else {
					SERVER_CHECK = false;
					logArea.append("SERVER CLOSE\r\n");
					Iterator<String> iterator = ClientsMap.keySet().iterator();
					String key;
					try {
						while (iterator.hasNext()) {
							key = iterator.next();
							ClientsMap.get(key).close();
						}
						ClientsMap.clear();
						serversocket.close();
						server.interrupt();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private void addClient(String nickname, DataOutputStream out) {
		ClientsMap.put(nickname, out);
	}

	private void sendMessage(String nickname, String msg) {
		Iterator<String> iterator = ClientsMap.keySet().iterator();
		String key;
		while (iterator.hasNext()) {
			try {
				key = iterator.next();
				ClientsMap.get(key).writeUTF(nickname+" : "+msg);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void removeClient(String nickname) {
		logArea.append(nickname + "님이 접속을 종료했습니다.\r\n");
		ClientsMap.remove(nickname);
	}
}
