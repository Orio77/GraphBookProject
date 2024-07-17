from abc import ABC
from abc import abstractmethod

class LLM(ABC):

    @abstractmethod
    def calculate_similarity_batch(self, texts, label):
        pass

    @abstractmethod
    def calculate_Concept_Scores(self, texts, concept, label):
        pass

    