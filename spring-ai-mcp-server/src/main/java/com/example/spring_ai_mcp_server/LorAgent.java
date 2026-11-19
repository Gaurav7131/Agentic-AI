// LoRAAgent.java
public class LoRAAgent {
    String baseModel = "Gemini Base";
        String loraAdapter = "Jio-Customer-Support-LoRA";

            String generate(String query) {
                    // Base + LoRA = Domain Expert
                            return "[" + loraAdapter + "] Answer for: " + query + " -> Use Jio 349";
                                }
                                    public static void main(String[] a) {
                                            System.out.println(new LoRAAgent().generate("Best recharge?"));
                                                }
                                                }