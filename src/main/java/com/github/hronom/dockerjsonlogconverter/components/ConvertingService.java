package com.github.hronom.dockerjsonlogconverter.components;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConvertingService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toTxt(String json) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new StringReader(json))) {
            String line = reader.readLine();
            while (line != null) {
                DockerJsonLogRow dockerJsonLogRow = objectMapper.readValue(line, DockerJsonLogRow.class);
                stringBuilder.append(dockerJsonLogRow.log);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    public String toTxt(MultipartFile file) throws IOException {
        Files.copy(file.getInputStream(), Paths.get(file.getOriginalFilename()));

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getOriginalFilename()))) {
            String line = reader.readLine();
            while (line != null) {
                DockerJsonLogRow dockerJsonLogRow = objectMapper.readValue(line, DockerJsonLogRow.class);
                stringBuilder.append(dockerJsonLogRow.log);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    public void saveToTxt(InputStream inputStream, PrintWriter printWriter) throws IOException {
        MappingIterator<DockerJsonLogRow> iterator =
            objectMapper.readerFor(DockerJsonLogRow.class).readValues(inputStream);
        while (iterator.hasNextValue()) {
            DockerJsonLogRow value = iterator.nextValue();
            printWriter.write(value.log);
        }
        inputStream.close();
        printWriter.flush();
        printWriter.close();
    }


    public void saveToTxt(Path tempSourceFilePath, Path tempCategoryFilePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(tempSourceFilePath);
             BufferedWriter bufferedWriter = Files.newBufferedWriter(
                 tempCategoryFilePath,
                 StandardCharsets.UTF_8
             )
        ) {
            MappingIterator<DockerJsonLogRow> iterator =
                objectMapper.readerFor(DockerJsonLogRow.class).readValues(inputStream);
            while (iterator.hasNextValue()) {
                DockerJsonLogRow value = iterator.nextValue();
                bufferedWriter.write(value.log);
            }
        }
    }
}
