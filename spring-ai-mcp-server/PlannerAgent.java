import java.util.List;

public class PlannerAgent {
        // PLANNER
            List<String> makePlan(String goal) {
                        System.out.println("Planner: Plan banvatoy for - " + goal);
                                return List.of(
                                                "1. Jio 349 plan check kar",
                                                            "2. Paytm vs GPay cashback compare kar",
                                                                        "3. Best wala select kar"
                                );
            }
                // EXECUTOR
                    void execute(List<String> plan) {
                                for (String step : plan) {
                                                System.out.println("Executor doing -> " + step);
                                }
                                        System.out.println("Done ✅");
                    }
                        public static void main(String[] args) {
                                    PlannerAgent a = new PlannerAgent();
                                            List<String> plan = a.makePlan("Jio Recharge 349");
                                                    a.execute(plan);
                        }
}
        