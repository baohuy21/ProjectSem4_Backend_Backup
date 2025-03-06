package com.shopee.shopeecareer.UserController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.shopee.shopeecareer.Entity.Education;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Repository.EducationRepo;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;

@Service
public class CreateCvService {
    @Autowired
    private EducationRepo educationRepo;
    @Autowired
    private ProfileUserRepo profileUserRepo;
    @Value("${config.upload-dir}")
    private String uploadDir;
    @Autowired
    private ResourceLoader resourceLoader;

    public String loadTemplate(String templatePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + templatePath);

        // Đọc nội dung tài nguyên vào một String
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String convertEducationListToHtml(List<Education> educationList) {
        StringBuilder htmlList = new StringBuilder();
        for (Education education : educationList) {
            htmlList.append(" <div class=\"item\">")
                    .append("<li>")
                    .append("<strong>").append(education.getSchoolName()).append("</strong>")
                    .append("<p>")
                    .append(education.getMajor())
                    .append("</p>")
                    .append("<p class=\"text-gray-600\">")
                    .append(education.getStartDate())
                    .append("</p>")
                    .append("</li>")
                    .append("</div>");
        }
        return htmlList.toString();
    }

    public byte[] generatePdfFromHtml(String htmlContent) throws IOException {
        // Output stream để chứa PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Tạo đối tượng ITextRenderer
            ITextRenderer renderer = new ITextRenderer();

            // Thiết lập tài liệu HTML
            renderer.setDocumentFromString(htmlContent);

            // Layout lại tài liệu
            renderer.layout();

            // Tạo PDF và ghi vào OutputStream
            renderer.createPDF(outputStream);

        } catch (DocumentException e) {
            System.out.println("Error creating PDF: " + e.getMessage());
            throw new IOException("Failed to create PDF", e);
        }

        // Trả về byte array chứa PDF
        return outputStream.toByteArray();
    }

    public byte[] generateCvPdf(int id) throws IOException {
        ProfileUser user = profileUserRepo.findById(id).get();
        List<Education> educationList = educationRepo.findEducationByApplicantId(id);

        String htmlTemplate = loadTemplate("/templates/CVTemplates.html").toString();

        // Thay thế dữ liệu vào template HTML
        String populatedHtml = htmlTemplate
                .replace("{{name}}", user.getFullName())
                .replace("{{email}}", user.getEmail())
                .replace("{{phone}}", user.getPhone())
                .replace("{{address}}", user.getAddress())
                .replace("{{educationList}}", convertEducationListToHtml(educationList));
        // .replace("{{experienceList}}",
        // pdfService.convertListToHtml(cv.getExperienceList()));
        populatedHtml = populatedHtml.replace("<br>", "<br />");

        byte[] pdfData = generatePdfFromHtml(populatedHtml);

        // Lưu PDF vào thư mục
        savePdfToFile(id, pdfData, uploadDir);
        return pdfData;
    }

    public void savePdfToFile(int id, byte[] pdfData, String uploadDir) throws IOException {
        Path path = Paths.get(uploadDir + "/filecv");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        String filename = "Quang_Nhat_apply.pdf";
        Path filePath = path.resolve(filename);
        Files.write(filePath, pdfData);
    }
}