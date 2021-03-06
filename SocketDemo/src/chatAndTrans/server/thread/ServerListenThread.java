package chatAndTrans.server.thread;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import javax.swing.*;

import chatAndTrans.server.entity.Node;
import chatAndTrans.server.entity.UserLinkList;

/**
 * 服务器端的侦听类
 * @author DossS
 *
 */
public class ServerListenThread extends Thread{
	ServerSocket server;
	
	JComboBox combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;	//用户链表
	
	Node client;
	ServerReceiveThread recvThread;
	
	public boolean isStop;
	
	/*
	 * 聊天服务器的用户上线与下线侦听类
	 */
	public ServerListenThread(ServerSocket server, JComboBox combobox, JTextArea textarea, JTextField textfield, UserLinkList userLinkList) {
		this.server = server;
		this.combobox = combobox;
		this.textarea = textarea;
		this.textfield = textfield;
		this.userLinkList = userLinkList;
		
		isStop = false;
	}
	
	public void run() {
		while(!isStop && !server.isClosed()) {
			try {
				client = new Node();
				client.setSocket(server.accept());
				client.setOutput(new ObjectOutputStream(client.getSocket().getOutputStream()));
				client.getOutput().flush();
				client.setInput(new ObjectInputStream(client.getSocket().getInputStream()));;
				client.setUsername((String)client.getInput().readObject());
				
				//显示提示信息
				combobox.addItem(client.getUsername());
				userLinkList.addUser(client);
				textarea.append("用户 " + client.getUsername() + " 上线" + "\n");
				textfield.setText("在线用户 " + userLinkList.getCount() + " 人\n");
				
				recvThread = new ServerReceiveThread(textarea, textfield, combobox, client, userLinkList);
				recvThread.start();
				
			} catch(Exception e) {
//				e.printStackTrace();
			}
		}
	}
}
