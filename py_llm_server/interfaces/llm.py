from abc import ABC
from abc import abstractmethod

class LLM(ABC):

    @abstractmethod
    def calculate_Similarity(self, text1, text2):
        pass

    @abstractmethod
    def calculate_similarity_batch(self, texts, label):
        pass

    