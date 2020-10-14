import socket
import time
import serial
ser =  serial.Serial('COM3',9600)



serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv.bind(('0.0.0.0', 6000))
serv.listen(5)



def repea():
    conn, addr = serv.accept()
    while True:
        sentence = conn.recv(1024).decode()
        line = ser.readline()
        print(line)
        if sentence == 'aa':
            repea()
        elif sentence == 'a':
            conn.send(line)
            time.sleep(2)

repea()