from llms.bert import BERT
from llms.mistral import Mistral
from llms.lama import OllamaTests

    
def testBert():
    bert = BERT()
    bert.load('bert-base-uncased')

    text1 = "google-bert/bert-large-uncased - Similar to the base model but larger, which typically means it can perform better at the cost of requiring more computational resources. It's also uncased, so it treats text in a case-insensitive manner."
    text2 = "All animals are composed of cells, surrounded by a characteristic extracellular matrix composed of collagen and elastic glycoproteins.[22] During development, the animal extracellular matrix forms a relatively flexible framework upon which cells can move about and be reorganised, making the formation of complex structures possible. This may be calcified, forming structures such as shells, bones, and spicules.[23] In contrast, the cells of other multicellular organisms (primarily algae, plants, and fungi) are held in place by cell walls, and so develop by progressive growth.[24] Animal cells uniquely possess the cell junctions called tight junctions, gap junctions, and desmosomes.[25]"

    scoreBert = bert.calculate_Similarity(text1, text2)

    print("BERT score: " + str(scoreBert))

def testMistral():
    mist = Mistral()
    mist.load()

    text1 = "google-bert/bert-large-uncased - Similar to the base model but larger, which typically means it can perform better at the cost of requiring more computational resources. It's also uncased, so it treats text in a case-insensitive manner."
    text2 = "All animals are composed of cells, surrounded by a characteristic extracellular matrix composed of collagen and elastic glycoproteins.[22] During development, the animal extracellular matrix forms a relatively flexible framework upon which cells can move about and be reorganised, making the formation of complex structures possible. This may be calcified, forming structures such as shells, bones, and spicules.[23] In contrast, the cells of other multicellular organisms (primarily algae, plants, and fungi) are held in place by cell walls, and so develop by progressive growth.[24] Animal cells uniquely possess the cell junctions called tight junctions, gap junctions, and desmosomes.[25]"
    
    scoreMist = mist.calculate_Similarity(text1, text2)

    
    print("Score Mist: " + str(scoreMist))

    mist.chat("What is AI?")

def testOllama():
    ollama = OllamaTests()
    ollama.test_Simple_Chat("Why the sky is blue?")
        

if __name__ == "__main__":
    testOllama()