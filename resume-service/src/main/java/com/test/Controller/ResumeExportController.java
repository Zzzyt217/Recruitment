package com.test.Controller;

import com.test.Service.ResumeExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/export")
@CrossOrigin
public class ResumeExportController {

    @Autowired
    private ResumeExportService resumeExportService;

    @GetMapping("/pdf/{userId}")
    public ResponseEntity<byte[]> exportResume(@PathVariable Long userId, HttpServletRequest request) {
        try {
            
            byte[] pdfContent = resumeExportService.exportToPdf(userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "resume_" + userId + ".pdf");
            headers.setContentLength(pdfContent.length);
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("PDF导出失败: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 