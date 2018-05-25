package bingo;

import java.net.*;
import java.io.*;
import java.util.*;

public class TcpIpMultichatServer {
	HashMap clients;


	TcpIpMultichatServer() {
		clients = new HashMap();
		Collections.synchronizedMap(clients);
		new Thread().start();
	}

	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("서버가 시작되었습니다.");

			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket);
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속하였습니다.");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // start()

	void sendToAll(String msg) {
		Iterator it = clients.keySet().iterator();

		while (it.hasNext()) {
			try {
				DataOutputStream out = (DataOutputStream) clients.get(it.next());

				out.writeUTF(msg);
			} catch (Exception e) {
				
			}
		} // while
	} // sendToAll

	public static void main(String args[]) {
		new TcpIpMultichatServer().start();
	}

	class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		DataOutputStream out;

		ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		public void run() {
			String name = "";
			try {
				name = in.readUTF();
				clients.put(name, out);

				sendToAll("#|" + name + "님이 들어오셨습니다.");
				System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");

				sendToAll("100|" + clients.size());

				while (true) {

					try {

						String msg = in.readUTF();
						System.out.println("msg:" + msg);
						String msgs[] = msg.split("\\|");
						String protocol = msgs[0];

						System.out.println(msg + "|서버도착");
						switch (protocol) {

						case "1":
							sendToAll("1|게임시작");
							break;
						default :
							sendToAll(msg);
							break;

						}
					} catch (Exception e) {}
				}
			} catch (Exception e) {
				// ignore
			} finally {
				sendToAll("#" + name + "님이 나가셨습니다.");
				clients.remove(name);
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
				System.out.println("현재 서버접속자 수는 " + clients.size() + "입니다.");
				sendToAll("100|" + clients.size());

			} // try
		} // run
	} // ReceiverThread
} // class