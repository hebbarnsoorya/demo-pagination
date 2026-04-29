package com.sun.test.demo_pagination.service;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleDriveService {

    @Autowired
    private Drive googleDriveClient;

    /**
     * Error Fix #1: Resolve filename to File ID
     */
    public String getFileIdByName(String filename) throws IOException {
        String query = "name = '" + filename + "' and trashed = false";
        FileList result = googleDriveClient.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles().isEmpty()) {
            throw new IOException("File not found on Google Drive: " + filename);
        }
        return result.getFiles().get(0).getId();
    }

    /**
     * Error Fix #2: Export Drive Doc to HTML string
     */
    public String exportDocToHtml(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        googleDriveClient.files().export(fileId, "text/html")
                .executeMediaAndDownloadTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    /**
     * Error Fix #3: Orchestration for Update
     */
    public void updateFileFromHtml(String fileId, String htmlContent) throws Exception {

        System.out.println("Received HTML from React: " + htmlContent);



        byte[] docxBytes = convertHtmlToDocx(htmlContent);
        ByteArrayContent mediaContent = new ByteArrayContent(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxBytes
        );

        googleDriveClient.files().update(fileId, new File(), mediaContent).execute();
    }

    /**
     * The logic you provided, integrated for clean byte conversion
     */
    public byte[] convertHtmlToDocx(String html) throws Exception {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document jsoupDoc = Jsoup.parse(html);

            for (Element element : jsoupDoc.body().children()) {
                if (element.tagName().startsWith("h")) {
                    XWPFParagraph title = document.createParagraph();
                    // Note: "Heading1" style must exist in the doc template,
                    // otherwise it defaults to standard text.
                    XWPFRun run = title.createRun();
                    run.setBold(true);
                    run.setFontSize(16);
                    run.setText(element.text());
                } else {
                    XWPFParagraph para = document.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setText(element.text());

                    // Handling "Mark Color" logic from your editor
                    if (element.attr("style").contains("background-color")) {
                        run.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);
                    }
                }
            }

            document.write(baos);
            return baos.toByteArray();
        }
    }
}