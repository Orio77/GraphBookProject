package com.graphbook.backend.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a text element within a PDF document. This class encapsulates the text content
 * of a PDF element, ensuring that it is not null or empty upon creation. It provides methods
 * to access the text content and overrides {@code toString}, {@code equals}, and {@code hashCode}
 * methods for text comparison and representation purposes.
 * <p>
 * The {@code PDFText} class implements {@link Serializable}, allowing PDF text elements to be
 * serialized, facilitating their use in scenarios where object persistence or transmission is required.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     PDFText pdfText = new PDFText("Example text");
 *     System.out.println(pdfText.getText());
 * </pre>
 * </p>
 * 
 * The {@code PDFText} class is immutable and thread-safe.
 *
 * @see Serializable
 */
public class PDFText implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The text content of the PDF element. This field is marked as final to ensure the immutability
     * of the {@code PDFText} instances.
     */
    private final String text;

    /**
     * Constructs a new {@code PDFText} object with the specified text content. The text provided
     * must not be null or empty; otherwise, an {@link IllegalArgumentException} is thrown.
     *
     * @param text The text content of the PDF element. Must not be null or empty.
     * @throws IllegalArgumentException if the provided text is null or empty.
     */
    public PDFText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Provided a null or empty text");
        }
        this.text = text;
    }

    /**
     * Returns the text content of this PDF element.
     *
     * @return The text content of this PDF element.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns a string representation of this PDF text element. The representation is the text content itself.
     *
     * @return A string representation of this PDF text element.
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Indicates whether some other object is "equal to" this one. The {@code equals} method implements
     * an equivalence relation on non-null object references:
     * <ul>
     * <li>It is reflexive: for any non-null reference value {@code x}, {@code x.equals(x)} should return {@code true}.</li>
     * <li>It is symmetric: for any non-null reference values {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if {@code y.equals(x)} returns {@code true}.</li>
     * <li>It is transitive: for any non-null reference values {@code x}, {@code y}, and {@code z},
     * if {@code x.equals(y)} returns {@code true} and {@code y.equals(z)} returns {@code true},
     * then {@code x.equals(z)} should return {@code true}.</li>
     * <li>It is consistent: for any non-null reference values {@code x} and {@code y}, multiple invocations
     * of {@code x.equals(y)} consistently return {@code true} or consistently return {@code false},
     * provided no information used in {@code equals} comparisons on the objects is modified.</li>
     * <li>For any non-null reference value {@code x}, {@code x.equals(null)} should return {@code false}.</li>
     * </ul>
     *
     * @param obj The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        PDFText pdfText = (PDFText) obj;
        return Objects.equals(text, pdfText.text);
    }

    /**
     * Returns the hash code value for this PDF text element. The hash code is computed based on the text content.
     *
     * @return The hash code value for this PDF text element.
     */
    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
