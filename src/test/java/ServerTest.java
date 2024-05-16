import org.junit.jupiter.api.Test;

import com.graphbook.server.AISimilarityClient;

public class ServerTest {
    
    @Test
    public void testGetResponse() {
        try {
            System.out.println("Response:");
            String text1 = """
                Animals are multicellular, eukaryotic organisms in the biological kingdom Animalia. With few exceptions, animals consume organic material, breathe oxygen, have myocytes and are able to move, can reproduce sexually, and grow from a hollow sphere of cells, the blastula, during embryonic development. Animals form a clade, meaning that they arose from a single common ancestor.

                Over 1.5 million living animal species have been described, of which around 1.05 million are insects, over 85,000 are molluscs, and around 65,000 are vertebrates. It has been estimated there are as many as 7.77 million animal species on Earth. Animal body lengths range from 8.5 μm (0.00033 in) to 33.6 m (110 ft). They have complex ecologies and interactions with each other and their environments, forming intricate food webs. The scientific study of animals is known as zoology, and the study of animal behaviors is known as ethology.
                
                Most living animal species belong to the infrakingdom Bilateria, a highly proliferative clade whose members have a bilaterally symmetric body plan. The vast majority belong to two large superphyla: the protostomes, which includes organisms such as the arthropods, molluscs, flatworms, annelids and nematodes; and the deuterostomes, which include the echinoderms, hemichordates and chordates, the latter of which contains the vertebrates. The simple Xenacoelomorpha have an uncertain position within Bilateria.
                    """;
            String text2 = """
                A black hole is a region of spacetime where gravity is so strong that nothing, including light and other electromagnetic waves, is capable of possessing enough energy to escape it.[2] Einstein's theory of general relativity predicts that a sufficiently compact mass can deform spacetime to form a black hole.[3][4] The boundary of no escape is called the event horizon. A black hole has a great effect on the fate and circumstances of an object crossing it, but it has no locally detectable features according to general relativity.[5] In many ways, a black hole acts like an ideal black body, as it reflects no light.[6][7] Quantum field theory in curved spacetime predicts that event horizons emit Hawking radiation, with the same spectrum as a black body of a temperature inversely proportional to its mass. This temperature is of the order of billionths of a kelvin for stellar black holes, making it essentially impossible to observe directly.

                Objects whose gravitational fields are too strong for light to escape were first considered in the 18th century by John Michell and Pierre-Simon Laplace.[8] In 1916, Karl Schwarzschild found the first modern solution of general relativity that would characterize a black hole. David Finkelstein, in 1958, first published the interpretation of "black hole" as a region of space from which nothing can escape. Black holes were long considered a mathematical curiosity; it was not until the 1960s that theoretical work showed they were a generic prediction of general relativity. The discovery of neutron stars by Jocelyn Bell Burnell in 1967 sparked interest in gravitationally collapsed compact objects as a possible astrophysical reality. The first black hole known was Cygnus X-1, identified by several researchers independently in 1971.[9][10]
                    """;
            
            System.out.println("Before calling getSimilarityResponse");
            Object responsePrim = AISimilarityClient.getSimilarityResponse(text1, text2);
            String response = AISimilarityClient.getSimilarityResponse(text1, text2).toString();
            System.out.println("After calling getSimilarityResponse");
            System.out.println("Response: " + response);  
            System.out.println("Response Prim: " + responsePrim);    

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
