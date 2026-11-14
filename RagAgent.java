public class EvaluatorAgent {
        String worker(String task) {
                return "Jio 349 is best";
                    }
                        String evaluate(String output) {
                                if (output.contains("Jio")) {
                                            return "PASS: Answer is correct ";
                                                    }
                                                            return "FAIL: Do it again ";
                                                                }
                                                                    public static void main(String[] a) {
                                                                            EvaluatorAgent agent = new EvaluatorAgent();
                                                                                    String out = agent.worker("Tell Jio recharge");
                                                                                            System.out.println(agent.evaluate(out));
                                                                                                }
                                                                                                }
}