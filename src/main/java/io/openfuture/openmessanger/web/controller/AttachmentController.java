package io.openfuture.openmessanger.web.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.openfuture.openmessanger.service.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/attachments")
@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public String handleFileUpload(final @RequestParam("file") MultipartFile file) throws IOException {
        attachmentService.upload(file);
        return file.getOriginalFilename();
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(final HttpServletResponse response, @PathVariable(value = "fileName") final String fileName) throws IOException {
        final byte[] fileData = attachmentService.download(fileName);
        response.reset();
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        response.setContentLength(fileName.length());
        response.getOutputStream().write(fileData);
        response.flushBuffer();
    }

}
