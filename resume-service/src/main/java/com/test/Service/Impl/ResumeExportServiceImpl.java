package com.test.Service.Impl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.test.Client.JobseekerClient;
import com.test.Pojo.JobseekerResume;
import com.test.Service.ResumeExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ResumeExportServiceImpl implements ResumeExportService {

    @Autowired
    private JobseekerClient jobseekerClient;

    @Override
    public byte[] exportToPdf(Long userId) {
        try {
            System.out.println("开始导出PDF，用户ID: " + userId);
            
            // 获取完整的profile信息
            System.out.println("正在调用jobseeker-service获取用户信息...");
            ResponseEntity<JobseekerResume> response = jobseekerClient.getResumeByUserId(userId.intValue());
            System.out.println("jobseeker-service响应状态: " + response.getStatusCode());
            
            if (!response.hasBody()) {
                System.out.println("用户信息不存在");
                throw new RuntimeException("用户信息不存在");
            }
            JobseekerResume resume = response.getBody();
            System.out.println("获取到用户信息: " + resume.getName());

            // 创建PDF文档
            System.out.println("开始创建PDF文档...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // 设置字体 - 使用默认字体，避免中文字体问题
            System.out.println("设置字体...");
            PdfFont font = PdfFontFactory.createFont();
            document.setFont(font);

            // 添加标题
            Paragraph title = new Paragraph("Personal Resume")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // 添加基本信息
            document.add(new Paragraph("\nBasic Information").setFontSize(16).setBold());
            document.add(new Paragraph("Name: " + resume.getName()));
            document.add(new Paragraph("Phone: " + resume.getPhone()));
            document.add(new Paragraph("Email: " + resume.getEmail()));
            document.add(new Paragraph("Desired Position: " + resume.getDesiredPosition()));

            // 添加教育经历
            document.add(new Paragraph("\nEducation").setFontSize(16).setBold());
            document.add(new Paragraph(resume.getEducation()));

            // 添加工作经验
            document.add(new Paragraph("\nWork Experience").setFontSize(16).setBold());
            document.add(new Paragraph(resume.getExperience()));

            // 添加技能特长
            document.add(new Paragraph("\nSkills").setFontSize(16).setBold());
            if (resume.getSkills() != null) {
                for (String skill : resume.getSkills().split(",")) {
                    document.add(new Paragraph("• " + skill.trim()));
                }
            }

            // 关闭文档
            System.out.println("关闭PDF文档...");
            document.close();

            byte[] result = baos.toByteArray();
            System.out.println("PDF生成成功，大小: " + result.length + " 字节");
            return result;
        } catch (Exception e) {
            System.err.println("生成PDF失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("生成PDF失败: " + e.getMessage(), e);
        }
    }
} 