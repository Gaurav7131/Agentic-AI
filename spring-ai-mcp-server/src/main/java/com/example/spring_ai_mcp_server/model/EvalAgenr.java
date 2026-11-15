// EvalAgent.java
public class EvalAgent {
    String evaluate(String output, String expected) {
            int score = output.contains(expected) ? 100 : 0;
                    return "Score: " + score + "/100";
                        }
                            public static void main(String[] a) {
                                    System.out.println(new EvalAgent().evaluate("Jio 349 is best", "Jio 349"));
                                        }
                                        }