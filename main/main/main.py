import threading

from Camera import Camera
from OCREngine import OCREngine
from FaceRecognitionEngine import FaceRecognitionEngine
from BroadcastTransmitter import BroadcastTransmitter

class main:

    def __init__(self):
        camera = Camera()
        ocrEngine = OCREngine()
        faceRecognitionEngine = FaceRecognitionEngine(camera.rectangleMatrix)
        broadcastTransmitter = BroadcastTransmitter()

        threading.Thread(target = camera.start).start()     # running camera in a separate thread to avoid camera FPS loss...
        threading.Thread(target = self.performRecognition, args = (camera, ocrEngine,)).start()      # running optical character recognition in a separate thread...
        threading.Thread(target = self.performRecognition, args = (camera, faceRecognitionEngine,)).start()      # running face recognition in another thread...
        threading.Thread(target = broadcastTransmitter.start, args = (camera,)).start()

        # threading.Thread(target = camera.start).start()     # running camera in a separate thread to avoid camera FPS loss...
        '''counter = 1

        while True:
            if camera.image is not None:
                faceRecognitionEngine.addNewFace(counter, "shahadul-alam", camera.image)
                counter = counter + 1

                if (counter == 201) or (camera.isStarted == False):
                    break
        
        faceRecognitionEngine.buildDatabase()

        if raw_input() == "":
            camera.stop()
            '''
        # camera.start()      # running camera in main thread...
    
    def performRecognition(self, camera, object):
        while True:
            if camera.grayscale is not None:
                object.recognize(camera.grayscale)     # first trying to recognize characters as this task will take less time than face recognition...

                if camera.isStarted == False:
                    break

if __name__ == '__main__':      # main method... execution of this program starts from here...
    main()