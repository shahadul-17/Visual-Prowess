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
        threading.Thread(target = self.opticalCharacterRecognition, args = (camera, ocrEngine,)).start()      # running optical character recognition in a separate thread...
        threading.Thread(target = self.faceRecognition, args = (camera, faceRecognitionEngine,)).start()      # running face recognition in another thread...
        threading.Thread(target = broadcastTransmitter.start, args = (camera,)).start()

        if raw_input() == "":
            camera.stop()

        # camera.start()      # running camera in main thread...

        '''counter = 1

        while True:
            if camera.image is not None:
                faceRecognitionEngine.addNewFace(counter, "shahadul-alam", camera.image)
                counter = counter + 1

                if (counter == 21) or (camera.isStarted == False):
                    break
        
        faceRecognitionEngine.buildDatabase()
        '''
        
    
    def opticalCharacterRecognition(self, camera, ocrEngine):
        while True:
            if camera.image is not None:
                ocrEngine.recognizeCharacters(camera.image)     # first trying to recognize characters as this task will take less time than face recognition...

                if camera.isStarted == False:
                    break
    
    def faceRecognition(self, camera, faceRecognitionEngine):
        while True:
            if camera.image is not None:
                faceRecognitionEngine.recognizeFaces(camera.image)      # this takes time...

                if camera.isStarted == False:
                    break
    
if __name__ == '__main__':      # main method... execution of this program starts from here...
    main()