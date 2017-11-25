import cv2
import time

from Utility import Utility

try:            # adding support for Raspberry Pi...
    from picamera import PiCamera
    from picamera.array import PiRGBArray

    Utility.hasPiCamera = True
except ImportError:
    Utility.hasPiCamera = False

class Camera:

    camera = None               # global variable...

    def __init__(self):
        print "initializing camera..."

        global camera

        self.isStarted = True
        self.image = None
        self.rectangleMatrix = []        # 2D list of "rectangle" objects...

        if Utility.hasPiCamera:
            camera = PiCamera()
            camera.resolution = (640, 480)
            camera.framerate = 32

            self.rgbArray = PiRGBArray(camera, size = camera.resolution)
            self.iterator = camera.capture_continuous(self.rgbArray, format = "bgr", use_video_port = True)
        else:
            camera = cv2.VideoCapture(0)
        
        print "camera initialized successfully..."

    def __showPreview(self, title, image):
        cv2.imshow(title, image)

        if cv2.waitKey(1) == 27:
            self.stop()
    
    def __drawRectangle(self, rectangleMatrix, image):
        for rectangles in rectangleMatrix:
            for rectangle in rectangles:
                cv2.rectangle(image, (rectangle.x, rectangle.y), (rectangle.x + rectangle.width, rectangle.y + rectangle.height), Utility.dodgerBlueColor, 2)

        return image

    def start(self):
        print "starting camera..."

        global camera

        self.isStarted = True

        if Utility.hasPiCamera:
            for i in self.iterator:
                self.image = self.__drawRectangle(self.rectangleMatrix, i.array)
                
                self.rgbArray.truncate(0)
                self.__showPreview(Utility.title, self.image)

                if self.isStarted == False:
                    break

                time.sleep(0.20)    # sleep for 20 milliseconds...
        else:
            while self.isStarted:
                framesGrabbed, self.image = camera.read(0)
                self.image = self.__drawRectangle(self.rectangleMatrix, self.image)
                
                self.__showPreview(Utility.title, self.image)
                time.sleep(0.20)    # sleep for 20 milliseconds...
    
    def stop(self):
        print "stopping camera..."

        global camera

        self.isStarted = False

        if Utility.hasPiCamera:
            self.iterator.close()
            self.rgbArray.close()
            camera.close()
        else:
            camera.release()
        
        cv2.destroyAllWindows()

        print "camera stopped successfully..."