import pyttsx

class TextToSpeech:

    def __init__(self):
        self.textToSpeech = pyttsx.init()
        self.textToSpeech.setProperty('rate', 70)

        voice = self.textToSpeech.getProperty('voices')[0]
        self.textToSpeech.setProperty('voice', voice.id)

    def speak(self, text):
        self.textToSpeech.say(text)
        self.textToSpeech.runAndWait()