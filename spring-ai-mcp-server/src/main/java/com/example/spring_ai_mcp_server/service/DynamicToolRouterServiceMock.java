/*
 * Mock Service Class for implementation of Semantic Router or Dynamic Tool
 * Prunning
 * package com.example.spring_ai_mcp_server.service;
 * 
 * import java.util.Comparator;
 * import java.util.HashMap;
 * import java.util.Map;
 * 
 * import org.springframework.ai.chat.client.ChatClient;
 * import org.springframework.ai.embedding.EmbeddingModel;
 * import org.springframework.ai.tool.annotation.Tool;
 * import org.springframework.ai.tool.metadata.ToolMetadata;
 * import org.springframework.stereotype.Service;
 * 
 * //The Monolithic Tool Catalog Problem vs. Dynamic Tool Pruning:This
 * implementation dynamically filters an enterprise catalog of tools down to the
 * most relevant candidates using semantic cosine similarity before invoking the
 * model.
 * 
 * @Service
 * public class DynamicToolRouterService {
 * private final ChatClient chatClient;
 * private final EmbeddingModel model;
 * private final Map<String, ToolMetaData> toolRegistry = new HashMap<>();
 * 
 * // record
 * public record ToolMetaData(String toolName, String toolDescpt, float[]
 * vector, Object toolBean) {
 * }
 * 
 * // Constructor
 * public DynamicToolRouterService(ChatClient.Builder chatBuilder,
 * EmbeddingModel model) {
 * this.model = model;
 * this.chatClient = chatBuilder.build();
 * registerCatalog();
 * }
 * 
 * // registerCatalog()
 * private void registerCatalog() {
 * // Regiter catalog definitions and cache embeddings
 * register("balanceCheck",
 * "Check user account balances, ledger totals, or savings", new
 * FinanceTools());
 * register("clusterRestart",
 * "Restart Kubernetes pods, nodes, or check deployment health", new
 * infraTools());
 * register("rotateApiKeys",
 * "Rotate IAM credentials, API access tokens, and secrets", new
 * SecurityTools());
 * }
 * 
 * // 2)register()
 * private void register(String toolName, String toolDescpt, Object toolBean) {
 * float[] vector = model.embed(toolDescpt);
 * toolRegistry.put(toolName, new ToolMetaData(toolName, toolDescpt, vector,
 * toolBean));
 * 
 * }
 * 
 * // 4.
 * public String executePrunedTools(String userPrompt) {
 * float[] vector = model.embed(userPrompt);
 * 
 * // find the top k candidate tool
 * ToolMetadata bestTool = toolRegistry.values().stream()
 * .max(Comparator.comparingDouble(t -> cosineSimilarity(queryVector,
 * t.vector())))
 * .filter(t -> cosineSimilarity(queryVector, t.vector()) > 0.65)
 * .orElse(null);
 * 
 * if (bestTool!=null) {
 * re
 * 
 * }
 * }
 * 
 * // cosineSimilarity
 * private double cosineSimilarity(float[] vA, float[] vB) {
 * double dot = 0.0,
 * nA = 0.0,
 * nB = 0.0;
 * 
 * for (int i = 0; i < vA.length; i++) {
 * dot += vA[i] * vB[i];
 * nA += Math.pow(vA[i], 2);
 * nB += Math.pow(vB[i], 2);
 * }
 * return (nA == 0 || nB == 0) ? 0.0 : dot / (Math.sqrt(nA) * Math.sqrt(nB));
 * }
 * 
 * // 3.Tool Catalog TOP K candidates
 * // 1):FInanceTools
 * public static class FinanceTools {
 * 
 * @Tool(description = "Fetch current Account Balance")
 * public String getBalance() {
 * return "Balance: $12,450.00";
 * }
 * }
 * 
 * // 2.infraTools
 * public static class infraTools {
 * 
 * @Tool(description = "Restarts a production cluster pod")
 * public String restartPod() {
 * return "Pod restarted successfully.";
 * }
 * }
 * 
 * // 3.SecurityTools
 * public static class SecurityTools {
 * 
 * @Tool(description = "Rotate access tokens and Credentials")
 * public String rotateToken() {
 * return "Access Tokens Rotated successfully.";
 * }
 * 
 * }
 * 
 * }
 */
