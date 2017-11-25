import cv2
import numpy
import os

from Utility import Utility
from Rectangle import Rectangle
from ImageProcessor import ImageProcessor

class FaceRecognitionEngine:

    faceCascadeClassifier = None

    def __init__(self, rectangleMatrix):
        global faceCascadeClassifier

        databasePath = Utility.getAbsolutePath("data\\face-recognition\\face-recognition-database.xml")

        if cv2.__version__ >= "3.3.0":
            self.faceRecognizer = cv2.face.LBPHFaceRecognizer_create()
            self.faceRecognizer.read(databasePath)
        else:
            self.faceRecognizer = cv2.face.createLBPHFaceRecognizer()
            self.faceRecognizer.load(databasePath)

        self.rectangles = []        # list of valid rectangles...

        rectangleMatrix.append(self.rectangles)

        if cv2.__version__ <= "3.1.0":
            self.predictCollector = cv2.face.MinDistancePredictCollector()
        
        faceCascadeClassifier = cv2.CascadeClassifier(Utility.getAbsolutePath("data\\face-recognition\\face-cascade-classifier.xml"))
    
    def detectFaces(self, image):
        global faceCascadeClassifier

        return faceCascadeClassifier.detectMultiScale(image, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))

    def countFaces(self, faces):
        return len(faces)

    def cropFaces(self, faces, image):
        croppedImages = []      # an empty list of cropped images...

        for (x, y, width, height) in faces:
            croppedImages.insert(len(croppedImages), image[y:(y + height), x:(x + width)])
        
        return croppedImages
    
    def createFaceRecognizer(self):
        if cv2.__version__ >= "3.3.0":
            return cv2.face.LBPHFaceRecognizer_create()
        else:
            return cv2.face.createLBPHFaceRecognizer()

    def addNewFace(self, ID, name, image):      # improvement needed...
        Utility.createDirectory(Utility.getAbsolutePath("data\\face-recognition\\faces\\" + name))
        
        grayImage = ImageProcessor.convertImageToGray(image)
        faces = self.detectFaces(grayImage)
        facesDetected = self.countFaces(faces)

        if facesDetected == 1:
            croppedImage = self.cropFaces(faces, grayImage)[0]
            croppedImage = ImageProcessor.resizeImage(200, croppedImage)
            cv2.imwrite(Utility.getAbsolutePath("data\\face-recognition\\faces\\" + name + "\\" + str(ID) + ".jpg"), croppedImage)
            
            print Utility.getAbsolutePath("data\\face-recognition\\faces\\" + name + "\\" + str(ID) + ".jpg")
    
    def buildDatabase(self):
        print "initializing..."

        ID = 0
        facesDetected = 0
        subdirectoryNames = os.listdir(Utility.getAbsolutePath("data\\face-recognition\\faces"))
        listIDs = []
        listFaces = []

        faceRecognizer = self.createFaceRecognizer()

        print "organizing database..."

        for subdirectoryName in subdirectoryNames:
            files = os.listdir(Utility.getAbsolutePath("data\\face-recognition\\faces\\" + subdirectoryName))

            for _file in files:
                image = ImageProcessor.loadImage(Utility.getAbsolutePath("data\\face-recognition\\faces\\" + subdirectoryName + "\\" + _file))
                grayImage = ImageProcessor.convertImageToGray(image)
                faces = self.detectFaces(grayImage)
                facesDetected = self.countFaces(faces)

                print "faces detected = ", facesDetected

                if facesDetected == 1:      # the image must contain only one face...
                    print ID,"data\\faces\\" + subdirectoryName + "\\" + _file
                    listIDs.append(ID)
                    croppedImage = self.cropFaces(faces, grayImage)[0]
                    listFaces.append(numpy.array(croppedImage, 'uint8'))
                
                facesDetected = 0
            # loop ends here...
            
            ID += 1
        # loop ends here...

        faceRecognizer.train(listFaces, numpy.array(listIDs))
        faceRecognizer.save(Utility.getAbsolutePath("data\\face-recognition\\face-recognition-database.xml"))

        print "operation completed successfully..."
    
    def recognizeFaces(self, image):
        names = os.listdir(Utility.getAbsolutePath("data\\face-recognition\\faces"))
        grayImage = ImageProcessor.convertImageToGray(image)
        faces = self.detectFaces(grayImage)
        facesDetected = self.countFaces(faces)

        del self.rectangles[:]      # clearing the list "rectangles"...

        if facesDetected > 0:
            for (x, y, width, height) in faces:
                rectangle = Rectangle()     # creating new instance of "Rectangle" class...
                rectangle.x = x
                rectangle.y = y
                rectangle.width = width
                rectangle.height = height

                self.rectangles.append(rectangle)

                grayImage = ImageProcessor.resizeImage(200, grayImage[y:(y + height), x:(x + width)])

                if cv2.__version__ <= "3.1.0":
                    self.faceRecognizer.predict(grayImage, self.predictCollector)

                    ID = self.predictCollector.getLabel()
                    confidence = self.predictCollector.getDist()
                else:
                    ID, confidence = self.faceRecognizer.predict(grayImage)
                
                if confidence < 50.0:
                    print "The person is '" + names[ID] + "' with ID = " + str(ID) + " with confidence = " + str(confidence)
                else:
                    print "The person is Unknown..."