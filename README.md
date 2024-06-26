# Моделирование голосования на блокчейне

Этот репозиторий содержит программу для моделирования голосования с использованием технологии блокчейн через P2P сеть. Программа реализована на языке Java.

## Основной класс (`Main.java`)

Основной класс Main.java служит центральным компонентом фреймворка. Он предоставляет пользовательский интерфейс для проведения голосования, просмотра и подсчета голосов в блокчейне.

## Классы блокчейна (`Block.java`)

Класс Block.java определяет структуру блока в блокчейне. Он содержит информацию о голосующем, выбранной партии и хэшах предыдущего и текущего блоков.

## Классы взаимодействия с сетью (`ClientManager.java`, `ServerManager.java`, `NetworkManager.java`)

Классы ClientManager.java и ServerManager.java обрабатывают взаимодействие сети между клиентом и сервером. NetworkManager.java служит базовым классом для управления сетью.
## Шифрование и дешифрование (`decrypt()`)

Метод decrypt() в классе Main.java используется для дешифрования объектов с использованием алгоритма AES.

## Запуск программы

Программа предоставляет простое текстовое меню для взаимодействия с пользователем. Пользователи могут выбирать опции для голосования, просмотра голосов в блокчейне или подсчета голосов.

## Установка

1. Клонируйте репозиторий на свой локальный компьютер.
2. Убедитесь, что у вас установлена Java Runtime Environment (JRE).
3. Запустите программу, выполните Main.java


### Алгоритм действий пользователя при запуске программы:

1. **Запуск программы:**
    - Пользователь запускает программу, запустив файл `Main.java`.

2. **Главное меню:**
    - После запуска программы пользователь видит главное меню с несколькими вариантами действий.
    - Меню предоставляет следующие опции:
        - "Отдать голос"
        - "Просмотр голосов в блокчейне"
        - "Подсчет голосов"
        - "Выход"

3. **Выбор действия:**
    - Пользователь выбирает опцию, введя соответствующий номер с клавиатуры.

4. **Отдать голос:**
    - Если выбрана опция "Отдать голос", программа запросит пользователя выбрать роль: сервер или клиент.
    - Пользователь должен ввести соответствующую команду:
        - Для запуска сервера: `server PORT` (где `PORT` - порт для прослушивания, по умолчанию `6777`).
        - Для подключения к серверу: `client SERVER_ADDRESS PORT` (где `SERVER_ADDRESS` - адрес сервера, `PORT` - порт для подключения).
    - После ввода команды программа начнет работу сервера или клиента.

5. **Работа сервера:**
    - Если выбрана роль сервера, сервер будет ожидать подключения клиентов и принимать их голоса.
    - Как только клиент подключится, сервер начнет получать сообщения с голосами от клиентов и сохранять их в блокчейне.
    - После получения голоса сервер отправит его всем клиентам.

6. **Работа клиента:**
    - Если выбрана роль клиента, клиент попытается подключиться к серверу.
    - После успешного подключения клиент сможет отдать свой голос, введя свой ID, имя и выбрав партию.
    - Клиент также проверит валидность голоса перед отправкой на сервер.
    - После отправки голоса клиент получит подтверждение от сервера.

7. **Просмотр голосов в блокчейне:**
    - Если выбрана опция "Просмотр голосов в блокчейне", программа откроет файл с блокчейном и отобразит информацию о голосах.

8. **Подсчет голосов:**
    - Если выбрана опция "Подсчет голосов", программа выполнит подсчет голосов из блокчейна и выведет результаты.

9. **Выход:**
    - При выборе опции "Выход" программа завершит свою работу.


## Затрагиваемые темы

В программе использован следующий материал:


1. Языковые конструкции Java: Используются основные конструкции языка Java, такие как классы, методы, переменные, операторы и условные выражения.
2. Потоки ввода-вывода (I/O Streams): Программа использует потоки ввода-вывода для чтения и записи данных, особенно при работе с файлами блокчейна.
3. Многопоточность (Multithreading): Для обработки одновременных операций клиента и сервера используются многопоточные подходы.
4. Сетевое программирование: Программа использует сокеты для установления соединения между клиентом и сервером через P2P сеть.
5. Serialization (Сериализация): Программа использует сериализацию объектов Java для передачи данных между клиентом и сервером.
6. Шифрование и дешифрование: Для обеспечения безопасности данных используется алгоритм AES для шифрования и дешифрования объектов блокчейна.
7. Обработка исключений (Exception Handling): Используется для обработки исключительных ситуаций, которые могут возникнуть во время выполнения программы, таких как ошибки ввода-вывода или ошибки при работе с сетью.
8. Коллекции (Collections): Программа использует коллекции Java, такие как ArrayList и HashMap, для хранения и обработки данных, таких как блоки блокчейна и результаты голосования.
