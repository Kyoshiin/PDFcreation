package com.royExample.PdfCreation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class App {

    private static String parseThymeleafTemplate(String name, String data) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("data", data);

        return templateEngine.process("template.html", context);
    }

    public static void generatePdfFromHtml(String html) throws IOException {
        String outputFolder = "C:/..../output.pdf";
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Load the background image
        BufferedImage backgroundImage = ImageIO.read(new File("....src\\main\\resources\\Page1.jpg"));
        // Save the background image to a temporary file
        File tempImageFile = File.createTempFile("background", ".png");
        ImageIO.write(backgroundImage, "png", tempImageFile);

        // Load the background image from the temporary file
        PDImageXObject background = PDImageXObject.createFromFileByContent(tempImageFile, document);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Draw the background image
        contentStream.drawImage(background, 0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());

        // Parse the HTML using Jsoup
        Document parsedHtml = Jsoup.parse(html);
        Element contentElement = parsedHtml.selectFirst(".content");
        Elements titleElements = contentElement.select("h1");
        Elements bodyElements = contentElement.select("p");

        float yOffset = PDRectangle.A4.getHeight() - 100;

        for (int i = 0; i < titleElements.size() && i < bodyElements.size(); i++) {
            String title = titleElements.get(i).text();
            String bodyText = bodyElements.get(i).text();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.newLineAtOffset(100, yOffset);
            contentStream.showText(title);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -50);
            contentStream.showText(bodyText);
            contentStream.endText();

            yOffset -= 100;
        }

        contentStream.close();
        document.save(outputFolder);
        document.close();
    }


    public static void main(String[] args) throws IOException {
        String name="name1";
        String data="data1";
        String response = parseThymeleafTemplate(name,data);
        generatePdfFromHtml(response);
    }
}
