import cv2
import os
import pytesseract

from ImageProcessor import ImageProcessor
from PIL import Image
from Utility import Utility

class OCREngine:
    
    def recognizeCharacters(self, image):
        image = ImageProcessor.convertImageToGray(image)
        cornerCoordinates = ImageProcessor.getCornerCoordinates(image)
        image = ImageProcessor.preprocessImage(image, cornerCoordinates)
        image = cv2.threshold(image, 0, 255, cv2.THRESH_BINARY)[1]
        image = cv2.medianBlur(image, 3)

        ImageProcessor.storeImage(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg"), image)
        
        if os.name == "nt":
            text = pytesseract.image_to_string(Image.open(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg")), lang="eng", config="--tessdata-dir \"C:/Program Files (x86)/Tesseract-OCR/tessdata\"").strip()
        else:
            text = pytesseract.image_to_string(Image.open(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg"))).strip()

        if len(text) != 0:
            print text