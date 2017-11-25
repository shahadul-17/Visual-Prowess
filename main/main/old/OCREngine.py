import cv2
import numpy
import operator

from Utility import Utility
from Rectangle import Rectangle
from Information import Information
from ImageProcessor import ImageProcessor

class OCREngine:

    classifications = None
    flattenedImages = None
    kNearest = None

    RESIZED_IMAGE_WIDTH = 0
    RESIZED_IMAGE_HEIGHT = 0

    def __init__(self, rectangleMatrix):
        global RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT, classifications, flattenedImages, kNearest

        self.rectangles = []        # list of valid rectangles...
        self.text = ""

        RESIZED_IMAGE_WIDTH = 20
        RESIZED_IMAGE_HEIGHT = 30

        rectangleMatrix.append(self.rectangles)

        classifications = numpy.loadtxt(Utility.getAbsolutePath("data\\optical-character-recognition\\classifications.txt"), numpy.float32)
        classifications = classifications.reshape((classifications.size, 1))    # reshape numpy array to 1d, necessary to pass to call to train
        flattenedImages = numpy.loadtxt(Utility.getAbsolutePath("data\\optical-character-recognition\\flattened-images.txt"), numpy.float32)

        kNearest = cv2.ml.KNearest_create()
        kNearest.train(flattenedImages, cv2.ml.ROW_SAMPLE, classifications)
    
    def recognizeCharacters(self, image):
        global RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT, kNearest

        self.text = ""
        informations = []

        grayBlurredImage = cv2.GaussianBlur(ImageProcessor.convertImageToGray(image), (5, 5), 0)     # convert the image to gray and blur it...
        thresholdedImage = cv2.adaptiveThreshold(grayBlurredImage, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 11, 2)
        image, contours, hierarchy = cv2.findContours(thresholdedImage.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        for contour in contours:
            information = Information()
            information.contour = contour
            information.contourArea = cv2.contourArea(information.contour)
            
            if information.isContourValid():
                information.boundingRectangle = cv2.boundingRect(information.contour)

                information.calculateRectangle()
                informations.append(information)
        
        informations.sort(key = operator.attrgetter("rectangle.x"))         # sort contours from left to right...

        del self.rectangles[:]      # clearing the list "rectangles"...

        for information in informations:
            image = thresholdedImage[information.rectangle.y:information.rectangle.y + information.rectangle.height, information.rectangle.x : information.rectangle.x + information.rectangle.width]
            image = cv2.resize(image, (RESIZED_IMAGE_WIDTH, RESIZED_IMAGE_HEIGHT))
            numpyArray = numpy.float32(image.reshape((1, RESIZED_IMAGE_WIDTH * RESIZED_IMAGE_HEIGHT)))    # convert type of numpy array from int to float...
            returnValue, result, neighborResponse, distance = kNearest.findNearest(numpyArray, k = 1)   # result = vector with results of prediction (regression or classification) for each input sample... it is a single-precision floating-point vector with number_of_samples elements...

            if distance > 4712875:
                continue
            
            self.text = self.text + str(chr(int(result[0][0])))

            self.rectangles.append(information.rectangle)

            print "=" + str(distance) + "="        # for debugging purpose...
                
        if len(self.text.strip()) != 0:
            print self.text + "\n"