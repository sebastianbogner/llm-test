package com.tallence.llm;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import java.io.File;

public class HelloChroma {

  public static void main(String[] args) {
    var filePath = "spring-security-reference.pdf";
    var file = new File(filePath);
    if (!file.exists()) {
      System.out.printf("File %s doesn't exist", filePath);
      System.exit(0);
    }

    EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
            .baseUrl("http://localhost:8000")
            .collectionName("chroma-test")
            .build();

    EmbeddingModel embeddingModel = new  AllMiniLmL6V2EmbeddingModel();

    var document = FileSystemDocumentLoader.loadDocument(file.toPath(), new ApacheTikaDocumentParser());

    var splitter = DocumentSplitters.recursive(300, 0);
    var textSegments = splitter.split(document);
    var embeddings = embeddingModel.embedAll(textSegments)
            .content();

    embeddingStore.addAll(embeddings);

    var ingestor = EmbeddingStoreIngestor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build();

    ingestor.ingest(document);

    var embedding = embeddingModel.embed("How to authorize a method call on a spring bean with spring security?")
            .content();

    var relevants = embeddingStore.findRelevant(embedding, 5);
    for (EmbeddingMatch<TextSegment> relevant : relevants) {
      System.out.printf("REL: %s - SCORE: %s", relevant.embedded().text(), relevant.score());
    }

  }
}
