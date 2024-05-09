from abc import ABC
from abc import abstractmethod

class ILLMHandler(ABC):

    @abstractmethod
    def load(self):
        pass

    @abstractmethod
    def query(self):
        pass

    @abstractmethod
    def getSimilarityScore(self):
        pass