import java.util.*;

public class MemoryAgent {
    Map<String, String> memory = new HashMap<>();

        String chat(String user, String msg) {
                if (msg.contains("nav")) {
                            memory.put(user, "Latur");
                                        return "Lakshat thevla, tu Latur cha ahes";
                                                }
                                                        if (msg.contains("mi kuthe")) {
                                                                    return "Tu " + memory.get(user) + " cha ahes, visarlo nahi mi";
                                                                            }
                                                                                    return "Bola bhava";
                                                                                        }
                                                                                            public static void main(String[] a) {
                                                                                                    MemoryAgent agent = new MemoryAgent();
                                                                                                            System.out.println(agent.chat("user1", "maz nav Latur"));
                                                                                                                    System.out.println(agent.chat("user1", "mi kuthe rahto?"));
                                                                                                                        }
                                                                                                                        }