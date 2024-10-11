package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.TextractClientBuilder;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class CodeParser implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private final TextractClient textractClient;
    private final boolean selfClosing;

    public CodeParser(TextractClient client) {
        this.textractClient = client;
        this.selfClosing = false;
    }

    public CodeParser(TextractClientBuilder builder) {
        this.textractClient = builder.build();
        this.selfClosing = true;
    }

    public void detectDocText(InputStream inputStream) throws IOException, TextractException {
        SdkBytes sourceBytes = SdkBytes.fromInputStream(inputStream);

        Document document = Document.builder().bytes(sourceBytes).build();

        DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse textResponse = this.textractClient.detectDocumentText(detectDocumentTextRequest);

        List<Block> documentInfo = textResponse.blocks();

        for (Block block : documentInfo) {
            if (Objects.requireNonNull(block.blockType()) == BlockType.WORD) {
                System.out.printf("\"%s\",%.2f%n", block.text(), block.confidence());
            }
        }

        DocumentMetadata documentMetadata = textResponse.documentMetadata();

        System.out.println(documentMetadata);
    }

    @Override
    public void close() {
        if (this.selfClosing) textractClient.close();
    }
}
