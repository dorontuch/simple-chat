import socket
import time
from threading import Thread

clients = []
skt = None

# send message function
# get the socket and the message to send


def send_message(client_socket, message):
    global clients
    # run on each the clients in the topple
    for clint in clients:
        # sends the message to the all the clients except the client who sent
        if clint is not client_socket:
            # encode the message in 'utf-8' and send
            clint.send(bytes(message, "utf-8"))


# The receive function listen to the socket and waits until new message entered
def receive_function(client_socket):
    while True:
        try:
            # read the data from the socket into the data variable
            data = client_socket.recv(1024)
            if not data:
                break
            # decode the message to 'utf-8' mode
            data = data.decode("utf-8")
            # call the function 'send_message' to the the message to the other clients
            send_message(client_socket, data)
        except Exception as e:
            print(str(e))
            # if client disconnect
            # remove the client from the clients list
            clients.remove(client_socket)
            break


# create connection
# open new socket to communicate
def create_connection():
    global skt
    try:
        # create socket with the IPV4 format and for streaming
        skt = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # set the ip for the 'localhost' and set the port
        skt.bind(("127.0.0.1", 21234))
        # set the number of connections
        skt.listen(5)
    except socket.error as e:
        # if error occurred trying to create the socket
        # retrying after 4 secs
        print("Socket Binding error:\n" + str(e) + "\n" + "Retrying...")
        time.sleep(4)
        create_connection()


def main():
    # call the function to open the socket
    create_connection()

    while True:
        try:
            client_socket, address = skt.accept()
            print(f"Connection from {address} has been established!")

            # for every client who connect
            # create new Thread to receive the data from the socket
            rcv = Thread(target=receive_function, args=(client_socket,), daemon=True)
            rcv.start()

            # save the new client in the list of the clients
            clients.append(client_socket)

        except Exception as e:
            print("Error accepting connection: ", e)


if __name__ == '__main__':
    main()
