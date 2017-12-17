import cv2
import os
import pytesseract

from ImageProcessor import ImageProcessor
from Information import Information
from PIL import Image
from TextToSpeech import TextToSpeech
from Utility import Utility

class OCREngine:
    
    # def __init__(self):
        # textToSpeech = TextToSpeech()

    def recognize(self, image):
        tempImage = cv2.medianBlur(image.copy(), 15)
        returnValue, tempImage = cv2.threshold(tempImage, 127, 255, cv2.THRESH_BINARY)

        im2, contours, hierarchy = cv2.findContours(tempImage, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

        if len(contours) > 0:
            contour = max(contours, key=cv2.contourArea)

            try:
                [x, y, w, h] = cv2.boundingRect(contour)

                # image = cv2.adaptiveThreshold(image, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 11, 2)
                image = image[y:(y + h), x:(x + w)]

                ImageProcessor.storeImage(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg"), image)
        
                if os.name == "nt":
                    text = pytesseract.image_to_string(Image.open(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg")), lang="eng", config="--tessdata-dir \"C:/Program Files (x86)/Tesseract-OCR/tessdata\"").strip()
                else:
                    text = pytesseract.image_to_string(Image.open(Utility.getAbsolutePath("data\\optical-character-recognition\\temporary-files\\temp.jpg"))).strip()

                if len(text) != 0:
                    try:
                        print text

                        # textToSpeech.speak(text);

                    except Exception:
                        pass

                ImageProcessor.showPreview("OCR", image)
        
                if cv2.waitKey(1) == 27:
                    pass
            except Exception:
                pass