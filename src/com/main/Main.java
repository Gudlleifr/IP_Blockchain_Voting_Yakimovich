package com.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.blockchain.Block;
import com.network.ClientManager;
import com.network.ServerManager;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.System.exit;
import static java.lang.System.lineSeparator;

/**
 Основной класс небольшого фреймворка для моделирования голосования с использованием блокчейна через P2P сеть.

 Поддерживается двусторонняя миграция между сервером и клиентом с использованием JAVA Serialization/Reflection и Socket.

 Подробный дизайн системы, сценарии использования и ограничения описаны в отчете.
 */
public class Main {

    private static final String DEFAULT_SERVER_ADDR = "localhost";
    private static final int DEFAULT_PORT = 6777;

    /*
     * Все начинается здесь!
     */
    public static void main(String[] args) {
//        int clientId=0;
        System.out.println(" ----- Главное меню ----- \n");
        System.out.println("1. Отдать голос");
        System.out.println("2. Просмотр голосов в блокчейне");
        System.out.println("3. Подсчет голосов");
        System.out.println("0. Выход\n");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите свой выбор: ");
        int ch = scanner.nextInt();

        if(ch == 1)
        {
            System.out.println("\n ----- Голосование ----- \n");
            System.out.println("Пожалуйста, выберите роль, которой вы хотите быть: сервер или клиент.");
            System.out.println("server PORT - порт для прослушивания; «6777» — порт по умолчанию.");
            System.out.println("client SERVER_ADDRESS PORT - Адрес сервера и порт для подключения; «localhost:6777» — это комбинация адреса и prt по умолчанию.");
            System.out.println("Обязательно сначала запустите сервер, а затем запустите клиент для подключения к нему.");
            System.out.println("> ---------- ");

            Scanner in = new Scanner(System.in);
            String line = in.nextLine();
            String[] cmd = line.split("\\s+");

            if (cmd[0].contains("s"))
            {   // сервер выбран

                /* работа сервера */
                int port = DEFAULT_PORT;
                if (cmd.length > 1) {
                    try {
                        port = Integer.parseInt(cmd[1]);
                    } catch(NumberFormatException e) {
                        System.out.println("Ошибка: порт не является числом!");
                        in.close();
                        return;
                    }
                }

                ServerManager _svrMgr =new ServerManager(port);
                new Thread(_svrMgr).start();


            }
            else if (cmd[0].contains("c"))
            {
                //выбран клиент

                /* работа клиента*/
                String svrAddr = DEFAULT_SERVER_ADDR;
                int port = DEFAULT_PORT;
                if (cmd.length > 2) {
                    try {
                        svrAddr = cmd[1];
                        port = Integer.parseInt(cmd[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("Ошибка: порт не является числом!");
                        in.close();
                        return;
                    }
                }

                ClientManager _cltMgr = new ClientManager(svrAddr, port);

                /* новая тема для получения сообщений */
                new Thread(_cltMgr).start();

                _cltMgr.startClient();
            }
            else {
                showHelp();
                in.close();
                return;
            }
            in.close();
        }

        // Просмотр голосов
        else if(ch == 2)
        {
            System.out.println("\n ----- Отображение голосов -----\n");

            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName=userHomePath+"/Desktop/blockchain_data";
            File f=new File(fileName);

            try
            {
                if(!f.exists())
                    System.out.println("Файл блокчейна не найден");

                ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileName));

                ArrayList<SealedObject> arr=(ArrayList<SealedObject>) in.readObject();
                for(int i=1;i<arr.size();i++) {
                    System.out.println(decrypt(arr.get(i)));
                }
                in.close();

                System.out.println("-------------------------\n");

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        // Подсчет голосов
        else if(ch == 3)
        {
            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName=userHomePath+"/Desktop/blockchain_data";
            File f=new File(fileName);

            try
            {
                if(!f.exists())
                    System.out.println("Пожалуйста, сначала отдайте голос !");

                else
                {
                    System.out.println();
                    System.out.println("-------------------------");
                    System.out.println("Число голосов: ");
                    ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileName));

                    ArrayList<SealedObject> arr=(ArrayList<SealedObject>) in.readObject();
                    HashMap<String,Integer> voteMap = new HashMap<>();

                    for(int i=1; i<arr.size(); i++)
                    {
                        Block blk = (Block) decrypt(arr.get(i));
                        String key = blk.getVoteObj().getVoteParty();

                        voteMap.put(key,0);
                    }

                    for(int i=1;i<arr.size();i++) {
                        Block blk = (Block) decrypt(arr.get(i));
                        String key = blk.getVoteObj().getVoteParty();

                        voteMap.put(key, voteMap.get(key)+1);
                    }
                    in.close();

                    for(Map.Entry<String, Integer> entry : voteMap.entrySet()) {
                        System.out.println(entry.getKey() + " : " + entry.getValue());
                    }

                    System.out.println("-------------------------\n");
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        else if(ch == 0)
            exit(0);
    }

    public static void showHelp() {
        System.out.println("Перезагрузите компьютер и выберите роль сервера или клиента.");
        exit(0);
    }

    public static Object decrypt(SealedObject sealedObject) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

        // Создать шифр
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks);

        try {
//    		System.out.println(sealedObject.getObject(cipher));
            return sealedObject.getObject(cipher);
        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
