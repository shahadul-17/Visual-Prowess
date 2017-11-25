import cv2
import numpy

class ImageProcessor:

    @staticmethod
    def loadImage(fileName):
        return cv2.imread(fileName)

    @staticmethod
    def storeImage(path, image):
        return cv2.imwrite(path, image)
    
    @staticmethod
    def convertImageToGray(image):
        return cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    @staticmethod
    def resizeImage(width, image):
        ratio = (width * 1.0) / image.shape[1]
        dimension = (width, int(image.shape[0] * ratio))
        
        return cv2.resize(image, dimension, interpolation = cv2.INTER_AREA)

    @staticmethod
    def getBufferFromImage(image):
        returnValue, image = cv2.imencode(".jpg", image, [int(cv2.IMWRITE_JPEG_QUALITY), 90])

        return numpy.array(image).tobytes()
    
    @staticmethod
    def __getOrderedCoordinates(cornerCoordinates):
        coordinates = numpy.zeros((4, 2), dtype = "float32")

        _sum = cornerCoordinates.sum(axis = 1)
        coordinates[0] = cornerCoordinates[numpy.argmin(_sum)]
        coordinates[2] = cornerCoordinates[numpy.argmax(_sum)]

        difference = numpy.diff(cornerCoordinates, axis = 1)
        coordinates[1] = cornerCoordinates[numpy.argmin(difference)]
        coordinates[3] = cornerCoordinates[numpy.argmax(difference)]

        return coordinates

    @staticmethod
    def preprocessImage(image, cornerCoordinates):
        coordinates = ImageProcessor.__getOrderedCoordinates(cornerCoordinates)
        (topLeft, topRight, bottomRight, bottomLeft) = coordinates
        
        width = max(int(numpy.sqrt(((bottomRight[0] - bottomLeft[0]) ** 2) + ((bottomRight[1] - bottomLeft[1]) ** 2))), int(numpy.sqrt(((topRight[0] - topLeft[0]) ** 2) + ((topRight[1] - topLeft[1]) ** 2))))
        height = max(int(numpy.sqrt(((topRight[0] - bottomRight[0]) ** 2) + ((topRight[1] - bottomRight[1]) ** 2))), int(numpy.sqrt(((topLeft[0] - bottomLeft[0]) ** 2) + ((topLeft[1] - bottomLeft[1]) ** 2))))

        return cv2.warpPerspective(image, cv2.getPerspectiveTransform(coordinates, numpy.array([ [0, 0], [width - 1, 0], [width - 1, height - 1], [0, height - 1] ], dtype = "float32")), (width, height))

    @staticmethod
    def getCornerCoordinates(image):
        image = cv2.GaussianBlur(image, (5, 5), 0)
        image = cv2.threshold(image, 45, 255, cv2.THRESH_BINARY)[1]     # 170
        image = cv2.erode(image, None, iterations = 2)
        image = cv2.dilate(image, None, iterations = 2)

        contours = cv2.findContours(image, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        if cv2.__version__.startswith("2."):
            contours = contours[0]
        else:
            contours = contours[1]
        
        contour = max(contours, key=cv2.contourArea)

        coordinates = cv2.approxPolyDP(contour, cv2.arcLength(contour, True) * 0.02, True)

        return numpy.array([ (coordinates[0][0][0], coordinates[0][0][1]), (coordinates[1][0][0], coordinates[1][0][1]), (coordinates[2][0][0], coordinates[2][0][1]), (coordinates[3][0][0], coordinates[3][0][1]) ], dtype = "float32")