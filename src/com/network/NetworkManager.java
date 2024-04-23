package com.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Сетевой менеджер
 *
 * Базовый класс ServerManager и ClientManager для обеспечения сетевых операций.
 */
public abstract class NetworkManager implements Runnable{
	
	/*
	 * Отправить сообщение в сокет
	 */
	public void sendMsg(Socket socket, MessageStruct msg) 
			throws IOException {
		ObjectOutputStream out;
		
		out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(msg);
	}
	

	public void receiveMsg(Socket socket) 
			throws ClassNotFoundException, IOException {
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		Object inObj = inStream.readObject();
		
		if (inObj instanceof MessageStruct) {
			MessageStruct msg = (MessageStruct) inObj;
			msgHandler(msg, socket);
		}
		
	}
	
	/*
	 * Закрыть сокет
	 */
	public void close(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Интерфейс для реализации ServerManager и ClientManager, обрабатывающий все входящие сообщения.
	 */
	public abstract void msgHandler(MessageStruct msg, Socket src);
}
