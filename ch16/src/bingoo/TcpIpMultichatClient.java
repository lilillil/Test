package bingoo;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class TcpIpMultichatClient {
	static DataOutputStream out;
	static Label stlb = new Label();
	static Button b = new Button("시 작");
	static Button exit = new Button("나가기");
	static Socket socket;
	static Bingo win = null;

	public static void main(String args[]) {

		//플레이어명 입력하는 프레임
		Frame nameFrame = new Frame("input name");
		nameFrame.setSize(400, 100);
		nameFrame.setLayout(new FlowLayout()); // . 레이아웃 매니저의 설정을 해제한다
		//플레이어명텍스트필드, 라벨
		TextField nametxt = new TextField(10);
		Label nameLabel = new Label();
		Button okBtn = new Button("접속");
		nameLabel.setText("플레이어이름입력");
		//프레임에 add
		nameFrame.add(nameLabel);
		nameFrame.add(nametxt);
		nameFrame.add(okBtn);
		nameFrame.setVisible(true);
		
		//접속버튼 눌렀을때 action
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				//플레이어 이름 텍스트 필드가 비어있으면 종료
				if(nametxt.getText().isEmpty()) {
					System.out.println("USAGE: java TcpIpMultichatClient 대화명");
					System.exit(0); 
				//플레이어 이름 입력 했으면 시작 버튼 화면 나오게 하기		
				}else {
					
					nameFrame.dispose();
					nameFrame.setVisible(false);
					
					String name = nametxt.getText();

					
					Frame f = new Frame("BinGo Game~");
					f.setSize(400, 400);
					f.setLayout(null); // . 레이아웃 매니저의 설정을 해제한다
					
					Font font = new Font("Serif", Font.ITALIC, 40) ;


						
					Label title = new Label();
					title.setBounds(120, 70, 200, 150); 
					title.setText("빙고게임");
					title.setFont(font);
					f.add(title);

					b.setSize(100, 50); // Button . 의 크기를 설정한다
					b.setLocation(70, 280); // Frame Button . 내에서의 의 위치를 설정한다
					f.add(b);
					exit.setSize(100, 50); // Button . 의 크기를 설정한다
					exit.setLocation(230, 280); // Frame Button . 내에서의 의 위치를 설정한다
					f.add(exit);	


					stlb.setBounds(75, 50, 100, 430); 
					stlb.setText("0");

					f.add(stlb);
					
					f.setVisible(true);

				
					try {
			            // 소켓을 생성하여 연결을 요청한다.
//						socket = new Socket("10.10.10.141", 7777);
						socket = new Socket("127.0.0.1", 7777);
						System.out.println("서버에 연결되었습니다.");
						//textfield name받게 수정
						Thread sender   = new Thread(new ClientSender(socket, name));
						Thread receiver = new Thread(new ClientReceiver(socket));

						sender.start();
						receiver.start();
					} catch(ConnectException ce) {
						ce.printStackTrace();
					} catch(Exception e) {}
					
					
					
					TcpIpMultichatClient awtControlDemo = new TcpIpMultichatClient();
					awtControlDemo.showButton();
					
					
					
					
				}
				
			}
		});
	} // main

	private void showButton() {
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeUTF("1|게임시작");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	static class ClientSender extends Thread {
		Socket socket;

		String name;

		ClientSender(Socket socket, String name) {
			this.socket = socket;
			try {
				out = new DataOutputStream(socket.getOutputStream());
				this.name = name;
			} catch (Exception e) {
			}
		}

		public void run() {
			Scanner scanner = new Scanner(System.in);
			try {
				if (out != null) {
					out.writeUTF(name);
				}

				while (out != null) {
					out.writeUTF("[" + name + "]" + scanner.nextLine());
				}
			} catch (IOException e) {}
		} // run()
	} // ClientSender

	static class ClientReceiver extends Thread {
		Socket socket;

		DataInputStream in;

		ClientReceiver(Socket socket) {
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
			}
		}

		//@180526
		boolean boss = false;
		
		public void run() {
			// System.out.println("클라"+in);
			while (in != null) {
				try {
					// String msg = in.readLine();// msg: 서버가 보낸 메시지
					String msg = in.readUTF();// msg: 서버가 보낸 메시지

					String msgs[] = msg.split("\\|");

					String protocol = msgs[0];

					System.out.println(msg + "|클라도착");
					switch (protocol) {

					case "100":
						stlb.setText("대기자 = " + msgs[1]);
						//@180526
						System.out.println(msgs[1]);
						if(msgs[1].equals("1")) {
							boss = true;
							System.out.println("보스1:"+ boss);
						}else {
							boss = false;
							System.out.println("보스2:"+ boss);
						}
						break;
					case "1":
						if (win == null)
							win = new Bingo("Bingo Game Ver1.0", socket);
						b.setEnabled(false);
						//@180526
						if (boss = true)
							win.turn = true;
						System.out.println("win.turn"+win.turn);
						break;
					case "300":
						win.bingoCheck(msgs[1]);
						break;
					case "400":
						if(msgs[1].equals("게임종료")) {
							win.winCheck(msgs[1]);
						}if(msgs[1].equals("게임재시작")) {
							b.setEnabled(true);
							win = null;
							
						}if(msgs[1].equals("게임끄기")) {
							System.exit(0);
						}
					case "#":
						System.out.println(msgs[1]);
						break;
					}
				} catch (IOException e) {
				}
			}
		} // run
	} // ClientReceiver
} // class