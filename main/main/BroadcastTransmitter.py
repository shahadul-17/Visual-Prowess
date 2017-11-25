import select
import socket
import threading

from Utility import Utility
from ImageProcessor import ImageProcessor

class BroadcastTransmitter:

    def __init__(self):
        print "initializing broadcast transmitter..."

        self.addresses = []
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)      # creating UDP socket...
        self._socket.setblocking(0)
        self._socket.bind(("", Utility.port))

        print "broadcast transmitter initialized successfully..."
    
    def start(self, camera):
        print "starting broadcast transmitter..."

        threading.Thread(target = self.__transmit, args = (camera,)).start()

        print "waiting for client(s)..."

        while camera.isStarted:     # waits for client(s)...
            try:
                _select = select.select([self._socket], [], [], Utility.timeout)    # implementing timeout...

                if _select[0]:
                    data, address = self._socket.recvfrom(Utility.bufferLength)

                    if data == "start":
                        print "client connected from [", str(address[0]), ":", str(address[1]), "]"

                        if address not in self.addresses:
                            self.addresses.append(address)
                    elif data == "stop":
                        print "client disconnected from [", str(address[0]), ":", str(address[1]), "]"

                        if address in self.addresses:
                            self.addresses.remove(address)
                    
                    print "currently", str(len(self.addresses)), "client(s) is/are connected..."
            except:
                pass
    
    def __transmit(self, camera):
        print "starting transmission..."

        byteArray = bytearray()
        counter = 0

        while camera.isStarted:
            if len(self.addresses) > 0:
                buffer = ImageProcessor.getBufferFromImage(camera.image)
                remaining = bufferLength = len(buffer)

                for i in range(0, bufferLength):
                    byteArray.append(buffer[i])

                    remaining = remaining - 1

                    if (counter == Utility.bufferLength) or (remaining == 0):
                        for address in self.addresses:
                            self._socket.sendto(byteArray, address)

                            if remaining == 0:
                                self._socket.sendto("reset", address)       # "reset" is a flag to let client know that one frame is sent...
                        
                        byteArray = bytearray()
                        counter = 0
                    
                    counter = counter + 1
                    i = i + 1
        
        self._socket.close()