// LangGraphAgent.java
public class LangGraphAgent {
    enum State { START, PLAN, EXECUTE, CHECK, END }

        void run(String goal) {
                State s = State.START;
                        while (s != State.END) {
                                    System.out.println("Current State: " + s);
                                                if (s == State.START) s = State.PLAN;
                                                            else if (s == State.PLAN) s = State.EXECUTE;
                                                                        else if (s == State.EXECUTE) s = State.CHECK;
                                                                                    else if (s == State.CHECK) {
                                                                                                    System.out.println("Checking result for: " + goal);
                                                                                                                    s = State.END;
                                                                                                                                }
                                                                                                                                        }
                                                                                                                                                System.out.println("Graph Finished");
                                                                                                                                                    }
                                                                                                                                                        public static void main(String[] a) {
                                                                                                                                                                new LangGraphAgent().run("Best Jio Recharge");
                                                                                                                                                                    }
                                                                                                                                                                    }