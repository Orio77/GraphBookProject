package com.graphbook;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.graphbook.elements.PDFText;
import com.graphbook.util.DataSaver;
import com.graphbook.util.NeoDatabase;
import com.graphbook.util.PDFReader;

public class Main {
    public static void main(String[] args) {
        
        testSaver();
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
        NeoDatabase db = new NeoDatabase();

        PDFReader reader = new PDFReader();
        List<PDFText> pages = new ArrayList<>();
        pages = reader.readPages("C:/Users/macie/Downloads/12-Rules-for-Life.pdf");
        db.save(pages);

        db.disconnect();
    }

    private static void testSaver() {

        DataSaver saver = new DataSaver();

        // saver.saveObject(List.of("Hello world"));



        System.out.println(saver.readObject(Paths.get("C:/Users/macie/iCloudDrive/MyProjects/graph-book-core/src/main/java/com/graphbook/files/serialized/object_5/saved_object.txt")));
    }

    private static void delete(DataSaver saver) {
        saver.deleteAll();
    }
}