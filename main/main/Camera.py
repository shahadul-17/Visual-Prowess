import cv2

from ImageProcessor import ImageProcessor
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
        self.grayscale = None
        self.rectangleMatrix = []        # 2D list of "rectangle" objects...

        if Utility.hasPiCamera:
            camera = PiCamera()
            camera.resolution = (640, 480)
            camera.framerate = 16

            self.rgbArray = PiRGBArray(camera, size = camera.resolution)
            self.iterator = camera.capture_continuous(self.rgbArray, format = "bgr", use_video_port = True)
        else:
            camera = cv2.VideoCapture(0)
        
        print "camera initialized successfully..."

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
                self.image = i.array
                self.grayscale = ImageProcessor.convertImageToGrayscale(self.image)
                self.image = self.__drawRectangle(self.rectangleMatrix, i.array)

                self.rgbArray.truncate(0)
                ImageProcessor.showPreview(Utility.title, self.image)

                if cv2.waitKey(1) == 27:
                    self.stop()

                if self.isStarted == False:
                    break
        else:
            while self.isStarted:
                framesGrabbed, self.image = camera.read(0)
                self.grayscale = ImageProcessor.convertImageToGrayscale(self.image);
                self.image = self.__drawRectangle(self.rectangleMatrix, self.image)
                
                ImageProcessor.showPreview(Utility.title, self.image)
                
                if cv2.waitKey(1) == 27:
                    self.stop()

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