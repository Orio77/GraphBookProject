import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.graphbook.elements.PDFText;
import com.graphbook.server.ApacheHTTP_SimilarityClient;
import com.graphbook.util.interfaces.ISimilarityClient;

public class BatchScoreResponseTest {
    private ISimilarityClient client;

    @SuppressWarnings("unchecked")
    public void getResponseTest() {
        client = new ApacheHTTP_SimilarityClient(null);

        List<PDFText> pdf = new ArrayList<>();
        pdf.add(new PDFText("Existentialism is a philosophy that emphasizes individual existence, freedom, and choice. It asserts that humans are responsible for imparting meaning to their lives despite inherent meaninglessness. Prominent existentialists like Jean-Paul Sartre and Albert Camus argue that we must confront the 'absurd' and create our own purpose."));
        pdf.add(new PDFText("Utilitarianism is an ethical theory that advocates actions that maximize happiness and well-being for the majority. Philosophers Jeremy Bentham and John Stuart Mill are key figures in this school of thought. They propose that the moral worth of an action is determined by its contribution to overall utility, meaning the greatest good for the greatest number."));
        pdf.add(new PDFText("Stoicism is a school of philosophy that teaches the development of self-control and fortitude to overcome destructive emotions. Founded in Athens by Zeno of Citium, Stoicism has been influential through the teachings of Epictetus, Seneca, and Marcus Aurelius. It encourages individuals to focus on what they can control and maintain a will aligned with nature."));
        pdf.add(new PDFText("Nihilism is the philosophical belief that life lacks intrinsic meaning, value, or purpose. This viewpoint is often associated with the works of Friedrich Nietzsche, who explored the implications of a world without inherent truths. Nihilism challenges the foundations of knowledge and morality, proposing that all values are baseless."));
        pdf.add(new PDFText("Rationalism is the epistemological view that reason is the chief source of knowledge. It stands in contrast to empiricism, which emphasizes sensory experience. Key rationalist philosophers include René Descartes, Baruch Spinoza, and Gottfried Wilhelm Leibniz, who argue that certain truths can be known a priori, through intellectual deduction."));
        pdf.add(new PDFText("Empiricism is the theory that all knowledge is derived from sensory experience. This philosophical approach emphasizes the role of observation and experimentation in the formation of ideas. John Locke, George Berkeley, and David Hume are notable empiricists who argue that the mind begins as a blank slate, shaped by experiences."));
        pdf.add(new PDFText("Pragmatism is a philosophical tradition that considers the practical consequences of an idea to be an essential component of its truth. Originating in the late 19th century with thinkers like Charles Sanders Peirce, William James, and John Dewey, pragmatism advocates that beliefs must be tested through practical application and utility."));
        pdf.add(new PDFText("Phenomenology is the study of structures of consciousness as experienced from the first-person perspective. Edmund Husserl, the founder, aimed to analyze the essence of phenomena through the act of 'bracketing' or suspending assumptions. Later phenomenologists like Martin Heidegger and Maurice Merleau-Ponty expanded its applications to existential and embodied experience."));
        pdf.add(new PDFText("Deontology is an ethical theory that uses rules to distinguish right from wrong. It is associated with the philosophy of Immanuel Kant, who argued that actions are morally right if they adhere to a set of rules or duties, irrespective of the consequences. This approach emphasizes the importance of moral principles and the intrinsic morality of actions."));

        Object potentialBatchScore = client.getSimilarityBatchResponse(pdf);
        HashMap<Integer, List<List<Double>>> batchScore = null;


        if (potentialBatchScore instanceof HashMap) {
            try {
                batchScore = (HashMap<Integer, List<List<Double>>>) potentialBatchScore;
            } catch (ClassCastException e) {
                throw new RuntimeException("Object is of class: " + potentialBatchScore.getClass().getName());
            }
        }
        
        if (batchScore != null) {
            batchScore.entrySet().stream().forEach(entry -> {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
            });
        }
        else {
            System.out.println("Batch score is null");
        }
    }

    public static void main(String[] args) {
        new BatchScoreResponseTest().getResponseTest();
    }
}
