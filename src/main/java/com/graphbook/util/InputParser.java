package com.graphbook.util;

public class InputParser {

    public int getDigit(String input) {
        if (input == null || input.isEmpty()) {
            return -1;
        }
        char[] inputChars = input.toCharArray();
        StringBuilder res = new StringBuilder(2);

        if (inputChars.length > 0 && Character.isDigit(inputChars[0])) {
            res.append(inputChars[0]);
        }
        if (inputChars.length > 1 && Character.isDigit(inputChars[1])) {
            res.append(inputChars[1]);
        }

        if (res.length() == 0) {
            // Handle case where no digits are found or input is empty
            throw new NumberFormatException("Input string does not contain any digits.");
        }

        return Integer.parseInt(res.toString());
    }
}
