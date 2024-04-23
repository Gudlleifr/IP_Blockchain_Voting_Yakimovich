package com.network;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

import com.blockchain.Block;

import static com.main.Main.decrypt;
import static java.nio.file.attribute.PosixFilePermission.*;


/**
 * ClientManager
 *Отвечает за все сетевые коммуникации на стороне клиента.
 *  *
 *  * В новом потоке запускается цикл получения сообщений, отправленных с сервера, и
 *  * отправляет его в основной поток для обработки.
 *  *
 *  * В основном потоке предусмотрен обработчик сообщений, обрабатывающий все входящие
 *  * Сообщения. Кроме того, у него есть интерфейсы, обслуживающие ProcessManager.
 *
 */
public class ClientManager extends NetworkManager {

	/* сокет, взаимодействующий с сервером */
	private Socket _socket = null;
	private Block genesisBlock;
	private ArrayList<SealedObject> blockList;
	private ArrayList<String> parties;
	private HashSet<String> hashVotes;
	private int prevHash=0;

	private int clientId;

	public ClientManager(String addr, int port) {
		try {
			_socket = new Socket(addr, port);
			System.out.println("Подключен к серверу: " + addr + ":" + port);
			genesisBlock=new Block(0, "", "", "");
			hashVotes=new HashSet<>();
			parties = new ArrayList<>();
			parties.add("Единая Россия");
			parties.add("ЛДПР");
			parties.add("КПРФ");

			blockList=new ArrayList<>();
			blockList.add(encrypt(genesisBlock));
		} catch (IOException e) {
			System.out.println("Не подключен к серверу " + addr + ":" + port);
			e.setStackTrace(e.getStackTrace());
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startClient() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Добро пожаловать на голосование ! ");
		String choice ="y";
		do{
			Block blockObj=null;

			String voterId= null;
			String voterName =null;
			String voteParty=null;

			try {
				System.out.print("Введите ID избирателя : ");
				voterId = br.readLine();
				System.out.print("Введите имя избирателя : ");
				voterName = br.readLine();

				System.out.println("Голосование за партии:");
				int voteChoice;

				do {
					for (int i=0 ;i<parties.size() ;i++) {
						System.out.println((i+1)+". "+ parties.get(i));
					}

					System.out.println("Отдайте ваш голос: ");
					voteParty=br.readLine();
					voteChoice=Integer.parseInt(voteParty);
//	                System.out.println("vote choice : "+ voteChoice);
					if(voteChoice>parties.size()||voteChoice<1)
						System.out.println("Пожалуйста, введите правильный индекс .");
					else
						break;
				}while(true);

				voteParty = parties.get(voteChoice-1);
				blockObj=new Block(prevHash, voterId, voterName, voteParty);

				if(checkValidity(blockObj)) {
					hashVotes.add(voterId);
					sendMsg(new MessageStruct( 1,encrypt(blockObj) ));

					prevHash=blockObj.getBlockHash();
					blockList.add(encrypt(blockObj));
					//добавить
				}
				else
				{
					System.out.println("Голосование недействительно.");
				}
				System.out.println("Проголосовать еще раз (y/n) ? ");
				choice=br.readLine();

			} catch (IOException e) {
				System.out.println("ОШИБКА: не удалось прочитать строку!");
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(choice.equals("y")||choice.equals("Y"));
		close();
	}

	public SealedObject encrypt(Block b) throws Exception
	{
		SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

		// Создать шифр
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		//Код для записи вашего объекта в файл
		cipher.init( Cipher.ENCRYPT_MODE, sks );

		return new SealedObject( b, cipher);
	}

	private boolean checkValidity(Block blockObj) {
		// TODO Auto-generated method stub
		if( hashVotes.contains((String)blockObj.getVoteObj().getVoterId() ))
			return false;
		else
			return true;
	}

	public void sendMsg(MessageStruct msg) throws IOException {
		sendMsg(_socket, msg);
	}

	// Закрыть сокет для выхода
	public void close() {

		String userHomePath = System.getProperty("user.home");
		String fileName;
		fileName=userHomePath+"/Desktop/blockchain_data";
		File f=new File(fileName);

		try
		{
			if(!f.exists())
				f.createNewFile();
			else {
				f.delete();
				f.createNewFile();
			}

			Files.setPosixFilePermissions(f.toPath(),
					EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_EXECUTE));
			System.out.println(fileName);

			ObjectOutputStream o=new ObjectOutputStream(new FileOutputStream(fileName,true));
			o.writeObject(blockList);

			o.close();

			_socket.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.exit(0);
	}

	@Override
	public void msgHandler(MessageStruct msg, Socket src) {
		switch (msg._code) {
			case 0:
				/* тип сообщения, отправляемого с сервера клиенту */
				// System.out.println((String)msg._content.toString()) ;
				try {

					blockList.add((SealedObject)msg._content);

					Block decryptedBlock=(Block) decrypt((SealedObject)msg._content);
					hashVotes.add(decryptedBlock.getVoteObj().getVoterId());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				/* тип сообщения, отправляемого из широковещательной рассылки всем клиентам */
				//сервер управляет этим
				break;
			case 2:
				clientId=(int)(msg._content);
			default:
				break;
		}
	}

	/*
	 * Запуск цикла для получения сообщений с сервера. Если при получении произойдет сбой,
	 * соединения нарушены. Закройте сокет и выйдите с -1.
	 */
	@Override
	public void run() {
		while(true) {
			try {
				receiveMsg(_socket);

			} catch (ClassNotFoundException | IOException e) {
				if(_socket.isClosed())
				{
					System.out.println("До свидания!");
					System.exit(0);
				}

				System.out.println("Соединение с сервером разорвано. Пожалуйста, перезапустите клиент.");
				close(_socket);
				System.exit(-1);
			}
		}
	}
}