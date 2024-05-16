package com.graphbook;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.graphbook.elements.PDFText;
import com.graphbook.server.SimilarityClient;
import com.graphbook.util.CONSTANTS;
import com.graphbook.util.DataSaver;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFReader;
import com.graphbook.util.SimilarityCalculator;

public class Main {
    private static final NeoDatabase db = new NeoDatabase();
    private static final DataSaver saver = new DataSaver();
    private static final Path pathToSavedPages = Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object/saved_object.txt");
    private static final String pathToPDF = "C:/Users/macie/Downloads/12-Rules-for-Life.pdf";


    public static void main(String[] args) {
        // db.clearAllEdges();
        testDatabaseEdgeCreationWithLLMScore();
        // new DataSaver().deleteAll(CONSTANTS.ERROR_LOG_PATH.toString());
    }


    private static void testLoadingAndParsingPages() {

        PDFReader reader = new PDFReader();
        List<PDFText> pages = new ArrayList<>();
        pages = reader.readPages("C:/Users/macie/Downloads/12-Rules-for-Life.pdf");

        System.out.println();
        System.out.println();
        System.out.println(pages.get(100));
        System.out.println();
        System.out.println();
        System.out.println(pages.get(101));
        System.out.println();
        System.out.println();
        System.out.println(pages.get(102));
        System.out.println();
        System.out.println();
        System.out.println(pages.get(103));
        System.out.println();
        System.out.println();
        System.out.println(pages.get(104));
        System.out.println();
        System.out.println();
    }

    private static void testDatabaseSaveOperation() {
        List<PDFText> pages = new ArrayList<>();
        // PDFReader reader = new PDFReader();
        // pages = reader.readPages("C:/Users/macie/Downloads/12-Rules-for-Life.pdf");

        pages = (List<PDFText>) saver.readObject(pathToSavedPages);
        db.save(pages);

        db.disconnect();
    }

    private static void testDatabaseReset() {
        db.reset();
    }

    private static void testDatabaseEdgeCreation() {
        List<PDFText> pages = (List<PDFText>) saver.readObject(pathToSavedPages);

        db.createEdge(pages.get(100), pages.get(200), 80.0);
    }

    private static void testDatabaseEdgeCreationWithLLMScore() {
        List<PDFText> texts = (List<PDFText>) saver.readObject(pathToSavedPages);
        double similarityTreshold = 70.0;
        db.connect();

        for (int i = 0; i < texts.size(); i++) {
            PDFText text1 = texts.get(i);
            for (int j = i+1; j < texts.size()-1; j++) {
                PDFText text2 = texts.get(j);
                double score = (Double) SimilarityClient.getSimilarityResponse(text1.getText(), text2.getText());
                int tries = 0;
                while (score == -1 && tries < 3) {
                    try {
                        score = (Double) SimilarityClient.getSimilarityResponse(text1.getText(), text2.getText());
                        tries++;
                    } catch (Exception e) {
                        System.out.println("Connection refused (probably). Retrying in 3 seconds");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                if (score < similarityTreshold) continue;
                db.createEdge(text1, text2, score);
            }
        }
        db.disconnect();
    }

    private static void testDatabaseFullGraphCreation() {
        List<PDFText> pages = (List<PDFText>) saver.readObject(pathToSavedPages);

        db.createAllEdges(pages, new SimilarityCalculator(), 80);
    }

    private static void testSaverSaveOperation() {

        DataSaver saver = new DataSaver();
        PDFReader reader = new PDFReader();
        List<PDFText> pages = new ArrayList<>();
        pages = reader.readPages(pathToPDF);

        saver.saveObject(pages);

        // System.out.println(saver.readObject(Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object_5/saved_object.txt")));
    }

    private static void testSaverReadOperation() {
        System.out.println(saver.readObject(pathToSavedPages));
    }

    private static void testServerRequests() {

        String resp1 = """
            Bees are winged insects closely related to wasps and ants, known for their roles in pollination and, in the case of the best-known bee species, the western honey bee, for producing honey. Bees are a monophyletic lineage within the superfamily Apoidea. They are currently considered a clade, called Anthophila.[1] There are over 20,000 known species of bees in seven recognized biological families.[2][3][4] Some species – including honey bees, bumblebees, and stingless bees – live socially in colonies while most species (>90%) – including mason bees, carpenter bees, leafcutter bees, and sweat bees – are solitary.
                """;

        String resp2 = """
            The immediate ancestors of bees were stinging wasps in the family Crabronidae, which were predators of other insects. The switch from insect prey to pollen may have resulted from the consumption of prey insects which were flower visitors and were partially covered with pollen when they were fed to the wasp larvae. This same evolutionary scenario may have occurred within the vespoid wasps, where the pollen wasps evolved from predatory ancestors.
                """;

        System.out.println(SimilarityClient.getSimilarityResponse(resp1, resp2));
    }

    private static double testScoreExtraction() {
        String resp1 = """
            To calculate the similarity score between TEXT1 and TEXT2, we can use a text comparison algorithm such as cosine similarity or Jaccard index. However, since this is an AI model, I'll provide an estimated manual assessment based on content overlap:

            Both texts discuss butterflies, their evolutionary history, life cycle, and fossil records. They share information about the origin of butterflies in the Late Cretaceous period and mention specific examples like Protocoeliades kristenseni from TEXT1 and Prodryas persephone from TEXT2.
            
            Considering these similarities, I would estimate a similarity score around 70 out of 100. However, it's important to note that this is an approximation as the actual calculation requires specialized algorithms or software.
            
            Score: 70.0
                """;

        String resp2 = """
            Score: 85.0

            Both texts discuss the evolutionary history and life cycle of butterflies, with some differences in details such as specific fossils' ages and geographical origins. However, they are largely consistent regarding key information about butterfly characteristics, their metamorphosis process, and general timeline for origin and diversification. The similarity score reflects the high level of agreement between these texts on fundamental aspects of butterflies.
                """;

        String resp3 = """
            Score: 85.0

            Both texts discuss the evolutionary history and life cycle of butterflies, with some slight differences in details such as the timing of their origin (Paleocene vs Late Cretaceous) and specific fossil examples. However, they are largely consistent in content, indicating a high similarity score.
                """;

        double score = SimilarityClient.extractScore(resp3);
        System.out.println(score);
        return score;
    }
}