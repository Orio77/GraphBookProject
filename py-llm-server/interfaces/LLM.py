from abc import ABC
from abc import abstractmethod

class LLM(ABC):

    @abstractmethod
    def load(self):
        pass

    