package bingoo;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

class Bingo extends Frame{

	final int SIZE = 5; // 빙고판의 크기
	int bingoCnt = 0; // 완성된 라인의 수
	boolean winCheck=false;
	Button[] btnArr = null;
	boolean[][] bArr = new boolean[SIZE][SIZE]; // 빙고판 체크여부 확인을 위한 배열
	boolean isBingo = false;
	boolean click=true;
	// 빙고판 버튼에 사용할 문자열, 빙고판의 크기에 따라 이들의 일부만 사용될 수 있다.
	String[] values = { "글쎄", "기로로", "김창우", "김천대표", "까꿍", "남궁성", "낭군이", "넓게보기", "네라주리", "다밀", "더클레오", "들개", "디벨로", "레몬",
			"루션", "루이지노", "무색이", "문학청년", "사천사", "상상", "세피룸", "스쿨쥐", "쌩", "쏭양", "씨드", "양수호", "에노야", "에비츄", "에이스", "엔즈",
			"오이날다", "오케클릭", "용주니", "우기파파", "잠탱이", "제러스", "조땜", "지냔", "카라", "캉스", "태연", "파티쉐", "페르소마", "폭풍", "핏빛노을",
			"핑크팬더", "하늘이", "하루", "한경훈", "헐레벌떡", "화염병", "흑빛" };

	DataOutputStream out;

	Bingo(Socket socket) {
		this("Bingo Game Ver1.0", socket);
	}

	Bingo(String title, Socket socket) {

		super(title);

		setLayout(new GridLayout(SIZE, SIZE));

		MyEventHandler handler = new MyEventHandler();
		addWindowListener(handler);

		btnArr = new Button[SIZE * SIZE];

		shuffle();

		// Frame에 버튼을 추가한다.
		for (int i = 0; i < SIZE * SIZE; i++) {

			btnArr[i] = new Button(values[i]); // 문자열배열 values의 값을 버튼의 Label로 한다.
			add(btnArr[i]);
			btnArr[i].addActionListener(handler);
		}

		setBounds(500, 200, 300, 300);
		setVisible(true);

		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {}

	}

	void shuffle() {
		for (int i = 0; i < values.length * 2; i++) {
			int r1 = (int) (Math.random() * values.length);
			int r2 = (int) (Math.random() * values.length);

			String tmp = values[r1];
			values[r1] = values[r2];
			values[r2] = tmp;
		}
	}

	void print() { // 배열 bArr을 출력한다.
		for (int i = 0; i < bArr.length; i++) {
			for (int j = 0; j < bArr.length; j++) {
				System.out.print(bArr[i][j] ? "O" : "X");
			}
			System.out.println();
		}
		System.out.println("----------------");
		// System.out.println(bingoCnt);
	}

	boolean checkBingo() { // 빙고가 완성되었는지를 확인한다.
		bingoCnt = 0;
		int garoCnt = 0;
		int seroCnt = 0;
		int crossCnt1 = 0;
		int crossCnt2 = 0;

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (i + j == SIZE - 1 && bArr[i][j])
					crossCnt2++;
				if (i == j && bArr[i][j])
					crossCnt1++;
				if (bArr[i][j])
					garoCnt++;
				if (bArr[j][i])
					seroCnt++;
			}

			if (garoCnt == SIZE)
				bingoCnt++;
			if (seroCnt == SIZE)
				bingoCnt++;

			// if(bingoCnt>=SIZE) return true;
			garoCnt = 0;
			seroCnt = 0;
		}

		if (crossCnt1 == SIZE)
			bingoCnt++;
		if (crossCnt2 == SIZE)
			bingoCnt++;

		// System.out.println(bingoCnt);
		return bingoCnt >= SIZE;
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			Button btn = (Button) ae.getSource();

			String str = ae.getActionCommand();
			
			click = false;
			btn.setEnabled(false);
			
//			for (int i = 0; i < SIZE * SIZE; i++) {
//				btnArr[i].setEnabled(false);
//			}		
			
			
			
			// 2. 눌러진 버튼을 전송한다
			try {
				out.writeUTF("300|" + str);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// print();
			

		}
	}

	public void windowClosing(WindowEvent e) {
		e.getWindow().setVisible(false);
		e.getWindow().dispose();
		System.exit(0);
	}
	
	public void winCheck(String bingo) {
		String tmp;
		
		for (int i = 0; i < SIZE * SIZE; i++) {
			btnArr[i].setEnabled(false);
		}	
		
		if (winCheck) {
			tmp= "승리!";		
		}
		else{
			tmp= "패배!";
		}
		
		int option = JOptionPane.showConfirmDialog(null, tmp+ " 다시시작?", "title", JOptionPane.YES_NO_OPTION);

		// 예
		if (option == 0) {
			// 종료된 빙고판 사라짐
			setVisible(false);
			try {
				out.writeUTF("400|게임재시작");
			} catch (IOException e) {}// 다시시작
		} 
		// 아니오
		else if (option == 1) {// 종료
			try {
				out.writeUTF("400|게임끄기");
			} catch (IOException e) {}
		}
	}

	public void bingoCheck(String bingo) {
		
		
		Button tmp = null;
		
		// 1. 눌린 버튼 체크
		
		for (int i = 0; i < btnArr.length; i++) {
			if (btnArr[i].getLabel().equals(bingo)) {
				tmp = btnArr[i];
				bArr[i / SIZE][i % SIZE] = true;
				break;
			}
		}	
		
		try {
			tmp.setBackground(Color.RED);
			tmp.setEnabled(false);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		if (checkBingo()) {

			if(!isBingo) {
				isBingo=true;
			System.out.println("Bingo~!!!");

			Frame f = new Frame("Parent");
			f.setSize(300, 200);
			// parent Frame f , modal true Dialog . 을 로 하고 을 로 해서 필수응답 로 함
			final Dialog info = new Dialog(f, "Information", true);
			info.setSize(140, 90);
			info.setLocation(50, 50); // parent Frame , 이 아닌 화면기준의 위치
			info.setLayout(new FlowLayout());
			Label msg = new Label("빙고를 누르면 이김", Label.CENTER);
			Button ok = new Button("빙고");
			
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { // OK . 버튼을 누르면 수행됨
					// info.setVisible(false); // Dialog . 를 안보이게 한다
					winCheck=true;
					info.dispose(); // Dialog . 를 메모리에서 없앤다
					f.setVisible(false);

					try {
						out.writeUTF("400|게임종료");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// 이긴사람 정보 전달??;;

					// 빙고판 버튼 disable
						

				}
			});
			
			info.add(msg);
			info.add(ok);
			f.setVisible(true);
			info.setVisible(true); // Dialog . 를 화면에 보이게 한다
			}
		}
		

	}
	//@180526
	public void turnCheck(boolean turn) {
		setEnabled(turn);
	}

}
