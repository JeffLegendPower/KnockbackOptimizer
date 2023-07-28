import json
import socket
import numpy as np
from scipy.optimize import differential_evolution
import matplotlib.pyplot as plt
from matplotlib import cm


class TCPClient:
    def __init__(self, address="localhost", port=271):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((address, port))

    def send_message(self, message):
        self.socket.sendall(bytes(json.dumps(message), encoding="utf-8"))

    def receive_message(self):
        data = b""
        while True:
            chunk = self.socket.recv(4096)
            data += chunk
            if len(chunk) < 4096:
                break
        return json.loads(data.decode("utf-8").rstrip('\r\n'))

    def close(self):
        self.socket.close()


def kb_func(p):
    horizontal, \
        vertical, \
        inheritanceStrengthHorizontal, \
        groundHorizontal, \
        groundVertical, \
        sprintHorizontal, \
        verticalLimit, \
        sprintVertical, \
        attackerSlowdown = p

    client.send_message({
        "horizontal": horizontal,
        "vertical": vertical,
        "inheritanceStrengthHorizontal": inheritanceStrengthHorizontal,
        "groundHorizontal": groundHorizontal,
        "groundVertical": groundVertical,
        "sprintHorizontal": sprintHorizontal,
        "verticalLimit": verticalLimit,
        "sprintVertical": sprintVertical,
        "attackerSlowdown": attackerSlowdown,
    })

    return client.receive_message()["feedback"]


def callback(xk, convergence):
    print(xk, convergence)


# make main function
if __name__ == "__main__":
    # create client
    client = TCPClient()

    # load differential evolution
    bounds = [
        [2, 4.3],  # horizontal
        [0, 3.5],  # vertical
        [0, 2.5],  # inheritanceStrengthHorizontal
        [0.5, 1.3],  # groundHorizontal
        [0.5, 1.2],  # groundVertical
        [0.8, 1.3],  # sprintHorizontal
        [0.5, 5],  # verticalLimit
        [0.8, 1.2],  # sprintVertical
        [0, 0.65],  # attackerSlowdown
    ]

    result = differential_evolution(kb_func, bounds, disp=True, callback=callback, popsize=5)

    print(result)
