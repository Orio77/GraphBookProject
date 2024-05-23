from abc import ABC
from abc import abstractmethod

class ILLMHandler(ABC):

    @abstractmethod
    def query(self):
        pass

    @abstractmethod
    def get_Similarity_Score(self):
        pass

    @abstractmethod
    def get_similarity_Scores(self, texts, label):
        pass