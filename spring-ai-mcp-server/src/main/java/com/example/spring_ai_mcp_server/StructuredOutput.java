// StructuredOutput.java
public class StructuredOutput {
    // LLM kadun story nako, direct JSON pahije
        String getPlanJson() {
                return """
                        {
                                  "plan": "Jio 349",
                                            "data": "2GB/day",
                                                      "price": 349,
                                                                "valid": true
                                                                        }
                                                                                """;
                                                                                    }

                                                                                        boolean validate(String json) {
                                                                                                return json.contains("\"plan\"") && json.contains("\"price\"");
                                                                                                    }

                                                                                                        public static void main(String[] a) {
                                                                                                                StructuredOutput s = new StructuredOutput();
                                                                                                                        String json = s.getPlanJson();
                                                                                                                                System.out.println(json);
                                                                                                                                        System.out.println("Valid: " + s.validate(json));
                                                                                                                                            }
                                                                                                                                            }