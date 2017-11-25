import os

class Utility:

    hasPiCamera = False
    timeout = 10        # timeout in seconds...
    bufferLength = 1024
    port = 60499
    title = "Visual Prowess"
    dodgerBlueColor = (255, 144, 30)

    @staticmethod
    def createDirectory(path):
        directoryCreated = False

        if not os.path.exists(path):
            os.makedirs(path)

            directoryCreated = True
        
        return directoryCreated
    
    @staticmethod
    def getAbsolutePath(relativePath):
        if os.name == "nt":
            relativePath = relativePath.replace("/", "\\")
        else:
            relativePath = relativePath.replace("\\", "/")
        
        return os.path.join(os.path.dirname(os.path.realpath(relativePath)), relativePath[(relativePath.rfind(os.path.sep) + 1):])